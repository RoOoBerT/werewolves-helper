package fr.rooobert.discord.rooobot.plugins.lg.cmds;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.rooobert.discord.rooobot.PluginSubcommand;
import fr.rooobert.discord.rooobot.plugins.lg.Game;
import fr.rooobert.discord.rooobot.plugins.lg.LgPlugin;
import fr.rooobert.discord.rooobot.plugins.lg.RoleType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RolesCommand implements PluginSubcommand {
	// --- Constants
	public static final Logger logger = LogManager.getLogger();
	
	// --- Attributes
	final LgPlugin plugin;
	
	// --- Methods
	public RolesCommand(LgPlugin plugin) {
		super();
		this.plugin = plugin;
	}
	
	@Override
	public Pattern getPattern() {
		return Pattern.compile("\\Qroles\\E", Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	public void onCommandReceived(MessageReceivedEvent event, Matcher matcher, String params) {
		event.getChannel().sendMessageFormat(String.format("**%s - Liste des rôles (%d)**",
				Game.GAME_NAME, RoleType.values().length))
			.queue();
		for (RoleType role : RoleType.values()) {
			event.getChannel().sendMessageFormat(String.format("**%s %s**",
					role.getEmoji(), role.getName()))
				.addFile(role.getImageAsInputStream(), String.format("%s.png", role.getName()))
				.queue();
		}
	}
}
