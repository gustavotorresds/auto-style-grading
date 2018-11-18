/*
 * File: Breakout.java
 * -------------------
 * Name: Simon Qin
 * Section Leader: Julia Daniel
 * 
 * This file sets up and plays the game breakout. 
 * Generally, we break this up into:
 * 1. Setting up the bricks and paddle
 * 2. Playing the game, with bounces, collisions, and end states.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;
	// Number of bricks per rainbow cycle
	// This says that every N rows we "reset" the rainbow
	// Used so that in cases where rows >10, we can repeat colors
	public static final int NBRICK_COLORS_CYCLE = 10;

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

	// Double radius for diameter
	public static final double BALL_DIAMETER = BALL_RADIUS *2; 

	// The ball's vertical velocity.
	// Highly recommended doubling default of 3.0 for better gameplay.
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Instance variables for paddle and ball
	private GRect paddle = null;
	private GOval ball = null;

	//Instance variables for velocity and random generation
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Instance variables for keeping track of game progress
	private int bricksleft = NBRICK_ROWS * NBRICK_COLUMNS;

	public void run() {		// Keeping it simple!
		breakoutSetup();		// 1. Set up the game
		breakoutPlay();		// 2. Play the game
	}

	/* Method breakoutSetup performs all of the actions required to launch the game.
	 * This includes the title, canvas size, and creating/adding the Bricks and Paddle objects.
	 * Pre: Running the program. 
	 * Post: All elements present on the canvas.
	 */
	private void breakoutSetup() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setupBricks();
		setupPaddle();
	}

	/* Method setupBricks adds all of the bricks based on defined inputs. 
	 * Pre: No bricks
	 * Post: All bricks added in desired sizes, positions, quantity, and colors. 
	 */
	private void setupBricks() {
		for(int rowNum=0; rowNum<NBRICK_ROWS; rowNum++) {
			for(int colNum=0; colNum<NBRICK_COLUMNS; colNum++) {
				createSingleBrick(rowNum, colNum);
			}
		}
	}

	/* Method createSingleBrick is the backbone of setupBricks method. 
	 * Given a row and column index, it creates one brick in that location.
	 * It uses constants to define size, and row and column index to set position.
	 * Pre: No brick in that location
	 * Post: Brick created in that location. 
	 */
	private void createSingleBrick(int row, int col) {
		GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		//Need to calculate x location of first brick, as BRICK_SEP != left and right padding. 
		//To get left padding, we take any width left after subtracting NColumns*width and NColumns-1 * Sep and split in half
		double firstBrickX = (getWidth() - (NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) - BRICK_SEP)) / 2; 
		double brickX = (firstBrickX + col*(BRICK_WIDTH + BRICK_SEP));
		double brickY = (BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP));
		brick.setLocation(brickX, brickY);
		colorBrick(row, brick);		
		add(brick);
	}

	/* Method coloBrick defines the logic of individual brick colors.. 
	 * Given a row index, it colors one brick in that location.
	 * Pre: Uncolored brick in a certain row.
	 * Post: Brick colored and filled.  
	 * QUESTION: What's the difference between if else*5 vs. if *5?
	 */
	private void colorBrick(int row, GRect brick) {
		// Need this nested set of if-else to set the five different colors based on row number
		if (row % NBRICK_COLORS_CYCLE == 0 || row % NBRICK_COLORS_CYCLE == 1) {
			brick.setColor(Color.RED);
		} else if (row % NBRICK_COLORS_CYCLE == 2 || row % NBRICK_COLORS_CYCLE == 3) {
			brick.setColor(Color.ORANGE);
		} else if (row % NBRICK_COLORS_CYCLE == 4 || row % NBRICK_COLORS_CYCLE == 5) {
			brick.setColor(Color.YELLOW);
		} else if (row % NBRICK_COLORS_CYCLE == 6 || row % NBRICK_COLORS_CYCLE == 7) {
			brick.setColor(Color.GREEN);
		} else if (row % NBRICK_COLORS_CYCLE == 8 || row % NBRICK_COLORS_CYCLE == 9) {
			brick.setColor(Color.CYAN);
		}
		brick.setFilled(true);
	}

	/* Method setupPaddle adds the paddle to the map with mouse tracking.
	 * Pre: No paddle
	 * Post: Paddle created with mouse tracking.
	 */
	private void setupPaddle() {	// Again, keeping it simple.
		paddle = makePaddle();		// 1. Let's make the object first and put it in the center
		addMouseListeners();			// 2. Let's add mouse tracking to the platform
	}

	/* Method makePaddle adds the paddle to the map.
	 * Pre: No paddle.
	 * Post: Paddle created in horizontal center, offset by the constant from bottom. 
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		double paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle.setLocation(paddleX, paddleY);	//Sets initial paddle to centered, before mouse tracking
		paddle.setFilled(true);		// No need to set color since defaults to black
		add(paddle);
		return paddle;
	}

	/* This mouse event method adds mouse tracking to the paddle.
	 * Pre: Paddle exists without mouse tracking (doesn't move).
	 * Post: Paddle now follows the mouse, bound by two edges. 
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		// Sets right edge by stopping the mouse tracking at where the paddle hits the edge
		double maxX = Math.min(getWidth() - PADDLE_WIDTH, mouseX - (PADDLE_WIDTH / 2));		
		// Sets left edge by making sure paddle can't move past left edge
		double minX = Math.max(0, maxX);
		paddle.setLocation(minX, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}

	/* This method encompasses the gameplay element of the program.
	 * Pre: Bricks and paddle are set up. 
	 * Post: Game is played with a conclusion (win or loss). 
	 */
	private void breakoutPlay() {
		welcomeText();
		playGame();
	}

	/* This  method adds the welcome message to the screen.
	 * Pre: Game hasn't started.
	 * Post: User sees message, clicks mouse, message disappears. 
	 */
	public void welcomeText() {
		GLabel startmessage = new GLabel("Welcome to Breakout! Click anywhere to start.");
		double startX = (getWidth() - startmessage.getWidth()) / 2;		// Center message
		double startY = (.75 * getHeight());								// We'll put this message 3/4ths of the way down. Not super important. 
		startmessage.setLocation(startX, startY);
		add(startmessage);
		waitForClick();				// We want to listen for a click before starting.
		remove(startmessage);
	}

	/* This  method defines the ball's velocity, launches it, and plays the game.
	 * Pre: User has just clicked and welcome message disappears.
	 * Post: Appropriate end state based on winning or losing.
	 */
	private void playGame() {
		for (int lives = NTURNS; lives>0; lives -=1) {		// Each "round" of the game is 1 "turn" or "life. We reset each time.
			setupBall();										// When we reset we set up the ball again.
			while(!hitBottom()) {							// When ball is live, it goes until we win or hit the bottom.
				moveBall();
				ballCollisions();
				if(bricksleft == 0) {
					winGame();
					return;		// Need to exit the game if we win, else we'll set up a new ball after hitting all the bricks!
				}
			}
			remove(ball);		// Want to get rid of the ball before resetting the next one. 
		}
		loseGame();				// Once we have no lives left we initiate the "you lost" method. 
	}


	/* The setupBall method adds a ball to the center of the screen.
	 * Pre: No ball exists.
	 * Post: Ball exists in the middle of the screen with a reasonably defined velocity. 
	 */
	private void setupBall() {
		createBall();
		setBallVelocity();
	}

	/* The createBall method adds a ball to the center of the screen.
	 * It creates a ball object, centers it, and sets fill, before adding.
	 */
	private void createBall() {
		ball = new GOval(BALL_DIAMETER, BALL_DIAMETER);
		double ballX = (getWidth() - BALL_DIAMETER) / 2;		//Centering
		double ballY = (getHeight() - BALL_DIAMETER) / 2; 
		ball.setLocation(ballX, ballY);	
		ball.setFilled(true);		// Again, no need to set color if black
		add(ball);
	}

	/* The setBallVelocity method defines what ball's exit velocity will be,
	 * using random generator.
	 */
	private void setBallVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if(rgen.nextBoolean(.5)) vx = -vx;
	}

	/* The moveBall method moves the ball with given delay.
	 * Simple enough.
	 */
	private void moveBall() {
		ball.move(vx, vy);
		pause(DELAY);
	}

	/* The ballCollisions method consolidates the three things that Ball could hit:
	 * 1. Walls
	 * 2. Paddle
	 * 3. Bricks
	 */
	private void ballCollisions() {
		hitWalls();				// 1. Walls is based on determining if ball reaches certain edges of the screen.
		if(hitPaddle()) {		// 2. Paddle is implemented as boolean since there are some special cases to check.
			vy = -vy;
		}	
		hitBricks();				// 3. Bricks is the most complex. More comments in the method(s).
	}

	/* This method tells the ball to "bounce" whenever it hits an edge of the world.
	 * Pre: Ball would just skirt past the edge indefinitely.
	 * Post: Ball bounces appropriately off of the edges of the world. 
	 */
	private void hitWalls() {
		if(hitLeft() || hitRight()) {
			vx = -vx;					// Question for Julia: Do we need a common method for flipVertical or flipHorizontal? 
		}
		if(hitTop() || hitBottom()) {		// Not using else because both could happen
			vy = -vy;
		}
	}

	/* This method returns true if the ball hits the left edge.
	 * Used for reversing velocity when it applies. 
	 */
	private boolean hitLeft() {
		return ball.getX() <= 0;
	}

	/* This method returns true if the ball hits the right edge.
	 * Used for reversing velocity when it applies. 
	 */
	private boolean hitRight() {
		return ball.getX() >= getWidth() - BALL_DIAMETER;			// Adjust for width of ball since ball's X is left.
	}

	/* This method returns true if the ball hits the top edge.
	 * Used for reversing velocity when it applies. 
	 */
	private boolean hitTop() {
		return ball.getY() <= 0;
	}

	/* This method returns true if the ball hits the bottom edge.
	 * Used for reversing velocity when it applies. 
	 */
	private boolean hitBottom() {
		return ball.getY() >= getHeight() - BALL_DIAMETER;  		// Adjust for height of ball since ball's X is top.
	}

	/* This method returns true if the ball hits the paddle.
	 * Used for reversing velocity when it applies. 
	 */
	private boolean hitPaddle() {
		double ballLeft = ball.getX();
		double ballRight = ball.getX() + BALL_DIAMETER;
		double ballBottom = ball.getY() + BALL_DIAMETER;
		// Two notes about checking for hitting the paddle:
		// 1. We only use the bottom corners of the ball for obvious reasons.
		// 2. We defensively check that the pixel above the element is empty, 
		// otherwise we have the "ball trapped in paddle" bug. 
		return ((getElementAt(ballLeft, ballBottom) == paddle && getElementAt(ballLeft, ballBottom - VELOCITY_Y) == null) || 
				(getElementAt(ballRight, ballBottom) == paddle && getElementAt(ballRight, ballBottom - VELOCITY_Y) == null));
	}

	/* This method hitBricks runs a double loop to check each corner. 
	 * So for left/right we loop 0 and 1, and same for top/bottom.
	 * Then we use the removeBricks method to control the mechanics.
	 */
	private void hitBricks() {
		for (double leftright = 0; leftright <= 1; leftright += 1) {	
			for (double topbottom = 0; topbottom <= 1; topbottom += 1) {
				removeBricks(leftright, topbottom);				// We loop through four corners to check for collision, see method.
			}
		}
	}

	/* This method removeBricks checks if there is a brick where the ball is and then:
	 * 1. Bounces vertically or horizontally based on where it's hitting the brick.
	 * 2. Removes the brick.
	 * 3. Subtracts from the brick counter. 
	 * Expected input: 0 or 1 for left/right and top/bottom => specifying one corner. 
	 */
	private void removeBricks(double leftright, double topbottom) {
		double ballX = ball.getX() + leftright * BALL_DIAMETER;	// If left/right = 1, we adjust the corner to check right.
		double ballY = ball.getY() + topbottom * BALL_DIAMETER;	// If top/bottom = 1, we adjust the corner to check bottom.
		GObject object = getElementAt(ballX, ballY);					
		if(object != null && object != paddle) {			 		// If there's an object and it's not the paddle, we know it's a brick!
			// This set of logic checks which side of brick the collision takes.
			// It checks if the collided brick is the same object one vertical or horizontal movement away.
			// We need to use the velocity because ball could find itself x pixels "into" the brick per movement when colliding, 
			// with max value of x equal to the velocity. For ex. ball is 2 pixels "south" of brick, moves 6 pixels, now is "four in".
			// We need to check up to 6 pixels below the brick to see if it is empty underneath (which tells us to go back down).
			// Technically, there is an unsolved edge case which is if the brick height or width is less than the velocity.
			// We don't try to solve this, since it won't really happen realistically. 
			// If we wanted to solve it we'd need to take the difference between height/width and velocity and apply it to the check.
			if(object != getElementAt(ballX, ballY - VELOCITY_Y)) {		// For example, we check if the object one movement above is the same.
				vy = -1 * Math.abs(vy); 									// If not, then we know we need the ball to bounce upwards.
			}
			if (object != getElementAt(ballX, ballY + VELOCITY_Y)) {
				vy = Math.abs(vy);
			}
			if(object != getElementAt(ballX - VELOCITY_X_MAX, ballY)) {
				vx = -1 * Math.abs(vx);
			}
			if(object != getElementAt(ballX + VELOCITY_X_MAX, ballY)) {
				vx = Math.abs(vx);
			}
			remove(object);										// Have to remember to remove the brick..
			bricksleft -= 1;										// ...and knock down our brick counter. 
		}
	}

	/* IGNORE FOR GRADING -> Why didn't this work? 
	 * This method setDirection takes an axis input (vy or vx),
	 * and sets it to either positive (direction = true) or negative (direction = false).
	 *
	private void setDirection(double axis, boolean direction) {
		if(direction) { 
			axis = Math.abs(axis);		// So if we want vy or vx to be positive, we just take the absolute value.
		} else {
			axis = -1 * Math.abs(axis);	// If we want it to be negative, we set it to be negative.
		}
	}
	 */

	/* This method concludes the game when a player hits every brick. 
	 * We display the message, and remove the paddle, ending the game.
	 */
	private void winGame() {
		GLabel winmessage = new GLabel("You've won! Check out BreakoutExtension for more challenges.");
		double winX = (getWidth() - winmessage.getWidth()) / 2;	
		double winY = (.75 * getHeight());			// Sticking with our 3/4ths down alignment. 	
		winmessage.setLocation(winX, winY);
		add(winmessage);
		remove(paddle);
	}

	/* This method concludes the game when player runs out of lives.
	 * No need for "exit function" from this point on.
	 */
	private void loseGame() {
		GLabel losemessage = new GLabel("You've lost! Restart the program to try again.");
		double loseX = (getWidth() - losemessage.getWidth()) / 2;		
		double loseY = (.75 * getHeight());			// I considered making this an instance variable but don't think getHeight() works at that point. 
		losemessage.setLocation(loseX, loseY);
		add(losemessage);
		remove(paddle);
	}
}
