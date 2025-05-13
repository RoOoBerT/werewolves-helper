package fr.rooobert.discord.rooobot.plugins.lg;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.BiConsumer;

import fr.rooobert.discord.rooobot.plugins.lg.ai.Menu;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class HumanPlayer extends Player {
	// --- Constants
	public static final String EMOJI_UNICODE = "🧑";
	
	// --- Attributes
	private final User user;
	private final PrivateChannel privateChannel;
	
	// --- Methods
	public HumanPlayer(int index, User user, PrivateChannel privateChannel) {
		super(index);
		this.user = user;
		this.privateChannel = privateChannel;
	}
	
	// - Discord functions
	public User getUser() {
		return this.user;
	}
	
	public PrivateChannel getPrivateChannel() {
		return this.privateChannel;
	}
	
	@Override
	public String getAsMention() {
		return this.user.getAsMention();
	}
	
	@Override
	public String getAsTag() {
		return this.user.getAsTag();
	}
	
	@Override
	public <Type> void showMenu(Menu<Type> menu, BiConsumer<Player, Type> selectionHandler) {
		menu.show(this.privateChannel, this, selectionHandler);
	}
	
	@Override
	public <Type> void showMenu(GuildChannel channel, Menu<Type> menu, BiConsumer<Player, Type> selectionHandler) {
		menu.show((MessageChannel) channel, this, selectionHandler);
	}
	
	@Override
	public String getEmojiUnicode() {
		return EMOJI_UNICODE;
	}
	
	@Override
	public void sendMessage(String message, InputStream is, String filename) {
		MessageAction sendMessage = this.privateChannel.sendMessage(message);
		if (is != null) {
			sendMessage.addFile(is, filename);
		}
		sendMessage.queue();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.user.getIdLong());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HumanPlayer) {
			return this.user.getIdLong() == ((HumanPlayer) obj).user.getIdLong();
		}
		return false;
	}
}
