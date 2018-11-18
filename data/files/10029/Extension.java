/*
 * File: Extension.java
 * -------------------
 * Name: Ryan Wu	
 * Section Leader: James Mayclin
 * 
 * This program is an extension of Breakout. It implements a few extra features:
 * 1) Adds bounce sound each time the ball hits a wall, brick, or paddle
 * 2) Score and lives left are recorded in the title
 * 3) The ball changes color to the last brick it hit
 * 4) The y-velocity of the ball increases by 5% each time it hits the paddle to 
 * make the game increasingly harder
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
//import javafx.scene.input.MouseEvent;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Extension extends GraphicsProgram {

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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	//Randomly generates a number for x-velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Creates paddle and ball
	private GRect paddle;
	private GOval ball;
	
	//Count of turns and bricks left
	private int turnsLeft;
	private int bricksLeft;
	 
	//Sets the bounds of the canvas 
	private double boundsX;
	private double boundsY;
	
	//Velocity of the ball
	private double ballX;
	private double ballY;

	public void run() {
		addMouseListeners();
		setupGame();
		playGame();
	}
	
	/*
	 * This method sets up the game.
	 * Pre: Empty program
	 * Post: Sets the title, canvas size, has 10 rows of bricks, paddle, and ball.
	 */
	private void setupGame() {
		//Extension: starts with score 0 and lives 3
		setTitle("CS 106A Breakout  -  Score: 0" + "  Lives: 3");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		drawBricks();
		drawPaddle();
		drawBall();
	}
	/*
	 * This method draws a set of 100 bricks for the game
	 * Pre: Title and canvas size are set up
	 * Post: Places set number of rows and columns of bricks. Every two rows of bricks
	 * changes color and is filled.
	 */
	private void drawBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			double startX = i * (BRICK_WIDTH + BRICK_SEP) + (1.8*BRICK_SEP);
			for (int j = 0; j < NBRICK_COLUMNS; j++) {				
				double startY = PADDLE_Y_OFFSET + j*(BRICK_SEP + BRICK_HEIGHT);
				GRect brick = new GRect (startX, startY, BRICK_WIDTH, BRICK_HEIGHT);
				if (j % 10 == 0 || j % 10 == 1) {
					brick.setColor(Color.RED);
				}
				if (j % 10 == 2 || j % 10 == 3) {
					brick.setColor(Color.ORANGE);
				}
				if (j % 10 == 4 || j % 10 == 5) {
					brick.setColor(Color.YELLOW);
				}
				if (j % 10 == 6 || j % 10 == 7) {
					brick.setColor(Color.GREEN);
				}
				if (j % 10 == 8 || j % 10 == 9) {
					brick.setColor(Color.CYAN);
				}
				brick.setFilled(true);
				add (brick);
			}
		}
	}
	/*
	 * Creates the paddle in the game
	 * Pre: Bricks are set up in the world
	 * Post: Black, rectangular paddle is set in the bottom middle of the screen
	 */
	private void drawPaddle() {
		double paddleX = (CANVAS_WIDTH/2) - (PADDLE_WIDTH/2);
		double paddleY = (CANVAS_HEIGHT) - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect (paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
	}
	/*
	 * Creates a mouse event to respond to mouse movement
	 * Pre: Paddle is created but is static
	 * Post: Within the confines of the canvas, mouse movement will move the paddle
	 */
	public void mouseMoved (MouseEvent e) {
		if (e.getX() >= 0 && e.getX() < CANVAS_WIDTH - PADDLE_WIDTH) {
			paddle.setLocation(e.getX(), (CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT)); 
		}
	}
	/*
	 * Draws the ball in the game
	 * Pre: Bricks and paddle are present
	 * Post: Black ball is added to the world
	 */
	private void drawBall() {
		ball = new GOval (CANVAS_WIDTH/2 - BALL_RADIUS, CANVAS_HEIGHT/2, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
	}
	/*
	 * This method plays the game while there are turns and bricks left in the game.
	 * Pre: World is set up and ready to be played
	 * Post: Keeps count of turns and bricks left and plays the game accordingly. It detects
	 * the end of a game and identifies if the player won or lost.
	 */
	private void playGame() {
		turnsLeft = NTURNS;
		bricksLeft = NBRICK_ROWS * NBRICK_COLUMNS;
		while (turnsLeft > 0 && bricksLeft > 0) {
			waitForClick();
			moveBall();
		}
		gameResult();
	}
	/*
	 * Pre: Ball is static in the canvas
	 * Post: Ball has bounds in the X and Y direction to bounce off of, and its
	 * x velocity is randomly generated between the min and max. 
	 */
	private void ballConstants() {
		boundsX = CANVAS_WIDTH - (BALL_RADIUS*2);
		boundsY = CANVAS_HEIGHT - (BALL_RADIUS*2);
		ballX = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) ballX = -ballX;
		ballY = VELOCITY_Y;
	}
	/*
	 * This method initiates the movement of the ball, setting its boundaries and 
	 * bounces, and its reactions to collisions with the bricks and paddle.
	 * Pre: Bounds and velocity of the ball are established
	 * Post: Differentiates a collision with a brick and a paddle and removes
	 * the brick when hit and keeping count of how many bricks left. When the ball
	 * hits the bottom of the screen, a life is lost and the ball is reset. 
	 */
	private void moveBall() {
		ballConstants();
		//Extension: adds bounce sound for the ball
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		while(true) {
			GObject collider = detectCollision();
			if (ball.getX() >= boundsX || ball.getX() <= 0) {
				bounceClip.play();
				ballX = -ballX;
			}
			if (ball.getY() <= 0) {
				bounceClip.play();
				ballY = -ballY;				
			} 
			if (ball.getY() >= boundsY) {
				ball.setLocation(CANVAS_WIDTH/2 - BALL_RADIUS, CANVAS_HEIGHT/2);
				turnsLeft--;
				//Extension: keeps count of lives remaining
				setTitle("CS 106A Breakout  -  Score: " + (100 - bricksLeft) + "  Lives: " + turnsLeft);
				break;
			}
			if (collider != null && collider != paddle) {
				bounceClip.play();
				//Extension: changes ball color to last brick color
				ball.setColor(collider.getColor());
				remove (collider);
				ballY = -ballY;				
				bricksLeft--;
				//Extension: keeps score in title
				setTitle("CS 106A Breakout  -  Score: " + (100 - bricksLeft) + "  Lives: " + turnsLeft);
				if (bricksLeft == 0) break;
			}
			if (collider != null && collider == paddle) {
				if(ballY > 0) {
					bounceClip.play();
					//Extension: increases ball speed by 5% each time it hits the paddle
					ballY = -ballY*1.05;
				}				
			}
			ball.move(ballX, ballY);
			ball.pause(DELAY);  
		}
	}
	/*
	 * This method detects the location of the object the ball collides with. 
	 * Pre: The ball bounces around the world without detecting collisions with the bricks
	 * Post: The ball identifies what brick it hits by detecting its own four corners,
	 * and consequently removes the brick. 
	 */
	private GObject detectCollision() {
		double x = ball.getX();
		double y = ball.getY();
		double d = BALL_RADIUS*2;
		if (getElementAt (x, y) != null) {
			return getElementAt (x, y);
		}
		if (getElementAt (x+d, y) != null) {
			return getElementAt (x+d, y);
		}
		if (getElementAt (x, y+d) != null) {
			return getElementAt (x, y+d);
		}
		if (getElementAt (x+d, y+d) != null) {
			return getElementAt (x+d, y+d);
		}
		return null;
	}
	/*
	 * This method identifies the result of the game based on turns and bricks left
	 * Pre: The game is played and it ends either in a win or loss
	 * Post: The program identifies whether it is a win or loss and prints out 
	 * "winner" or "loser" accordingly. 
	 */
	private void gameResult() {
		remove(ball);
		remove(paddle);
		if (bricksLeft == 0) {
			GLabel winner = new GLabel ("Winner!");
			winner.setLocation(CANVAS_WIDTH/3, CANVAS_HEIGHT/2 - winner.getAscent()/2);
			winner.setColor(Color.GREEN);
			winner.setFont("Helvetica-44");
			add (winner);
		} else if (turnsLeft == 0) {
			GLabel loser = new GLabel ("Loser :'(");
			loser.setLocation(CANVAS_WIDTH/3 , CANVAS_HEIGHT/2 - loser.getAscent()/2);
			loser.setColor(Color.RED);
			loser.setFont("Helvetica-44");
			add(loser);
		}
	}
}
