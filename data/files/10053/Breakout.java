/*
 * File: Breakout.java
 * -------------------
 * Name: Tess Stewart
 * Section Leader: Garrick Fernandez 
 * Due Date: February 7, 2018
 * 
 * This file creates the game of Breakout. It begins with 10 rows of 10 bricks on the screen. 
 * The goal is to remove all the bricks from the screen by hitting them with the ball. The 
 * ball bounces off the paddle as well as the three sides of the window. However, if the ball
 * misses the paddle and goes past the bottom wall, the round is over. The game consists of 
 * three rounds (or three chances to remove all the bricks).  
 * 
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
	public static double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle; 
	private GOval ball; 
	private int numBricksLeft = NBRICK_ROWS*NBRICK_COLUMNS; 
	private int numPaddleHits = 0; 

	//keep track of velocity of ball
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {

		setUpWindow(); 
		addMouseListeners();	

		StartMessage(); 
		playGame();
		EndMessage(); 
	}

	/* Method: set up window
	 * -----------------------
	 * this sets up the screen for the game. It places the bricks, title, and paddle 
	 */
	private void setUpWindow() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		makePaddle();
	}

	/* Method: play game
	 * -----------------------
	 * this sets up the three rounds of the game (with corresponding messages)  
	 */
	private void playGame() {
		makeRound(); 
		if (numBricksLeft!=0) {
			roundTwoMessage();
			makeRound();
		}
		if (numBricksLeft!=0) {
			roundThreeMessage();
			makeRound();
		}

	}

	/* Method: make round
	 * -----------------------
	 * this begins each round of the game. It animates sets the velocity and makes the ball
	 * bounce off the walls. It also detects if the game is over and then ends the round. 
	 */
	private void makeRound() {

		vx = rgen.nextDouble(VELOCITY_X_MIN , VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy =  VELOCITY_Y;

		makeBall(); 
		while (!gameOver() && numPaddleHits< 7) {

			if (hitRightWall() || hitLeftWall()) {
				vx=-vx;
			}
			if (hitTopWall() ) {
				vy= -vy;		
			}
			ball.move(vx, vy);
			removeBricks(); 
			pause(DELAY); 

		}
		
		speedUpBall(); 
		while (!gameOver() && numPaddleHits>= 7) {

			if (hitRightWall() || hitLeftWall()) {
				vx=-vx;
			}
			if (hitTopWall() ) {
				vy= -vy;		
			}
			ball.move(vx, vy);
			removeBricks(); 
			pause(DELAY); 

		}
		remove(ball);
		//reset initial velocity
		numPaddleHits= 0; 
	}

	/*Method: speedUpBall
	 * -----------------------
	 * this triples the velocity so that the game becomes harder
	 */
	private void speedUpBall() {
		vx= vx*3;
		vy= vy*1.5;
	}

	/*Method: remove bricks
	 * -----------------------
	 *this reads in the value from getCollidingObjects and sees if the value is something other 
	 *than null. If it is not null, it checks to see if it is the paddle. If it is, the vertical 
	 *velocity switches so that the ball "bounces" off the paddle. Additionally, it keep track of 
	 *the number of paddle hits so that it increases the speed after 7. If it is not the paddle, it 
	 *must be a brick, so this removes the brick and switches the vertical velocity so that is 
	 *"bounces" off the brick. 
	 */
	private void removeBricks() {
		GObject collider = getCollidingObjects();
		if (collider!= null) {
		}
		if (collider == paddle) {
			vy= -vy; 
			numPaddleHits= numPaddleHits+1; 
		}
		if (collider!= null && collider != paddle) {
			GObject brick = collider;  
			remove(brick); 
			vy=-vy; 
			numBricksLeft= numBricksLeft - 1; 
		}


	}

	/* Method: get colliding objects
	 * -----------------------
	 * Detects if there are any objects at the four specified points of the ball. These 
	 * four points are the four corners directly outside the ball. It begins with the top right corner. 
	 * If there is nothing there it moves onto the rest of the corners. This method either retuns the 
	 * object that was hit, or returns the value null. 
	 */
	private GObject getCollidingObjects() {
		GObject object=null; 
		if (getElementAt(rightOfBall(), topOfBall()) != null) {
			object =  getElementAt(rightOfBall(),  topOfBall()); 
		} 
		if (getElementAt(rightOfBall(),  bottomOfBall()) != null) {
			object =  getElementAt(rightOfBall(),  bottomOfBall()); 
		} 
		if (getElementAt(leftOfBall(),  topOfBall()) != null) {
			object =  getElementAt(leftOfBall(),  topOfBall()); 
		} 
		if (getElementAt(leftOfBall(),  bottomOfBall()) != null) {
			object =  getElementAt(leftOfBall(),  bottomOfBall()); 
		} 
		return (object); 

	}


	/* Method: set up bricks
	 * -----------------------
	 * Method meant to add the starting bricks to the screen (create 10 rows of 10 
	 * bricks that change color every two rows)
	 * Pre: blank screen
	 * Post: 10 rows of bricks that change color every two rows.Each row is evenly spaced
	 * and the entire block starts a set distance from the top of the screen  
	 */
	private void setUpBricks() {
		int loopNum = 0;
		for (int i = 0; i<NBRICK_ROWS; i += 1) {
			//set y location for each row 
			double y = BRICK_Y_OFFSET+(i*(BRICK_HEIGHT+BRICK_SEP));

			// Change brick color every two rows 
			Color rowColor = setRowColor(loopNum);

			for (int brickNum = 0 ;brickNum < NBRICK_COLUMNS; brickNum += 1) {
				double x = ((getWidth()-lengthOfRow())/2)+(brickNum*(BRICK_WIDTH+BRICK_SEP));
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT); 
				brick.setLineWidth(0);
				brick.setFilled(true);
				brick.setFillColor(rowColor);
				add(brick);
			} 

			loopNum=loopNum+1;
		}

	}

	/*Method: length of row
	 * -----------------------
	 * detects the total length of a row so that it can be placed in the center of the screen
	 */
	private double lengthOfRow() {
		double length = (NBRICK_COLUMNS*(BRICK_WIDTH))+ ((NBRICK_COLUMNS-1)*BRICK_SEP);
		return length;
	}

	/* Method: set row color
	 * -----------------------
	 * make it so that the row color changes ever two rows. This reads in the loop number 
	 * (or what row number the program is on) and then assigns the appropriate color
	 */
	private Color setRowColor(int loopNum) {
		Color rowColor = null; 
		if ((loopNum== 0)||(loopNum== 1)) {
			rowColor= Color.RED;
		}
		if ((loopNum== 2)||(loopNum== 3)) {
			rowColor= Color.ORANGE;
		}
		if ((loopNum== 4)||(loopNum== 5)) {
			rowColor= Color.YELLOW;
		}
		if ((loopNum== 6)||(loopNum== 7)) {
			rowColor= Color.GREEN;
		}
		if ((loopNum== 8)||(loopNum== 9)) {
			rowColor= Color.CYAN;
		}
		return rowColor;
	}

	/* Method: mouse event
	 * -----------------------
	 * Make is to the paddle moves across the window (in the x direction) as the mouse
	 * moves across the screen
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX= e.getX();
		if (mouseX>=(getWidth()-PADDLE_WIDTH)) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, getHeight()-PADDLE_Y_OFFSET);
		}else {
			paddle.setLocation(mouseX, getHeight()-PADDLE_Y_OFFSET);
		}
	}

	/* Method: make paddle
	 * -----------------------
	 * make starting paddle so that it is at the correct offset height and on the left
	 * side of the window. Make it so the paddle does not extend beyond the bounds of the 
	 * right side of the window.  
	 */
	private void makePaddle() {
		paddle = new GRect(0, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
	}

	/* Method: make ball
	 * -----------------------
	 * make a ball in the center of the window	 
	 */
	private void makeBall() {
		ball = new GOval ((getWidth()/2)-(BALL_RADIUS), (getHeight()/2)-BALL_RADIUS, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}


	/* Method: Start
	 * -----------------------
	 * prints the first message on the screen. It tells the user to click to begin the game
	 */
	private void StartMessage() {
		GLabel start = new GLabel("Click to Start");
		start.setFont("Helvetica-24");
		start.setLocation((getWidth()-start.getWidth())/2, (getHeight()-start.getHeight())/2);
		add(start);
		waitForClick();
		remove(start); 

	}


	/* Method: round two message
	 * -----------------------
	 * prints the mid round message, when you have two ball left. It then waits
	 * for the user to click before starting the next round
	 */ 
	private void roundTwoMessage() {
		GLabel roundTwo = new GLabel("You have 2 more balls. Click to Begin.");
		roundTwo.setFont("Helvetica-24");
		roundTwo.setLocation((getWidth()-roundTwo.getWidth())/2, (getHeight()-roundTwo.getHeight())/2);
		add(roundTwo);
		waitForClick();
		remove(roundTwo); 

	}

	/* Method: round three message
	 * -----------------------
	 * prints the mid round message, when you only have one ball left. It then waits
	 * for the user to click before starting the next round
	 */
	private void roundThreeMessage() {
		GLabel roundThree = new GLabel("You have 1 more ball. Click to Begin.");
		roundThree.setFont("Helvetica-24");
		roundThree.setLocation((getWidth()-roundThree.getWidth())/2, (getHeight()-roundThree.getHeight())/2);
		add(roundThree);
		waitForClick();
		remove(roundThree); 

	}

	/* Method: end massage
	 * -----------------------
	 * prints the last message of the game (after the three rounds) and tell you if you won or lost 
	 */
	private void EndMessage() {
		if (passBottomWall()) {
			GLabel end = new GLabel("You Lose :( ");
			end.setFont("Helvetica-24");
			end.setLocation((getWidth()-end.getWidth())/2, (getHeight()-end.getHeight())/2);
			add(end);
		} else {
			remove(ball);
			GLabel end = new GLabel("YOU WIN!");
			end.setFont("Helvetica-Bold-50");
			end.setLocation((getWidth()-end.getWidth())/2, (getHeight()-end.getHeight())/2);
			add(end);
		}
	}

	/* Method: game over
	 * -----------------------
	 * If the ball passes the bottom wall or if there are no bricks left, the game should end 
	 */
	private boolean gameOver() {
		return(passBottomWall()) || (numBricksLeft==0);
	}

	/* Method: right of ball
	 * -----------------------
	 * returns the location of the right most point of the ball 
	 */
	private double rightOfBall() {
		return ball.getCenterX()-BALL_RADIUS;
	}

	/* Method: left of ball
	 * -----------------------
	 * returns the location of the left most point of the ball 
	 */
	private double leftOfBall() {
		return ball.getCenterX()+BALL_RADIUS;
	}

	/* Method: bottom of ball
	 * -----------------------
	 * returns the location of the lowest point of the ball 
	 */
	private double bottomOfBall() {
		return ball.getCenterY()+BALL_RADIUS;
	}

	/* Method: top of ball
	 * -----------------------
	 * returns the location of the highest point of the ball 
	 */
	private double topOfBall() {
		return ball.getCenterY()-BALL_RADIUS;
	}

	/* Method: Pass Bottom Wall
	 * -----------------------
	 * If the ball passes the bottom wall, it should end the game 
	 */
	private boolean passBottomWall() {
		return ball.getY() >= getHeight() + ball.getHeight();
	}

	/* Method: Hit Top Wall
	 * -----------------------
	 * If the ball hits the top wall, it should bounce off (switch vertical velocities)
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	/* Method: Hit Left Wall
	 * -----------------------
	 * If the ball hits the left wall, it should bounce off (switch horizontal velocities)
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	/* Method: Hit Right Wall
	 * -----------------------
	 * If the ball hits the right wall, it should bounce off (switch horizontal velocities)
	 */
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
}
