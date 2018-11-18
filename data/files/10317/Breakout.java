/*
 * File: Breakout.java
 * -------------------
 * Name: David Guo 
 * Section Leader: Akua 
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
	public static final double BALL_DELAY = 1000.0 / 60.0;

	// Animation delay or pause time before player wins or loses screen
	public static final double GAME_DELAY = 100.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// The Kicker multiplies its vx by this increment every time it is divisible by the kicker_divisible 
	private static final double KICKER_INCREMENT = 1.5;

	// every time the Kicker's counter is divisible by this, 4, then it will multiply its speed
	private static final int KICKER_DIVISIBLE = 6;

	// number of bricks on the screen, number of bricks required to win the game 
	private static final int NUM_BRICKS = 100; 

	public static final double PADDLE_Y_POSITION = CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

	private static final int NUM_CIRCLES = 20;

	private static final double MIN_RADIUS = 3;

	private static final double MAX_RADIUS = 50;


	/**
	 * the run method makes the game Breakout and plays it for up to three times, or three lives for the player.   
	 */
	public void run() {
		makeGame();
		for (lives = NTURNS; lives> -1; lives--) { // the player has three chances to win the game, these three chances are called "lives"
			boolean result = playGame(); // playGame returns a boolean called result that determines the result of the game
			if (result != true) { // if the result returned is that you removed all bricks from the game, you win 
				loseCondition();
			} else {
				winCondition();
				break;
			}
		}
	}

	/**
	 * this method makeGame actually plays the game 
	 * pre-condition: blank screen, only mouse conditions have been set
	 * post-condition: the bricks, the paddle, and the ball as objects are made. the counter for how we determine the winner
	 * has been initialized at 0.
	 */
	private void makeGame() { 
		setTitle("CS 106A Breakout"); 
		addMouseListeners();
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); 
		makeBricks();
		makePaddle();
		makeBall();
		setCounters();
		makeStartScreen(); // in order for the game to start the player will click 
		waitForClick(); 
	}

	/**
	 * playGame does four things: 1. it is the mechanism for how the ball actually moves at its vy and vx speeds
	 * 2. it regulates how the ball bounces off the walls 
	 * 3. it increments the counter for up to how many bricks there are and returns a boolean based on whether you win or lose
	 * 4. collision with other objects work in the game 
	 * pre-condition: all necessary objects in the game have been made.
	 * post-condition: the game is now able to be played, all collision, bouncing, etc. have been input. 
	 * furthermore, the playGame method returns a boolean that determines whether a. the player has removed all bricks in the game and wins the game,
	 * or b. the player has lost all their lives and loses the game 
	 */
	private boolean playGame() {
		remove(StartScreen);
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);  
		if (rgen.nextBoolean(0.5)) vx = -vx; //randomize the angle for which the ball moves
		while(true) {
			Ball.move(vx, vy);
			pause(BALL_DELAY);
			checkCollision(); //checks for whether or not the ball has collided with a brick or paddle
			if (Ball.getY() > getHeight() + Ball.getHeight()) { // the player loses his or her turn if the ball is lost at the bottom
				Ball.setLocation(BallX, BallY); // if the ball is lost at the bottom it resets in the middle and you lose a life
				lives = lives - 1;
				if (lives == 0) {
					return false;
				}
			}
			if (Ball.getY() < 0 + Ball.getHeight()) {
				vy = -vy;
			}
			if (Ball.getX() > getWidth() - Ball.getHeight()) { // if the ball hits the side walls its vx will change 
				vx = -vx;
			}
			if (Ball.getX() < 0) {
				vx = -vx;
			}
			if (counterGame == NUM_BRICKS) { // if counter reaches a certain level the player wins 
				return true; // this boolean will be used to determine the ending screen 
			}

		}
	}

	/**
	 * checkCollision checks whether or not the object "Ball" has collided with either a paddle or a brick. if it collides
	 * with a paddle then it simply bounces off (without getting stuck), and if it is a brick, it removes it. 
	 * pre-condition: the ball is able to move with a delay and to have an initial speed from the beginning screen
	 * post-condition: the ball now collides with the paddle and bricks when their locations are the same 
	 */
	private void checkCollision() {
		GObject collided = getCollidingObject(); //checkCollision has a variable GObject "collided" that is returned by getCollidingObject. it calls getCollidingObject
		if (collided == Paddle) {
			vy = Math.abs(vy) * -1; //if the paddle is stuck inside the ball, stop changing the direction of it, just always make it negative
			counterKicker = counterKicker +1;
			if (counterKicker % KICKER_DIVISIBLE == 0) { // every time the ball hits the paddle a certain amount of times, the vx is multiplied by the increment
				vx = KICKER_INCREMENT * vx;
			}
		} 
		if (collided != Paddle && collided != null) { //if the returned GObject "collided" is not the GObject Paddle, and if it's not null, then it has to be the bricks
			remove(collided);
			counterGame = counterGame + 1;
			vy = -vy;
			playSound();
		}
	}

	/**
	 * getCollidingObject returns a GObject called "collider", which is at the position where the ball hits an object.
	 * if it's not null, "collider," an object, is returned. 
	 * pre-condition: the ball is able to move but can't tell how or where it collides with objects
	 * post-condition: an object called "collider" is returned every time the ball collides with an object, or the ball's position is the same as an object
	 */
	private GObject getCollidingObject() {
		GObject collider = getElementAt(Ball.getX(), Ball.getY()); // extension project where ball is going to be at the next increment, in between has GObject
		if (collider != null) { 
			return collider;
		}
		collider = getElementAt(Ball.getX() + 2 * BALL_RADIUS, Ball.getY()); // have to account for all four points of the ball
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(Ball.getX(), Ball.getY() + 2 * BALL_RADIUS);
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(Ball.getX() + 2 * BALL_RADIUS, Ball.getY() + 2 * BALL_RADIUS);
		if (collider !=null) {
			return collider;
		}
		return null; //if the ball hit nothing then it returns null

	}

	/**
	 * makeBricks makes the set of bricks needed for the game Breakout.
	 * this is one of the first steps of the game; it makes a 10x10 rectangle consisting of 100 bricks, with a gradient of coloring
	 * pre-condition: mouseListeners have been added to the game 
	 * post-condition: a 10-row and 10-column object of bricks have been made with 2 row increments of different colors in the 
	 * gradient of: red, orange, yellow, green, and cyan. 
	 */
	private void makeBricks() {
		for ( int k = 0; k< NBRICK_ROWS; k++) { // k as a variable increases to 10 rows of bricks 
			for (int i = 0; i< NBRICK_COLUMNS; i++) { //i increases to 10, how many bricks in a row 
				double CenteredX = getWidth() - ( i*BRICK_WIDTH + (i + 1)*BRICK_SEP + ((NBRICK_COLUMNS)*BRICK_SEP));
				double CenteredY = k*BRICK_HEIGHT + k*BRICK_SEP;
				Bricks = new GRect( CenteredX , CenteredY + BRICK_Y_OFFSET, BRICK_WIDTH, BRICK_HEIGHT); 
				add(Bricks);
				Bricks.setFilled(true);

				if (k <2) { // based on the number of the row, incremented by the number of rows by variable "k," the bricks are colored
					Bricks.setColor(Color.RED);
				}
				if (k>=2 && k<4) {
					Bricks.setColor(Color.ORANGE);
				}
				if (k>=4 && k<6) {
					Bricks.setColor(Color.YELLOW);
				}
				if (k>=6 && k<8) {
					Bricks.setColor(Color.GREEN);
				}
				if (k>=8 && k<=10) {
					Bricks.setColor(Color.CYAN);
				}
			}
		}
	}

	/**
	 * makePaddle makes the paddle integral to the game of Breakout, it is centered initially in width and at the bottom of the screen
	 * pre-condition: bricks have been made 
	 * post-condition: paddle has been made, it moves according to the location of the mouse on the screen and stops at the end of the screen
	 */
	private void makePaddle() {
		double PaddleX = getWidth()/2 - PADDLE_WIDTH/2;
		Paddle = new GRect( PaddleX, PADDLE_Y_POSITION , PADDLE_WIDTH, PADDLE_HEIGHT);
		Paddle.setFilled(true);
		Paddle.setColor(Color.BLACK);
		add(Paddle);
	}

	/**
	 * this mouseMoved event tracks the paddle to the movement of the player's mouse, which is needed for playing the game Breakout
	 * this event pairs with the method that makes the Paddle
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		Paddle.setLocation(mouseX - PADDLE_WIDTH/2, PADDLE_Y_POSITION); 
		if (mouseX + PADDLE_WIDTH > getWidth()) {
			Paddle.move(-PADDLE_WIDTH/2, 0);
		}
		if (0 > mouseX - PADDLE_WIDTH/2) { // if the mouse gets lost on the left side then the mouse stops at the very end of the screen
			Paddle.move(PADDLE_WIDTH/2, 0);  
		}
	}

	/**
	 * makeBall makes the ball important to the game of Breakout. This ball is the win condition of whether the player can get rid of all the bricks
	 * pre-condition: bricks and the paddle have been made 
	 * post-condition: the ball has been made, it is centered in the middle of the screen
	 */
	private void makeBall() {
		BallX = getWidth()/2 - BALL_RADIUS;
		BallY = getHeight()/2 - BALL_RADIUS;
		Ball = new GOval( BallX, BallY, 2* BALL_RADIUS, 2*BALL_RADIUS);
		Ball.setFilled(true);
		Ball.setColor(Color.BLACK);
		add(Ball);

	}
	
	/**
	 * setCounters makes the counters needed to play the game, counters that measure how many bricks you've removed in order 
	 * to win the game, the kicker for when to pick up the pace of the game, and the lives, for how many chances you have 
	 * to play the game.
	 */
	private void setCounters() {
		counterGame = 0;//counter for how many bricks in the game is initialized at 0
		counterKicker = 0;
		lives = NTURNS;
	}

	/**
	 * makeStartScreen creates a start screen for the game to start. this start screen requires a click to start the game
	 * pre-condition: most of the makeGame method has been made, including the bricks, ball and paddle
	 * post-condition: before the game starts makeStartScreen, a GLabel, is created and is paired with a WaitForClick method 
	 * in order to start the game itself 
	 */
	private void makeStartScreen() { 
		StartScreen = new GLabel("Click to Start the Game");
		StartScreen.setLocation(getWidth()/2 - StartScreen.getWidth()/2, getHeight() - StartScreen.getAscent());
		StartScreen.setFont(Font.SANS_SERIF);
		StartScreen.setColor(Color.blue);
		add(StartScreen);
	}

	/**
	 * This method plays the audio when the ball hits a brick
	 * post-condition: whenever the ball hits the bricks now there is an audio message that is played, as opposed to before,
	 * where when the ball hits the brick no sound plays 
	 */
	private void playSound() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
	}
	
	/**
	 * makeRandomCircles makes 20 random circles with different sizes and different colors scattered around the screen
	 */
	private void makeRandomCircles() {
		for ( int i = 0; i < NUM_CIRCLES; i++) {
			double r = rgen.nextDouble(MIN_RADIUS, MAX_RADIUS); //randomize for the circle between the minimum and maximum radius
			double x = rgen.nextDouble(0, getWidth() - 2 * r);
			double y = rgen.nextDouble(0, getHeight() - 2 * r);
			GOval circle = new GOval (x,y, 2 * r, 2 * r);
			circle.setFilled(true);
			circle.setFillColor(rgen.nextColor());
			add(circle);
		}
	}
	
	/**
	 * winCondition makes and adds all the elements on the screen in the condition you win the game 
	 * pre-condition: the player has removed all 100 bricks in the game by collding the ball with all bricks in the game 
	 * post-condition: there is a "congratulations" screen made when this happens 
	 */
	private void winCondition() {
		pause(GAME_DELAY);
		removeAll(); //remove all objects on the screen to make way for the winning/losing announcements
		addWonRect();
		addWonLabel();
	}

	/**
	 * addWonRect is the color of the "Congratulations" or "Win" Screen if the player wins at the end of the game 
	 */
	private void addWonRect() {
		GRect Congratulations = new GRect(0,0, getWidth(), getHeight());
		Congratulations.setFilled(true);
		Congratulations.setColor(Color.YELLOW);
		add(Congratulations);
		makeRandomCircles();
	}

	/**
	 * addWonLabel is the label that tells them they won at the end of the game
	 */
	private void addWonLabel() {
		GLabel Won = new GLabel("You Won! Good Job :)");
		Won.setFont(Font.SANS_SERIF);
		Won.setLocation(getWidth()/2 - Won.getWidth()/2, getHeight()/2 - Won.getAscent()/2);
		add(Won);
	}
	/**
	 * loseCondition makes all the screen elements and objects when you lose the game 
	 */
	private void loseCondition() {
		pause(GAME_DELAY); //the game pauses for a more smooth transition into the loss or wins creen
		removeAll();
		addLoseRect();
		addLoseLabel();
	}

	/**
	 * addLoseLabel is a label that tells you you lost if you lose the game 
	 */
	private void addLoseLabel() {
		GLabel Lose = new GLabel("You Lost! Sorry :(");
		Lose.setFont(Font.SANS_SERIF);
		Lose.setColor(Color.white);
		Lose.setLocation(getWidth()/2 - Lose.getWidth()/2, getHeight()/2 - Lose.getAscent()/2);
		add(Lose);
	}

	/**
	 * addLoseRect makes the screen that tells you lost at the end of the game 
	 */
	private void addLoseRect() {
		GRect Lose = new GRect(0,0, getWidth(), getHeight());
		Lose.setFilled(true);
		Lose.setColor(Color.RED);
		add(Lose);
		makeRandomCircles();
	}
	
	/* Private Instance Variables */
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private double BallX, BallY;
	int lives; // amount of lives a player before he/she loses 
	int counterKicker;
	int counterGame;
	GLabel StartScreen; // GLabel that tells the player you need to click to start the game
	GRect Paddle; 
	GRect Bricks;
	GOval Ball;
}

