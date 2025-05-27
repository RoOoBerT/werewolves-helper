package com.rooobert.werewolves.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFrame;

import com.rooobert.werewolves.Role;
import com.rooobert.werewolves.RolesWorkbook;

public class Launcher {
	public static void main(String args[]) throws Exception {
		JFrame frame = new JFrame();
		frame.setTitle("Werewolves Helper by RoOoBerT");
		
		frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		System.exit(0);
        	}
        });
        
        // Create paintable panel
		List<Role> roles = RolesWorkbook.load(Paths.get("THIERCELIEUX.xlsx"));
        frame.add(new WerewolfPanel(roles));
        
        // Set size
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = (int) screenSize.getWidth();
        final int height = (int) screenSize.getHeight();
        
        frame.setSize(width * 3 / 4, height * 3 / 4);
        frame.setLocation((width - frame.getWidth()) / 2, (height - frame.getHeight()) / 2);
		frame.setVisible(true);
	}
}
