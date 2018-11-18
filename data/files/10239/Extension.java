/*
 * File: Extension.java
 * -------------------
 * Name: Ju Yeon (Julie) Lee
 * Section Leader: Niki Agrawal
 * 
 * This program is an extension of the Breakout.
 * It gives user instructions and number of turns left, tells
 * if user won or lost, makes sounds whenever ball bounces,
 * and keeps score.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Extension extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 9;

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
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Velocity of ball
	private double vx, vy;
	
	GRect paddle = null;
	GOval ball = null;
	
	// Total number of bricks
	int totalBrick = 0;
	
	// x and y coordinates of bricks
	double brickX=0;
	double brickY=0;
	
	// x coordinate of paddle
	double paddleX = 0;
	
	// Current turn number
	int curTurn = NTURNS;
	
	// Display when turn ends
	GLabel loseTurn = null;
	
	// Bouncing sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// Score of user
	int score = 0;
	GLabel scoreBoard = new GLabel("score: " + score);
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Set bricks on the screen
		setBricks();
		
		// Creates paddle and moves paddle with mouse
		paddle = setPaddle();
		addMouseListeners();
		
		// Creates ball
		ball = setBall();
		
		// Shows score
		showScore();
		
		// Displays instructions and gives player time to prepare
		showInstruction();
		
		// Moves ball
		moveBall();
	}

	/*
	 * Sets bricks according to the determined number of rows
	 * and columns. The color of bricks change in red, orange, 
	 * yellow, green, and cyan every two rows. 
	 */
	private void setBricks() {
		//numRow is the place of row. 
		for(int numRow=0;numRow<NBRICK_ROWS;numRow++) {
			//numBrick is the place of brick in a row. 
			for(int numBrick=0;numBrick<NBRICK_COLUMNS;numBrick++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				//brickX and brickY are x and y coordinates of bricks.
				brickX = (getWidth()-NBRICK_COLUMNS*(BRICK_WIDTH+BRICK_SEP)+BRICK_SEP)/2+numBrick*(BRICK_WIDTH+BRICK_SEP);
				brickY = BRICK_Y_OFFSET+numRow*(BRICK_HEIGHT+BRICK_SEP);
				// sets color of bricks
				if(numRow%10==0||numRow%10==1) {
					brick.setColor(Color.RED);
					brick.setFilled(true);
				} else if(numRow%10==2||numRow%10==3) {
					brick.setColor(Color.ORANGE);
					brick.setFilled(true);
				} else if(numRow%10==4||numRow%10==5) {
					brick.setColor(Color.YELLOW);
					brick.setFilled(true);
				} else if(numRow%10==6||numRow%10==7) {
					brick.setColor(Color.GREEN);
					brick.setFilled(true);
				} else if(numRow%10==8||numRow%10==9) {
					brick.setColor(Color.CYAN);
					brick.setFilled(true);
				}
				add(brick,brickX,brickY); 
				//counts total number of bricks
				totalBrick = totalBrick+1;
			}
		}
	}

	/*
	 * Creates black paddle in the bottom center of the screen.
	 */
	private GRect setPaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		// x coordinate of paddle
		paddleX = getWidth()/2-PADDLE_WIDTH/2;
		// y coordinate of paddle
		double paddleY = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		add(paddle, paddleX, paddleY);
		return paddle;
	}
	
	/*
	 * Moves paddle left and right as the user moves the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		// x coordinate of paddle
		paddleX = e.getX();
		//make paddle follow the mouse without going out of screen
		if(paddleX>PADDLE_WIDTH/2&&paddleX<getWidth()-PADDLE_WIDTH/2) {
			paddle.setLocation(paddleX,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		} else if(paddleX<=PADDLE_WIDTH) {
			paddle.setLocation(0,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		} else if(paddleX>=getWidth()-PADDLE_WIDTH/2) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		}
	}	
	
	/*
	 * Sets one black ball in the middle of the screen. 
	 */
	private GOval setBall() {
		GOval ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball, getWidth()/2-BALL_RADIUS,getHeight()/2-BALL_RADIUS);
		return ball;
	}
	
	/*
	 * The scoreboard is added at the bottom right corner of the 
	 * screen in magenta. 
	 */
	private void showScore() {
		add(scoreBoard, getWidth()-2*scoreBoard.getWidth(), getHeight()-PADDLE_Y_OFFSET/3);
		scoreBoard.setColor(Color.MAGENTA);
	}
	
	/*
	 * Shows instructions when starting and asks for click to start. 
	 */
	private void showInstruction() {
		GLabel inst = new GLabel("Move the paddle to remove blocks with a ball.");
		GLabel start = new GLabel("You have 3 turns! Click to Start!");
		add(inst,getWidth()/2-inst.getWidth()/2,getHeight()/2-start.getHeight()*2);
		add(start,getWidth()/2-start.getWidth()/2,getHeight()/2-start.getHeight());
		waitForClick();
		remove(start);
		remove(inst);
	}
	
	/*
	 * Sets the velocity of ball and makes it bounce off the walls
	 * and bricks. Bricks are removed once hit and game ends when
	 * all the bricks are gone or user used up 3 turns. 
	 */
	private void moveBall() {
		// horizontal velocity of ball randomly generated
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		// vertical velocity of ball
		vy = VELOCITY_Y;
		while(true) {
			// Bounces off walls
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
				bounceClip.play();
			}
			if(hitTopWall(ball)) {
				vy = -vy;
				bounceClip.play();
			}
			ball.move(vx, vy);
			pause(DELAY);

			// Collides with objects and removes
			GObject collider = getCollidingObject();
			if(collider!=null && collider!=scoreBoard) {
				removeObject(collider);
			}
			
			// Ends game when appropriate
			endGame();
		}	
	}
	
	/*
	 * Returns true when ball hits bottom wall.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight()-2*BALL_RADIUS;
	}
	/*
	 * Returns true when ball hits top wall.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	/*
	 * Returns true when ball hits right wall.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth()-2*BALL_RADIUS;
	}
	/*
	 * Returns true when ball hits left wall.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;		
	}
	
	/*
	 * Checks four corners of the rectangle surrounding the ball
	 * and returns the object if there is any.
	 */
	private GObject getCollidingObject() {
		//top left corner
		GObject collider = getElementAt(ball.getX(),ball.getY());
		if(collider==null) {
			//top right corner
			collider = getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY());
			if(collider==null) {
				//bottom left corner
				collider = getElementAt(ball.getX(),ball.getY()+2*BALL_RADIUS);
				if(collider==null) {
					//bottom right corner
					collider= getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS);
				}				
			}
		} 
		return collider;
	}
	
	/*
	 * Ball bounces and removes brick when collides,
	 * and ball just bounces when collides with paddle. 
	 */
	private void removeObject(GObject collider) {
		if(collider == paddle) {
			// The vertical velocity of ball after hitting the paddle should be positive
			if(vy<0) {
				vy=-vy;
			}
			vy=-vy;
			bounceClip.play();
		} else {
			vy=-vy;
			bounceClip.play();
			remove(collider);
			// counts remaining number of bricks
			totalBrick = totalBrick-1;
			score+=1;
			scoreBoard.setLabel("score: " + score);
		}
	}
	
	/*
	 * Ends game when all the bricks are gone
	 * and ends turn when ball hits bottom wall.
	 * After 3 turns, game ends. Instructions and number
	 * of turns left shows on the screen. 
	 */
	private void endGame() {
		//ends game when all bricks gone
		if(totalBrick==0) {
			terminateGame();
			GLabel win = new GLabel("You win!");
			add(win, getWidth()/2-win.getWidth()/2,getHeight()/2+win.getHeight()/2);
		}
		//ends turn
		if(hitBottomWall(ball)) {
			endTurn();
			curTurn = curTurn-1;
			if(curTurn==2) {
				loseTurn = new GLabel("You have 2 turns left. Click to Start!");
				add(loseTurn, getWidth()/2-loseTurn.getWidth()/2,getHeight()/2-BALL_RADIUS*2);
				waitForClick();
				remove(loseTurn);
			} else if(curTurn==1) {
				loseTurn = new GLabel("This is your last chance! Click to Start!");
				add(loseTurn, getWidth()/2-loseTurn.getWidth()/2,getHeight()/2-BALL_RADIUS*2);
				waitForClick();
				remove(loseTurn);
			} else if(curTurn==0) {
				//ends game
				terminateGame();
				loseTurn = new GLabel("You Lose :(");
				add(loseTurn, getWidth()/2-loseTurn.getWidth()/2,getHeight()/2-BALL_RADIUS*2);
			}
		}			
	}
	
	// Ends game by removing ball
	private void terminateGame() {
		remove(ball);
	}
	
	// Ends turn by setting paddle and ball in staring position
	private void endTurn() {
		paddle.setLocation(getWidth()/2-PADDLE_WIDTH/2,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		ball.setLocation(getWidth()/2-BALL_RADIUS,getHeight()/2-BALL_RADIUS);
	}
}

