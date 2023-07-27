package cc.stormworth.hcf.util.reflect;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.ability.impl.Invis.Invis;
import cc.stormworth.hcf.ability.impl.Invis.InvisPacketHelper;
import cc.stormworth.hcf.util.glass.GlassInfo;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Executor;

public class PacketsUtils implements Listener {

    protected static final String HANDLER_NAME = "packet_handler";
    protected static final String LISTENER_NAME = "glass_listener";
    @Getter
    private static PacketsUtils instance;
    private Executor bukkitExecutor;

    public PacketsUtils() {
        instance = this;
        this.fetchBukkitExecutor();
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        TaskUtil.runAsyncLater(Main.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(this::injectPacketInterceptor), 20L);
    }

    public void disable() {
        Bukkit.getOnlinePlayers().forEach(this::deinjectPacketInterceptor);
    }

    public Thread getMainThread() {
        return MinecraftServer.getServer().primaryThread;
    }

    public Executor getBukkitExecutor() {
        return this.bukkitExecutor;
    }

    public boolean isInvulnerable(Player player) {
        return ((CraftPlayer) player).getHandle().isInvulnerable();
    }

    public void toggleInvulnerable(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Class<?> superclass = entityPlayer.getClass().getSuperclass();

        do {
            superclass = superclass.getSuperclass();
        } while (superclass != net.minecraft.server.v1_7_R4.Entity.class);

        try {
            Field invulnerableField = superclass.getDeclaredField("invulnerable");
            invulnerableField.setAccessible(true);

            invulnerableField.set(entityPlayer, !this.isInvulnerable(player));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public PotionEffect getPotionEffect(Player player, PotionEffectType type) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        MobEffect nmsEffect = entityPlayer.getEffect(MobEffectList.byId[type.getId()]);

        return nmsEffect != null ? new PotionEffect(PotionEffectType.getById(nmsEffect.getEffectId()),
                nmsEffect.getDuration(), nmsEffect.getAmplifier(), nmsEffect.isAmbient()) : null;
    }

    public void addPotionEffect(Player player, PotionEffect effect) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        MobEffect mobEffect = new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier());

        entityPlayer.removeEffect(mobEffect.getEffectId());

