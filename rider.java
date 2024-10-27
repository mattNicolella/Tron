import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;
import java.awt.*;

public class rider {
	private Color color;
	private String name;
	private int direction;
	private int[] loc;
	private BufferedImage[] bike;
	private ImageIcon[] icoBike;
	private ImageIcon icoTrail;
	private BufferedImage trail;
	private String path;
	private boolean alive;
	private Random rand;

	public static void main(String[] args) throws Exception {
		rider r = new rider("Paul", new Color(115, 69, 69));
		System.out.println(new Color(115, 69, 69).getRGB());
		JWindow w = new JWindow();
		w.add(new JLabel(r.getImage()));
		w.pack();
		w.setVisible(true);
	}

	public rider() {
		rand = new Random();
		name = "";
		direction = rand.nextInt(4);
		loc = new int[2];
		alive = true;
	}

	public rider(String name, Color color) {
		this.color = color;
		rand = new Random();
		this.name = name;
		// direction = rand.nextInt(4);
		direction = 1;
		loc = new int[2];
		alive = false;
		path = "images/bikes/";
		try {
			trail = ImageIO.read(new File("images/Colors/black.png"));
			icoTrail = new ImageIcon(trail);
			BufferedImage[] bikes = { ImageIO.read(new File(path + "left.png")),
					ImageIO.read(new File(path + "right.png")),
					ImageIO.read(new File(path + "up.png")),
					ImageIO.read(new File(path + "down.png")) };
			bike = bikes;
		} catch (Exception e) {
			System.out.println("Incorrect File Path");
		}

		for (int i = 0; i < bike.length; i++)
			for (int x = 0; x < bike[i].getHeight(); x++)
				for (int y = 0; y < bike[i].getWidth(); y++) {
					if (bike[i].getRGB(x, y) == new Color(55, 255, 0).getRGB()
							|| bike[i].getRGB(x, y) == new Color(134, 216, 118).getRGB())
						bike[i].setRGB(x, y, color.getRGB());

					if (trail.getRGB(x, y) == new Color(0, 0, 0).getRGB())
						trail.setRGB(x, y, color.getRGB());

				}

		icoBike = new ImageIcon[bike.length];

		for (int i = 0; i < bike.length; i++)
			icoBike[i] = new ImageIcon(bike[i]);
	}

	public ImageIcon update(String name, Color color) {
		ImageIcon tmpTrail = icoTrail;

		this.name = name;
		this.color = color;

		for (int i = 0; i < bike.length; i++)
			for (int x = 0; x < bike[i].getHeight(); x++)
				for (int y = 0; y < bike[i].getWidth(); y++) {
					if (bike[i].getRGB(x, y) == new Color(55, 255, 0).getRGB()
							|| bike[i].getRGB(x, y) == new Color(134, 216, 118).getRGB())
						bike[i].setRGB(x, y, color.getRGB());

					if (trail.getRGB(x, y) == new Color(0, 0, 0).getRGB())
						trail.setRGB(x, y, color.getRGB());

				}

		return tmpTrail;
	}

	public void setLoc(int[] loc) {
		this.loc = loc;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setColor(Color c) {
		color = c;
	}

	public void setX(int x) {
		loc[0] = x;
	}

	public void setY(int y) {
		loc[1] = y;
	}

	public int[] getLoc() {
		return loc;
	}

	public int getX() {
		return loc[0];
	}

	public int getY() {
		return loc[1];
	}

	public boolean isAlive() {
		return alive;
	}

	public ImageIcon getImage() {
		return icoBike[direction];
	}

	public ImageIcon getTrail() {
		return icoTrail;
	}

	public int getDirection() {
		return direction;
	}

	public Color getColor() {
		return color;
	}

	public String toString() {
		return name;
	}
}