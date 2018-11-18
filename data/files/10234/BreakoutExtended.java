/*
 * File: BreakoutExtended.java
 * -------------------
 * Name: Eddie Mattout
 * Section Leader: Chase Davis 
 * 
 * This file implements the game of Breakout. The program first sets up the board with 
 * 10 columns and 10 rows of bricks. It then creates a rectangle, which is used as a paddle. 
 * The middle of the paddle is coordinated with the mouse and moves as the mouse moves. Lastly,
 * the program creates a ball which starts at the center of the screen. 
 * Once the user clicks their mouse, the ball starts to move. The ball initially goes towards the 
 * bottom of the screen. If the user manages to get the paddle under the ball by the time 
 * the ball reaches the paddle, the ball bounces off the paddle towards the bricks. When the ball hits
 * a brick, it removes it and changes its direction. The game continues until there are either 
 * no more bricks on the screen or the ball manages to avoid the paddle when at the bottom of 
 * the screen. The extension of the program adds labels that indicate how many lives are the 
 * user has left, and how many bricks they have hit. It also plays sounds every time a brick
 * or the paddle is hit. Lastly, it puts an image if the user manages to win the game. 
 */


import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtended extends GraphicsProgram {

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
	public static final double BRICK_WIDTH = Math.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	// Diameter of the ball in pixels 
	public static final double BALL_DIAMETER = 2 * BALL_RADIUS; 

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
	
	// Number of bricks that it takes to speed up 
	public static final int SPEED_UP = 5; 
	
	// Label Offset
	private static final int TEXT_WIDTH = 300;
	private static final int TEXT_HEIGHT = 500;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBoard();
		playGame();
	}

	/*
	 * This method sets up the board for the game of Brickbreaker. The method 
	 * first sets up the bricks based on the number of columns and number of rows, 
	 * it then creates a paddle which moves with the mouse, creates the ball,
	 * and lastly adds labels that tell the user how many lives they have left. 
	 */
	private void setUpBoard() {
		setUpBricks();
		setUpPaddle(); 
		setUpBall();
		addLabels();
	}

	/*
	 * This method adds bricks to the center of the screen based on the number 
	 * of rows and columns predefined in the static variables. It then colors in every 
	 * two rows of  bricks with a different color. 
	 */
	private void setUpBricks() {
		// Sets up the bricks. 
		for (int row = 1; row <= NBRICK_ROWS; row++) {
			double y = row * BRICK_HEIGHT + row * BRICK_SEP + BRICK_Y_OFFSET;  
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double x = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH - (NBRICK_COLUMNS - 1) * BRICK_SEP)/2 + (col * BRICK_WIDTH + col *BRICK_SEP); 
				GRect brick =  new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				// Colors in the bricks.                                                                                                                                                                                                                                                                                                                                                                                                                    
				brick.setFilled(true);
				Color color = getBrickColor(row);
				brick.setFillColor(color);
				brick.setColor(color);
			}
		}
	}

	/*
	 * This method determined the color of the bricks depending on the row of bricks. 
	 * The color of the bricks remain constant for two rows and run in the 
	 * following rainbow-like sequence: RED,ORANGE, YELLOW, GREEN, CYAN. 
	 */
	private Color getBrickColor(int row) { 
		if ((row % 10 == 1) || (row % 10 == 2)) {
			return Color.RED;
		} else if ((row % 10 == 3) || (row % 10 == 4)) {
			return(Color.ORANGE);	
		} else if ((row % 10 == 5) || (row % 10 == 6)) {
			return(Color.YELLOW);
		} else if ((row % 10 == 7) || (row % 10 == 8)) {
			return(Color.GREEN);
		} else if ((row % 10 == 9) || (row  % 10 == 0)) {
			return(Color.CYAN);
		}
		return null; 
	}

	/*
	 * Method that adds a rectangle at the bottom of the screen which is 
	 * the paddle of the Brickbreaker game. The paddle is responsive to the 
	 * movement of the mouse. 
	 */
	private void setUpPaddle() {
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		double paddleX = (getWidth()- PADDLE_WIDTH)/2; 
		paddle.setLocation(paddleX, paddleY);
		add(paddle);
		paddle.setFilled(true);	
		addMouseListeners();
	}

	/*
	 * This method moves the paddle based on the location of the mouse. The center of 
	 * the paddle is in correlation with the mouse. If the mouse moves left, the paddle will
	 * move left. If the mouse moves right, the paddle will go right. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if ((mouseX > PADDLE_WIDTH/2) && (mouseX < getWidth() - PADDLE_WIDTH/2)) {
			paddle.setLocation(mouseX - (PADDLE_WIDTH / 2), getHeight() - PADDLE_Y_OFFSET );
		}
	}

	/*
	 *  Places a ball of radius 50 at the center of the screen. 
	 */
	public void setUpBall() {
		double ballX = (getWidth() - BALL_DIAMETER)/2;
		double ballY = (getHeight() - BALL_DIAMETER)/2;
		ball.setLocation(ballX, ballY);	
		add(ball);
		ball.setFilled(true);
	}

	/*
	 * This method plays the Brickbreaker game. The game plays the game a maximum of three
	 * times. If the player does not hit the ball with the paddle, the ball is placed at the 
	 * middle of the screen and the user can click and play again for a total
	 * of three turns. . If the user hits all the bricks, the game finishes and the user wins.  
	 */
	private void playGame() { 
		for (int i = 0; i < NTURNS; i++) { 
			if(!moveBall()) {
				ball.setLocation((getWidth() - BALL_DIAMETER)/2, (getHeight() - BALL_DIAMETER)/2); 
				livesLeft.setText("Lives Left:" + (NTURNS - i-1));
			} else {
				GImage img = new GImage ("Winner.jpg");
				add(img, 0 ,0);
				break; 
			} 
		}
	}

	/*
	 * Once the user clicks the mouse, the ball begins to move. If the ball goes 
	 * beyond the bottom of the screen the boolean returns false, and the game starts over.
	 * The method makes sure the ball bounces off the left, right and top walls. 
	 * The ball constantly checks to see if there are objects at any of its four corners. 
	 * If the ball detects an object: 
	 * If it is the paddle, the ball bounces off it. 
	 * If it is a brick, the ball removes the brick and keeps track of how many bricks it has removed. 
	 */
	private boolean moveBall() { 
		waitForClick();  
		ballVelocity();
		// Ball is in an animation loop while it is on the screen, above the bottom wall. 
		while (ball.getY() < getHeight()) {
			ball.move(vx,vy);
			bounceOffWalls(); 
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				bounceOffPaddle(); 
				// Ball removes a brick that it touches, goes in the opposite direction, and counts how many bricks it has removed. 
			} else if (collider != null && collider != bricksLeft && collider != livesLeft) {
				screenBlink(); 
				remove(collider);
				count++;
				bounceClip.play();
				bricksLeft.setText("Bricks Left:"+ ((NBRICK_ROWS * NBRICK_COLUMNS) - count));
				if (count == (NBRICK_ROWS * NBRICK_COLUMNS)) {
					return true;
				}
				// Increases speed of the ball every time it hits a brick.  
				if (count % SPEED_UP == 0 ) {
					vy = (vy++);
				}
				vy= -vy; 
			}
			pause(DELAY);
			
		}
		// Boolean is false if the ball moves past the bottom wall. 
		return false;
		
	}
	/*
	 * This method creates a blink on the screen every time the ball hits a brick. 
	 */
	private void screenBlink() {
		setBackground (Color.BLACK);
		pause(DELAY);
		setBackground (Color.WHITE);
	}
	
	/*
	 * This method makes the ball bounce off the paddle, and plays a sound.  
	 */
	
	private void bounceOffPaddle() { 
		vy = -Math.abs(vy);
		bounceClip.play();
	}

	/*
	 * This method determines the x and y velocities of the ball. 
	 * It defines the x velocity as a random number between the minimum velocity 
	 * and the maximum velocity, so that it starts going towards a new direction
	 * every round of the game. The y velocity remains constant no matter what. 
	 */
	private void ballVelocity() {
		vx =  rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx; 
		}
		vy = VELOCITY_Y; 
	}

	/*
	 * This method checks to see if the ball touches a wall. If the ball 
	 * is at the left, right or top wall, the ball bounces off the wall, by changing its direction. 
	 * If the ball touches the bottom wall, the ball does not bounce off. 
	 */
	private void bounceOffWalls() {
		// Bounce off right wall. 
		if (ball.getX() >= getWidth() - BALL_DIAMETER) {
			vx = -vx;	
		} 
		// Bounce off left wall. 
		if (ball.getX() <= 0) {
			vx = -vx; 	
		} 
		// Bounce off top wall.
		if (ball.getY() <= 0) {
			vy = -vy; 
		}
	}	

	/*
	 * This method checks if there is an object at any one of the four corners of 
	 * the ball. If there is an object at one of the four corners, the method returns that 
	 * object and stores it in the variable of getCollidingObject. 
	 * If there is no element present, the method returns a null value. 
	 */
	private GObject getCollidingObject() {
		// Checks the top left corner of the ball for an object. 
		GObject Corner = getElementAt (ball.getX(), ball.getY()); 
		if ((Corner != null) && (Corner != paddle))  {
			return (Corner); 
		} 
		// Checks the top right corner of the ball for an object. 
		Corner = getElementAt (ball.getX() + BALL_DIAMETER, ball.getY());
		if ((Corner !=null) && (Corner != paddle)) {
			return (Corner); 
		}
		// Checks the bottom left corner of the ball for an object. 
		Corner = getElementAt (ball.getX(), ball.getY() + BALL_DIAMETER); 
		if (Corner !=null) {
			return (Corner); 
		}
		// Checks the bottom right corner of the ball for an object. 
		Corner = (getElementAt (ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER));
		if (Corner !=null) {
			return (Corner);
		}
		return null;
	}
	
	/*
	 * This method adds a label at the top left corner of the screen. 
	 * This label will tell the user how many turns they have left in the game.
	 * This method also adds a label to the bottom right corner of the screen, 
	 * which tells the user how many bricks they have left to hit.  
	 */
	private void addLabels() {
		livesLeft = new GLabel ("Lives Left: "  + NTURNS, 20, 20);
		add(livesLeft);
		bricksLeft = new GLabel ( "Bricks Left:"+ ((NBRICK_ROWS * NBRICK_COLUMNS)), TEXT_WIDTH, TEXT_HEIGHT);
		add(bricksLeft);
	}

	/** Private instance variables.*/
	private GRect paddle = new GRect(getX(), getY(), PADDLE_WIDTH, PADDLE_HEIGHT);
	private GOval ball = new GOval (getX(), getY(), BALL_DIAMETER, BALL_DIAMETER);
	private RandomGenerator rgen = RandomGenerator.getInstance(); 
	private double vx;  
	private double vy;
	private int count; 
	private GLabel livesLeft;
	private GLabel bricksLeft;
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
}










