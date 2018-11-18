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
	
	private GOval ball;
	
	private GRect paddle; 
	
	private RandomGenerator rgen = RandomGenerator.getInstance(); 
	
	private double vx, vy; 
	
	private int counter = 100; 
	
	private int counter2 = 3; 
	
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	private AudioClip backgroundMusic = MediaTools.loadAudioClip("GiveYouUp.mp3");


	public void run() {
		
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		backgroundMusic.play();
		backgroundMusic.loop();
		createBrickMass();
		paddle = makePaddle();
		addPaddle();
		addMouseListeners();
		addBall();
		while (true) {
			
			bouncyBall();
			GObject collider = getCollidingObject(); 
			if (collider == paddle) {
				//opp direction 
				bounceClip.play();
				vy = -vy;	
			} else if (collider != null) {
				remove(collider);
				bounceClip.play();
				vy = -vy;
				counter= counter - 1;
				if (counter == 0) {
					GLabel winningMessage = new GLabel("YOU WON!"); 
					add(winningMessage, getWidth()/2, getHeight()/2);
					return;
				}
			} 
			
			
			pause(DELAY);
		}
	}
	private void createBrickMass() { 
		for (int stack=0; stack < NBRICK_COLUMNS ; stack++) {
			for (int across = 0; across < NBRICK_ROWS; across++ ) {
				double xCoordinate = (across * BRICK_WIDTH) + (BRICK_SEP* 1.5 );
				double yCoordinate = ((stack * BRICK_HEIGHT) + BRICK_SEP) + BRICK_Y_OFFSET;
				GRect brick = new GRect (BRICK_WIDTH , BRICK_HEIGHT);
				brick.setFilled(true);
				if (stack<2) {
					brick.setColor(Color.RED);

				} else {
					if (stack<4) {
						brick.setColor(Color.ORANGE);
					} else {
						if (stack<6) { 
							brick.setColor(Color.YELLOW);
						} else {
							if (stack < 8) { 
								brick.setColor(Color.GREEN);
							} else { 
								if ( stack < 10) {
									brick.setColor(Color.CYAN);
								}
							}
						}
					}
				}
				add(brick , xCoordinate+ BRICK_SEP*across , yCoordinate+ BRICK_SEP*stack);
			}
		}
	}
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if ( x >= getWidth() - PADDLE_WIDTH) { 
			x = getWidth() - PADDLE_WIDTH;	
		}
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle.setLocation( x , y );	
		
	}
	private GRect makePaddle() { 
		GRect paddle = new GRect ( PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return(paddle);
	}
	private void addPaddle() {
		double paddleX = (getWidth()/2) - (PADDLE_WIDTH/2);
		double paddleY = getHeight()- PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle,paddleX, paddleY);
	}
	private GOval makeBall() {
		double ballX = (getWidth()/2) - (BALL_RADIUS  );
		double ballY = getHeight()/2 - (BALL_RADIUS );
		GOval ball = new GOval (ballX, ballY, BALL_RADIUS*2 , BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball, ballX, ballY);
		return(ball);
	}
	private void addBall() { 
		ball = makeBall();
		vx = rgen.nextDouble(1.0, 3.0);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;

	}
	private void bouncyBall() {  
		//made own counter bc didnt know one already existed whoops also 3 strikes and out 
			if(ball.getY()  >= getHeight() - (BALL_RADIUS*2)  ) {
				counter2 = counter2 - 1; 
				remove(ball);
				if (counter2 == 0) {
					GLabel losingMessage = new GLabel("YOU LOST"); 
					add(losingMessage, getWidth()/2, getHeight()/2);
					addImage();
					return; 
					
				} else { 
					if (counter2 > 0) {
						addBall();
					}
				}
			}
			if (ball.getY() <= getHeight()* 0 ) {
				vy =  -vy;
				bounceClip.play();
			}
			if(ball.getX()  >= getWidth() - BALL_RADIUS*2) {
				 vx = -vx;
				 bounceClip.play();
			}
			if (ball.getX() <= getWidth()* 0 ) {
				vx = -vx;
				bounceClip.play();
			}
			ball.move(vx, vy);
	}
	//finds four points of possible intersection 
	private GObject getCollidingObject() {
		GObject collidingItem = getElementAt(ball.getX(), ball.getY());
		if (collidingItem != null) {
			return collidingItem; 
		}
		collidingItem = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		if (collidingItem != null) {
			return collidingItem; 
		}
		collidingItem = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS); 
		if (collidingItem != null) {
			return collidingItem; 
		}
		collidingItem = getElementAt(ball.getX()+ 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		return collidingItem; 
		
	}
	private GImage addImage() { 
		GImage image = new GImage("robbie.png"); 
		image.setLocation(getWidth()/2 - BALL_RADIUS*20, getHeight()/2);
		image.setSize(BALL_RADIUS*20, BALL_RADIUS * 20);
		add(image);
		return(image);
	}
}


