package fr.rooobert.discord.rooobot.plugins.lg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.rooobert.Database;
import fr.rooobert.discord.rooobot.Plugin;
import fr.rooobert.discord.rooobot.RoOoBoTException;
import fr.rooobert.discord.rooobot.plugins.lg.cmds.RolesCommand;
import fr.rooobert.discord.rooobot.plugins.lg.cmds.StartCommand;
import fr.rooobert.discord.rooobot.plugins.lg.cmds.StopCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class LgPlugin extends Plugin {
	// --- Constants
	public static final Logger logger = LogManager.getLogger();
	
	// --- Attributes
	final Map<Long, Game> games = new HashMap<>();
	final Set<Long> admins = new HashSet<>();
	
	// --- Methods
	public LgPlugin(String alias, String name, Database database, Properties configuration) throws RoOoBoTException {
		super(alias, name, database, configuration);
		
		// Parse administrator users
		String adminsStr = this.getConfiguration().getProperty("admins", "");
		String[] adminsId = adminsStr.split("\\D+");
		for (String adminId : adminsId) {
			if (!adminId.trim().isEmpty()) {
				try {
					this.admins .add(Long.parseLong(adminId));
				} catch (NumberFormatException e) {
					logger.error("Invalid user ID : " + e.getMessage(), e);
				}
			}
		}
		
		logger.info("LG plugin : loaded");
	}
	
	public boolean isAdmin(User user) {
		return this.admins.contains(user.getIdLong());
	}
	
	@Override
	public void onEnabled(JDA jda) throws Exception {
		if (this.getSubcommands().isEmpty()) {
			this.registerSubcommand(new StartCommand(this));
			this.registerSubcommand(new StopCommand(this));
			this.registerSubcommand(new RolesCommand(this));
		}
		
		logger.info("LG plugin : enabled");
	}
	
	public Game getGame(MessageChannel channel) {
		return this.games.get(channel.getIdLong());
	}
	
	public void setGame(MessageChannel channel, Game game) {
		synchronized (LgPlugin.this.games) {
			Game alreadyRunningGame = LgPlugin.this.games.put(channel.getIdLong(), game);
			if (alreadyRunningGame != null && game != null) {
				throw new RuntimeException("Game started several times : " + alreadyRunningGame.toString());
			}
		}
	}
}
