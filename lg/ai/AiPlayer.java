package fr.rooobert.discord.rooobot.plugins.lg.ai;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

import fr.rooobert.discord.rooobot.plugins.lg.Player;
import net.dv8tion.jda.api.entities.GuildChannel;

public class AiPlayer extends Player {
	// --- Constants
	public static final String[] AI_NAMES = {"Alice", "Bob", "Charlie", "Daisy", "Fifi", "Lili", "Mike", "Riri", "Olivier", "Papa"};
	
	public static final String EMOJI_UNICODE = "🤖";
	
	// --- Attributes
	final String name;
	
	// --- Methods
	public AiPlayer(int index) {
		super(index);
		this.name = AI_NAMES[index];
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String getAsMention() {
		return this.getAsTag();
	}
	
	@Override
	public String getAsTag() {
		return String.format("Bot %s#%02d", this.name, this.getIndex());
	}
	
	@Override
	public <Type> void showMenu(Menu<Type> menu, BiConsumer<Player, Type> selectionHandler) {
		this.showMenu(null, menu, selectionHandler);
	}
	
	@Override
	public <Type> void showMenu(GuildChannel channel, Menu<Type> menu, BiConsumer<Player, Type> selectionHandler) {
		final Collection<MenuItem<Type>> options = menu.getOptions();
		final int optionCount = options.size();
		final int randomIndex = ThreadLocalRandom.current().nextInt(optionCount);
		final Iterator<MenuItem<Type>> iterator = options.iterator();
		for (int i = 0; i != randomIndex; i++) {
			iterator.next();
		}
		Type selectedItem = iterator.next().getData();
		selectionHandler.accept(this, selectedItem);
	}
	
	@Override
	public String getEmojiUnicode() {
		return EMOJI_UNICODE;
	}
	
	@Override
	public void sendMessage(String message, InputStream is, String filename) {
		// Do nothing (AI does not need to receive textual messages)
	}
}
