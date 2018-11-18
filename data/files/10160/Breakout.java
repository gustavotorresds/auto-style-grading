/*
 * File: Breakout.java

 * -------------------
 * Name: Anna-Luisa Brakman
 * Section Leader: Julia Daniel
 * 
 * This file runs a program that implements the game of Breakout.
 * The game includes 3 turns for the user. The objective is to hit 
 * all of the bricks set up at the beginning of the program by the end 
 *of the game with the bouncing ball in order to remove the bricks.
 *
 *NOTE: A "*" on post conditions means that it is not executed until
 *the method is called within another method.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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

	//Variable for color of brick
	private Color cl;

	//Initializes the rectangle that serves as paddle
	private GRect paddle;

	//Initializes the oval the serves as ball
	private GOval ball;

	//Velocity of the ball, in x and y directions respectively
	private double vx, vy;

	//Initializes a random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Gives the total number of bricks at the start of the game
	private int brickCount = NBRICK_COLUMNS * NBRICK_ROWS;

//	//Initializes the bounce sound when the ball collides with an object
//	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

//	//Initializes the label at the beginning of every turn that
//	//instructs the user to click to make the ball move
//	GLabel labelMoveBall = new GLabel ("Click to move the ball!");

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		for (int i=0; i<NTURNS; i++) { //this for loop encompasses entire run method and establishes that the user has NTURNS number of turns
			setUpGame(); //sets up the game
			while (true) { //initiates animation loop
				if (isTopWall()) { //sets condition for when the ball hits the top wall
					vy = -vy; //reverses direction of ball (bounces off wall)
				} if (isRightWall() || isLeftWall()) { //sets condition for when the ball hits the right or left wall
					vx = -vx; //reverses direction of ball (bounces off wall)
				} if (isBottomWall()) { //sets condition for when ball hits bottom wall (resets)
					vx = 0;
					vy = 0;
					clear();
					break; //exits for loop is ball goes through bottom
				}
				ball.move(vx, vy); //moves ball
				pause (DELAY);
				getCollidingObject(); //test for an object under the ball, determines if it is brick or paddle and acts accordingly
				if (brickCount == 0 ) { //if the user removes all of the bricks before all attempts are up, exits the loop
					clear(); 
//					winningLabel(); //displays message that user wins!
					break;
				}
			}
			if (brickCount == 0 ) { // breaks again to exit next loop
				break;
			}
		}
//		if (brickCount > 0) { //if the user exhausts all attempts and brick count is greater than zero, the user loses
//			losingLabel();			
//		} 
	}

	/*This method calls various other methods to set up the game
	 * by building all the necessary pieces, adding mouse
	 * listeners, and instructing the user to click to
	 * begin the game
	 * Pre: none, post: game is set up and ball is moving
	 */
	private void setUpGame() {
		buildBricks();
		drawPaddle();
		addMouseListeners();
		drawBall();
		//labelMoveBall();
		waitForClick();
		//remove(labelMoveBall);
		moveBall();
	}

	/*This method builds one individual brick,
	 * fills it, and sets color cl.
	 * Pre: none, *post: brick is built and filled
	 */
	private void buildBrick(double x, double y) {
		GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		add(brick);
		brick.setFilled(true);
		brick.setColor(cl);
	}

	/*This method builds one row of bricks
	 * Pre: none, *post: one row of bricks is built,
	 * with constant NBRICK_COLUMNS number of bricks
	 */
	private void buildRow(double x, double y) {
		for (int i=0; i<NBRICK_COLUMNS; i++) {
			buildBrick(x, y);
			x += BRICK_WIDTH+BRICK_SEP;
		}
	}

	/*This method builds all of the bricks needed for setup in a game
	 * according to the constants assigned before run method
	 * Pre: none, *post: all bricks are built and colored using setColor
	 */
	private void buildBricks() {
		double x = (getWidth()-(NBRICK_COLUMNS*BRICK_WIDTH + (NBRICK_COLUMNS-1)*BRICK_SEP))/2;
		double y = BRICK_Y_OFFSET;
		for (int i=0; i<NBRICK_ROWS; i++) {
			setColor(i);
			buildRow(x,y);
			y += BRICK_HEIGHT+BRICK_SEP;
		}
	}

	/*This method sets a color for each row of bricks.
	 * Pre: bricks are filled black, *post: bricks are colored by row
	 */
	private void setColor (int i) {
		if (i == 0 || i == 1) {
			cl = Color.RED;
		} else if (i == 2 || i == 3) {
			cl = Color.ORANGE;
		} else if (i == 4 || i== 5) {
			cl = Color.YELLOW;
		} else if (i== 6 || i== 7) {
			cl =  Color.GREEN;
		} else {
			cl = Color.CYAN;
		}
	}

	/*This method builds and adds to the screen an x-centered paddle
	 *at the given y constant for paddle offset. 
	 *pre: bricks are drawn, *post: bricks and paddle are drawn
	 */
	private void drawPaddle() {
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2;
		double paddleY = getHeight() - (PADDLE_Y_OFFSET+PADDLE_HEIGHT);
		paddle = new GRect (paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	/*This mouse event tracks when the mouse moves, and assigns
	 * the x value of the paddle to the mouse so that paddle movement
	 * follows mouse movement.
	 * Pre:bricks and paddle are drawn, *post: paddle follows mouse movement
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		if ((mouseX <= getWidth()-PADDLE_WIDTH/2) && mouseX >= PADDLE_WIDTH/2) {
			paddle.setX(mouseX-PADDLE_WIDTH/2);
		}
	}

	/*This method draws a ball that is centered within the canvas
	 * and fills the ball with color (black). 
	 * Pre: the rest of the game is set up, *post: ball is drawn
	 */
	private void drawBall() {
		double ballX = getWidth()/2 - BALL_RADIUS;
		double ballY = getHeight()/2 - BALL_RADIUS;
		ball = new GOval (ballX, ballY, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}

	/*This method implements the ball's movement.
	 * It includes a random x generated within the range of given
	 * minimum and maximum x velocity constants, as well as the given y velocity.
	 * Pre: all of game is drawn, post: ball moves
	 */
	private void moveBall() {
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		ball.move(vx, vy);
	}

	/*This method returns the x value when the ball hits the top wall.
	 * Pre: N/A, post: N/A
	 */
	private boolean isTopWall() {
		return ball.getY() <= 0;
	}

	/*This method returns the x value when the ball hits the right wall.
	 * Pre: N/A, post: N/A
	 */
	private boolean isRightWall() {
		return ball.getX() + BALL_RADIUS*2 >= getWidth();
	}

	/*This method returns the x value when the ball hits the left wall.
	 * Pre: N/A, post: N/A
	 */
	private boolean isLeftWall() {
		return ball.getX() <= 0;
	}

	/*This method returns the y value when the ball hits the bottom wall.
	 * Pre: N/A, post: N/A
	 */
	private boolean isBottomWall() {
		return ball.getY() + BALL_RADIUS*2 > getHeight();
	}

	/*This method test for an object on the canvas at the x,y location
	 * of each of 4 corners of the ball, and proceeds to determine if 
	 * that objects is the paddle or a brick. If it is the paddle, the
	 * ball will bounce off the paddle (using absolute value, to
	 * avoid a "sticky paddle," whereas if it is a brick the ball 
	 * will bounce and remove the object.
	 * Pre: ball is moving, post: ball checks if object underneath it exists
	 * and proceeds to either bounce off it or remove and bounce of it if an object does exist
	 */
	private void getCollidingObject() {
		GObject maybeAnObject = null;
		if ((getElementAt(ball.getX(),ball.getY()) != null)) {
			maybeAnObject = getElementAt(ball.getX(),ball.getY());
		} else if ((getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()) != null)) {
			maybeAnObject = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY());
		} else if ((getElementAt(ball.getX(),ball.getY()+BALL_RADIUS*2) != null)) {
			maybeAnObject = getElementAt(ball.getX(),ball.getY()+BALL_RADIUS*2);
		} else if ((getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY()+BALL_RADIUS*2)) != null) {
			maybeAnObject = getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY()+BALL_RADIUS*2);
		}
		if (maybeAnObject == paddle) {
			//bounceClip.play();
			vy = -Math.abs(vy);
		} else if (maybeAnObject != null){
			vy = -vy;
			//bounceClip.play();
			remove(maybeAnObject);
			brickCount--;
		}
	}

	/*This label, initialized as an instance variable, tells the user to click to begin
	 * Pre: no label to start game, but game set up
	 * Post: Label telling user to click to start the game
	 */
