package fr.rooobert.discord.rooobot.plugins.lg.cmds;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import fr.rooobert.discord.dialog.DiscordOptionDialogWithButtons;
import fr.rooobert.discord.rooobot.PluginSubcommand;
import fr.rooobert.discord.rooobot.plugins.lg.Game;
import fr.rooobert.discord.rooobot.plugins.lg.Game.Status;
import fr.rooobert.discord.rooobot.plugins.lg.HumanPlayer;
import fr.rooobert.discord.rooobot.plugins.lg.LgPlugin;
import fr.rooobert.discord.rooobot.plugins.lg.Player;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public class StartCommand implements PluginSubcommand {
	// --- Constants
	public static final Logger logger = LogManager.getLogger();
	
	// --- Attributes
	final LgPlugin plugin;
	
	// --- Methods
	public StartCommand(LgPlugin plugin) {
		super();
		this.plugin = plugin;
	}
	
	@Override
	public Pattern getPattern() {
		return Pattern.compile("\\Qstart\\E", Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	public void onCommandReceived(MessageReceivedEvent event, Matcher matcher, String params) {
		// Only in a guild
		final User owner = event.getAuthor();
		if (!event.isFromGuild()) {
			event.getChannel().sendMessageFormat("%s, le jeu %s n'est possible que dans un Discord de guilde.",
					owner.getAsMention(), Game.GAME_NAME).queue();
			return;
		}
		
		// Game already running in this channel ?
		final MessageChannel channel = event.getChannel();
		{
			Game game = this.plugin.getGame(channel);
			if (game != null) {
				if (game.getStatus() != Status.FINISHED) {
					channel.sendMessageFormat("%s, dans <#%d>, une partie est déjà en cours : %s",
							owner.getAsMention(), channel.getIdLong(), game.toString()).queue();
					return ;
				}
				this.plugin.setGame(channel, null);
			}
		}
		
		owner.openPrivateChannel().queue(privateChannel -> {
			// Initialize new game
			final Game newGame = new Game(owner, event.getGuild(), channel, new Date(), ThreadLocalRandom.current().nextLong());
			this.plugin.setGame(channel, newGame);
			
			showStartDialog(channel, owner, newGame);
			
			newGame.addHumanPlayer(owner, privateChannel);
		}, t -> {
			// Open private channel failure : show error message
			channel.sendMessageFormat("🚫 Désolé %s, je n'ai pas réussi à accéder à tes messages privés.",
					owner.getAsMention()).queue();
			logger.warn(new ParameterizedMessage("Failure opening private channel with {} : {}",
					owner.getIdLong(), t.getMessage()), t);
		});
	}
	
	private static void showStartDialog(MessageChannel channel, User owner, Game newGame) {
		final JDA jda = channel.getJDA();
		DiscordOptionDialogWithButtons dialog = new DiscordOptionDialogWithButtons();
		dialog.setTextFormat("🐺 **%s**\n"
				+ "Utilisez la réaction ✋ pour rejoindre la partie.", newGame.toString());
		
		// Register a new human player
		dialog.addOption(ReactionEmote.fromUnicode("✋", jda), ButtonStyle.PRIMARY, "Rejoindre la partie", participationEvent -> {
			final User participant = participationEvent.getUser();
			
			participant.openPrivateChannel().queue(privateChannel -> {
				HumanPlayer newPlayer = newGame.addHumanPlayer(participant, privateChannel);
				if (newPlayer != null) {
					participationEvent.deferEdit().queue();
				}
			}, t -> {
				// Open private channel failure : show error message
				participationEvent.replyFormat("🚫 Désolé %s, je n'ai pas réussi à accéder à tes messages privés.",
						participant.getAsMention()).queue();
				logger.warn(new ParameterizedMessage("Failure opening private channel with {} : {}",
						participant.getIdLong(), t.getMessage()), t);
			});
		});
		
		// Register a new AI player
		dialog.addOption(ReactionEmote.fromUnicode("🤖", jda), ButtonStyle.SECONDARY, String.format("(%s) Ajouter un joueur IA", owner.getAsTag()), startEvent -> {
			// Is it game's owner ?
			final User startingUser = startEvent.getUser();
			if (!owner.equals(startingUser)) {
				startEvent.replyFormat("⚠️ %s, seulement %s peut réaliser cette action.",
						startingUser.getAsTag()).queue();
				return ;
			}
			
			// Check number of players
			final List<Player> players = newGame.getPlayers();
			if (players.size() >= Game.PLAYER_COUNT.getMax()) {
				startEvent.replyFormat("⚠️ %s, il faut au maximum %d joueurs pour démarrer une partie de %s ! (Actuellement %d)",
						startingUser.getAsMention(), Game.PLAYER_COUNT.getMax(), Game.GAME_NAME, players.size()).queue();
				return ;
			}
			
			// Start game
			newGame.addAiPlayer();
			//startEvent.replyFormat("**Ajout d'un joueur IA**", newGame.toString()).queue();
			startEvent.deferEdit().queue();
		});
		
		// Complete with AI players
		dialog.addOption(ReactionEmote.fromUnicode("👾", jda), ButtonStyle.SECONDARY, String.format("(%s) Compléter avec des joueurs IA", owner.getAsTag()), startEvent -> {
			// Is it game's owner ?
			final User startingUser = startEvent.getUser();
			if (!owner.equals(startingUser)) {
				startEvent.replyFormat("⚠️ %s, seulement %s peut réaliser cette action.",
						startingUser.getAsTag()).queue();
				return ;
			}
			
			// Check number of players
			final List<Player> players = newGame.getPlayers();
			if (players.size() >= Game.PLAYER_COUNT.getMin()) {
				startEvent.replyFormat("⚠️ %s, le nombre minimum de %d joueurs est déjà atteint !",
						startingUser.getAsMention(), Game.PLAYER_COUNT.getMin()).queue();
				return ;
			}
			
			// Start game
			while (newGame.getPlayers().size() < Game.PLAYER_COUNT.getMin()) {
				newGame.addAiPlayer();
			}
			
			startEvent.deferEdit().queue();
		});
		
		// Start the game
		dialog.addOption(ReactionEmote.fromUnicode("▶️", jda), ButtonStyle.SECONDARY, String.format("(%s) Démarrer la partie", owner.getAsTag()), startEvent -> {
			// Is it game's owner ?
			final User startingUser = startEvent.getUser();
			if (!owner.equals(startingUser)) {
				startEvent.replyFormat("⚠️ %s, c'est %s qui doit démarrer la partie.",
						startingUser.getAsTag()).queue();
				return ;
			}
			
			// Check number of players
			final List<Player> players = newGame.getPlayers();
			if (!Game.PLAYER_COUNT.contains(players.size())) {
				startEvent.replyFormat("⚠️ %s, il faut entre %d et %d joueurs pour démarrer une partie de %s ! (Actuellement %d)\n",
						startingUser.getAsMention(), Game.PLAYER_COUNT.getMin(), Game.PLAYER_COUNT.getMax(), Game.GAME_NAME, players.size()).queue();
				return ;
			}
			
			// Start game
			startEvent.replyFormat("**%s**", newGame.toString()).queue(success -> {
				newGame.onStart();
			});
			dialog.close();
		});
		
		dialog.show(channel);
	}
}
