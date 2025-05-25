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

import javax.swing.JFrame;
import javax.swing.JPanel;

public class WerewolfPanel extends JPanel {
	// --- Constants
	
	// --- Attributes
	
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
        this.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				WerewolfPanel.mousePressed(e);
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				WerewolfPanel.mouseWheelMoved(e);
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				WerewolfPanel.mouseMoved(e);
			}
		});
	}
	
	protected static void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	protected static void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
	}
	
	protected static void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setBackground(Color.RED);
        g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
        
    }
}
