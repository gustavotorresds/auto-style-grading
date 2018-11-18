/*
 * File: Breakout.java
 * -------------------
 * Name: Jennalei Louie
 * Section Leader: Chase Davis
 * 
 * This file will eventually implement the game of Breakout.
 *
 * Overview: The program creates many bricks in a pattern of colors.
 * A paddle is created and a ball is created.
 * The paddle follows the horizontal movement of the mouse.
 * The goal of the user is to hit the bricks with the ball.
 * When a brick is hit, the brick is removed.
 * The user has three chances win the game.
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

	private GRect paddle = null;
	private GOval ball = null;
	private GRect brick = null;
	private GLabel points = new GLabel("");
	
	private RandomGenerator rg = new RandomGenerator();
	
	private double vx, vy;
	
	private int brickTotal = NBRICK_COLUMNS * NBRICK_ROWS;
	
	private int initialBrickTotal = brickTotal;
	
	//private double colliderX, colliderY;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		vx = rg.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rg.nextBoolean(0.5))	vx = -vx;
		vy = VELOCITY_Y;

		setUpBricks();
		addMouseListeners();
		paddle = makePaddle();
		gameMechanism();
	}
	
	/**Game Mechanism, with click to start the game.
	 * if there are still lives (i.e. turns), a ball is made,
	 * the brick score is kept, and the vertical velocity changes direction,
	 * if a brick touches the ball (hence all the or statements).
	 * Then, if there is a collision with an object, the object (brick)
	 * is removed from the canvas.
	 */
	private void gameMechanism() {	
		clickToStart();
		
		//set up amount of lives.
		for(int lives = 0; lives < NTURNS; lives++) {
			//recreates ball.
			ball = makeBall();
			brickScore();
			//changes vy direction and checks for collision to remove object.
			//while loop ends if bottom wall is hit or when there are no more bricks.
			while(!hitBottomWall(ball) && brickTotal != 0) {
				double colliderX1 = ball.getX();
				double colliderX2 = ball.getX() + ball.getWidth();
				double colliderX3 = ball.getX() + ball.getWidth() / 2;
				double colliderY1 = ball.getY();
				double colliderY2 = ball.getY() + ball.getHeight();
				if(hitLeftWall(ball) || hitRightWall(ball)){
						//|| hitCollider(ball, colliderX1, colliderY1)
						//|| hitCollider(ball, colliderX2, colliderY1)) {
					vx = -vx;
				//creation of awful or statement to change vy direction.
				} else if(hitTopWall(ball)
						//top left corner of ball hit.
						|| hitCollider(ball, colliderX1, colliderY1) 
						//bottom left corner of ball hit.
						|| hitCollider(ball, colliderX1, colliderY2) 
						//top right corner of ball hit.
						|| hitCollider(ball, colliderX2, colliderY1) 
						//bottom right corner of ball hit.
						|| hitCollider(ball, colliderX2, colliderY2)
						//middle top of ball hit.
						|| hitCollider(ball, colliderX3, colliderY1) 
						//middle bottom of ball hit.
						|| hitCollider(ball, colliderX3, colliderY2)){
					vy = -vy;
					//checks for brick to remove it.
					checkForCollision(colliderX1, colliderY1);
					checkForCollision(colliderX1, colliderY2);
					checkForCollision(colliderX2, colliderY1);
					checkForCollision(colliderX2, colliderY2);
					checkForCollision(colliderX3, colliderY1);
					checkForCollision(colliderX3, colliderY2);
				}
				ball.move(vx, vy);
				pause(DELAY);
			}
			//removes ball at end of game.
			remove(ball);
			terminationMessage(lives);
			remove(points);
		}
		remove(paddle);
	}
	
	//displays continue message if lives still available.
	//displays game over message if dead.
	//displays you win message if all bricks are gone.
	private void terminationMessage(int lives) {
		if(brickTotal != 0) {
			if(lives < (NTURNS - (NTURNS - 2))) {
				clickToContinue();
			} else if (lives < NTURNS) {
				addGameOverLabel();
			}
		} else {
		youWinLabel();	
		remove(paddle);
		}
	}
	
	//creates the score label.
	//if score is divisible by 10, vy increases by 1.
	private void brickScore(){
		int score = initialBrickTotal - brickTotal;
		points.setFont("Courier-24");
		points.setLabel("Score: " + score);
		add(points, getWidth() / 10, BRICK_Y_OFFSET / 2 + points.getHeight() / 4);
		if(score % 10 == 0) {
			vy++;
		}
	}
	
	//creates game over label.
	private void addGameOverLabel() {
		GLabel gameOverLabel = new GLabel("Game Over! ):");
		gameOverLabel.setFont("Courier-24");
		add(gameOverLabel, getWidth() / 2 - gameOverLabel.getWidth() / 2, getHeight() / 2);
	}
	
	//creates you win label.
	private void youWinLabel() {
		GLabel gameOverLabel = new GLabel("Yay! You Win! (:");
		gameOverLabel.setFont("Courier-24");
		add(gameOverLabel, getWidth() / 2 - gameOverLabel.getWidth() / 2, getHeight() / 2);
	}
	
	//creates a click to continue label.
	//game continues when mouse is clicked.
	private void clickToContinue() {
		GLabel clickToContinue = new GLabel("Click Anywhere To Continue!");
		clickToContinue.setFont("Courier-24");
		add(clickToContinue, getWidth() / 2 - clickToContinue.getWidth() / 2, getHeight() / 2);
		waitForClick();
		remove(clickToContinue);
	}
	
	//creates a click to start label.
	//game starts when mouse is clicked.
	private void clickToStart() {
		GLabel clickToStart = new GLabel("Click Anywhere To Start!");
		clickToStart.setFont("Courier-24");
		add(clickToStart, getWidth() / 2 - clickToStart.getWidth() / 2, BRICK_Y_OFFSET / 2 + clickToStart.getHeight() / 4);
		
		GLabel instructions = new GLabel("You have 3 lives to break all the bricks.");
		instructions.setFont("Courier-15");
		add(instructions, getWidth() / 2 - instructions.getWidth() / 2, getHeight() / 2 + instructions.getHeight() / 2);
		
		GLabel moreInstructions = new GLabel("Good luck!");
		moreInstructions.setFont("Courier-15");
		add(moreInstructions, getWidth() / 2 - moreInstructions.getWidth() / 2, getHeight() / 2 + moreInstructions.getHeight() * 2);
		
		waitForClick();
		remove(clickToStart);
		remove(instructions);
		remove(moreInstructions);
	}
	
	//checks for collision by seeing if GObject is null.
	//if ball collides with brick (not paddle, not points)
	private void checkForCollision(double colliderX, double colliderY) {
		if(hitCollider(ball, colliderX, colliderY)) {
			GObject collider = getCollidingObject(colliderX, colliderY);
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			if(collider != null && collider != points) {
				if(collider != paddle) {
						bounceClip.play();
						remove(collider); 
						brickTotal--;
						remove(points);
						brickScore();
				}
			}
		}
	}
	
	//returns collided object to remove if necessary.
	private GObject getCollidingObject(double colliderX, double colliderY) {
		GObject collider = getElementAt(colliderX, colliderY);
		return collider;
	}
	
	//used to determine if the ball collided with something.
	private boolean hitCollider(GOval ball, double colliderX, double colliderY) {	
		GObject hitCollider = getElementAt(colliderX, colliderY);
		return hitCollider != null;
	}
	
	//creates the ball every turn
	public GOval makeBall() {
		double centerX = (getWidth() / 2) - BALL_RADIUS;
		double centerY = (getHeight() / 2) - BALL_RADIUS;		
		double size = BALL_RADIUS * 2;
		GOval ball = new GOval(size, size);
		ball.setFilled(true);
		add(ball, centerX, centerY);
		return ball;
	}
	
	//used to determine if ball touched bottom wall.
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}
	
	//used to determine if ball touched top wall.
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	//used to determine if ball touched right wall.
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	
	//used to determine if ball touched left wall.
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	//moves paddle to follow mouse.
	public void mouseMoved(MouseEvent e) {
			double minX = (PADDLE_WIDTH / 2);
			double maxX = getWidth() - (PADDLE_WIDTH / 2);
			double mouseX = e.getX();
			double paddleX = mouseX - (PADDLE_WIDTH / 2);
			double paddleY = getHeight() - PADDLE_Y_OFFSET;
		if(mouseX >= minX && mouseX <= maxX) {
			paddle.setLocation(paddleX, paddleY);
		}
		add(paddle);
	}
	
	//creates the paddle.
	private GRect makePaddle() {
		double initialPaddleX = PADDLE_WIDTH / 2;
		double initialPaddleY = getHeight() - PADDLE_Y_OFFSET;
		GRect paddle = new GRect(initialPaddleX, initialPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	//sets up rows of bricks, with the color depending on the row number.
	private void setUpBricks() {
		//for loop to create row with a certain color.
		//the remainder determines the nth number of row in the color sequence.
		for(int rowNumber = 0; rowNumber < NBRICK_ROWS; rowNumber++) {
			//if row number is first or second in the sequence, color is red.
			if(rowNumber % 10 == 0 || rowNumber % 10 == 1) {
				makeRow(Color.RED, rowNumber);
			//if row number is third or four in the sequence, color is orange.
			} else if (rowNumber % 10 == 2 || rowNumber % 10 == 3) {
				makeRow(Color.ORANGE, rowNumber);
			//if row number is fifth or sixth in the sequence, color is yellow.
			} else if (rowNumber % 10 == 4 || rowNumber % 10 == 5) {
				makeRow(Color.YELLOW, rowNumber);
			//if row number is seventh or eighth in the sequence, color is green.
			} else if (rowNumber % 10 == 6 || rowNumber % 10 == 7) {
				makeRow(Color.GREEN, rowNumber);
			//if row number is ninth or tenth in the sequence, color is cyan.
			} else if (rowNumber % 10 == 8 || rowNumber % 10 == 9) {
				makeRow(Color.CYAN, rowNumber);
			}
		}
	}

	//makes a row given a color and a row number.
	private void makeRow(Color color, int rowNumber) {
		//for loop to repeat creation of each brick to the number of columns (i.e. 10).
		for(int brickMadePerRow = 0; brickMadePerRow < NBRICK_COLUMNS; brickMadePerRow++) {
			
			//determines the first x coordinate to center the set of bricks.
			double centerRowXCoord = (getWidth() / 2) + (BRICK_SEP / 2) - (NBRICK_COLUMNS / 2) * (BRICK_SEP + BRICK_WIDTH);
			//determines the x & y coordinates of bricks in the row.
			double brickXCoord = centerRowXCoord + (BRICK_SEP + BRICK_WIDTH) * brickMadePerRow;
			double brickYCoord = BRICK_Y_OFFSET + (BRICK_SEP + BRICK_HEIGHT) * rowNumber;
			
			//creates each brick and adds it to screen.
			brick = new GRect(brickXCoord, brickYCoord, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setColor(color);
			brick.setFilled(true);
			add(brick);
		}
	}
	
}
