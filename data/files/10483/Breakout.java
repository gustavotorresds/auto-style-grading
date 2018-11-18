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

	private double vx, vy;
	
	// Declare an instance variable rgen, which will serve as a random-number generator:
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Number of turns 
	public static final int NTURNS = 3;
	
	private int brickCounter = 100;

	
	
	public void run() {
		for(int i=0; i < NTURNS; i++) {
			
			// SETUP //
			drawBricks(BRICK_Y_OFFSET);
			drawPaddle();
			drawBall();
			
			// PLAY //
			play();
			
			
			if(brickCounter == 0) {
				ball.setVisible(false);
				printWinner();
				break;
			}
			if(brickCounter > 0) {
				removeAll();
			}
		}
		
		if(brickCounter > 0) {
			printGameOver();
		}	
	}

/////////////////////////////////////////////////////////////////////
///////////////////////// SET UP ////////////////////////////////////
/////////////////////////////////////////////////////////////////////

	
	///// DRAWING BRICKS /////
	private void drawBricks(double brickYOffset) {
		// TODO Auto-generated method stub
		double g = 0;
		double ss = 0;
		double xcord = 10;
		double z = 0;
		
		for ( int j = 0; j < NBRICK_ROWS; j++) {
			g = 0;
		for ( int i = 0; i < NBRICK_COLUMNS; i++) {
			ss = (((getWidth() - (BRICK_WIDTH*10))/2) + g);
			GRect rect = new GRect(ss-xcord, BRICK_Y_OFFSET+z, BRICK_WIDTH, BRICK_HEIGHT);
			add(rect);
			g = ((BRICK_WIDTH + 2)*(i+1));
			rect.setFilled(true);			
			if (j < 2) {
				rect.setColor(Color.RED);
			}
			else if (j >= 2 && j < 4) {
				rect.setColor(Color.ORANGE);
			}
			else if ( j >= 4 && j < 6) {
				rect.setColor(Color.YELLOW);
			}
			else if ( j >= 6 && j < 8) {
				rect.setColor(Color.GREEN);
			}
			else if ( j >= 8 && j < 10) {
				rect.setColor(Color.CYAN);
			}	
		}
		z = (BRICK_HEIGHT + 2)*(j+1);
	}
	}

	///// DRAWING PADDLE /////
	private GRect paddle;
	private void drawPaddle() {
		// TODO Auto-generated method stub
		double xcor = (getWidth() - PADDLE_WIDTH) / 2; 
		double ycor = (getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT));
		paddle = new GRect (xcor, ycor, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.PINK);
		add (paddle);
		addMouseListeners();
	}
	

	///// MOUSE PADDLE TRACK /////
	public void mouseMoved(MouseEvent e) {
		if ((e.getX() < (getWidth() - PADDLE_WIDTH/2)) && (e.getX() > PADDLE_WIDTH/2)) {
			double ycor_const = (double) (getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT)); 
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, ycor_const);
		}
	}
	
		
	///// CREATE BALL /////
	private GOval ball;
	private void drawBall() {
		double xcor = getWidth()/2 - BALL_RADIUS;
		double ycor = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(xcor, ycor, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.GRAY);
		add(ball);
	}
		
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// PLAY ////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

		private void play() {
			waitForClick();
			getBallVelocity();
			while (true) {
				moveBall();
				if (ball.getY() >= getHeight()) {
					break;
				}
				if(brickCounter == 0) {
					break;
				}
			}
		}
		
				
		///// DISPLAY WINNER /////
		private void printWinner() {
			GLabel Winner = new GLabel ("You Won !!!", getWidth()/2, getHeight()/2);
			Winner.move(-Winner.getWidth()/2, -Winner.getHeight());
			Winner.setColor(Color.RED);
			add (Winner);
		}	
		
		///// DISPLAY GAME OVER /////
		private void printGameOver() {
			GLabel gameOver = new GLabel ("Game Over, You Suck", getWidth()/2, getHeight()/2);
			gameOver.move(-gameOver.getWidth()/2, -gameOver.getHeight());
			gameOver.setColor(Color.RED);
			add (gameOver);
		}
		
		
		///// BOUNCE IT OFF THE WALL /////
		private void moveBall() {
			// IF COLLIDING WITH THE UPPER AND SIDE WALLS //
			ball.move(vx, vy);
			if ((ball.getX() - vx <= 0 && vx < 0 ) || 
				(ball.getX() + vx >= (getWidth() - BALL_RADIUS*2) && vx > 0)) {
				vx = -vx;	// REVERSE X VELOCITY COMPONENT AFTER Y COLLISION
			}
			if ((ball.getY() - vy <= 0 && vy < 0 )) {
				vy = -vy;	// REVERSE Y VELOCITY COMPONENT AFTER X COLLISION
			}
			// IF COLLIDING WITH THE BRICK OR THE PADDLE
			collider();
		}
		
		///// COLLIDING WITH THE BRICK OR THE PADDLE /////
		private void collider() {
			GObject collider = getCollidingObject();
			// IF COLLIDING OF THE PADDLE //
			if (collider == paddle) {		
				double z = PADDLE_Y_OFFSET + PADDLE_HEIGHT + BALL_RADIUS*2;
				if(ball.getY() >= getHeight() - z && 
				   ball.getY() <  getHeight() - z + 4) {
				   vy = -vy;	
				}
			}
			// IF COLLIDING WITH THE BRICK - DELETE BRICK //
			else if (collider != null) {
				remove(collider); 
				brickCounter--;
				vy = -vy;
			}
			pause (DELAY);
		}

		
		///// BALL DYNAMICS - SPEED UP or DOWN /////
		private void getBallVelocity() {
			vy = 7.0;
			vx = rgen.nextDouble(2.0, 4.0);
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
		}
		
		
		///// COLLIDING OBJECTS /////
		private GObject getCollidingObject() {
			if((getElementAt(ball.getX(), ball.getY())) != null) {
		         return getElementAt(ball.getX(), ball.getY());
		      }
			else if(getElementAt((ball.getX() + BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2)) != null ){
		         return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
		      }
			else if(getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2)) != null ){
		         return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
		      }
			else if (getElementAt( (ball.getX() + BALL_RADIUS*2), ball.getY()) != null ){
		         return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
		      }
		
			else{
		         return null;
		      }
		}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////  END  ////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////