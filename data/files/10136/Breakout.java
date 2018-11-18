/*
 * File: Breakout.java
 * -------------------
 * Name: Marine Yamada 
 * Section Leader: Rhea Karuturi 
 * 
 * This creates the game breakout. 
 * The objective of the game is to get rid 
 * of all the bricks by hitting them with a ball, 
 * which bounces off of the walls and the paddle. 
 * The user gets three tries to complete this objective
 * and loses a turn each time the ball hits the bottom. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 5.0;

	// Color of ball 
	private static final Color BALL_COLOR = Color.BLACK;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 4.0;
	public static final double VELOCITY_X_MAX = 7.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 500.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Need to set the paddle equal to something first so we can use the methods that require it  
	GRect paddle = null; 

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private double vx, vy; 
	private GOval ball;

	private int BrickCount = NBRICK_COLUMNS*NBRICK_ROWS;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Sets the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//setup 
		makeRowOfBricks(); 
		paddle = createPaddle(); 
		addMouseListeners();

		//play 
		ball = makeBall();
		ballInPlay();
		waitForClick();

		//number of turns per game 
		int turnsLeft = NTURNS;

		while(true) {
			// update velocity
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball) || hitBottomWall(ball)) {
				vy = -vy;
			}

			// update visualization
			ball.move(vx, vy);

			// pause
			pause(DELAY);

			//check for collisions 
			checkCollider();

			//if hits bottom & no turns left --> will remove ball and display you lost message with music 
			//if hits bottom & turns left --> will remove ball, add it in the center, and will be in play after user clicks 
			if (ball.getY()+BALL_RADIUS*2 >= getHeight()) {
				turnsLeft --;
				if (turnsLeft == 0) { 
					remove(ball);
					break;
				} else {
					remove(ball);
					add(ball, getWidth()/2, getHeight()/2);
					waitForClick();
					ballInPlay();
				}
			}

			//checks for when user wins game 
			//precondition: number of bricks left is 0 
			//postcondition: ball is removed, message alerting that the user has won appears and music plays
			if (BrickCount == 0) {
				remove(ball);
				break; 
			}
		}
	}

	//makes a colored brick with the dimensions given by constants at its appropriate location
	//precondition: x and y position of the brick and its color is defined 
	//postcondition: brick with the given dimensions and color is placed at the given location 
	private void makeABrick(double x, double y, Color color) {
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setFillColor(color);
		brick.setColor(color);
		add(brick);
	}

	//makes the ten rows of bricks with the appropriate color; 
	//x and y position is determined by the column number and the number of bricks left to filled in the row
	private void makeRowOfBricks() {
		for (int j = 1; j <= NBRICK_ROWS; j++) {
			for (int i = 0; i< NBRICK_COLUMNS; i++) {
				double startXposition =  i*(BRICK_WIDTH + BRICK_SEP) + (getWidth()-NBRICK_COLUMNS*BRICK_WIDTH-(NBRICK_COLUMNS-1)*BRICK_SEP)/2;	
				double startYposition = (BRICK_Y_OFFSET+((BRICK_SEP + BRICK_HEIGHT)*j));	
				if (j== 1 || j==2) {
					makeABrick(startXposition, startYposition, Color.RED);
				}
				if (j== 3 || j==4) {
					makeABrick(startXposition, startYposition, Color.ORANGE);
				}
				if (j== 5 || j==6) {
					makeABrick(startXposition, startYposition, Color.YELLOW);
				} 
				if (j ==7 || j ==8) {
					makeABrick(startXposition, startYposition, Color.GREEN);
				}
				if (j== 9 || j==10) {
					makeABrick(startXposition, startYposition, Color.CYAN);
				}
				if (j > 10) {
					makeABrick(startXposition, startYposition, Color.PINK); 
				}
			}
		}
	}

	//makes a black paddle and sets it towards the bottom of the screen 
	private GRect createPaddle() {
		GRect paddle = new GRect(getWidth()/2, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true); 
		paddle.setFillColor(Color.BLACK);
		paddle.setColor(Color.BLACK);
		return paddle; 
	}

	//makes paddle move with mouse, preventing it from going off the screen 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX(); 
		if (mouseX > getWidth()-(PADDLE_WIDTH / 2)) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, getHeight()-PADDLE_Y_OFFSET);
		} else if (mouseX < PADDLE_WIDTH / 2){
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET); 
		} else {
			paddle.setLocation(mouseX- PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET); 
		}
		add(paddle);
	}

	//makes the ball according to the size and color given by constants and puts it in the middle of the window 
	public GOval makeBall() {
		double size = BALL_RADIUS * 2;
		GOval r = new GOval(size, size);
		r.setFilled(true);
		r.setColor(BALL_COLOR);
		add(r, getWidth()/2, getHeight()/2);
		return r;
	}

	//puts the ball in play with a random velocity between the range set by the given constants of the maximum and minimum velocity  
	private void ballInPlay() {
		vy = VELOCITY_Y; 
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
		if (rgen.nextBoolean(0.5)) {
			vx = -vx; 
		}
	}
	
	//took the following four lines from Chris' bouncing balls solution 
	//returns whether or not ball should bounce off of the bottom of the window 
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	//returns whether or not ball should bounce off of the top of the window
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	//returns whether or not ball should bounce off of the right side of the window 
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	//returns whether or not ball should bounce off of the left side of the window 
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	//checks the first corner of the ball to see if there is an object (paddle or brick) at that point 
	//if object is there --> returns it 
	//if there is nothing --> checks the next corner (repeats if condition is the same until it checks all four corners) 
	private GObject getCollidingObject(GOval ball) {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider != null) {
			return collider;
		} else if (collider == null) {
			collider = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		}
		if (collider != null) {
			return collider;
		}else if (collider== null) {
			collider = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+2*BALL_RADIUS);

		} 
		if (collider != null) {
			return collider;
		}else {
			collider = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+2*BALL_RADIUS);
			if (collider != null) {
				return collider; 
			}
		}
		return null;
	}

	//checks if the ball has collided with the paddle or a brick 
	private void checkCollider() {
		GObject collider = getCollidingObject(ball);
		if (collider == paddle) { //changes trajectory of the ball 
			if (vy > 0) vy = -vy;
		} else if (collider != null) {	//if collider is a brick
			vy = -vy;
			remove(collider);
			BrickCount --;
		}
	}
}
