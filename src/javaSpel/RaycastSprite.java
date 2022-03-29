package javaSpel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RaycastSprite {
	
	int x;
	int y;
	private double size;
	
	private String imagePath;
	
	private BufferedImage picture;

	public RaycastSprite(int x, int y, double size, String imagePath) {
		
		this.x = x;
		this.y = y;
		
		this.size = size;
		
		this.imagePath = imagePath;
		
		// Read the image path from the file system
		try {
			this.picture = ImageIO.read(new File(this.imagePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	// Draw the sprite at the location in screenspace
	public void renderSprite(Graphics g, ImageObserver observer, int playerX, int playerY, double playerAngle, double fov) {
		
		
		double screenPercentage = toScreenPercentage(playerX, playerY, playerAngle, fov);
		
		g.setColor(Color.CYAN);
		
		g.fillRect(this.x, this.y, 10, 10);
		
		int width = (int)(size*10000/(Math.sqrt(Math.pow(Math.abs(this.x-playerX), 2)+Math.pow(Math.abs(this.y-playerY), 2))));
		int height = width;
		
		g.drawImage(this.picture, (int)(screenPercentage*800-(0.5*width)), 400, width, height, observer);
		
	}
	
	// Converts the location of the sprite in the grid to the position it should have on the screen
	private double toScreenPercentage(int playerX, int playerY, double playerAngle, double fov) {
		
		double tan;
		
		tan = (double)(this.y-playerY)/(double)(this.x-playerX);
		
		double pointAngle = (360+(fov+(Math.toDegrees(Math.atan(tan))-(playerAngle+0.5*fov))))%360;
		
		if (this.x-playerX<0) {
			pointAngle = (180 + pointAngle)%360;
		}
		
		return pointAngle/fov;
	}
	
}
