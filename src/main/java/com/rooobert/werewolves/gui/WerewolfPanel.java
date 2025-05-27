package com.rooobert.werewolves.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.sampled.Clip;
import javax.swing.JPanel;

import com.rooobert.audio.Sound;
import com.rooobert.image.ImageUtils;
import com.rooobert.math.geometry.Rectangle2DInt;
import com.rooobert.werewolves.Role;

public class WerewolfPanel extends JPanel {
	// --- Constants
	public static final Clip SOUND_POP = Sound.loadSound(Paths.get("Pop.wav"));
	
	// --- Attributes
	private final List<Role> roles;
	private final Set<String> teams;
	private final Set<Role> activeRoles = new HashSet<>();
	private final Map<Role, Rectangle2DInt> areaByRole = new HashMap<>();
	
	private final transient int maxRolesPerTeam;
	
	// --- Methods
	public WerewolfPanel(List<Role> roles) {
		this.roles = Collections.unmodifiableList(roles);
		this.activeRoles.addAll(roles);
		
		// Save teams
		Map<String, Integer> rolesByTeam = new HashMap<>();
		Set<String> teams = new HashSet<>();
		for (Role role : roles) {
			for (String team : role.getTeams()) {
				teams.add(team);
				
				int count = rolesByTeam.getOrDefault(team, 0);
				rolesByTeam.put(team, count + 1);
			}
		}
		this.teams = Collections.unmodifiableSet(teams);
		this.maxRolesPerTeam = rolesByTeam.values().stream().max(Integer::compareTo).get();
		
		// Register events
        this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent event) {
				WerewolfPanel.this.mousePressed(event);
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent event) {
				WerewolfPanel.this.mouseWheelMoved(event);
			}
			
			@Override
			public void mouseMoved(MouseEvent event) {
				WerewolfPanel.this.mouseMoved(event);
			}
		});
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
            	WerewolfPanel.this.componentResized(event);
            }
        });
	}
	
	protected void componentResized(ComponentEvent event) {
		this.recomputePositions(this.getWidth(), this.getHeight());
	}
	
	protected void mousePressed(MouseEvent event) {
		Role role = this.getRoleAt(event.getX(), event.getY());
		if (role != null) {
			if (!this.activeRoles.contains(role)) {
				this.activeRoles.add(role);
				Sound.play(SOUND_POP);
			} else {
				this.activeRoles.remove(role);
				Sound.play(SOUND_POP);
			}
		}
	}
	
	protected void mouseWheelMoved(MouseWheelEvent event) {
		// 
	}
	
	protected void mouseMoved(MouseEvent event) {
		// 
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
        for (Entry<Role, Rectangle2DInt> entry : this.areaByRole.entrySet()) {
        	final Role role = entry.getKey();
        	final Rectangle2DInt area = entry.getValue();
        	
        	final BufferedImage originalImage = role.getImage();
        	final int cardSize = Math.min(area.getWidth(), area.getHeight());
        	final BufferedImage resizedImage = ImageUtils.resizeImage(originalImage, cardSize, cardSize);
        	
        	g2d.drawImage(resizedImage, area.getX(), area.getY(), null);
		}
    }
	
	private void recomputePositions(int width, int height) {
		final int columns = this.teams.size();
		final int columnWidth = width / columns;
		
		final int lines = this.maxRolesPerTeam;
		final int rowHeight = height / lines;
		
		final int cardSize = Math.min(columnWidth, rowHeight);
		
		int x = 0;
		for (String team : this.teams) {
			int y = 0;
			for (Role role : this.activeRoles) {
				if (role.getTeams().contains(team) && role.getTeams().size() == 1) {
					this.areaByRole.put(role, new Rectangle2DInt(x, y, cardSize, cardSize));
					y += rowHeight;
				}
			}
			
			x += columnWidth;
		}
	}
	
	private Role getRoleAt(int x, int y) {
		for (Entry<Role, Rectangle2DInt> entry : this.areaByRole.entrySet()) {
			Rectangle2DInt area = entry.getValue();
			if (area.contains(x, y)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
