package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.kt.util.Callback;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.menu.*;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class TeamManageCommand {
    @Command(names = {"manageteam leader"}, permission = "SENIORMOD", hidden = true)
    public static void teamLeader(final Player sender, @Param(name = "team") final Team team) {
        new SelectNewLeaderMenu(team).openMenu(sender);
    }

    @Command(names = {"manageteam promote"}, permission = "SENIORMOD", hidden = true)
    public static void promoteTeam(final Player sender, @Param(name = "team") final Team team) {
        new PromoteMembersMenu(team).openMenu(sender);
    }

    @Command(names = {"manageteam demote"}, permission = "SENIORMOD", hidden = true)
    public static void demoteTeam(final Player sender, @Param(name = "team") final Team team) {
        new DemoteMembersMenu(team).openMenu(sender);
    }

    @Command(names = {"manageteam kick"}, permission = "SENIORMOD", hidden = true)
    public static void kickTeam(final Player sender, @Param(name = "team") final Team team) {
        new KickPlayersMenu(team).openMenu(sender);
    }

    @Command(names = {"manageteam balance"}, permission = "op", hidden = true)
    public static void balanceTeam(Player sender, @Param(name = "team") Team team) {
        conversationDouble(sender, "§bEnter a new balance for " + team.getName() + ".", (d) -> {
            SetTeamBalanceCommand.setTeamBalance(sender, team, d.intValue());
            sender.sendRawMessage(ChatColor.GRAY + team.getName() + " now has a balance of " + team.getBalance());
        });
    }

    @Command(names = {"manageteam lives"}, permission = "op", hidden = true)
    public static void livesTeam(Player sender, @Param(name = "team") Team team) {
        conversationDouble(sender, "§bEnter a new lives for " + team.getName() + ".", (d) -> {
            team.setLives(d.intValue());
            sender.sendRawMessage(ChatColor.GRAY + team.getName() + " now has " + team.getLives() + " lives.");
        });
    }

    @Command(names = {"manageteam dtr"}, permission = "SENIORMOD", hidden = true)
    public static void dtrTeam(Player sender, @Param(name = "team") Team team) {
        if (sender.isOp()) {
            conversationDouble(sender, "§eEnter a new DTR for " + team.getName() + ".", (d) -> {
                team.setDTR(d.floatValue());
                sender.sendRawMessage(ChatColor.GOLD + team.getName() + ChatColor.YELLOW + " has a new DTR of " + ChatColor.GOLD + d.floatValue() + ChatColor.YELLOW + ".");
            });
        } else {
            new DTRMenu(team).openMenu(sender);
        }
    }

    @Command(names = {"manageteam rename"}, permission = "fotrot.manage", hidden = true)
    public static void renameTeam(final Player sender, @Param(name = "team") final Team team) {
        conversationString(sender, "§aEnter a new name for " + team.getName() + ".", name -> {
            final String oldName = team.getName();
            team.rename(name);
            sender.sendRawMessage(ChatColor.GRAY + oldName + " now has a name of " + team.getName());
        });
    }

    @Command(names = {"manageteam mute"}, permission = "SENIORMOD", hidden = true)
    public static void muteTeam(final Player sender, @Param(name = "team") final Team team) {
        new MuteMenu(team).openMenu(sender);
    }

    @Command(names = {"manageteam manage"}, permission = "SENIORMOD", hidden = true)
    public static void manageTeam(final Player sender, @Param(name = "team") final Team team) {
        new TeamManageMenu(team).openMenu(sender);
    }

    private static void conversationDouble(final Player p, final String prompt, final Callback<Double> callback) {
        final ConversationFactory factory = new ConversationFactory(Main.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(final ConversationContext context) {
                return prompt;
            }

            public Prompt acceptInput(final ConversationContext cc, final String s) {
                try {
                    callback.callback(Double.parseDouble(s));
                } catch (NumberFormatException e) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + s + " is not a number.");
                }
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        final Conversation con = factory.buildConversation(p);
        p.beginConversation(con);
    }

    private static void conversationString(final Player p, final String prompt, final Callback<String> callback) {
        final ConversationFactory factory = new ConversationFactory(Main.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(final ConversationContext context) {
                return prompt;
            }

            public Prompt acceptInput(final ConversationContext cc, final String newName) {
                if (newName.length() > 16) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }
                if (newName.length() < 3) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        final Conversation con = factory.buildConversation(p);
        p.beginConversation(con);
    }
}
