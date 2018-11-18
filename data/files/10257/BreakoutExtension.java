/*
 * File: Breakout.java
 * -------------------
 * Name: Nicolas Guillen
 * Section Leader: Jonathan Kula
 * 
 * This program creates a much harder version of the breakout game, in which 
 * the player will have three turns to eliminate all the bricks in the game. 
 * However as the game progress the bricks are going to come closer and closer 
 * maxing it super hard to beat.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*; 

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {
	
	// Size of the scoreboard at the bottom of the canvas
	public static final double SCOREBOARD_SIZE = 30;
	
	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600 + SCOREBOARD_SIZE;

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
	public static final double PADDLE_TOP_WIDTH = 40;
	public static final double PADDLE_WIDTH = 100;
	public static final double PADDLE_HEIGHT = 20;
	
	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30 + SCOREBOARD_SIZE;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 8;
	
	//Margin for labels within the screen
	public static final double SCREEN_SEPARATION = 5;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 100.0;
	
	public static final double LABEL_SPEED = 1000.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	//indicates how often are the bricks going to move
	public static final int MOVE_EVERY_NTOUCHES = 5;
	
	public GRect scoreboard, canvasBlackout;
	
	public GPolygon paddle;
	
	public GOval ball;
	
	private int touchesInThePaddle, timesMoved;
	
	private int bricksRemaining = NBRICK_COLUMNS*NBRICK_ROWS;
	
	private int roundNumber = 0;
	
	public GLabel totalScore = new GLabel("Total Score: " + ((NBRICK_COLUMNS*NBRICK_ROWS-bricksRemaining)*100));
	
	public GLabel livesRemaining = new GLabel("Lives Remaining: " + (NTURNS - roundNumber));
	
	public GImage background;
	
	private double vx, vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*
	 * Method that runs the entire breakout game.
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setUpGame();
		playGame();
	}

	/*
	 * This method sets up the entire game, bricks paddle and balls.
	 */
	private void setUpGame() {
		setBackround();
		buildBricks();
		buildPaddle();
		buildScoreboard();
	}
	
	private void setBackround() {
		background = new GImage ("background.jpg", 0, 0);
		add(background);
	}

	/*
	 * Method that runs the playable part of the game.
	 */
	private void playGame() {
		while(roundNumber < NTURNS && bricksRemaining > 0) {
			roundNumber++;
			vy = VELOCITY_Y;
			vx = randomVX();		
			playRound();
		}
		finalMessage();
	}
	
	/*
	 * Method that generates a random VX for the ball.
	 */
	private double randomVX() {
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		return vx;
	}

	/* 
	 * Method that makes the player play one round.
	 */
	private void playRound() {
		introMeassage();
		buildBall();
		waitForClick();
		while(ballInBounds() && bricksRemaining > 0) {
			moveBall();
			bounceToWalls();
			bounceToElements();
			updateScoreboard();
		}
		remove(ball);
	}
	
	/*
	 * This method moves the ball in the indicated vx, vy, and the pauses the code to create animation.
	 */
	private void moveBall() {
		ball.move(vx, vy);
		pause(DELAY);
	}
	
	/*
	 * This method updates the scoreboard every time score changes.
	 */
	private void updateScoreboard() {
		totalScore.setLabel("Total Score: " + ((NBRICK_COLUMNS*NBRICK_ROWS-bricksRemaining)*100));
		livesRemaining.setLabel("Lives Remaining: " + (NTURNS - roundNumber));
	}

	/*
	 * Method that displays the initial message when the game is startin.
	 */
	private void introMeassage() {
		GLabel intro = new GLabel ("Round " + (roundNumber) + "!");
		intro.setFont(SCREEN_FONT);
		intro.setColor(Color.BLUE);
		intro.setLocation((getWidth() - intro.getWidth())/2, (getHeight() + intro.getAscent())/2);
		add(intro);
		pause(LABEL_SPEED*2);
		intro.setLabel("Ready!");
		intro.setLocation((getWidth() - intro.getWidth())/2, (getHeight() + intro.getAscent())/2);
		pause(LABEL_SPEED);
		intro.setLabel("Set!");
		intro.setLocation((getWidth() - intro.getWidth())/2, (getHeight() + intro.getAscent())/2);
		pause(LABEL_SPEED);
		intro.setLabel("Go!");
		intro.setLocation((getWidth() - intro.getWidth())/2, (getHeight() + intro.getAscent())/2);
		pause(LABEL_SPEED);
		remove(intro);
	}
	
	/*
	 * Method that displays the final message when the game is over.
	 */
	private void finalMessage() {
		removeAll();
		setBackground(Color.BLACK);
		if (bricksRemaining > 0) {
			GLabel outro = new GLabel ("GAME OVER");
			outro.setFont(SCREEN_FONT);
			outro.setColor(Color.WHITE);
			outro.setLocation((getWidth() - outro.getWidth())/2, (getHeight() + outro.getAscent())/2);
			add(outro);
			pause(LABEL_SPEED*2);
			remove(outro);
		} else {
			GLabel outro = new GLabel ("CONGRATULATIONS");
			outro.setFont(SCREEN_FONT);
			outro.setColor(Color.WHITE);
			outro.setLocation((getWidth() - outro.getWidth())/2, (getHeight() + outro.getAscent())/2);
			add(outro);
			pause(LABEL_SPEED);
			outro.setLabel("YOU WIN!");
			outro.setLocation((getWidth() - outro.getWidth())/2, (getHeight() + outro.getAscent())/2);
			pause(LABEL_SPEED*2);
			remove(outro);
		}
	}
	
	/*
	 * This method bounce to elements and eliminates them if they're not  a paddle.
	 */
	private void bounceToElements() {
		GObject collider = getCollider();
		if (!(collider == null || collider == scoreboard || collider == totalScore || collider == livesRemaining)) {
			if (!(collider == paddle)) {
				changeDirection();
			} else if (collider == paddle){
				bounceInPaddle();
				//will move bricks until they are past the middle of the canvas.
				if (canvasBlackout.getY()+CANVAS_HEIGHT < getHeight()/2) {
					moveBricks();
				}
			}
			if (!(collider == paddle || collider == canvasBlackout)) {
				removeBricks();	
			}
			//This while loop solves the sticky paddle problem
			while (collider == paddle) {
				moveBall();
				collider = getCollider();
			}
		}
	}

	private void bounceInPaddle() {
		if (ball.getX()+BALL_RADIUS < paddle.getX()-PADDLE_TOP_WIDTH/2 || ball.getX()+BALL_RADIUS > paddle.getX()+PADDLE_TOP_WIDTH/2) {
			vy = -vy;
			vx = -vx;
		} else {
			vy = -vy;
		}
	}

	/*
	 * This method bounce the ball when it goes out of bounds.
	 */
	private void bounceToWalls() {
		double topWall = 0;
		double leftWall = 0;
		double rightWall = getWidth();
		//changes direction when ball bounces to left or right wall.
		if (isBallPastX(ball.getX()+BALL_RADIUS*2, rightWall) || !isBallPastX(ball.getX(), leftWall)) {
			vx = -vx;
			//to solve sticky wall issue
			while(isBallPastX(ball.getX()+BALL_RADIUS*2, rightWall) || !isBallPastX(ball.getX(), leftWall)) {
				moveBall();
			}
		}
		//changes direction when wall bounces to top wall.
		if (!isBallPastX(ball.getY(), topWall)) {
			vy = -vy;
			//to solve sticky wall issue
			while(!isBallPastX(ball.getY(), topWall)) {
				moveBall();
			}
		}
	}

	/*
	 * Get at least one object that is colliding with the wall to later identify if that's a paddle.
	 */
	private GObject getCollider() {
		GObject collider = null;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (pointTouchesElement(ball.getX() + BALL_RADIUS*x, ball.getY() + BALL_RADIUS*y)) {
					GObject elementTouched = getElementAt(ball.getX() + BALL_RADIUS*x, ball.getY() + BALL_RADIUS*y);
					if (elementTouched != background) {
						collider = elementTouched;
					}
				}
			}
		}
		return collider;
	}

	/* 
	 * Method changeDirection is run when ball touches element, the method determines in what direction to bounce the ball
	 * and whether or not to remove the element.
	 */
	private void changeDirection() {
		//makes ball bounce in the exact opposite direction when hitting a corner.
		if (ballTouchesUpDown() && ballTouchesSideways()) {
			vy = -vy;
			vx = -vx;
		} else if (ballTouchesSideways()) {
			vx = -vx;
		} else if (ballTouchesUpDown()) {
			vy = -vy;
		} else {
			vy = -vy;
		}
	}
	
	/* CHANGED
	 * Method that eliminates all elements touched by the ball at the time that it's called.
	 */
	private void removeBricks() {
		//looks for objects in 8 points surround the ball and remove those objects.
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (pointTouchesElement(ball.getX() + BALL_RADIUS*x, ball.getY() + BALL_RADIUS*y)) {
					GObject elementTouched = getElementAt(ball.getX() + BALL_RADIUS*x, ball.getY() + BALL_RADIUS*y);
					if (elementTouched != background){
						remove(elementTouched);
						bricksRemaining--;	
					}
				}
			}
		}
	}

	/*
	 * Boolean that determines if a given point in the location x, y touches and element
	 */
	private boolean pointTouchesElement(double x, double y) {
		GObject element = getElementAt(x, y);
		return !(element == null || element == ball || element == background);
	}
	
	/*
	 * Boolean that determines if ball touches element sideways
	 */
	private boolean ballTouchesSideways() {
		return pointTouchesElement(ball.getX(), ball.getY() + BALL_RADIUS) || pointTouchesElement(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS);
	}

	/*
	 * Boolean that determines if ball touches element up and down
	 */
	private boolean ballTouchesUpDown() {
		return pointTouchesElement(ball.getX() + BALL_RADIUS, ball.getY()) || pointTouchesElement(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS*2);
	}
	
	/*
	 * Boolean that determines if ball touches north wall
	 */
	private boolean isBallPastX(double ball, double x) {
		return ball > x;
	}
	
	/*
	 * Boolean that determines if ball is in bounds
	 */
	private boolean ballInBounds() {
		return ball.getY() < getHeight();
	}
	
	/*
	 * This method builds a scoreboard in the bottom of the canvas.
	 */
	private void buildScoreboard() {
		canvasBlackout = new GRect(0, -CANVAS_HEIGHT, CANVAS_WIDTH, CANVAS_HEIGHT);
		scoreboard = new GRect (0, getHeight()-SCOREBOARD_SIZE, getWidth(), SCOREBOARD_SIZE);
		scoreboard.setFilled(true);
		add(scoreboard);
		totalScore.setColor(Color.WHITE);
		totalScore.setFont(SCREEN_FONT);
		totalScore.setLocation(SCREEN_SEPARATION, getHeight() - (SCOREBOARD_SIZE - totalScore.getAscent())/2);
		add(totalScore);
		livesRemaining.setColor(Color.WHITE);
		livesRemaining.setFont(SCREEN_FONT);
		livesRemaining.setLocation(getWidth() - (livesRemaining.getWidth() + SCREEN_SEPARATION), getHeight() - (SCOREBOARD_SIZE - totalScore.getAscent())/2);
		add(livesRemaining);
	}

	/*
	 * This method builds the ball.
	 */
	private void buildBall() {
		double ballX = getWidth()/2 - BALL_RADIUS;
		double ballY = getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT + BALL_RADIUS*3);
		ball = new GOval (ballX, ballY, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}

	/* 
	 * This method builds the paddle.
	 */
	private void buildPaddle() {
		paddle = new GPolygon ();
		paddle.addVertex(+PADDLE_TOP_WIDTH/2, 0);
		paddle.addVertex(-PADDLE_TOP_WIDTH/2, 0);
		paddle.addVertex(-PADDLE_WIDTH/2, PADDLE_HEIGHT);
		paddle.addVertex(+PADDLE_WIDTH/2, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setLocation(getWidth()/2, getHeight()-(PADDLE_Y_OFFSET+PADDLE_HEIGHT));
		add(paddle);	
	}
	
	/* 
	 * This mouse event method gets the x location of the mouse and use that x location 
	 * as middle x of the paddle.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleX = paddleX(mouseX);
		paddle.setLocation(paddleX, getHeight()-(PADDLE_Y_OFFSET+PADDLE_HEIGHT));
	}
	
	/*
	 * This method determines the minimum and maximum x values for the paddle
	 * @param mouseX receives as an argument the x location of the mouse
	 * @return the value paddleX
	 */
	private double paddleX(double mouseX) {
		double paddleX = mouseX;
		if (paddleX - PADDLE_TOP_WIDTH/2 < 0) {
			paddleX = PADDLE_TOP_WIDTH/2;
		} else if (paddleX + PADDLE_TOP_WIDTH/2 > getWidth()) {
			paddleX = getWidth() - PADDLE_TOP_WIDTH/2;
		}
		return paddleX;
	}

	/*
	 * This method builds all bricks for the breakout game
	 */
	private void buildBricks() {
		for (int y = 0; y < NBRICK_ROWS; y++) {
			for (int x = 0; x < NBRICK_COLUMNS; x++) {
				createBrick(x, y);
			}
		}
	}
	
	/*
	 * This method builds all bricks for the breakout game
	 */
	private void moveBricks() {
		int stepsMoved = 4;
		canvasBlackout.setFilled(true);
		add(canvasBlackout);
		touchesInThePaddle++;
		if (touchesInThePaddle == MOVE_EVERY_NTOUCHES) {
			for (int y = NBRICK_ROWS-1; y >= 0; y--) {
				for (int x = 0; x < NBRICK_COLUMNS; x++) {
					double initialX = BRICK_SEP + (getWidth() - ((BRICK_SEP + BRICK_WIDTH)*NBRICK_COLUMNS)- BRICK_SEP)/2;
					double xBrick = initialX + BRICK_WIDTH/2 + (BRICK_SEP + BRICK_WIDTH)*x;
					double yBrick = (BRICK_Y_OFFSET) + BRICK_HEIGHT/2 + (BRICK_SEP + BRICK_HEIGHT)*(y+timesMoved*stepsMoved);
					GObject brick = getElementAt(xBrick, yBrick);
					if (!(brick == null || brick == background)) {
						double newXbrick = brick.getX();
						double newYbrick = brick.getY();
						brick.setLocation(newXbrick, newYbrick + (BRICK_SEP + BRICK_HEIGHT)*stepsMoved);
					}	
				}
			}
			timesMoved++;
			canvasBlackout.setLocation(0, -CANVAS_HEIGHT + (BRICK_SEP + BRICK_HEIGHT)*(timesMoved*stepsMoved));	
			touchesInThePaddle = 0;
		}
	}
	
	/*
	 * This method builds each individual brick
	 * @param x indicates the x position of the brick
	 * @param y indicates the y position of the brick
	 */
	private void createBrick(int x, int y) {
		double initialX = BRICK_SEP + (getWidth() - ((BRICK_SEP + BRICK_WIDTH)*NBRICK_COLUMNS)- BRICK_SEP)/2;
		double xBrick = initialX + (BRICK_SEP + BRICK_WIDTH)*x;
		double yBrick = (BRICK_Y_OFFSET) + (BRICK_SEP + BRICK_HEIGHT)*y;
		GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		Color rowColor = rowColor(y);
		brick.setColor(rowColor);
		brick.setLocation(xBrick, yBrick);
		add(brick);
	}

	/*
	 * This method receives as an input the number of the row and returns 
	 * a variable containing the color corresponding to that row
	 */
	private Color rowColor(int y) {
		Color rowColor;
		switch (y % 10) {
		case 0: case 1: rowColor = Color.RED;
		break;
		case 2: case 3: rowColor = Color.ORANGE;
		break;
		case 4: case 5: rowColor = Color.YELLOW;
		break;
		case 6: case 7: rowColor = Color.GREEN;
		break;
		case 8: case 9: rowColor = Color.CYAN;
		break;
		default: rowColor = Color.BLACK;
		}	
		return rowColor;
	}


}
