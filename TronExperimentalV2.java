import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class TronExperimentalV2 extends JFrame {
	private final Random rand;
	private final int cols, rows;
	private final ImageIcon black, blackWithGrid, grid, icoWall;
	private JLabel[][] board;
	private int[][] walls;

	private final rider[] riders = {
			new rider("Player 1", new Color(255, 0, 0)),
			new rider("Player 2", new Color(0, 255, 0)),
			new rider("Player 3", new Color(0, 0, 255))
	};

	private int speed, playerCount;
	private boolean ai, pause = false;
	private Panel p, q;
	private int players = getPlayers();
	private final javax.swing.Timer movementTimer, refreshTimer;
	private JWindow menu, settings;
	private JButton btnQuit, btnSettings, btnUpdate, btnCancel, btnRestart, btnResume;
	private JLabel[][] lblCycle;
	private JTextField[] name, r, g, b;
	private Dimension dim;

	public TronExperimentalV2() {
		super("Tron");

		//Color c=JColorChooser.showDialog(this,"Choose",Color.CYAN);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		dim = Toolkit.getDefaultToolkit().getScreenSize();

		speed = 60;

		cols = dim.width / 15;
		rows = dim.height / 15;

		p = new Panel();
		q = new Panel();
		p.setLayout(new GridLayout(riders.length, 9));
		q.setLayout(new FlowLayout());
		Container cp = getContentPane();
		cp.setLayout(new GridLayout(rows, cols));

		rand = new Random();

		ButtonListener listener = new ButtonListener();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocation(-50, -50);
		frame.pack();
		frame.setVisible(true);

		menu = new JWindow();
		settings = new JWindow(frame);

		menu.setLayout(new GridLayout(4, 1));
		settings.setLayout(new GridLayout(2, 1));

		lblCycle = new JLabel[3][5];
		name = new JTextField[3];
		r = new JTextField[3];
		g = new JTextField[3];
		b = new JTextField[3];

		for (int i = 0; i < lblCycle.length; i++) {
			lblCycle[i][0] = new JLabel("Cycle " + i + ": ");
			lblCycle[i][1] = new JLabel(" Name: ");
			lblCycle[i][2] = new JLabel(" R: ");
			lblCycle[i][3] = new JLabel(" G: ");
			lblCycle[i][4] = new JLabel(" B: ");
		}

		for (int i = 0; i < r.length; i++) {
			name[i] = new JTextField(riders[i].toString(), 20);
			r[i] = new JTextField(Integer.toString(riders[i].getColor().getRed()), 10);
			g[i] = new JTextField(Integer.toString(riders[i].getColor().getGreen()), 10);
			b[i] = new JTextField(Integer.toString(riders[i].getColor().getBlue()), 10);
		}

		btnRestart = new JButton("Restart");
		btnResume = new JButton("Resume");
		btnUpdate = new JButton("Update");
		btnCancel = new JButton("Cancel");
		btnQuit = new JButton("Quit");
		btnSettings = new JButton("Settings");
		black = new ImageIcon("images/Colors/black.png");
		blackWithGrid = new ImageIcon("images/Colors/blackWithGrid.png");
		icoWall = new ImageIcon("images/Colors/blue.png");

		grid = blackWithGrid;
		reset();

		setStarts();

		for (int i = 0; i < riders.length; i++) {
			p.add(lblCycle[i][0]);
			p.add(lblCycle[i][1]);
			p.add(name[i]);
			p.add(lblCycle[i][2]);
			p.add(r[i]);
			p.add(lblCycle[i][3]);
			p.add(g[i]);
			p.add(lblCycle[i][4]);
			p.add(b[i]);
		}

		menu.addKeyListener(new keyListener());
		settings.addKeyListener(new keyListener());

		menu.add(btnResume);
		menu.add(btnRestart);
		menu.add(btnSettings);
		menu.add(btnQuit);
		menu.pack();

		q.add(btnCancel);
		q.add(btnUpdate);

		settings.add(p);
		settings.add(q);

		settings.pack();

		center();

		for (JLabel[] board1 : board) {
			for (JLabel board2 : board1) {
				cp.add(board2);
			}
		}

		btnQuit.addActionListener(listener);
		btnSettings.addActionListener(listener);
		btnCancel.addActionListener(listener);
		btnUpdate.addActionListener(listener);
		btnRestart.addActionListener(listener);
		btnResume.addActionListener(listener);

		refreshTimer = new javax.swing.Timer(15, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// p.dispose();
				repaint();
			}
		});

		this.addKeyListener(new keyListener());

		validate();
		pack();

		int x = (dim.width - this.getSize().width) / 2;
		int y = (dim.height - this.getSize().height) / 2;

		setBackground(Color.black);
		setExtendedState(this.MAXIMIZED_BOTH);
		setVisible(true);

		movementTimer = new javax.swing.Timer(speed, new Mover());

		javax.swing.Timer refresher = new javax.swing.Timer(15, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// this.revalidate();
				settings.repaint();
			}
		});

		refresher.start();
		movementTimer.start();

		System.out.println(board[0][0].getHeight() + ", " + board[0][0].getWidth());

	}

	private void setStarts() {
		for (int i = 0; i < riders.length; i++) {
			riders[i].setLoc(new int[] { rand.nextInt(rows - (rows / 4)) + (rows / 8),
					rand.nextInt(cols - (cols / 4)) + (cols / 8) });

			System.out.println(riders[i].getX() + " " + riders[i].getY());

			if (!board[riders[i].getX()][riders[i].getY()].getIcon().equals(grid))
				setStarts();
		}

		try {
			for (int i = 0; i < riders.length; i++)
				for (int j = i + 1; j < riders.length; j++)
					if (riders[i].getX() == riders[j].getX() && riders[i].getY() == riders[j].getY())
						setStarts();
		} catch (StackOverflowError e) {
			System.out.println("Infinite Recurion");
		}
	}

	public int getPlayers() {
		Object[] options = { "One", "Two", "Three" };
		playerCount = (int) JOptionPane.showOptionDialog(null, "How many players ",
				"Difficulty Level", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
				options[1]);

		playerCount++;

		System.out.println(playerCount);

		for (int i = 0; i < playerCount; i++)
			riders[i].setAlive(true);

		ai = false;

		return playerCount;
	}

	public void updateBoard(ImageIcon trail, int rider) {
		for (int x = 0; x < board.length; x++)
			for (int y = 0; y < board[x].length; y++)
				if (board[x][y].getIcon().equals(trail))
					board[x][y].setIcon(riders[rider].getTrail());
	}

	public void center() {
		int x = (int) (dim.getWidth() - menu.getSize().width) / 2;
		int y = (int) (dim.getHeight() - menu.getSize().height) / 2;

		menu.setLocation(x, y);

		x = (int) (dim.getWidth() - settings.getSize().width) / 2;
		y = (int) (dim.getHeight() - settings.getSize().height) / 2;

		settings.setLocation(x, y);
	}

	public void reset() {
		// players = getPlayers();
		System.out.println("Reset Called");

		board = new JLabel[rows][cols];
		walls = new int[rand.nextInt(50)][2];

		for (int[] wall : walls) {
			wall[0] = rand.nextInt(rows);
			wall[1] = rand.nextInt(rows);
		}

		for (JLabel[] board2 : board) {
			for (int j = 0; j < board2.length; j++) {
				board2[j] = new JLabel(grid);
			}
		}
		for (int[] wall : walls) {
			board[wall[0]][wall[1]].setIcon(icoWall);
		}

		for (int i = 0; i < this.playerCount; i++) {
			riders[i].setAlive(true);
		}

		setStarts();
		this.players = this.playerCount;

		System.out.println("Players: " + players + "\n\tP1: " + riders[0].isAlive() + "\n\tP2: " + riders[1].isAlive()
				+ "\n\tP3: " + riders[2].isAlive());
	}

	private class Mover implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// if(pause)
			// while(pause);
			for (int i = 0; i < riders.length; i++)
				try {
					if (riders[i].isAlive())
						switch (riders[i].getDirection()) {
							case 0: // LEFT
								board[riders[i].getX()][riders[i].getY()].setIcon(riders[i].getTrail());
								riders[i].setY(riders[i].getY() - 1);

								if (!board[riders[i].getX()][riders[i].getY()].getIcon().equals(grid))
									remove(i);
								else
									board[riders[i].getX()][riders[i].getY()]
									.setIcon(
										new ImageIcon(
											riders[i].getImage().getImage().getScaledInstance(
												board[riders[i].getX()][riders[i].getX()].getWidth(),
												board[riders[i].getX()][riders[i].getX()].getHeight(),
												Image.SCALE_SMOOTH
											)
										)
									);
								break;
							case 1: // RIGHT
								board[riders[i].getX()][riders[i].getY()].setIcon(riders[i].getTrail());
								riders[i].setY(riders[i].getY() + 1);

								if (!board[riders[i].getX()][riders[i].getY()].getIcon().equals(grid))
									remove(i);
								else
									board[riders[i].getX()][riders[i].getY()]
											.setIcon(new ImageIcon(riders[i].getImage().getImage().getScaledInstance(
													board[riders[i].getX()][riders[i].getX()].getWidth(),
													board[riders[i].getX()][riders[i].getX()].getHeight(),
													Image.SCALE_SMOOTH)));
								break;
							case 2: // UP
								board[riders[i].getX()][riders[i].getY()].setIcon(riders[i].getTrail());
								riders[i].setX(riders[i].getX() - 1);

								if (!board[riders[i].getX()][riders[i].getY()].getIcon().equals(grid))
									remove(i);
								else
									board[riders[i].getX()][riders[i].getY()]
											.setIcon(new ImageIcon(riders[i].getImage().getImage().getScaledInstance(
													board[riders[i].getX()][riders[i].getX()].getWidth(),
													board[riders[i].getX()][riders[i].getX()].getHeight(),
													Image.SCALE_SMOOTH)));
								break;
							case 3: // DOWN
								board[riders[i].getX()][riders[i].getY()].setIcon(riders[i].getTrail());
								riders[i].setX(riders[i].getX() + 1);

								if (!board[riders[i].getX()][riders[i].getY()].getIcon().equals(grid))
									remove(i);
								else
									board[riders[i].getX()][riders[i].getY()]
											.setIcon(new ImageIcon(riders[i].getImage().getImage().getScaledInstance(
													board[riders[i].getX()][riders[i].getX()].getWidth(),
													board[riders[i].getX()][riders[i].getX()].getHeight(),
													Image.SCALE_SMOOTH)));
								break;
						}
				} catch (Exception ex) {
					remove(i);
				}
		}

		public void remove(int player) {
			try {
				System.out.println("Remove " + player + " Triggered");

				for (int i = 0; i < rows; i++)
					for (int j = 0; j < cols; j++)
						if (board[i][j].getIcon().equals(riders[player].getTrail())
								|| board[i][j].getIcon().equals(riders[player].getImage()))
							board[i][j].setIcon(grid);

				riders[player].setAlive(false);

				players--;
			} catch (Exception e) {
				System.out.println(e.toString());
				System.out.println("Player Out Of Bounds");
				riders[player].setAlive(false);
				players--;
			}

			if (players < 2)
				endGame();
		}

		public void endGame() {
			System.out.println("Game Over!");

			for (int i = 0; i < riders.length; i++)
				if (riders[i].isAlive())
					JOptionPane.showMessageDialog(null, riders[i] + " Wins!");

			int restart = JOptionPane.showConfirmDialog(null, "Do you Wish To Restart?", "Restart",JOptionPane.YES_NO_OPTION);
			if (restart == 0)
				reset();
			else {
				JOptionPane.showMessageDialog(null, "Goodbye!");
				System.exit(0);
			}
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();

			if (src == btnQuit)
				System.exit(0);
			else if (src == btnSettings)
				settings.setVisible(true);
			else if (src == btnCancel)
				settings.setVisible(false);
			else if (src == btnUpdate) {
				for (int i = 0; i < riders.length; i++)
					updateBoard(riders[i].update(name[i].getText(), new Color(Integer.parseInt(r[i].getText()),
							Integer.parseInt(g[i].getText()), Integer.parseInt(b[i].getText()))), i);

				System.out.println("Settings Updated.");
			} else if (src == btnRestart) {

			} else if (src == btnResume) {
				menu.dispose();
				pause = false;
				movementTimer.start();
			}
		}
	}

	private class keyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (riders[0].isAlive()) {
				if (key == KeyEvent.VK_LEFT)
					if (riders[0].getDirection() != 1)
						riders[0].setDirection(0);

				if (key == KeyEvent.VK_RIGHT)
					if (riders[0].getDirection() != 0)
						riders[0].setDirection(1);

				if (key == KeyEvent.VK_UP)
					if (riders[0].getDirection() != 3)
						riders[0].setDirection(2);

				if (key == KeyEvent.VK_DOWN)
					if (riders[0].getDirection() != 2)
						riders[0].setDirection(3);

			}

			if (riders[1].isAlive()) {
				if (!ai) {
					// System.out.println("No Ai");
					if (key == KeyEvent.VK_A)
						if (riders[1].getDirection() != 1)
							riders[1].setDirection(0);

					if (key == KeyEvent.VK_D)
						if (riders[1].getDirection() != 0)
							riders[1].setDirection(1);

					if (key == KeyEvent.VK_W)
						if (riders[1].getDirection() != 3)
							riders[1].setDirection(2);

					if (key == KeyEvent.VK_S)
						if (riders[1].getDirection() != 2)
							riders[1].setDirection(3);
				}
			}

			if (riders[2].isAlive()) {
				if (key == KeyEvent.VK_J)
					if (riders[2].getDirection() != 1)
						riders[2].setDirection(0);

				if (key == KeyEvent.VK_L)
					if (riders[2].getDirection() != 0)
						riders[2].setDirection(1);

				if (key == KeyEvent.VK_I)
					if (riders[2].getDirection() != 3)
						riders[2].setDirection(2);

				if (key == KeyEvent.VK_K)
					if (riders[2].getDirection() != 2)
						riders[2].setDirection(3);
			}
			if (key == KeyEvent.VK_ESCAPE) {
				if (pause) {
					System.out.println("Menu Close Request");
					pause = false;
					movementTimer.start();
					// menu.dispose();
					menu.setVisible(false);
					settings.setVisible(false);
				} else {
					System.out.println("Menu Open Request");
					pause = true;
					movementTimer.stop();
					// getMenu();
					menu.setVisible(true);
				}
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

	}
}