/*
 * File: Breakout.java
 * -------------------
 * Name:Kate Salmon
 * Section Leader: Garrick
 * 2/6/18
 * 
 * This file will eventually implement the game of Breakout.
 * 
 * Approach:
 * 1.Figured out how to lay bricks - took a "checkerboard" approach
 * 2.Created a method that put a brick on the screen and took parameter color, x, and y position
 * 3.Made the paddle and got it moving using mouse listeners
 * 4.Set the conditions for the paddle by checking the edge conditions so the paddle does not go off of the screen
 * 5.Created and set the ball in the middle of the screen
 * 6.Got the ball to move by using mouse listeners - mouse clicked
 * 7.Added ball conditions so it would stay contained within the screen except for when it hit the bottom wall
 * 8.Created a method called moveBall that moves the ball as long as the conditions hold
 * 9.Created a setUpGame method which consisted up laying the bricks on the screen, making and setting the paddle, and making
 * and setting the ball
 * 10.Made a check for collisions method that either removes a brick if the ball hits it or rebounds off the paddle if it hits the paddle
 * 11.Put all attributes into a playGame method which executes while the player has turns left and the brick count is not equal to 0
 * 12. Once these condition no longer hold the program prints a message saying whether or not the player won or lost the game.
 * 
 * Extensions:
 * 1.Audio Clip- makes sound every time ball hits a brick
 * 2.Ball changes to color of brick when it hits it
 * 3.Brick tracker- Displays how many bricks the player has left to go at the top of the screen
 * 4.Speeds up ball every time a player loses a turn
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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Creates paddle as instance variable
	private GRect paddle = null;

	//Create ball as instance variable
	private GOval ball = null;

	//Random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Keeps track of velocity of ball in the x direction
	private double vx;

	//Keeps track of the velocity of the ball in the y direction
	private double vy = VELOCITY_Y;

	//Keeps track of the number of bricks left during the game
	private int brickCount = NBRICK_ROWS*NBRICK_COLUMNS;

	//Keeps track of the number of turns used
	private int turn = 0;

	//Initializes the brick counter label displayed on the screen
	private GLabel counter;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//Sets up game by laying the bricks and placing the ball and paddle on the screen

		setUpGame();
		addMouseListeners();
		waitForClick();
		while(turn < NTURNS && brickCount > 0){ //plays the game while player has turns left and bricks are still on the screen
			playGame();
		}

		if(brickCount == 0){// condition if game is won
			remove(ball);
			gameIsWon();
		}
		if(turn == NTURNS){// condition if game is lost
			gameIsLost();
		}

	}

	//Displays losing message on screen if the game is lost
	private void gameIsLost() {
		displayMessage("Awww You Lost! Better Luck Next Time!");

	}

	//Plays the Breakout game
	private void playGame() {
		moveBall();
		checkForCollison();
		pause(DELAY);
		if(ballHitsBottomWall()){// If the ball hits the bottom wall, the player loses a turn and the ball is removed
			remove(ball);
			turn++;
			vy++;// Increases ball speed every turn
			if(turn < NTURNS){ // If the player still has turns left, the ball resets and waits for the player to click
				makeAndSetBall();
				waitForClick();
			}
		}

	}

	//Updates the brick counter on the screen
	private void updateBrickCounter(){
		remove(counter);
		counter = brickCounter();
		add(counter);
	}

	//Creates and returns the label for the brick counter
	private GLabel brickCounter(){
		double y = BRICK_Y_OFFSET/2;
		GLabel label = new GLabel("Bricks Left: " + brickCount, 0, y);
		label.setFont("SansSerif-12");
		return label;
	}

	//Method that creates winning and losing messages 
	private void displayMessage(String msg){
		GLabel label = new GLabel(msg);
		label.setFont("SansSerif-14");
		double x = (getWidth() - label.getWidth())/2;
		double y = (getHeight() - label.getHeight())/2;
		add(label, x, y);

	}

	//Condition if the game is won
	private void gameIsWon() {
		displayMessage("Congratulations! You Won!");

	}

	//Checks for collisions with the paddle and the bricks
	private void checkForCollison(){
		GObject collider = getCollidingObject();
		if(collider == paddle){
			vy = -vy;//Changes ball's y direction if the ball hits the paddle
			//AudioClip paddleYa = MediaTools.loadAudioClip("Breakout.m4a");
			//paddleYa.play();

		}else if(collider != null){//Removes brick if the ball hits a brick
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			bounceClip.play();//makes sounds when ball hits the brick
			remove(collider);
			vy = -vy;
			ball.setColor(collider.getColor());
			brickCount--;
			updateBrickCounter();
		}
	}

	//Checks for colliding object in the four corners of the ball, if an object is present
	//it returns the object
	private GObject getCollidingObject(){
		double x = ball.getX();
		double y = ball.getY();
		GObject objectTopLeft = getElementAt(x, y);
		GObject objectBottomLeft = getElementAt(x, y + 2*BALL_RADIUS);
		GObject objectTopRight = getElementAt(x + 2*BALL_RADIUS, y);
		GObject objectBottomRight = getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS);

		if(objectInTopLeft()){//Checks for object in top left corner
			return objectTopLeft;

		}else if(objectInBottomLeft()){//Checks for object in bottom left corner
			return objectBottomLeft;

		}else if(objectInTopRight()){//Checks for object in top right corner
			return objectTopRight;

		}else if(objectInBottomRight()){//Checks for object in bottom right corner
			return objectBottomRight;

		}else{
			return null;//Returns null if no object is present
		}
	}

	//Returns true if there's an object in the bottom right corner
	private boolean objectInBottomRight() {
		double x = ball.getX() + 2*BALL_RADIUS;
		double y = ball.getY() + 2*BALL_RADIUS;
		return getElementAt(x,y) != null;
	}

	//Returns true if there's an object in the top right corner
	private boolean objectInTopRight() {
		double x = ball.getX() + 2*BALL_RADIUS;
		double y = ball.getY();
		return getElementAt(x,y) != null;
	}

	//Returns true if there's an object in the bottom left corner
	private boolean objectInBottomLeft() {
		double x = ball.getX();
		double y = ball.getY() + 2*BALL_RADIUS;
		return getElementAt(x, y) != null;
	}

	//Returns true if there's an object in the top left corner
	private boolean objectInTopLeft() {
		double x = ball.getX();
		double y = ball.getY();
		return getElementAt(x,y) != null;
	}


	//Method makes the ball bounce and keeps it contained within the screen
	private void moveBall() {
		ball.move(vx, vy);

		//Makes ball move in the opposite x direction if it hits the left wall
		if(ballHitsLeftWall()){
			vx = -vx;
		}
		//Makes the ball move in the opposite x direction if it his the right wall
		if(ballHitsRightWall()){
			vx = -vx;
		}
		//Makes the ball move in the opposite direction if it hits the top wall
		if(ballHitsTopWall()){
			vy = -vy;		
		}

	}

	//Returns true if the ball hits the bottom wall
	private boolean ballHitsBottomWall() {
		return ball.getY() >= getHeight() - 2*BALL_RADIUS;
	}

	//If ball hit the top wall, returns true, otherwise returns false
	private boolean ballHitsTopWall() {
		return ball.getY() <= 0;
	}

	//If ball hit the right wall, returns true, otherwise returns false
	private boolean ballHitsRightWall() {
		return ball.getX() >= getWidth() - 2*BALL_RADIUS;
	}

	//If ball hit the left wall, returns true, otherwise returns false
	private boolean ballHitsLeftWall() {
		return ball.getX() <= 0;
	}

	//Sets up the game by laying the bricks and setting the paddle and ball
	private void setUpGame() {
		layBricks();
		makeAndSetPaddle();
		makeAndSetBall();
		counter = brickCounter();
		add(counter);



	}

	//Makes and sets the ball in the center of the screen
	private void makeAndSetBall() {
		double x = (getWidth() - 2*BALL_RADIUS)/2;
		double y = getHeight()/2 - 2*BALL_RADIUS;
		double size = 2*BALL_RADIUS;
		ball = new GOval(x, y,size, size);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);

	}
	//Establishes x direction of ball when the mouse is clicked
	public void mouseClicked(MouseEvent e){
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
	}

	//Mouse event that moves the paddle accordingly
	// as the player's mouse changes x coordinates.
	//The y coordinate stays constant.

	public void mouseMoved(MouseEvent e){
		double x = e.getX();// gets x coordinate of mouse
		double y = getHeight()- PADDLE_Y_OFFSET - PADDLE_HEIGHT;

		//Right Edge Condition
		if(e.getX() >= getWidth() - PADDLE_WIDTH){
			paddle.setLocation(getWidth() - PADDLE_WIDTH, y);	

			//Left Edge Condition
		}else if(e.getX() <= 0){
			paddle.setLocation(0, y);

			//Moves paddle otherwise
		}else{
			paddle.setLocation(x,y);
		}
	}

	//Creates the paddle
	private void makeAndSetPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2;
		double y = getHeight()- PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);


	}

	//Sets up the bricks on the console
	private void layBricks() {

		//Double for loop keeps track of the bricks laid in a grid-like pattern
		for(int col = 0; col < NBRICK_COLUMNS; col++){
			for(int row = 0; row < NBRICK_ROWS; row++){
				double xInitial = (getWidth() - (NBRICK_ROWS*BRICK_WIDTH + (NBRICK_ROWS - 1) * BRICK_SEP))/2;// sets starting x position
				double x = xInitial + BRICK_WIDTH *(col) + (col)*BRICK_SEP;//x coordinate (varies with column number)
				double y = BRICK_Y_OFFSET + BRICK_HEIGHT*(row) + (row)*BRICK_SEP;// y coordinate (varies with row number)

				if(row < NBRICK_ROWS/5.0 ){
					putBrick(Color.RED, x , y);//lays red bricks for the first fifth rows

				}
				if(row >= NBRICK_ROWS/5.0 && row < NBRICK_ROWS/5.0*2){
					putBrick(Color.ORANGE, x, y);//lays orange bricks for the second fifth rows

				}
				if(row >= NBRICK_ROWS/5.0*2 && row < NBRICK_ROWS/5.0*3){
					putBrick(Color.YELLOW, x, y);//lays yellow bricks for the third fifth rows

				}
				if(row >= NBRICK_ROWS/5.0*3 && row < NBRICK_ROWS/5.0*4){
					putBrick(Color.GREEN, x, y);//lays green bricks for the fourth fifth rows

				}
				if(row >= NBRICK_ROWS/5.0*4 && row < NBRICK_ROWS){
					putBrick(Color.CYAN, x, y);//lays cyan (blue) bricks for the last fifth rows


				}
			}

		}

	}

	//Method for creating bricks and adding them to the screen
	//Takes in 3 arguments: The bricks color, its x coordinate, and its y coordinate
	private void putBrick(Color color, double x, double y) {
		GRect brick = new GRect(x, y , BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);



	}

}
