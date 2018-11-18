/*
 * File: Breakout.java
 * -------------------
 * Name: Akim Richards
 * Section Leader: Rhea K.
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

	// Sets the x and y velocities of the ball
	double xVelocity = RandomGenerator.getInstance().nextDouble(1.0,3.0);
	double yVelocity = 3.0;

	// Sets height of score label
	private GLabel label;
	private double labelHeight = 30;

	GOval Ball=null;
	GRect Paddle=null;
	int turns = NTURNS;
	int score;

	public void run() {
		setUpGame();
		playGame();

	}

	// sets up game interface, including creating the bricks, adding the paddle, mouse listeners, and score label.
	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		label=scoreLabel();
		add(label);
		addMouseListeners();
		buildBricks();
		addPaddle();

	}

	// allows the game to be played in the specified number of turns, while bricks are still available.
	private void playGame() {
		while(turns>0&&score<100) {
			addBall();
			waitForClick();
			while(Ball!=null) {
				launchBall();
			}
			if(turns==0) {
				GLabel gameover = new GLabel("GAME OVER", getWidth()/2, getHeight()/2);
				gameover.setColor(Color.red);
				add(gameover);
			}
			if(score==100) {
				GLabel youwin = new GLabel("You Win!", getWidth()/2, getHeight()/2);
				add(youwin);
			}
		}
	}

	// builds a pre-specified number of rows of bricks
	private void buildBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int columns = 0; columns<NBRICK_COLUMNS;columns++) {
				GRect Brick = new GRect((((BRICK_WIDTH+BRICK_SEP) * columns) + BRICK_SEP), ((((BRICK_HEIGHT+BRICK_SEP)*row)+BRICK_SEP) + labelHeight), BRICK_WIDTH, BRICK_HEIGHT);
				if(row%10<=1) {
					Brick.setColor(Color.RED);
					Brick.setFilled(true);
					Brick.setFillColor(Color.RED);
				}
				if(row%10==2 || row ==3) {
					Brick.setColor(Color.ORANGE);
					Brick.setFilled(true);
					Brick.setFillColor(Color.ORANGE);
				}
				if(row%10==4 || row==5) {
					Brick.setColor(Color.YELLOW);
					Brick.setFilled(true);
					Brick.setFillColor(Color.YELLOW);
				}
				if(row%10==6 || row==7) {
					Brick.setColor(Color.GREEN);
					Brick.setFilled(true);
					Brick.setFillColor(Color.GREEN);
				}
				if(row%10==8||row==9) {
					Brick.setColor(Color.CYAN);
					Brick.setFilled(true);
					Brick.setFillColor(Color.CYAN);
				}
				add(Brick);
			}
		}
	}

	// adds paddle to the center of the bottom of the screen
	private void addPaddle() {
		double x = getWidth()/2 - PADDLE_WIDTH/2;
		double y = getHeight()-PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		Paddle = new GRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		Paddle.setFilled(true);
		add(Paddle);
	}

	// adds the ball to its starting position
	private void addBall() {
		double x = getWidth()/2-BALL_RADIUS;
		double y = getHeight()/2-BALL_RADIUS;
		Ball=new GOval(x,y,2*BALL_RADIUS,2*BALL_RADIUS);
		Ball.setFilled(true);
		add(Ball);
	}

	// commands the paddle to move with the mouse
	public void mouseMoved(MouseEvent event) {
		double mouseControl = event.getX();
		double YLocation = Paddle.getY();
		if(getCanvasWidth() - mouseControl < PADDLE_WIDTH) {
			Paddle.setLocation(getCanvasWidth()-PADDLE_WIDTH,YLocation);
		}
		else {
			Paddle.setLocation(mouseControl,YLocation);
		}
	}

	// commands the ball to begin motion with the specified velocities; also guarantees bricks disappear when they are hit, and that the score updates;
	// essentially one comprehensive turn
	public void launchBall() {
		Ball.move(xVelocity,  yVelocity);
		if((Ball.getX()-xVelocity <=0 && xVelocity<0)||(Ball.getX()+xVelocity>=(getWidth()-BALL_RADIUS*2) && xVelocity>0)){
			xVelocity=-xVelocity;
		}
		if((Ball.getY()-yVelocity<=0&&yVelocity<0)) {
			yVelocity=-yVelocity;
		}
		paddleBounce();
		GObject objHit = collisions();
		if(objHit!=null && objHit!=Paddle && objHit != label) {
			score++;
			updateLabel();
			remove(objHit);
			yVelocity=-yVelocity;
		}
		if(score==100) {
			remove(Ball);
			Ball=null;
			return;
		}
		missedBall();
		pause(DELAY);
	}

	// allows the ball to bounce off of the paddle instead of go through it
	private void paddleBounce() {
		if(Ball!=null) {
			GObject objHit = getElementAt(Ball.getX()+BALL_RADIUS, Ball.getY() + (BALL_RADIUS*2));
			if(objHit==Paddle) {
				yVelocity=-yVelocity;
			}
		}
	}

	// resets ball and updates label when ball is missed
	private void missedBall() {
		if(Ball.getY()+(BALL_RADIUS*2)>=getHeight()) {
			remove(Ball);
			Ball=null;
			turns--;
			updateLabel();
		}
	}

	// checks for collisions at the ball's extreme/critical points
	private GObject collisions() {
		if(getElementAt(Ball.getX(),Ball.getY())!=null) {
			return getElementAt(Ball.getX(),Ball.getY());
		}
		else if(getElementAt((Ball.getX()+2*BALL_RADIUS),Ball.getY()) != null) {
			return getElementAt((Ball.getX() +2*BALL_RADIUS),Ball.getY());
		}
		else if(getElementAt(Ball.getX(),(Ball.getY() + 2*BALL_RADIUS))!=null) {
			return getElementAt(Ball.getX(),(Ball.getY() +2*BALL_RADIUS));
		}
		else if (getElementAt((Ball.getX() + 2*BALL_RADIUS),(Ball.getY() + 2*BALL_RADIUS)) !=null) {
			return getElementAt((Ball.getX()+2*BALL_RADIUS),(Ball.getY()+2*BALL_RADIUS));
		}
		else {
			return null;
		}
	}

	// creates score label
	private GLabel scoreLabel() {
		GLabel scoreLabel = new GLabel (("Score: " + score + ", " + "Turns: " + turns),0,labelHeight);
		return scoreLabel;
	}

	// updates score label
	private void updateLabel() {
		label.setText("Score: " + score + ", " + "Turns: " + turns);
	}
}

