/*
 * File: Breakout.java
 * -------------------
 * Name: Mia Leonard
 * Section Leader: Jonathan Kula
 * 
 * Plays a game of breakout, where you use a ball to hit floating tiles. When they are
 * all hit, they game has been won.
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

	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	private GOval ball = null;

	private double vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private GObject collider;

	
	/*
	 * I know this project is super unfinished, but as you can probably see, I've been working on it for around 10 hours 
	 * straight and at this point I'm getting random error messages that take me to a different screen when I try to run
	 * the program and for some reason my paddle isn't working even though I'm pretty sure
	 * it should and really I have no clue what's going on
	 * 
	 * I really doubt I'm going to get anything more done before the deadline but I'll keep trying until I
	 * fall asleep or something :( 
	 * 
	 * Sorry! I know you have to go over these and I really didn't mean to waste your time with this one. 
	 * 
	 */
	public void run() {
		//maybe i should try separating the velocity this time
		//and the ball creation and movement

		addMouseListeners();

		gameSetup();
		playGame();

	}

	private void gameSetup() {
		createBricks();
		createPaddle();
		createBall();

	}


	private void playGame() {
		
		moveBall();
		//need to remember a while statement since they only get 3 turns

	}

	private void createBricks() {
		for(int brickRows = 0; brickRows < NBRICK_COLUMNS; brickRows++){

			//the "wall" refers to the total game board space, as if marking the outside 'walls' of a monopoly board
			double wallSize = BRICK_WIDTH*NBRICK_ROWS + BRICK_SEP*(NBRICK_ROWS - 1);

			for(int brickColumns = 0; brickColumns < NBRICK_ROWS; brickColumns++ ){
				double offsetHorizontal = (getWidth()/2 - wallSize/2 + brickColumns*BRICK_WIDTH + brickColumns*BRICK_SEP);
				double offsetVertical = BRICK_Y_OFFSET + brickRows*BRICK_HEIGHT + brickRows*BRICK_SEP;

				GRect coloredBricks = new GRect(offsetHorizontal, offsetVertical, BRICK_WIDTH, BRICK_HEIGHT);
				coloredBricks.setFilled(true);

				//colors the bricks based on their row 
				if(brickRows <= 1){
					coloredBricks.setColor(Color.RED);
				}else if (brickRows == 2 || brickRows == 3){
					coloredBricks.setColor(Color.ORANGE);
				}else if (brickRows == 4 || brickRows == 5){
					coloredBricks.setColor(Color.YELLOW);
				}else if (brickRows == 6 || brickRows == 7){
					coloredBricks.setColor(Color.GREEN);
				}else {
					coloredBricks.setColor(Color.BLUE);
				}

				add(coloredBricks);
			}

		}

	}


	private void createPaddle(){
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);

	}

	public void mouseMoved(MouseEvent movePaddle){

		//assigns an x value to the paddle based on the mouse position
		double x = movePaddle.getX();


		//keeps paddle from moving up or down, locked in offset position
		double y = getHeight() - PADDLE_Y_OFFSET;

		//keeps paddle from moving off screen
		double edge = 0;
		if( x > edge && x + PADDLE_WIDTH < getWidth()){
			paddle.setLocation(x,y);
		}
		

	}

	private void createBall(){

		double offsetHorizontal = getWidth()/2;
		double offsetVertical = getHeight()/2;

		ball = new GOval(offsetHorizontal, offsetVertical, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);

	}


	private void moveBall(){
		
		waitForClick();
			
			//for randomly generating velocity of ball
			double vx = rgen.nextDouble(1.0, 3.0);
			double vy = VELOCITY_Y;
			if (rgen.nextBoolean(0.5)){ 
				vx = -vx;
			}
			
			while(true){	
			
			ballHitsSomething();
			//setting minimum and maximum values, similar to how I did for the paddle
			double xMax = getWidth() - BALL_RADIUS*2;
			double xMin = 0;
			double yMax = getHeight() - BALL_RADIUS*2; 
			double yMin = 0;
						
			//changes direction when ball hits horizontal barrier
			if(ball.getX() > xMax || ball.getX() < xMin){
				vx = -vx;
			}
			
			//changes direction when ball hits vertical barrier
			if(ball.getY() > yMax || ball.getY() < yMin){
				vy = -vy;
			}
			
			ball.move(vx, vy);

			pause(DELAY);
			
			}
	
	}

	private void ballHitsSomething() {
		getCollidingObject();
		
		if(collider == paddle){
			vy = -vy;
		
		}
		
		if(collider != paddle){
			remove(collider);
		}
		
		vy = -vy; //takes brick makes it bounce off
	}

	private GObject getCollidingObject() {
		
		
		if(getElementAt(ball.getX(), ball.getY()) != null){
			 collider = getElementAt(ball.getX(), ball.getY());
			return(collider);
		}
		
		if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null){
			collider = (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()));
			return(collider);
		}
		
		if (getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS) != null){
			collider = (getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS));
			return(collider);
		} 
		
		if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) != null){
			collider = (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS));
			return(collider);
		}else{
			return(null);
		}
	}

		
	}