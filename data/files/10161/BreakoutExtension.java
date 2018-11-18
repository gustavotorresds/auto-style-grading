/*
 * File: BreakoutExtension.java
 * -------------------
 * Name: Eliza Pink 
 * Section Leader: Drew Bassilakis 
 *
 * This file implements the game of Breakout plus some extensions. I added a 
 * welcoming message at the beginning of the game, a score counter,
 * sound effects each time the ball hits a brick, a background image, and an image of a pot 
 * of gold that appears if you win the game. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

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

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;
	
	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Total number of bricks
	public static final int TOTAL_BRICKS = NBRICK_ROWS*NBRICK_COLUMNS;
	
	// Instance variables 
		private GRect paddle; 
		private GOval ball;	
		private GRect brick;
		private double vx;
		private double vy;
		private RandomGenerator rgen = RandomGenerator.getInstance();
		private int brickCount = 0; 
		private int turns = 0;
		private GLabel endmessage;
		private int scoreCounter;
		private GLabel score; 
		private AudioClip bounce = MediaTools.loadAudioClip("bounce.au");
		private GLabel welcome;
		private GLabel begin;
		
		private GImage img1;
		

	
	/*
	 * Method: Run
	 * ----------------
	 * Program execution starts here. It runs the breakout program. 
	 */
		
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size. 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// Add GImage 	
		// Order of methods 
		setUp();
		addMouseListeners();
		playGame();
	}

	
	
	/*
	 * Method: Set Up 
	 * ----------------
	 * Program execution starts here.  
	 */
	private void setUp() {
			
		img1 = new GImage("bluesky.jpg");
		add(img1, 0, 0);
		
		makeBricks(getWidth()/2, BRICK_Y_OFFSET);
		
		// adding a score counter
		score = new GLabel("0");
		score.setFont("Helvetica-30");
		score.setColor(Color.RED);
		scoreCounter = -1;
		add(score, (getWidth()/2 - (score.getWidth())/2), 40);
		
		// adding instructions
		welcome = new GLabel("Welcome to Breakout!");
		welcome.setFont("Helvetica-24");
		welcome.setColor(Color.BLACK);
		add(welcome, (getWidth()/2 - (welcome.getWidth())/2), 300);
		
		begin = new GLabel("Click the mouse to begin!");
		begin.setFont("Helvetica-24");
		begin.setColor(Color.BLACK);
		add(begin, (getWidth()/2 - (begin.getWidth())/2), 400);
		
		
		makePaddle(); 
		
	}
	
	/*
	 * Method: Make Bricks 
	 * ----------------
	 * This method creates 10 rows of bricks in a rainbow-like sequence.   
	 */
	private void makeBricks(double cx, double cy) {

		for (int i = 0; i < NBRICK_ROWS ; i++) {
			
			for (int j = 0; j < NBRICK_COLUMNS ; j++) {
				
				//coordinates for starting brick 
				double xbrick = cx - (NBRICK_COLUMNS*BRICK_WIDTH)/2 - ((NBRICK_COLUMNS) -1)*BRICK_SEP/2 + j*BRICK_WIDTH + j*BRICK_SEP ;
				double ybrick = cy + i*BRICK_HEIGHT + i*BRICK_SEP;
				
				GRect brick = new GRect(xbrick, ybrick, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add (brick);
			
				//if statement checks the row number to determine designated color
				if (i < 2) {
					brick.setFillColor(Color.RED);
				}
				if (i >= 2 && i < 4) {
					brick.setFillColor(Color.ORANGE);
				}
				if (i >= 4 && i < 6) {
					brick.setFillColor(Color.YELLOW);
				}
				if (i >= 6 && i < 8) {
					brick.setFillColor(Color.GREEN);
				}
				if (i >= 8 && i < 10) {
					brick.setFillColor(Color.CYAN);
				}
				
			}
		}
	}
	
	/*
	 * Method: Make Paddle
	 * ----------------
	 * This method constructs and places the paddle on the canvas. 
	 */
	private void makePaddle() {
		double xpaddle = getWidth()/2 - PADDLE_WIDTH/2;
		double ypaddle = getHeight()/2 - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		
		paddle = new GRect(xpaddle, ypaddle, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);			
	}
	
	/*
	 * Method: Mouse Moved 
	 * ----------------
	 * This method is called any time the mouse moves in the program screen. The paddle made
	 * above tracks the mouse without moving off the edge of the window.  
	 */ 
	public void mouseMoved(MouseEvent e) {
		//Checking whether the midpoint of the paddle is between half paddle width of the
		// beginning and end of screen.  
		if ((e.getX() < getWidth() - PADDLE_WIDTH/2) && (e.getX() > PADDLE_WIDTH/2)) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT); 
		}	
	}
	
	
	/*
	 * Method: Play Game 
	 * ----------------
	 * This method plays the game, assuming the bricks and paddle are already set up in the
	 * precondition. It executes three turns, determines whether there are any bricks left, 
	 * and prints the correct ending message in the post condition.  
	 */
	private void playGame() {
		while (turns < NTURNS) {
			waitForClick();
			remove(welcome);
			remove(begin);
			ballVelocity(); 
			makeBall();
			while (brickCount < TOTAL_BRICKS) {
				moveBall();
				checkCollision(); 
				pause(DELAY); 
				if(ball.getY() == getHeight() - BALL_RADIUS*2) {
					remove(ball);
					turns ++;
					break;
				}
			}
		}
		printEnd(); 
	}
	
	
	
	/*
	 * Method: Make Ball
	 * ----------------
	 * This method creates a ball and places it on the screen.  
	 */	
	private void makeBall() {
		// ball coordinates 
		double xball = getWidth()/2 - BALL_RADIUS;
		double yball = getHeight()/2 - BALL_RADIUS;
		
		ball = new GOval(xball, yball, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}
	
	/*
	 * Method: Ball Velocity 
	 * ----------------
	 * This method determines the ball's velocity. 
	 */
	private void ballVelocity() {
		vy = 5.0;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		}
	
	/*
	 * Method: Move Ball 
	 * ----------------
	 * This method moves the ball and makes it change directions if it hits any wall, except
	 * for the bottom one. 
	 */
	private void moveBall() {
			ball.move(vx, vy); 
			//ball hits side walls  
			if ((ball.getX() - vx  <= 0 && vx < 0) || (ball.getX() + vx >= (getWidth() - BALL_RADIUS*2) && vx > 0)) {
				vx=-vx; 
			}
			//ball hits top wall
			if (ball.getY() - vy <= 0 && vy < 0) {
				vy= -vy;  
			}	 
		}

	/*
	 * Method: Check Collision
	 * ----------------
	 * This method determines whether the ball hit the paddle or a brick. If it was a brick, that
	 * brick is then removed from the screen.  
	 */
	private void checkCollision() {
		GObject collider = getCollidingObject();
		if(collider == paddle) {
			/* To avoid the "glued" ball situation, we have to make sure ball.getY() reaches the height
			 * at which the ball hits the paddle. 
			 */
			if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 &&
					ball.getY() < getHeight() - PADDLE_Y_OFFSET - BALL_RADIUS*2 + 4) {
				vy= -vy; 
			}
				
		} else if (collider != null && collider != score) {
			remove(collider);
			vy = -vy; 
			bounce.play();
			brickCount++;
			scoreCounter++;
			score.setLabel("" + scoreCounter);
		}
	}
	
	/*
	 * Method: Get Colliding Object 
	 * ----------------
	 * This method determines whether and where the ball collides with an object. 
	 */
	private GObject getCollidingObject() {
		if ((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY()); 	
		} else if ((getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY())) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
		} else if ((getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2)) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY()+ BALL_RADIUS*2); 
		} else if ((getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2)) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
		} else {
			return null;
		}		
	}
	
	/*
	 * Method: Print End 
	 * ----------------
	 * This method prints the final message of the game and determines whether the player won
	 * or lost. If there are no bricks remaining on the screen, the win message is displayed. 
	 * If not, the lose message is displayed. 
	 */
	private void printEnd() {
		img1 = new GImage("bluesky.jpg");
		add(img1, 0, 0);
		
		endmessage = new GLabel("You win!");
		double x = getWidth()/2 - endmessage.getWidth();
		double y = getHeight()/2 - endmessage.getAscent()/2;
		
		if (brickCount == TOTAL_BRICKS) {
			add(endmessage, x, y);
			endmessage.setFont("Helvetica-28");
			endmessage.setColor(Color.BLACK);
			GImage img2 = new GImage("potofgold.jpg");
			add(img2, getWidth()/2 - img2.getWidth()/2, 400);
		}
		else if (turns == NTURNS){
			endmessage = new GLabel("You lose!");
			add(endmessage, x, y);
			endmessage.setFont("Helvetica-28");
			endmessage.setColor(Color.BLACK);
		}	
	}

		
}

// Cloudy sky image: https://www.istockphoto.com/photos/vertical-cloud-blue-sky?excludenudity=true&mediatype=photography&phrase=vertical%20cloud%20blue%20sky&sort=mostpopular
// Pot of gold image: http://weclipart.com/cartoon+pot+of+gold+clipart 