/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
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
	public static final double BALL_DIAMETER = 2*BALL_RADIUS;

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

	/*
	 * Used to change the colors of the rows of bricks
	 * Initially set to red as that is starting color
	 */
	private Color brickColor = Color.RED;

	//Initializes the rectangle for the paddle in the Breakout game
	private GRect paddle;

	//Initializes the ball that is used to hit the bricks and bounce off paddle
	private GOval ball;

	//Used to change the x and y velocity for the ball so the game is harder for the user 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*
	 * the horizontal velocity of the ball which changes if it bounces 
	 * off of the left or right wall of the game (sides of the canvas)
	 */
	private double vx = rgen.nextDouble(1.0, 3.0); 

	/*
	 * The vertical velocity of the ball which becomes negative if it bounces
	 * off of the top wall, the paddle, or hits a brick. The faster
	 * this speed, the harder it is for the player. 
	 */
	private double vy = VELOCITY_Y;

	/*
	 * Object that is initialized here but is later used to mark any 
	 * object that is underneath the ball to compare if it is a brick, 
	 * the paddle, or null. 
	 */
	private GObject obj;

	/*
	 * This is used to record how many bricks are left during the game.
	 * The user has won the game when brickCount == 0. 
	 */
	private int brickCount = NBRICK_COLUMNS*NBRICK_ROWS;

	/*
	 * This is the number of turns that a user has left to try to win 
	 * the game.
	 */
	private int numTurns = NTURNS;

	/*
	 * This audio file makes a sound each time a brick is hit. 
	 */
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	/*
	 * This method sets up the game by making all of the bricks and creating
	 * the paddle. Then it adds mouse listeners to help control the 
	 * movement of the paddle. Next it allows users to play the game. 
	 * In the play game method, the ball is created and after the user 
	 * clicks to start, the game can be played for a give number of turns
	 * or until the user gets all of the bricks. 
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setup();
		addMouseListeners();
		playGame();
	}
	
	/*
	 * This method holds the conditions needed to play the game 
	 * according to the rules. While there are still bricks left, and
	 * the user has not used up all of their turns, the game will run. 
	 * If the user wins or loses, they will be notified
	 * via a label on the canvas and the game will end. 
	 */
	private void playGame() {
		GLabel winMessage = new GLabel ("Congratulations! You have won!", getWidth()/2, getHeight()/2);
		GLabel losingMessage = new GLabel ("You Lose!", getWidth()/2, getHeight()/2);
		while (brickCount > 0 && numTurns > 0) { //need both or it will make a new ball but it won't move the ball 
			makeBallPlayGame();
		} 
		if (brickCount == 0) {
			remove(ball);
			add(winMessage);
			winMessage.move(-(winMessage.getWidth()/2), (winMessage.getAscent()/2));
		} else {
			remove(ball);
			add(losingMessage);
			losingMessage.move(-(losingMessage.getWidth()/2), (losingMessage.getAscent()/2));
		}
	}
	
	/*
	 * This method is called as long as there are still bricks left
	 * and the user still has turns remaining. It creates a ball, then
	 * makes the ball move using an animation loop. 
	 */
	private void makeBallPlayGame() {
		createBall();
		moveBall();
	}

	/*
	 * moveBall calls get colliding object to check if the ball
	 * is colliding with any object. This method checks each of the four
	 * corners of the rectangle that encompasses the ball. It checks them 
	 * using if else statements so that only one brick is hit by the ball. 
	 * It returns the object that the ball is hitting. 
	 * 
	 * the moveBall method then checks what kind of object this is and
	 * determines the future action of the ball. 
	 */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			obj = getElementAt(ball.getX(),ball.getY());
			return obj;
		} else if (getElementAt(ball.getX()+ball.getWidth(), ball.getY()) != null) {
			obj = getElementAt(ball.getX()+ball.getWidth(), ball.getY());
			return obj;
		} else if (getElementAt(ball.getX()+ball.getWidth(), ball.getY() + ball.getHeight()) != null) {
			obj= getElementAt(ball.getX()+ball.getWidth(), ball.getY() + ball.getHeight());
			return obj;
		} else if (getElementAt(ball.getX(), ball.getY()+ball.getHeight()) != null) {
			obj = getElementAt(ball.getX(), ball.getY()+ball.getHeight());
			return obj;
		} else {
			return null;
		}
	}

	/*
	 * This method controls the balls movement. The ball enters an
	 * animation loop so long as the ball is not hitting the bottom 
	 * wall and there are still bricks left. 
	 * 
	 * The if statements at the beginning of the while loop instruct the
	 * ball to bounce off of the walls (except the bottom wall). 
	 * 
	 * If it hits the bottom wall, the ball is removed, a turn is lost, and
	 * a message is displayed to the user on the screen. Once the screen is
	 * clicked, the messages are removed. 
	 * This exits the animation loop. The program then starts the while 
	 * loop in playGame method and the game starts again. 
	 * 
	 * If it is not colliding with the bottom wall, 
	 * it checks to see if the ball is colliding with any objects
	 * on the screen. If it hits a brick, the brick is removed, brickCount
	 * is decreased, a sound is played, and the vertical speed is reversed.
	 * 
	 * If it hits the paddle, the vertical speed is reversed. 
	 */
	private void moveBall() {
		GLabel nextTurnMessage = new GLabel ("Bummer. Number of Turns Left: " +(numTurns-1), getWidth()/2, getHeight()/2);
		GLabel clickToStart = new GLabel ("Click to start your next turn!", getWidth()/2, getHeight()/2 + nextTurnMessage.getAscent());
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		while((!hitBottomWall(ball)) && brickCount > 0) {
			ball.move(vx, vy);
			if (hitLeftWall(ball) || hitRightWall(ball) ) {
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			if (hitBottomWall(ball)) {
				remove(ball);
				numTurns--;
				if (numTurns >= 1) {
					add(nextTurnMessage);
					add(clickToStart);
					nextTurnMessage.move(-(nextTurnMessage.getWidth()/2), (nextTurnMessage.getAscent()/2));
					clickToStart.move(-(clickToStart.getWidth()/2), (clickToStart.getAscent()/2));
					waitForClick();
					remove(nextTurnMessage);
					remove(clickToStart);
				}
			}
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy = -Math.abs(vy);
			} else if (collider != null) {
				bounceClip.play();
				vy = -vy; 
				remove(collider);
				brickCount--;
			}
			pause(DELAY);
		} 
	}

	/*
	 * The hit wall methods take in GOvals (aka the ball) and 
	 * return booleans which are true if the 
	 * location of the ball is outside of the canvas on any of the four 
	 * sides. These are used to determine what the ball should do when 
	 * it is out of the boundaries. 
	 */
	private boolean hitTopWall(GOval o) {
		return ball.getY() <= 0;
	}

	private boolean hitBottomWall(GOval o) {
		return ball.getY() >= getHeight()-ball.getHeight();
	}

	private boolean hitRightWall(GOval o) {
		return ball.getX() >= getWidth()-ball.getWidth();
	}

	private boolean hitLeftWall(GOval o) {
		return ball.getX() <= 0;
	}

	/*
	 * This method creates the ball and centers it on the screen to start.
	 * It is re-made at the beginning of each turn. 
	 */
	private void createBall() {
		double firstBallX = (getWidth()-BALL_DIAMETER)/2;
		double firstBallY = (getHeight()-BALL_DIAMETER)/2;
		ball = new GOval(firstBallX, firstBallY, BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
		add(ball);
	}
	
	/*
	 * This mouse event controls the paddle. The paddle takes on the
	 * location of the mouse coordinates so that the user can move 
	 * the paddle by moving their mouse. 
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX >= 0 && mouseX <= (getWidth()-PADDLE_WIDTH)) {
			paddle.setLocation(mouseX , getHeight()-30);
		} 
	}
	
	/*
	 * This method puts down all the bricks with their correct color
	 * sequence. Then it creates the paddle and displays a starter message.
	 * The stater message is removed once the player clicks the canvas and 
	 * the game begins.  
	 */
	private void setup() {
		layDownBricks();
		createPaddle();
		starterMessage();
	}
	
	/*
	 * This creates the two labels that contain starting information
	 * for the user. It places them first and then moves them to center the 
	 * labels on the display properly. 
	 * It waits for a click, then removes the labels and the game starts. 
	 * The user must click for the game to start. This makes it a little
	 * bit easier for the user so they can start when they are fully 
	 * prepared. 
	 */
	private void starterMessage() {
		double labelX = (getWidth()/2);
		double labelY = (getHeight()/2+BALL_DIAMETER);
		GLabel welcomeMessage = new GLabel("Welcome to Breakout!", labelX, labelY);
		GLabel turnMessage = new GLabel ("You will have "+ numTurns + " turns to Breakout. Click to start!", getWidth()/2, getHeight()/2+BALL_DIAMETER);
		add(welcomeMessage);
		add(turnMessage);
		welcomeMessage.move(-(welcomeMessage.getWidth()/2), (welcomeMessage.getAscent()/2));
		turnMessage.move(-(turnMessage.getWidth()/2), (turnMessage.getAscent()/2)+welcomeMessage.getAscent());
		waitForClick();
		remove(welcomeMessage);
		remove(turnMessage);
	}

	/*
	 * This method places all of the bricks on the canvas using a nested
	 * for loop. The inner for loop lays down the bricks in one line. The
	 * outer for loop then lays down the line of bricks for as many rows
	 * there are supposed to be. 
	 * 
	 * The if statements determine the color 
	 * of the brick which is to follow a specific sequence. If a brickColor
	 * is a certain color, the sequence will continue to be another
	 * color. The color is only to change when i (which is related to 
	 * the row number) is odd. There is a special case for i=0 and i=1
	 * which is why the complex looking modulus equation is used.  
	 */
	private void layDownBricks() {
		double lengthOfRows = ((NBRICK_COLUMNS*BRICK_WIDTH)+((NBRICK_COLUMNS-1)*BRICK_SEP));
		double locX = (getWidth()-(lengthOfRows))/2;
		double locY = BRICK_Y_OFFSET;
		double numBricks = NBRICK_COLUMNS;
		for (int i = 0; i<NBRICK_ROWS; i++) {
			locY = locY + BRICK_SEP + BRICK_HEIGHT; 
			for (int j = NBRICK_COLUMNS; j>0; j--) {
				numBricks = j;
				double bricksInRow = NBRICK_COLUMNS-numBricks;
				add(makeBrick(locX+(bricksInRow*(BRICK_SEP+BRICK_WIDTH)), locY, brickColor));
			}
			if ((i-1)%2 == 0) {
				if (brickColor == Color.RED) {
					brickColor = Color.ORANGE;
				} else if (brickColor == Color.ORANGE) {
					brickColor = Color.YELLOW;
				} else if (brickColor == Color.YELLOW) {
					brickColor = Color.GREEN;
				} else if (brickColor == Color.GREEN) {
					brickColor = Color.CYAN;
				} else if (brickColor == Color.CYAN) {
					brickColor = Color.RED;
				}
			}
		}
	}
	
	/*
	 * This method places the rectangle that is the paddle onto 
	 * the canvas. It is later controlled my a mouse event. 
	 */
	private void createPaddle() {
		double paddleX = (getWidth()-PADDLE_WIDTH)/2;
		double paddleY = (getHeight()-PADDLE_Y_OFFSET);
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/*
	 * This method is used in laydownbricks to make the rectangles 
	 * a given color. Because they all have the same dimensions, it 
	 * is convenient to have a method that takes in their changing
	 * locations and changing colors and gives back the correct brick (GRect)
	 * to be added to the canvas later. 
	 * Input = x and y location of brick and its color. 
	 * Output = properly filled GRect with correct coordinates ready
	 * to be added to the screen. 
	 */
	private GRect makeBrick (double x, double y, Color color) {
		GRect brick = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
		brick.setColor(color);
		brick.setFilled(true);
		return brick;
	}
}
