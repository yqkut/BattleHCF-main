package cc.stormworth.hcf.ability.impl.sacrifice;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.misc.request.Request;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Teleport;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public class SacrificeMenu extends Menu {
    private final Team team;

    public SacrificeMenu(Team team) {
        this.team = team;
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "&7Select a teammate to sacrifice.";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Player target : team.getOnlineMembers()) {
            if (target != player && target != null) {
                buttons.put(buttons.size(), new PlayerButton(target));
            }
        }

        return buttons;
    }

    @RequiredArgsConstructor
    public class PlayerButton extends Button {

        private final Player target;

        @Override
        public String getName(Player player) {
            return ChatColor.GREEN + target.getName();
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.SKULL_ITEM;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SKULL_ITEM)
                    .data((short) 3)
                    .setSkullOwner(target.getName())
                    .name(ChatColor.GREEN + target.getName())
                    .addToLore("", CC.translate("&eClick to teleport!"))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            HCFProfile profile = HCFProfile.get(player);

            Request request = new Request(
                    new Clickable("&6" + player.getDisplayName() + " &ehas sent a sacrifice request, &a[Click here] &eto accept.",
                            "&bClick to accept",
                            "/acceptsacrifice"),
                    player.getUniqueId(), target.getUniqueId(),
                    System.currentTimeMillis() + TimeUtil.parseTimeLong("1m"));

            Ability ability = Ability.getByName("Sacrifice");

            request.addAction((players) -> {

                Player player1 = players.get(0);

                Player player2 = players.get(1);

                Teleport teleport = new Teleport(player, player.getLocation(), target.getLocation(), 10);

                teleport.setOnTeleport((other) -> {
                    player1.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1));

                    player1.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));
                    player2.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));

                    CooldownAPI.setCooldown(player1, "damage_sync", TimeUtil.parseTimeLong("5s"));
                    CooldownAPI.setCooldown(player2, "damage_sync", TimeUtil.parseTimeLong("5s"));

                    player1.setMetadata("damage_sync", new FixedMetadataValue(Main.getInstance(), player2.getUniqueId().toString()));
                    player2.setMetadata("damage_sync", new FixedMetadataValue(Main.getInstance(), player1.getUniqueId().toString()));
                });

                teleport.setUuid(target.getUniqueId());
                teleport.setAbility(ability);

                profile.setTeleport(teleport);
                profile.setCountdown(teleport.start());
            });

            request.send();

            player.closeInventory();

            consume(player);

            CooldownAPI.setCooldown(player, ability.getName(), ability.getCooldown(), "&aYou can now use " + ability.getDisplayName() + "&a again.");
        }
    }

    private void consume(Player player) {

        PlayerInventory inventory = player.getInventory();

        Ability ability = Ability.getByName("Sacrifice");

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && ability.isItem(item)) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    inventory.setItem(i, null);
                }

                break;
            }
        }
    }
}