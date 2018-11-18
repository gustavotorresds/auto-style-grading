/*
 * File: Breakout.java
 * -------------------
 * Name: Tran Lam 
 * Section Leader: Andrew 
 * 
 * This file implements the game of Breakout.
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

	/*instance variables*/
	private GRect paddle;                
	private GOval ball; 
	private int 	removedBrickCount = 0; 
	private double vx;
	private double vy; 

	public void run() {
		setTitle("CS 106A Breakout");					 /*set window's title bar text*/
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);       /*set screen size*/
		setupBricks();
		setupPaddle();
		addMouseListeners();
		makeBouncingBall();
		playGame();
	}

	/*this method sets up 5 groups of 2 rows of brick with different colors*/
	private void setupBricks() {
		double TwoRowsSpacing = 2*(BRICK_SEP+BRICK_HEIGHT);
		double x_startBrick = (getWidth()-(NBRICK_ROWS-1)*BRICK_SEP-NBRICK_ROWS*BRICK_WIDTH)/2;  

		make2rowsofBricks(x_startBrick,BRICK_Y_OFFSET,Color.RED);
		make2rowsofBricks(x_startBrick,BRICK_Y_OFFSET+TwoRowsSpacing,Color.ORANGE);
		make2rowsofBricks(x_startBrick,BRICK_Y_OFFSET+TwoRowsSpacing*2,Color.YELLOW);
		make2rowsofBricks(x_startBrick,BRICK_Y_OFFSET+TwoRowsSpacing*3,Color.GREEN);
		make2rowsofBricks(x_startBrick,BRICK_Y_OFFSET+TwoRowsSpacing*4,Color.CYAN);
	}

	/*this method sets up paddle at it's starting point*/
	private void setupPaddle(){
		paddle = new GRect((getWidth()-PADDLE_WIDTH)/2,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*this method creates a ball in the middle of the screen and randomize ball's x-direction velocity*/
	private void makeBouncingBall() {
		ball = new GOval (getWidth()/2-BALL_RADIUS,getHeight()/2-BALL_RADIUS,BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);	
		RandomGenerator rgen = RandomGenerator.getInstance();        /*have to turn this on for randomizing*/
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);         /*choosing random vx within range vmax and vmin*/
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) {                                 /*have the ball starts flyin left (or right)*/
			vx = -vx; 												/*50% of the time*/
		}
	}

	/*this method set up game procedure, giving player NTURNS. When turn is active, the user click the screen 
	 * to initialize the game. The ball check for wall and paddle collision and remove bricks. If hit bottom wall,
	 * turn is reduced by one, this ball is removed and a new ball is made. New turn is again initialized by user's
	 * click. If no turn is available, the loop breaks and a loser message is displayed. If user clears all the 
	 * bricks, the loop breaks and the victory message is displayed.*/
	private void playGame() {
		waitForClick();
		int turnsLeft = NTURNS;
		while(turnsLeft > 0) {				
			checkForWall();					
			removeBrick();					
			ball.move(vx,vy);
			pause(DELAY);
			if (hitBottomWall(ball)){         /*change the bottomWall condition later to signal end of turn*/
				turnsLeft = turnsLeft -1;
				remove(ball);
				makeBouncingBall();
				if (turnsLeft == 0) {               /*display loser message, set arbitrary equations for location (laziness)*/
					GLabel loser = new GLabel("TOO BAD, SUCKER!");
					add(loser,(getWidth()-loser.getX())/2,(getHeight()/2.5));
					break;
				}
				waitForClick();
			}
			if(victory()) {	 						/*display victory message, set arbitrary equations for location (laziness)*/
				GLabel victory = new GLabel("Congratulations, you broke out!");
				add(victory,(getWidth()-victory.getX())/2,(getHeight()-victory.getY())/2);
				break; 	
			}
		}
	}
	/*this method instructs the ball to reverse its x or y direction when hitting right, left, and top wall*/
	private void checkForWall() {
		if (hitRightWall(ball) || hitLeftWall(ball)){
			vx = -vx;
		}
		if (hitTopWall(ball)){         
			vy = -vy;  
		}        
	}

	/*this method removes elements identified by getCollidingObject() method*/
	private void removeBrick() {
		GObject collider = getCollidingObject(); 
		if (collider == paddle) {						/*ball bounces on paddle, change direction*/
			vy=-Math.abs(vy);			                /*abs prevents tunnel-like bouncing inside paddle*/ 
		} else if (collider != null) {	                /*ball bounces on brick, change direction and remove brick*/
			vy=-vy; 
			remove(collider); 
			removedBrickCount++;                         /*instance variable keeping track of # of breaks removed*/
		}

	}

	/*this method test 4 corner of the "square" of the ball and identified returns any element
	 * present at the four points. If there's no element, the method returns null value*/
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		GObject ballTopLeft = getElementAt(x,y);
		GObject ballTopRight = getElementAt(x+2*BALL_RADIUS,y);
		GObject ballBottomLeft = getElementAt(x,y+2*BALL_RADIUS);
		GObject ballBottomRight = getElementAt(x+2*BALL_RADIUS,y+2*BALL_RADIUS);
		if (ballTopLeft != null) {
			return (ballTopLeft);
		} else if (ballTopRight != null) {
			return (ballTopRight);
		} else if (ballBottomLeft != null) {
			return (ballBottomLeft); 
		} else if (ballBottomRight != null) {
			return (ballBottomRight);
		} else {
			return(null); 
		}
	}

	/*this boolean checks if the ball is hitting the RightWall*/
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth()-ball.getHeight(); 	
	}

	/*this boolean checks if the ball is hitting the LeftWall*/
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0; 
	}

	/*this boolean checks if the ball is hitting the TopWall*/
	private boolean hitTopWall (GObject ball) {
		return ball.getY() <= 0;
	}

	/*this boolean checks if the ball is hitting the BottomWal*/
	private boolean hitBottomWall (GObject ball) {
		return ball.getY() > getHeight()-ball.getHeight(); 
	}

	/*this mouseMoved method synchronizes paddle's x-direction movement with the mouse*/
	public void mouseMoved(MouseEvent e) {
		double x_paddle = e.getX()-PADDLE_WIDTH/2; 
		double y_paddle = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT; 

		if (x_paddle < 0) {
			paddle.setLocation(0,y_paddle);
		} else if (x_paddle > getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH,y_paddle);
		} else { 
			paddle.setLocation(x_paddle, y_paddle);
		}
	}

	/*this method creates two rows of bricks and requires 3 inputs: 
	 * 1) starting x coordinate
	 * 2) starting y coordinate
	 * 3) color of the these 2 rows of brick */
	private void make2rowsofBricks(double x_start,double y_start,Color color) {
		for (int p=0;p<2;p++) {
			for(int i=0;i<NBRICK_ROWS;i++) {
				double x_brick = x_start + (BRICK_SEP+BRICK_WIDTH)*i;
				double y_brick = y_start +(BRICK_HEIGHT+BRICK_SEP)*p;
				GRect brick = new GRect(x_brick,y_brick,BRICK_WIDTH, BRICK_HEIGHT);
				brick.setColor(color);
				brick.setFilled(true);
				add(brick);
			}
		}	
	}

	/*this boolean checks if the amount of brick removed is all the bricks present or not*/
	private boolean victory() {
		if (removedBrickCount >= NBRICK_COLUMNS*NBRICK_ROWS) {
			return true;
		} else {
			return false;
		}
	}
}
