//-------------------------------------------------------------------------------
// Minesweeper3.java
// LC
//
// This program emulates a simple game of Minesweeper.
//-------------------------------------------------------------------------------

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import sun.audio.*;
import java.io.*;

public class Minesweeper3 extends JFrame 
{
	private static Minesweeper3 game;
	private static int width; 
	private static int height;
	private static int numberOfMines; // Total mines on the board
	public static int remainingMines; // Number of mines remaining
	
	Square2[][] board;
		
	JPanel gamePanel;
	JPanel mineBoard;
	
	JTextField minesRemaining;
	JTextField timerBox;
	java.util.Timer timer;
	long startTime = System.nanoTime();
	long currentTime;
	
	MouseListener menuListen = new menuListen();
	
	// Menu Items
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem startGame;
	JRadioButtonMenuItem easy;
	JRadioButtonMenuItem medium;
	JRadioButtonMenuItem hard;
	JMenuItem quit;
	
		
	//-------------------------------------------------------------------------------
	
	public Minesweeper3(char difficulty)
	{
		setTitle("Minesweeper");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createMenu();
		createGamePanel();
		
		// Easy Game
		if (difficulty == 'E') 
		{
			setSize(300, 300);
			width = 9;
			height = 9;
			numberOfMines = 10;
			remainingMines = 10;
			minesRemaining.setText(Integer.toString(numberOfMines));
		} 
		else if (difficulty == 'M') 
		{
			setSize(400, 400);
			width = 16;
			height = 16;
			numberOfMines = 40;
			remainingMines = 40;
			minesRemaining.setText(Integer.toString(numberOfMines));
		}
		else if (difficulty == 'H') 
		{
			setSize(800, 600);
			width = 30;
			height = 16;
			numberOfMines = 99;
			remainingMines = 99;
			minesRemaining.setText(Integer.toString(numberOfMines));
		}
		createContents();
		setVisible(true);
	} // end Minesweeper constructor
		
	//-------------------------------------------------------------------------------

	private void createGamePanel()
	{
		gamePanel = new JPanel();
		
		// Adds a timer to track length of game
		JLabel timerLabel = new JLabel("Time: ");
		gamePanel.add(timerLabel);
		timerBox = new JTextField(4);
		timerBox.setText("0");
		timer = new java.util.Timer();
		gamePanel.add(timerBox);
		
		// Tracks remaining mines on board
		JLabel rMines = new JLabel("Mines Remaining: ");
		gamePanel.add(rMines);
		minesRemaining = new JTextField(3);
		gamePanel.add(minesRemaining);
		
		getContentPane().add(gamePanel, BorderLayout.NORTH);
	}

	public class ScheduleTask extends TimerTask
	{
		public void run()
		{
			currentTime = (long) ((System.nanoTime() - startTime) * (1 * Math.pow(10, -9)));
			timerBox.setText(Long.toString(currentTime));
		}
	}

	private void createMenu()
	{
		
		menuBar = new JMenuBar();
		
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(menu);
		
		startGame = new JMenuItem("New Game", KeyEvent.VK_T);
		startGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		startGame.getAccessibleContext().setAccessibleDescription("Begins a new game of Minesweeper");
		menu.add(startGame);
		
		menu.addSeparator();
		
		submenu = new JMenu("Difficulty");
		submenu.setMnemonic(KeyEvent.VK_S);
		
		easy = new JRadioButtonMenuItem("Easy");
		easy.addMouseListener(menuListen);
		submenu.add(easy);
		medium = new JRadioButtonMenuItem("Medium");
		medium.addMouseListener(menuListen);
		submenu.add(medium);
		hard = new JRadioButtonMenuItem("Hard");
		hard.addMouseListener(menuListen);
		submenu.add(hard);
		menu.add(submenu);
		
		menu.addSeparator();
		quit = new JMenuItem("Quit Minesweeper");
		quit.addMouseListener(menuListen);
		menu.add(quit);
		
		this.setJMenuBar(menuBar);
	}
	
	//-------------------------------------------------------------------------------
	// Create components and add to window.
	
	private void createContents()
	{
		mineBoard = new JPanel();
		mineBoard.setLayout(new GridLayout(height, width));
		
		Square2.setBoardDimensions();
		
		// Build board array and add buttons to grid
		board = new Square2[height][width]; 
		for (int row = 0; row < height; row++) 
		{
			for (int col = 0; col < width; col++) 
			{
				board[row][col] = new Square2(board, row, col);
				board[row][col].addMouseListener(new Listener());
				mineBoard.add(board[row][col]);
			} // end for (col)
		} // end for (row)
		
		// Establishes position of mines
		placeMines(board);
		
		for (int row = 0; row < height; row++) 
		{
			for (int col = 0; col < width; col++) 
			{
				board[row][col].buildNeighbors(board);
				board[row][col].autoSetNeighborMines();
			} // end for (col)
		} // end for (row)
		
		Square2.activateMines();
		getContentPane().add(mineBoard);
	} // end createConents
	
	
	//-------------------------------------------------------------------------------
	// When a button is clicked, perform an appropriate action. 
	
