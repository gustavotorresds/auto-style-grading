/*
 * File: Breakout.java
 * -------------------
 * Name: Thomas Thach
 * Section Leader: Kaitlyn Lagattuta
 * 
 * This file will implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10; //originally 10

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static double VELOCITY_Y = 5.0; //originally 3.0

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	GRect paddle = null; 
	GOval ball = null; 
	GRect brick; 
	
	private GLabel gameOver = new GLabel("GAME OVER");
	private int deaths = 0; 
	
	private GLabel youWin = new GLabel("YOU WIN!");
	private int brickBreaks = 0; 
	
	private double vx; 
	private double vy = VELOCITY_Y; 
	
	private RandomGenerator rgen = RandomGenerator.getInstance(); 

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		addMouseListeners();
		
		buildBricks();
		paddle = new GRect(0, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx; 
		
		while (true) {
			moveBall();
			pause(DELAY);
		}
	}
	
	//This method constructs a pile of bricks at the top of the screen depending 
	//on the number of rows and columns specified. It also colors the bricks 
	//following a pre-established order, which should hold regardless of how many
	//rows of bricks exist. 
	private void buildBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				double x = BRICK_WIDTH*j + BRICK_SEP*j;
				double y = BRICK_Y_OFFSET + BRICK_HEIGHT*i + BRICK_SEP*i;
				double margin = (getWidth() - (BRICK_WIDTH*NBRICK_COLUMNS + (BRICK_SEP*NBRICK_COLUMNS - BRICK_SEP)))/2;
				brick = new GRect(x + margin, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (i%10 == 0 || i%10 == 1) {
					brick.setColor(Color.RED);
				} else if (i%10 == 2 || i%10 == 3) {
					brick.setColor(Color.ORANGE);
				} else if (i%10 == 4 || i%10 == 5) {
					brick.setColor(Color.YELLOW);
				} else if (i%10 == 6 || i%10 == 7) {
					brick.setColor(Color.GREEN);
				} else if (i%10 == 8 || i%10 == 9) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}
	
	//This mouse event is what controls the paddle. Whenever the user moves
	//the mouse, the paddle should follow the mouse along the x-direction. 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX(); 
		double mouseY = e.getY();
		if (mouseX >= PADDLE_WIDTH/2 && mouseX <= getWidth()-PADDLE_WIDTH/2) {
			paddle.setLocation(mouseX-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET); 
			paddle.setFilled(true);
			add(paddle);
		}
	}
	
	//Whenever the user clicks on the screen, if a ball is not present,
	//a new ball will be added into play. 
	public void mouseClicked(MouseEvent e) { 
		if (ball == null) {
			double xBall = getWidth()/2 - BALL_RADIUS; 
			double yBall = getHeight()/2 - BALL_RADIUS;
			double ballDiameter = BALL_RADIUS * 2; 
			ball = new GOval(xBall, yBall, ballDiameter, ballDiameter); 
			ball.setFilled(true);
			add(ball); 
		}
	}
    //This method animates the ball so that it moves throughout the screen.
	//When the ball collides with anything (or the paddle when the ball is moving in a downward direction),
	//the ball should change the direction of its vertical velocity. 
	private void moveBall() { 
		if (ball != null) {
			ball.move (vx, vy);
			GObject ballCollision = getCollidingObject();
			if (ballCollision != null) {
				if (ballCollision == paddle) {
					if (vy > 0) {
						vy = -vy; 
					} 	
				} else {
					vy = -vy;
				}
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();
				if (ballCollision != paddle) {
					remove(ballCollision);
					brickCounter(); 
				}
			}	
		    //This ensures that the ball reverses its direction when it hits the top of the screen
			//or the sides of the screen. 
			if (ball.getY() <= 0) {
				vy = -vy; 	
			}
			if (ball.getX() >= getWidth() - BALL_RADIUS*2 || ball.getX() <= 0) {
				vx = -vx;
			}
		}
		//This removes the ball from the playing field if the user fails to stop the ball
		//from reaching the bottom of the screen, during which it will be considered 
		//a "death". 
		if (ball != null) {
			if (ball.getY() >= getHeight() - BALL_RADIUS*2) {
				remove(ball); 
				ball = null;
				deathCounter();
			} 
		}
	}
	
	//This method makes sure that all four "corners" of the ball are able 
	//to register collisions with any object.
	private GObject getCollidingObject() {
		GObject ballCollision = getElementAt(ball.getX(), ball.getY()); 
		if (ballCollision != null) {
			return ballCollision;
		} 
		ballCollision = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		if (ballCollision != null) {
			return ballCollision;
		} 
		ballCollision = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		if (ballCollision != null) {
			return ballCollision;
		} 
		ballCollision = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		if (ballCollision != null) {
			return ballCollision;
		} 
		return ballCollision; 
	}
	
	//This method adds 1 to the death counter each time the ball hits 
	//the bottom of the screen and reset into play. If the number of deaths
	//exceeds the allowed number of turns, then the game ends and the player
	//receives no more tries. A "game over" message will appear on the screen
	//to indicate to the player that they have run out of turns. 
	private void deathCounter() {
		deaths++;
		if (deaths >= NTURNS) {
			gameOver.setFont("Courier-48");
			gameOver.setColor(Color.RED);
			double gameOverWidth = gameOver.getWidth();
			add(gameOver, (getWidth() - gameOverWidth)/2, getHeight()/2); 
		}
	}
	
	//This method "counts" each time a brick is removed from the screen.
	//Keeping track of this allows the game to tell the user that they have beaten
	//the game when all the bricks have been removed. 
	private void brickCounter() {
		brickBreaks++;
		if (brickBreaks >= NBRICK_COLUMNS * NBRICK_ROWS) {
			youWin.setFont("Courier-48");
			youWin.setColor(Color.BLUE);
			double gameOverWidth = youWin.getWidth();
			add(youWin, (getWidth() - gameOverWidth)/2, getHeight()/2);
			
		}
	}
}
