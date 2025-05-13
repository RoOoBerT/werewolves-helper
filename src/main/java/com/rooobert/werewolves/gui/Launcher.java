package com.rooobert.werewolves.gui;

import jdk.javadoc.internal.doclets.formats.html.markup.HtmlAttr.Role;

public class Launcher {
	public static void main(String args[]) throws Exception {
		// Declare nodes
		
		for (Role role : Role.values()) {
			System.out.println(role.toString());
		}
	}
}
