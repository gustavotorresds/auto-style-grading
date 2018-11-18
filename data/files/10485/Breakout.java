/*
 * File: Breakout.java
 * -------------------
 * Name:Iman Floyd-Carroll
 * Section Leader: Andrew Marshall
 * In this program we create "Breakout" - a game consisting of using a ball and directional bounces to break bricks until there are no bricks remaining. 
 * 
 * This file will eventually implement the game of Breakout.
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
	
	//ball velocity variables
	private double vx;
	private double vy;
	
	//random velocity generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// max velocity variable
	private static final double vyValue = 3.0;
	
	// bricks per row = 10
	private static final int NBRICKS_PER_ROW = 10;
	
	// this variable changes & determines the number of bricks per row during play.
	private int bricksLeft = NBRICKS_PER_ROW * NBRICK_ROWS;
	
	// lives = 3 tries/turns
	private int lives = NTURNS;
	
	// declares the paddle to the entire program
	private GRect paddle;
	
	// this gives the programm the ability to have sounds on contact.
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		makePaddle();
		createBall();
		generateMovement();
		GLabel startMessage = gameMessage("Click to Play");
		add(startMessage, (CANVAS_WIDTH / 2) - (startMessage.getWidth() / 2), CANVAS_HEIGHT - (startMessage.getAscent() / 2) - PADDLE_HEIGHT - PADDLE_Y_OFFSET - 20);
		// this initializes the beginning of the game when the user clicks. 
		waitForClick();
		remove(startMessage);
		playGame();
		endGame();
	}

// this code determines the rules and actions of the game. Either the game is played without consequence if lives are not lost
// or the player loses lives upon missing the paddle or losing their three lives. 
	private void playGame() {
		lives = NTURNS;
		generateMovement();
	// the game begins above with the click and then the upward trajectory of the ball.
	//the while loop below is entered during play on the condition that there are stilkl bricks and lives left in order to play.
		while (bricksLeft > 0 && lives > 0) {
			if(ball.getY() >= getHeight()) {
				lives--;
				remove(ball);
				GLabel livesLeft = gameMessage("Lives Left: " + lives);
				add(livesLeft, (CANVAS_WIDTH / 2) - (livesLeft.getWidth() / 2), CANVAS_HEIGHT - (livesLeft.getAscent() / 2) - PADDLE_HEIGHT - PADDLE_Y_OFFSET - 20);
				createBall();
				vy = vyValue;
				waitForClick();
				remove(livesLeft);
				generateMovement();
			} else {
				moveBall();
				determineCollision();
			}
		// these are the two end game scenarios, the while loop is exited and these become to two feasible end game options. 
		}
		if (bricksLeft == 0) {
			printVictoryMessage();
		} else if (lives == 0) {
			printLoserMessage();
		}
	}



// this block of code includes the conditions necessary to end the game - either the loss of three lives or all the bricks being demolished. 
	private void endGame() {
		if (bricksLeft == 0) {
		printVictoryMessage();
		} else if  (lives == 0) {
		printLoserMessage();
		} 
	}

	private void printLoserMessage() {
		GLabel loserMessage = gameMessage("GAME OVER");
		add(loserMessage, (CANVAS_WIDTH / 2) - (loserMessage.getWidth() / 2), (CANVAS_HEIGHT / 2) - (loserMessage.getAscent() / 2));
	}

	private void printVictoryMessage() {
	GLabel victoryMessage = gameMessage(" YOU'VE WON!");
	add(victoryMessage, CANVAS_WIDTH/2 -(victoryMessage.getWidth()/2), (CANVAS_HEIGHT/2) -(victoryMessage.getAscent()/2));
}
	
	
// this displays the game messages and text for before/after/during the game. 
	private GLabel gameMessage(String message) {
		GLabel label = new GLabel (message);
		label.setFont("Courier");
		return label;
	}


	// If getElementAt returns null for a particular corner, go on and try the next corner.
	//	 If you get through all four corners without finding a collision, then no collision exists.
	// the +/- 1 values within the code add "cushion" to the ball so it doesn't recognize itself as a collider and destroy itself.
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX() + BALL_RADIUS, (ball.getY()) - 1) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS, ball.getY() - 1);
		} else if (getElementAt(ball.getX() + BALL_RADIUS, (ball.getY() + BALL_RADIUS * 2 + 1)) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS * 2 + 1);
		} else if (getElementAt(ball.getX() - 1, (ball.getY() + BALL_RADIUS)) != null) {
			return getElementAt(ball.getX() - 1, ball.getY() + BALL_RADIUS);
		} else if (getElementAt(ball.getX() + BALL_RADIUS*2 + 1, (ball.getY() + BALL_RADIUS)) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS*2 + 1, ball.getY() + BALL_RADIUS);
		} else {
			return null;
		}
	// if all values return null, then no collision has occurred. 

	}
// this gives the program the ability to register if the ball has encountered an object via either of its four points along its periphery/its mass. 
	private void determineCollision() {
		GObject collider = getCollidingObject();
		println(collider);
		if (collider == paddle) {
			generateMovement();
			vy = -Math.abs(vy);
		} else if (collider != null && collider != paddle) { 
			bounceClip.play();
			remove (collider);
			bricksLeft--;
			vx = -vx;
			vy = -vy;
		} else if (ball.getX() < 0 || ball.getX() > getWidth() - BALL_RADIUS*2) {
			vx = -vx;
		} else if (ball.getY() == getHeight() - BALL_RADIUS*2) {
			vy = -vy;
		}

	}

	// this code constructs the paddle and attaches a mouse listener in order to be responsive to the user's mouse.
	private void makePaddle() {
		paddle = new GRect (getWidth()/2 - PADDLE_WIDTH/2, CANVAS_HEIGHT - PADDLE_HEIGHT-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.black);
		add(paddle);
		addMouseListeners();
	}

// this activates the mouse listener, allowing the mouse movement to correlate with that of the paddle across the screen's x-axis.
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		if ((mouseX < getWidth() - PADDLE_WIDTH/2) && (mouseX > PADDLE_WIDTH/2)) {
			paddle.setLocation(mouseX - PADDLE_WIDTH/2, mouseY); 
		}
	}
// this creates the ball that will be used to break bricks.
	private GOval ball;
	public void createBall() {
		double size = BALL_RADIUS*2;
		ball = new GOval (size, size);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball, CANVAS_WIDTH/2 - BALL_RADIUS - 3, CANVAS_HEIGHT - 60);
		return;
	}
// this code below activates the ball's ability to move in variable directions/velocities.
	private void moveBall(){
		ball.move (vx, vy); 
		pause(DELAY);
	}

// this is the move method for the ball, it allows the ball to respond to contact with an object with a randomly generated velocity within the range of given variables.
	private void generateMovement() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = vyValue;
	}
// this sets up the complete 10x10 grid of bricks for playing.
	private void setUpBricks() {
		for(int row = 0; row <  NBRICK_ROWS; row ++) {
			double x = 0; 
			double y = (row*(BRICK_HEIGHT+ BRICK_SEP) + BRICK_SEP);
			Color color = Color.BLACK;
			if(row < 2) {
				color = Color.RED;
			} else if(row < 4) {
				color = Color.ORANGE;
			} else if(row < 6) {
				color = Color.YELLOW;
			} else if(row < 8) {
				color = Color.GREEN;
			} else if(row < 10) {
				color = Color.CYAN;
			}
			makeRow(x,y,color);
		}
	}
// this is the code that allows us to make the initial row of bricks that we then use to build the grid. 
	private void makeRow(double x, double y, Color color) {
		for (int col = 0; col < NBRICK_COLUMNS; col ++) {
			x = (col*(BRICK_WIDTH + BRICK_SEP) + BRICK_SEP);
			makeOneBrick(x,y, color);
		}
	}
// this gives us the ability to make a single brick, which we then construct into rows, and then the 10x10 grid. 
	private void makeOneBrick(double x, double y, Color color) {
		GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT); 
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);	
	}
}



