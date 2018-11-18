/*
 * File: Breakout.java
 * -------------------
 * Name: Leigh Warner
 * Section Leader: Esteban
 * 
 * This file will allow a user to play the game Breakout. 
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
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / 
			NBRICK_COLUMNS);

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
	public static final double DELAY = 500.0 / 60.0;

	//Animation delay between "Attempt" label and start of round. 
	public static final double LABEL_DELAY = 40000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	GRect paddle = null;

	GOval ball = null;

	GLabel attempt = null;

	private int countBrick = 0;

	private static final int TOTAL_NUMBER_BRICKS = NBRICK_COLUMNS*NBRICK_ROWS;

	private double vx;

	private double vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*This run method is the main method of the game. It sets the title and canvas size, and then sets up the game
	 * and allows the user to play the game. The latter two methods are defined later to keep the run method simple.
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUp();	  													//This function will set up the game in a new method
		playGame();													//This will call a method that allows the user to play the game. 
	}

	//This method generally allows the user to play the game
	private void playGame() {	
		for (int turn =1; turn<NTURNS+1; turn++ ) {
			addLabel(turn);
			GOval ball = setUpBall();
			animateBall();
			waitForClick();
			while(countBrick<TOTAL_NUMBER_BRICKS) {
				ball.move(vx, vy);
				checkCollisions();
				pause(DELAY);
				if(hitBottomWall(ball)) {							 	
					remove(ball);
					break;
				}
			}
			if(countBrick==TOTAL_NUMBER_BRICKS) {  
				remove(ball);
				endGameWin();
				break;
			}
			if(countBrick != TOTAL_NUMBER_BRICKS && turn==3) {
				remove(ball);
				endGameLose();
				break;
			}
		}
	}

	//This method gives the user the label that says "Game Over"
	private void endGameLose() {
		GLabel gameOver = new GLabel ("Game Over");
		add(gameOver, getWidth()/2-(gameOver.getWidth()/2), getHeight()/2);
	}

	//This method  gives the user the label that says "You Won!"
	private void endGameWin() {
		GLabel youWon = new GLabel ("You Won!");
		add (youWon, getWidth()/2-(youWon.getWidth()/2), getHeight()/2);
	}

	//This method checks to see if the ball has hit other objects in the game. 
	private void checkCollisions() {
		GObject collider = getCollidingObject();
		if(collider==paddle) {
			vy = -Math.abs(vy);
			bounceClip.play();
		}
		if(collider !=paddle && collider!=null) {
			vy=-vy;
			remove(collider);
			countBrick++;
		}

		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball)) {
			vy=-vy;
		}

	}

	//This method animates the ball
	private void animateBall() {
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx=-vx;
		}
		vy = VELOCITY_Y;
	}

	/*This method adds the label at the beginning of the game that tells the player what turn 
	they are on*/
	private void addLabel(int turn) {
		GLabel attempt = new GLabel("Attempt: " +turn);
		add (attempt, (getWidth()/2)-(attempt.getWidth()/2), (getHeight()/2)+3*attempt.getAscent());
		pause(LABEL_DELAY);
		remove(attempt);
	}

	//This method evaluates the four corners of the ball to establish whether it has hit something. 
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		if(getElementAt((ball.getX()+(2*BALL_RADIUS)), ball.getY()) != null) {
			return getElementAt(ball.getX()+(2*BALL_RADIUS),ball.getY());
		}
		if(getElementAt(ball.getX(), (ball.getY()+(2*BALL_RADIUS))) != null) {
			return getElementAt(ball.getX(), (ball.getY()+(2*BALL_RADIUS)));
		}	
		if(getElementAt((ball.getX() + (2*BALL_RADIUS)), (ball.getY() + (2*BALL_RADIUS))) != null) {
			return getElementAt(ball.getX() + (2*BALL_RADIUS), (ball.getY() + (2*BALL_RADIUS)));
		} else { 
			return null;
		}
	}


	/*This method is the setup portion of the game, in which the bricks are built, the paddle is built, and mouse
	 listeners are added so the paddle responds to the movement of the mouse. */
	private void setUp() {
		buildBricks();
		addMouseListeners();
		buildPaddle();
	}

	//The following four methods give information about whether the ball has hit the walls.
	private boolean hitTopWall(GOval ball) {				
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <=0;
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/*This method uses a for loop and a counter to lay out the bricks. It also counts the bricks,
	 * which will be utilized later in the code to find out when the bricks are all gone. */
	private void buildBricks() {
		int count = 0;
		for (int row = 0; row<NBRICK_ROWS; row++) {
			count++;
			for (int column = 0; column<NBRICK_COLUMNS; column++) {
				double x = ((getWidth()-(((BRICK_WIDTH+BRICK_SEP)*NBRICK_COLUMNS)-BRICK_SEP))/2) + column*(BRICK_WIDTH+BRICK_SEP);
				double y = BRICK_Y_OFFSET + row*(BRICK_HEIGHT+BRICK_SEP);
				brick = new GRect (x,y,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				if(count==1 || count==2) {
					brick.setColor(Color.RED);
				}
				if(count==3 || count==4) {
					brick.setColor(Color.ORANGE);
				}
				if(count==5 || count==6) {
					brick.setColor(Color.YELLOW);
				}
				if(count==7 || count==8) {
					brick.setColor(Color.GREEN);
				}	
				if(count==9 || count==10) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}

	//This mouse event controls the paddle, allowing the paddle to move with the mouse's location. 
	public void mouseMoved (MouseEvent e) {
		double x = e.getX()-(PADDLE_WIDTH*0.5);
		double y = getHeight()-(PADDLE_Y_OFFSET+PADDLE_HEIGHT);
		paddle.setLocation(x,y);
		add(paddle);
		if((paddle.getX()+PADDLE_WIDTH)>getWidth()) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, y);
		} else {
			if((paddle.getX())<0) {
				paddle.setLocation(0,y);
			}
			x = e.getX()-(PADDLE_WIDTH*0.5);
		}
	}

	//This method builds a paddle and returns it to be controlled by the mouse.
	private GRect buildPaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	//This method sets up the ball, adding it to the screen and returning it to be used in the run method. 
	public GOval setUpBall() {
		double x = (getWidth()/2)-BALL_RADIUS;
		double y = (getHeight()/2)-BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball, (getWidth()/2)-BALL_RADIUS, getHeight()/2);
		return ball;
	}

	//This method sets up a method called brick that can be used in multiple other methods. 
	private GRect brick;

	//Method for the sound 
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
}
