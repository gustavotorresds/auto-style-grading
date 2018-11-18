/*
 * File: Breakout.java
 * -------------------
 * Name: Arlene Aleman
 * Section Leader: James Mayclin 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram 
{

	// Dimensions of the canvas, in pixels
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; 
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (m/s)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//necessary instance variables
	GRect paddle = null;
	GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int brickCount = 0;
	private int turn;
	
	/* The Breakout class extends GraphicsProgram and has a run method that loops through two commands as long as you still have turns
	 * in the game. The setup method will add 10 x 10 colored bricks at the top of the screen, a paddle, and a ball. The game begins when
	 * the mouse is clicked, giving that ball an initial random motion. The paddle moves horizontally with the mouse. The object of the game
	 * is to clear all the bricks by having the ball hit them. If the ball touches the bottom wall 3 times, it is game over. 
	 */
	
	public void run() 
	{
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		paddle = makePaddle();
		ball = makeBall();
		
		addMouseListeners();
		turn = NTURNS;
		while(turn != 0)
		{
			setup();
			playGame();
			if(brickCount > 0 && turn == 0)
			{
				gameOver();
			}
			if(brickCount == 0)
			{
				gameWon();
			}
		}
	}
	
	/* Makes the ball bounce of the walls and the paddle upon collision but removes a brick if ball collides with it while subtracting one 
	 * from the total brick count. If all the bricks are cleared, the game is won.
	 */
	private void checkForCollisions() 
	{	
		if (hitTopWall(ball) || hitBottomWall(ball))
		{
			vy = - vy;
		}
		
		if (hitRightWall(ball) || hitLeftWall(ball))
		{
			vx = -vx;
		}
				
		GObject collider = getCollidingObject();
		
		if (collider == paddle)
		{
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			vy = -vy;
			bounceClip.play();
		}
		
		else if (collider != null)
		{
			vy = - vy;
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			bounceClip.play();
			remove(collider);
			brickCount--;
			if (brickCount == 0)
			{
				gameWon();
			}
		}
	}
	
	/* Checks the four "corners" of the ball to see if it has collided with anything. Returns location of what has been collided with and
	 * null if no collision occurred.
	 */
	private GObject getCollidingObject() 
	{
		if (getElementAt(ball.getX(), ball.getY()) != null)
		{
			return getElementAt(ball.getX(), ball.getY());
		}
		else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null)
		{
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		}
		else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null)
		{
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		}
		else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null)
		{
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}
		else 
		{
			return null;
		}
	}

	private boolean hitTopWall(GOval ball2)
	{
		return ball.getY() <= 0;
	}
	private boolean hitRightWall(GOval ball2)
	{
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	
	private boolean hitLeftWall(GOval ball2)
	{
		return ball.getX() <= 0;
	}
	
	private boolean hitBottomWall(GOval ball2)
	{
		if(turn == 0)
		{
			gameOver();
		}
		return ball.getY() > getHeight() - ball.getHeight();
	}
	
	// Places what's necessary to start playing the game. Restarts every time the ball touches the bottom wall. 
	private void setup()
	{
		placeBricks();
		placePaddle();
		placeBall();
	}
	
	// Puts down the bricks row by row and column by column. Every third row changes color. 
	private void placeBricks() 
	{
		for (int row = 0; row < NBRICK_ROWS; row++)
		{	
			for (int col = 0; col < NBRICK_COLUMNS; col++)
			{
				double rowWidth = BRICK_WIDTH * NBRICK_COLUMNS + (BRICK_SEP * (NBRICK_COLUMNS - 1));
				double rowX = getWidth()/2 - rowWidth/2 + (BRICK_WIDTH * col) + (BRICK_SEP * col);
				double rowY = (BRICK_Y_OFFSET + BRICK_HEIGHT * row + BRICK_SEP * row);
				
				GRect brick = new GRect(rowX, rowY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (row < 2)
				{
					brick.setColor(Color.RED);
				}
				if (row > 1 && row < 4)
				{
					brick.setColor(Color.ORANGE);
				}
				if (row > 3 && row < 6)
				{
					brick.setColor(Color.YELLOW);
				}
				if (row > 5 && row < 8)
				{
					brick.setColor(Color.GREEN);
				}
				if (row > 7 && row < 10)
				{
					brick.setColor(Color.CYAN);
				}
				add(brick);
				brickCount++;
			}
		}	
	}

	private GRect makePaddle() 
	{
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	
	private void placePaddle()
	{
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, paddleX, paddleY);
	}
		
	private GOval makeBall()
	{
		GOval ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		return ball;
	}

	private void placeBall() 
	{
		double ballX = getWidth()/2 - BALL_RADIUS * 2;
		double ballY = getHeight()/2 - BALL_RADIUS * 2;
		add(ball, ballX, ballY);
	}

	// Randomly sets the ball's initial velocity and checks if the ball hits anything. If the bricks are all cleared, it stops. 
	private void playGame() 
	{
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		GLabel ready = new GLabel("READY? Click to start");
		ready.setColor(Color.GRAY);
		add(ready, getWidth()/2 - ready.getWidth()/2, getHeight()/2 - ready.getAscent() - ball.getY()/2); 
		waitForClick();
		remove(ready);
		while(true)
		{
			ball.move(vx, vy);
			pause(DELAY);
			checkForCollisions();
			if (brickCount == 0)
			{
				break;
			}
			else if (hitBottomWall(ball))
			{
				remove(ball);
				turn--;
				break;
			}
		}
	}
	
	// Shows user a victory message.
	private void gameWon()
	{
		GLabel w = new GLabel("WINNER");
		w.setColor(Color.CYAN);
		add(w, getWidth()/2 - w.getWidth()/2, getHeight()/2 + w.getAscent()/2);
	}
	
	private void gameOver()
	{
		GLabel l = new GLabel("GAME OVER");
		l.setColor(Color.RED);
		add(l, getWidth()/2 - l.getWidth()/2, getHeight()/2 + l.getAscent()/2);
	}
	
	// Shows user a message saying the game is over.
	public void mouseMoved(MouseEvent e)
	{
		int mouseX = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle.setLocation(mouseX - PADDLE_WIDTH/2, y);
		if(mouseX > getWidth() - PADDLE_WIDTH/2)
		{
			paddle.setLocation(getWidth() - PADDLE_WIDTH, y);
		}
		if(mouseX < PADDLE_WIDTH/2)
		{
			paddle.setLocation(0, y);
		}
	}
}
