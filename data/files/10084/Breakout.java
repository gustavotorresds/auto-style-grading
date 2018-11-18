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
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;


	// Instant Variables

	private double vx, vy; 

	private RandomGenerator rgen = RandomGenerator.getInstance(); 

	private double paddleY = CANVAS_HEIGHT - PADDLE_Y_OFFSET; 

	private 	GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT); 

	private double paddleX = 0; 

	private 	GOval ball = makeBall(); 

	private int total_bricks = NBRICK_COLUMNS * NBRICK_ROWS; 
	
	private int turns_played = NTURNS; 


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// SETUP
		// 1. Bricks
		double brickX = CANVAS_WIDTH - (CANVAS_WIDTH - ((BRICK_WIDTH+BRICK_SEP) * (NBRICK_COLUMNS-1) + BRICK_WIDTH))/2 - ((NBRICK_COLUMNS-1)*(BRICK_WIDTH+BRICK_SEP)) - BRICK_WIDTH;
		double brickY = BRICK_Y_OFFSET; 

		for (int r=0; r< NBRICK_ROWS; r++) {
			drawRow(r, brickX, brickY);
			brickY = brickY + BRICK_SEP + BRICK_HEIGHT;
		}

		// paddle
		addMouseListeners(); 
		paddle.setFilled(true);
		add (paddle, paddleX, paddleY); 

		//ball
		ballSetup();
		while (turns_played != 0) {
		waitForClick(); 
		ballMove(); 
		}
	

	}

	private void drawRow(int r, double brickX, double brickY) {
		for (int c=0; c < NBRICK_COLUMNS; c++) {
			GRect brick = new GRect (BRICK_WIDTH,BRICK_HEIGHT); 
			brick.setFilled(true);

			if (r%10 == 0 || r%10 == 1) {
				brick.setColor(Color.RED);
			}
			if (r%10 == 2 || r%10 == 3) {
				brick.setColor(Color.ORANGE);
			}
			if (r%10 == 4 || r%10 == 5) {
				brick.setColor(Color.YELLOW);
			}
			if (r%10 == 6 || r%10 == 7) {
				brick.setColor(Color.GREEN);
			}
			if (r%10 == 8 || r%10 == 9) {
				brick.setColor(Color.CYAN);
			}

			add(brick, brickX, brickY); 
			brickX = brickX + BRICK_WIDTH + BRICK_SEP; 

		}
	}

	// 2. Paddle

	public void mouseMoved(MouseEvent e) {
		if (e.getX() > CANVAS_WIDTH - PADDLE_WIDTH) {
			paddleX = CANVAS_WIDTH - PADDLE_WIDTH; 		
		}
		else if (e.getX() < 0) {
			paddleX = 0; 		
		}
		else {
			paddleX = e.getX(); 
		}
		paddle.setLocation(paddleX, paddleY);
	}

	// PLAY
	// 3. Ball

	//ball setup
	private void ballSetup() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx; 
		vy = VELOCITY_Y; 
	}


	// update velocity
	private void ballMove() {
		while (hitBottomWall(ball) != true || total_bricks != 0) {
			//check if games is over

			if (hitBottomWall(ball) == true) {
				turns_played = turns_played - 1; 
				println(turns_played);
				if (turns_played == 0) {
					youLost(); 
					break;
				}
				else {
					remove(ball) ; 
					ball = makeBall();
					ballSetup(); 
				}
			}
			else if (total_bricks == 0) {
				youWon(); 
				break;
			}

			else {
				//update velocity
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx; 
				}

				if(hitTopWall(ball)) {
					vy = -vy; 
				}

				//update visualization
				ball.move(vx, vy);
				collision(); 

				//pause
				pause(DELAY);
			}


		}

	}


	private boolean hitBottomWall(GOval ball) {
		return ball.getY () > CANVAS_HEIGHT - ball.getHeight();
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY () <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX () > CANVAS_WIDTH - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX () <= 0;
	}


	private GOval makeBall() {
		double size = BALL_RADIUS * 2; 
		GOval r = new GOval (size, size); 
		r.setFilled(true);
		double ballX = CANVAS_WIDTH/2 - BALL_RADIUS; 
		double ballY = CANVAS_HEIGHT/2 - BALL_RADIUS; 
		add(r,ballX, ballY); 
		return r; 
	}


	// 4. Collisions 

	//Find collision
	private GObject getCollidingObject() {
		GObject touchedBallA = getElementAt(ball.getX(), ball.getY()); 
		GObject touchedBallB = getElementAt(ball.getX() + 2* BALL_RADIUS, ball.getY());
		GObject touchedBallC = getElementAt(ball.getX(), ball.getY() + 2* BALL_RADIUS); 
		GObject touchedBallD = getElementAt(ball.getX() + 2* BALL_RADIUS, ball.getY() + 2* BALL_RADIUS); 

		if (touchedBallA != null) {
			return touchedBallA;
		}

		if (touchedBallB != null) {
			return touchedBallB;
		}

		if (touchedBallC != null) {
			return touchedBallC;
		}

		if (touchedBallD != null) {
			return touchedBallD;
		}

		else { 	
			return null; 
		}
	}


	private void collision() {
		GObject collider =	getCollidingObject();
		if (collider == paddle) {
			vy = -vy;
		}
		else if (collider!= null) {
			vy = -vy;
			remove(collider); 
			total_bricks = total_bricks - 1 ;
		}
	}


	//5. Finishing up

	// Terminate when ball hits the bottom

	private void youLost() {
		GLabel youLost = new GLabel ("You lost! Play again?");
		youLost.setColor(Color.RED); 
		youLost.setFont("IMPACT-40");
		add (youLost, CANVAS_WIDTH/2 - youLost.getWidth()/2 , CANVAS_HEIGHT/2 - (youLost.getHeight()/2));

	}

	// Terminate when bricks are over

	private void youWon() {
		GLabel youWon = new GLabel ("You won! Play again?");
		youWon.setColor(Color.GREEN); 
		youWon.setFont("IMPACT-40");
		add (youWon, CANVAS_WIDTH/2 - youWon.getWidth()/2 , CANVAS_HEIGHT/2 - youWon.getHeight()/2);
	} 



	// Play again
	public void mouseCliked(MouseEvent e) {
		ballMove();
	}


}



