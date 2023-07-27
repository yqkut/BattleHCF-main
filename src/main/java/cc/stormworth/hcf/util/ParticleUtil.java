package cc.stormworth.hcf.util;

import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;

@UtilityClass
public class ParticleUtil {

    public void sendParticle(Player player, ParticleMeta... particleMeta){

        Map<Location, Packet> packets = Maps.newHashMap();

        for (ParticleMeta meta : particleMeta) {
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                    meta.getParticle(),
                    (float) meta.getLocation().getX(),
                    (float) meta.getLocation().getY(),
                    (float) meta.getLocation().getZ(),
                    meta.getDeltaX(),
                    meta.getDeltaY(),
                    meta.getDeltaZ(),
                    meta.getSpeed(),
                    meta.getAmount()
            );

            packets.put(meta.getLocation(), packet);
        }

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        packets.forEach((location, packet) -> {
            if (location.distance(player.getLocation()) <= 32.0) {
                entityPlayer.playerConnection.sendPacket(packet);
            }
        });
    }

    public void sendParticleToAll(World world, ParticleMeta... particleMeta){
        world.getPlayers().forEach(player -> sendParticle(player, particleMeta));
    }

}
