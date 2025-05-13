package fr.rooobert.discord.rooobot.plugins.lg;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import fr.rooobert.discord.rooobot.RoOoBoTException;
import fr.rooobert.discord.rooobot.plugins.lg.ai.Menu;
import net.dv8tion.jda.api.entities.GuildChannel;

public abstract class Player {
	// --- Constants
	
	// --- Attributes
	private final int index;
	
	// Variable game data
	private final Set<RoleType> roles = new HashSet<>();
	boolean alive = true;
	
	// --- Methods
	public Player(int index) {
		this.index = index;
	}
	
	/** Add a new role to a player 
	 * @param role */
	public final void addRole(RoleType role) {
		if (!this.roles.add(role)) {
			throw new RoOoBoTException(String.format("Player %s already has role %s !", this.getAsMention(), role.getName()));
		}
	}
	
	/** @return Current roles of the player */
	public final Set<RoleType> getCurrentRoles() {
		return Collections.unmodifiableSet(this.roles);
	}
	
	public final int getIndex() {
		return this.index;
	}
	
	public boolean isAlive() {
		return this.alive;
	}
	
	public boolean kill() {
		boolean alive = this.alive;
		this.alive = false;
		return alive;
	}
	
	// --- Abstract methods
	@Override
	public int hashCode() {
		return this.index;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			return ((Player) obj).index == this.index;
		}
		return false;
	}
	
	public abstract String getEmojiUnicode();
	
	public abstract String getAsMention();
	
	public abstract String getAsTag();
	
	// - Show menu
	public abstract <Type> void showMenu(Menu<Type> menu, BiConsumer<Player, Type> selectionHandler);
	
	public abstract <Type> void showMenu(GuildChannel channel, Menu<Type> menu, BiConsumer<Player, Type> selectionHandler);
	
	// - Send message
	public abstract void sendMessage(String message, InputStream is, String filename);
	
	public final void sendMessage(String message) {
		this.sendMessage(message, null, null);
	}
	
	public final void sendMessageFormat(String format, Object ... params) {
		this.sendMessageFormat(null, format, params);
	}
	
	public final void sendMessageFormat(InputStream is, String filename, String format, Object ... params) {
		this.sendMessage(String.format(format, params), is, filename);
	}
}
