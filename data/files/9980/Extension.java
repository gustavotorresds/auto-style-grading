/*
 * File: Extension.java
 * -------------------
 * Name: Vincent Nicandro
 * Section Leader: Rachel Gardner
 * 
 * Implements the game of Breakout with extensions, including:
 * - Title screen
 * - Instructions pop up before start of game
 * - Life counter
 * - Score counter, with different bricks counting differently
 * - Two levels of gameplay
 * - Increasing speed and decreasing width of paddle as gameplay progresses (kicker)
 * - End screen announcing win or loss
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Extension extends GraphicsProgram {

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

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		intro();
		while (level < 3) {		// Loops through setup and gameplay for two levels
			clear();
			setup();
			if (playLevel(level)) {		// If player successful with level, moves on to next level; otherwise break
				level++;
			} else {
				break;
			}
		}
		endGame();
	}
	
	// Prints title screen
	private void intro() {
		title = new GLabel("Breakout");
		title.setFont("Courier-30");
		add(title, (getWidth() - title.getWidth())/2, (getHeight() - title.getAscent())/2);
		
		GLabel play = new GLabel("click to play");
		add(play, (getWidth() - play.getWidth())/2, (getHeight() - play.getAscent())/2 + PADDLE_Y_OFFSET);
	
		waitForClick();
	}
	
	// Sets up screen with bricks and paddle
	private void setup() {
		addMouseListeners();
		createPaddle();
		createScore();
		createLives();
	}
	
	// Runs level of Breakout based on given level; returns true if bricks are
	// cleared before turns are exhausted, otherwise returns false
	private boolean playLevel(int level) {		
		createBricks(level);
		
		title.setColor(Color.BLACK);
		add(title, (getWidth() - title.getWidth())/2, (getHeight() + title.getAscent())/2);
		if (level == 1) {
			instructions();		// Prints instructions if first level; for both levels, prints out level number

			GLabel levelOne = new GLabel("Level One");
			add(levelOne, (getWidth() - levelOne.getWidth())/2, (getHeight() + levelOne.getAscent() + 1.5*PADDLE_Y_OFFSET)/2);
			waitForClick();
			remove(levelOne);
		} else if (level == 2) {
			GLabel levelTwo = new GLabel("Level Two");
			add(levelTwo, (getWidth() - levelTwo.getWidth())/2, (getHeight() + levelTwo.getAscent() + 1.5*PADDLE_Y_OFFSET)/2);
			waitForClick();
			remove(levelTwo);
		}
		
		title.setColor(Color.LIGHT_GRAY);	// Changes title to background
		
		for (;turns > 0; turns--) {	// Limits regeneration of ball to three lives
			lives.setText("Lives: " + turns);	// Updates life counter
			createBall();
			waitForClick();		// Waits for player to click to launch ball motion
			moveBall();
			if (count == 0) {
				return true;	// Returns true if before three turns, bricks are cleared
			}
		}
		return false;
	}
	

	// Creates paddle at bottom of screen
	private void createPaddle() {
		paddle = new GRect((getWidth() - PADDLE_WIDTH)/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	// When mouse is moved, paddle moves accordingly left or right to match mouse movement
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		// If mouse causes paddle to be cropped out of screen, keeps paddle within bounds of screen
		if (x < paddleWidth/2) {
			x = paddleWidth/2;
		} else if (x > (getWidth() - paddleWidth/2)) {
			x = getWidth() - paddleWidth/2;
		}
		// Mouse is kept at same y-component offset
		paddle.setCenterLocation(x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT/2);
	}
	
	// Creates score counter
	private void createScore() {
		score = new GLabel("Score: " + points);
		add(score, PADDLE_Y_OFFSET/2, getHeight() - (PADDLE_Y_OFFSET - score.getAscent())/2);
	}
	
	// Creates life counter
	private void createLives() {
		lives = new GLabel("Lives: " + turns);
		add(lives, getWidth() - lives.getWidth() - PADDLE_Y_OFFSET/2, getHeight() - (PADDLE_Y_OFFSET - lives.getAscent())/2);
	}
	
	// Creates brick pattern in rainbow pattern depending on level; for
	// level one, rectangular formation, for level two, inverted pyramid
	private void createBricks(int level) {
		double y = BRICK_Y_OFFSET;
		if (level == 1) {
			for (int row = 0; row < NBRICK_ROWS; row++) {	// For NBRICK_ROWS, creates a row of NBRICK_COLUMNS bricks
				double x = (getWidth() - (10 * BRICK_WIDTH) - (9 * BRICK_SEP))/2;
				for (int column = 0; column < NBRICK_COLUMNS; column++) {
					GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
					brick.setFilled(true);
					brick.setColor(colorBrick(row));
					add(brick);
					count++;
					x += BRICK_WIDTH + BRICK_SEP;
				}
				y += BRICK_HEIGHT + BRICK_SEP;
			}
		} else if (level == 2) {	// Similar to Pyramid assignment in Assignment Two
			for (int row = 10; row > 0; row--) {
				double x = (getWidth() - (row * BRICK_WIDTH) - ((row - 1) * BRICK_SEP))/2;
				for (int rect = 0; rect < row; rect++) {
					GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
					brick.setFilled(true);
					brick.setColor(colorBrick(10 - row));
					add(brick);
					count++;
					x += BRICK_WIDTH + BRICK_SEP;
				}
				y += BRICK_HEIGHT + BRICK_SEP;
			}
		}	
	}
	
	// Returns color based on which row brick is in
	private Color colorBrick(int row) {
		if (row % 10 < 2) {
			return Color.RED;
		} else if (row % 10 < 4) {
			return Color.ORANGE;
		} else if (row % 10 < 6) {
			return Color.YELLOW;
		} else if (row % 10 < 8) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}
	
	// Creates instructions object that flashes at beginning of game before start of
	// level one
	private void instructions() {
		GCompound instructions = new GCompound();
		
		GRect rect = new GRect(getWidth()/4, getHeight()/4, getWidth()/2, getHeight()/2);
		rect.setFilled(true);
		rect.setColor(Color.GRAY);
		instructions.add(rect);
		
		GLabel title = new GLabel("Instructions");
		title.setFont("Courier-18");
		title.setColor(Color.WHITE);
		instructions.add(title, (getWidth() - title.getWidth())/2, getHeight()/4 + title.getAscent() + PADDLE_Y_OFFSET/2);
		
		GLabel line1 = new GLabel("Welcome to Breakout!");
		line1.setColor(Color.WHITE);
		instructions.add(line1, (getWidth() - line1.getWidth())/2, getHeight()/4 + title.getAscent() + 1.5*PADDLE_Y_OFFSET);
		
		GLabel line2 = new GLabel("Use your cursor to move the");
		line2.setColor(Color.WHITE);
		instructions.add(line2, (getWidth() - line2.getWidth())/2, getHeight()/4 + title.getAscent() + 2.5*PADDLE_Y_OFFSET);
		
		GLabel line3 = new GLabel("paddle left and right.");
		line3.setColor(Color.WHITE);
		instructions.add(line3, (getWidth() - line3.getWidth())/2, getHeight()/4 + title.getAscent() + 3*PADDLE_Y_OFFSET);
		
		GLabel line4 = new GLabel("Be sure to keep the ball");
		line4.setColor(Color.WHITE);
		instructions.add(line4, (getWidth() - line4.getWidth())/2, getHeight()/4 + title.getAscent() + 4*PADDLE_Y_OFFSET);
		
		GLabel line5 = new GLabel("above the bottom of");
		line5.setColor(Color.WHITE);
		instructions.add(line5, (getWidth() - line5.getWidth())/2, getHeight()/4 + title.getAscent() + 4.5*PADDLE_Y_OFFSET);
		
		GLabel line6 = new GLabel("the screen!");
		line6.setColor(Color.WHITE);
		instructions.add(line6, (getWidth() - line6.getWidth())/2, getHeight()/4 + title.getAscent() + 5*PADDLE_Y_OFFSET);
		
		GLabel line7 = new GLabel("Higher bricks on the screen");
		line7.setColor(Color.WHITE);
		instructions.add(line7, (getWidth() - line7.getWidth())/2, getHeight()/4 + title.getAscent() + 6*PADDLE_Y_OFFSET);
		
		GLabel line8 = new GLabel("are worth more points!");
		line8.setColor(Color.WHITE);
		instructions.add(line8, (getWidth() - line8.getWidth())/2, getHeight()/4 + title.getAscent() + 6.5*PADDLE_Y_OFFSET);

		GLabel line9 = new GLabel("You only have three lives:");
		line9.setColor(Color.WHITE);
		instructions.add(line9, (getWidth() - line9.getWidth())/2, getHeight()/4 + title.getAscent() + 7.5*PADDLE_Y_OFFSET);
		
		GLabel line10 = new GLabel("make them count!");
		line10.setColor(Color.WHITE);
		instructions.add(line10, (getWidth() - line10.getWidth())/2, getHeight()/4 + title.getAscent() + 8*PADDLE_Y_OFFSET);
		
		GLabel line11 = new GLabel("Good Luck!");
		line11.setFont("Courier-18");
		line11.setColor(Color.WHITE);
		instructions.add(line11, (getWidth() - line11.getWidth())/2, getHeight()/4 + title.getAscent() + 9*PADDLE_Y_OFFSET);
		
		add(instructions);
		waitForClick();
		remove(instructions);
	}
	
	// Generates ball at center of screen
	private void createBall() {
		ball = new GOval((getWidth() - 2 * BALL_RADIUS)/2, (getHeight() - 2 * BALL_RADIUS)/2, 
				2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	// Animates movement of ball for ball's lifetime
	private void moveBall() {
		// Generates random x-component for speed of ball
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;	// Randomly determines if x-component is negative or positive
		}
		vy = VELOCITY_Y;
		
		while (true) {
			if (count == 0) {	// Breaks ball movement if player has cleared the level
				remove(ball);
				paddleWidth = PADDLE_WIDTH;		// Resets paddleWidth to original width
				break;
			}
		
			double x = ball.getCenterX() + vx;
			double y = ball.getCenterY() + vy;
			
			// If ball reaches bottom edge of screen, ball is void
			if (y > getHeight() - BALL_RADIUS) {
				remove(ball);
				paddleWidth = PADDLE_WIDTH;		// Resets paddleWidth to original width if level not clear
				paddle.setSize(paddleWidth, PADDLE_HEIGHT);
				return;
			}
			
			// Bounces ball from sides and top of screen
			if (x < BALL_RADIUS || x > getWidth() - BALL_RADIUS) {
				vx = -vx;
			}
			if (y < BALL_RADIUS) {
				vy = -vy;
			}
			
			checkForCollisions();	// Checks for collisions (from brick or paddle)
						
			ball.setCenterLocation(ball.getCenterX() + vx, ball.getCenterY() + vy);
			pause(DELAY);
		}
	}
	
	// Checks for collisions: if collider is brick, removes brick and decreases count
	// by one, then ball bounces off object. If collider is paddle, ball bounces off paddle.
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		if (collider != null && collider != score && collider != lives && collider != title) {		// If collider isn't null, means it's either paddle or brick
			bounceClip.play();
			if (collider == paddle) {	// If paddle, ensure ball moves in upwards direction
				vy = -Math.abs(vy);
			} else {	// Otherwise, object is brick, so remove brick, decrease count, and reverse y-component of velocity
				points += updateScore(collider);
				remove(collider);
				score.setText("Score: " + points);	// Updates score counter
				count--;
				if (count % 10 == 0) {	// The Kicker: for every 10 bricks, multiplies vy by 1.1 and reduces paddle width to 0.9x size
					vy *= 1.1;
					paddleWidth *= 0.9;
					paddle.setSize(paddleWidth, PADDLE_HEIGHT);
				}
				vy = -vy;
			}
		}
	}
	
	// Returns score based on color of brick
	private int updateScore(GObject collider) {
		if (collider.getColor() == Color.CYAN) {
			return 100;
		} else if (collider.getColor() == Color.GREEN) {
			return 200;
		} else if (collider.getColor() == Color.YELLOW) {
			return 300;
		} else if (collider.getColor() == Color.ORANGE) {
			return 400;
		} else {
			return 500;
		}
	}
	
	// Returns object ball collides into, if it exists, by checking corners of GOval
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {	// Checks top-left corner of GOval
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getRightX(), ball.getY()) != null) {	// Checks top-right corner of GOval
			return getElementAt(ball.getRightX(), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getBottomY()) != null) {	// Checks bottom-left corner of GOval
			return getElementAt(ball.getX(), ball.getBottomY());
		} else if (getElementAt(ball.getRightX(), ball.getBottomY()) != null) {		// Checks bottom-right corner of GOval
			return getElementAt(ball.getRightX(), ball.getBottomY());
		} else {
			return null;
		}
	}
	
	// Prints end credits and notifies players whether they won or lost the game
	private void endGame() {
		clear();
		if (count == 0) {	//  If player has cleared bricks, clears screen and announces win; else, announces loss			
			GLabel win = new GLabel("You win!");
			win.setFont("Courier-30");
			add(win, (getWidth() - win.getWidth())/2, (getHeight() - win.getAscent())/2);
		} else {
			GLabel lose = new GLabel("You lose!");
			lose.setFont("Courier-30");
			add(lose, (getWidth() - lose.getWidth())/2, (getHeight() - lose.getAscent())/2);
		}
		score.setText("Final Score: " + points);	// Prints final score
		add(score, (getWidth() - score.getWidth())/2, (getHeight() - score.getAscent())/2 + PADDLE_Y_OFFSET);
	}
	
	// Private instance variables
	private GRect paddle;
	private GOval ball;
	private double vx,
			vy,
			paddleWidth = PADDLE_WIDTH;
	private int count,
			level = 1,
			turns = NTURNS,
			points = 0;
	private GLabel title, score, lives;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
}