	private class Listener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			Square2 btnPressed = (Square2) e.getSource();
			
			if (!btnPressed.getActivated())
			{
				// If user right clicks, display an "F" to indicate flag has been placed
				if (e.isMetaDown()) 
				{
					if (btnPressed.getText().isEmpty() || btnPressed.getText().equals("X")) 
					{
						btnPressed.setText("?");
					}
					else if (btnPressed.getText().equals("?"))
					{
						btnPressed.setText("F");
						if (btnPressed.getHasMine()) 
						{
							remainingMines--;
							minesRemaining.setText(Integer.toString(remainingMines));
						}
						if (remainingMines == 0) 
						{
							JOptionPane.showMessageDialog(null, "You win!", null, JOptionPane.WARNING_MESSAGE);
							freezeAll(board);
						}
					}
					else 
					{
						btnPressed.setText("");
					}
				} // end if
				
				// If user left clicks 
				else
				{
					// If the button pressed contains a mine, all the mines activate and the game is over
					if(btnPressed.getHasMine())
					{
						Square2.activateMines();
						try 
						{
							// Open an input stream  to the audio file.
							InputStream in = new FileInputStream("bomb.au");
							// Create an AudioStream object from the input stream.
							AudioStream as = new AudioStream(in); 
							AudioPlayer.player.start(as); 
						} catch (Exception f) 
						{
						}
						// Game over message
						JOptionPane.showMessageDialog(null, "Game over!", null, JOptionPane.WARNING_MESSAGE);
						freezeAll(board);
					} // end if
					
					// If the button pressed contains no mines, provide hints to location of nearby mines
					else 
					{
						// Activate button
						btnPressed.setActivated(true);
						
						if (btnPressed.getNeighborMines() > 0) 
						{
							btnPressed.setText(Integer.toString(btnPressed.getNeighborMines()));
						}
						
						// If button pressed has no neighbor mines
						if (btnPressed.getNeighborMines() == 0) 
						{
							btnPressed.activate();
						} // end if
					} // end else
				} // end else
			} // end if
		} // end mousePressed
	} // end Listener
	
	private class menuListen extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if (e.getSource() == quit) 
			{
				System.exit(0);
			}
			if (e.getSource() == easy) 
			{
				game.dispose();
				game = new Minesweeper3('E');
			}
			if (e.getSource() == medium) 
			{
				game.dispose();
				game = new Minesweeper3('M');
			}
			if (e.getSource() == hard) 
			{
				game.dispose();
				game = new Minesweeper3('H');
			}
		}
	}
	
	private class timeTracker extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 1) 
			{
				;
			}
		}
	}
	
	//-------------------------------------------------------------------------------
	// Place mines in randomly assigned sqaures
	
	private void placeMines(Square2[][] board)
	{
		int minesToPlace = numberOfMines;
		
		while (minesToPlace > 0) 
		{
			// Assigns random values for i and j between 0 and WIDTH
			int i = (int) (Math.random() * height);
			int j = (int) (Math.random() * width);
			
			// Uses chosen coordinates to add to mines ArrayList
			if (!board[i][j].getHasMine()) 
			{
				Square2.addToMines(board[i][j]);
				board[i][j].hasMine(true);
				minesToPlace--;
			}
		}
	} // end placeMines
	
	//-------------------------------------------------------------------------------

	public static int getBoardWidth()
	{
		return width;
	}
	
	public static int getBoardHeight()
	{
		return height;
	}
	
	//-------------------------------------------------------------------------------
	// Freezes all squares when game is won
	
	public static void freezeAll(Square2[][] board)
	{
		for (int row = 0; row < height; row++) 
		{
			for (int col = 0; col < width; col++) 
			{
				board[row][col].setActivated(true);
			} // end for (col)
		} // end for (row)
	}
	
	//-------------------------------------------------------------------------------
	// Clears board and places new mines.
	/*
	private void resetBoard()
	{
		for (int row = 0; row < WIDTH; row++) 
		{
			for (int col = 0; col < WIDTH; col++) 
			{
				board[row][col].setText("");
				mines.clear();
				placeMines();
			} // end for col
		} // end for row
	} // end resetBoard
	*/
	
	//-------------------------------------------------------------------------------

	public static void main(String[] args) 
	{
		game = new Minesweeper3('E');
	} // end main
} // end Minesweeper

