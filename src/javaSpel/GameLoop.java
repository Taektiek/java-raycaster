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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameLoop extends JFrame {

	private JPanel contentPane;
	
	int framerate = 10;
	
	boolean drawTopDown = true;
	
	int screenResX = 1600;
	int screenResY = 800;
	
	int gameResX = 800;
	int gameResY = 800;
	
	int[][] mapMatrix = {
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 2, 0, 0, 2, 0, 0, 1},
			{1, 0, 0, 2, 0, 0, 2, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 3, 0, 0, 0, 0, 3, 0, 1},
			{1, 0, 0, 3, 3, 3, 3, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
	};
	
	int tileSize = 50;
	
	int tileStartX = 900;
	int tileStartY = 100;
	
	int playerX = 975;
	int playerY = 175;
	
	int playerMoveSpeed = 10;
	double playerRotateSpeed = 15;
	
	int playerWidth = 20;
	
	double playerAngle = 45;
	
	int lineLength = 500;
	
	boolean movingRight = false;
	boolean movingLeft = false;
	boolean movingUp = false;
	boolean movingDown = false;
	
	int castLimit = 500;
	double castIncrement = 0.05;
	
	double fov = 90;
	int horizontalResolution = 200;

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
		
		if (drawTopDown) {
		
			paintGrid(g);
			paintPlayer(g);
			for (int i = 0; i < horizontalResolution; i ++) {
				double angleOffset = (i - (horizontalResolution / 2))*(fov/horizontalResolution);
				int[] castCoords = cast(playerAngle + angleOffset);
				paintPlayerSight(g, playerAngle + angleOffset, castCoords);
				paintCastPoint(g, castCoords);
			}
		}
		for (int i = 0; i < horizontalResolution; i ++) {
			double angleOffset = (i - (horizontalResolution / 2))*(fov/horizontalResolution);
			int[] castCoords = cast(playerAngle + angleOffset);
			double wallDistance = Math.sqrt(Math.pow(Math.abs(castCoords[0]-playerX),2)+Math.pow(Math.abs(castCoords[1]-playerY),2));
			
			int rectWidth = gameResX/horizontalResolution;
			
			double wallCloseness = 50000/(wallDistance*Math.cos(Math.toRadians(Math.abs(angleOffset))));
			
			int matrixValue = mapMatrix[(int)(castCoords[1]-tileStartY)/tileSize][(int)(castCoords[0]-tileStartX)/tileSize];
			
			switch (matrixValue) {
				case 1:
					g.setColor(Color.getHSBColor((float) 0.5, (float) 0.0, 20/(float) wallDistance));
					break;
				case 2:
					g.setColor(Color.getHSBColor((float) 0, (float) 1.0, 100/(float) wallDistance));
					wallCloseness = wallCloseness * 3 + 100;
					break;
				case 3:
					g.setColor(Color.getHSBColor((float) 0.186, (float) 1.0, 100/(float) wallDistance));
					break;
			}
			
			
			
			g.fillRect(i*rectWidth, (int)((gameResY-wallCloseness)/2), rectWidth, (int)wallCloseness);
		}
		
	}
	
	public void paintCastPoint(Graphics g, int[] coords) {
		g.setColor(Color.RED);
		g.fillOval(coords[0]-3, coords[1]-3, 6, 6);
	}
	
	public void paintGrid(Graphics g) {
		g.setColor(Color.BLACK);
		for (int i=0; i<mapMatrix.length; i++) {
			for (int j=0; j<mapMatrix[i].length; j++) {
				if (mapMatrix[i][j]!=0) {
					g.fillRect(tileStartX + j*tileSize, tileStartY + i*tileSize, tileSize, tileSize);
				}
			}
		}
	}
	
	public void paintPlayer(Graphics g) {
		g.setColor(Color.PINK);
		g.fillOval((int)(playerX-0.5*playerWidth), (int)(playerY-0.5*playerWidth), playerWidth, playerWidth);
	}
	
	public void paintPlayerSight(Graphics g, double angle, int[] castCoords) {
		g.setColor(Color.GRAY);
		g.drawLine(playerX, playerY, castCoords[0], castCoords[1]);
	}
	
	public void paintClear(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, screenResX, screenResY);
		g.setColor(Color.getHSBColor((float)0.08, (float)0.6, (float)0.4));
		g.fillRect(0, gameResY/2, gameResX, gameResY/2);
	}
	
	public void handleMovementKeys(KeyEvent e, boolean release) {
		if (e.getKeyChar() == 'w' && !release) {
			movingUp = true;
		}
		if (e.getKeyChar() == 's' && !release) {
			movingDown = true;
		}
		if (e.getKeyChar() == 'a' && !release) {
			movingLeft = true;
		}
		if (e.getKeyChar() == 'd' && !release) {
			movingRight = true;
		}
		if (e.getKeyChar() == 'w' && release) {
			movingUp = false;
		}
		if (e.getKeyChar() == 's' && release) {
			movingDown = false;
		}
		if (e.getKeyChar() == 'a' && release) {
			movingLeft = false;
		}
		if (e.getKeyChar() == 'd' && release) {
			movingRight = false;
		}
	}
	
	public void movePlayer() {
		int[] moveCast = cast(playerAngle);
		int[] backwardsMoveCast = cast((playerAngle+180)%360);
		
		double wallDistance = Math.sqrt(Math.pow(Math.abs(moveCast[0]-playerX),2)+Math.pow(Math.abs(moveCast[1]-playerY),2));
		double backwardsWallDistance = Math.sqrt(Math.pow(Math.abs(backwardsMoveCast[0]-playerX),2)+Math.pow(Math.abs(backwardsMoveCast[1]-playerY),2));
		
		if (movingUp && wallDistance > playerWidth) {
			playerX = playerX + (int)(Math.cos(Math.toRadians(playerAngle)) * playerMoveSpeed);
			playerY = playerY + (int)(Math.sin(Math.toRadians(playerAngle)) * playerMoveSpeed);
		}
		if (movingDown && backwardsWallDistance > playerWidth) {
			playerX = playerX - (int)(Math.cos(Math.toRadians(playerAngle)) * playerMoveSpeed);
			playerY = playerY - (int)(Math.sin(Math.toRadians(playerAngle)) * playerMoveSpeed);
		}
		if (movingLeft) {
			playerAngle = (360+playerAngle - playerRotateSpeed) % 360;
		}
		if (movingRight) {
			playerAngle = (playerAngle + playerRotateSpeed) % 360;
		}
	}
	
	public boolean inGrid(double X, double Y) {
		return mapMatrix[(int)(Y)][(int)(X)] != 0;
	}
	
	public int[] cast(double angle) {
		int[] coords = {100, 100};
		
		double checkerX = (double)(playerX-tileStartX)/tileSize;
		double checkerY = (double)(playerY-tileStartY)/tileSize;
		
		
		for (int i = 0; i < castLimit; i++) {
			if (inGrid(checkerX, checkerY)) {
				coords[0] = (int)(checkerX*tileSize+tileStartX);
				coords[1] = (int)(checkerY*tileSize+tileStartY);
				return coords;
			}
			checkerX = checkerX + Math.cos(Math.toRadians(angle)) * castIncrement;
			checkerY = checkerY + Math.sin(Math.toRadians(angle)) * castIncrement;
			
		}
		
		return coords;
	}

	/**
	 * Create the frame.
	 */
	public GameLoop() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleMovementKeys(e, false);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				handleMovementKeys(e, true);
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (drawTopDown) {
			setBounds(0, 0, screenResX, screenResY);
		} else {
			setBounds(0, 0, screenResX/2, screenResY);
		}
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		final Timer t = new Timer(1000/framerate, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				movePlayer();
				repaint();
			}
		});
		
		t.start();
		
	}

}
