package javaSpel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class GameLoop extends JFrame {

	private JPanel contentPane;
	
	int framerate = 10;
	
	int[][] mapMatrix = {
			{1, 1, 1, 1, 1},
			{1, 0, 1, 1, 1},
			{1, 0, 1, 0, 1},
			{1, 0, 0, 0, 1},
			{1, 0, 1, 1, 1},
			{1, 0, 1, 0, 1},
			{1, 0, 0, 0, 1},
			{1, 1, 1, 1, 1}
	};
	
	int tileMargin = 0;
	int tileSize = 50;
	
	int tileStartX = 100;
	int tileStartY = 100;
	
	int playerX = 175;
	int playerY = 175;
	
	int playerWidth = 20;
	
	double playerAngle = 45;
	
	int lineLength = 20;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameLoop frame = new GameLoop();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void paint(Graphics g) {
		
		paintClear(g);
		
		paintGrid(g);
		paintPlayer(g);
		paintPlayerSight(g);
		
	}
	
	public void paintGrid(Graphics g) {
		g.setColor(Color.BLACK);
		for (int i=0; i<mapMatrix.length; i++) {
			for (int j=0; j<mapMatrix[i].length; j++) {
				if (mapMatrix[i][j]==1) {
					g.fillRect(tileStartX + j*tileSize + j*tileMargin, tileStartY + i*tileSize + i*tileMargin, tileSize, tileSize);
				}
			}
		}
	}
	
	public void paintPlayer(Graphics g) {
		g.setColor(Color.PINK);
		g.fillOval((int)(playerX-0.5*playerWidth), (int)(playerY-0.5*playerWidth), playerWidth, playerWidth);
	}
	
	public void paintPlayerSight(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawLine(playerX, playerY, (int)(playerX+(Math.cos(playerAngle)*lineLength)), (int)(playerY+(Math.sin(playerAngle)*lineLength)));
	}
	
	public void paintClear(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * Create the frame.
	 */
	public GameLoop() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 800, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		final Timer t = new Timer(1000/framerate, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		
		t.start();
		
	}

}