        if (Thread.currentThread() == this.getMainThread()) {
            entityPlayer.addEffect(mobEffect);
        } else {
            TaskUtil.run(Main.getInstance(), () -> entityPlayer.addEffect(mobEffect));
        }
    }

    public void injectPacketInterceptor(Player player) {
        if (!player.isOnline()) return;

        CraftPlayer cplayer = (CraftPlayer) player;

        Channel channel = this.getChannel(cplayer);
        if (channel == null) return;

        ChannelDuplexHandler handler = new ChannelDuplexHandler() {

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
                if (packet instanceof PacketPlayOutEntityEquipment) {
                    packet = handlePlayOutEntityEquipmentPacket(player, (PacketPlayOutEntityEquipment) packet);
                }

                if (packet != null) {
                    super.write(context, packet, promise);
                }
            }

            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                if (packet instanceof PacketPlayInBlockDig) {
                    if (handlePlayInBlockDigPacket(player, (PacketPlayInBlockDig) packet)) return;
                } else if (packet instanceof PacketPlayInBlockPlace) {
                    if (handlePlayInBlockPlacePacket(player, (PacketPlayInBlockPlace) packet)) return;
                }

                super.channelRead(context, packet);
            }
        };

        if (channel.pipeline().get(LISTENER_NAME) == null) {
            try {
                channel.pipeline().addBefore(HANDLER_NAME, LISTENER_NAME, handler);
            } catch (NoSuchElementException ignored) {
            }
        }

        Invis ability = (Invis) Ability.getByName("Invis");

        if (ability != null) {
            ability.hidePlayers(player);
        }
    }

    public void deinjectPacketInterceptor(Player player) {
        CraftPlayer cplayer = (CraftPlayer) player;

        Channel channel = this.getChannel(cplayer);
        if (channel == null) return;

        if (channel.pipeline().get(LISTENER_NAME) != null) {
            channel.pipeline().remove(LISTENER_NAME);
        }
    }

    private boolean handlePlayInBlockDigPacket(Player player, PacketPlayInBlockDig digPacket) {
        if (digPacket.g() != 0 && digPacket.g() != 2) {
            return false;
        }

        Location location = new Location(player.getWorld(), digPacket.c(), digPacket.d(), digPacket.e());
        GlassInfo glassInfo = Main.getInstance().getGlassManager().getGlassAt(player, location);

        if (glassInfo != null) {
            player.sendBlockChange(location, glassInfo.getMaterial(), glassInfo.getData());
            return true;
        }

        return false;
    }

    private boolean handlePlayInBlockPlacePacket(Player player, PacketPlayInBlockPlace placePacket) {
        Location location = new Location(player.getWorld(), placePacket.c(), placePacket.d(), placePacket.e());
        GlassInfo glassInfo = Main.getInstance().getGlassManager().getGlassAt(player, location);

        if (glassInfo != null) {
            player.sendBlockChange(location, glassInfo.getMaterial(), glassInfo.getData());
            return true;
        }

        return false;
    }

    public void updateArmor(Player player, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = this.getEquipmentPackets(player, remove);

        for (Player other : player.getWorld().getPlayers()) {
            if (other == player) continue;

            for (PacketPlayOutEntityEquipment packet : packets) {
                this.sendPacket(other, packet);
            }
        }

        player.updateInventory();
    }

    public void updateArmorFor(Player player, Player target, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = this.getEquipmentPackets(target, remove);

        for (PacketPlayOutEntityEquipment packet : packets) {
            this.sendPacket(player, packet);
        }
    }

    private Set<PacketPlayOutEntityEquipment> getEquipmentPackets(Player player, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = new HashSet<>();

        for (int slot = 1; slot < 5; slot++) {
            PacketPlayOutEntityEquipment equipment = InvisPacketHelper.createEquipmentPacket(player, slot, remove);
            packets.add(equipment);
        }

        return packets;
    }

    public void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet) packet);
    }

    private PacketPlayOutEntityEquipment handlePlayOutEntityEquipmentPacket(Player player, PacketPlayOutEntityEquipment equipmentPacket) {
        Invis ability = (Invis) Ability.getByName("Camouflage");

        if (ability != null) {
            try {
                int entityId = InvisPacketHelper.getEntityId(equipmentPacket);
                net.minecraft.server.v1_7_R4.Entity sender = ((CraftPlayer) player).getHandle().world.getEntity(entityId);

                if (sender instanceof EntityPlayer && ability.getPlayers().contains(sender.getUniqueID())) {
                    int slot = InvisPacketHelper.getSlot(equipmentPacket);
                    net.minecraft.server.v1_7_R4.ItemStack itemStack = InvisPacketHelper.getItemStack(equipmentPacket);

                    // Make sure we only cancel the armor packets
                    if (itemStack != null && slot != 0) {
                        return null;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return equipmentPacket;
    }

    private Channel getChannel(CraftPlayer cplayer) {
        NetworkManager networkManager = cplayer.getHandle().playerConnection.networkManager;

        try {
            Field channelField = networkManager.getClass().getDeclaredField("m");
            channelField.setAccessible(true);

            return (Channel) channelField.get(networkManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void fetchBukkitExecutor() {
        try {
            Field executorField = CraftScheduler.class.getDeclaredField("executor");
            executorField.setAccessible(true);

            this.bukkitExecutor = (Executor) executorField.get(Bukkit.getScheduler());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.bukkitExecutor.execute(() -> this.injectPacketInterceptor(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        TaskUtil.runAsync(Main.getInstance(), () -> this.deinjectPacketInterceptor(event.getPlayer()));
    }
}