package com.rooobert.werewolves;

import java.awt.image.BufferedImage;
import java.util.Set;

public class CustomRole implements Role {
	// --- Constants
	
	// --- Attributes
	private final String name;
	private final String description;
	private final int min;
	private final int max;
	private final BufferedImage image;
	private final String behaviour;
	private final Set<String> teams;
	
	// --- Methods
	public CustomRole(String name, String description, int min, int max, BufferedImage image, String behaviour,
			Set<String> teams) {
		super();
		this.name = name;
		this.description = description;
		this.min = min;
		this.max = max;
		this.image = image;
		this.behaviour = behaviour;
		this.teams = teams;
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

	@Override
	public String getDescription() {
		return this.description;
	}
}
