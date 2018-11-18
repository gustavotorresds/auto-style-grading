/*
 * File: BreakoutWithExtensions.java
 * -------------------
 * Name: Cosima Justus
 * Section Leader: Niki
 * 
 * The BreakoutWithExtensions subclass allows the user to play a game of breakout. The user has
 * a specified number of lives. The parameters that set up the game may be changed.
 * I added some extensions: a life counter, start message, and sound effects.
 * 
 * 
 * make sure ball cant hit life counter
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutWithExtensions extends GraphicsProgram {

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
	
	
	
	//instance variables
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private int turn;
	private GLabel lifecounter, startmessage;
	
	public void run() {
		// Set the window's title bar text
		setTitle("Breakout Extension");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		AudioClip sadTimes = MediaTools.loadAudioClip("sadtimes.au");
		addMouseListeners();
		setUpTheGame();
		int bricksleft = getElementCount()-3;
		//play game with NTURNS lives
		for(turn = 0; turn < NTURNS && bricksleft != 0;turn++) {
			if(bricksleft != 0) {
				addLivesCounter();
				makeBall();
				waitForClick();
				remove(startmessage);
				startTheGame();
				bricksleft = getElementCount()-3;
				remove(lifecounter);
			}
			if (bricksleft == 0) {
				remove(ball);
				addWinMessage();
			}
			else if (turn == NTURNS - 1) {
				sadTimes.play();
				addLoseMessage();
				
			}
		}
	}
	

	private void addLoseMessage() {
		GLabel losemessage = new GLabel("YOU LOST. :-(");
		losemessage.setFont("Courier-20");
		add(losemessage, getWidth()/2.0 - losemessage.getWidth()/2.0, getHeight()/2.0 + losemessage.getAscent()/2.0);
	}

	private void addWinMessage() {
		GLabel winmessage = new GLabel("CONGRATULATIONS! YOU WIN!");
		winmessage.setFont("Courier-20");
		add(winmessage, getWidth()/2.0 - winmessage.getWidth()/2.0, getHeight()/4.0 + winmessage.getAscent()/2.0);
	}

	private void addLivesCounter() {
		lifecounter = new GLabel("Lives left: " + (NTURNS-turn));
		lifecounter.setFont("Courier-15");
		add(lifecounter, getWidth()/2-lifecounter.getWidth()/2.0, getHeight()-2);
	}

	private void startTheGame() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;	
		if (rgen.nextBoolean(0.5)) vx = -vx;
		//counts bricks left
		int bricksleft = getElementCount()-3;
		
		while (bricksleft!=0) {	
			if(hitsLeftWall(ball) || hitsRightWall(ball)) {
				vx = -vx;
			}
			if(hitsTopWall(ball)) {
				vy = -vy;
			}
			if(hitsBottomWall(ball)) {
				remove(ball);	
			}
			
			//coordinates at ball's corners
			double leftx = ball.getX();
			double rightx = ball.getX()+2*BALL_RADIUS;
			double uppery = ball.getY();
			double lowery = ball.getY() + 2*BALL_RADIUS;
			//ensures area in which ball may not remove objects (paddle)
			double dontremovehere = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - 2*BALL_RADIUS; 
			//get object at all four corners of ball
			GObject obj1 = getElementAt(leftx, uppery);
			GObject obj2 = getElementAt(rightx, uppery);
			GObject obj3 = getElementAt(rightx, lowery);
			GObject obj4 = getElementAt(leftx, lowery);
			
			if(obj1 != null && ball.getY() < dontremovehere) {
				remove(obj1);
				bounceClip.play();
				//if ball hits two bricks simultaneously on way up
				if(obj2 != null && ball.getY() < dontremovehere) {
					remove(obj2);
					bounceClip.play();
				}
				//if ball hits two bricks simultaneously sideward
				if(obj4 != null && ball.getY() < dontremovehere) {
					remove(obj4);
					bounceClip.play();
					vx = -vx;
				}
				else vy = -vy;
			}
			//need else, because if not, when two bricks are hit simultaneously, the ball would continue its path
			else if(obj2 != null && ball.getY() < dontremovehere) {
				remove(obj2);
				bounceClip.play();
				//if ball hits two bricks simultaneously sideward
				if(obj3 != null && ball.getY() < dontremovehere) {
					remove(obj3);
					bounceClip.play();
					vx = -vx;
				}
				else vy = -vy;
			}
			
			//when ball hits brick on way down 
			else if (obj3 != null && ball.getY() < dontremovehere) {
				remove(obj3);
				bounceClip.play();
				//if ball hits two bricks simultaneously downward
				if(obj4 != null && ball.getY() < dontremovehere) {
					remove(obj4);
					bounceClip.play();
				}
				vy = -vy;
			}	
			//ball hits brick on way down
			else if (obj4 != null && ball.getY() < dontremovehere) {
				remove(obj4);
				bounceClip.play();
				vy = -vy;
			}	
			
			//////////////HITS PADDLE//////////////	
			else if (obj3!=null && obj3!=lifecounter|| obj4!=null && obj4!=lifecounter) {
				//if ball hits side of paddle. player is too late if only obj1 or obj2 != null here
				if (ball.getY()+BALL_RADIUS > getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT) {
					vx=-vx;
					while(obj3!=null || obj3!=null) {
						obj3=null;
						obj4=null;
					}
				}
				//absolute value to stop very buggy sticky paddle
				vy=-Math.abs(vy);
			}

			//if ball exits bottom of screen
			if (hitsBottomWall(ball) ) {
				remove(ball);
				break;
			}
			ball.move(vx, vy);
			pause (DELAY);
			bricksleft = getElementCount()-3;
		}
	}

	
	//ball hits left wall
	private boolean hitsLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	//ball hits right wall
	private boolean hitsRightWall(GOval ball) {
		return ball.getX() >= getWidth() - 2*BALL_RADIUS;
	}
	
	//ball hits top wall
	private boolean hitsTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	//ball hits bottom wall
	private boolean hitsBottomWall(GOval ball) {
		return ball.getY() >= getHeight();
	}

	//paddle movement, according to mouse movement
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		//at left edge
		if (e.getX() <= PADDLE_WIDTH/2) {
			x = 0; 
		}
		//at right edge
		if (e.getX() >= getWidth() - PADDLE_WIDTH/2) {
			x = getWidth() - PADDLE_WIDTH;
		}
		add(paddle, x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}
	
	private void setUpTheGame() {
		makeBrickRows();
		makePaddle();
		addStartMessage();
	}
	
	private void addStartMessage() {
		startmessage = new GLabel("Welcome to Breakout. You have 3 lives. Click to start.");
		startmessage.setFont("Courier-15");
		add(startmessage, getWidth()/2.0 - startmessage.getWidth()/2.0, startmessage.getAscent()+2.0);
		
	}


	//adds paddle to screen
	private void makePaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double x = getWidth()/2.0 - PADDLE_WIDTH/2.0;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, x, y);
	}
	
	//adds ball to screen
	private void makeBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}
	
	//adds bricks to screen
	private void makeBrickRows() {
		for (int j=0; j < NBRICK_ROWS; j++) {
			double y = BRICK_Y_OFFSET + j*BRICK_HEIGHT + BRICK_SEP*j;
			for (int i=0; i < NBRICK_COLUMNS; i++) {
				// determine x value
				double x = getWidth()/2.0 + i*(BRICK_WIDTH+BRICK_SEP) - (NBRICK_COLUMNS*(BRICK_WIDTH + BRICK_SEP)-BRICK_SEP)/2.0;
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				//coloring in rainbow like sequence
				if (j%10==0 || j%10 == 1) brick.setColor(Color.RED); 
				else if (j%10 == 2 || j%10 == 3) brick.setColor(Color.ORANGE);
				else if (j%10 == 4 || j%10 == 5) brick.setColor(Color.YELLOW);
				else if (j%10 == 6 || j%10 == 7) brick.setColor(Color.GREEN);
				else brick.setColor(Color.CYAN);
				add(brick, x, y);
			}
		}
	}
	
	//for random ball start
	private RandomGenerator rgen = RandomGenerator.getInstance();
}
