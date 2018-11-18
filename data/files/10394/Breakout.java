/*
 * File: Breakout.java
 * -------------------
 * Name: Doris Rodriguez
 * Section Leader: Drew Bassilakis
 * 
 * This file will allow the user to attempt win the game break out in three
 * tries. The objective of the game is to use a paddle and a ball to knock
 * out all of the bricks in a wall above it.
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
	public static int NTURNS = 3;

	// making the paddle an instance variable allows me to make changes to it while
	// using a mouseListener (since mouse listeners can't pass parameters)

	GRect paddle = null;

	//making the ball an instance variable allows for us to use it in our animation loop

	GOval ball = null;

	// instance variable rgen will serve as random number generator. 

	private RandomGenerator rgen = RandomGenerator.getInstance();

	//These instance variable are used to update the changing x & y locations of our ball

	double vx;
	double vy;

	//Keeps track of bricks removed, should not be double!
	int BRICK_COUNT = 0;

	//Needs to be instance so it can update the score on the screen
	// Extension

	private GLabel score = new GLabel("");

	//Extension: KEEPS track of Lives Left on Screen
	private GLabel lives = new GLabel("");

	//Extension: Gives you a LOSERS or WINNERS Screen
	private GLabel loserScreen = new GLabel ("");
	private GLabel winnersScreen = new GLabel ("AYYYYYY U R a WINNER! ");

	public void run() {


		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//Create our world
		buildWorld();

		// Have to add mouse listener
		addMouseListeners();

		//Interactions in our world
		//1.create the ball
		//ball = makeBall();

		//2.MAKE the ball move
		waitForClick();
		ball = makeBall();
		vx =  rgen.nextDouble(1.0, 3.0); 
		vy =  - VELOCITY_Y; 
		
		//3. Give the ball Direction
		if (rgen.nextBoolean(0.5)) 
			vx = -vx; 
		
		//PLAY GAME with ball (Animation LOOP)
		int livesLeft = NTURNS;
		while (true) {
			// LIFE LINE
			//int livesLeft = NTURNS;
			ball.move(vx, vy);	
			
			//4. make sure that ball bounces off of walls
			double mathForBallLeftEdge = ball.getX() + ( BALL_RADIUS * 2 );
			double mathForRightEdge = (NBRICK_COLUMNS * (BRICK_SEP + BRICK_WIDTH));
			if (mathForBallLeftEdge > mathForRightEdge) {
				vx = -vx;
				//ball will move towards top Left of screen if it hits right wall
			}
			if (ball.getX() < BRICK_SEP) {	
				vx = Math.abs(vx);
				//ball will move toward top Right of screen if it hits left wall
			}
			if (ball.getY() <= 0) {
				vy = VELOCITY_Y;
				// ball should bounce back from top of screen
			}

			// 5. get ball to bounce off of the paddle
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy = -VELOCITY_Y;
			}

			//lose a life if ball goes past paddle 
			//removes ball
			//resets with new ball
			double lossLine = getHeight() - PADDLE_Y_OFFSET;
			if (ball.getY() >= lossLine) {
				livesLeft = livesLeft -1;
				remove(ball);
					waitForClick();
					ball = makeBall();
					vx =  rgen.nextDouble(1.0, 3.0); 
					vy =  - VELOCITY_Y; 
					if (rgen.nextBoolean(0.5)) 
						vx = -vx; 
			}
			


			//6. get ball to tear down wall by taking bricks off 
			double mathForBottomOfWall = (BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * (NBRICK_COLUMNS + 1))) ;
			if (collider != null && collider.getY() < mathForBottomOfWall) {
				vy = VELOCITY_Y;
				remove(collider);
				BRICK_COUNT ++;
			}

			//KEEPS TRACK OF SCORE ON SCREEN
			score.setLabel("YOUR SCORE IS " + BRICK_COUNT);
			score.setColor(Color.BLUE);
			double x = 50;
			double y = getHeight() - PADDLE_Y_OFFSET + (PADDLE_HEIGHT*2.5) ;
			add(score, x, y);

			// KEEPS TRACK OF LIVES LEFT ON SCREEN
			lives.setLabel( livesLeft + " LIVES");
			lives.setColor(Color.RED);
			double xL = x + 200;
			double yL = getHeight() - PADDLE_Y_OFFSET + (PADDLE_HEIGHT*2.5) ;
			add(lives, xL, yL);
			
			
			//LOSERS
			if (livesLeft == 0) {
				removeAll();
				loserScreen.setLabel("YOU ONLY SCORED " + BRICK_COUNT + " POINTS HOMIE" );
				loserScreen.setFont("Courier-16");
				loserScreen.setColor(Color.BLACK);
				add(loserScreen,PADDLE_WIDTH,getHeight()/2);
				break;
			}

			//WINERS
			int totalBricks = NBRICK_COLUMNS * NBRICK_ROWS;
			;
			if (BRICK_COUNT == totalBricks) {
				removeAll();
				winnersScreen.setFont("Courier-22");
				winnersScreen.setColor(Color.BLACK);
				add(winnersScreen,PADDLE_WIDTH,getHeight()/2);
				break;
			}


			// Pause ALWAYS needed
			pause(DELAY);
		}

	}




	private GObject getCollidingObject() {
		GObject collider = null;
		double X = ball.getX();
		double Y = ball.getY();
		double rX = X + (BALL_RADIUS * 2);
		//bottom right X && top right X
		double bY = Y + (BALL_RADIUS * 2);
		//bottom right Y && bottom left Y
		if (getElementAt ( X, Y) != null) {
			collider = getElementAt(X,Y);
			return(collider);
		}
		if (getElementAt ( X, bY) != null) {
			collider = getElementAt(X,bY);
			return(collider);
		}

		if (getElementAt ( rX, bY) != null) {
			collider = getElementAt(rX,bY);
			return(collider);
		}
		if (getElementAt ( rX, Y) != null) {
			collider = getElementAt(X,Y);
			return(collider);
		}
		return collider;


	}


	void buildWorld() {
		buildWall();
		paddle = makePaddle();
		add(paddle);
	}



	//1. figured out how to code one brick
	//2. Made an Initial row
	//3. Use 1&2 in a while loop to build wall	
	private void buildWall() {
		int rowNumber = 1;
		int bricksInColumn = NBRICK_COLUMNS;
		while (rowNumber < NBRICK_ROWS + 1) {
			double initialX = ((getWidth() - (NBRICK_COLUMNS * (BRICK_SEP + BRICK_WIDTH))) / 2) + (BRICK_SEP + BRICK_WIDTH);

			double initialY = BRICK_Y_OFFSET;
			double brickY = initialY + ((BRICK_HEIGHT + BRICK_SEP) * rowNumber);
			for (int i=0; i < bricksInColumn  ; i++) {
				double nextX = initialX + ((BRICK_WIDTH + BRICK_SEP) * (i-1));
				drawBricks(rowNumber,nextX,brickY);
			}
			rowNumber= rowNumber + 1;
			//for loop to end when wall is done


		}


	}

	//Colors bricks based on row number
	private void drawBricks (double row, double X, double Y) {
		GRect nextBrick = new GRect (X,Y,BRICK_WIDTH,BRICK_HEIGHT);
		if (row == 1 ||  row == 2) {
			nextBrick.setColor(Color.RED);
			nextBrick.setFilled(true);
			nextBrick.setFillColor(Color.RED);
			add (nextBrick);
			add (nextBrick);}
		if (row == 3 ||  row == 4){
			nextBrick.setColor(Color.ORANGE);
			nextBrick.setFilled(true);
			nextBrick.setFillColor(Color.ORANGE);
			add (nextBrick);
		}
		if (row == 5 ||  row == 6){
			nextBrick.setColor(Color.YELLOW);
			nextBrick.setFilled(true);
			nextBrick.setFillColor(Color.YELLOW);
			add (nextBrick);
		}
		if (row == 7 ||  row == 8){
			nextBrick.setColor(Color.GREEN);
			nextBrick.setFilled(true);
			nextBrick.setFillColor(Color.GREEN);
			add (nextBrick);
		}
		if (row == 9 ||  row == 10){
			nextBrick.setColor(Color.CYAN);
			nextBrick.setFilled(true);
			nextBrick.setFillColor(Color.CYAN);
			add (nextBrick);
		}

	}

	private GRect makePaddle() {
		double pY = getHeight() - PADDLE_Y_OFFSET;
		double pX = ((NBRICK_COLUMNS * (BRICK_SEP + BRICK_WIDTH)) / 2) - (BALL_RADIUS/2); 
		GRect paddle = new GRect (pX, pY, PADDLE_WIDTH,PADDLE_HEIGHT);
		makeBlack(paddle);
		return paddle;
	}

	public void mouseMoved(MouseEvent event) {
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		double X = event.getX();
		paddle.setLocation(X, paddleY);
		// our paddle ONLY moves left to right (moves by following mouse's coordinates)
		double mathForEdge =  (NBRICK_COLUMNS * (BRICK_SEP + BRICK_WIDTH));
		double mathForEdgeofPaddle = X + PADDLE_WIDTH;	
		if (mathForEdgeofPaddle  >= mathForEdge) {
			double rightX = mathForEdge - (PADDLE_WIDTH + BRICK_SEP);
			paddle.setLocation(rightX,paddleY);
		}
		if (X  < BRICK_SEP ) {
			double leftX = BRICK_SEP;
			paddle.setLocation(leftX,paddleY);
		}
		// creates restrictions for our paddle. Its has to remain on screen and be parallel 
		//	to our wall

	}


	private void makeBlack(GRect object) {
		object.setColor(Color.BLACK);
		object.setFilled(true);

	}

	private GOval makeBall() {
		double centeredX = ((NBRICK_COLUMNS * (BRICK_SEP + BRICK_WIDTH)) / 2) - (BALL_RADIUS/2); 
		double centeredY =getHeight() - (PADDLE_Y_OFFSET + (BALL_RADIUS*2));
		ball = new GOval (centeredX,centeredY,BALL_RADIUS,BALL_RADIUS);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
		return(ball);
		//returns ball so that run has access to it
	}



}
