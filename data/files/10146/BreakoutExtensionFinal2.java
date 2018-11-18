/*
 * File: Breakout.java
 * -------------------
 * Name: Sam Hiatt
 * Section Leader: Ben Barnett
 * 
 * This is the extension version!
 * 
 * This file will eventually implement the game of Breakout. In Breakout, a paddle is 
 * used to keep a ball bouncing upwards towards rows of colored bricks. When the ball
 * hits a brick, the brick is removed. The player wins the game when all the bricks
 * are gone. They lose the game if they miss the ball and it goes down below the paddle. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtensionFinal2 extends GraphicsProgram {

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

	// Offset of the columns on the end, in pixels
	public static final double BRICK_X_OFFSET = 
			(CANVAS_WIDTH - (NBRICK_COLUMNS*BRICK_WIDTH + (NBRICK_COLUMNS  - 1)*BRICK_SEP))/2;

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




	// Instance Variables

	private GRect paddle = null;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	// The ball's velocity in the x direction
	private double vx;

	//The ball's velocity in the y direction
	private double vy;

	private GOval ball = null;

	private GObject collider = null;

	private GLabel label;

	private int totalBricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;

	private GLabel score = new GLabel (" ");

	private GLabel level = new GLabel (" ");

	private int points;

	private int levelNumber = 1;

	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");




	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		gameSetUp();

		/* This allows the player to play the game for a certain number of turns  */
		for(int turnNumber = 0; turnNumber < NTURNS; turnNumber++) {
			if(totalBricksLeft != 0) {
				playGame();
			} else {
				break;
			}
		}

		/* Message shown if the player loses*/
		if(totalBricksLeft != 0) {
		label = new GLabel ("You lose! Better luck next time");
		label.setFont("Helvetica-28");
		add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2);
		remove(ball);
		}

	}

	/* This sets up the game with rows of colored bricks at the top and 
	 * a paddle at the bottom of the screen*/
	private void gameSetUp() {
		setUpBricks();
		createPaddle();
	}

	/* This creates the rows of colored bricks at the top of the game */
	private void setUpBricks() {
		for(int rowNumber = 0; rowNumber < NBRICK_ROWS; rowNumber ++) {
			Color rowColor = pickRowColor(rowNumber);
			drawRow(rowNumber, rowColor); 
		}
	}

	/* This draws a single row of bricks*/
	private void drawRow(double rowNumber, Color rowColor) {
		for(int brickNumber = 0; brickNumber < NBRICK_COLUMNS; brickNumber++) {

			/* starting x coordinate for each brick in the row */
			double brickX = BRICK_X_OFFSET + brickNumber*(BRICK_WIDTH + BRICK_SEP);
			/* starting y coordinate for each brick in the row */
			double brickY = BRICK_Y_OFFSET + rowNumber*(BRICK_HEIGHT + BRICK_SEP);


			/*draws each brick using the x, y, width, height, and color. (Color
			 * is determined by the method pickRowColor */
			GRect brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(rowColor);
			add(brick);
		}
	}

	/* This takes in the row number, uses it to determine what color the row should be,
	 * and then returns the color that corresponds with that row (0 and 1 return red, 
	 * and 3 return orange, 4 and 5 return yellow, 6 and 7 return green, and 8 and 9 
	 * return cyan.)*/
	private Color pickRowColor(double rowNumber) {

		/* This allows the rows to be colored in the correct color, regardless of the 
		 * number of rows*/
		double numberOfTheColoredRow = rowNumber % 10;
		if(numberOfTheColoredRow ==  0 || numberOfTheColoredRow == 1) {
			return Color.RED;
		} 
		if(numberOfTheColoredRow ==  2 || numberOfTheColoredRow == 3) {
			return Color.ORANGE;
		}
		if(numberOfTheColoredRow ==  4 || numberOfTheColoredRow == 5) {
			return Color.YELLOW;
		}
		if(numberOfTheColoredRow ==  6 || numberOfTheColoredRow == 7) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}

	/* Creates the paddle that will move back and forth*/
	private void createPaddle() {
		paddle = addPaddle();
		addMouseListeners();
	}

	/* Adds the paddle to the center of the screen*/
	private GRect addPaddle() {

		/* Starting xcoordinate for paddle*/
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2;

		/* Starting ycoordinate for paddle*/
		double paddleY = getHeight() - PADDLE_Y_OFFSET;

		/* Draws the paddle*/
		GRect paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}	


	/* Has the paddle follow the movements of the mouse, up until the edges of the
	 * screen */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		if(x > 0 && x < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	/* Starts the actual playing with the game*/
	private void playGame() {
		createBall();
		moveBall();
	}

	/* Creates a ball and adds it to the middle of the screen */
	private GOval createBall() {
		ball = new GOval (getWidth()/2-BALL_RADIUS, getHeight()/2, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
		return ball;
	}



	/* Gets the ball to move, bounce off the walls, and remove bricks. When all bricks are gone
	 * the player has won the game, and if the ball goes past the paddle and hits the bottom
	 * wall of the screen, the player has lost and the game is over. */
	private void moveBall() {

		/* Waits for a click from the user to start the game*/
		waitForClick();

		/* Establishes the ball's velocity in the x direction, defined as 
		 * as a randomly generated number between the given constants for
		 * the minimum and maximum velocity. */
		vx =  rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);

		/* This uses a boolean to create a 50/50 chance of the ball having a positive
		 * or negative starting velocity in the x direction*/
		if (rgen.nextBoolean(0.5)) {
			vx =-vx;	
		}

		vy =  VELOCITY_Y;

		/* This loop animates the ball*/
		while(true) {

			/* Gets the ball moving in a randomly generated direction*/
			ball.move(vx, vy);

			/* Updates the velocity of the ball when it hits either the right
			 * or left wall and changes direction */
			if(hitsLeftWall() == true || hitsRightWall() == true) {
				vx = -vx;
			}

			/* Updates the velocity of the ball when it bounces off the top
			 * wall and changes direction */
			if(hitsTopWall() == true) {
				vy = -vy;
			}

			/* If the ball hits the bottom wall, the ball is removed and the game starts over. 
			 * note: this continues up to the NTURNS number of times the player gets to play the game. */
			if(hitsBottomWall() == true) {
				remove(ball);
				break;
			}

			checkForCollisions();

			keepScore();

			trackLevels();


			/* If there are no bricks left on the screen, this creates a label that 
			 * tells the player they have won the game and removes the ball. */
			if(totalBricksLeft == 0) {
				label = new GLabel ("WOW LOOK AT THAT! YOU WON!!!");
				label.setFont("Helvetica-24");
				label.setColor(Color.MAGENTA);
				add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2);
				remove(ball);
				break;
			}

			/* Pause*/
			pause(DELAY);
		}
	}

	/* Creates a boolean that checks if the x coordinate of the ball has reached
	 * the left wall. It returns this information to the if statement above to 
	 * instruct the ball on whether or not to change velocity in the x direction */
	private boolean hitsLeftWall() {
		return ball.getX() <= 0;
	}

	/* Creates a boolean similar to hitsLeftWall, but checks if the ball has hit 
	 * the right wall */
	private boolean hitsRightWall() {
		return ball.getX() >= getWidth() - BALL_RADIUS * 2;
	}	

	/* Creates a boolean similar to hitsLeftWall, but checks if the ball has hit 
	 * the top wall */
	private boolean hitsTopWall() {
		return ball.getY() <= 0;
	}

	/* Creates a boolean similar to hitsLeftWall, but checks if the ball has hit 
	 * the bottom wall */
	private boolean hitsBottomWall() {
		return ball.getY() >= getHeight() - BALL_RADIUS * 2;
	}

	/* Checks what type of object the ball collides with, and then either removes
	 * or does not remove the object, depending on what object the ball collided with */
	private void checkForCollisions() {
		collider = getCollidingObject();
		handleCollidedObject();
	}

	/* At each of the four corners of the GObject ball, this method checks to see if 
	 * there is any other element at that point, and if there is something there (meaning
	 * that the ball has collided with something) then this method returns that object as
	 * a variable labeled "collider"*/
	private GObject getCollidingObject() {
		double ballUpperLeftCornerX = ball.getX();
		double ballUpperLeftCornerY = ball.getY();

		double ballUpperRightCornerX = ball.getX() + 2 * BALL_RADIUS;
		double ballUpperRightCornerY = ball.getY();

		double ballLowerLeftCornerX = ball.getX();
		double ballLowerLeftCornerY = ball.getY() + 2 * BALL_RADIUS;

		double ballLowerRightCornerX = ball.getX() + 2 * BALL_RADIUS;
		double ballLowerRightCornerY = ball.getY() + 2 * BALL_RADIUS;

		if (getElementAt(ballLowerRightCornerX, ballLowerRightCornerY) != null) {
			return(getElementAt(ballLowerRightCornerX, ballLowerRightCornerY)); 
		} else if (getElementAt(ballLowerLeftCornerX, ballLowerLeftCornerY) != null) {
			return (getElementAt(ballLowerLeftCornerX, ballLowerLeftCornerY));
		} else if (getElementAt(ballUpperLeftCornerX, ballUpperLeftCornerY) != null) {	
			return getElementAt(ballUpperLeftCornerX, ballUpperLeftCornerY);
		} else if (getElementAt(ballUpperRightCornerX, ballUpperRightCornerY) != null) {
			return (getElementAt(ballUpperRightCornerX, ballUpperRightCornerY));
		}
		return null;
	}

	/* Determines if the object that the ball has collided with is the paddle or a brick. 
	 * If it is the paddle, the ball bounces off of it (its velocity in the y direction is changed
	 * from vy to -vy). If it is a brick, the ball bounces off it (again the velocity is changed
	 * from vy to -vy) and the brick is removed. */
	private void handleCollidedObject() {

		/* If the object that the ball collides with is the paddle, the velocity in the y direction
		 * is changed so that it bounces back up. Also adds noise when the ball hits the paddle */
		if(collider == paddle) {
			vy = - Math.abs(vy);
			bounceClip.play();

			/* If the object that the ball hits is not the paddle, not the score or the level tracker,
			 * and not null, it must be a brick. This removes the brick from the screen and also 
			 * changes the y velocity of the ball so that the ball bounces off the brick. */
		} else if (collider != null && collider != score && collider != level) {
			remove (collider);
			vy = -vy;

			/* Keeps track of the total number of bricks left on the screen, and subtracts one brick
			 * every time a brick is removed. */
			totalBricksLeft = totalBricksLeft - 1;
			points ++;
		}
	}

	private void keepScore() {
		score.setLabel("SCORE:  " + points );
		add(score, getWidth()/2 - score.getWidth()/2, getHeight() - score.getAscent()/2);	
	}

	private void trackLevels() {
		if (points == 2) {
			levelNumber = 2;
		} else if (points == 4) {
			levelNumber = 3;
		} else if (points == 32) {
			levelNumber = 4;
		} else if (points == 48) {
			levelNumber = 5;
		} else if (points == 58) {
			levelNumber = 6;
		} else if (points == 68) {
			levelNumber = 7;
		} else if (points == 74) {
			levelNumber = 8;
		} else if (points == 84) {
			levelNumber = 9;
		} else if (points == 90) {
			levelNumber = 10;
		}


		level.setLabel("LEVEL " + levelNumber);
		add(level, level.getAscent(), level.getAscent());
	}
}

