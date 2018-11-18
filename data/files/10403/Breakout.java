/*
 * File: Breakout.java
 * -------------------
 * Name: Hope Harrington
 * Section Leader: Maggie Davis 
 * 
 * This program generates and plays the Breakout game.
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

	//Creates the paddle, generates game, and creates label displaying instructions for the game.
	//Loops through the game three times. Creates labels stating that the player has 
	//lost the game, won the game, and lost one of the three rounds.
	double XStartingBrickOrigin;
	double YStartingBrickOrigin;
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createAndAddPaddle();
		addMouseListeners();
		addStartLabelThenRemove();
		while (true) {
			double numberOfTimesLost = 0;
			for(int q=0; q<3; q++) {
				waitForClick();
				generateWorldAndPlayGame();
				if(ifGameWon==true) {
					addGameWonLabel();
					break;
				}else if(numberOfTimesLost < 2) {
					addLostRoundLabelThenRemove ();
					numberOfTimesLost=numberOfTimesLost+1;
				}
			}
			if (numberOfTimesLost==2) {
				addLabelLoseGame();
			}
		}
	}

	//Adds mouseMoved event to set the x-coordinate of the paddle to the mouse x-coordinate.
		public void mouseMoved(MouseEvent e) {
			if (0 < e.getX() && e.getX()+PADDLE_WIDTH < CANVAS_WIDTH) {
				paddle.setX(e.getX());
			}
		}
		
	//Creates the paddle.
	private GRect paddle;
	
	//Creates and adds paddle.
	private void createAndAddPaddle () {
		double paddleStartingX = getWidth()/2.0-PADDLE_WIDTH/2.0;
		double paddleStartingY = getHeight()-PADDLE_Y_OFFSET;	
		paddle = new GRect(paddleStartingX, paddleStartingY, PADDLE_WIDTH, PADDLE_HEIGHT);
		add(paddle);
		paddle.setFilled(true);
	}
	
	//Adds and removes start label.
	private void addStartLabelThenRemove () {
		GLabel labelStart= new GLabel("YOU HAVE 3 TRIES TO WIN. DOUBLE CLICK TO START!");
		double xLabel = getWidth()/2-labelStart.getWidth()/2;
		double yLabel= CANVAS_HEIGHT/2-labelStart.getAscent()/2;
		labelStart.setLocation(xLabel,yLabel);
		add(labelStart);
		waitForClick();
		remove(labelStart);
	}
	
	//Adds label stating that game has been won.
	private void addGameWonLabel () {
		GLabel labelWon= new GLabel("CONGRATS! YOU WON! DOUBLE CLICK TO RESTART.");
		double xLabelWon = getWidth()/2-labelWon.getWidth()/2;
		double yLabelWon= CANVAS_HEIGHT/2-labelWon.getAscent()/2;
		labelWon.setLocation(xLabelWon,yLabelWon);
		add(labelWon);
		waitForClick();
		remove(labelWon);
	}
	
	//Adds label stating lost one of three rounds and instructions.
	private void addLostRoundLabelThenRemove () {
		GLabel labelLostRound= new GLabel("YOU LOST THIS ROUND. DOUBLE CLICK TO TRY AGAIN!");
		double xLabelLostRound = getWidth()/2-labelLostRound.getWidth()/2;
		double yLabelLostRound= CANVAS_HEIGHT/2-labelLostRound.getAscent()/2;
		labelLostRound.setLocation(xLabelLostRound,yLabelLostRound);
		add(labelLostRound);
		waitForClick();
		remove(labelLostRound);
	}
	
	//Adds label stating lost the game (three rounds).
	private void addLabelLoseGame () {
		GLabel labelLost= new GLabel("THAT'S 3 TRIES! YOU LOST :( DOUBLE CLICK TO RESET THE GAME");
		double xLabelLost = getWidth()/2-labelLost.getWidth()/2;
		double yLabelLost= CANVAS_HEIGHT/2-labelLost.getAscent()/2;
		labelLost.setLocation(xLabelLost,yLabelLost);
		add(labelLost);
		waitForClick();
		remove(labelLost);
	}
	
	//Builds the bricks in the alternating color scheme 2 red, 2 orange, 2 yellow, 2 green, 2 cyan.
	private void buildBricks(double XOriginBrick, double YOriginBrick) {
		double originalXValue = XOriginBrick;
		double rowNumber = 1;
		for (int j=0; j<NBRICK_ROWS; j++) {
			for(int i=0; i<NBRICK_COLUMNS; i++) {
				GRect rect = new GRect (XOriginBrick, YOriginBrick, BRICK_WIDTH, BRICK_HEIGHT);
				add(rect);
				rect.setFilled(true);
				if(rowNumber % 10 == 1 || rowNumber % 10 == 2) {
					rect.setColor(Color.RED);
				}
				if(rowNumber % 10 == 3 || rowNumber % 10 == 4) {
					rect.setColor(Color.ORANGE);
				}
				if(rowNumber % 10 == 5 || rowNumber % 10 == 6) {
					rect.setColor(Color.YELLOW);
				}
				if(rowNumber % 10 == 7 || rowNumber % 10 == 8) {
					rect.setColor(Color.GREEN);
				}
				if(rowNumber % 10 == 9 || rowNumber % 10 == 0) {
					rect.setColor(Color.CYAN);
				}
				XOriginBrick = XOriginBrick + BRICK_WIDTH + BRICK_SEP;
			}
			rowNumber = rowNumber+1;
			XOriginBrick = originalXValue;
			YOriginBrick = YOriginBrick + BRICK_HEIGHT + BRICK_SEP; 
		}
	}
	
	//Generates the start of the game by building the wall of bricks and creating the ball and make
	//the ball move.
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private void initializeGame () {
		XStartingBrickOrigin = ((getWidth()/2.0)-(BRICK_SEP+BRICK_WIDTH)*NBRICK_COLUMNS/2.0)+2.0;
		YStartingBrickOrigin = BRICK_Y_OFFSET;
		buildBricks(XStartingBrickOrigin,YStartingBrickOrigin);
		ball = new GOval (getWidth()/2.0-BALL_RADIUS, getHeight()/2.0, BALL_RADIUS*2.0, BALL_RADIUS*2.0);
		add(ball);
		ball.setColor(Color.RED);
		ball.setFilled(true);
		vx = rgen.nextDouble(1.0, 3.0);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	
	//Checks to see if ball is within the left, right, and top boundaries and then 
	//makes the ball shift in velocity direction if ball hits one of the boundaries. 
	private void checkIfBallWithinTopLeftRightBoundaries () {
		if (checkIfHitLeftWall(ball)||(checkIfHitRightWall(ball))){
			vx = -vx;
		}
		if (checkIfHitTopWall(ball)){
			vy = -vy;
		}
	}

	//Create ball and make it move. Removes bricks. Keeps within boundaries and check for collisions
	//with the walls and paddle. 
	//and losing.
	private boolean ifGameWon;
	private void generateWorldAndPlayGame() {
		double numberOfBricks = NBRICK_ROWS*NBRICK_COLUMNS;
		initializeGame();
		while (true) {
			checkIfBallWithinTopLeftRightBoundaries();
			getCollidingObject();
			GObject collider = getCollidingObject();
			if(collider==paddle) {
				if (ball.getY()>=paddle.getY()) {
					ball.setLocation(paddle.getX(), paddle.getY()-BALL_RADIUS*2-VELOCITY_Y);
				}
				vy = -vy;
			} else if(collider!=ball) {
				remove(collider);
				numberOfBricks=numberOfBricks-1;
				vy = -vy;
			}
			ball.move(vx, vy);
			if (checkIfHitBottomWall(ball)) {
				removeAll();
				add(paddle);
				ifGameWon=false;
				break;
			}else if(numberOfBricks==0) {
				removeAll();
				add(paddle);
				ifGameWon=true;
				break;
			}
			pause(DELAY/2);
			}
	}

	//Checks to see if ball is within the 4 walls.
	private boolean checkIfHitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}
	private boolean checkIfHitBottomWall(GOval ball) {
		return ball.getY() > getHeight();
	}
	private boolean checkIfHitRightWall (GOval ball) {
		return ball.getX()+BALL_RADIUS*2 > getWidth();
	}
	private boolean checkIfHitTopWall (GOval ball) {
		return ball.getY() < 0;
	}

	//Checks to see if ball has collided with an object at any of the 4 points and returns that object if 
	//the ball has indeed collided with an object (value is not equal to null). If none of the 4 points have collided, then 
	//the program returns the ball.
	private GObject getCollidingObject() {
		GPoint bottomRightPointBall = new GPoint ((ball.getX()+BALL_RADIUS*2), (ball.getY()+BALL_RADIUS*2));
		GPoint topRightPointBall = new GPoint ((ball.getX()+BALL_RADIUS*2), ball.getY());
		GPoint bottomLeftPointBall = new GPoint (ball.getX(), ball.getY()+(BALL_RADIUS*2));
		GPoint ballOrigin = new GPoint (ball.getX(), ball.getY());
		getElementAt(ballOrigin);
		if (getElementAt(ballOrigin) != null) {
			return getElementAt(ballOrigin); 
		} else if (getElementAt(bottomLeftPointBall) != null) {
			return getElementAt(bottomLeftPointBall);
		} else if(getElementAt(topRightPointBall) != null) {
			return getElementAt(topRightPointBall);
		} else if(getElementAt(bottomRightPointBall) != null) {
			return getElementAt(bottomRightPointBall);
		} else{
			return (ball);
		}
	}
}

