package javaSpel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.SystemColor;

public class GameLoop extends JFrame {

	private JPanel contentPane;
	
	int framerate = 10;
	
	boolean drawTopDown = false;
	
	int screenResX = 1600;
	int screenResY = 800;
	
	int gameResX = 800;
	int gameResY = 800;
	
	int frameCounter = 0;
	
	mazeGenerator maze = new mazeGenerator();
	
	int[][] mapMatrix = new int[mazeGenerator.height*2+1][mazeGenerator.width*2+1];
	
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
	
	int castLimit = 2500;
	double castIncrement = 0.01;
	
	double fov = 90;
	int horizontalResolution = 400;
	
	RaycastSprite basicKey;
	RaycastSprite guy;
	
	
	boolean firstRender = true;
	private JTextField Txtwelcoming;
	private JTextField txtKiesHierJe;
	private JTextField txtGoedGedaan;

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
		

		// Create RaycastSprites when first render
		if (firstRender) {
			
//			basicKey = new RaycastSprite(1050, 300, 1, "D:\\Data\\Programming\\java-raycaster\\sprites\\key.png");
//			guy = new RaycastSprite(1200, 350, 1, "D:\\Data\\Programming\\java-raycaster\\sprites\\guy.png");
			
			maze.generate();
			mapMatrix = maze.getMatrix();
			firstRender = false;
			return;
		}
		else if (frameCounter > 2) {
			
		paintClear(g);
		
		paintRaycastRects(g);
			
		
		
		
		// Compare distance between player and RaycastSprite to distance between the wall in the direction of the RaycastSprite and the player
		
//		double spriteAngle = Math.toDegrees(Math.atan((double)(basicKey.y-playerY)/(double)(basicKey.x-playerX)));
//		
//		int[] spriteCastCoords = cast(spriteAngle);
//		double spriteCastDistance = Math.sqrt(Math.pow(Math.abs(spriteCastCoords[0]-playerX),2)+Math.pow(Math.abs(spriteCastCoords[1]-playerY),2));
//		double spriteDistance = Math.sqrt(Math.pow(Math.abs(basicKey.x-playerX),2)+Math.pow(Math.abs(basicKey.y-playerY),2));
//		
//		if (spriteCastDistance > spriteDistance) {
//			
//			basicKey.renderSprite(g, this, playerX, playerY, playerAngle, fov);
//			
//		}
//		
//		if (spriteDistance < 30) {
//			basicKey.show = false;
//		}
//		
//
//		guy.renderSprite(g, this, playerX, playerY, playerAngle, fov);
		
		
		// When drawTopDown draw the grid to the right side of the window for the purpose of demonstration
		
		if (drawTopDown) {
		
			paintGrid(g);
			paintPlayer(g);
			
			// Draw every line of sight of the player
			for (int i = 0; i < horizontalResolution; i ++) {
				double angleOffset = (i - (horizontalResolution / 2))*(fov/horizontalResolution);
				int[] castCoords = cast(playerAngle + angleOffset);
				paintPlayerSight(g, playerAngle + angleOffset, castCoords);
				paintCastPoint(g, castCoords);
			}
		}
		}
		frameCounter++;
		
	}
	
	// Paints point at cast location
	public void paintCastPoint(Graphics g, int[] coords) {
		g.setColor(Color.RED);
		g.fillOval(coords[0]-3, coords[1]-3, 6, 6);
	}

	// Loops over map matrix and render squares
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
	
	// Paints player representation
	public void paintPlayer(Graphics g) {
		g.setColor(Color.PINK);
		g.fillOval((int)(playerX-0.5*playerWidth), (int)(playerY-0.5*playerWidth), playerWidth, playerWidth);
	}
	
	// Paints player line of sight
	public void paintPlayerSight(Graphics g, double angle, int[] castCoords) {
		g.setColor(Color.GRAY);
		if (angle == playerAngle) {
			g.setColor(Color.GREEN);
		}
		g.drawLine(playerX, playerY, castCoords[0], castCoords[1]);
	}
	
	// Resets screen after every frame
	public void paintClear(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, screenResX, screenResY);
		g.setColor(Color.getHSBColor((float)0.08, (float)0.6, (float)0.4));
		g.fillRect(0, gameResY/2, gameResX, gameResY/2);
	}
	
	// For each cast draws a rectangle with the correct size given the distance
	public void paintRaycastRects(Graphics g) {
		
		for (int i = 0; i < horizontalResolution; i ++) {
			double angleOffset = (i - (horizontalResolution / 2))*(fov/horizontalResolution);
			int[] castCoords = cast(playerAngle + angleOffset);
			double wallDistance = Math.sqrt(Math.pow(Math.abs(castCoords[0]-playerX),2)+Math.pow(Math.abs(castCoords[1]-playerY),2));
			
			int rectWidth = gameResX/horizontalResolution;
			
			double wallCloseness = 50000/(wallDistance*Math.cos(Math.toRadians(Math.abs(angleOffset))));
			
			int matrixValue = mapMatrix[(int)(castCoords[1]-tileStartY)/tileSize][(int)(castCoords[0]-tileStartX)/tileSize];
			
			
			// Picks different colours based on value in matrix
			switch (matrixValue) {
				case 1:
					g.setColor(Color.getHSBColor((float) 0.5, (float) 0.0, Math.min(20/(float) wallDistance,(float)1.0)));
					break;
				case 2:
					g.setColor(Color.getHSBColor((float) 0, (float) 1.0, Math.min(100/(float) wallDistance,(float)1.0)));
					break;
				case 3:
					g.setColor(Color.getHSBColor((float) 0.186, (float) 1.0, Math.min(100/(float) wallDistance,(float)1.0)));
					break;
				case 4:
					g.setColor(Color.getHSBColor((float) 0.8, (float) 1.0, Math.min(100/(float) wallDistance,(float)1.0)));
					break;
			}
			
			
			
			g.fillRect(i*rectWidth, (int)((gameResY-wallCloseness)/2), rectWidth, (int)wallCloseness);
		}
		
	}
	
	// Handle all the different keys that control the game
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
		if (e.getKeyChar() == 'o' && !release) {
			fov -= 3;
		}
		if (e.getKeyChar() == 'p' && !release) {
			fov += 3;
		}
		if (e.getKeyChar() == 'k' && !release) {
			horizontalResolution -= 10;
		}
		if (e.getKeyChar() == 'l' && !release) {
			horizontalResolution += 10;
		}

	}
	
	// Moves the player based on the values assigned in handleMovementKeys()
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
	
	// Checks if a given coordinate is a wall on the grid
	public boolean inGrid(double X, double Y) {
		return mapMatrix[(int)(Y)][(int)(X)] != 0;
	}
	
	// Casts a line given a certain angle and returns the distance to the nearest wall in that direction
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
		// Pick different screen size based on drawTopDown
		if (drawTopDown) {
			setBounds(0, 0, screenResX, screenResY);
		} else {
			setBounds(0, 0, screenResX/2, screenResY);
		}
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("Panel.background"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel startPanel = new JPanel();
		startPanel.setForeground(UIManager.getColor("Panel.background"));
		startPanel.setBackground(UIManager.getColor("Panel.background"));
		startPanel.setBounds(10, 10, 853, 704);
		contentPane.add(startPanel);
		startPanel.setLayout(null);
		startPanel.setVisible (true);
		
		
		// Create and start frame loop timer.
		final Timer t = new Timer(1000/framerate, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				movePlayer();
				repaint();
			}
		});
		JButton btnEasy = new JButton("Langster");
		btnEasy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				startPanel.setVisible(false);
				t.start();
				revalidate();
				requestFocus();
					
			}
		});
		
		JPanel PanelEind = new JPanel();
		PanelEind.setBounds(10, 10, 738, 626);
		startPanel.add(PanelEind);
		PanelEind.setLayout(null);
		PanelEind.setVisible(false);
		
		txtGoedGedaan = new JTextField();
		txtGoedGedaan.setFont(new Font("Rockwell", Font.PLAIN, 28));
		txtGoedGedaan.setText("Goed gedaan");
		txtGoedGedaan.setHorizontalAlignment(SwingConstants.CENTER);
		txtGoedGedaan.setBounds(232, 154, 299, 81);
		PanelEind.add(txtGoedGedaan);
		txtGoedGedaan.setColumns(10);
		
		JButton btnOpnieuw = new JButton("Begin opnieuw");
		btnOpnieuw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PanelEind.setVisible(false);
				startPanel.setVisible(true);
			}
		});
		btnOpnieuw.setBounds(232, 314, 299, 57);
		PanelEind.add(btnOpnieuw);
		btnEasy.setFont(new Font("Rockwell", Font.PLAIN, 28));
		btnEasy.setBackground(new Color(30, 144, 255));
		btnEasy.setBounds(110, 245, 556, 105);
		startPanel.add(btnEasy);
		
		JButton Btngemiddeld = new JButton("Gemiddeld");
		Btngemiddeld.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startPanel.setVisible(false);
				t.start();
				revalidate();
				requestFocus();
			}
		});
		Btngemiddeld.setFont(new Font("Rockwell", Font.PLAIN, 28));
		Btngemiddeld.setBackground(new Color(60, 179, 113));
		Btngemiddeld.setBounds(110, 407, 556, 109);
		startPanel.add(Btngemiddeld);
		
		JButton BtnMoeilijk = new JButton("Gangster");
		BtnMoeilijk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startPanel.setVisible(false);
				t.start();
				revalidate();
				requestFocus();
			}
		});
		BtnMoeilijk.setFont(new Font("Rockwell", Font.PLAIN, 28));
		BtnMoeilijk.setBackground(new Color(178, 34, 34));
		BtnMoeilijk.setBounds(110, 569, 556, 109);
		startPanel.add(BtnMoeilijk);
		
		Txtwelcoming = new JTextField();
		Txtwelcoming.setFont(new Font("Rockwell", Font.PLAIN, 28));
		Txtwelcoming.setText("Welkom ");
		Txtwelcoming.setHorizontalAlignment(SwingConstants.CENTER);
		Txtwelcoming.setBackground(UIManager.getColor("Panel.background"));
		Txtwelcoming.setBounds(145, 36, 494, 68);
		startPanel.add(Txtwelcoming);
		Txtwelcoming.setColumns(10);
		
		txtKiesHierJe = new JTextField();
		txtKiesHierJe.setText("Kies hier je moeilijkheidsgraad");
		txtKiesHierJe.setHorizontalAlignment(SwingConstants.CENTER);
		txtKiesHierJe.setFont(new Font("Rockwell", Font.PLAIN, 28));
		txtKiesHierJe.setColumns(10);
		txtKiesHierJe.setBackground(SystemColor.menu);
		txtKiesHierJe.setBounds(145, 129, 494, 68);
		startPanel.add(txtKiesHierJe);
		
		
		
		
	}
}
