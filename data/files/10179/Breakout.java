/*
 * File: Breakout.java
 * -------------------
 * Name: Niki Flamen
 * Section Leader: Drew B.
 * 
 * This file creates the game Breakout.
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

	public static final int NBRICK_COLUMNS = 10;

	public static final int NBRICK_ROWS = 10;

	public static final double BRICK_SEP = 4;

	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	public static final double BRICK_HEIGHT = 8;

	public static final double BRICK_Y_OFFSET = 70;

	public static final double PADDLE_WIDTH = 60; 
	public static final double PADDLE_HEIGHT = 10;

	public static final double PADDLE_Y_OFFSET = 30;

	public static final double BALL_RADIUS = 10;

	public static final double VELOCITY_Y = 9; 

	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	public static final double DELAY = 1000.0 / 60.0;

	public static final int NTURNS = 3;

	private GRect paddle;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy;
	private int brickcount; //counts # of bricks hit
	private int ballcount; //counts # of turns
	

	/*
	 * This program creates the game of Breakout by using different methods and instance variables.  It uses a wait for click to start the program.
	 */
	
	//this method is our run method which has canvasSetup(creates general elements that we use in game) and gameplay(which allows us to play the game).
	public void run() {
		canvasSetup();
		gameplay();
	}
	
	//this method simply creates the bricks and paddle
	private void canvasSetup() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createBricks();
		createPaddle();
		addMouseListeners();
	}
	
	//this game play method simply calls three methods which creates the game after it has been set-up in the previous method
	private void gameplay() {
		getBallVelocity();
		waitForClick();
		animationLoop();			
	}

	//this loop creates the animation of the game
	private void animationLoop() {
		while (true) {
			if (ball.getY() > getHeight()){  //ball goes to bottom, remove ball
				remove(ball);
				ballcount ++;
				if (ballcount == NTURNS) {
					loseCondition();
					break;
				}
				if (ballcount < NTURNS) { //generates the new ball after we have lost the turn
					getBallVelocity();
					waitForClick();
				}	
			}
			collisions();
			ball.move(vx, vy);
			pause(DELAY);
			//this GObject checks to see what the ball is colliding into
			GObject collided = getCollidingObject();
			if (collided == paddle) {
				vy = -1*Math.abs(vy); //solves the sticky paddle problem
			} else if (collided != null) {
				vy = -vy;
				remove(collided);
				brickcount ++;
				if (brickcount == NBRICK_ROWS*NBRICK_COLUMNS) {
					winCondition();
					break;
				}
			}
			if (brickcount == NBRICK_ROWS*NBRICK_COLUMNS) {
				break;
			}
		}
	}
	
	//this method prints a label that states when the player has won!
	private void winCondition() {
		GLabel text = new GLabel("You won!");
		double labelx= getWidth()/2 - text.getWidth()/2;
		double labely= getHeight()/2 - text.getHeight()/2;
		add(text, labelx, labely);
		remove(ball);
	}
	
	//this method prints a label that states that the player has lost!
	private void loseCondition() {
		GLabel text = new GLabel("You lose! Game is over!!");
		double labelx= getWidth()/2 - text.getWidth()/2;
		double labely= getHeight()/2 - text.getHeight()/2;
		add(text, labelx, labely);
	}
	
	//this method simply codes to say how the ball should change its velocity if it hits the side or top wall
	private void collisions() { 
		if (ball.getX() >= (getWidth()-ball.getWidth()) || ball.getX() <= 0) { //ball goes beyond left or right wall it hits and bounces back with opposite velocity
			vx = -vx;
		}
		if (ball.getY() <= 0) { // if ball hits top wall it hits and changes direction 
			vy = -vy;
		}
	}

	//this method generates the random velocity of the ball
	private void getBallVelocity() {  
		ball = makeBall();
		vx = rgen.nextDouble(1.0,4.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
	}

	//this method allows the paddle to move with mouse movements
	public void mouseMoved(MouseEvent event) { 
		int mouseX = event.getX();
		paddle.setLocation(mouseX,getHeight()-PADDLE_Y_OFFSET);
		if(paddle.getX() + PADDLE_WIDTH > getWidth()) { //this method does not let the paddle go off the edge of the screen
			paddle.setLocation(getWidth()-PADDLE_WIDTH,getHeight()-PADDLE_Y_OFFSET);
		}
	}

	//this method both creates and colors our bricks
	private void createBricks() { 
		for(int r=0; r<NBRICK_ROWS; r++) {
			int blocksInRow= NBRICK_COLUMNS;
			double startingRowX = getWidth()/2 -(((NBRICK_COLUMNS*BRICK_WIDTH)/2) +(((NBRICK_COLUMNS-1)*BRICK_SEP)/2));
			double startingRowY = BRICK_Y_OFFSET + r*BRICK_SEP + r*BRICK_HEIGHT;
			for (int c=0; c <blocksInRow; c++) {
				double x = startingRowX + c*BRICK_WIDTH + c*BRICK_SEP;
				GRect rect = new GRect(BRICK_WIDTH,BRICK_HEIGHT);
				rect.setFilled(true);
				add(rect, x, startingRowY);
				int q = r %10;
				if (q==0 || q==1) {
					rect.setColor(Color.RED);
				}
				if (q==2 || q==3) {
					rect.setColor(Color.ORANGE);
				}
				if (q==4 || q==5) {
					rect.setColor(Color.YELLOW);
				}
				if (q==6 || q==7) {
					rect.setColor(Color.GREEN);
				}
				if (q==8 || q==9) {
					rect.setColor(Color.CYAN);
				}
			}
		}
	}

	//this method will create a paddle and set it in the bottom with the appropriate y-offset
	private void createPaddle() { 
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, (getWidth() -(PADDLE_WIDTH))/2, getHeight()- PADDLE_Y_OFFSET);
	}

	//this method will create a ball and set it in the center
	private GOval makeBall() { 
		double diameter = BALL_RADIUS * 2;
		double ballx = (getWidth()/2) - (diameter/2);
		double bally = (getHeight()/2) - (diameter/2);
		GOval ball = new GOval(diameter,diameter);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, ballx, bally);
		return ball;
	}

	//this method uses if statements to determine what object the ball is colliding with at each of the four coordinates of the ball
	private GObject getCollidingObject() {  
		if (getElementAt(ball.getX(), ball.getY()) != null ) { 
			return getElementAt(ball.getX(), ball.getY());
		}
		if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null ) {
			return getElementAt(ball.getX()+ BALL_RADIUS * 2, ball.getY());
		}

		if (getElementAt(ball.getX(), ball.getY()+ BALL_RADIUS * 2) != null ) {
			return getElementAt(ball.getX(), ball.getY()+ BALL_RADIUS * 2);
		}

		if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()+ BALL_RADIUS * 2) != null ) {
			return getElementAt(ball.getX()+ BALL_RADIUS * 2, ball.getY()+ BALL_RADIUS * 2);
		} else {
			return null; //if there is no collision, do not do anything and only return null
		}
	} 
}
