package fr.rooobert.discord.rooobot.plugins.lg;

import java.util.concurrent.TimeUnit;

public class GameSettings {
	// --- Constants
	public static final GameSettings DEFAULT = new GameSettings(
		TimeUnit.SECONDS.toMillis(60)
	);
	
	// --- Attributes
	public final long playerActionExpirationMs;
	public final boolean forbidRevealRole = false;
	public final boolean disableBlackPlague = false;
	
	// --- Methods
	public GameSettings(long playerActionExpirationMs) {
		super();
		
		this.playerActionExpirationMs = playerActionExpirationMs;
	}
}
