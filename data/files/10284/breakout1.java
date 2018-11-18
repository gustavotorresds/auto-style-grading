/*
 * File: Breakout.java
 * -------------------
 * Name: Isaac Osafo Nkansah
 * Section Leader: Andrew Marshall
 * 
 * This file implements the ordinary game version of Breakout.
 * The extension is named Breakout
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class breakout1 extends GraphicsProgram {

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
	private double ballDiameter = BALL_RADIUS*2;
	private GObject collider = null;
	private GRect gameOver = null;
	private GRect tryLeft = null;
	private GLabel trialRemainder = null;
	private GLabel gameOverLabel = null;
	private GLabel youWinLabel;
	private int counter = 0;
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
		setBackground(new Color(230, 240, 240)); //rgb values
		createGameSpace();
		createBall();
		createPad();
		animateBall();
	}
	
	//This creates the bricks within the game space
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

	//As the name suggests: this creates the paddle off of which the ball bounces
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
	
	//As the name suggests: this creates the ball
	private void createBall() {
		double windowHeight = getHeight();
		double windowWidth = getWidth();
		double ballX = (windowWidth - ballDiameter)/2;
		double ballY = (windowHeight -ballDiameter)/2;
		ball = new GOval(ballX, ballY, ballDiameter, ballDiameter);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}

	//As the name suggests: this animates the ball to add the key dynamics to the breakout game
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
			}
			
			if(ball.getY() > (windowHeight - PADDLE_Y_OFFSET - PADDLE_HEIGHT - ballDiameter)) {
				if(ball.getX() > pad.getX() - BALL_RADIUS && ball.getX() < pad.getX() + PADDLE_WIDTH + BALL_RADIUS) {
					vy =-Math.abs(vy);
				}
			}
			
			
			if (ball.getY() > (windowHeight - PADDLE_Y_OFFSET) && ball.getY()<windowHeight) {
				checkForBallHittingPaddle();
			}
			
			if (counter == 100) {
				createWinLabel();
				// This ensures that the ball moves towards the brick when the game is restarted
				vy = -vy;
			}
			pause(DELAY);
		}
	}
	
	private void checkForBallHittingPaddle() {
		if (ball.getX() < (pad.getX() ) || (ball.getX() > pad.getX() + PADDLE_WIDTH)) {
			tryCounter -= 1;
			/*
			 * Generates label for when player has exhausted 1 of his/her tries
			 */
			if (tryCounter == 2) {
				trialRemainder = new GLabel("You have "+ tryCounter +" tries left");
			}

			/*
			 * Generates label for when player has exhausted 2 of his/her tries
			 */
			if (tryCounter == 1) {
				trialRemainder = new GLabel("You have "+ tryCounter +" try left");
			}

			if (tryCounter == 0) {
				trialRemainder = new GLabel("You have "+ tryCounter +" tries left");
			}
			
			//Checks how many turns the user has left and displays it to him/her
			remainderNotifier();
						
			/*
			 * Generates label for when player has exhausted all of his/her tries
			 * This adds the game over screen and label when the player runs out of tries
			 */
			if (tryCounter == 0) {
				gameOverCreator();
				tryCounter = NTURNS;
			}
		}
	}
	
	//Notifies the user of the remaining "lives"
	private void remainderNotifier() {
		trialRemainder.setFont("SanSeriff-30");
		trialRemainder.setColor(Color.WHITE);
		double trialRemainderX = trialRemainder.getWidth();
		double trialRemainderY = trialRemainder.getAscent();
		double trialRemainderXPos = (getWidth() -trialRemainderX)/2;
		double trialRemainderYPos = (getHeight() - trialRemainderY)/2 + trialRemainderY;

		add(tryLeft);
		add(trialRemainder, trialRemainderXPos, trialRemainderYPos);
		pause(1000);
		resetGame();
		if(tryCounter == 1||tryCounter == 2) {
			waitForClick();
		}
		vy =-vy;
	}
	
	// Creates a win label when the player gets all the bricks without exhausting his/her "lives"
	private void createWinLabel() {
		youWinLabel = new GLabel("Congratulations, You Win");
		youWinLabel.setFont("SanSeriff-30");
		youWinLabel.setColor(Color.WHITE);
		double youWinLabelX = youWinLabel.getWidth();
		double youWinLabelY = youWinLabel.getAscent();
		double youWinLabelXPos = (getWidth() - youWinLabelX)/2;
		double youWinLabelYPos = (getHeight() - youWinLabelY)/2 + youWinLabelY;
		add(tryLeft);
		add(youWinLabel, youWinLabelXPos, youWinLabelYPos);
		waitForClick();
		counter = 0;
		replayGame();
	}

	private void replayGame() {
		createGameSpace();
		createBall();
		createPad();
		waitForClick();
	}

	/*
	 * Resetting the game
	 * First remove previous ball and pad
	 * waits for click and creates a new ball and pad
	 */
	private void resetGame() {
		/*
		 * Resetting the game
		 * First remove previous ball and pad
		 */
		remove(trialRemainder);
		remove(tryLeft);
		remove(ball);
		remove(pad);

		/*
		 *Wait for click to remove blank white space and Add new pad and bricks
		 */
		createBall();
		createPad();
		vy = -vy;
	}
	
	//Checks to see if the animated ball is touching either brick/paddle
	private boolean isBallTouchingBrickOrPaddle() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		collider = getColliderObject(ballX, ballY);
		if (collider != null) {
			return true;
		} else {
			return false;
		}
	}
	
	//Gets elements at specific locations within the game space
	private GObject getColliderObject(double X, double Y) {
		GObject element = getElementAt(X, Y);
		return element;
	}
	
	//Removes bricks from game
	private void brickRemover(double X, double Y) {
		for(int row  = 0; row < 2; row++) {
			for (int col = 0; col < 2; col ++) {
				X = ball.getX() + col*ballDiameter;
				Y = ball.getY() + row*ballDiameter;

				collider = getColliderObject(X, Y);
				if (collider != null && collider.getColor() != Color.BLACK) {
					remove(collider);
					counter += 1;
					}
				}
			}
		}
	
    //This creates and adds GLabel and GRect GObjects to signal the end of the game, when player looses
	private void gameOverCreator() {
		//This code creates the new background that displays the Game Over label
		gameOver = new GRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		gameOver.setFilled(true);
		gameOver.setColor(new Color(120, 120, 120)); //rgb values

		//This code creates a white Game Over label
		gameOverLabel = new GLabel ("GAMEOVER");
		gameOverLabel.setFont("SanSeriff-36");
		gameOverLabel.setColor(Color.WHITE);
		double gameLabelX = gameOverLabel.getWidth();
		double gameLabelY = gameOverLabel.getAscent();
		double gLabelXPos = (getWidth() -gameLabelX)/2;
		double gLabelYPos = (getHeight() - gameLabelY)/2 + gameLabelY;


		add(gameOver);
		add(gameOverLabel, gLabelXPos, gLabelYPos);

		waitForClick();
		
		//Gets rid of all the GObjects on canvas and resets the game
		removeAll();
		counter = 0;
		replayGame();
	}

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

