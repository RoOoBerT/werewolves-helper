package fr.rooobert.discord.rooobot.plugins.lg;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import fr.rooobert.discord.rooobot.RoOoBoTException;

// https://boardgamegeek.com/image/181307/werewolves-millers-hollow
public enum RoleType {
	// --- Values
	ANGEL("👼", "Ange", 1,
			"Si l'ange est éliminé le premier jour suite au vote du village, il remporte la partie.\n"
			+ "(Ne fonctionne pas avec tout autre type de décès tel que morsure des loups-garous, potion de la sorcière etc.)\n"
			+ "A partir du second jour, s'il est toujours en vie, il devient un villageois."
		),
	CUPID("💘", "Cupidon", 1,
			"Lors de la première nuit, cupidon peut rendre deux personnes amoureuses.\n"
			+ "Indépendament de leurs rôles, les amoureux(ses) sont liés à la vie à la mort et doivent terminer la partie à deux."
		),
	FORTUNE_TELLER("🔮", "Voyante", 1,
			"Chaque nuit, la voyante peut découvrir le rôle d'un des joueurs."
		),
	HUNTER("🔪", "Chasseur", 1,
			"En mourant, le chassseur tue une personne de son choix."
		),
	LITTLE_GIRL("👧", "Petite fille", 1,
			"Durant la nuit, la petite fille peut entendre les loups-garous."
		),
	VILLAGER("🧑‍🌾", "Villageois", 0,
			"En fin de journée, les villageois font un vote pour choisir quelle personne sera pendue."
		),
	WEREWOLF("🐺", "Loup-garou", 0, 
			"Durant la nuit, les loups-garous se concertent pour dévorer un des villageois."
		),
	SCAPEGOAT("👉", "Bouc émissaire", 1,
			"En cas d'égalité lors du vote du village, c'est le bouc émissaire qui est tué."
		),
	SORCERESS("🧙‍♀️", "Sorcière", 1,
			"Pour toute la partie, la sorcière possède une potion de vie permettant de ressuciter un joueur,"
			+ " et une potion de mort permettant de tuer un joueur."
		),
	;
	
	// --- Constants
	public static RoleType[] ROLE_RESOLUTION_ORDER = {};
	
	// --- Attributes
	private final String emoji;
	private final String name;
	private final String description;
	private final int min;
	private final BufferedImage image;
	
	// --- Methods
	private RoleType(String emoji, String name, int max, String description) {
		this.emoji = emoji;
		this.name = name;
		this.min = max;
		this.description = description;
		
		// Load images
		final String resourcePath = String.format("/lg/%s.png", this.name());
		try (InputStream resourceAsStream = RoleType.class.getResourceAsStream(resourcePath)) {
			if (resourceAsStream == null) {
				throw new RuntimeException(String.format("Resource image for %s not found : %s", this.name, resourcePath));
			}
			this.image = ImageIO.read(resourceAsStream);
		} catch (IOException e) {
			throw new RoOoBoTException("Failure loading werewolf card image : " + e.getMessage(), e);
		}
	}
	
	public String getEmoji() {
		return this.emoji;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getMaxCount() {
		return this.min;
	}
	
	public Image getImage() {
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

	public String getDescription() {
		return this.description;
	}
}
