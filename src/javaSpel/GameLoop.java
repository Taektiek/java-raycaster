package javaSpel;

import java.awt.BorderLayout;
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
	
	int framerate = 30;

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
		int rand_int_1 = (int)(Math.random()*500);
		int rand_int_2 = (int)(Math.random()*500);
		int rand_int_3 = (int)(Math.random()*50);
		g.fillRect(rand_int_1, rand_int_2, rand_int_3, rand_int_3);
	}

	/**
	 * Create the frame.
	 */
	public GameLoop() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		final Timer t = new Timer(1000/framerate, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("joo");
				
				repaint();
			}
		});
		
		t.start();
		
	}

}
