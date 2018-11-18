/*
 * File: Breakout.java
 * -------------------
 * Name: Isaac Osafo Nkansah
 * Section Leader: Andrew Marshall
 * 
 * This file implements the extension of the ordinary game of Breakout.
 * The ordinary breakout version is named breakout1
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

	private GRect pad = null;
	private GRect brick = null;
	private GOval ball = null;
	private GLabel scoreDisplay = new GLabel("Score:");
	private GLabel livesLeft = new GLabel("Lives: ");
	private GImage backgroundImage = new GImage("blue_sea.jpg");
	private GImage youWin = new GImage("youwin.jpg");
	private int count = 0;
	private double ballDiameter = BALL_RADIUS*2;
	private GObject collider = null;
	private GImage gameOver = null;
	private GRect tryLeft = null;
	private int counter = 0;
	private double gameSpeed = DELAY;
	private int kicker = 0;
	private int tryCounter  = NTURNS;
	private double vx;
	private double vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		addMouseListeners();
		addKeyListeners();
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		backgroundImage.scale(0.109375, 0.25);
		add(backgroundImage, 0, 0);
		createGameSpace();
		createBall();
		createPad();
		animateBall();
	}
	
	//This is the bricks creator
	private void createGameSpace() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double brickX = BRICK_SEP + col*(BRICK_WIDTH + BRICK_SEP) ;
				double brickY = BRICK_Y_OFFSET + row*(BRICK_HEIGHT + BRICK_SEP);
				brick = new GRect (brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (row == 0 || row == 1) {
					brick.setColor(Color.RED);
				} else if (row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
				} else if (row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				} else if (row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				} else {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}

	//As the name suggests, this creates the paddle
	private void createPad() {
		double windowHeight = getHeight();
		double windowWidth = getWidth();
		double padX = (windowWidth - PADDLE_WIDTH)/2;
		double padY = (windowHeight - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		pad = new GRect(padX, padY, PADDLE_WIDTH, PADDLE_HEIGHT);
		pad.setFilled(true);
		pad.setFillColor(Color.BLACK);
		add(pad);
	}
	
	//As the name suggests, this creates the ball that bounces around
	private void createBall() {
		double windowHeight = getHeight();
		double windowWidth = getWidth();
		double ballX = (windowWidth - ballDiameter)/2;
		double ballY = (windowHeight - ballDiameter)/2;
		ball = new GOval(ballX, ballY, ballDiameter, ballDiameter);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}

	//As the name suggests, this animates the ball created above...
	private void animateBall() {
		double windowHeight = getHeight();
		double windowWidth = getWidth();
		tryLeft = new GRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		tryLeft.setFilled(true);
		tryLeft.setColor(new Color(120, 120, 120)); //rgb values for grey

		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;

		vy = VELOCITY_Y;
		
		waitForClick();
		while(true) {
			ball.move(vx, vy);
			/*
			 * This sets the label lives left = 3 whiles the program runs.
			 * This runs so long as the player is able to keep the ball on the paddle
			 */
			if(tryCounter == 3) {
				livesLeft();
			}

			if ((ball.getX() > (windowWidth - ballDiameter)) || (ball.getX() < BRICK_SEP)){
				vx = -vx;
			}

			if(ball.getY() < ballDiameter) {
				vy = -vy;
			}

			if(isBallTouchingBrickOrPaddle()) {
				
				if(collider.getColor() != Color.BLACK) {
					vy = -vy;
				}
				
				brickRemover(ball.getX(), ball.getY());
				createScoreLabel();
			}
			
			if(ball.getY() > (windowHeight - PADDLE_Y_OFFSET - PADDLE_HEIGHT - ballDiameter)) {
				if(ball.getX() > pad.getX() - BALL_RADIUS && ball.getX() < pad.getX() + PADDLE_WIDTH + BALL_RADIUS) {
					
					/*
					 * This increments the speed by 2 after the player successfully hits 7 bricks successively
					 */
					count += 1;
					
					if(count%7 == 0) {
						kicker += 2;
						gameSpeed = DELAY - kicker;
					}
					
					/*
					 * Deals with the sticky paddle problem
					 */
					vy =-Math.abs(vy);
				}
			}
			
			
			if (ball.getY() > (windowHeight - PADDLE_Y_OFFSET) && ball.getY()<windowHeight) {
				checkForBallHittingPaddle();
			}

			/*
			 * This increments the speed by 5 after the player successfully hits 7 bricks successively
			 */
			
			if (counter == 240) {
				createWinLabel();
				// This ensures that the ball moves towards the brick when the game is restarted
				vy = -vy;
			}
			
			/*
			 * Ensures that the gameSpeed never gets to zero to keep the animation characteristic of the game
			 */
			if(gameSpeed < 5) {
				gameSpeed = 5;
			}
			
			pause(gameSpeed);
		}
	}
	
	private void checkForBallHittingPaddle() {
		if (ball.getX() < (pad.getX() ) || (ball.getX() > pad.getX() + PADDLE_WIDTH)) {
			livesLeft();
			
			tryCounter -= 1;
			
			/*
			 * Checks to see if try counter == 2 and sets the lives left label to 2: 
			 * Only runs once
			 */
			if(tryCounter == 2) {
				livesLeft();
				waitForClick();
			}
			
			/*
			 * Checks to see if try counter == 2 and sets the lives left label to 1
			 * Only runs once
			 */
			if(tryCounter == 1) {
				livesLeft();
				waitForClick();
			}
			
			// Checks how many turns the user has left and displays it to him/her
			//remainderNotifier();
						
			/*
			 * Generates label for when player has exhausted all of his/her tries
			 * This adds the game over screen and label when the player runs out of tries
			 */
			if (tryCounter == 0) {
				gameOverCreator();
				//vy = -vy;
				tryCounter = NTURNS;
			}
		}
	}
	
	/*
	 * This method simply uses a GImage to creates a congratulatory message to the player
	 * after he/she wins the game
	 */
	private void createWinLabel() {
		youWin = new GImage("youwin.jpg");
		youWin.scale(0.93334, 1.329787);
		add(youWin, 0, 0);
		
		waitForClick();
		counter = 0;
		removeAll();
		replayGame();
	}

	/*
	 * This method is invoked upon the player ending the game: either by loosing or winning
	 */
	private void replayGame() {
		add(backgroundImage, 0, 0);
		createGameSpace();
		createBall();
		createPad();
		gameSpeed = DELAY;
		waitForClick();
	}
	
	/*
	 * This keeps track of the number of lives left
	 * Three lives per game
	 */
	private void livesLeft() {
		livesLeft.setFont("SanSeriff-28");
		livesLeft.setLabel("Lives left: " + tryCounter);
		double livesX = livesLeft.getWidth();
		double livesY = livesLeft.getAscent();
		
		/*
		 * The lives left and score labels are just added to the canvas
		 */
		if(tryCounter == 3) {
		add(livesLeft, (getWidth() - livesX), livesY);
		} else {
		add(livesLeft, (getWidth() - livesX), livesY);
		remove(ball);
		remove(pad);
		/*
		 *Wait for click to remove blank white space and Add new pad and bricks
		 */
		createBall();
		createPad();
		gameSpeed = DELAY;
		}
	}
	
	/*
	 * This keeps track of the number of bricks removed from the set of bricks within the game
	 * Counts until total == 240
	 */
	private void createScoreLabel() {
		double windowWidth = getWidth();
		scoreDisplay.setLabel("Score:" + counter);
		scoreDisplay.setFont("SanSeriff-28");
		double scoreX = scoreDisplay.getWidth();
		double scoreY = scoreDisplay.getAscent();
		double scoreXLoc = (windowWidth - scoreX);
		
		/*
		 * The lives left and score labels are just added to the canvas
		 */
		add(scoreDisplay, scoreXLoc, scoreY + livesLeft.getAscent());
	}

	/*
	 * This method checks to see if the ball is hitting the paddle or brick
	 */
	private boolean isBallTouchingBrickOrPaddle() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		//double windowHeight = getHeight();
		collider = getColliderObject(ballX, ballY);
		if (collider != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * This method collects elements from the gamespace to be later identified as brick/paddle
	 */
	private GObject getColliderObject(double X, double Y) {
		GObject element = getElementAt(X, Y);
		return element;
	}

	/*
	 * This method simply removes bricks from the game.
	 * Additionally, it assigns different worth to different coloured bricks to add to the 
	 * dynamics of the game
	 */
	private void brickRemover(double X, double Y) {
		for(int row  = 0; row < 2; row++) {
			for (int col = 0; col < 2; col ++) {
				X = ball.getX() + col*ballDiameter;
				Y = ball.getY() + row*ballDiameter;

				collider = getColliderObject(X, Y);
				if (collider != null && collider.getColor() != Color.BLACK) {
					if(collider.getColor() == Color.RED) {
						bounceClip.play();
						remove(collider);
						counter += 5;
					} else if(collider.getColor() == Color.ORANGE) {
						bounceClip.play();
						remove(collider);
						counter += 3;
					} else if(collider.getColor() == Color.YELLOW) {
						bounceClip.play();
						remove(collider);
						counter += 2;
					} else {
						bounceClip.play();
						remove(collider);
						counter += 1;
					}
				}
			}
		}
	}
	
	/*
	 * This simply creates and adds a gameover image when the player looses 
	 */
	private void gameOverCreator() {
		gameOver = new GImage("GameOver.jpg", 0, 0);
		gameOver.scale(0.21875, 0.555556);
		add(gameOver, 0, 0);

		waitForClick();

		removeAll();
		counter = 0;
		replayGame();
	}
	
	/*
	 * (non-Javadoc)
	 * @see acm.program.Program#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		double windowWidth = getWidth();
		double windowHeight = getHeight();
		double locX = e.getX();
		double padConstantY = windowHeight - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		double padConstantX = (windowWidth - PADDLE_WIDTH);
		if(locX < 0) {
			pad.setLocation(0, padConstantY);
		} else if (locX > windowWidth - PADDLE_WIDTH) {
			pad.setLocation(padConstantX, padConstantY);
		} else {
			pad.setX(locX);
		}
	}
}

