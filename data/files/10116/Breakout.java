/*
 * File: Breakout.java
 * -------------------
 * Name: Alicia Hu
 * Section Leader: Rhea Karuturi
 * 
 * This class implements the game of Breakout.
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
	
	private GRect paddle;
	private double paddleX;
	private double paddleY;
	private GLabel scoreCount;
	private double scoreCountX;
	private double scoreCountY;
	private GOval ball;
	private double vx;
	private double vy = 3.0;
	private RandomGenerator rgen = new RandomGenerator();
	private int numBricks = NBRICK_ROWS * NBRICK_COLUMNS;
	private int paddleHits;
	private int score;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setUpGame();
		playGame();
	
	}

	/*
	 * creates rows of bricks and the paddle that will be used to play the game
	 */
	private void setUpGame() {
		buildBricks();
		buildPaddle();
		buildScoreCounter();
	}

	/*
	 * starts the game and gives the player the number of turns specified by 
	 * NTURNS, stopping the game when all the bricks are hit or all the turns 
	 * are used up
	 */
	private void playGame() {
		for (int i = 0; i < NTURNS; i++) {
			if (numBricks > 0) {
				playTurn();
			}
		}
		if (numBricks == 0) {
			displayWin();
		} else {
			displayLost();
		}
	}
	
	/*
	 * displays "Game Over." if all turns are played and there are bricks
	 * still remaining on the screen.
	 */
	private void displayLost() {
		GLabel lost = new GLabel("Game Over. Score: " + score);
		lost.setFont("Courier-24");
		lost.setColor(Color.MAGENTA);
		addCenteredText(lost);
	}

	/*
	 * displays "You won!" if all bricks are cleared from the screen.
	 */
	private void displayWin() {
		GLabel win = new GLabel("You won! Score: " + score);
		win.setFont("Courier-24");
		win.setColor(Color.MAGENTA);
		addCenteredText(win);
	}
	
	/*
	 * adds the label that is passed into the method to the center of the screen.
	 */
	private void addCenteredText(GLabel text) {
		double centerX = getWidth() / 2 - text.getWidth() / 2;
		double centerY = getHeight() / 2 + text.getHeight() / 2;
		add(text, centerX, centerY);
	}


	/*
	 * creates rows of bricks as specified by constants
	 */
	private void buildBricks() {
		double startY = BRICK_Y_OFFSET;
		for (int i = 0; i < NBRICK_ROWS; i++) {
			double startX = (getWidth() - (NBRICK_ROWS * BRICK_WIDTH + (NBRICK_ROWS - 1) * BRICK_SEP)) / 2; 
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick = colourInBrick(brick, i);
				add(brick, startX, startY);
				startX += BRICK_WIDTH + BRICK_SEP; 
			}
			startY += BRICK_HEIGHT + BRICK_SEP;
		}
		
	}
	
	/*
	 * sets colour of bricks 
	 */
	private GRect colourInBrick(GRect brick, int n) {
		if (n < 2) {
			brick.setColor(Color.RED);
		} else if (n < 4) {
			brick.setColor(Color.ORANGE);
		} else if (n < 6) {
			brick.setColor(Color.YELLOW);
		} else if (n < 8) {
			brick.setColor(Color.GREEN);
		} else {
			brick.setColor(Color.CYAN);
		}
		return brick;
	}
	
	/*
	 * creates paddle
	 */
	private void buildPaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddleY = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, paddleX, paddleY);
	}
	
	/*
	 * creates a score counter
	 */
	private void buildScoreCounter() {
		scoreCount = new GLabel("Score: " + score);
		scoreCount.setFont("Courier-12");
		scoreCount.setColor(Color.MAGENTA);
		scoreCountX = paddleX;
		scoreCountY = paddleY + PADDLE_HEIGHT + scoreCount.getHeight();
		add(scoreCount, scoreCountX, scoreCountY);
	}
	
	/*
	 * tracks x coordinate of mouse and sets the x coordinate of 
	 * the paddle and score counter according to mouse movement  
	 */
	public void mouseMoved (MouseEvent e){
		paddleX = e.getX();
		scoreCountX = e.getX();
	}

	/*
	 * gives the player a new ball to play with, and ends turn if the ball goes past 
	 * the bottom wall or if all the bricks are destroyed.
	 */
	private void playTurn() {
		displayNewTurn();
		buildBall();
		paddleHits = 0;
		while(!hitBottomWall(ball) && numBricks > 0) {
			animatePaddle();
			animateScore();
			animateBall();
			checkIfHit();
		}
		remove(ball);
	}
	
	/*
	 * displays a message that tells the user to click to begin the 
	 * next turn of the game. 
	 */
	private void displayNewTurn() {
		GLabel newTurn = new GLabel ("Click to begin turn.");
		newTurn.setFont("Courier-24");
		newTurn.setColor(Color.MAGENTA);
		addCenteredText(newTurn);
		waitForClick();
		remove(newTurn);
	}

	/*
	 * creates a ball and adds it to the canvas, giving it a random velocity 
	 * in the x direction between 1.0 and 3.0
	 */
	private void buildBall() {
		ball = new GOval (BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}
	
	/*
	 * animates the paddle to move horizontally 
	 */
	private void animatePaddle() {
		if (paddleX < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(paddleX, paddleY);
		} else {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, paddleY);
		}
	}
	
	/*
	 * animates score counter to follow under the paddle and increase
	 * the displayed score 
	 */
	private void animateScore() {
		scoreCount.setLabel("Score: " + score);
		if (scoreCountX < getWidth() - scoreCount.getWidth()) {
			scoreCount.setLocation(scoreCountX, scoreCountY);
		} else {
			scoreCount.setLocation(getWidth() - scoreCount.getWidth(), scoreCountY);
		}
	}
	
	/*
	 * animates the ball so it will bounce off the side and top 
	 * edges of the canvas
	 */
	private void animateBall() {
		if (hitSideWall(ball)) {
			vx = -vx;
		} else if (hitTopWall(ball)) {
			vy = -vy;
		}
		ball.move(vx, vy);
		pause(DELAY);
	}
	
	/*
	 * returns true when the ball goes past the bottom edge of the canvas
	 */
	private boolean hitBottomWall(GOval ball) {
		return (ball.getY() + BALL_RADIUS * 2) >= getHeight();
	}
	
	/*
	 * returns true when the ball reaches either side of the canvas
	 */
	private boolean hitSideWall(GOval ball) {
		return ball.getX() <= 0 || (ball.getX() + BALL_RADIUS * 2) >= getWidth();
	}
	
	/*
	 * returns true when the ball reaches the top edge of the canvas
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/*
	 * if ball hits paddle or brick, bounces ball back,
	 * and removes brick if it is hit.
	 */
	private void checkIfHit() {
		GObject collider = getCollidingObject();
		if (hitPaddle(collider)) {
			paddleHits++;
			bounceBack();
		} else if (hitBrick(collider)) {
			numBricks--;
			changeScore(collider);
			bounceBack();
			remove(collider);
		}
	}
	
	/*
	 * returns a GObject that stores any object the ball hits or stores
	 * null if no objects have been hit
	 */
	private GObject getCollidingObject() { 
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider == null) {
			collider = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		} 
		if (collider == null) {
			collider = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}
		if (collider == null) {
			collider = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		}
		return collider;
	}
	
	/*
	 * plays bounce sound when ball hits paddle or brick, bounces ball back
	 * and increases the speed every 8th hit with the paddle
	 */
	private void bounceBack() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
		if (paddleHits == 8) {
			vy *= -1.5;
			paddleHits = 0;
		} else {
			vy = -vy;
		}
	}
	
	/*
	 * increases the score count according to the colour of the brick hit.
	 */
	private void changeScore(GObject collider) {
		if (collider.getColor() == Color.RED) {
			score += 5;
		} else if (collider.getColor() == Color.ORANGE) {
			score += 4;
		} else if (collider.getColor() == Color.YELLOW) {
			score += 3;
		} else if (collider.getColor() == Color.GREEN) {
			score += 2;
		} else {
			score += 1;
		}
	}
	
	
	/*
	 * returns true if ball hits the paddle
	 */
	private boolean hitPaddle(GObject collider) {
		return collider == paddle && ball.getY() < (paddleY - PADDLE_HEIGHT);
	}
	
	/*
	 * returns true if ball hits a brick
	 */
	private boolean hitBrick(GObject collider) {
		return collider != null && collider != paddle;
	}

}
