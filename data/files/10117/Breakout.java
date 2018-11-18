/*
 * File: Breakout.java
 * -------------------
 * Name: Britney Armstrong
 * Section Leader: Maggie Davis 
 * 
 * This file will eventually implement the game of Breakout in which a ball 
 * bounces off of a paddle and removes bricks when they are touched.The game 
 * commences when the player clicks. The game will not function adequately
 * when the vy of the ball is greater than 10, as the width of the  ball will 
 * cause it to leave rows untouched. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

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

	// The ball's minimum and maximum horizontal velocity
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 900.0 / 60.0;
	
	// Number of turns 
	public static int NTURNS = 3;
	
	// Number of rows wanted per color 
	public static int ROWS_PER_COLOR = 2;
	
	// The color that the bricks will start with
	public static Color STARTING_COLOR = Color.RED;
	
	// The probability that the vx variable's sign is switched 
	public static double VX_SIGN_PROBABILITY = 0.5;
	
	
	// Defines instance variables 
	private GRect paddle = null;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GOval ball = null;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		setUpGame();
		addMouseListeners();
		waitForClick();
		createGamePlay();
	}

	/* Method: setUpGame
	 * -----------------
	 * Creates the necessary elements of the game's world. This entails the 
	 * bricks, the paddle, the ball, and the ball's velocity.  
	 * 
	 * Postconditions: Objects will have been assigned to the ball and paddle
	 * instance variables.
	 */
	private void setUpGame() {
		setUpBricks();
		paddle = makePaddle();
		addCenteredPaddle ();
		ball = makeBall();
		addCenteredBall();
		addBallVelocity();	
	}

	/* Method: setUpBricks
	 * -------------------
	 * Creates the block of bricks of varying colors along the top of the 
	 * canvas.
	 */
	private void setUpBricks() {
		Color color = STARTING_COLOR;
		for (int rowNumber = 0; rowNumber < NBRICK_ROWS;) {
			for (int i = 0; i < ROWS_PER_COLOR; i++) {
				//Ensures that an odd number of rows can be made
				if (rowNumber<NBRICK_ROWS) {
					makeRow(rowNumber, color);
					rowNumber++;
				}
			}
			color = switchColor (color);
		}
	}
	
	/* Method: makeRow 
	 * ----------------
	 * Calculates necessary elements for a centered row. 
	 * 
	 * Postcondition: Not only does this method pass its calculations, 
	 * but it also passes the rowNumber and color.
	 */
	private void makeRow (int rowNumber, Color color) { 
		double centerX = getWidth()/2.0;
		double totalBlockWidth = (((NBRICK_COLUMNS-1)*BRICK_SEP)+ (BRICK_WIDTH * NBRICK_COLUMNS));
		double startingX = centerX - (0.5*totalBlockWidth);
		
		// Calculates values by which X and Y values will increase
		double xMovement = BRICK_WIDTH + BRICK_SEP;
		double yMovement = BRICK_HEIGHT + BRICK_SEP;

		createRow(startingX, xMovement, yMovement, color, rowNumber);
	}
	
	/* Method: createRow
	 * -----------------
	 * Creates and adds a row 
	 * 
	 * Precondition: Relevant values and calculations have been passed on from the make 
	 * row method. 
	 */
	private void createRow(double startingX, double xMovement, double yMovement, Color color, double rowNumber) {
		for (int brickNumber= 0; brickNumber < NBRICK_COLUMNS; brickNumber ++) {
			GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add (brick, startingX + (brickNumber*xMovement), BRICK_Y_OFFSET + (rowNumber*yMovement));
		}
	}

	/*
	 * Method: switchColor
	 * -------------------
	 * Switches a color based on the color that is already stored in it. 
	 * 
	 * Postcondition: A color is returned 
	 */
	private Color switchColor (Color color) {
		if (color == Color.RED) {
			return Color.ORANGE; 
		}
	    if (color == Color.ORANGE) {
			return Color.YELLOW;
	    }
		if (color == Color.YELLOW) {
			return Color.GREEN;
		}
		if (color == Color.GREEN) {
			return Color.CYAN;
		}
		return Color.RED;
	}
	
	
	/*
	 * Method: makePaddle
	 * ------------------
	 * Creates the paddle. 
	 * 
	 * Postcondition: It is returned to an instance variable.
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}
	
	/* Method: addCenteredPaddle
	 * -------------------------
	 * Adds the paddle to the bottom center of the screen
	 */
	private void addCenteredPaddle() {
		double paddleX = getWidth()/2.0 - PADDLE_WIDTH/2.0;
		double paddleY = getHeight()-PADDLE_Y_OFFSET;
		add (paddle, paddleX, paddleY);
	}
	
	/*
	 * Method: mouseMoved
	 * -------------------
	 * Is called whenever the mouse is moved. Changes the location of the 
	 * paddle based on the mouse movement. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleY = getHeight()-PADDLE_Y_OFFSET;
		double maxX = getWidth()-0.5*PADDLE_WIDTH;
		double minX = 0.5*PADDLE_WIDTH;
		
		//Ensures that the paddle does not go outside the bounds of the canvas 
		if (mouseX <= maxX && mouseX >= minX) {
			//Makes cursor in the center of the paddle
			paddle.setLocation(mouseX - 0.5*PADDLE_WIDTH, paddleY);
		}
	}
	
	/*
	 * Method: makeBall
	 * ----------------
	 * 
	 * Postcondition: Returns the ball to what will be an instance variable. 
	 */
	private GOval makeBall() {
		GOval ball = new GOval (BALL_RADIUS*2.0, BALL_RADIUS*2.0);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		return ball;
	}
	
	/*
	 * Method: addCenteredBall
	 * -----------------------
	 * Adds the ball to the center of the canvas
	 */
	private void addCenteredBall() {
		double x = getWidth()/2.0-BALL_RADIUS;
		double y = getHeight()/2.0-BALL_RADIUS;
		add(ball, x, y);
	}

	/*
	 * Method: addBallVelocity
	 * -----------------------
	 * Initializes  the velocity of the ball. 
	 * 
	 * Preconditions: vx and vy have been created as instance variables.
	 */
	private void addBallVelocity() {
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(VX_SIGN_PROBABILITY)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
	}
	
	/* Method: createGamePlay
	 * ----------------------
	 * Creates necessary elements of the game play. This entails bouncing off walls 
	 * and the paddle, removing bricks, and keeping track of lives. 
	 * 
	 * Postconditions: Once this loop is ended, either the number of turns or total 
	 * number of bricks will be equal to zero. 
	 */
	private void createGamePlay() {
		int  totalBricks = NBRICK_ROWS*NBRICK_COLUMNS;
		
		while(NTURNS >= 1 && totalBricks > 0) {
			bounceBallOffWalls();
			GObject collider = getCollidingObject(ball.getX(), ball.getY());
			
			// If there is a collision, totalBricks is reduced and the collider
			// is removed. 
			totalBricks = checkForCollision(collider, totalBricks);
			
			ball.move(vx, vy);
			pause(DELAY);
		}
	}
	
	/*
	 * Method: bounceBallOffWalls
	 * --------------------------
	 * Bounces the ball off of the walls by checking for collisions with 
	 * the walls
	 * 
	 * Preconditions: Booleans for if each wall is hit have been created
	 * Postconditions: If a wall is hit, the respective velocity is inverted 
	 * and if the bottom is hit, then a turn is lost and the ball is returned 
	 * to the center. 
	 */
	private void bounceBallOffWalls() {
		if (hitsTop(ball)) {
			vy = -vy;
		} else if (hitsRight(ball) || hitsLeft(ball)) {
			vx = -vx;
		} else if (hitsBottom(ball)) {
			NTURNS--;
			ball.setLocation(getWidth()/2.0 - BALL_RADIUS, getHeight()/2.0 - BALL_RADIUS);
		}		
	}
	
	/*
	 * Method: hitsLeft
	 * ----------------
	 * Tests whether the ball has hit the left side of the canvas
	 * 
	 * Postcondition: Returns true if the left was hit
	 */
	private boolean hitsLeft (GOval ball) {
		return ball.getX() <= 0; 
	}
	
	/*
	 * Method: hitsRight
	 * -----------------
	 * Tests whether the ball has hit the right side of the canvas
	 * 
	 * Postcondition: Returns true if the right was hit
	 */
	private boolean hitsRight(GOval ball) {
		return ball.getX() >= getWidth()-BALL_RADIUS*2;
	}
	
	/*
	 * Method: hitsTop
	 * ---------------
	 * Tests whether the ball has hit the top of the canvas
	 * 
	 * Postcondition: Returns true if the top was hit
	 */
	private boolean hitsTop(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/*
	 * Method: hitsBottom
	 * ------------------
	 * Tests whether the ball has hit the bottom of the canvas
	 * 
	 * Postcondition: Returns true if the bottom was hit
	 */
	private boolean hitsBottom(GOval ball) {
		return ball.getY() >= getHeight()-BALL_RADIUS*2;
	}
	
	/*
	 * Method: getCollidingObject
	 * --------------------------
	 * Test the instance to see whether there is a colliding object 
	 * at four points around the ball. 
	 * 
	 * Postcondition: If an object exists, then it is returned.
	 */
	private GObject getCollidingObject(double x, double y) {
		GObject object = getElementAt(x,y);
		if (object == null) {
			object = getElementAt(x + 2.0*BALL_RADIUS, y);
		}
		if (object == null) {
			object = getElementAt(x, y+ 2.0*BALL_RADIUS);
		}
		if (object == null) {
			object = getElementAt(x+BALL_RADIUS*2.0, y+2.0*BALL_RADIUS);
		}
		return object;
	}
	
	/*
	 * Method: checkForCollision:
	 * -------------------------
	 * Checks to see whether there exists a collision with the paddle or bricks.  
	 * 
	 * Precondition: Collider has been returned with either an object or a null 
	 * value from the method, getCollidingObject
	 * 
	 * PostCondition:If there exists a collision, the colliding object is removed
	 * and the new total number of bricks is returned.
	 */
	private int checkForCollision(GObject collider, int totalBricks) {
		if (collider == paddle) {
			//Accounts for the "sticky" paddle bug
			if (isPositive(vy)) {
				vy = -vy;
			}
		} else if (collider != null) {
			remove(collider);
			totalBricks--;
			vy = -vy;
		}
		return totalBricks;
	}
	
	/* Method: isPositive
	 * -----------------
	 * Determines whether the value of a double is positive or negative
	 * 
	 * Postcondition: Returns true if it is positive. 
	 */
	
	private boolean isPositive (double x) {
		return x + 0 > 0; 
	}
}


