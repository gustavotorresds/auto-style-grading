/*
 * File: Breakout.java
 * -------------------
 * Name:Chinedum Egbosimba
 * Section Leader: Marilyn Zhang
 * Date: LAst Modified Feb 3 2018
 * This is is my first full program. It is a simple version of the universally
 * popular game brick breaker (or breakout to Americans :) ) Enjoy!
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings({ "unused", "serial" })
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
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Here are my instance variables

	// This paddle is visible to the entire program
	private GRect paddle = null;

	// This ball is visible to the entire program
	private GOval ball = null;

	//One random generator for the whole program, less redundant code
	private RandomGenerator rgen =  RandomGenerator.getInstance();

	//brickCounter and lifeCounter visible to entire program
	private GLabel brickCounter = null;

	private GLabel lifeCounter = null;

	public void run() {
		//don't forget to add mouse listeners
		addMouseListeners();

		//initialize program state variable 
		boolean play = true;
		boolean winner = false;

		while (play) {
			int brickCount = setupGame();
			waitForClick();
			//you always start with NTURNS lives
			for (int currentTurn = 1; currentTurn <=  NTURNS; currentTurn ++) {
				int turnsRemaining = NTURNS - currentTurn;
				brickCount = playOneLife(currentTurn, brickCount, turnsRemaining, winner);
				if (winner == true) {
					break;
				}
			}
			play = checkIfPlayingAgain(play);
		}
	}


	/*
	 * Method: checkIfPlayingAgain
	 * -----------------------
	 * Checks if the user wants to play again using the console and updates 
	 * this decision in the play variable
	 */
	private boolean checkIfPlayingAgain(boolean play) {
		//create these local fields so I can reuse my addtextbox method.
		int currentTurn = 0;
		int turnsRemaining = 0;
		shareGameUpdate(currentTurn, turnsRemaining);
		play = readBoolean("play again? (y/n): ", "y", "n");
		if (play == true) {
			removeAll();
		}
		else {
			removeAll();
			currentTurn = -1;
			turnsRemaining = -1;
			GRect textBox = addTextBox(currentTurn);
			GLabel thanksForPlaying = addGameProgressText(currentTurn, turnsRemaining);
			println("Thanks for playing!");
		}
		return play;
	}

	/*
	 * Method: playOneLife
	 * -----------------------
	 * This method plays through one life of the game. It adds a ball to the screen
	 * increments the x and y velocities and speeds appropriately depending on collision
	 * paths with the walls, paddle, or bricks. It animates the balls movement using 
	 * an animation loop. It also removes bricks when the ball hits them.
	 * Finally when a life ends, or the game is won, it recognizes this as the end of the 
	 * turn. 
	 */
	private int playOneLife(int currentTurn, int brickCount, int turnsRemaining, boolean winner) {
		//add the ball to the screen
		ball = addBall();

		//initialize ball x and velocities
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		double vy = VELOCITY_Y;
		//for my implementation, for reasons that will soon be apparent, it is useful to have  speeds as well as velocities
		double ySpeed = Math.abs(vy);
		double xSpeed = Math.abs(vx);

		while (currentTurn <= NTURNS) {
			winner = false;
			//update our lives remaining label
			lifeCounter.setLabel("Unused balls remaining = " + turnsRemaining);

			//figure out how to add a shadow to the ball
			//look to past solutions from class

			//update our ball's velocity for wall simple elastic reflections with the three playable walls
			if (hitRightWall(ball)) {
				vx = xSpeed * -1;
			}
			if (hitLeftWall(ball)) {
				vx = xSpeed;
			}
			if (hitTopWall(ball)) {
				vy = ySpeed;
			}

			//figure out if we are hitting anything.
			GObject collider = getCollidingObject();

			if (collider != null && collider != brickCounter && collider != lifeCounter) {
				//get full bounding dimensions of whatever we are hitting
				double colliderLeftX	 = collider.getX();
				double colliderRightX = collider.getRightX();
				double colliderTopY = collider.getY();
				double colliderBottomY = collider.getBottomY();

				//figure out if we are hitting a corner on the collider.
				char cornerBeingHit = checkIfHittingCorner(colliderRightX, colliderLeftX);

				//first check, if we are dealing with the paddle
				if (collider == paddle) {
					//lets make sure we can't save a life by dragging the paddle over a ball that has fallen to far to reflect naturally.
					//makes sense to set depth of reflection as vy - 1, since this is the deepest the ball could travel in one frame if it
					//started exactly one pixel above the paddle.(sorry for long comment. Important point!)
					double paddleDepthofReflection = paddle.getY() + (vy - 1); 

					if (ball.getBottomY() > paddleDepthofReflection) {
						//make sure the ball doesn't stick to the paddle; any contact results in upward motion
						vy = ySpeed * -1;

						//if ball hits the middle 2/3 of paddle (let's call this our sweet spot) return vx to it's original range.
						if (ballHitSweetSpot(colliderLeftX, colliderRightX)) {

							//1. figure out what sign the current velocity has so we can reflect normally
							double signOfXVelocity = Math.signum(vx);

							//2. get magnitude of vx values to be back in the normal range (not our edge reflection speed)
							vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);

							//3. use our stored knowledge of the sign of velocity to make sure the ball reflects normally
							if (signOfXVelocity == 1) {
								vx = -vx;
							}
						}

						// Next, lets check to see of ball is "hitting the corner". 
						//We are defining a "hit" as the corner being within the first .375 of the ball
						if (cornerBeingHit == 'r') {
							//send ball of with random positive x velocity
							vx = sendBallFlyingRight(ySpeed);
						}

						//if no contact with right corner, check left corner
						else if (cornerBeingHit == 'l') {
							//send ball of with random negative x velocity
							vx = sendBallFlyingLeft(ySpeed);
						}
					}
					//update speeds after paddle collision
					ySpeed = Math.abs(vy);
					xSpeed = Math.abs(vx);
				}

				// insight: if we hit something that isn't the paddle, it is a brick
				else {
					//if hit horizontal side flip vy
					if (ball.getY() < colliderTopY || ball.getBottomY( ) > colliderBottomY) {
						vy = -vy;
					}
					//if no horizontal hit, flip vx
					else {
						vx = -vx;
					}
					remove(collider);

					//update the brick count and display new value
					brickCount--;
					brickCounter.setLabel("Bricks remaining = " + brickCount); 

					//update speeds after brick collision
					ySpeed = Math.abs(vy);
					xSpeed = Math.abs(vx);
				}
			}

			//we need to end the life if the ball hits the bottom wall
			if (hitBottomWall(ball)) {
				remove(ball);
				pause(1500);
				shareGameUpdate(currentTurn, turnsRemaining);
				break;
			}

			//move the ball
			ball.move(vx, vy);

			//animation pause
			pause(DELAY);

			//if player takes out all the bricks, need to end game
			if (brickCount == 0) {
				currentTurn = NTURNS + 1; 
				winner = youWonTheGame(turnsRemaining, currentTurn);
			}
		}
		return brickCount;
	}

	/*
	 * Method: youWonTheGame
	 * -----------------------
	 * Congratulates the user for handily beating the game, and removes the paddle and ball from the screen
	 */
	private boolean youWonTheGame(int turnsRemaining, int currentTurn) {
		remove(paddle);
		remove(ball);
		pause(1000);
		shareGameUpdate(currentTurn, turnsRemaining);
		return true;
	}

	/*
	 * Method: shareGameUpdate
	 * -----------------------
	 * this method shares an update of game progress in a rectangle centered in the game window
	 */
	private void shareGameUpdate(int currentTurn, int turnsRemaining) {
		//add box, color it and center it on screen
		GRect textDisplayBox = addTextBox(currentTurn);

		//next lets add our label text
		GLabel gameProgressText = addGameProgressText(currentTurn, turnsRemaining);
		pause(1500);

		remove(gameProgressText);
		remove(textDisplayBox);
	}

	/*
	 * Method: addTextBox
	 * -----------------------
	 * Adds a rectangle to the screen to serve as the background for the game progress update.
	 */
	private GRect addTextBox(int currentTurn) {
		//define the size for our announcement box
		double boxHeight = 100;
		double boxWidth = 300;

		//use helper methods to return coordinates for centering box
		double boxXposition = calculateBoxXposition(boxWidth);
		double boxYposition = calculateBoxYposition(boxHeight);

		GRect textDisplayBox = new GRect (boxXposition, boxYposition, boxWidth, boxHeight);
		Color boxColor = chooseBoxColor(currentTurn);
		textDisplayBox.setColor(boxColor);
		textDisplayBox.setFilled(true);
		add(textDisplayBox);

		return textDisplayBox;
	}

	/*
	 * Method: addGameProgressText
	 * -----------------------
	 * Adds the game progress update text the colored rectangle displayed in the middle
	 * of the game window.
	 */
	private GLabel addGameProgressText(int currentTurn,  int turnsRemaining) {
		GLabel gameProgressText = null;
		//define the size for our announcement box
		double boxHeight = 100;
		double boxWidth = 300;

		//use helper methods to return coordinates for centering box
		double boxXposition = calculateBoxXposition(boxWidth);
		double boxYposition = calculateBoxYposition(boxHeight);

		/**Ask Marilyn during IG: Is there a way to make a helper method that can return the four above
		 *  quantities so I can use them in my two separate methods
		 */
		if (currentTurn == 0) {
			gameProgressText = new GLabel("Would you like to play again?", boxXposition, boxYposition); 
			gameProgressText.setColor(Color.WHITE);
		}
		else if (currentTurn == -1) {
			gameProgressText = new GLabel("thanks for playing!", boxXposition, boxYposition); 
			gameProgressText.setColor(Color.WHITE);
		}
		else if (currentTurn == -2) {
			gameProgressText = new GLabel("Welcome to Breakout! Double-click to begin.", boxXposition, boxYposition); 
			gameProgressText.setColor(Color.WHITE);
		}
		else if (currentTurn == NTURNS + 1) {
			gameProgressText = new GLabel("You won the game with " + turnsRemaining + " ball(s) to spare! Well Done!", boxXposition, boxYposition); 
		}
		else if (currentTurn < NTURNS - 1) {
			gameProgressText = new GLabel("You lost a ball! Careful...you only have " + turnsRemaining + " left!", boxXposition, boxYposition); 
		}
		else if (currentTurn == NTURNS - 1) {
			gameProgressText = new GLabel("This is your last ball...Better make it Count!!", boxXposition, boxYposition); 
		}
		else {
			gameProgressText = new GLabel("GAME OVER", boxXposition, boxYposition);
		}

		double yCorrectionDist = (boxHeight + gameProgressText.getAscent()) / 2;
		double xCorrectionDist = (boxWidth - gameProgressText.getWidth()) / 2;
		gameProgressText.move(xCorrectionDist, yCorrectionDist);
		add(gameProgressText);
		return gameProgressText;
	}

	/*
	 * Method: chooseBoxColor
	 * -----------------------
	 * Returns the appropriate color for the current game update textbox to be displayed.
	 */
	private Color chooseBoxColor(int currentTurn) {
		Color boxColor = null;

		if (currentTurn == 0 || currentTurn == -1 || currentTurn == -2 ) {
			boxColor = Color.BLUE;
		}
		//return green if the player won the game
		else if (currentTurn == NTURNS + 1) {
			boxColor = Color.GREEN;
		}
		else if (currentTurn < NTURNS - 1) {
			boxColor = Color.ORANGE;
		}
		else {
			boxColor = Color.RED;
		}
		return boxColor;
	}

	/*
	 * Method: calculateBoxXposition
	 * -----------------------
	 * Returns the x coordinate which properly horizontally centers the text box
	 */
	private double calculateBoxXposition(double boxWidth) {
		return (getWidth() - boxWidth) / 2;
	}


	/*
	 * Method: calculateBoxYposition
	 * -----------------------
	 * Returns the y coordinate which properly vertically centers the text box.
	 */
	private double calculateBoxYposition(double boxHeight) {
		return (getHeight() - boxHeight) / 2;
	}

	/*
	 * Method: ballHitSweetSpot
	 * -----------------------
	 * Checks if the middle of the ball is within the sweet spot (2/3 of the paddle width centered in
	 * the middle of the paddle)
	 */
	private boolean ballHitSweetSpot(double colliderLeftX, double colliderRightX) {
		return ball.getCenterX() > colliderLeftX + (1 / 6) * PADDLE_WIDTH && ball.getCenterX() < colliderRightX - (1 / 6) * PADDLE_WIDTH;
	}

	/*
	 * Method: sendBallFlyingLeft
	 * -----------------------
	 * Returns a random leftward x velocity with magnitude that ranges from the yspeed to the 
	 * yspeed + 2 for the ball. We use this method when we hit a corner on the left.
	 */
	private double sendBallFlyingLeft(double ySpeed) {
		return rgen.nextDouble(-1 * (ySpeed), -1 * (ySpeed + 2));
	}

	/*
	 * Method: sendBallFlyingRight
	 * -----------------------
	 * Returns a random rightward x velocity that ranges from the yspeed to the 
	 * yspeed +2 for the ball. We use this method when we hit a corner on the right
	 */
	private double sendBallFlyingRight(double ySpeed) {
		return rgen.nextDouble(ySpeed, ySpeed + 2);
	}

	/*
	 * Method: checkIfHittingCorner
	 * -----------------------
	 * Checks if the ball is hitting the corner of the paddle and returns 'n' if it isn't. If it is, the method returns
	 * 'r' or 'l' if the ball is hitting the right or left of the paddle respectively.  
	 */
	private char checkIfHittingCorner(double colliderRightX, double colliderLeftX) {
		char cornerBeingHit = 'n';
		if (ball.getX() <= colliderRightX && colliderRightX <= ball.getX() + 0.75 * BALL_RADIUS) {
			cornerBeingHit = 'r';
		}
		else if (ball.getRightX() >= colliderLeftX && ball.getRightX() - colliderLeftX  <= 0.75 * BALL_RADIUS) {
			cornerBeingHit = 'l';
		}
		return cornerBeingHit;
	}
	/*
	 * Method: getCollidingObject
	 * -----------------------
	 * Inspects each corner of the the imaginary box that would be inscribed by the ball in its current position for possible
	 * colliding objects. If there is an object at one location, the method returns it as a collider.
	 */
	private GObject getCollidingObject() {
		//need to check the four points for a collider
		GObject collider = null;

		for (int currentCorner = 1; currentCorner <= 4; currentCorner++) {
			//need a method to create simple binary coordinates for each 	corner
			int xBinaryCoordinate = calculateXBinary(currentCorner);
			int yBinaryCoordinate = calculateYBinary(currentCorner);

			//from the binary coordinates I can calculate real coordinates for the corner
			double xRealCoordinate = calculateXReal(xBinaryCoordinate);
			double yRealCoordinate = calculateYReal(yBinaryCoordinate);

			//check if there is an object at the real coordinates
			collider = getElementAt(xRealCoordinate, yRealCoordinate);

			if (collider != null) {
				break;
			}
		}
		return collider;
	}

	/*
	 * Method: calculateXBinary
	 * -----------------------
	 * Returns a binary x coordinate for the corner currently being checked.
	 * A binary coordinate is either 0 for the left side or 1 for the right side.
	 */
	private int calculateXBinary(int currentCorner) {
		//insight: odd corners in my scheme are on the left, even on the right
		int xBinary = 0;
		if (currentCorner % 2 == 0 ) {
			xBinary = 1;
		}
		return xBinary;
	}

	/*
	 * Method: calculateYBinary
	 * -----------------------
	 * Returns a binary y coordinate for the corner currently being checked.
	 * A binary coordinate is either 0 for the top row or 1 for the bottom row
	 */
	private int calculateYBinary(int currentCorner) {
		//insight: odd corners in my scheme are on the left, even on the right
		int yBinary = 0;
		if (currentCorner > 2 ) {
			yBinary = 1;
		}
		return yBinary;
	}

	/*
	 * Method: calculateXReal
	 * -----------------------
	 * Takes in the binary x coordinate at returns the real x coordinate for 
	 * the corner being checked
	 */
	private double calculateXReal(int xBinaryCoordinate) {
		//get x of the left corner
		double ballLeftX = ball.getX();
		//write method that spits out the real x coordinate
		double xReal = ballLeftX + (xBinaryCoordinate * (BALL_RADIUS * 2));
		return xReal;
	}

	/*
	 * Method: calculateYReal
	 * -----------------------
	 * Takes in the binary y coordinate at returns the real y coordinate for 
	 * the corner being checked
	 */
	private double calculateYReal(int yBinaryCoordinate) {
		//get y of the top corners
		double ballTopY = ball.getY();
		//write method that spits out the real y coordinate
		double yReal = ballTopY + (yBinaryCoordinate * (BALL_RADIUS * 2));
		return yReal;
	}

	/*
	 * Method: hitBottomWall
	 * -----------------------
	 * Returns true if the ball touches the bottom wall
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getBottomY() >= getHeight();
	}

	/*
	 * Method: hitTopWall
	 * -----------------------
	 * Returns true if the ball touches the top wall
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * Method: hitLeftWall
	 * -----------------------
	 * Returns true if the ball touches the left wall
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/*
	 * Method: hitRightWall
	 * -----------------------
	 * Returns true if the ball touches the right wall
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getRightX() >= getWidth();
	}

	/*
	 * Method: addBall
	 * -----------------------
	 * Creates a ball, adds it to the screen, and returns it so
	 * that the ball can be used for animation.
	 */
	private GOval addBall() {

		double ballDiameter = 2 * BALL_RADIUS;
		double ballStartingX = getWidth() / 2 - BALL_RADIUS;
		double ballStartingY = getHeight() / 2 - BALL_RADIUS;
		GOval ball = new GOval(ballDiameter, ballDiameter);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, ballStartingX, ballStartingY);
		return ball;
	}

	/*
	 * Method: setupGame
	 * -----------------------
	 * Changes the title bar of the window, sets the canvas size, 
	 * adds a brick structure, paddle, life and brick counters, and welcome
	 * message to the screen
	 */
	private int setupGame() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//this method adds the brick structure to the screen
		buildBrickStructure();

		//add in our paddle
		paddle = addPaddle();

		//setup life and brick in game counters
		int brickCount = NBRICK_ROWS *NBRICK_COLUMNS;
		brickCounter = new GLabel("Bricks remaining = " + brickCount, 0, 0);
		lifeCounter = new GLabel("Unused balls remaining = " + NTURNS, 0, 0);

		brickCounter.move(0,brickCounter.getAscent());
		lifeCounter.move(getWidth() - lifeCounter.getWidth(), lifeCounter.getAscent());
		add(brickCounter);
		add(lifeCounter);

		//using the same two variables to trigger method to generate my starting message
		int currentTurn = -2;
		int turnsRemaining = -2;
		GRect introDisplayBox = addTextBox(currentTurn);
		GLabel introText = addGameProgressText(currentTurn, turnsRemaining);

		waitForClick();
		remove(introText);
		remove(introDisplayBox);	

		return brickCount;
	}

	/*
	 * Method: addPaddle
	 * -----------------------
	 * Adds our paddle offset from the bottom to the screen
	 */
	private GRect addPaddle() {
		//calculate starting paddle x and y
		double startingPaddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(startingPaddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

		paddle.setFillColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}
	/*
	 * Method: mouseMoved
	 * -----------------------
	 * Tracks the mouse position and moves the paddle so it is centered at the x position
	 * of the mouse
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleX = mouseX - (PADDLE_WIDTH / 2);

		//need the paddle to stop on the edge of the screen
		if (paddleX >= getWidth() -PADDLE_WIDTH) {
			paddleX = getWidth() - PADDLE_WIDTH;
		}
		else if (paddleX < 0) {
			paddleX = 0;
		}
		paddle.setLocation(paddleX, getHeight() - PADDLE_Y_OFFSET);
	}


	/*
	 * Method: buildBrickStructure
	 * -----------------------
	 * Adds the brick super structure to screen, offset from the top by the named constant
	 * and centered horizontally. Insight: very similar to pyramid implementation.
	 */
	private void buildBrickStructure() {
		double FirstColumnX = calculateFirstColumnX();

		//need a loop to cycle through the rows
		for (int currentRow =1; currentRow <= NBRICK_ROWS; currentRow++ ) {
			double rowY = calculateRowY(currentRow);

			//create a row
			for (int brickCounter = 0; brickCounter < NBRICK_COLUMNS; brickCounter++ ) {
				double brickX = calculateBrickX(brickCounter, FirstColumnX);
				addABrick(brickX, rowY, currentRow);
			}
		}
	}

	/*
	 * Method: calculateBrickX
	 * -----------------------
	 * Calculates the x position for the current brick in the row
	 */
	private double calculateBrickX(int brickCounter, double firstColumnX) {
		// simple arithmetic setup to find x for current brick to be added
		double brickX = firstColumnX + (BRICK_WIDTH + BRICK_SEP) * brickCounter;
		return brickX;
	}
	/* 
	 * Method: calculateBrickY
	 * -----------------------
	 * This method calculates the y coordinate for the current row of bricks
	 */
	private double calculateRowY(int currentRow) {
		double rowY = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * (currentRow - 1);
		return rowY;
	}

	/*
	 * Method: addABrick
	 * -----------------------
	 * Creates a brick and add it to the row being built 
	 * 
	 */
	private void addABrick(double brickX, double rowY, double currentRow) {
		GRect brick = new GRect(brickX, rowY, BRICK_WIDTH, BRICK_HEIGHT);
		Color brickColor = colorABrick(currentRow);
		brick.setColor(brickColor);
		brick.setFilled(true);
		add(brick);
	}

	/*
	 * Method: colorABrick
	 * -----------------------
	 * Returns appropriate color for brick being added
	 * 
	 */
	private Color colorABrick(double currentRow) {
		Color brickColor = null;
		if (currentRow <= 2) {
			brickColor = Color.RED;
		}
		else if (currentRow == 3 || currentRow == 4) {
			brickColor = Color.ORANGE;
		}
		else if (currentRow == 5 || currentRow == 6) {
			brickColor = Color.YELLOW;
		}
		else if (currentRow == 7 || currentRow == 8) {
			brickColor = Color.GREEN;
		}
		else if (currentRow == 9 || currentRow == 10) {
			brickColor = Color.CYAN;
		}
		//make any extra rows beyond 10 random colored, because extensions!
		else {
			//generate random brick color
			brickColor = rgen.nextColor();
		}
		return brickColor;
	}

	/*
	 * Method: calculateFirstColumnX
	 * -----------------------
	 * Calculates x for the first column of bricks
	 * 
	 */
	private double calculateFirstColumnX() {
		//calculate width of figure with the spacing
		double widthOfFigure = NBRICK_COLUMNS * BRICK_WIDTH + ((NBRICK_COLUMNS - 1) * BRICK_SEP);
		double FirstColumnX = (getWidth() - widthOfFigure)  / 2;
		return FirstColumnX;
	}
}
