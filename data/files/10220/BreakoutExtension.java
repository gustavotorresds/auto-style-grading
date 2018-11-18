/*
 * File: Breakout.java
 * -------------------
 * Name: Anna Ekholm
 * Section Leader: Marilyn Zhang
 * 
 * An extended version of the game breakout
 * titled CS 106 A Breakout: Starring Chris Piech,
 * MTL, and me (and karel too tbh)
 */

/**
 * THINGS I WANT MY PROGRAM TO DO
 * ------------------------------
 * - Make the ball someones face DONE
 * - Play background music DONE
 * - add intro screen with a lot of karels floating around DONE
 * - get better audio for the mtl ball
 * - CS106A starring Chris Piech, MTL, and me :) DONE
 * - change starting coordinates of the ball
 * - add outtro screen music
 * - add sad music for when you drop the ball
 * - adjust point system maybe??
 * - fix sticky paddle DONE
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-30";
	
	// create the paddle so that it can be accessed outside of any method
	public GRect paddle = null;
	
	// create the ball so that it can be accessed in any method
	public GImage ball = null;
	
	// add the random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	AudioClip chrisBackground = MediaTools.loadAudioClip("freedom.au");
	
	AudioClip annaBackground = MediaTools.loadAudioClip("shakeitoff.au");
	
	AudioClip mTLBackground = MediaTools.loadAudioClip("hello.au");
	
	AudioClip introBackground = MediaTools.loadAudioClip("holdup.au");
	
	public int SCORE = 0;


	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		intro();
		setUpScreen();
		int bricksRemaining = play();
		endScreen(bricksRemaining);
	}


	/*
	 * Method: Intro 
	 * ---------------------------
	 * Sets up the introductory screen for the 
	 * game. 
	 * -----------------------
	 * Inputs: None
	 * Outputs: Adds a random number of Karels 
	 * to the screen, and prints the title of 
	 * the game
	 */
	private void intro() {
		// play the intro background song
		introBackground.play();
		//adds a random number of karels to random locations on the screen
		addKarels();
		
		
		//adds the three lines for the intro message
		GLabel introMessage = new GLabel ("CS 106A Breakout");
		messageSettings(SCREEN_FONT, Color.CYAN, introMessage, 100);
		
		
		introMessage = new GLabel ("starring chris piech, mtl, and me");
		messageSettings("SansSerif-BOLD-20", Color.MAGENTA, introMessage, 300);
		
		introMessage = new GLabel ("click to continue");
		messageSettings("SansSerif-BOLD-20", Color.ORANGE, introMessage, 350);
	
		
		waitForClick();
		introBackground.stop();
		removeAll();
	}

	/*
	 * Method: Add Karels
	 * --------------------------
	 * Adds a random number of Karels
	 * to the screen in random locations
	 * ---------------------------
	 * Inputs: none
	 * Outputs: a random number of Karels
	 * on the screen in random locations
	 */

	private void addKarels() {
		int howManyKarels = rgen.nextInt(20,50);
		for (int karelcount= 0; karelcount<howManyKarels; karelcount++) {
			GImage karel = new GImage("karel.png");
			karel.setSize(50, 50);
			double karelsWidth = karel.getWidth();
			double xLoc = rgen.nextDouble(0, getWidth()-karelsWidth);
			double karelsHeight = karel.getHeight();
			double yLoc = rgen.nextDouble(0, getHeight()-karelsHeight);
			add(karel, xLoc, yLoc);
		}
	}

	/*
	 * Method: End Screen
	 * --------------------------
	 * Displays the end screen at the end
	 * of a game
	 * ---------------------------
	 * Inputs: 
	 * Number of bricks: the number of bricks
	 * remaining on the screen
	 * Outputs: none
	 */
	private void endScreen(int numberOfBricks) {
		annaBackground.stop();
		removeAll();
		addKarels();
		if (numberOfBricks == 0) {
			winningOutro();
		} else {
			losingOutro(numberOfBricks);
		}
	}

	/*
	 * Method: Losing Outro
	 * --------------------------
	 * Displays the end message when when the 
	 * player has lost the game
	 * ---------------------------
	 * Inputs: 
	 * Number of bricks: the number of bricks
	 * remaining on the screen
	 * Outputs: Displays a Karel in the middle of the 
	 * screen, prints out the score, and bricks
	 * remaining
	 */
	private void losingOutro(int numberOfBricks) {
		addCenteredKarel();
		
		GLabel loseMessage = new GLabel ("BOO you lost");
		messageSettings(SCREEN_FONT, Color.CYAN, loseMessage, 100);
		
		loseMessage = new GLabel ("You had " + numberOfBricks + " bricks left.");
		messageSettings(SCREEN_FONT, Color.MAGENTA, loseMessage, getHeight()-100);
		
		loseMessage = new GLabel ("Your score was " + SCORE); 
		messageSettings(SCREEN_FONT, Color.ORANGE, loseMessage, getHeight()-50);
		
	}

	/*
	 * Method: Add Centered Karel
	 * --------------------------
	 * Adds a Karel to the middle of the screen
	 * ---------------------------
	 * Inputs: none
	 * Outputs: 
	 * An image of Karel centered in the 
	 * middle of the screen
	 */
	private void addCenteredKarel() {
		GImage karel = new GImage ("karel.png");
		double xLoc = getWidth()/2 - karel.getWidth()/2;
		double yLoc = getHeight()/2 - karel.getHeight()/2;
		add(karel, xLoc, yLoc);
	}

	/*
	 * Method: Winning Outro
	 * --------------------------
	 * Displays the end message when when the 
	 * player has won the game
	 * ---------------------------
	 * Inputs: 
	 * Number of bricks: the number of bricks
	 * remaining on the screen
	 * Outputs: Displays a Karel in the middle of the 
	 * screen, prints out the score.
	 */
	private void winningOutro() {
		GImage karel = new GImage ("karel.png");
		double xLoc = getWidth()/2 - karel.getWidth()/2;
		double yLoc = getHeight()/2 - karel.getHeight()/2;
		add(karel, xLoc, yLoc);
		GLabel winMessage = new GLabel ("YAY you won!!!");
		messageSettings(SCREEN_FONT, Color.CYAN, winMessage, 100);
		
		winMessage = new GLabel ("Your score was " + SCORE);
		messageSettings(SCREEN_FONT, Color.ORANGE, winMessage, getHeight()-100);
		
	}

	/*
	 * Method: Message Setting
	 * --------------------------
	 * A method that easily allows a programmer
	 * to change the look of a message
	 * ---------------------------
	 * Inputs: 
	 * Screen Font: the font that you want your 
	 * message in
	 * Color: the color that you want your 
	 * message in
	 * Message: the glabel that you want to add to 
	 * the screen
	 * yHeight: the y location where you want to add
	 * your message to
	 * Outputs: Prints your label onto the screen with
	 * the specified conditions
	 */
	private void messageSettings(String screenFont, Color color, GLabel message, double yHeight) {
		message.setFont(screenFont);
		message.setColor(color);
		double xLoc = getWidth()/2 - message.getWidth()/2;
		double yLoc = yHeight + message.getAscent();
		add(message, xLoc, yLoc);
	}

	/*
	 * Method: Play
	 * --------------------------
	 * Plays the game
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * Number of bricks (integer) - the number of
	 * bricks remaining on the screen
	 */
	private int play() {
		int numberOfBricks = NBRICK_ROWS * NBRICK_COLUMNS;
		int bounces = 0;
		GLabel scoreLabel = addScoreLabel();
		if (numberOfBricks != 0) {
			for (int round = 0; round < NTURNS; round ++) {
				addBall(round);
				double vy = VELOCITY_Y * (round/2 + 1);
				double vx = rgen.nextDouble(1.0, 3.0);
				if (rgen.nextBoolean(0.5)) vx = -vx;
				waitForClick();
				while (numberOfBricks != 0) {
					ball.move(vx, vy);
					pause(DELAY);
					vx = ifYouHitSidewall(vx);
					vy = ifYouHitTopWall(vy);
					if(hitBottomWall()) {
						hitTheBottomWall();
						break;
					}
					if (hit()) {
						GObject collider = getCollidingObject();
						if (collider == paddle) {
							bounces ++;
							vx = changevxOfBall(vx, bounces);
							vy = -vy;
							fixStickyPaddle(vx, vy);
							bounceClip.play();
						} else if (collider == scoreLabel) {
							ball.move(vx, vy);
							pause(DELAY);
						} else {
							numberOfBricks = hitBrick(collider, numberOfBricks);
							scoreLabel = updateScoreLabel(scoreLabel, collider, round);
							vy = -vy;

						}
					}

				}
				remove(ball);
			}

		}
		return numberOfBricks;
	}
	


	/*
	 * Method: Fix Sticky Paddle
	 * --------------------------
	 * Fixes the sticky paddle problem
	 * ---------------------------
	 * Inputs: 
	 * vx (double) - x velocity of the ball
	 * vy (double) - y velocity of the ball
	 * Outputs: 
	 * none
	 */
	
	private void fixStickyPaddle(double vx, double vy) {
		double yLocOfHit = ball.getY();
		if (yLocOfHit < getHeight() + PADDLE_HEIGHT) {
			for (int i = 0; i < - PADDLE_HEIGHT/vy; i++) {
				ball.move(vx, vy);
				pause(DELAY);
			}
		}
	}

	/*
	 * Method: Hit Brick
	 * --------------------------
	 * What happens when you hit a brick
	 * ---------------------------
	 * Inputs: 
	 * collider (GObject) - what the ball has
	 * collided with, in this cas a brick
	 * number of bricks (integer) - the number
	 * of bricks left on the screen
	 * Outputs: 
	 * Number of bricks (integer) - the number of
	 * bricks remaining on the screen
	 */
	private int hitBrick(GObject collider, int numberOfBricks) {
		remove(collider);
		numberOfBricks --;
		return numberOfBricks;
	}

	/*
	 * Method: Change Vx Of Ball
	 * --------------------------
	 * Changes the x velocity of the ball depending 
	 * on the number of bounces
	 * ---------------------------
	 * Inputs: 
	 * vx (double) - the x velocity of the ball
	 * bounces (integer) - the number of times the 
	 * ball has hit the paddle
	 * Outputs: 
	 * vx (double) - the new x velocity of the ball
	 */
	private double changevxOfBall(double vx, int bounces) {
		double ballXLoc = ball.getX() + BALL_RADIUS;
		double paddleXLoc = paddle.getX();
		double oneThirdPaddleX = paddleXLoc + PADDLE_WIDTH/3;
		double twoThirdPaddleX = paddleXLoc + 2*PADDLE_WIDTH/3;
		double fullPaddle = paddleXLoc + 2*PADDLE_WIDTH/3;
		if (ballXLoc < oneThirdPaddleX) {
			vx = -vx;
		}
		if (ballXLoc > twoThirdPaddleX && ballXLoc < fullPaddle) {
			vx = -vx;
		}
		if (bounces % 7 == 0) {
			vx = vx*2;
		}
		return vx;
	}

	/*
	 * Method: Update Score Label
	 * --------------------------
	 * Changes the score label to the updated score
	 * ---------------------------
	 * Inputs: 
	 * Score Label (GLabel) - the score of the game
	 * collider (GObject) - the thing that the ball
	 * collided with
	 * round (int) - the round of the game
	 * Outputs: 
	 * Score Label (GLabel) - the label with the 
	 * current score
	 */
	private GLabel updateScoreLabel(GLabel scoreLabel, GObject collider, int round) {
		increaseScore(collider, round);
		remove(scoreLabel);
		scoreLabel.setLabel("Score: " + String.valueOf(SCORE));
		double SCOREXLoc = getWidth()/2 - scoreLabel.getWidth()/2;
		double SCOREYLoc = getHeight() - PADDLE_Y_OFFSET/2 + scoreLabel.getAscent()/2;
		add(scoreLabel, SCOREXLoc, SCOREYLoc);
		return scoreLabel;
	}

	/*
	 * Method: Hit The Bottom Wall
	 * --------------------------
	 * What happens when you hit the bottom wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * none
	 */
	private void hitTheBottomWall() {
		GLabel sadFace = new GLabel (":(");
		sadFace.setFont(SCREEN_FONT);
		double sadFaceHeight = sadFace.getAscent();
		double sadFaceWidth = sadFace.getWidth();
		double xLoc = getWidth()/2 - sadFaceWidth/2;
		double yLoc = getHeight()/2 - sadFaceHeight/2;
		add(sadFace, xLoc, yLoc);
		waitForClick();
		remove(sadFace);
	}

	/*
	 * Method: If You Hit Top Wall
	 * --------------------------
	 * Checks to see if you hit the top
	 * wall, and changes the direction of the 
	 * y velocity if the ball hit the top
	 * ---------------------------
	 * Inputs: 
	 * vy (double) - the y velocity
	 * Outputs: 
	 * vy (double) - the y velocity
	 */
	private double ifYouHitTopWall(double vy) {
		if(hitTopWall()) {
			bounceClip.play();
			return -vy;
		}
		return vy;
	}

	/*
	 * Method: If You Hit Side Wall
	 * --------------------------
	 * Checks to see if you hit the side
	 * wall, and changes the direction of the 
	 * x velocity if the ball hit the top
	 * ---------------------------
	 * Inputs: 
	 * vx (double) - the y velocity
	 * Outputs: 
	 * vx (double) - the y velocity
	 */
	private double ifYouHitSidewall(double vx) {
		if(hitLeftWall() || hitRightWall()) {
			bounceClip.play();
			return -vx;
		}
		return vx;
	}

	/*
	 * Method: Add Score Label
	 * --------------------------
	 * Adds the label with the score to the
	 * screen
	 * ---------------------------
	 * Inputs: 
	 * Outputs: 
	 * score label (GLabel) - the label with the score
	 */
	private GLabel addScoreLabel() {
		GLabel scoreLabel = new GLabel("Score: " + String.valueOf(SCORE));
		scoreLabel.setFont(SCREEN_FONT);
		double SCOREXLoc = getWidth()/2 - scoreLabel.getWidth()/2;
		double SCOREYLoc = getHeight() - PADDLE_Y_OFFSET/2 + scoreLabel.getAscent()/2;
		add(scoreLabel, SCOREXLoc, SCOREYLoc);
		return scoreLabel;
	}

	/*
	 * Method: Add Ball To Screen
	 * --------------------------
	 * Adds the ball to the screen, depending on 
	 * which round it is a different face
	 * ---------------------------
	 * Inputs: 
	 * round (int) - the round of game it is
	 * Outputs: 
	 * ball on the screen
	 */
	private void addBall(int round) {
		if (round%3 == 1) {
			ball = makeMTLBall();
			addBallToScreen();
			chrisBackground.stop();
			mTLBackground.play();
		} else if (round % 3 == 0){
			ball = makeChrisPiechBall();
			addBallToScreen();
			chrisBackground.play();
		} else {
			ball = makeAnnaBall();
			addBallToScreen();
			mTLBackground.stop();
			annaBackground.play();
		}
	}


	/*
	 * Method: Make Anna Ball
	 * -------------------------
	 * Makes a ball with my face on it
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a ball with my face
	 */	
	private GImage makeAnnaBall() {
		GImage anna = new GImage ("anna.png");
		anna.setSize(2*BALL_RADIUS, 2*BALL_RADIUS);
		add(anna);
		return anna;
	}

	/*
	 * Method: Make Chris Piech Ball
	 * -------------------------
	 * Makes a ball with Chris Piech's face on it
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a ball with chris piech's face
	 */
	private GImage makeChrisPiechBall() {
		GImage chris = new GImage ("chrispiech.png");
		chris.setSize(2*BALL_RADIUS, 2*BALL_RADIUS);
		add(chris);
		return chris;
	}
	
	/*
	 * Method: Make MTL Ball
	 * -------------------------
	 * Makes a ball with MTL's face on it
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a ball with MTL's face
	 */
	private GImage makeMTLBall() {
		// testing out gimage
		// now i need to figure out how to make my ball mtl
		GImage mtl = new GImage ("mtl.png");
		mtl.setSize(2*BALL_RADIUS, 2*BALL_RADIUS);
		add(mtl);
		return mtl;
		
	}



	/*
	 * Method: Increase Score
	 * --------------------------------
	 * Calculate score depending on which color the 
	 * brick that the ball hits is and which round 
	 * it is
	 * ---------------------------
	 * Inputs: 
	 * collider (GObject) - the thing that the ball
	 * has collided with
	 * round (integer) - the round of play
	 * Outputs: 
	 * none
	 */
	private void increaseScore(GObject collider, int round) {
		if (collider.getColor() == Color.CYAN) {
			SCORE += (10*(NTURNS-round));
		} else if (collider.getColor()  == Color.BLUE) {
			SCORE += (20*(NTURNS-round));
		} else if (collider.getColor() == Color.YELLOW) {
			SCORE += (30*(NTURNS-round));
		} else if (collider.getColor() == Color.ORANGE) {
			SCORE += (40*(NTURNS-round));
		} else {
			SCORE += (50*(NTURNS-round));
		}
	}

	/*
	 * Method: Get Colliding Object 
	 * --------------------------------
	 * Gets whatever object the ball has collided 
	 * with
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * obj (GObject) - whatever the ball has collided 
	 * with, or null if the ball did not collide with
	 * anything
	 */
	private GObject getCollidingObject() {
		double xLoc = ball.getX()-1;
		double yLoc = ball.getY()-1;
		GObject obj = getElementAt(xLoc, yLoc);
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(xLoc + 2*BALL_RADIUS + 2, yLoc);
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(xLoc, yLoc + 2*BALL_RADIUS + 2);
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(xLoc + 2*BALL_RADIUS + 2, yLoc + 2*BALL_RADIUS + 2);
		if (obj != null) {
			return obj;
		}
		return null;
		
	}
	
	/*
	 * Method: Hit
	 * --------------------------------
	 * Returns true if one of the corners of the ball
	 * hit something
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean hit() {
		return topLeftHit() || bottomLeftHit() || topRightHit() || bottomRightHit();
	}
	
	
	/*
	 * Method: Bottom Right Hit
	 * ----------------------
	 * Checks to see if the ball has hit in its bottom right corner
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean bottomRightHit() {
		double xLoc = ball.getX() + 2*BALL_RADIUS;
		double yLoc = ball.getY() + 2*BALL_RADIUS;
		if (getElementAt(xLoc + 1, yLoc+1) != null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Method: Top Right Hit
	 * ----------------------
	 * Checks to see if the ball has hit in its top right corner
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	
	private boolean topRightHit() {
		double xLoc = ball.getX() + 2*BALL_RADIUS;
		double yLoc = ball.getY();
		if (getElementAt(xLoc - 1, yLoc - 1) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Method: Bottom Left Hit
	 * ----------------------
	 * Checks to see if the ball has hit in its bottom left corner
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean bottomLeftHit() {
		double xLoc = ball.getX();
		double yLoc = ball.getY() + 2*BALL_RADIUS;
		if (getElementAt(xLoc - 1, yLoc+1) != null) {
			return true;
		} else {
			return false;
		}
	}


	/*
	 * Method: Top Left Hit
	 * ----------------------
	 * Checks to see if the ball has hit in its top left corner
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean topLeftHit() {
		double xLoc = ball.getX();
		double yLoc = ball.getY();
		if (getElementAt(xLoc + 1, yLoc-1) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	/*
	 * Method: Hit Bottom Wall
	 * ----------------------
	 * checks if the ball has hit the bottom wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean hitBottomWall() {
		return ball.getY() >= getHeight() - 2*BALL_RADIUS;
	}


	/*
	 * Method: Hit Top Wall
	 * ----------------------
	 * checks if the ball has hit the top wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}


	/*
	 * Method: Hit Right Wall
	 * ----------------------
	 * checks if the ball has hit the right wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - 2*BALL_RADIUS;
	}


	/*
	 * Method: Hit Left Wall
	 * ----------------------
	 * checks if the ball has hit the right wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0 ;
	}


	/*
	 * Method: Mouse Moved
	 * --------------------------
	 * Tells the program what to do if the
	 * mouse is moved
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if (e.getX() >= getWidth() - PADDLE_WIDTH/2) {
			x = getWidth() - PADDLE_WIDTH;
		} else if (e.getX()<=PADDLE_WIDTH/2) {
			x = 0;
		}
		paddle.setLocation(x, y);
		
	}
		
	


	/*
	 * Method: Set Up Screen 
	 * -----------------------------
	 * Adds all of the bricks to the top of the 
	 * screen and the initial paddle
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * bricks and paddles on the screen
	 */

	private void setUpScreen() {
		createTopBricks();
		paddle = createPaddle();
		addPaddleToScreen();
		
	}
	
	/*
	 * Method: Add Ball to Screen
	 * -----------------------------
	 * Adds a ball to the center of the screen
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * ball in the center of the screen
	 */
	private void addBallToScreen() {
		double initXCord = getWidth()/2 - BALL_RADIUS;
		double initYCord = getHeight()/2 - BALL_RADIUS;
		add(ball, initXCord, initYCord);
	}


	/*
	 * Method: Add Paddle to screen
	 * ---------------------------
	 * Adds the paddle to the center of the screen
	 * in terms of its x coordinate 
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * paddle in the center of the screen
	 */
	
	private void addPaddleToScreen() {
		double initXCord = getWidth()/2 - PADDLE_WIDTH/2;
		double initYCord = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, initXCord, initYCord);
	}


	/*
	 * Method: Create paddle
	 * ----------------------------
	 * Makes a black paddle but dos not add it to the screen
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * paddle (GRect) - the paddle
	 */
	private GRect createPaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}


	/*
	 * Method: Create Top Bricks
	 * ------------------------------
	 * Adds all of the bricks to the top of the screen
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * Colored bricks at the top of the screen
	 */
	private void createTopBricks() {
		double initialxCord = getWidth()/2 - 5*BRICK_WIDTH - (4*BRICK_SEP) - BRICK_SEP/2;
		double initialyCord = BRICK_Y_OFFSET;
		
		for (int col = 0; col < NBRICK_COLUMNS; col ++) {
			for (int row = 0; row < NBRICK_ROWS; row ++) {
				double xCord = initialxCord + col*(BRICK_WIDTH + BRICK_SEP);
				double yCord = initialyCord + row*(BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				addColor(brick, row);
				add(brick, xCord, yCord);
			}
		}
	}

	/*
	 * Method: Add Color
	 * --------------------------
	 * Adds the correct color for the row of bricks 
	 * that you're trying to add to the screen
	 * ---------------------------
	 * Inputs: 
	 * brick (GRect) - the brick that you want to color
	 * row (integer) - the row that the brick is in
	 * Outputs: 
	 * the brick has the correct color
	 */

	private void addColor(GRect brick, int row) {
		row %= 10;
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
	}

}
