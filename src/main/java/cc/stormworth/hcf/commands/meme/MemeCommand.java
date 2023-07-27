package cc.stormworth.hcf.commands.meme;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class MemeCommand {

    private static boolean enableMemeCommand = false;

    @Command(names = "meme", permission = "")
    public static void meme(Player player){
        HCFProfile profile = HCFProfile.get(player);

        if(!enableMemeCommand){
            player.sendMessage(CC.translate("&cMeme command is currently disabled."));
            return;
        }

        if(profile.isUseMemeCommand()){
            player.sendMessage(CC.translate("&cYou have already claimed it."));
            return;
        }

        player.sendMessage(CC.translate("&eYou have successfully claimed, &6&lbattle's issues &erewards."));

        Bukkit.broadcastMessage(CC.translate("&6&lMeme &7》 &6&l" + player.getName() + " &ehas  claimed our &6Rewards&e thanked to the &6Maintenance &etime. &7*/meme*"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Airdrop 1");
        profile.setUseMemeCommand(true);
    }

    @Command(names = "togglememe", permission = "op")
    public static void toggle(Player player){
        enableMemeCommand = !enableMemeCommand;

        player.sendMessage(CC.translate("&eMeme command is now " + (enableMemeCommand ? "&aenabled" : "&cdisabled")));
    }

    @Command(names = "resetmeme", permission = "op")
    public static void reset(CommandSender player){

        TaskUtil.runAsync(Main.getInstance(), () -> {

            Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("profiles")
                    .find().forEach((Consumer<? super Document>) (document) -> {
                if (document.containsKey("useMemeCommand")) {
                    document.remove("useMemeCommand");
                }

                UUID uuid = UUID.fromString(document.getString("uuid"));

                Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("profiles")
                        .replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
            });

            player.sendMessage(CC.translate("&eMeme command has been reset for all players."));
        });

    }
}
