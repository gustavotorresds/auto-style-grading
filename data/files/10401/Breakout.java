/*
 * File: Breakout.java
 * -------------------
 * Name: Victoria Yang
 * Section Leader: Cat Xu
 * 
 * This file implements the game of Breakout.
 * The user begins by selecting a difficult mode amongst three choices, with each choice resulting in the ball moving at different speed.
 * The user will have three tries each game to try to clear all the breaks. 
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

	// Number of bricks
	public static final int NBRICKS = NBRICK_COLUMNS * NBRICK_ROWS;

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
	public static final double BALL_SIZE = BALL_RADIUS * 2;

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

	// Separation between the three different mode labels
	public static final  double MODE_SEP = 40;

	//The fraction of speed corresponding to each mode 
	public static final double EASY = 1.0;
	public static final double MEDIUM = 1.5;
	public static final double HARD = 2.0;


	//create instance variables for the objects and values that are going to be used 
	//throughout this game
	GRect paddle;
	GOval ball;
	GLabel message;
	GLabel lifecount;
	GLabel easy;
	GLabel medium;
	GLabel hard;
	GLabel select;
	double fraction;
	int numberTurnsTracker = NTURNS;
	int numberBricksTracker = NBRICKS;
	double vy, vx;


	/**
	 * This defines the run method.
	 * The user will first choose the mode of difficulty (with one chance only), then the click will activate the game, 
	 * and the user will play the game either until a) the number of turns becomes 0 or b) the number of bricks becomes 0.
	 * After the program hits this condition, the program will display win-lose message. 
	 * The mouse listener will activate the mouseClicked Event defined later that would determine the fraction variable, which would
	 * determine the speed of the ball moving on the screen. 
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		selectMode();
		waitForClick();
		initiateGame();
		endGame();
	}

	/**
	 * This method initiates the game by first setting up the world. Afterward, the program will keep as long as the playing condition
	 * defined above satisfies. 
	 * The program will exit once the user hit the win or lose condition.
	 */
	private void initiateGame() {
		setWorld();
		while (numberTurnsTracker !=0 && numberBricksTracker !=0 ) {
			play ();
		}

	}

	/**
	 * This method defines what happens at the end of the game, with the process to display either win or lose messages.
	 */
	private void endGame() {
		if ( numberBricksTracker == 0) {
			message = new GLabel ("You won the game :)");
			double x = 0.5*(getWidth() - message.getWidth());
			double y = 0.5*(getHeight()- message.getHeight());
			message.setLocation(x, y);
			add(message);
		} else if (numberTurnsTracker == 0) {
			message = new GLabel ("You lost the game :(");
			add(message);
			double x = 0.5*(getWidth() - message.getWidth());
			double y = 0.5*(getHeight()- message.getHeight());
			message.setLocation(x, y);
		}
	}

	/**
	 * This method draws the "select mode" interface where the user can choose a difficulty mode from. 
	 */
	private void selectMode() {
		select = new GLabel("Select Mode from Below:");
		easy = new GLabel("Easy");
		medium = new GLabel ("Medium");
		hard = new GLabel ("Hard");
		double x_medium = 0.5*(getWidth()- medium.getWidth());
		double y_medium = 0.5*(getHeight()- medium.getAscent());
		double x_easy = 0.5*(getWidth()- easy.getWidth());
		double y_easy = 0.5*(getHeight()- easy.getAscent()) - MODE_SEP;
		double x_hard = 0.5*(getWidth()- hard.getWidth());
		double y_hard = 0.5*(getHeight()- hard.getAscent()) + MODE_SEP;
		medium.setLocation(x_medium, y_medium);
		easy.setLocation(x_easy, y_easy);
		hard.setLocation(x_hard, y_hard);
		double x_select = 0.5*(getWidth() - select.getWidth());
		double y_select = 70;
		select.setLocation(x_select, y_select);
		add(select);
		add(medium);
		add(easy);
		add(hard);
	}

	/**
	 * This mouseClicked event will detect the click location of the mouse and when the mouse click on the three difficulty labels created,
	 * define the fraction value and remove the object on the screen.
	 * This prepares the setWorld() method later as it will draw the world of the actual game. 
	 */
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		GObject maybeAMode = getElementAt (x,y);
		if (maybeAMode != null) {
			if (maybeAMode == easy) {
				fraction = EASY;
				removeAll();
			} 
			if (maybeAMode == medium) {
				fraction = MEDIUM;
				removeAll();
			}
			if (maybeAMode == hard) {
				fraction = HARD;
				removeAll();
			} 
		} 
	}

	/**
	 * This sets up the world that the program operates in
	 * The paddle is movable, tracked through mouseMoved event
	 */
	private void setWorld() {
		drawBricks();
		drawPaddle();
	}

	/**
	 * This method will display the number of "life" or turns left after the user lose one life 
	 * After displaying this message, this method will remove the lifecount message after the user clicks once.  
	 */
	private void displayNumberOfLife() {
		if (numberTurnsTracker != 0) {
			lifecount = new GLabel("You have "+ numberTurnsTracker + " live(s) left.");
			add(lifecount, 0.5*(getWidth()-lifecount.getWidth()), 0.3*(getHeight()-lifecount.getAscent()));
			waitForClick();
			remove(lifecount);
		}
	}

	/**
	 * This play method will create the ball. Once the user clicks on the screen, the program will move the ball as long as 
	 * it does not hit the bottom wall. If it does, the moveBall() method stops and the screen will display the number of lives (turns) that
	 * the user has left. 
	 */
	private void play () {
		makeBall();
		waitForClick();
		moveBall();
		displayNumberOfLife();
	}

	/**
	 * This method draws the Bricks according to the requirement using the method drawTwoRows defined below.
	 */
	private void drawBricks() {
		double lengthOfRow = NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS-1) * BRICK_SEP;
		double x = (getWidth() - lengthOfRow) * 0.5;
		double y = BRICK_Y_OFFSET;
		drawTwoRows(Color.RED, x, y);
		drawTwoRows(Color.ORANGE, x, y + 2*(BRICK_HEIGHT + BRICK_SEP));
		drawTwoRows(Color.GREEN, x, y + 4*(BRICK_HEIGHT + BRICK_SEP));
		drawTwoRows(Color.CYAN, x, y + 6*(BRICK_HEIGHT + BRICK_SEP));
	}

	/**
	 * This method draws two rows of bricks by passing parameters 
	 * @param color
	 * @param x
	 * @param y
	 */
	private void drawTwoRows(Color color, double x, double y) {

		for (int i = 0; i < 2 ; i++) {
			for (int r = 0; r < NBRICK_COLUMNS; r++) {
				GRect brick = new GRect(x + r*(BRICK_WIDTH+BRICK_SEP), y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setColor(color);
				brick.setFilled(true);
			}
			y = y + BRICK_HEIGHT + BRICK_SEP; 
		}
	}

	/**
	 * This method draws the paddle 
	 */
	private void drawPaddle() {
		double x = 0.5*(getWidth()- PADDLE_WIDTH);
		double y = getHeight()- PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		add(paddle);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
	}

	/**
	 * This method tracks the movement of the mouse and move the paddle correspondingly while preventing the 
	 * paddle from moving off the screen using certain conditions 
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() ;
		double y = paddle.getY();
		if (x >= 0.5*PADDLE_WIDTH && x <= getWidth()-0.5*PADDLE_WIDTH) {
			paddle.setLocation(x - 0.5*PADDLE_WIDTH,y);
		}
	}

	/**
	 * This method makes a ball (GOval object) 
	 * @return
	 */
	private GOval makeBall() {
		double x = 0.5*getWidth() - BALL_RADIUS;
		double y = 0.5*getHeight() - BALL_RADIUS;
		ball = new GOval(x, y, BALL_SIZE, BALL_SIZE);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
		return ball;
	}

	/**
	 * This method moves the ball. It first defines the velocities in x and y direction. The x velocity is randomly generated 
	 * between a certain range as required of this game. The y velocity is varied depending on the difficulty mode (and thus fraction)
	 * that the user chose earlier. This method  will keep running until either a) the number of bricks becomes 0
	 * or b) the program hits the bottom wall, after which the program will return to the play() method.
	 */
	private void moveBall (){
		RandomGenerator rgen = RandomGenerator.getInstance();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y * fraction;
		while (numberBricksTracker != 0) {
			ball.move(vx, vy);
			pause(DELAY);
			checkForWall();
			if(hitBottomWall()) {
				numberTurnsTracker --;
				remove(ball);
				break;
			}
			else {
				encountersCollider();
			}
		}

	}

	/**
	 * This method defines what the ball will do if encountering a collider (paddle or brick)
	 * It also keeps track of the number of bricks left after removing the bricks. 
	 */
	private void encountersCollider() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			vy = -Math.abs(vy);
		} else if (collider != null & collider != paddle) {
			remove(collider);
			numberBricksTracker --;
			vy = -vy;
		}

	}

	/**
	 * This method returns an object when the ball encounters an object, otherwise it returns null
	 * @return
	 */
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		if (getElementAt(x,y) != null) {
			GObject obj = getElementAt (x,y);
			return obj;
		} else if (getElementAt(x + BALL_SIZE,y) != null) {
			GObject obj = getElementAt (x + BALL_SIZE,y);
			return obj;
		} else if (getElementAt(x , y+ BALL_SIZE) != null) {
			GObject obj = getElementAt (x , y+ BALL_SIZE);
			return obj;
		}
		else if (getElementAt(x+ BALL_SIZE , y+ BALL_SIZE) != null) {
			GObject obj = getElementAt (x,y);
			return obj;
		}
		else {
			return (null);
		}
	}

	/**
	 * This method checks for the wall on all sides except for the bottom one and changes the direction of the ball as neede 
	 */
	private void checkForWall () {
		if(hitLeftWall() || hitRightWall()) {
			vx = -vx;
		}
		if(hitTopWall()) {
			vy = -vy;
		}
	}

	/**
	 * This boolean returns whether or not the ball hits the bottom wall of the window.
	 */
	private boolean hitBottomWall() {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/**
	 * This boolean returns whether or not the ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	/**
	 * This boolean returns whether or not the ball should bounce off
	 * of the right wall of the window.
	 */
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/**
	 * This boolean returns whether or not the ball should bounce off
	 * of the left wall of the window.
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	} 
}


