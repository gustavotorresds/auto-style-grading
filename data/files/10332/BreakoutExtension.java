/*
 * File: Breakout.java
 * -------------------
 * Name:Joshua Chang
 * Section Leader:Esteban
 * 
 * This file implements the game of Breakout.  The player gets a specified number of turns to try 
 * to clear all the bricks from the screen.  If all the bricks are cleared before the player runs
 * out of turns, they win.  Each brick has a point value that the played accumulates.  The higher
 * the brick on the screen, the more points it is worth.  If the player runs out of tries before 
 * all the bricks are cleared, they lose.  There are several extra features to this game.  The 
 * speed of the ball can be controlled using the 'up' and 'down' arrows.  An 'autoplay' mode can 
 * be toggled on and off by pressing 'a'.  The x velocity of the ball will increase or decrease 
 * depending on how close to the center of the paddle it is hit.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

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
	
	//Width of each row of bricks
	public static final double ROW_WIDTH = BRICK_WIDTH * NBRICK_COLUMNS + BRICK_SEP * (NBRICK_COLUMNS - 1);
	
	//Height of a column of bricks
	public static final double COLUMN_HEIGHT = (BRICK_HEIGHT + BRICK_SEP) * NBRICK_ROWS;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;
	
	//Diameter of the ball in pixels
	public static final double BALL_DIAMETER = 2 * BALL_RADIUS;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1;
	public static final double VELOCITY_X_MAX = 3;
	
	//Change in speed of the ball
	public static final double DELTA_V = 0.5;
	
	public static final double VX_MULTIPLIER = 0.02;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 7;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//Bonus points for magenta brick
	public static final int BONUS_POINTS = 1000;
	
	//INSTANCE VARIABLES
	private GRect paddle = null;
	private GOval ball = null;
	private GRect randomBrick = null;
	GObject collider = null;
	private GLabel startMessage;
	private GLabel turnsMessage;
	private GLabel speedMessage;	
	private GLabel autoplayMessage;
	private GLabel pointsMessage;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int turnsRemaining = NTURNS;
	private int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;
	private int points;
	private boolean gameWon = false;
	private boolean inPlay = true;
	private boolean autoPlay = false;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		
		addMouseListeners();
		gameSetup();
		while(turnsRemaining > 0) {
				playGame();
		}
		

	}

	/* Plays the Breakout game.  Controls motion of the ball, interactions with walls, collisions,
	 * winning and losing conditions, points accumulation, and message displays. 
	 * Pre: Game setup is complete.
	 * Post: One turn of Breakout is complete. If all the bricks are gone before the player runs out
	 * 		 tries, they win.  If the player runs out of tries before all the bricks are gone, they lose.
	 */
	private void playGame() {
		if(!gameWon) {
			displayStartMessage();
			featuresMessages();
			waitForClick();
			createBall();			
			if(randomBrick == null) {
			createRandomBrick();
			}
			remove(startMessage);
			remove(turnsMessage);
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			vy = VELOCITY_Y;
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			inPlay = true;
		}
		
		/*The inPlay boolean provides a way to break out of the while animation loop.  When the ball 
		 * falls below the paddle or one of the end-game conditions is met, inPlay is set to false.
		 */
		while(inPlay) {
			displayPoints();
			//Autoplay function makes the paddle track the ball on its own
			if (autoPlay) {
				paddle.setLocation(ball.getX() - PADDLE_WIDTH/2 + BALL_RADIUS, getHeight()-PADDLE_Y_OFFSET);
			}

			if (hitLeftWall() || hitRightWall()) {
				vx = -vx;
			}

			if (hitTopWall()) {
				vy = -vy;
			}

			if (passedBottomWall()) {
				inPlay = false;
				remove(ball);
				turnsRemaining --;
			}
			
			collisionAction();

			ball.move(vx, vy);
			pause(DELAY);
			winCondition();
			loseCondition();
		}
	}
	
	/* Gives instructions to start the game and indicates how many turns remaining.
	 * Pre: Game setup is complete.
	 * Post: Messages are displayed on the screen.
	 */
	private void displayStartMessage() {
		startMessage = new GLabel("Click to start.");
		if (turnsRemaining > 1) {
			turnsMessage = new GLabel("You have " + turnsRemaining + " turns remaining.");
		}
		else {
			turnsMessage = new GLabel("You have " + turnsRemaining + " turn remaining.");
		}
		double xPos1 = getWidth()/2 - startMessage.getWidth();
		double yPos1 = (getHeight()/2 - startMessage.getAscent()*2);
		double xPos2 = (getWidth()/2-turnsMessage.getWidth());
		double yPos2 = getHeight()/2;
		startMessage.setFont("Courier-24");
		turnsMessage.setFont("Courier-24");
		add(startMessage, xPos1, yPos1);
		add(turnsMessage, xPos2, yPos2);
	}
	
	/* Displays instructions to activate autoplay and speed features.
	 * Pre: none
	 * Post: Autoplay and speed labels are displayed centered near the top of the screen.
	 */
	private void featuresMessages() {
		speedMessage = new GLabel("Use 'up' and 'down' arrows to adjust speed.");	
		autoplayMessage = new GLabel("Press 'a' for autoplay.");	
		speedMessage.setFont("Courier-20");
		autoplayMessage.setFont("Courier-20");
		double xPos1 = (getWidth() - speedMessage.getWidth())/2;
		double yPos1 = speedMessage.getAscent();
		double xPos2 = (getWidth() - autoplayMessage.getWidth())/2;
		double yPos2 = speedMessage.getAscent()*2.2;
		add(speedMessage, xPos1, yPos1);
		add(autoplayMessage, xPos2, yPos2);
	}
	
	/* Displays the point total on the screen.
	 * Pre: Point total is increased upon collisions with bricks.
	 * Post: Point total label is displayed centered near the top of the screen.
	 */
	private void displayPoints() {
		if (pointsMessage != null) {
			remove(pointsMessage);
		}
		pointsMessage = new GLabel("Points: " + points);	
		pointsMessage.setFont("Courier-20");
		pointsMessage.setColor(Color.BLUE);
		double xPos3 = (getWidth() - pointsMessage.getWidth())/2;
		double yPos3 = pointsMessage.getAscent()*3.4;
		add(pointsMessage, xPos3, yPos3);
	}

	/* Sets the condition for winning the game as when there are no bricks remaining.  Prints a message
	 * and exits the while animation loop. 
	 * Pre: No more bricks remaining.
	 * Post: Congratulatory message displayed on screen, all other objects removed.
	 */
	private void winCondition() {
		if (bricksRemaining == 0) {
			GLabel winMessage = new GLabel("Congratulations, you win!");
			double xPos = (getWidth()/2-winMessage.getWidth());
			double yPos = (getHeight() - winMessage.getHeight())/2;
			winMessage.setFont("Courier-24");
			add(winMessage, xPos, yPos);
			gameWon = true;
			inPlay = false;
			remove(paddle);
			remove(ball);
			remove(randomBrick);
			displayPoints();
		}
	}

	/* Sets the condition for losing the game as when there are no turns remaining.  Prints a message
	 * and exits the while animation loop. Provides user option to play again with a click, and sets up
	 * new game if user clicks.
	 * Pre: No more turns remaining.
	 * Post: Click to play again message displayed on screen.  All objects removed and new game set up 
	 * if user clicks.
	 */
	private void loseCondition() {
		if (turnsRemaining == 0) {
			GLabel loseMessage = new GLabel("Click to play again.");
			double xPos = (getWidth()/2-loseMessage.getWidth());
			double yPos = (getHeight() - loseMessage.getHeight())/2;
			loseMessage.setFont("Courier-24");
			add(loseMessage, xPos, yPos);
			waitForClick();
			turnsRemaining = NTURNS;
			points = 0;
			randomBrick = null;
			removeAll();
			gameSetup();
			
		}
	}
	
	/* Creates a ball.
	 * Pre: none
	 * Post: Adds a ball to the center of the screen.
	 */	
	private void createBall() {
		ball = new GOval(BALL_DIAMETER, BALL_DIAMETER);
		double xPos = getWidth()/2 - BALL_RADIUS;
		double yPos = getHeight()/2 - BALL_RADIUS;
		ball.setFilled(true);
		add(ball, xPos, yPos);
	}
	
	/* Draws a brick, colors it magenta, and places it in a random location below the brick array.
	 * Pre: none
	 * Post: A magenta brick is added to a random location.
	 */
	private void createRandomBrick() {
		randomBrick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		double xPos = rgen.nextDouble(0, getWidth() - BRICK_WIDTH);
		double yPos = rgen.nextDouble(BRICK_Y_OFFSET + COLUMN_HEIGHT, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BRICK_HEIGHT);
		//double delay = rgen.nextDouble(7, 15) * 1000;
		randomBrick.setFilled(true);
		randomBrick.setColor(Color.MAGENTA);
		add(randomBrick, xPos, yPos);
	}

	/* Checks if the position of the ball has exceeded the bottom wall.
	 * Pre: Ball created.
	 * Post: Return true if y-position of ball is greater than the height of the canvas.
	 */			
	private boolean passedBottomWall() {
		return ball.getY() >= getHeight();
	}

	/* Checks if the position of the ball has exceeded the top wall.
	 * Pre: Ball created.
	 * Post: Return true if y-position of ball is less than 0.
	 */	
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}
	
	/* Checks if the position of the ball has exceeded the right wall.
	 * Pre: Ball created.
	 * Post: Return true if x-position of ball is greater width of the canvas minus the ball diameter.
	 */	
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - BALL_DIAMETER;
	}
	
	/* Checks if the position of the ball has exceeded the left wall.
	 * Pre: Ball created.
	 * Post: Return true if x-position of ball is less than 0.
	 */	
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	/* Creates 8 collision points around the circumference of the ball separated by 45 degrees.  
	 * Note: this is different from the assignment instructions as it yields more accuracy for collisions.
	 * The method then returns the object that the ball runs into at any of the collision points.
	 * Pre: Ball created.
	 * Post: Returns object in collision with ball, or null if ball is not in collision with any object.
	 */	
	private GObject getCollidingObject() {
		GPoint top = new GPoint(ball.getX() + BALL_RADIUS, ball.getY());
		GPoint left = new GPoint(ball.getX(), ball.getY() + BALL_RADIUS);
		GPoint bottom = new GPoint(ball.getX() + BALL_RADIUS, ball.getY() + BALL_DIAMETER);
		GPoint right = new GPoint(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_RADIUS);
		
		GPoint topLeft = new GPoint(ball.getX() + BALL_RADIUS - BALL_RADIUS/Math.sqrt(2.0), ball.getY() + BALL_RADIUS - BALL_RADIUS/Math.sqrt(2.0));
		GPoint topRight = new GPoint(ball.getX() + BALL_RADIUS + BALL_RADIUS/Math.sqrt(2.0), ball.getY() + BALL_RADIUS - BALL_RADIUS/Math.sqrt(2.0));
		GPoint bottomLeft = new GPoint(ball.getX() + BALL_RADIUS - BALL_RADIUS/Math.sqrt(2.0), ball.getY() + BALL_RADIUS + BALL_RADIUS/Math.sqrt(2.0));
		GPoint bottomRight = new GPoint(ball.getX() + BALL_RADIUS + BALL_RADIUS/Math.sqrt(2.0), ball.getY() + BALL_RADIUS + BALL_RADIUS/Math.sqrt(2.0));
		
		
		if (getElementAt(top) != null) {
			return getElementAt(top);
		}
		else if (getElementAt(left) != null) {
			return getElementAt(left);
		}
		else if (getElementAt(bottom) != null) {
			return getElementAt(bottom);
		}
		else if (getElementAt(right) != null) {
			return getElementAt(right);
		}
		else if (getElementAt(topLeft) != null) {
			return getElementAt(topLeft);
		}
		else if (getElementAt(topRight) != null) {
			return getElementAt(topRight);
		}
		else if (getElementAt(bottomLeft) != null) {
			return getElementAt(bottomLeft);
		}
		else if (getElementAt(bottomRight) != null) {
			return getElementAt(bottomRight);
		}
		
		else{
			return null;
		}
	}

	/* Dictates what happens when the ball collides with an object.  Reverses vy direction if ball hits
	 * paddle or brick.  Removes brick upon collision.  Changes magnitude of vx upon collision with paddle.
	 * Makes sound effects for collisions and accumulates
	 * points for collisions with bricks
	 * Pre: Ball is in collision with an object.
	 * Post: Direction of ball switches.  If collision is with brick, brick is removed. Sound effect on collision.
	 * Points are accumulated and bricks remaining is decremented.
	 * 
	 */	
	private void collisionAction() {
		collider = getCollidingObject();
		if (collider != null && collider != ball && collider != speedMessage && collider != autoplayMessage && collider != pointsMessage) {
			if (collider == paddle) {
				vy = -Math.abs(vy);
				
				/*changeVx() is turned off while autoplay is on because autoplay hits the ball in the middle of 
				 * the paddle each time, and eventually the ball is essentially moving straight up and down.
				 */
				if (!autoPlay) {
				changeVx();
				}
				collisionSound();
			}
			
			else if (collider == randomBrick) {
				collisionSound();
				points += BONUS_POINTS;
				remove(randomBrick);
				createRandomBrick();
			}

			else {
				vy = -vy;
				collisionSound();
				pointsCalculation();
				remove(collider);
				bricksRemaining --;
			}
		}
		
	}
	
	/* Changes the magnitude of vx upon collision with the paddle.  Collision with edges of the paddle increase vx
	 * while collision the center of the paddle decreases vx.
	 * Pre: Ball is in collision with the paddle.
	 * Post: Magnitude of vx is increased or decreased.
	 */	
	private void changeVx() {
		double center = paddle.getX() + PADDLE_WIDTH/2;
		double distance = Math.abs((ball.getX() + BALL_RADIUS) - center);
			vx *= ((1 - (VX_MULTIPLIER * PADDLE_WIDTH/2)/2) + distance * VX_MULTIPLIER);
	}
	
	/* Makes sound effect upon collision with an object.
	 * Pre: Ball is in collision with an object.
	 * Post: 'Bouncing' sound effect is produced.
	 */	
	private void collisionSound() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
	}
	
	/* Calculates the points from collisions with bricks.  The higher bricks are worth more points.
	 * Pre: Ball is in collision with a brick.
	 * Post: Point total is increased by brick value.
	 */	
	private void pointsCalculation() {
		if (collider.getColor() == Color.RED) {
			points += 500;
		}
		else if (collider.getColor() == Color.ORANGE) {
			points += 400;
		}
		else if (collider.getColor() == Color.YELLOW) {
			points += 300;
		}
		else if (collider.getColor() == Color.GREEN) {
			points += 200;
		}
		else if (collider.getColor() == Color.CYAN) {
			points += 100;
		}
	}
	
	/* Sets up the game.  Draws rows of bricks and adds a paddle to the screen.
	 * Pre: none
	 * Post: Rows of bricks in rainbow colors are displayed near the top of the screen and the paddle
	 * 		is displayed centered near the bottom of the screen.
	 */	
	private void gameSetup() {
		makeBrickRows();
		paddle = makePaddle();
		addPaddleToCenter();
	}

	/* Makes an array of bricks, NBRICK_ROWS by NBRICK_COLUMNS.
	 * Pre: none
	 * Post: Rows of bricks in rainbow colors are displayed near the top of the screen.
	 */
	private void makeBrickRows() {
		for (int row = NBRICK_ROWS; row > 0; row--) {
			for(int col = 0; col < NBRICK_COLUMNS; col++) {
				drawBrick(row, col, row);
			}
		}
	}
	
	/* Draws a brick, colors it, and places it in the correct location in the brick array.
	 * Pre: none
	 * Post: A colored brick is added to the correct location.
	 */
	private void drawBrick(double row, double col, int i) {
		GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		double xPos = col*(BRICK_WIDTH + BRICK_SEP) + (getWidth() - ROW_WIDTH)/2;
		double yPos = row*(BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET;
		
		if(i == 1 || i == 2) {
			brick.setColor(Color.RED);
		}
		else if(i == 3 || i == 4) {
			brick.setColor(Color.ORANGE);
		}
		else if(i == 5 || i == 6) {
			brick.setColor(Color.YELLOW);
		}
		else if(i == 7 || i == 8) {
			brick.setColor(Color.GREEN);
		}
		else if(i == 9 || i == 10) {
			brick.setColor(Color.CYAN);
		}
		brick.setFilled(true);
		add(brick, xPos, yPos);
	}

	/* Draws the paddle.
	 * Pre: none
	 * Post: A filled rectangle is returned.
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;

	}
	/* Centers the paddle.
	 * Pre: Paddle created.
	 * Post: The paddle is centered at the bottom of the screen and is at the height, PADDLE_Y_OFFESET.
	 */
	private void addPaddleToCenter() {
		double initialXpos = (getWidth()-PADDLE_WIDTH)/2;
		double initialYpos = getHeight()-PADDLE_Y_OFFSET;
		add(paddle, initialXpos, initialYpos);
	}

	/* Makes the paddle track the x location of the mouse.
	 * Pre: Paddle created.
	 * Post: The center do the paddle follows the x location of the mouse.  
	 * 		The paddle remains in the bounds of the canvas.
	 */
	public void mouseMoved(MouseEvent e) {
		double xPos = e.getX() - PADDLE_WIDTH/2;
		double yPos = getHeight() - PADDLE_Y_OFFSET;
		double leftEdge = xPos;
		double rightEdge = xPos + PADDLE_WIDTH;
		if (leftEdge >= 0 && rightEdge <= getWidth()) {
			if (!autoPlay) {
			paddle.setLocation(xPos, yPos);
			}
		}
		
			
	}
	
	/* Creates certain game-play features which can be controlled by keys on the keyboard.
	 * Pre: playGame() is running
	 * Post: Pressing 'a' toggles autoplay.  'Up' and 'down' arrows control the speed of the ball.
	 */
	public void keyPressed(KeyEvent e) {
		
		//Turns autoplay on and off
		if (e.getKeyChar() == 'a') {
			autoPlay = !autoPlay;
		}
		
		//Pressing up arrow increases speed of ball
		if (e.getKeyCode() == 38) {
			if(vy > 0 && vx > 0) {
				vy += DELTA_V;
				vx += DELTA_V;
			}
			else if(vy > 0 && vx <0) {
				vy += DELTA_V;
				vx -= DELTA_V;
			}
			else if(vy < 0 && vx >0) {
				vy -= DELTA_V;
				vx += DELTA_V;
			}
			else if(vy < 0 && vx < 0) {
				vy -= DELTA_V;
				vx -= DELTA_V;
			}
		}
		
		//Pressing down arrow decreases speed of ball
		if (e.getKeyCode() == 40) {
			if(vy > 0 && vx > 0) {
				vy -= DELTA_V;
				vx -= DELTA_V;
			}
			else if(vy > 0 && vx <0) {
				vy -= DELTA_V;
				vx += DELTA_V;
			}
			else if(vy < 0 && vx >0) {
				vy += DELTA_V;
				vx -= DELTA_V;
			}
			else if(vy < 0 && vx < 0) {
				vy += DELTA_V;
				vx += DELTA_V;
			}
		}

	}
}
