/*
 * File: Breakout.java
 * -------------------
 * Name: Ian Arko
 * Section Leader: Vineet 
 * 
 * This is the game Brick Breaker. Click to play. You have three lives. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
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

	// Distance between each of the base coordinates of bricks.
	public static final double ANCHOR_DISTANCE_X = BRICK_WIDTH + BRICK_SEP;
	public static final double ANCHOR_DISTANCE_Y= BRICK_HEIGHT + BRICK_SEP;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius and diameter of the ball in pixels
	public static final double BALL_RADIUS = 10;
	public static final double BALL_DIAMETER = BALL_RADIUS * 2;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 5;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 100;

	// Number of turns 
	public static final int NTURNS = 3;

	//The track that the paddle runs in in the Y
	public static final double PADDLE_Y = CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

	//allows the ball to move up and down by multiplying by -1.
	double vY = VELOCITY_Y;

	//tracks the mouse's x location so that the paddle can be moved appropriately. 
	double mouseX = 0;

	//Initiates the two main moving parts so they can be accessed by the game playing method and the mouse movement.
	GRect paddle = null;
	GOval ball = null;
	GLabel starter = null;

	//establishes the play conditions so they can be changed. Decides whether the game is over or not.
	int brickHitNum = 0;
	int numLives = 3;

	//randomizes the x coordinates to make the game more FUN!
	private RandomGenerator rg = new RandomGenerator();
	double vX = rg.nextDouble(1.0, 3.0);

	//used as a control to reset and end the game as you play through.
	boolean ballInPlay;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	//sets up where the ball will enter into play from.
	private static final double BALL_START_X = (CANVAS_WIDTH - BALL_DIAMETER )/ 2;
	private static final double BALL_START_Y= ((BRICK_HEIGHT + BRICK_SEP) * 10) + (BRICK_Y_OFFSET * 2);

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// SetsUpGame
		setUpGame();

		//plays game so long as there are 3 lives there are still bricks on the screen.
		while(numLives != 0 &&  brickHitNum != 100) {
			playGame();
			if(numLives == 0) {
				addYouLose();
			}else if(brickHitNum == 100) {
				addYouWin();
			}
		}
	}

	//this sets up the game by putting the bricks on the screen and by making all of the main play elements including the ball and the paddle.
	private void setUpGame() {
		setUpBricks();
		makePaddle();
		makeBall();
		addStarterLabel();
		addMouseListeners();
	}

	//plays the game.
	private void playGame() {
		waitForClick();
		remove(starter);
		add(ball);
		ballInPlay = true;
		ball.setLocation(BALL_START_X, BALL_START_Y );
		add(ball);
		while(ballInPlay) {
			if(brickHitNum >= 100) {
				ballInPlay = false;
			} else {
				animateBall();
				removeBricksIfHit();
			}
		}
		remove(ball);
	}

	// adds statement when the user wins. 
	private void addYouWin() {
		removeAll();
		GLabel youWin = new GLabel(" YOU WIN!");
		youWin.setFont(SCREEN_FONT);
		add(youWin, (getWidth() - youWin.getWidth()) / 2, (getHeight() - youWin.getAscent()) / 2);
	}

	//adds statement when the user loses
	private void addYouLose() {
		removeAll();
		GLabel youLose = new GLabel("I'm sorry. You just lost.");
		youLose.setFont(SCREEN_FONT);
		add(youLose, (getWidth() - youLose.getWidth())/2, (getHeight() - youLose.getAscent())/2);
	}

	//moves the ball based on the velocity, bounces off of any walls, and has a delay that allows the user to see the movement.
	private void animateBall() {
		ball.move(vX , vY);
		wallBouncer();
		pause(DELAY);
	}

	//This method senses what object is being hit and removes it if it is a brick. If it is the paddle it bounces.
	private void removeBricksIfHit() {

		//checks the Top Right (TR), Bottom Right (BR), and Bottom Left Corners (BL).
		GObject senseObjectTL =  getElementAt(ball.getX(), ball.getY());	
		GObject senseObjectTR = getElementAt(ball.getRightX(), ball.getY());
		GObject senseObjectBR= getElementAt(ball.getRightX(), ball.getBottomY());
		GObject senseObjectBL = getElementAt(ball.getX(), ball.getBottomY());

		if(senseObjectBR == paddle || senseObjectBL == paddle ) {
			vY = vY * -1;

		//THIS else if keeps the ball from getting stuck inside of the paddle when the paddle moves into the ball
		//Precondition: Paddle is stuck inside of the ball.
		//Post: Ball is moved up past the paddle.
		} else if(senseObjectTR == paddle || senseObjectTL == paddle) {
			ball.setLocation(ball.getX(), ball.getY() + BALL_DIAMETER);
			vY = vY * -1;
		}else if(senseObjectTL != null || senseObjectTR != null || senseObjectBR != null || senseObjectBL != null) {
			if(senseObjectTL != null && senseObjectTL != paddle) {

				remove(senseObjectTL);

			} else if (senseObjectTR != null && senseObjectTR != paddle) {

				remove(senseObjectTR);

			} else if (senseObjectBR != null && senseObjectBR != paddle) {

				remove(senseObjectBR);

			} else if (senseObjectBL != null && senseObjectBL != paddle) {

				remove(senseObjectBL);
			} 
			vY = vY * -1;
			brickHitNum++;
		} 
	}

	//This method makes the ball bounce off of any walls that it hits.
	private void wallBouncer() {
		if(ball.getY() <= 0){
			vY = vY * -1;
		}else if(ball.getX() >= CANVAS_WIDTH -BALL_DIAMETER ) {
			vX = vX * -1;
		}else if (ball.getX() <= 0) {
			vX= vX * -1;
		}else if(ball.getY() >= CANVAS_HEIGHT - BALL_DIAMETER) {
			numLives--;
			ballInPlay = false;
		}
	}

	//setUpBricks from the left to right, bottom to the top.
	private void setUpBricks() {
		int colorTracker = 0;
		for(int columnCounter = NBRICK_ROWS; columnCounter != 0; columnCounter--) {
			for(int rowCounter = NBRICK_COLUMNS; rowCounter !=0; rowCounter--) {
				GRect brick = new GRect (brickDistanceX(rowCounter), brickDistanceY(columnCounter), BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(colorChooser(colorTracker));
				add(brick);
			}
			colorTracker++;
		}
	}

	//calculates where to put the Y of the brick using the column counters. 
	private double brickDistanceY(int columnCounter) {
		return BRICK_Y_OFFSET + ANCHOR_DISTANCE_Y * columnCounter;
	}

	//calculates where to put the X based on the row counters.
	private double brickDistanceX(int rowCounter) {
		return CANVAS_WIDTH - (rowCounter * ANCHOR_DISTANCE_X) - BRICK_SEP;
	}

	//chooses the color for each row of bricks. Counts from the bottom up.
	private Color colorChooser(int colorTracker) {
		if(colorTracker < 2) {
			return Color.CYAN;
		}else if (colorTracker < 4) {
			return Color.GREEN;
		}else if(colorTracker < 6) {
			return Color.YELLOW;
		}else if(colorTracker < 8) {
			return Color.ORANGE;
		}
		return Color.RED;
	}

	public void addStarterLabel() {
		starter = new GLabel("Click to start the game.");
		starter.setFont(SCREEN_FONT);
		add(starter, (CANVAS_WIDTH - starter.getWidth())/ 2, (CANVAS_HEIGHT - starter.getAscent()) / 2 );
	}

	//Adds paddle to the center of the canvas.
	public void makePaddle() {
		paddle = new GRect((CANVAS_WIDTH - PADDLE_WIDTH) / 2, PADDLE_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	public void makeBall(){
		double ballStartX = (CANVAS_WIDTH - BALL_DIAMETER )/ 2;
		double ballStartY = ((BRICK_HEIGHT + BRICK_SEP) * 10) + (BRICK_Y_OFFSET * 2);
		ball = new GOval(ballStartX, ballStartY, BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
	}

	//whenever the mouse is moved, the paddle location will move to a point that is centered on the mosue. 
	public void mouseMoved(MouseEvent event) {
		mouseX = event.getX();
		if(mouseX < CANVAS_WIDTH - PADDLE_WIDTH) {
			paddle.setLocation(mouseX - (PADDLE_WIDTH /2), PADDLE_Y);
		}else{
			paddle.setLocation(CANVAS_WIDTH - PADDLE_WIDTH, PADDLE_Y );
		}
	}
}

