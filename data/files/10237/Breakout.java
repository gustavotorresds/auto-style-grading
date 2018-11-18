
/*
 * File: Breakout.java
 * -------------------
 * Name: Joe Garigliano
 * Section Leader: Garrick
 * Date: September 7, 2018 
 * 
 * This file is the game of breakout.
 * The goal of the game is to destroy all the bricks before losing your allotted lives
 * Construction involves a world setup as well as animation that tracks lives
 * 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int brickCounter;
	private int livesCounter;
	private GLabel winMessage;
	private GLabel gameOverMessage;
	private GLabel clickForBall; 


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
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//the setupGame methods involve the setup of the world
		//adding the bricks, paddle, ball, mouse listeners, and counters
		setupGame();

		// set x and y coordinate velocity parameters for the ball
		// x coordinate velocity based on random generator numbers
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		double vy = VELOCITY_Y;


		//Add label to game showing the remaining number of bricks
		GLabel brickCounterLabel = new GLabel("Bricks Remaining: " + brickCounter);
		add(brickCounterLabel, (getWidth()-brickCounterLabel.getWidth()-10), brickCounterLabel.getAscent() + 5);

		//Add label to game showing the remaining number of game lives
		GLabel livesCounterLabel = new GLabel("Lives Remaining: " + livesCounter);
		add(livesCounterLabel, 10, livesCounterLabel.getAscent() +5);


		// Animation Loop
		// Update direction of ball when it hits walls 
		// Collider function removes bricks when hit and changes direction
		//Method runs until all bricks have been hit (brickCounter variable)
		while(livesCounter>0  && brickCounter>0) {			
			//Click by user will start the game in motion
			//Once click happens, remove the "click to launch" label
			addClickForBall();
			waitForClick();
			remove(clickForBall);
			add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2);

			// bounce off the left, right, and top wall
			// if bottom wall is hit, exit while loop and lose 1 life
			while(hitBottomWall(ball)==false) {					
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				}
				if(hitTopWall(ball)) {
					vy = -vy;
				}

				//if ball hits a non-paddle object (i.e. brick) remove the brick
				//reverse direction, and count down on brick counter
				GObject collider = getCollidingObject();
				if(collider != null && collider != brickCounterLabel && collider != livesCounterLabel && collider!=winMessage) {
					if(collider != paddle) {
						remove(collider);
						vy=-vy;
						brickCounter = brickCounter -1;	
					} 

					// when ball hits paddle, reverse direction
					if(collider == paddle) {
						if(ball.getY() <= paddle.getY()) {
							vy=-vy;
						}
					}
				}	

				// update visualization
				ball.move(vx, vy);

				// update the brick counter reflecting the current number stored
				// in the brick counter variable
				brickCounterLabel.setText("Bricks Remaining: " + brickCounter);

				//Add message once all bricks are gone that game is over
				if(brickCounter == 0) {
					winMessage = new GLabel ("Congratulations, you've won!!!");
					add(winMessage, (getWidth()-winMessage.getWidth())/2, getHeight()/2 +winMessage.getAscent());
					remove(ball);
				}
				pause(DELAY);				
			}
			remove(ball);

			// Update the lives counter down by 1 life
			// Reset the life counter label
			livesCounter = livesCounter - 1;
			livesCounterLabel.setText("Lives Remaining: " + livesCounter);		
		}					
		//Add ending message for when ball hits the bottom and lives hit zero
		if(livesCounter==0) {
			gameOverMessage = new GLabel("Game Over");
			add(gameOverMessage, (getWidth()-gameOverMessage.getWidth())/2, getHeight()/2);
		}
	}		

	// this method creates the major objects in the game before the user starts to play
	private void setupGame() {
		setupBricksGrid();
		createPaddle();
		createBall();
		addMouseListeners();
		createBrickCounter();
		createGameLives();
	}


	// Add label prompting the user to click to launch ball
	private void addClickForBall() {
		clickForBall = new GLabel("Click to launch ball");
		add(clickForBall, (getWidth() - clickForBall.getWidth())/2, (getHeight() - clickForBall.getAscent())/2);		
	}

	//Create counter for the remaining number of lives in the game
	private void createGameLives() {
		livesCounter = NTURNS;		
	}

	//Add brick counter to count down to the end of the game
	private void createBrickCounter() {
		brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;

	}

	// Method: Get Colliding Object
	// this method searches the 4 corners of the ball 
	// and returns any non-null value it finds at those corners as the collider variable
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()) != null){
			return getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS) != null){
			return getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS) != null) { 
			return getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		}	return null; 
	}


	// Returns if ball goes below the bottom wall
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	// Returns if ball should bounce off top wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	// Returns if ball should bounce off right wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	// Returns if ball should bounce off the left wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}


	// this method creates the ball for the game
	private void createBall() {
		ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);				
	}


	// This method creates the paddle for the game
	private void createPaddle() {
		double centerX = (getWidth() - PADDLE_WIDTH) / 2;
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, centerX, getHeight() - PADDLE_Y_OFFSET);				
	}



	// Method: Paddle horizontal tracker
	// this method makes the paddle move left/right to follow the mouse's movement
	// the method contains constraints that limit the paddle from moving off the screen
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();

		// Limit the left movement of the paddle to the left wall		
		if(mouseX <0) {
			paddle.setLocation(0,PADDLE_Y_OFFSET);
		} else {
			paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
		}

		// Limit the right movement of the paddle to the right wall
		if(mouseX > (getWidth() - PADDLE_WIDTH)) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH,getHeight() - PADDLE_Y_OFFSET);
		} else {
			paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
		}

	}

	// create the 10 rows with 10 bricks using for loops
	private void setupBricksGrid() {
		for(int brickRows=0; brickRows<NBRICK_ROWS; brickRows++) {
			for(int brickColumns=0; brickColumns<NBRICK_COLUMNS; brickColumns++) {
				double X = (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH) - (NBRICK_COLUMNS * BRICK_SEP) + brickColumns * (BRICK_WIDTH + BRICK_SEP));

				// orient the horizontal location of each row based on # of bricks from center

				double Y = (BRICK_Y_OFFSET + (brickRows * BRICK_HEIGHT) + ((brickRows - 1) * BRICK_SEP));
				//orient the vertical location of each brick based on the row number

				GRect brick = new GRect(X, Y, BRICK_WIDTH, BRICK_HEIGHT);


				// set color scheme for brick rows: 
				// 0,1=RED // 2,3=ORANGE // 4,5=YELLOW // 6,7=GREEN // 8,9=CYAN


				if(brickRows%10 == 0) {
					brick.setColor(Color.RED);
				} else {
					if(brickRows%10 == 1) {
						brick.setColor(Color.RED);
					} else {
						if(brickRows%10 == 2) {
							brick.setColor(Color.ORANGE);
						} else {
							if(brickRows%10 == 3) {
								brick.setColor(Color.ORANGE);
							} else {
								if(brickRows%10 == 4) {
									brick.setColor(Color.YELLOW);
								} else {
									if(brickRows%10 == 5) {
										brick.setColor(Color.YELLOW);
									} else {
										if(brickRows%10 == 6) {
											brick.setColor(Color.GREEN);
										} else {
											if(brickRows%10 == 7) {
												brick.setColor(Color.GREEN);
											} else {
												if(brickRows%10 == 8) {
													brick.setColor(Color.CYAN);
												} else {
													if(brickRows%10 == 9) {
														brick.setColor(Color.CYAN);
													} else {
													}
												}
											}
										}
									}
								}
							}
						}
					} 
				}
				brick.setFilled(true);
				add(brick);					
			}
		}
	}
}
