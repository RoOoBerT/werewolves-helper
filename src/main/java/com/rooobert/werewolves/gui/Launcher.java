package com.rooobert.werewolves.gui;

import com.rooobert.werewolves.StandardRole;

public class Launcher {
	public static void main(String args[]) throws Exception {
		// Declare nodes
		
		for (StandardRole role : StandardRole.values()) {
			System.out.println(role.toString());
		}
	}
}
