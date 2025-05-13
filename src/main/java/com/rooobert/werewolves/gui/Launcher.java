package com.rooobert.werewolves.gui;

import com.rooobert.werewolves.Role;

public class Launcher {
	public static void main(String args[]) throws Exception {
		// Declare nodes
		
		for (Role role : Role.values()) {
			System.out.println(role.toString());
		}
	}
}
