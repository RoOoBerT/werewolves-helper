package fr.rooobert.discord.rooobot.plugins.lg.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import fr.rooobert.discord.dialog.DiscordOptionDialogWithMenu;
import fr.rooobert.discord.rooobot.plugins.lg.HumanPlayer;
import fr.rooobert.discord.rooobot.plugins.lg.Player;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

public class Menu<Type> {
	// --- Constants
	public final static String[] CHARACTERS_EMOJIS_UNICODE = {"🇦", "🇧", "🇨", "🇩", "🇪", "🇫", "🇬", "🇭", "🇮", "🇯"};
	
	// --- Attributes
	final String text;
	final Collection<MenuItem<Type>> options = new ArrayList<>();
	
	// --- Methods
	public Menu(String text, Collection<MenuItem<Type>> options) {
		super();
		this.text = text;
		this.options.addAll(options);
	}
	
	public String getText() {
		return this.text;
	}
	
	public Collection<MenuItem<Type>> getOptions() {
		return Collections.unmodifiableCollection(this.options);
	}
	
	public void show(MessageChannel channel, BiConsumer<Player, Type> selectionHandler) {
		this.show(channel, Arrays.asList(), selectionHandler);
	}
	public void show(MessageChannel channel, Player allowedPlayer, BiConsumer<Player, Type> selectionHandler) {
		this.show(channel, Arrays.asList(allowedPlayer), selectionHandler);
	}
	public void show(MessageChannel channel, Collection<Player> allowedPlayers, BiConsumer<Player, Type> selectionHandler) {
		// Create a list of available emojis
		final List<ReactionEmote> availableEmotes = new ArrayList<>(this.options.size());
		for (String emojiUnicode : CHARACTERS_EMOJIS_UNICODE) {
			availableEmotes.add(ReactionEmote.fromUnicode(emojiUnicode, channel.getJDA()));
		}
		
		// Create discord dialog
		DiscordOptionDialogWithMenu dialog = new DiscordOptionDialogWithMenu();
		dialog.setText(this.text);
		for (MenuItem<Type> option : this.options) {
			ReactionEmote emote = option.getEmoji();
			if (emote == null) {
				emote = availableEmotes.remove(0);
			}
			dialog.addOption(emote, option.getLabel(), optionSelectedEvent -> {
				// Identify which player responded
				Optional<Player> player = allowedPlayers.stream().filter(p -> {
					return  (p instanceof HumanPlayer) && ((HumanPlayer) p).getUser().getIdLong() == optionSelectedEvent.getUser().getIdLong();
				}).findAny();
				// Call the handler
				selectionHandler.accept(player.get(), option.getData());
				optionSelectedEvent.deferEdit().queue();
			});
		}
		
		// (Optional) Restrict menu interaction to specified players
		if (allowedPlayers != null) {
			for (Player user : allowedPlayers) {
				if (user instanceof HumanPlayer) {
					dialog.addUser(((HumanPlayer) user).getUser());
				}
			}
		}
		
		dialog.show(channel);
	}
}
