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
	public static final double DELAY = 450.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	/*these are the instance variables I employ in the program*/
	private GRect paddle = null;
	private GObject collision;
	private GOval ball;
	private int blocksOnScreen;
	private int deaths;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;

	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		
		/*necessary*/
		addMouseListeners();
		
		/*method adds the rows of blocks to screen*/
		makeBlocks();
		
		/*method creates and adds paddle to be used for future animation*/
		paddle = makePaddle();
		add(paddle, (getWidth() - PADDLE_WIDTH) / 2 , getHeight() - PADDLE_Y_OFFSET);
		
		/*method creates and adds ball to be used for future animation*/
		ball = makeBall();
		
		/*this method returns initial vx and vy*/
		getRandomDirection();
		
		/*used to count lives left and blocks remaining*/
		deaths = NTURNS;
		blocksOnScreen = NBRICK_COLUMNS * NBRICK_ROWS;

		/*this method calls on the series of methods that constitute game play, and not set up*/
		playMode();
		
	}
			
			/*playMode is just a while loop that runs as long as the player has not won or lost, 
			 * it checks if the balls has hit the bottom, a wall, the paddle or a block
			 * then the game moves and Delays for animation*/
			private void playMode() {
				while(deaths > 0 && blocksOnScreen > 0) {
					checkForDeath();
					checkForEnd();
					checkForWall();
					checkForCollision();
					checkForWin();
					ball.move(vx, vy);
					pause(DELAY);
				}
			}	

			/*player wins when there is no blocks left, prints label Congratulations You Win*/
			private void checkForWin() {
				if(blocksOnScreen == 0) {
					GLabel win = new GLabel("Congratulations! You Win!");
					double xwin = (getWidth() / 2) - (win.getWidth() / 2);
					double ywin = (getHeight() / 2) - (win.getHeight() / 2);
					add(win, xwin, ywin);
				}
			}

			/*method has the ball bounce sideways of side walls and backward off top wall*/
			private void checkForWall() {
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				}
				if(hitTopWall(ball)) {
					vy = -vy;
				}
			}

			/*player has died if the bottom of the ball has hit the bottom
			 * the system then subtracts a death and adds the ball back into gameplay if any turns remain
			 * creates another random direction for the loop*/
			private void checkForDeath() {
				if(hitBottomWall(ball)) {
					deaths = deaths - 1;
					remove(ball);
					if(deaths > 0) {
						ball = new GOval((getWidth() - (2 * BALL_RADIUS)) / 2 , (getHeight() - (2 * BALL_RADIUS)) / 2, BALL_RADIUS * 2, BALL_RADIUS * 2);
						ball.setFilled(true);
						add(ball);
						getRandomDirection();
					} 
				}
			}

			/*end of game happens when no more deaths remain, prints Game Over*/
			private void checkForEnd() {
				if(deaths == 0) {
					GLabel end = new GLabel("Game Over");
					double xend = (getWidth() / 2) - (end.getWidth() / 2);
					double yend = (getHeight() / 2) - (end.getHeight() / 2);
					add(end, xend, yend);
				}
			}

			/*this embedded if statement checks continuously for a collision at the four corners 
			 * and the mid-point of the sides, in case the paddle slaps it*/
			private void checkForCollision() {
				collision = getElementAt(ball.getX(), ball.getY());
				if(collision != null) {
					collisionSteps();					
				} else {
					collision = getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
					if(collision != null) {
						collisionSteps();
					} else {
						collision = getElementAt(ball.getX()+ (2 * BALL_RADIUS), ball.getY());
						if(collision != null) {
							collisionSteps();
						} else {
							collision = getElementAt(ball.getX()+ (2 * BALL_RADIUS),ball.getY() + (2 * BALL_RADIUS));
							if(collision != null) {
								collisionSteps();
							} 
							}
						}
					}
				}
			
			

			/*if there is a collision, this method decides what to do with the ball direction and the collision object*/ 
			private void collisionSteps() {
				if(collision != paddle) {
					remove(collision);
					blocksOnScreen = blocksOnScreen - 1;
				}
				vy = -vy;
			}

			public GOval makeBall() {
				ball = new GOval((getWidth() - (2 * BALL_RADIUS)) / 2 , (getHeight() - (2 * BALL_RADIUS)) / 2, BALL_RADIUS * 2, BALL_RADIUS * 2);
				ball.setFilled(true);
				add(ball);
				return(ball);
	}

			public GRect makePaddle() {
				paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
				paddle.setFilled(true);
				return(paddle);
			}

			/*creates the mouse event that tracks the paddle*/
			public void mouseMoved(MouseEvent e) {
				
				/*these two lines ensure that the paddle stays moving only along the x-axis*/
				double x = e.getX() - (PADDLE_WIDTH / 2);
				double y = getHeight() - PADDLE_Y_OFFSET;
				
				/*these lines keep the paddle on the screen*/
				if(x >= getWidth() - PADDLE_WIDTH) {
					x = getWidth() - PADDLE_WIDTH;
				}
				if(x < 0) {
					x = 0;
				}
				
				/*sets paddle location*/
				paddle.setLocation(x, y);
				}
			
			private void makeBlocks() {
				for(double row = 0; row < NBRICK_COLUMNS; row++) {
					double x = (getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) + ( BRICK_SEP * (NBRICK_COLUMNS - 1)))) / 2;
					double y = (BRICK_Y_OFFSET + (row * (BRICK_HEIGHT + BRICK_SEP)));
					for(int column = 0; column < NBRICK_ROWS; column++) {
						GRect block = new GRect(x + (column * BRICK_SEP) + (column * BRICK_WIDTH), y, BRICK_WIDTH, BRICK_HEIGHT);
						block.setFilled(true);
							if(row <= 1) {
								block.setColor(Color.RED);
							} else {
								if(row <= 3 && row > 1) {
									block.setColor(Color.ORANGE);
								} else {
									if(row <= 5 && row > 3) {
										block.setColor(Color.YELLOW);
									} else { 
										if(row <= 7 && row > 5) {
											block.setColor(Color.GREEN);
										} else {
											if(row > 7){
												block.setColor(Color.CYAN);	
											}
										}
									}
								}
							}
							add(block);
					}
					}
	}

			private void getRandomDirection() {
				vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
				if (rgen.nextBoolean(0.5)) vx = -vx;
				vy = VELOCITY_Y;
	}

			private boolean hitBottomWall(GOval ball) {
				return ball.getY() > getHeight() - ball.getHeight();
			}

			private boolean hitTopWall(GOval ball) {
				return ball.getY() <= 0;
			}

			private boolean hitRightWall(GOval ball) {
				return ball.getX() >= getWidth() - ball.getWidth();
			}

			private boolean hitLeftWall(GOval ball) {
				return ball.getX() <= 0;
			}

	}

