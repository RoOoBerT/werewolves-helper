package com.rooobert.werewolves.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.rooobert.audio.Sound;
import com.rooobert.image.ImageUtils;
import com.rooobert.werewolves.StandardRole;

public class WerewolfPanel extends JPanel {
	// --- Constants
	public static final Clip SOUND_POP = Sound.loadSound(Paths.get("Pop.wav"));
	
	// --- Attributes
	private final Set<StandardRole> activeRoles = new HashSet<>(StandardRole.values().length);
	
	
	// --- Methods
	public static void main(String args[]) {
		JFrame frame = new JFrame();
		frame.setTitle("Werewolves Helper by RoOoBerT");
		
		frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		System.exit(0);
        	}
        });
        
        // Create paintable panel
        frame.add(new WerewolfPanel());
        
        // Set size
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = (int) screenSize.getWidth();
        final int height = (int) screenSize.getHeight();
        
        frame.setSize(width * 3 / 4, height * 3 / 4);
        frame.setLocation((width - frame.getWidth()) / 2, (height - frame.getHeight()) / 2);
		frame.setVisible(true);
	}
	
	public WerewolfPanel() {
		// Register events
        this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent event) {
				WerewolfPanel.mousePressed(event);
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent event) {
				WerewolfPanel.mouseWheelMoved(event);
			}
			
			@Override
			public void mouseMoved(MouseEvent event) {
				WerewolfPanel.mouseMoved(event);
			}
		});
	}
	
	protected static void mousePressed(MouseEvent event) {
		Sound.play(SOUND_POP);
	}

	protected static void mouseWheelMoved(MouseWheelEvent event) {
		// TODO Auto-generated method stub
	}
	
	protected static void mouseMoved(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dimensions
        final int width = this.getWidth();
        final int height = this.getHeight();
        
        // Clear background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setBackground(Color.RED);
        g2d.clearRect(0, 0, width, height);
        
        // Analyze the list of roles
        final StandardRole[] roles = StandardRole.values();
        final int roleSize = height / (1 + roles.length);
        
        for (int i = 0; i != roles.length; i++) {
        	final StandardRole role = roles[i];
        	final BufferedImage originalImage = role.getImage();
        	final BufferedImage resizedImage = ImageUtils.resizeImage(originalImage, roleSize, roleSize);
        	
        	g2d.drawImage(resizedImage, width / 2 - resizedImage.getWidth() / 2, roleSize / 2 + i * roleSize, null);
        }
    }
}