//	private void labelMoveBall () {
//		labelMoveBall.setLocation ((getWidth()-labelMoveBall.getWidth())/2, getHeight()/2-labelMoveBall.getAscent()/2);
//		labelMoveBall.setFont("SansSerif-24");
//		labelMoveBall.setColor(Color.BLUE);
//		add(labelMoveBall);
//	}

	/*This method implements the label that is displayed
	 * if the user wins the game
	 * Pre: no label when user wins the game, post: label if the user wins
	 */
//	private void winningLabel () {
//		GLabel winningLabel = new GLabel ("yas girl u win! :)");
//		winningLabel.setLocation ((getWidth()-winningLabel.getWidth())/2, getHeight()/2-winningLabel.getAscent()/2);
//		winningLabel.setFont("SansSerif-24");
//		winningLabel.setColor(Color.ORANGE);
//		add(winningLabel);
//	}

	/*This method implements the label that is displayed
	 * if the user loses the game
	 * Pre: no label when user loses the game, post: label if the user loses
	 */
//	private void losingLabel () {
//		GLabel losingLabel = new GLabel ("lmao u lose");
//		losingLabel.setLocation ((getWidth()-losingLabel.getWidth())/2, getHeight()/2-losingLabel.getAscent()/2);
//		losingLabel.setFont("SansSerif-24");
//		losingLabel.setColor(Color.MAGENTA);
//		add(losingLabel);
//	}
}

