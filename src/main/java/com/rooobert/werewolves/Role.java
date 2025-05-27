package com.rooobert.werewolves;

import java.awt.image.BufferedImage;
import java.util.Collection;

public interface Role {
	String getName();
	
	int getMinCount();
	int getMaxCount();
	
	public BufferedImage getImage();
	
	public String getDescription();

	public String getBehaviour();
	
	public Collection<String> getTeams();
}
