/*
 * Name: Michael Oduoza
 * Section Leader: Andrew Marshall
 * 
 * This is the extended project. The extension has a scoretracker, vibrant imagery, a kicker that
 * adjusts difficulty, and messages/labels that improve game-user interface. 
 * The basic functionality of the game remains unchanged: This program implements the game 
 * Breakout, in which the user tries to repeatedly bounce a ball off a paddle in order to destroy 
 * a stack of bricks and "break out". The user must not allow the ball to 
 * fall beyond the paddle.  
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

	private GImage background;
	private GImage paddle;
	private GImage ball; 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int brickCount = NBRICK_COLUMNS * NBRICK_ROWS;
	private GLabel scoreLabel = new GLabel("Score: " + 0);
	private GLabel turnsLabel = new GLabel ("Tries left: " + NTURNS);
	private int score; 
	private int paddleStrikeCount = 0;
	private double kicker = EASY_DELAY; //declared as an instance variable so the kicker is not reset every time

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

	// The ball's minimum and maximum horizontal velocity
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Initial animation delay or pause time between ball moves (ms)- for easy mode 
	public static final double EASY_DELAY = 1000.0/90.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public void run() {
		setUpGame();

		//A loop for the number of turns the user has
		for(int turnsLeft = NTURNS; turnsLeft > 0; turnsLeft--) {
			serveBall();
			addMouseListeners();

			while(!endGameCondition()) {
				waitForClick();
				playGame(turnsLeft);
			}
			
			resetRound(turnsLeft);
		}
		//If we get here, the whole game must have ended (either the user won, or used all his/her turns)
		provideFinalMessage();
	}


	/*
	 * This method gives the user a message after they lose 1 turn, and then resets the game for the next round/turn (if any are left).
	 * It also waits for a user click before beginning the next round. 
	 */
	private void resetRound(int turnsLeft) {
		remove(ball); //Gets rid of the old ball

		//Creates a red background
		GRect redBackground = new GRect(0,0, getWidth(), getHeight());
		redBackground.setFilled(true);
		redBackground.setColor(Color.RED);

		//Creates a label to display no. of turns left
		GLabel noOfTurns = new GLabel("You have " + (turnsLeft -1) + " turns left! Click to try again!");
		noOfTurns.setFont("Courier-23");
		double xCoordinate = getWidth()/2 - noOfTurns.getWidth()/2;
		double yCoordinate = getHeight()/2 - noOfTurns.getAscent()/2;

		if (userLoses()) {
			if (turnsLeft > 1) {
				add(redBackground);
				add(noOfTurns, xCoordinate, yCoordinate );
				waitForClick();
				remove(noOfTurns);
				remove(redBackground);
			}
		}
	}


	//This method provides the appropriate closing message to the user after the whole game is over
	private void provideFinalMessage() {
		if (userLoses()) {
			//Displays a "you have lost" message to the user
			GImage finalLoss = new GImage("finalLoss.jpg", 0, 0); 
			finalLoss.setBounds(0, 0, getWidth(), getHeight());
			add(finalLoss);

			//Displays the user's final score after the game is over
			GLabel displayScore = new GLabel("Your score was: " + score);
			displayScore.setColor(Color.RED);
			displayScore.setFont("Courier-23");
			double xCoordinate = (getWidth() - displayScore.getWidth())/2.0;
			double yIndent = (getHeight() - displayScore.getAscent()); 
			add(displayScore, xCoordinate, yIndent);

		} else {
			//Displays a "you have won" message to the user
			GImage win = new GImage("win.jpg", 0, 0); 
			win.setBounds(0, 0, getWidth(), getHeight());
			add(win);
		}
	}


	/*
	 * Either the userWins or the userLoses condition will
	 * terminate the game/round. The userWins condition will immediately terminate the whole game
	 * while the userLoses condition will terminate the current round and give the user
	 * another try, if there are any left. 
	 */
	private boolean endGameCondition(){
		return (userWins() || userLoses());
	}


	//The user loses if the ball falls below the paddle
	private boolean userLoses() {
		return ball.getY() > (getHeight() - PADDLE_Y_OFFSET);
	}


	//The user wins if all the bricks have been removed
	private boolean userWins() {
		return brickCount == 0;
	}


	//This mouseMoved method is here to make the paddle follow the mouse
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleXCoordinate = mouseX - PADDLE_WIDTH/2;
		double paddleYCoordinate = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

		
		//Prevents the paddle from going off the screen
		boolean paddleIsAtEdge = (mouseX < PADDLE_WIDTH/2 || mouseX > getWidth() - PADDLE_WIDTH/2);
		if(!paddleIsAtEdge) {

			//Sometimes the user may move the mouse before the program begins, and a nullpointer exception will be thrown.
			//We prevent this with an if statement:
			if(paddle!= null) { 
				paddle.setLocation(paddleXCoordinate, paddleYCoordinate); 
			}
		}
	}


	// Performs all the necessary initial setup for the game before the game begins  
	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		background = new GImage("background.jpg", 0, 0); 
		background.setBounds(0, 0, getWidth(), getHeight());
		add(background);
		setUpBricks();
		makePaddle();
	} 


	// Sets up the bricks at the top of the console
	private void setUpBricks() {
		setUpRows(Color.RED, NBRICK_ROWS);
		setUpRows(Color.ORANGE, NBRICK_ROWS - NBRICK_ROWS/5);
		setUpRows(Color.YELLOW, NBRICK_ROWS - 2 * NBRICK_ROWS/5);
		setUpRows(Color.MAGENTA, NBRICK_ROWS - 3 * NBRICK_ROWS/5);
		setUpRows(Color.BLUE, NBRICK_ROWS - 4 * NBRICK_ROWS/5);
	}


	/*Sets up the rows for each color (2 rows per color); the parameters it takes in are:
	 * a) the color 
	 * b) the number associated with the "top row" of each color. 
	 * In the game we are considering, there are 10 rows total, and I set the row at the very top of the brick stack 
	 * as 10 and the row at the bottom of the brick stack as 1. Since we have 2 colors per row, the top row number goes 10, 8,
	 * 6, 4, 2, for RED, ORANGE, YELLOW, GREEN (MAGENTA FOR THE EXTENSION), and CYAN (BLUE for the extension). 
	 * If there were 20 rows, it would go 20, 16, 12, 8, and 4 (because then we would have 4 rows per color instead of two). 
	 * As we move down the brick stack, the number associated with each row decreases by 1. We divide NBRICK_ROWS by 5 in the formula
	 * because there are 5 colors, each of which we want to have an equal no. of rows. 
	 */
	private void setUpRows(Color color, int topRowNumber) {
		double startingXCoordinate = (getWidth() - (NBRICK_COLUMNS - 1) * BRICK_SEP)/2.0 - NBRICK_COLUMNS/2.0 * BRICK_WIDTH;
		double startingYCoordinate = BRICK_Y_OFFSET;

		//A loop to repeatedly set up rows
		for(int rowNumber = topRowNumber; rowNumber > (topRowNumber - NBRICK_ROWS/5); rowNumber--) {

			//A loop to set up each row by repeatedly adding bricks
			for (int columnNumber = 0; columnNumber < NBRICK_COLUMNS; columnNumber++) {
				double xCoordinate = startingXCoordinate + (BRICK_WIDTH + BRICK_SEP) * columnNumber;
				double yCoordinate = startingYCoordinate + ((NBRICK_ROWS - rowNumber) * (BRICK_HEIGHT + BRICK_SEP));
				GRect brick = new GRect(xCoordinate, yCoordinate, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick);
			}
		}	
	}


	/*Makes the paddle initially and places it in the middle
	 * before the game starts (i.e. the x coordinate of the paddle is in the middle)
	 * The y coordinate of the paddle is set appropriate, according to PADDLE_Y_OFFSET
	 */
	private void makePaddle() {
		double initialXCoordinate = (getWidth() - PADDLE_WIDTH)/2;
		double initialYCoordinate = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle = new GImage("paddle.jpg");
		paddle.setBounds(initialXCoordinate, initialYCoordinate, PADDLE_WIDTH, PADDLE_HEIGHT);
		add(paddle);
	}


	/*Makes the ball initially before the game starts
	 * and places it right in the middle of the space
	 * in between the bricks and the paddle 
	 */
	private void serveBall() {
		double ballSize = 2 * BALL_RADIUS; // The diameter of the ball

		double topOfPaddle = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double bottomOfBricks = (BRICK_Y_OFFSET + NBRICK_ROWS * (BRICK_HEIGHT + BRICK_SEP)); //fix this midpoint

		double initialXCoordinate = (getWidth() - ballSize)/2;
		double initialYCoordinate = (topOfPaddle + bottomOfBricks)/2 - ballSize/2;

		ball = new GImage("ball.jpg");
		ball.setBounds(initialXCoordinate, initialYCoordinate, ballSize, ballSize);
		add(ball);
	}


	/*
	 * This method animates the ball and gets it moving the way it is supposed to
	 * It also makes the ball interact with the bricks, walls, and paddle in the manner that it is supposed to.  
	 */
	private void playGame(int turnsLeft) {
		//Initially adds the score label
				scoreLabel.setFont("Courier-24"); 
				add(scoreLabel, 0, scoreLabel.getAscent());
				
				//A label to tell the user the number of tries they have left; it will update after the user clicks
				turnsLabel.setFont("Courier-24");
				add(turnsLabel, getWidth() - turnsLabel.getWidth(), turnsLabel.getAscent());
				turnsLabel.setLabel("Tries left: " + turnsLeft);

				vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
				if (rgen.nextBoolean(0.5)) vx = -vx;
				vy = VELOCITY_Y;

				while(!endGameCondition()) {
					  if(hitLeftWall(ball) || hitRightWall(ball)) {
						vx = -vx;
					}
					if(hitTopBoundary(ball)) {
						vy = -vy;
					}
					getCollidingObject();
					resolveCollision();
					ball.move(vx,vy);
					pause(kicker); //Pause according to the difficulty of the kicker
				}
			}

		
	//checks if the ball hit the left wall
	private boolean hitLeftWall(GImage ball) {
		return ball.getX() < 0;
	}


	//checks if the ball hit the right wall
	private boolean hitRightWall(GImage ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}


	//checks if the ball hit the top boundary 
	private boolean hitTopBoundary(GImage ball) {
		return ball.getY() < 0;
	}


	/*
	 * This method checks for the presence of a colliding object with respect to the ball
	 * in each frame. It checks the "four corners" of the ball and returns the colliding object 
	 * if it exists and returns null otherwise. 
	 */
	private GObject getCollidingObject() {
		double upperLeftX = ball.getX(); // the x-coordinate of the upper left corner
		double upperLeftY = ball.getY(); // the y-coordinate of the upper left corner

		double lowerLeftX = ball.getX(); // the x-coordinate of the lower left corner
		double lowerLeftY = ball.getY() + ball.getHeight(); // the y-coordinate of the lower left corner

		double upperRightX = ball.getX() + ball.getWidth(); // the x-coordinate of the upper right corner
		double upperRightY = ball.getY(); // the y-coordinate of the upper right corner

		double lowerRightX = ball.getX() + ball.getWidth(); // the x-coordinate of the lower right corner
		double lowerRightY = ball.getY() + ball.getHeight(); // the y-coordinate of the lower right corner

		/*
		 * These are the booleans that define the existence of collisions at each corner; I found that GImages can actually "get themselves" as elements,
		 * which sometimes resulted in the ball moving through the paddle and/or bricks. Thus, I needed the != statements for background and ball images. 
		 */
		boolean lowerLeftCollision = getElementAt(lowerLeftX, lowerLeftY) != null && getElementAt(lowerLeftX, lowerLeftY) != background
				&& getElementAt(lowerLeftX, lowerLeftY) != ball;

		boolean lowerRightCollision = getElementAt(lowerRightX, lowerRightY) != null && getElementAt(lowerRightX, lowerRightY) != background 
				&& getElementAt(lowerRightX, lowerRightY) != ball;

		boolean upperLeftCollision = getElementAt(upperLeftX, upperLeftY) != null && getElementAt(upperLeftX, upperLeftY) != background
				&& getElementAt(upperLeftX, upperLeftY) != ball;

		boolean upperRightCollision = getElementAt(upperRightX, upperRightY) != null && getElementAt(upperRightX, upperRightY) != background
				&& getElementAt(upperRightX, upperRightY) != ball;
		
		//Decides what to do with a collision at each corner
		if(upperLeftCollision) {
			return(getElementAt(upperLeftX, upperLeftY));

		} else if(upperRightCollision) {
			return(getElementAt(upperRightX, upperRightY));

		} else if(lowerLeftCollision) {
			return getElementAt(lowerLeftX, lowerLeftY);

		} else if(lowerRightCollision) {
			return getElementAt(lowerRightX, lowerRightY);

		} else {
			return null; 
		}
	}

 
	/*
	 * This method logically decides what the ball should do next after a collision 
	 * has been detected (i.e. remove the collider and bounce, or simply just bounce)
	 */
	private void resolveCollision() {
		GObject collider = getCollidingObject();
		AudioClip bounceclip = MediaTools.loadAudioClip("bounce.au");

		if (collider == paddle) {
			vy = -Math.abs(vy);		
			bounceclip.play();
			paddleStrikeCount++;
			
			//the kicker increases the speed of the game (reduces delay) by a factor of 1.5 for every 10th time the ball strikes the paddle.
			if(paddleStrikeCount % 10 == 0 && paddleStrikeCount != 0) {
				kicker = kicker / 1.5; 
			}

			//now, the collider must be a brick
		} else if (collider != null && collider != scoreLabel && collider != turnsLabel) {
			vy = -vy;
			remove(collider);
			bounceclip.play();
			brickCount --; //Reduce the brick count by 1 

			//These if/else statements assign points for breaking different colors of bricks
			if (collider.getColor() == Color.BLUE) {
				score = score + 10;

			} else if (collider.getColor() == Color.MAGENTA) {
				score = score + 20;

			} else if (collider.getColor() == Color.YELLOW) {
				score = score + 40;

			} else if (collider.getColor() == Color.ORANGE) {
				score = score + 60;

				//if we get here, the color must be RED
			} else { 
				score = score + 80;
			}
			scoreLabel.setLabel("Score: " + score);
		}
	}
}

