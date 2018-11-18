/*
 * File: Breakout.java
 * -------------------
 * Name: Gaby Goldberg
 * Section Leader: Ben Allen
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	GRect paddle = null;
	GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); {
		if(rgen.nextBoolean(0.5)) 
			vx = -vx; 
	}
	double vy = VELOCITY_Y;
	int currentBricksInGame = NBRICK_ROWS * NBRICK_COLUMNS; // counter to see how many bricks are remaining.
	int turnsRemaining = NTURNS; // counter to see how many turns are remaining, beginning with 3 turns.
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	AudioClip cheerClip = MediaTools.loadAudioClip("cheer3.au");
	AudioClip booClip = MediaTools.loadAudioClip("booing.au");

	public void run() {
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setTitle("CS 106A Breakout"); // Set the window's title bar text
		addBricks();
		paddle = makePaddle();
		addPaddleToCenter(paddle);
		addMouseListeners();
		ball = makeBall();
		ballMove();
	}
	private void addBricks() {
		for(int height = 0; height < NBRICK_ROWS; height++) {
			for(int width = 0; width < NBRICK_COLUMNS; width++) {
				double x = (getWidth() / 2 - ((BRICK_WIDTH + BRICK_SEP) * (NBRICK_COLUMNS / 2) - BRICK_SEP / 2)) + width * (BRICK_WIDTH + BRICK_SEP);
				double y = ((BRICK_Y_OFFSET) + height * (BRICK_HEIGHT + BRICK_SEP)); 
				GRect rect = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT); 
				rect.setFilled(true);
				add(rect);			
				if(height % 10 == 0 || height % 10 == 1) {
					rect.setColor(Color.RED);
				}
				else if(height % 10 == 2 || height % 10 == 3) {
					rect.setColor(Color.ORANGE);
				}
				else if(height % 10 == 4 || height % 10 == 5) {
					rect.setColor(Color.YELLOW);
				}
				else if(height % 10 == 6 || height % 10 == 7) {
					rect.setColor(Color.GREEN);
				}
				else if(height % 10 == 8 || height % 10 == 9) {
					rect.setColor(Color.CYAN);
				}
			}
		}
	}
	public void mouseMoved(MouseEvent e) {
		if(e.getX() < getWidth() - PADDLE_WIDTH) { // Accounts for the paddle going off the edge of the screen:
			double x = e.getX(); // The paddle moves horizontally with the mouse.
			double y = ((getHeight() - PADDLE_Y_OFFSET)); // The y-value of the paddle never changes; it only moves horizontally.
			paddle.setLocation(x, y);
		}
	}
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	private void addPaddleToCenter(GRect paddle) { // Adds the paddle to the screen:
		double x = (getWidth() / 2 - PADDLE_WIDTH / 2); 
		double y = ((getHeight() - PADDLE_Y_OFFSET));
		add(paddle, x, y);
	}
	public GOval makeBall() { // Makes the ball and adds it to the center of the screen:
		double size = BALL_RADIUS * 2;
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		GOval ball = new GOval(size, size);
		ball.setFilled(true);
		add(ball, x, y);
		return ball;
	}	
	private void ballMove() { // This is the "play game" method and accounts for different possibilities during in-game play.
		while(turnsRemaining > 0 && currentBricksInGame > 0) { // The while loop is entered as long as there is at least 1 turn left AND bricks still remain.
			waitForClick();
			while(!hitBottomWall(ball)) { // This while loop is entered as long as the ball does NOT hit the bottom wall.
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx; // Reverse the ball's horizontal velocity if it hits the left or right walls.
				}
				if(hitTopWall(ball)) {
					vy = -vy; // Reverse the ball's vertical velocity if it hits the top wall.
				}
				GObject collider = getCollidingObject();
				if(collider == paddle) { // If the ball hits the top of the paddle, reverse the vertical velocity so it bounces back up:
					if(getElementAt(ball.getX(), ball.getY()) != collider && getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != collider) {
						vy = -vy;
						bounceClip.play(); // Plays a sound if the ball hits the paddle.
					}
	/* There are three options for the collider. It can hit the paddle (shown above), it can hit a brick (which is hard to test), or it can hit nothing at all, written as "null." The method below accounts for the collider hitting bricks: because it's an "else if" statement, we know it didn't hit the paddle, and since the statement is checking if the collider DIDN'T hit "null," then if it enters the "if" statement, we know the collider hit a brick. */
				} else if (collider != null) {
					vy = -vy; // Reverse the vertical velocity so the ball keeps bouncing.
					bounceClip.play(); // Plays a sound if the ball hits a brick.
					remove(collider); // If the ball hits a brick, remove the brick.
					currentBricksInGame--; // Account for there now being one fewer brick in the counter.

	/* If the last brick in the game is hit, remove the ball and add the following label, signaling the end of the game: */
					if(currentBricksInGame == 0) {
						remove(ball);
						cheerClip.play(); // Plays cheering sounds if the game is won.
						GLabel youWinLabel = new GLabel("You win! Nice work :)"); 
						double x = (getWidth() / 2 - youWinLabel.getWidth() / 2);
						double y = (getHeight() / 2 - youWinLabel.getHeight() / 2);		
						add(youWinLabel, x, y);
						break;
					}
				} 
				ball.move(vx, vy); // Update visualization
				pause(DELAY); // Pause
			}
	/* If the initial while loop ends, meaning the ball hit the bottom wall, remove the ball and begin a new turn with a new ball, as long as there are turns remaining. If no turns remain, skip to the next "if" statement. */
			remove(ball);
			turnsRemaining--;
			if(turnsRemaining > 0 && currentBricksInGame > 0) {
				ball = makeBall();
			} 
		}
	/* If no turns are remaining, add the following label, signaling the end of the game: */ 
		if(turnsRemaining == 0) {
			booClip.play(); // Plays a booing sound if the game is lost.
			GLabel youLoseLabel = new GLabel("You lose! Nice try :)"); 
			double x = (getWidth() / 2 - youLoseLabel.getWidth() / 2);
			double y = (getHeight() / 2 - youLoseLabel.getHeight() / 2);		
			add(youLoseLabel, x, y);
		}
	}
	/* This method checks to see if the ball has collided with an object on screen. As explained above, the collider can hit 1) the paddle, 2) a brick, or 3) nothing at all, defined here as "null." */ 
	private GObject getCollidingObject() {
		GObject collider = null;
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			collider = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
			collider = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
			collider = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		} else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
			collider = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}
		return collider;
	}
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
}