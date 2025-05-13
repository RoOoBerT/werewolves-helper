package fr.rooobert.discord.rooobot.plugins.lg.cmds;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.rooobert.discord.rooobot.PluginSubcommand;
import fr.rooobert.discord.rooobot.plugins.lg.Game;
import fr.rooobert.discord.rooobot.plugins.lg.LgPlugin;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopCommand implements PluginSubcommand {
	// --- Constants
	public static final Logger logger = LogManager.getLogger();
	
	// --- Attributes
	final LgPlugin plugin;
	
	// --- Methods
	public StopCommand(LgPlugin plugin) {
		super();
		this.plugin = plugin;
	}
	
	@Override
	public Pattern getPattern() {
		return Pattern.compile("\\Qstop\\E", Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	public void onCommandReceived(MessageReceivedEvent event, Matcher matcher, String params) {
		// Only in a guild
		final User author = event.getAuthor();
		if (!event.isFromGuild()) {
			event.getChannel().sendMessageFormat("%s, le jeu %s n'est possible que dans un Discord de guilde.",
					author.getAsMention(), Game.GAME_NAME).queue();
			return;
		}
		
		// Game already running in this channel ?
		final MessageChannel channel = event.getChannel();
		final Game game = this.plugin.getGame(channel);
		if (game == null) {
			channel.sendMessageFormat("%s, aucune partie de %s en cours dans <#%d>.",
					author.getAsMention(), Game.GAME_NAME, channel.getIdLong()).queue();
			return ;
		}
		
		// Initialize new game
		final User owner = game.getOwner();
		if (!owner.equals(author)) {
			channel.sendMessageFormat("%s, seul %s peut annuler la partie en cours : %s.",
					author, owner.getAsMention(), game.toString()).queue();
			return ;
		}
		game.onEnd();
		this.plugin.setGame(channel, null);
		channel.sendMessageFormat("%s, partie annulée : %s.",
				author, game.toString()).queue();
		return ;
	}
}
