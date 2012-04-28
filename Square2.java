//-------------------------------------------------------------------------------
// Square2.java
// LC
//
// This class represents a square in a Minesweeper board.
//-------------------------------------------------------------------------------

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Square2 extends JButton
{
	//-------------------------------------------------------------------------------
	// BOARD PROPERTIES
	
	// Board size
	private static int width;
	private static int height;

	// ArrayLists
	private static ArrayList<Square2> mines = new ArrayList<Square2>(); // Squares with mines
	
	//-------------------------------------------------------------------------------
	// INDIVIDUAL SQUARE PROPERTIES 
	
	private int row, col; // Stores index values of 2-dimensional board array
	
	private boolean activated; // Tracks when a cell has been activated
	private boolean hasMine; // Indicates a cell contains a mine
	private int neighborMines; // Indicates number of neighbors that contain mines, -1 indicates the square has a mine
	private String display; // Message to be displayed once square is activated
	
	private ArrayList<Square2> neighbors = new ArrayList<Square2>(); // Adjacent squares
	
	//-------------------------------------------------------------------------------
	
	public Square2(Square2[][] board, int r, int c)
	{
		this.activated = false;
		this.hasMine = false;
		
		// Save index values
		this.row = r;
		this.col = c;
	}
	
	//-------------------------------------------------------------------------------
	
	public static void setBoardDimensions()
	{
		width = Minesweeper3.getBoardWidth(); 
		height = Minesweeper3.getBoardHeight();
	}
	
	//-------------------------------------------------------------------------------
	// 

	public void buildNeighbors(Square2[][] board)
	{
		int row = this.row;
		int col = this.col;
		
		// Build array of neighbors
		if (row-1 >= 0) 
		{
			neighbors.add(board[row-1][col]);
			if (col-1 >= 0) 
			{
				neighbors.add(board[row-1][col-1]);
			}
			if (col+1 < width) 
			{
				neighbors.add(board[row-1][col+1]);
			}
		}
		
		if (row+1 < height) 
		{
			neighbors.add(board[row+1][col]);
			if (col-1 >= 0) 
			{
				this.neighbors.add(board[row+1][col-1]);
			}
			if (col+1 < width) 
			{
				this.neighbors.add(board[row+1][col+1]);
			}
		}
		
		if (col-1 >= 0) 
		{
			this.neighbors.add(board[row][col-1]);
		}
		
		if (col+1 < width) 
		{
			this.neighbors.add(board[row][col+1]);
		}
		// Finished building array
	}
	
	public int numOfNeighbors()
	{
		return this.neighbors.size();
	}
	
	//-------------------------------------------------------------------------------
	// Sets display (-1 for mine square, - for 0 mines, and integer for 1+ mines)
	
	public void setDisplay(String d)
	{
		this.display = d;
	}
	
	//-------------------------------------------------------------------------------
	// Sets index values for square
	
	public void setRow(int r)
	{
		this.row = r;
	}
	
	public void setCol(int c)
	{
		this.col = c;
	}
	
	//-------------------------------------------------------------------------------
	
	public void setDisplay(int m)
	{
		// Sets display string and indicates whether there are zero neighbor mines
		if (m == -1) 
		{
			this.display = "X";
			this.hasMine(true);
			mines.add(this);
		}
		else if (m == 0) 
		{
			this.display = "-";
		}
		else 
		{
			this.display = Integer.toString(this.neighborMines);
		}
	}
	
	//-------------------------------------------------------------------------------
	
	public void hasMine(boolean hasMine)
	{
		this.hasMine = hasMine;
	}
	
	//-------------------------------------------------------------------------------
	
	public void autoSetNeighborMines()
	{
		// Tracks number of neighbor mines
		int sum;
		
		sum = 0;
		
		for (int count = 0; count < this.neighbors.size(); count++) 
		{
			if (this.neighbors.get(count).hasMine)
			{
				sum++;
			}
		}
	
		this.neighborMines = sum;
	}
	
	public void setNeighborMines(int m)
	{
		this.neighborMines = m;
	}
	
	//-------------------------------------------------------------------------------
	
	public static void addToMines(Square2 mineSquare)
	{
		mines.add(mineSquare);
		// mineSquare.neighborMines = -1;
	}
	
	//-------------------------------------------------------------------------------
	
	public void setActivated(boolean activated)
	{
		this.activated = activated;
	}
	
	//-------------------------------------------------------------------------------
	
	public boolean getActivated()
	{
		return this.activated;
	}
	
	//-------------------------------------------------------------------------------
	
	public String getDisplay()
	{
		return this.display;
	}
	
	//-------------------------------------------------------------------------------
	
	public int getRow()
	{
		return this.row;
	}
	
	public int getCol()
	{
		return this.col;
	}
	
	//-------------------------------------------------------------------------------
	
	public ArrayList<Square2> getNeighbors()
	{
		return this.neighbors;
	}
	
	//-------------------------------------------------------------------------------
	
	public int getNeighborMines()
	{
		return this.neighborMines;
	}
	
	//-------------------------------------------------------------------------------
	
	public boolean getHasMine()
	{
		return this.hasMine;
	}
	
	//-------------------------------------------------------------------------------
	// Activates all squares with mines
	
	public static void activateMines()
	{
		for (int count = 0; count < mines.size(); count++) 
		{
			mines.get(count).setText("X");
		}
	}
	
	//-------------------------------------------------------------------------------
	// Sets off activation chain when a square with 0 neighboring mines is clicked
	
	public void activate()
	{
		this.activated = true;
		this.setText("-");
		System.out.println(this.row + ", " + this.col + ": " + neighbors.size() + " neighbors.");
		for (int count = 0; count < neighbors.size(); count++) 
		{
			if (!neighbors.get(count).hasMine && !neighbors.get(count).activated) 
			{
				neighbors.get(count).activated = true;
				
				if (neighbors.get(count).neighborMines == 0) 
				{
					neighbors.get(count).setText("-");
					neighbors.get(count).activate();
				} 
				else 
				{
					neighbors.get(count).setText(Integer.toString(neighbors.get(count).neighborMines));
				}
			}
		}
	}
}	




