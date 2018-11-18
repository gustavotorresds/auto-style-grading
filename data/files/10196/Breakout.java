/*
 * File: Breakout.java
 * -------------------
 * Name: Richard Correro
 * Section Leader: Justin Xu
 * This file implements the game of Breakout. This version
 * includes a ball velocity multiplier to make game-play more interesting.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_Extended extends GraphicsProgram {

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
	public static final double BRICK_SEP = 5;

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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Multiplier for speed of ball. Increases speed every time a brick is hit.
	public static final double BALL_SPEED_MULTIPLIER = 1.01;
	
	// Create instance variable for random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Instance variable for ball x-velocity
	double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
	
	// Instance variable for ball y-velocity
	double vy = VELOCITY_Y;
	
	// Instance variable for paddle
	GRect paddle = null;
	
	// Instance variable for ball
	GOval ball = null;
	
	// Instance variable for the number of lives a player has
	int numberLives = 3;
	
	// Boolean sentinel value to determine whether the game has been won.
	// Initialize to false since game has not started.
	boolean isGameWon = false;
	

	public void run() {
		// Begin monitoring for mouse input
		addMouseListeners();
		
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//Setup world
		setup();
		startGame();
	}
	
	// Stores control flow for the game. Each game consists of a number of
	// rounds determined by the numberLives
	private void startGame()	{
		// Make sure the player still has lives
		while(numberLives > 0)	{
			if(numberLives == 1)	{
				// Change tense to match number one
				GLabel livesLabel = createLabel(numberLives + " Life Left", "Helvetica-36", 
						Color.RED);	
				pause(1000);	// For emphasis
				remove(livesLabel);				
			}	else		{
				GLabel livesLabel = createLabel(numberLives + " Lives Left", "Helvetica-36", 
						Color.BLUE);
				pause(1000);	
				remove(livesLabel);				
			}

			pause(500);
			waitForClick();
			// Start method for each round. Contains the animation loop.
			startRound();
			// Check whether the game has been won. If so, end execution.
			if(isGameWon)	{
				break;
			}
			// Check for lose condition.
			if(numberLives == 0)	{
				createLabel("YOU LOSE!", "Helvetica-48", Color.RED);
			} else {
				// Generate new ball for next round
				ball = makeBall();
				add(ball);
			}
		}
	}

	// Add all of the necessary elements to begin the game.
	private void setup() {
		// Add bricks
		placeBricks();
		// Create and add paddle
		paddle = makePaddle();
		add(paddle);
		// Create and add ball
		ball = makeBall();
		add(ball);
	}
	
	// Add the rows and columns of bricks to the window
	private void placeBricks() {
		// Find y coordinate of bottom left brick
		double bottomLeftY = BRICK_Y_OFFSET + (NBRICK_ROWS * BRICK_HEIGHT) + 
				((NBRICK_ROWS - 1) * BRICK_SEP);
		// Find x coordinate of bottom left brick
		double bottomLeftX = (getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) +
				((NBRICK_COLUMNS - 1) * BRICK_SEP))) / 2;

		// Add bricks to the window
		for(int i = 0; i < NBRICK_ROWS; i++) {
			// Find y coordinate for each row
			double rowY = bottomLeftY - (i * (BRICK_HEIGHT + BRICK_SEP));
			// Find brick color using a method
			Color color = colorFinder(i);
			
			// Add each row of bricks
			for(int j = 0; j < NBRICK_COLUMNS; j++) {
				// Find x coordinate for each column
				double colX = bottomLeftX + (j * (BRICK_WIDTH + BRICK_SEP));
				GRect brick = new GRect(colX, rowY, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				// Set brick color
				brick.setFilled(true);
				brick.setFillColor(color);
				
			}
		}
	}
	// Find color for each row of bricks. This method works for an
	// arbitrary number of rows.
	private Color colorFinder(int i) {
		// Calculate row color using modulo arithmetic
		if(i >= 10)	{
			i %= 10;
		}
		// Color bricks based on index of the brick's row.
		switch(i) {
		case 0:
		case 1: 
			Color cyan = (Color.CYAN);
			return cyan;
		case 2:
		case 3: 
			Color green = (Color.GREEN);
			return green;
		case 4:
		case 5: 
			Color yellow = (Color.YELLOW);
			return yellow;
		case 6:
		case 7:
			Color orange = (Color.ORANGE);
			return orange;
		case 8:
		case 9:
			Color red = (Color.RED);
			return red;
		default:
			Color black = (Color.BLACK);
			return black;
		}
	}
	
	// Create paddle and return to setup
	private GRect makePaddle()	{
		// Find paddle's x and y coordinates
		double paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		
		GRect paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		// Fill in the paddle
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}
	
	// Create the ball and return to setup
	private GOval makeBall()	{
		// Find the ball's starting x and y coordinates
		double ballX = (getWidth() - BALL_RADIUS) / 2;
		double ballY = (getHeight() - BALL_RADIUS) / 2;
		GOval ball = new GOval(ballX, ballY, BALL_RADIUS, BALL_RADIUS);
		// Fill in the ball
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		return ball;
	}
	
	// Called whenever the mouse moves in the x-axis - moves paddle
	public void mouseMoved(MouseEvent e)	{
		// Find paddle's y coordinate
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		// Find paddle's x coordinate. Determined by position of mouse.
		double mouseX = e.getX() - (PADDLE_WIDTH / 2);
		double leftSide = mouseX;	// X-coordinate of left side of paddle
		double rightSide = e.getX() + (PADDLE_WIDTH / 2);	// X-coordinate of right side 
		if(leftSide <= 0)	{
			paddle.setLocation(0, paddleY);
		} else if(rightSide >= getWidth())	{
			paddle.setLocation(getWidth() - PADDLE_WIDTH, paddleY);
		} else	{
		paddle.setLocation(mouseX, paddleY);
		}
	}
	
	// Method for the game-play in each round.
	private void startRound()	{
		// Make random selection of negative or positive vx value
		if(rgen.nextBoolean(0.5))	{
			vx = -vx;
		}
		
		// Create local variable for the number of bricks in the game.
		// Takes the value of the instance variable to start each game.
		int numBricks = getElementCount() - 2;


		// Create label displaying number of bricks in top right corner
		GLabel numberBricksLeft = new GLabel(numBricks + " left to destroy",
					BRICK_SEP, BRICK_Y_OFFSET / 2);	// Placed in middle of y offset
		numberBricksLeft.setFont("Helvetica-24");
		numberBricksLeft.setColor(Color.DARK_GRAY);
		add(numberBricksLeft);
		
		//ANIMATION LOOP
		while(true)	{
			
			// First check for collisions with walls
			if(leftWall() || rightWall())	{
				vx = -vx;	// Reverse x-direction for collisions with side walls
			} else if(topWall())	{
				vy = -vy;	// Reverse y-direction for collisions with top wall
			} else if(bottomWall())	{
				ball.setColor(Color.RED); // Make ball red to show that the round is over
				GLabel loseLabel = createLabel("YOU LOSE A LIFE!", "Helvetica-36", Color.RED);
				pause(1500); // Give the user a second to calm down
				remove(loseLabel);
				remove(numberBricksLeft); // Clear label to ready for next round
				remove(ball);
				numberLives--; // Decrement numberLives since the player lost
				break;
			}
			
			// Find corners surrounding the ball
			GPoint upperLeft = new GPoint(ball.getX(), ball.getY());
			GPoint upperRight = new GPoint(ball.getX() + (BALL_RADIUS), ball.getY());
			GPoint lowerLeft = new GPoint(ball.getX(), ball.getY() + (BALL_RADIUS));
			GPoint lowerRight = new GPoint(ball.getX() + (BALL_RADIUS),
					ball.getY() + (BALL_RADIUS));
			
			// Look for any colliding objects;
			GObject collidingObj = getCollidingObject(upperLeft, upperRight, 
					lowerLeft, lowerRight);
			
			// Determine what collidingObj is
			if(collidingObj == paddle)	{
				// Use constant value to prevent sticking
				vy = -VELOCITY_Y; // Reverse vertical direction
				// Make sure that the 
			} else if(collidingObj != null && collidingObj != numberBricksLeft)	{
				// Check whether the brick is above or below the ball
				// to determine which direction to bounce
				boolean above = checkBrickLocation(collidingObj);
				if(above)	{
					vy = -VELOCITY_Y;
				} else {
					vy = VELOCITY_Y;
				}
				numBricks--;	 // Decrement number of bricks
				remove(collidingObj);
				// Update number of bricks label
				numberBricksLeft.setLabel(numBricks + " left to destroy");
				
				// Multiply ball velocity in both axis by the constant every
				// time a brick is hit to speed up game-play
				vy *= BALL_SPEED_MULTIPLIER;
				vx *= BALL_SPEED_MULTIPLIER;
			}
			
			// Check to see if the player has won by destroying all of the bricks
			if(numBricks ==0)	{
				winCondition();	// Method containing all of the end-game procedures
				break;
			}
			
			
			// Move the ball 
			ball.move(vx, vy);
			
			// Pause according to static value
			pause(DELAY);
		}
	}
	
	// Returns true if ball hits left wall
	private boolean leftWall()	{
		double ballX = ball.getX();
		// X coordinate == 0 implies that ball has hit left wall
		if(ballX <= 0)	{
			return true;
		} else	{
			return false;
		}
	}
	
	// Returns true if ball hits right wall
	private boolean rightWall()	{
		double ballX = ball.getX() + (BALL_RADIUS);
		// X coordinate == the width of the screen implies the ball
		// has hit the right wall.
		if(ballX >= getWidth())	{
			return true;
		} else	{
			return false;
		}
	}
	
	// Returns true if ball hits top wall
	private boolean topWall()	{
		double ballY = ball.getY();
		// Y coordinate == 0 implies ball has hit top wall
		if(ballY <= 0)	{
			return true;
		} else	{
			return false;
		}
	}
	
	// Returns true if ball hits bottom wall
	private boolean bottomWall()	{
		double ballY = ball.getY() + (BALL_RADIUS);
		// Y coordinate == the height of the screen implies ball
		// has hit bottom of window
		if(ballY >= getHeight())	{
			return true;
		} else	{
			return false;
		}
	}
	
	// Checks for objects at each of the corners of the ball
	// Returns null if no object is found
	private GObject getCollidingObject(GPoint upperLeft, GPoint upperRight,
			GPoint lowerLeft, GPoint lowerRight)	{
		// Check for objects at the four corners
		// Upper left corner
		GObject upperLeftObj = getElementAt(upperLeft);
		if(upperLeftObj != null)	{
			return upperLeftObj;
		}
		// Upper right corner
		GObject upperRightObj = getElementAt(upperRight);
		if(upperRightObj != null)	{
			return upperRightObj;
		}
		// Lower left corner
		GObject lowerLeftObj = getElementAt(lowerLeft);
		if(lowerLeftObj != null)	{
			return lowerLeftObj;
		}
		// Lower right corner
		GObject lowerRightObj = getElementAt(lowerRight);
		if(lowerRightObj != null)	{
			return lowerRightObj;
		}
		// All else fails return null
		return null;
	}
	
	// Check which side of the ball collided with a brick. Returns true
	// if the top side collided with a brick and false otherwise.
	private boolean checkBrickLocation(GObject collidingObj)	{
		// Find the Y coordinate for the colliding object
		double collidingObjY = collidingObj.getY();
		if(collidingObjY > ball.getY())	{
			return true;
		} else	{
			return false;
		}
	}

	// Creates labels in roughly the center of the screen.
	// Takes a font as an input. Also takes a color as a parameter
	// to set the font color.
	private GLabel createLabel(String message, String font, Color color)	{
		GLabel label = new GLabel(message);
		// Set the label's font
		label.setFont(font);	
		// Set the label's color
		label.setColor(color);
		// Find label dimensions
		double ascent = label.getAscent();
		double width = label.getWidth();
		// Find the center of the window
		double labelX = (getWidth() - width) / 2;	// Calculate label x coordinate
		double labelY = (getHeight() + (3 * ascent)) / 2;	// Calculate label y coordinate
		// Create GPoint with coordinates for label
		GPoint labelPoint = new GPoint(labelX, labelY);
		// Add the label
		label.setLocation(labelPoint);
		add(label);
		return label;
	}
	
	// Called whenever the game is won
	private void winCondition()	{
		createLabel("YOU WIN!","Helvetica-48", Color.GREEN);
		remove(ball);
		pause(1500); // Give the user a second to calm down
		removeAll();
		// Set the_woz.jpeg as a GImage so that it can be displayed
		GImage theWoz = new GImage("the_woz.jpeg");
		theWoz.setSize(getWidth(), getHeight());
		add(theWoz);
		// Set the sentinel to true to end execution
		isGameWon = true;
	}
}

