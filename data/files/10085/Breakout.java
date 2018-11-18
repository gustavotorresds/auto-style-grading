/*
 /*
 * Name: Michelle Ly
 * Section Leader: Drew Bassilakis 
 * File: Breakout.java
 * -------------------
 * Creates the iconic game "Break Out" 
 * by creating a row of bricks at the top of the screen
 * a ball that moves around the game and removes the bricks
 * and a paddle to hit the ball back up.
 * Game has a total of round three.
 * If player wins any round between 1-3 they win, but if they don't they lose.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// Used when setting up the initial size of the game.
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
	
	//instance variables used in breakout 
	private GRect paddle = null;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private GObject collider;
	private int counter;
	private int round;

	//Sets title and size of the game.
	//Then makes the world of the game
	//and finally runs the game.
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		makeWorld();
		playGame();
	}

	//Creates the game: the bricks, paddle, ball,
	//and allows the paddle to move.
	private void makeWorld() {
		clear();
		makeBricks();
		makePaddle();
		makeBall();
		addMouseListeners();
	}
	
	//Allows game to run by having the ball move.
	//If the ball hits a brick it deletes it.
	//Goes on for three rounds, and if the player loses all three they lose.
	//If not they win.
	private void playGame() {
		for(round = 1; round < NTURNS; round++) {
			if(!moveBallAndGetCollider()) {
				makeWorld();
			}else {
				writeYouWin();
				break;
			}
		}
		if(round == NTURNS && !moveBallAndGetCollider()) {
			writeYouLose(); 
		} else {
			writeYouWin();
		}
	}

	//makes the bricks in the game and sets the different colors
	private void makeBricks() { 
		double startXCoord = (getWidth() / 2) - (((BRICK_WIDTH + BRICK_SEP) * (NBRICK_COLUMNS - 1) + BRICK_WIDTH) / 2); 
		double startYCoord = BRICK_Y_OFFSET;
		for (int col = 0; col < NBRICK_COLUMNS; col++) {
			double xCoord = startXCoord + ((BRICK_SEP + BRICK_WIDTH) * col);
			for (int row = 0; row < NBRICK_ROWS; row++) {
				double yCoord = startYCoord + ((BRICK_SEP + BRICK_HEIGHT) * row);
				GRect brick = new GRect(xCoord, yCoord, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(row <= 1) {
					brick.setColor(Color.RED);
				}
				if(row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
				}
				if(row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				}
				if(row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				}
				if(row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
				}
				add(brick);				
			}
		}
	}
	
	//makes the paddle used in the game
	private void makePaddle() {
		double xCoord = (getWidth() / 2) - (PADDLE_WIDTH / 2);
		double yCoord = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(xCoord, yCoord, PADDLE_WIDTH, PADDLE_HEIGHT);	
		paddle.setFilled(true);
		add(paddle);
	}
	
	//uses mouse event to track when the mouse moves
	//in order to move the paddle in the game
	public void mouseMoved(MouseEvent e) {
		double xCoord = e.getX();
		double yCoord = getHeight() - PADDLE_Y_OFFSET;
		if ((e.getX() - (PADDLE_WIDTH / 2)) > 0 && (e.getX()) < (getWidth() - PADDLE_WIDTH/2)) {  
			remove(paddle);
			add(paddle, e.getX() - (PADDLE_WIDTH / 2), yCoord);
			paddle.setFilled(true);
		}
	}
	
	//makes the ball in the game
	private void makeBall() {
		double xCoord = (getWidth() / 2) - BALL_RADIUS;
		double yCoord = (getHeight() / 2) - BALL_RADIUS;
		ball = new GOval(xCoord, yCoord, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}	

	//tells when the ball is going to top of canvas
	private boolean goingToTop() {
		return ball.getY() < 0;
	}
	
	//tells when the ball is going to bottom of canvas
	private boolean goingToBottom() {
		return ball.getY() > getHeight();
	}
	
	//tells when the ball is going to left side of canvas
	private boolean goingToLeftSide() {
		return ball.getX() < 0;
	}
	
	//tells when the ball is going to right side of canvas
	private boolean goingToRightSide() {
		return ball.getX() > getWidth() - (BALL_RADIUS * 2);
	}
	
	//tracks where the ball is at using the four points using getElementAt() 
	//and see whether if either of the four points touches something
	//and to then identify it as a collider
	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if(getElementAt(ball.getX(), ball.getY()) != null) {
		}
		else if(getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS * 2)) != null){
			collider = getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS * 2));
		}
		else if(getElementAt((ball.getX() + BALL_RADIUS * 2), ball.getY()) != null){
			collider = getElementAt((ball.getX() + BALL_RADIUS * 2), ball.getY());
		}
		else if(getElementAt((ball.getX() + BALL_RADIUS * 2), (ball.getY() + BALL_RADIUS * 2)) != null){
			collider = getElementAt((ball.getX() + BALL_RADIUS * 2), (ball.getY() + BALL_RADIUS * 2));
		}
		return collider;
	}
	
	//Moves the ball in a certain direction depending on what wall it hits,
	//and ends the round if the ball goes past the bottom of the canvas.
	//Also tracks what the ball collides in, 
	//and removes the collider if it isn't the paddle.
	private boolean moveBallAndGetCollider() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		counter = 0;
		
		while(counter < NBRICK_COLUMNS * NBRICK_ROWS) {
			ball.move(vx, vy);
			
			pause(DELAY);
			if(goingToTop()) { 
				vy = -vy;
			}
			if(goingToBottom()) { 
				return(false);
			}
			if(goingToLeftSide() || goingToRightSide()) {
				vx = -vx;
			}
			
			collider = getCollidingObject();
			if(collider != null) {
				if(collider == paddle) {
					if (vy > 0) {
						vy = -vy;
					}
				}
				if(collider != paddle) {
					remove(collider);
					counter++;
					vy = -vy;
				}
			}
		}
		return(true);
	}
		
	//Prints the "YOU WIN!" statement after player wins.
	private GLabel writeYouWin() {
		clear();
		GLabel won = new GLabel("YOU WIN!");
		won.setFont("Arial-30");
		double yCoord = (getHeight() / 2) + (won.getAscent() / 2);
		double xCoord = (getWidth() / 2) - (won.getWidth() / 2);
		add(won, xCoord, yCoord);
		return(won);
	}
	
	//Prints the "YOU LOSE!" statement after player loses.
	private GLabel writeYouLose() {
		clear();
		GLabel lose = new GLabel("YOU LOSE!");
		lose.setFont("Arial-30");
		double yCoord = (getHeight() / 2) + (lose.getAscent() / 2);
		double xCoord = (getWidth() / 2) - (lose.getWidth() / 2);
		add(lose, xCoord, yCoord);
		return(lose);
	}
}

