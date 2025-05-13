package com.rooobert.werewolves;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public enum Team {
	// --- Values
	VILLAGERS("🧑‍🌾", "Villageois"),
	WEREWOLVES("🐺", "Loup-Garou"),
	OWN("🪞", "Personnel")
	;
	
	// --- Attributes
	private final String emoji;
	private final String nameFr;
	private final BufferedImage image;
	
	// --- Methods
	private Team(String emoji, String nameFr) {
		this.emoji = emoji;
		this.nameFr = nameFr;

		final String resourcePath = String.format("/images/teams/%s.png", this.name());
		try (InputStream resourceAsStream = Role.class.getResourceAsStream(resourcePath)) {
			if (resourceAsStream == null) {
				//throw new RuntimeException(String.format("Resource image for %s not found : %s", this.name(), resourcePath));
			}
			this.image = null;//ImageIO.read(resourceAsStream);
		} catch (IOException e) {
			throw new RuntimeException("Failure loading werewolf card image : " + e.getMessage(), e);
		}
	}
}
