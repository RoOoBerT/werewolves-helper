package com.rooobert.werewolves;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

// https://boardgamegeek.com/image/181307/werewolves-millers-hollow
public enum StandardRole implements Role {
	// --- Values
	ANGEL("👼", "Ange", 0, 1, Behaviour.PASSIVE, Team.OWN, Team.VILLAGERS,
			"Si l'ange est éliminé le premier jour suite au vote du village, il remporte la partie.\n"
			+ "(Ne fonctionne pas avec tout autre type de décès tel que morsure des loups-garous, potion de la sorcière etc.)\n"
			+ "A partir du second jour, s'il est toujours en vie, il devient un villageois."
		),
	CUPID("💘", "Cupidon", 0, 1, Behaviour.CALLED_ONCE, Team.VILLAGERS,
			"Lors de la première nuit, cupidon peut rendre deux personnes amoureuses.\n"
			+ "Indépendament de leurs rôles, les amoureux(ses) sont liés à la vie à la mort et doivent terminer la partie à deux."
		),
	DEFENDER("🛡️", "Salvateur", 0, 1, Behaviour.CALLED_EACH_NIGHT, Team.VILLAGERS,
			""
		),
	DEVOTED_SERVANT("🧹", "Servante dévouée", 0, 1, Behaviour.PASSIVE, Team.OWN,
			""
		),
	HUNTER("🔫", "Chasseur", 0, 1, Behaviour.PASSIVE, Team.VILLAGERS,
			"En mourant, le chassseur tue une personne de son choix."
		),
	LITTLE_GIRL("👧", "Petite fille", 0, 1, Behaviour.PASSIVE, Team.VILLAGERS,
			"Durant la nuit, la petite fille peut entendre les loups-garous."
			),
	PREJUDICED_MANIPULATOR("🏴‍☠️", "Abominable Sectaire", 0, 1, Behaviour.PASSIVE, Team.OWN,
			""
		),
	RAVEN("🐦‍⬛", "Corbeau", 0, 1, Behaviour.CALLED_EACH_NIGHT, Team.VILLAGERS,
			""
		),
	SCAPEGOAT("👉", "Bouc émissaire", 0, 1, Behaviour.PASSIVE, Team.VILLAGERS,
			"En cas d'égalité lors du vote du village, c'est le bouc émissaire qui est tué."
			),
	SEER("🔮", "Voyante", 0, 1, Behaviour.CALLED_EACH_NIGHT, Team.VILLAGERS,
			"Chaque nuit, la voyante peut découvrir le rôle d'un des joueurs."
			),
	SHERIFF("⭐", "Shérif", 0, 1, Behaviour.PASSIVE, Team.OWN,
			""
			),
	THIEF("", "Voleur", 0, 1, Behaviour.PASSIVE, Team.OWN,
			""
			),
	THREE_BROTHERS("⭐", "3 frères", 3, 3, Behaviour.PASSIVE, Team.VILLAGERS,
			""
			),
	TRUSTED_VILLAGER("", "Villageois de confiance", 0, 1, Behaviour.PASSIVE, Team.VILLAGERS,
			""
			),
	TWO_SISTERS("", "2 soeurs", 2, 2, Behaviour.PASSIVE, Team.VILLAGERS,
			""
			),
	VILLAGE_ELDER("", "Ancient du village", 0, 1, Behaviour.PASSIVE, Team.VILLAGERS,
			""
			),
	VILLAGE_IDIOT("", "Idiot du village", 0, 1, Behaviour.PASSIVE, Team.VILLAGERS,
			""
			),
	VILLAGER("🧑‍🌾", "Villageois", 0, 0, Behaviour.PASSIVE, Team.VILLAGERS,
			"En fin de journée, les villageois font un vote pour choisir quelle personne sera pendue."
		),
	WANDERING_JUDGE("⚖️", "Juge", 0, 1, Behaviour.PASSIVE, Team.VILLAGERS,
			""
			),
	WEREWOLF("🐺", "Loup-garou", 1, 0, Behaviour.CALLED_EACH_NIGHT, Team.WEREWOLVES,
			"Durant la nuit, les loups-garous se concertent pour dévorer un des villageois."
		),
	WHITE_WEREWOLF("🐺", "Loup blanc", 0, 1, Behaviour.CALLED_EACH_NIGHT, Team.OWN,
			""
		),
	WILD_CHILD("🐺", "Enfant sauvage", 0, 1, Behaviour.CALLED_ONCE, Team.VILLAGERS, Team.WEREWOLVES,
			""
		),
	WITCH("🧙‍♀️", "Sorcière", 0, 1, Behaviour.CALLED_EACH_NIGHT, Team.VILLAGERS,
			"Pour toute la partie, la sorcière possède une potion de vie permettant de ressuciter un joueur,"
			+ " et une potion de mort permettant de tuer un joueur."
		),
	WOLF_HOUND("🐶", "Chien-loup", 0, 1, Behaviour.CALLED_ONCE, Team.VILLAGERS, Team.WEREWOLVES,
			""
		),
	;
	
	// --- Constants
	
	// --- Attributes
	private final String emoji;
	private final String name;
	private final String description;
	private final int min;
	private final int max;
	private final BufferedImage image;
	private final Behaviour behaviour;
	private final Team[] teams;
	
	// --- Methods
	private StandardRole(String emoji, String name, int min, int max, Behaviour behaviour, Team team, String description) {
		this(emoji, name, min, max, behaviour, new Team[] {team}, description);
	}
	
	private StandardRole(String emoji, String name, int min, int max, Behaviour behaviour, Team team1, Team team2, String description) {
		this(emoji, name, min, max, behaviour, new Team[] {team1, team2}, description);
	}
	
	private StandardRole(String emoji, String name, int min, int max, Behaviour behaviour, Team[] teams, String description) {
		this.emoji = emoji;
		this.name = name;
		this.min = min;
		this.max = max;
		this.description = description;
		this.behaviour = behaviour;
		this.teams = teams;
		
		// Load images
		final String resourcePath = String.format("/images/roles/%s.png", this.name());
		try (InputStream resourceAsStream = StandardRole.class.getResourceAsStream(resourcePath)) {
			if (resourceAsStream == null) {
				throw new RuntimeException(String.format("Resource image for %s not found : %s", this.name, resourcePath));
			}
			this.image = ImageIO.read(resourceAsStream);
		} catch (IOException e) {
			throw new RuntimeException("Failure loading werewolf card image : " + e.getMessage(), e);
		}
	}
	
	public String getEmoji() {
		return this.emoji;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getMinCount() {
		return this.min;
	}
	
	@Override
	public int getMaxCount() {
		return this.max;
	}
	
	@Override
	public BufferedImage getImage() {
		return this.image;
	}
	
	public InputStream getImageAsInputStream() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(this.image, "png", os);
		} catch (IOException e) {
			throw new RuntimeException("Fatal error writing image to stream", e);
		} // Passing: ​(RenderedImage im, String formatName, OutputStream output)
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		return is;
	}
	
	@Override
	public String getDescription() {
		return this.description;
	}
}
