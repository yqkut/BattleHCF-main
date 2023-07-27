package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.notes.TeamNote;
import cc.stormworth.hcf.team.notes.TeamNotesMenu;
import org.bukkit.entity.Player;

import java.util.Date;

public final class TeamNoteCommands {

    @Command(names = { "team addnote", "t addnote", "faction addnote", "f addnote", "fac addnote" }, permission = "MOD", hidden = true)
    public static void teamAddNote(Player sender, @Param(name = "team") Team team, @Param(name = "reason", wildcard = true) String reason) {
    	team.getNotes().add(new TeamNote(new Date(), sender.getName(), reason));
    	sender.sendMessage(CC.translate("&eNew note added to " + team.getName() + "!"));
    }
    
    @Command(names = { "team removenote", "t removenote", "faction removenote", "f removenote", "fac removenote" }, permission = "MOD", hidden = true)
    public static void teamRemoveNote(Player sender, @Param(name = "team") Team team, @Param(name = "index") int index) {
    	if (index <= 0 || index > team.getNotes().size()) {
    		sender.sendMessage(CC.translate("&cNote #" + index + " not found!"));
    		return;
    	}
    	
    	team.getNotes().remove(index - 1);
    	sender.sendMessage(CC.translate("&eNote successfully removed!"));
    }
    
    @Command(names = { "team checknotes", "t checknotes", "faction checknotes", "f checknotes", "fac checknotes" }, permission = "MOD", hidden = true)
    public static void teamCheckNotes(Player sender, @Param(name = "team") Team team) {
    	if (team.getNotes().isEmpty()) {
    		sender.sendMessage(CC.translate("&cThat team doesn't have any note!"));
    		return;
    	}
    	
    	sender.sendMessage("");
    	sender.sendMessage(CC.translate("&6&l" + team.getName() + "'s Notes &7(&f" + team.getNotes().size() + "&7)"));
    	
    	for (int i = 0; i < team.getNotes().size(); i++) {
    		TeamNote note = team.getNotes().get(i);
    		
    		sender.sendMessage(CC.translate(" &c&l#" + (i + 1) + " &7- &eIssued on: &f" + Team.DATE_FORMAT.format(note.getIssuedOn()) + " &7- &eBy: &f" + note.getStaff() + " &7- &eReason: &f" + note.getReason()));
    	}
    	
    	sender.sendMessage("");
    }

    @Command(names = { "team notes", "t notes", "faction notes", "f notes", "fac notes" }, permission = "MOD", hidden = true)
    public static void teamnotes(Player sender, @Param(name = "team") Team team) {
    	if (team.getNotes().isEmpty()) {
    		sender.sendMessage(CC.translate("&cThat team doesn't have any note!"));
    		return;
    	}

    	new TeamNotesMenu(team).open(sender);
    }
}
