
/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
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
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 500.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// constants
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private int numberOfBricks = 100;
	private int turnsLeft = NTURNS;
	private int bricksRemoved = 0;

	RandomGenerator rgen = RandomGenerator.getInstance();

	public void run()
	{
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// set up game

		drawGame();
		playGame();
	}

	// This code draws the game.
	private void drawGame()
	{
		drawBricks();
		drawPaddle();
		drawBall();

	}

	private void drawBricks()
	{

		// Draw bricks as a series of rows and columns, in nested loops.

		for (int row = 0; row < NBRICK_ROWS; row++)
		{
			for (int col = 0; col < NBRICK_COLUMNS; col++)
			{

				GRect brick = new GRect((BRICK_WIDTH + BRICK_SEP) * col,
						BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row, BRICK_WIDTH, BRICK_HEIGHT);

				brick.setFilled(true);

				// It's most concise to use a switch and cases for brick colors.
				switch (row)
				{
				case 0:
					brick.setColor(Color.RED);
					break;
				case 1:
					brick.setColor(Color.RED);
					break;
				case 2:
					brick.setColor(Color.ORANGE);
					break;
				case 3:
					brick.setColor(Color.ORANGE);
					break;
				case 4:
					brick.setColor(Color.YELLOW);
					break;
				case 5:
					brick.setColor(Color.YELLOW);
					break;
				case 6:
					brick.setColor(Color.GREEN);
					break;
				case 7:
					brick.setColor(Color.GREEN);
					break;
				case 8:
					brick.setColor(Color.CYAN);
					break;
				case 9:
					brick.setColor(Color.CYAN);
					break;
				default:
					break;
				}

				add(brick);

			}
		}
	}

	private void drawPaddle()
	{
		paddle = new GRect(CANVAS_WIDTH / 2 - PADDLE_WIDTH / 2, CANVAS_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH,
				PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);

	}

	private void drawBall()
	{
		ball = new GOval(CANVAS_WIDTH / 2 - BALL_RADIUS, CANVAS_HEIGHT / 2 - BALL_RADIUS, 2 * BALL_RADIUS,
				2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	// This code allows you to play the game.
	private void playGame()
	{
		ballMotion();
	}

	// This event locater allows you to connect ball and paddle.

	public void mouseMoved(MouseEvent e)
	{
		if (e.getX() >= 0 && e.getX() < CANVAS_WIDTH - PADDLE_WIDTH)
		{
			paddle.setLocation(e.getX(), CANVAS_HEIGHT - PADDLE_Y_OFFSET);

		}
		else if (e.getX() >= CANVAS_WIDTH - PADDLE_WIDTH)
		{

			paddle.setLocation(CANVAS_WIDTH - PADDLE_WIDTH, CANVAS_HEIGHT - PADDLE_Y_OFFSET);
		}
	}

	// This code lets you control the ball velocity with a delay.
	private void ballMotion()
	{
		vx = rgen.nextDouble(1.0, 3.0);

		if (rgen.nextBoolean(0.5))
			;
		vx = -vx;
		vy = 3.0;

		while (true)
		{
			ball.move(vx, vy);
			pause(DELAY);
			wallBounce();
		}
	}

	// This code detects when ball has hit wall and should return the opposite
	// direction

	private void wallBounce()
	{
		if (ball.getX() <= 0)
		{
			vx = -vx;
		}
		else if ((ball.getX() + 2 * BALL_RADIUS) >= CANVAS_WIDTH)
		{
			vx = -vx;
		}
		else if (ball.getY() <= 0)
		{
			vy = -vy;
		}
		else if (ball.getY() + 2 * BALL_RADIUS >= CANVAS_HEIGHT)
		{
			missBall();
		}

		// This collider detects the various types of objects the ball will hit.
		// If it hits a brick, the brick disappears.

		GObject collider = getCollidingObject();
		if (collider == paddle)
		{
			vy = -vy;
		}
		else if (collider != null)
		{
			remove(collider);
			bricksRemoved++;
			vy = -vy;
		}

		if(bricksRemoved >= numberOfBricks)
		{
			youWin();
			pause(5000);
			System.exit(0);
		}

		pause(DELAY);
	}

	// This code keeps track of ball position

	private GObject getCollidingObject()
	{
		if ((getElementAt(ball.getX(), ball.getY())) != null)
		{
			return getElementAt(ball.getX(), ball.getY());
		}
		else if (getElementAt((ball.getX() + BALL_RADIUS * 2), ball.getY()) != null)
		{
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		}
		else if (getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS * 2)) != null)
		{
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		}
		else if (getElementAt((ball.getX() + BALL_RADIUS * 2), (ball.getY() + BALL_RADIUS * 2)) != null)
		{
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}

		else
		{
			return null;
		}

	}

	// If paddle misses ball, end turn.
	private void missBall()
	{
		turnsLeft--;
		ball.move(CANVAS_WIDTH + 100, CANVAS_HEIGHT + 100);
		if (turnsLeft > 0)
		{
			drawBall();
			playGame();
		}
		else
		{
			terminateGame();
			pause(5000);
			System.exit(0);
		}

	}

	//Game Over sign to user.
	private void terminateGame()
	{
		GLabel terminate = new GLabel("Game Over", getWidth() / 2, getHeight() / 2);
		terminate.setFont("Courier-24");
		terminate.move(-terminate.getWidth() / 2, -terminate.getHeight());
		terminate.setColor(Color.RED);
		add(terminate);
	}

	//You win sign to user.
	private void youWin()
	{
		GLabel youWin = new GLabel("You win!! YAY!!!", getWidth() / 2, getHeight() / 2);
		youWin.setFont("Courier-24");
		youWin.move(-youWin.getWidth() / 2, -youWin.getHeight());
		youWin.setColor(Color.RED);
		add(youWin);
	}
}
