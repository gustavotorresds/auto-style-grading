/*
 * Name: Michelle Ly
 * Section Leader: Drew Bassilakis 
 * File: BreakoutExtension.java
 * -------------------
 * Space themed version of breakout game. Has it so that
 * the game prints out the different rounds, scores, and lives.
 * Ball speeds up after a number of points is won.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;


public class BreakoutExtension extends GraphicsProgram {


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
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 2.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//instance variables used in breakoutextension
	private GRect paddle = null;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private GObject collider;
	private int counter;
	private int round = 1;
	private int r;
	private GLabel points = new GLabel("");
	private GLabel lives = new GLabel("");
	private GImage image;

	// Set the window's title bar text and then
	// Set the canvas size and color to black.
	//Method sets limit of three rounds in the game
	//Has it so that if the ball goes past the bottom of canvas
	//the round ends and a new round is created.
	//If they win in any of the three rounds, prints out "YOU WIN!",
	//if they lose after three rounds, prints out "YOU LOSE!"
	public void run() {
		setTitle("CS 106A Breakout Extension");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBackground(Color.BLACK);
		makeWorld();
		playGame();
	}
	
	//Creates the game, from counting the number of rounds,
	//making the world the game is in, and tracks the colliders,
	//lives, and the number of rounds.
	private  void makeWorld() {
		roundCountdown();
		clear();
		addImage();
		makebricks();
		makePaddle();
		makeBall();
		pointTracker();
		livesTracker();
		addMouseListeners();
	}
	
	//Plays the game for three rounds, must click to start game.
	private void playGame() {
		for(int r = 1; r < NTURNS; r++) {
			waitForClick();
			if(!moveBallAndGetCollider()) {
				makeWorld();
			}else {
				writeYouWin();
			}
		}
		waitForClick();
		if(!moveBallAndGetCollider()) {
			writeYouLose(); 
		} else {
			writeYouWin();
		}
	}
	
	
	//makes the bricks in the game and sets the different colors
	private void makebricks() { 
		double startXCoord = (getWidth() / 2) - (((BRICK_WIDTH + BRICK_SEP) * (NBRICK_COLUMNS - 1) + BRICK_WIDTH) / 2); 
		double startYCoord = BRICK_Y_OFFSET;
		for (int col = 0; col < NBRICK_COLUMNS; col++) {
			double xCoord = startXCoord + ((BRICK_SEP + BRICK_WIDTH) * col);
			for (int row = 0; row < NBRICK_ROWS; row++) {
				double yCoord = startYCoord + ((BRICK_SEP + BRICK_HEIGHT) * row);
				GRect brick = new GRect(xCoord, yCoord, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(row <= 1) {
					brick.setColor(Color.WHITE);
				}
				if(row == 2 || row == 3) {
					brick.setColor(Color.LIGHT_GRAY);
				}
				if(row == 4 || row == 5) {
					brick.setColor(Color.GRAY);
				}
				if(row == 6 || row == 7) {
					brick.setColor(Color.DARK_GRAY);
				}
				if(row == 8 || row == 9) {
					brick.setColor(Color.DARK_GRAY);
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
		paddle.setColor(Color.LIGHT_GRAY);
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
		ball.setColor(Color.WHITE);
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
	//Also tracks what the ball collides in and the points won, 
	//and removes the collider if it isn't the paddle while making a sound.
	//Makes ball move faster depending on how many bricks removed.
	private boolean moveBallAndGetCollider() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		counter = 0;
		
		while(counter < NBRICK_ROWS * NBRICK_COLUMNS) { 
			
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
				if(collider == lives && collider == points && collider == image) {	
					vy = vy;
				}
				if(collider != paddle && collider != lives && collider != points && collider != image) {
					AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
					remove(collider);
					bounceClip.play();
					counter++;
					vy = -vy;
					points.setLabel("Score: " + counter);
					if(counter == 15) {
						vy += 1;  
					}
					if(counter == 30) {
						vy += 1;  
					}
					if(counter == 60) {
						vy += 2;  
					}
				}
			}
		}
		return(true);
	}
	
	//Creates a label that states the round number.
	private GLabel writeRoundNum() {
		GLabel roundNum = new GLabel("ROUND " + round);
		roundNum.setFont("Courier New-30");
		roundNum.setColor(Color.WHITE);
		double yCoord = (getHeight() / 2) + (roundNum.getAscent() / 2);
		double xCoord = (getWidth() / 2) - (roundNum.getWidth() / 2);
		add(roundNum, xCoord, yCoord);
		return(roundNum);
	}
	
	//Prints the round number for every new round in the game.
	private void roundCountdown() {
		clear();
		writeRoundNum();
		pause(1000);
		round++;
		lives.setLabel("Lives: " + (3 - round));
	}

	//Prints the "YOU WIN!" statement after player wins.
	private GLabel writeYouWin() {
		clear();
		GLabel win = new GLabel("YOU WIN!");
		win.setFont("Courier New-30");
		win.setColor(Color.WHITE);
		double yCoord = (getHeight() / 2) + (win.getAscent() / 2);
		double xCoord = (getWidth() / 2) - (win.getWidth() / 2);
		add(win, xCoord, yCoord);
		return(win);
	}
	
	//Prints the "YOU LOSE!" statement after player loses.
	private GLabel writeYouLose() {
		clear();
		GLabel lose = new GLabel("YOU LOSE!");
		lose.setFont("Courier New-30");
		lose.setColor(Color.WHITE);
		double yCoord = (getHeight() / 2) + (lose.getAscent() / 2);
		double xCoord = (getWidth() / 2) - (lose.getWidth() / 2);
		add(lose, xCoord, yCoord);
		return(lose);
	}
	
	//Prints out the points label
	private GLabel pointTracker() {
		double xCoord = 25;
		double yCoord = getHeight() - 10;
		points.setFont("Courier New-20");
		points.setColor(Color.WHITE);
		points.setLabel("Score: " + 0);
		add(points, xCoord, yCoord);
		return points;
	}
	
	//Prints out the lives label
	private GLabel livesTracker() {
		double xCoord = 300;
		double yCoord = getHeight() - 10;
		lives.setFont("Courier New-20");
		lives.setColor(Color.WHITE);
		lives.setLabel("Lives: " + (4 - round));
		add(lives, xCoord, yCoord);
		return lives;
	}
	
	//Adds image of a night sky to the game.
	private void addImage() {
		image = new GImage("nightsky.jpg");
		image.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		add(image);
	}
}
