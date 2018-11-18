/*
 * File: Breakout.java
 * -------------------
 * Name: Brian Kaplun
 * Section Leader: Rachel Gardner
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
//
public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	private static final int CANVAS_WIDTH = 420;
	private static final int CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	private double BRICK_WIDTH = Math.floor(
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
	public static  double VELOCITY_X_MIN = 1.0;
	public static  double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	int turns = NTURNS;


	//instance variables for the objects
	private GRect paddle = null; 
	private GOval ball = null; 
	private GRect brick = null;
	private GObject collider = null;

	//instance variables relating to ball movement, including the dx and dy, x coordinates for mouse listeners
	double mouseX;
	double vx; 
	double vy = VELOCITY_Y;

	//countdown of number of bricks
	double b = 100;

	//instance for random number generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//this adds the mouse listeners
	public void init() {
		addMouseListeners();
	}

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//This initializes the setup of the game
		setUpGame(); 
		//this adds motion to the game and allows a user to play it 
		playGame();
		
		//uncommenting this adds extensions
		//addExtensions();
	}
	

	private void setUpGame() {
		makeBricks();
		makePaddle();
		makeBall();		
	}


	//this makes bricks
	private void makeBricks() {

		int r =NBRICK_ROWS - 9;
		double startingbrickx = (CANVAS_WIDTH/2.0) - ((5*BRICK_WIDTH)+(4.5*BRICK_SEP));
		double startingbricky = BRICK_Y_OFFSET + (r*BRICK_HEIGHT); 
		for(r=1; r<=2; r++) {
			for(int i = 1; i<=10; i++) 
			{brick = new GRect(startingbrickx, startingbricky, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(Color.RED);
			add(brick);
			startingbrickx = startingbrickx + BRICK_WIDTH + BRICK_SEP;}
			startingbrickx= (getCanvasWidth() /2) - ((5*BRICK_WIDTH)+(4.5*BRICK_SEP));
			startingbricky = startingbricky + BRICK_HEIGHT + BRICK_SEP;
		}
		

		for(r=3; r<=4; r++) {
			for(int i = 1; i<=10; i++) { brick = new GRect(startingbrickx, startingbricky, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(Color.ORANGE);
			add(brick);
			startingbrickx = startingbrickx + BRICK_WIDTH + BRICK_SEP;}
			startingbrickx= (getCanvasWidth() /2) - ((5*BRICK_WIDTH)+(4.5*BRICK_SEP));
			startingbricky = startingbricky + BRICK_HEIGHT + BRICK_SEP;
		}
		for(r=5; r<=6; r++) {
			for(int i = 1; i<=10; i++) { brick = new GRect(startingbrickx, startingbricky, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(Color.YELLOW);
			add(brick);
			startingbrickx = startingbrickx + BRICK_WIDTH + BRICK_SEP;}
			startingbrickx= (getCanvasWidth() /2) - ((5*BRICK_WIDTH)+(4.5*BRICK_SEP));
			startingbricky = startingbricky + BRICK_HEIGHT + BRICK_SEP;
		}
		for(r=7; r<=8; r++) {
			for(int i = 1; i<=10; i++) { brick = new GRect(startingbrickx, startingbricky, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(Color.GREEN);
			add(brick);
			startingbrickx = startingbrickx + BRICK_WIDTH + BRICK_SEP;}
			startingbrickx= (getCanvasWidth() /2) - ((5*BRICK_WIDTH)+(4.5*BRICK_SEP));
			startingbricky = startingbricky + BRICK_HEIGHT + BRICK_SEP;
		}
		for(r=9; r<=10; r++) {
			for(int i = 1; i<=10; i++) { brick = new GRect(startingbrickx, startingbricky, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(Color.CYAN);
			add(brick);
			startingbrickx = startingbrickx + BRICK_WIDTH + BRICK_SEP;}
			startingbrickx= (getCanvasWidth() /2) - ((5*BRICK_WIDTH)+(4.5*BRICK_SEP));
			startingbricky = startingbricky + BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	//this makes the paddle 
	private void makePaddle() {
		paddle = new GRect(((getCanvasWidth()-PADDLE_WIDTH)/2), getCanvasHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.black);
		add(paddle);
	}

	//this allows the mouse to move the paddle within the canvas width
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		if(mouseX >= (PADDLE_WIDTH /2.0) && mouseX <= (getCanvasWidth() - PADDLE_WIDTH/2.0)) {
			paddle.setLocation(mouseX - PADDLE_WIDTH/2.0, getCanvasHeight() - PADDLE_Y_OFFSET);
		}
	}

	//this makes the ball 
	private void makeBall() {
		ball = new GOval(getCanvasWidth()/2.0- BALL_RADIUS, getCanvasHeight()/2.0 -BALL_RADIUS, 2.0*BALL_RADIUS, 2*BALL_RADIUS); 
		ball.setFilled(true);
		ball.setColor(Color.MAGENTA);
		add(ball);
	}	

	private void playGame() {
		//this sets the random initial vx
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		//this creates a label for the number of turns one has left		
		waitForClick();	

		//this sets up the loop for game play along with the if statements that affect it
		while(true) {
			ball.move(vx, vy);
			pause(DELAY);
			//this establishes the horizontal bounces
			if((ball.getX() + 2.0*BALL_RADIUS >= getCanvasWidth()) || (ball.getX() <= 0)){
				vx= 0 - vx;
			}
			//this establishes the vertical bounces
			if(ball.getY() <= 0) {
				vy= 0 - vy;
			}	

			//this checks for collisions and changes what happens depending on the colliding object
			getCollidingObject();
			makeCollisions();

			//this establishes how you lose, including fixing the below-paddle bug
			if(ball.getY() +2.0*BALL_RADIUS >= getCanvasHeight() || (ball.getY() >= CANVAS_HEIGHT - PADDLE_Y_OFFSET))
			{
				turns--;
				GLabel Turns = new GLabel("Turns left:" + turns, getCanvasWidth()/2.0-PADDLE_WIDTH/2.0, getCanvasHeight()/2+PADDLE_WIDTH/2.0);
				add(Turns);
				if(turns == 0 ) {
					remove(ball);
					remove(Turns);
					GLabel gameOver = new GLabel("Game Over!", getCanvasWidth() / 2.0 - PADDLE_WIDTH / 2.0, getCanvasHeight() / 2.0);
					add(gameOver);
					break;
				}
				//this returns the ball to the center and waits for a click to begin again
				ball.setLocation(getCanvasWidth() / 2.0- BALL_RADIUS, getCanvasHeight() / 2.0-BALL_RADIUS);
				waitForClick();
				remove(Turns);
			}
			//this establishes a path to victory! 
			if(b==0) {
				GLabel gameWon = new GLabel("You Won!", getCanvasWidth()/2.0 - PADDLE_WIDTH/2, getCanvasHeight() / 2.0);
				add(gameWon);				
				break;
			}
		}
	}

	//this checks the four corners of the GRect the ball is in for collisions
	private GObject getCollidingObject() {
		collider = getElementAt(ball.getX(), ball.getY());
		if(collider == null) {	
			collider = getElementAt(ball.getX() +2.0 * BALL_RADIUS, ball.getY());}
		if(collider == null) {	
			collider = getElementAt(ball.getX(), ball.getY()+2.0 * BALL_RADIUS);}
		if(collider == null) {	
			collider = getElementAt(ball.getX() +2.0 * BALL_RADIUS, ball.getY() + 2.0 * BALL_RADIUS);}
		return collider; 

	}
	//this changes what happens based on what the collider is
	private void makeCollisions() {
		if(collider == paddle) {
			vy= 0 - vy;
		}
		if(collider != null && collider.getWidth() == BRICK_WIDTH) {
			remove(collider);
			vy= 0 - vy;
			b= b - 1;
		}
	}		
	
	private void addExtensions() {
		//adds audio 
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
		//speed up game
		if(b==93) {
			VELOCITY_X_MIN = 2; 
			VELOCITY_X_MAX= 5; 
		}
		if(b==50) {
			VELOCITY_X_MIN = 4; 
			VELOCITY_X_MAX= 8; 
		}
	}
}

