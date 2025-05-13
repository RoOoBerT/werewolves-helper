package fr.rooobert.discord.rooobot.plugins.lg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rooobert.math.Range.RangeInt;

import fr.rooobert.discord.DiscordUtilities;
import fr.rooobert.discord.rooobot.RoOoBoTException;
import fr.rooobert.discord.rooobot.plugins.lg.ai.AiPlayer;
import fr.rooobert.discord.rooobot.plugins.lg.ai.Menu;
import fr.rooobert.discord.rooobot.plugins.lg.ai.MenuItem;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class Game {
	// --- Constants
	public static final Logger logger = LogManager.getLogger();
	public static final RangeInt PLAYER_COUNT = new RangeInt(7, 66);
	public static final String GAME_NAME = "Loups-Garous de Thiercelieux";
	
	// --- Types
	public static enum Status {
		PENDING("⏳", "En attente de joueurs"),
		DAY("☀️", "Jour"),
		NIGHT("🌕", "Nuit"),
		FINISHED("♻️", "Terminé"),
		;
		
		public final String emoji;
		public final String label;
		
		private Status(String emoji, String label) {
			this.emoji = emoji;
			this.label = label;
		}
	}
	
	// --- Attributes
	// Game metadata
	private final long seed;
	private final User owner;
	private final MessageChannel channel;
	private final Guild guild;
	private final Date time;
	private final String gameName;
	private final Random random;
	
	// Dynamic game properties
	private final StringBuilder gameLog = new StringBuilder();
	private final List<Player> players = new ArrayList<>(PLAYER_COUNT.getMax());
	private final Map<Long, HumanPlayer> humanPlayersById = new HashMap<>(PLAYER_COUNT.getMax());
	private final GameSettings settings = GameSettings.DEFAULT;
	
	//
	private int day = 0;
	private Status status = Status.PENDING;
	
	// --- Methods
	public Game(User owner, Guild guild, MessageChannel channel, Date time, long seed) {
		super();
		this.owner = owner;
		this.channel = channel;
		this.guild = guild;
		this.time = time;
		this.seed = seed;
		this.random = new Random(seed);
				
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.gameName = String.format("Partie de %s par %s dans %s sur %s le %s (seed %s)",
				GAME_NAME, this.owner.getAsTag(), this.channel.getName(), this.guild.getName(), 
				df.format(this.time), this.getSeedStr());
		
		this.logFormat("%s crée la partie : %s", owner.getAsTag(), this.gameName);
	}
	
	public String getName() {
		return this.gameName;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public User getOwner() {
		return this.owner;
	}
	
	public HumanPlayer getHumanPlayerById(long id) {
		return this.humanPlayersById.get(id);
	}
	
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(this.players);
	}
	
	public List<Player> getPlayers(boolean includeDeadPlayers) {
		if (includeDeadPlayers) {
			return Collections.unmodifiableList(this.players);
		}
		return Collections.unmodifiableList(this.players.stream().filter(Player::isAlive).collect(Collectors.toList()));
	}
	
	public List<Player> getPlayersByRole(RoleType role) {
		return this.getPlayersByRole(role, false);
	}
	public List<Player> getPlayersByRole(RoleType role, boolean includeDeadPlayers) {
		final List<Player> playersByRole = new ArrayList<>();
		for (Player player : this.players) {
			if (includeDeadPlayers || player.isAlive()) {
				if (player.getCurrentRoles().contains(role)) {
					playersByRole.add(player);
				}
			}
		}
		
		return playersByRole;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public String getSeedStr() {
		return Long.toHexString(this.getSeed()).toUpperCase();
	}
	
	@Override
	public String toString() {
		return this.gameName;
	}
	
	public void log(String message) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss - ");
		this.gameLog.append(df.format(new Date()));
		this.gameLog.append(message);
		this.gameLog.append("\n");
	}
	
	public void logFormat(String format, Object ... params) {
		this.log(String.format(format, params));
	}
	
	public HumanPlayer addHumanPlayer(User user, PrivateChannel privateChannel) {
		final int index = this.players.size();
		final HumanPlayer human = new HumanPlayer(index, user, privateChannel);
		
		// Is user already registered ?
		final Player alreadyRegisteredPlayer = this.humanPlayersById.put(user.getIdLong(), human);
		if (alreadyRegisteredPlayer != null) {
			logger.warn("User tried to join {} while already registered : {}",
					this.gameName, user.getAsTag());
			return null;
		}
		
		this.players.add(human);
		this.onPlayerJoin(human);
		return human;
	}
	
	public AiPlayer addAiPlayer() {
		final int index = this.players.size();
		AiPlayer ai = new AiPlayer(index);
		this.players.add(ai);
		this.onPlayerJoin(ai);
		return ai;
	}
	
	/** Inform a player of his/her role 
	 * @param player 
	 * @param role */
	private void notifyRole(Player player, RoleType role) {
		String filename = String.format("%s.png", role.name());
		player.sendMessageFormat(role.getImageAsInputStream(), filename,
				"**%s**\nVous avez reçu le rôle de %s %s.\n%s", this.gameName, role.getEmoji(), role.getName(), role.getDescription());
	}
	
	public void showStatusMessage() {
		this.channel.sendMessageFormat("**%s Jour %d - %s**\n",
				this.status.emoji, this.day, this.status.label)
			.queue();
	}
	
	public <Type> void createMultiuserVote(MessageChannel channel, Collection<Player> players, Menu<Type> menu, Consumer<Map<Player, Type>> selectionHandler) {
		final Map<Player, Type> results = new HashMap<>();
		if (players.isEmpty()) {
			throw new RoOoBoTException("Cannot create a poll for 0 users !");
		}
		
		menu.show(channel, players, (Player player, Type choice) -> {
			results.put(player, choice);
			if (results.size() == players.size()) {
				selectionHandler.accept(results);
			}
		});
	}
	
	// --- Events
	public void onStart() {
		final int playerCount = this.players.size();
		
		// Make a list of roles to distribute
		final List<RoleType> selectedRoles = new ArrayList<>(playerCount);
		
		// http://jeuxstrategie.free.fr/Loups_garous_complet.php
		// How many werewolves are expected ?
		final long werewolvesCount;
		if (!this.settings.disableBlackPlague && this.random.nextInt(10) == 0) {
			// Black plague !
			werewolvesCount = 0;
		} else {
			final int recommendedWerwolvesCount = playerCount / 4;
			werewolvesCount = recommendedWerwolvesCount + this.random.nextInt(3) - 1;
			
			for (int i = 0; i != werewolvesCount; i++) {
				selectedRoles.add(RoleType.WEREWOLF);
			}
		}
		this.logFormat("Pour un total de %d joueurs, nombre de loups-garous choisi pseudo-aleatoirement : %d%s",
				playerCount, werewolvesCount, (werewolvesCount > 0 ? "" : " -> C'est la peste noire !"));
		
		// Select random roles
		final List<RoleType> availableRoles = new ArrayList<>(Arrays.asList(RoleType.values()));
		availableRoles.remove(RoleType.WEREWOLF);
		while (selectedRoles.size() < playerCount) {
			final int randomIndex = this.random.nextInt(availableRoles.size());
			final RoleType randomRole = availableRoles.remove(randomIndex);
			selectedRoles.add(randomRole);
		}
		
		String strRoles = selectedRoles.stream().map(RoleType::getName).collect(Collectors.joining(", "));
		this.logFormat("Les roles suivants ont été choisis pour %d joueurs : %s",
				playerCount, strRoles);
		
		// Associate each player with a random role
		Collections.shuffle(selectedRoles, this.random);
		for (Player player : this.players) {
			final RoleType role = selectedRoles.remove(0);
			player.addRole(role);
			this.notifyRole(player, role);
		}
		
		// Log attribution of roles
		boolean first = true;
		strRoles = "";
		for (RoleType role : RoleType.values()) {
			
			// List players with this role
			final List<Player> playersByRole = this.getPlayersByRole(role);
			if (!playersByRole.isEmpty()) {
				if (!first) {
					strRoles += ", ";
				}
				first = false;
				
				// 
				strRoles += role.getName() + " (";
				for (Player player : playersByRole) {
					strRoles += player.getAsTag();
				}
				strRoles += ")";
			}
		}
		this.logFormat("L'attribution des %d roles a été faite de la façon suivante :\n%s",
				playerCount, strRoles);
		
		//// Create a group chat between werewolves -> Technically impossible ?
		//List<Player> werewolves = this.getPlayersByRole(Role.WEREWOLF);
		//if (werewolves.size() > 1) {
		//	this.owner.getJDA().
		//}
		
		// 
		this.onNight();
	}
	
	public void onDay() {
		// 
		this.status = Status.DAY;
		this.logFormat("---- Jour %d - %s ----",
				this.day, this.status.label);
		
		// Create a menu of alive players to kill
		final List<MenuItem<Player>> choices = new ArrayList<>();
		for (Player player : this.getPlayers(false)) {
			choices.add(new MenuItem<Player>(player.getAsTag(), player));
		}
		
		final String menuLabel = String.format("**%s**\nVotez pour la personne à éliminer.", this.gameName);
		final Menu<Player> selectTargetPlayerMenu = new Menu<>(menuLabel, choices);
		this.createMultiuserVote(this.channel, this.players, selectTargetPlayerMenu, voteResults -> {
			//
			Map<Integer, List<Player>> collect = voteResults.values().stream().collect(Collectors.groupingBy(Player::getIndex));
			this.channel.sendMessageFormat("Resultats du vote : %d", collect.size()).queue();
		});
	}
	
	public void onNight() {
		this.day++;
		this.status = Status.NIGHT;
		this.logFormat("---- Jour %d - %s ----",
				this.day, this.status.label);
		this.showStatusMessage();
		
		
		this.onDay();
	}
	
	public void onEnd() {
		this.status = Status.FINISHED;
		final String message = String.format("**Résumé de la partie : %s**\n```%s```",
				this.gameName, this.gameLog.toString());
		DiscordUtilities.sendLongDiscordMessage(this.channel, "```", "```", message);
	}
	
	public void onPlayerJoin(Player player) {
		this.logFormat("%s rejoint la partie de %s", player.getAsTag(), Game.GAME_NAME);
		this.channel.sendMessageFormat("%s %s a rejoint la partie de %s.",
			player.getEmojiUnicode(), player.getAsMention(), Game.GAME_NAME).queue();
	}
}
