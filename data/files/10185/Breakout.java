/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
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
	private GRect paddle; 

	public static final double BALL_RADIUS = 10;
	public static final double VELOCITY_Y = 8.0;
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;
	private GOval ball; 
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int lives = 3 ;
	private GObject getCollidingObject;
	private GObject collider; 
	private GRect brick; 
	public static final double DELAY = 1000.0 / 60.0;
	AudioClip bounceClip= MediaTools.loadAudioClip("bounce.au");
	public static final int NTURNS = 3;
	private int brickNumber = 100;

	// this is the run method that only begins if the player hasent already had 3 turns in the session.
	// it also displays different labels depending on the number of bricks left.
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		for(int i=0; i < NTURNS; i++) {
			setUpGame();
			playTheGame();
			if(brickNumber == 0) {
				ball.setVisible(false);
				printWinner();
				break;
			}

			else {
				removeAll();

			}
		}
		if(brickNumber > 0) {
			removeAll();
			printLost();
		}
		printGameOver();
	}


	private void playTheGame() {
		drawBall();
		startPlaying();
	}

	// this is to randomly initialize the start of the ball in the X direction.
	private void startPlaying() {
		double vx = rgen.nextDouble(1.0,3.0);
		if (rgen.nextBoolean(0.5)) vx=-vx;
		double vy = VELOCITY_Y;

		// the game only starts when the player has clicked the mouse and states what happens 
		// when the ball hits the paddle (bounce back with a sound) or one of the walls or 
		// when it hits a collider that it removes a brick and keeps count of how manu brick
		// there still are in the game 

		waitForClick();
		while(true) {
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				bounceClip.play();
				// this line is to make sure that the ball doesnt stick to the paddle as it stays above or below the paddle and doesnt get stuck inbetween the two.
				if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - (BALL_RADIUS * 2) && ball.getY() <  getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - (BALL_RADIUS * 2)+7){
					vy = - vy;		
				}
			}
			else if (collider != null) {
				remove(collider); 
				brickNumber = brickNumber-1;
				vy=-vy;

			}

			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			if(hitBottomWall(ball)) {
				remove(ball);
				while(lives!= 0) {
					lives = lives-1;
					pause(DELAY);
					removeAll();
					ball = new GOval ( getWidth()/2 , getHeight()/2, BALL_RADIUS*2,BALL_RADIUS*2);
					add (ball);	
				}
				if ( lives ==0) {
					printGameOver();
					break;
				}
				if(brickNumber == 0) {
					ball.setVisible(false);
					printWinner();
					break;
				} 
			}
			ball.move(vx, vy);
			pause(DELAY);
		}

	}
	//this is the message displayed to let the player know how many lives they have left and 
	// that they lost a life.
	private void printLost() {
		GLabel printLives = new GLabel ("You LOST!, you have "+lives+ "lives left!", getWidth()/2, getHeight()/2-20);
		printLives.move(-printLives.getWidth()/2, -printLives.getHeight());
		printLives.setColor(Color.RED);
		add (printLives);	 
	}  

	// this method determines what happens when the ball hits an object and checks each of
	// 4 corners of the ball to see if there is something or a null
	private GObject getCollidingObject() {

		if((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if (getElementAt( (ball.getX() + BALL_RADIUS*2), ball.getY()) != null ){
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
		}
		else if(getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2)) != null ){
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
		}
		else if(getElementAt((ball.getX() + BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2)) != null ){
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
		}

		else{
			return null;
		}
	}

	// when the player loses this message is displayed
	private void printGameOver() {
		if (brickNumber >0) {
			GLabel gameOver = new GLabel ("You died, Game Over! :(", getWidth()/2, getHeight()/2);
			gameOver.move(-gameOver.getWidth()/2, -gameOver.getHeight());
			gameOver.setColor(Color.ORANGE);
			add (gameOver);	 
		}
	}
	// when the player wins this winner message is displayed
	private void printWinner() {
		GLabel Winner = new GLabel ("Winner!!:)", getWidth()/2, getHeight()/2);
		Winner.move(-Winner.getWidth()/2, -Winner.getHeight());
		Winner.setColor(Color.PINK);
		add (Winner);
	}

	// these set of boolean commands determines where the walls of the game are. 
	private boolean hitTopWall(GOval ball2) {
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball2) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	// this draws the ball and places it in the center of the screen at the start of the game
	private void drawBall() {
		ball = new GOval ( getWidth()/2 , getHeight()/2, BALL_RADIUS*2,BALL_RADIUS*2);
		add (ball);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);

	}

	// this is the method that sets up the game including the bricks and paddle. 
	private void setUpGame() {
		drawBrick(getWidth()/2, BRICK_Y_OFFSET);
		DrawPaddle();
	}

	// this draws the paddle at the bottom of the screen and adds a mouselistener so that the user 
	// can control it with the mouse
	private void DrawPaddle() {
		paddle = new GRect( getWidth()/2-PADDLE_WIDTH/2,getHeight()- PADDLE_Y_OFFSET-PADDLE_HEIGHT, PADDLE_WIDTH , PADDLE_HEIGHT );
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
		addMouseListeners();
		pause(DELAY);

	}
	public void mouseMoved(MouseEvent e) {
		paddle.setLocation(e.getX() - PADDLE_WIDTH, getHeight()- PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		if (e.getX()< PADDLE_WIDTH) {
			paddle.setLocation(PADDLE_WIDTH/2-PADDLE_WIDTH/2, getHeight()- PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		}
		if(e.getX()> getWidth()- PADDLE_WIDTH/2) {
			paddle.setLocation(getWidth()- PADDLE_WIDTH, getHeight()- PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		}


	}

	// this draws and colors all the brick in the game at the correct position
	private void drawBrick(double cx, double cy) {
		for ( int row = 0; row < NBRICK_ROWS; row++) {
			for ( int column = 0; column< NBRICK_ROWS; column++) {

				double xBrick = cx - (NBRICK_ROWS*BRICK_WIDTH)/2 - ((NBRICK_ROWS-1)*BRICK_SEP)/2 + column*BRICK_WIDTH + column*BRICK_SEP;
				double yBrick = cy +row*BRICK_HEIGHT +row*BRICK_SEP;

				GRect brick = new GRect( xBrick , yBrick , BRICK_WIDTH , BRICK_HEIGHT );
				brick.setFilled(true);

				add(brick);

				if ( row == 0 || row ==1 ) {
					brick.setFillColor(Color.RED);
				}if ( row == 2 || row ==3 ) {
					brick.setFillColor(Color.ORANGE);
				}if ( row == 4 || row ==5 ) {
					brick.setFillColor(Color.YELLOW);
				}if ( row == 6 || row ==7 ) {
					brick.setFillColor(Color.GREEN);
				}if ( row == 8 || row ==9 ) {
					brick.setFillColor(Color.BLUE);
				}

			}
		}

	}
}
