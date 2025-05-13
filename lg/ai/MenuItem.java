package fr.rooobert.discord.rooobot.plugins.lg.ai;

import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

public class MenuItem<Type> {
	// --- Constants
	
	// --- Attributes
	private ReactionEmote emoji;
	private final String label;
	private final Type data;
	
	// --- Methods
	public MenuItem(String label, Type data) {
		this(null, label, data);
	}
	
	public MenuItem(ReactionEmote emoji, String label, Type data) {
		super();
		this.emoji = emoji;
		this.label = label;
		this.data = data;
	}
	
	public void setEmoji(ReactionEmote emoji) {
		this.emoji = emoji;
	}
	
	public ReactionEmote getEmoji() {
		return this.emoji;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public Type getData() {
		return this.data;
	}
}

