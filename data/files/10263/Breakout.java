/*
 * File: Breakout.java
 * -------------------
 * Name:Cam Burton
 * Section Leader: Akua McLeod
 * 
This program creates the classic game of Breakout for hours of fun
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
	public static final double DELAY = 1000.0/60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	GRect paddle =null;
	private RandomGenerator rgen= RandomGenerator.getInstance();
	double vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
	double vy = VELOCITY_Y;
	private GOval ball = null;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		int lives = 3;
		int bricks = NBRICK_ROWS*NBRICK_COLUMNS;
		GLabel youlose = new GLabel ("YOU LOSE :(");
		GLabel youwin= new GLabel ("YOU WIN :)");
		addBackground();
		setUpBricks();
		makePaddle();
		addPaddleToScreen();
		addMouseListeners();
		createBall();
		waitForClick();
		
		while (lives>0 || bricks==0 )	{
			if(hitLeftWall(ball)||hitRightWall(ball))	{
				vx=-vx;
			}
			if (hitTopWall(ball))	{
				vy=-vy;
			}
			if (ball.getY()> getHeight())	{
				lives --;

				remove (ball);
				createBall();
				if (lives == 0 )	{
					remove (ball);
					add(youlose, getWidth()/2-youlose.getWidth()/2, getHeight()/2-youlose.getAscent()/2);

				}
				waitForClick();

			}
			pause(DELAY);
			ball.move(vx, vy);
			if (ball.getX()==paddle.getX())	{
				vx=-vx;

			}

			GObject collider = getCollidingObject();
			if (collider == paddle) 	{
				if (vy>0)	{
					vy=-vy;
					AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
					bounceClip.play();
				}
			}

			else if (collider !=null)	{
				vy=-vy;
				remove(collider);
				bricks--;
				
				if (bricks == 0)	{
					add (youwin, getWidth()/2-youwin.getWidth()/2, getHeight()/2-youwin.getAscent()/2);
					remove(ball);
					remove(paddle);
					break;
				}
			}

		}
	}
	
	/*
	 * Method: getCollidingObject
	 * --------------------------
	 * This method defines collisions in the program by
	 * defining four points around the ball and returns any
	 * GObject that comes into cotact with any of those four points to the variable
	 * collider in the animation loop.
	 */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(),ball.getY())!=null)	{
			return getElementAt(ball.getX(),ball.getY());
		}
		else if (getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY())!=null)	{
			return getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY());
		}
		else if (getElementAt(ball.getX(),ball.getY()+BALL_RADIUS*2)!=null) {
			return (getElementAt(ball.getX(),ball.getY()+BALL_RADIUS*2));
		}
		else if (getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY()+BALL_RADIUS*2)!=null)	{
			return (getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY()+BALL_RADIUS*2));
		}
		return null;
	}
// These three lines of code make it so that collision with the walls are possible.
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <=0;
	}
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <=0;
	}
	/*
	 * Method: createBall
	 * ------------------
	 * This method creates the ball with size BALL_RADIUS*2
	 */
	private void createBall() {
		double size = BALL_RADIUS*2;
		ball = new GOval(size,size);
		ball.setFilled(true);
		add(ball,getWidth()/2,getHeight()/2);
	}
/*
 * Method: mouseMoved
 * ------------------
 * This method allows the paddle to be moved by the mouse.
 * The paddle cannot be moved off of the screen and its y-position on the
 * screen is fixed.
 */
	public void mouseMoved(MouseEvent e)	{
		double x=e.getX();
		double y=getHeight()-PADDLE_Y_OFFSET;
		paddle.setLocation(x,y);
		if(x>getWidth()-PADDLE_WIDTH)	{
			paddle.setLocation(x-PADDLE_WIDTH, y);
		}

	}
/*
 * Method: addPaddleToScreen
 * -------------------------
 * This method adds the paddle to the graphics window
 */
	private void addPaddleToScreen() {
		double x= getWidth()/2 - PADDLE_WIDTH/2;
		double y= getHeight()-PADDLE_Y_OFFSET;
		add(paddle,x,y);

	}
	/*
	 * Method: makePaddle
	 * ------------------
	 * This method creates the paddle.
	 */
	private void makePaddle() {
		paddle = new GRect (PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);

	}
	/*
	 * Method: setUpBricks
	 * -------------------
	 * This method creates NROWS by NCOLUMNS of bricks
	 * It makes the first two rows red, the second two orange, the third two
	 * yellow, the fourth two green and the last two blue
	 */
	private void setUpBricks() {
		for ( int rows=0; rows<NBRICK_ROWS; rows++)	{
			for (int columns=0; columns<NBRICK_COLUMNS; columns++)	{
				double newy = (BRICK_HEIGHT+BRICK_SEP);
				double newx = (BRICK_WIDTH +BRICK_SEP);
				GRect brick = new GRect (rows*newx+BRICK_SEP*2,(columns*newy)+BRICK_Y_OFFSET,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				if (columns==0)	{
					brick.setColor(Color.RED);
				}
				if(columns==1)	{
					brick.setColor(Color.RED);
				}
				if(columns==2)	{
					brick.setColor(Color.ORANGE);
				}
				if(columns==3) {
					brick.setColor(Color.ORANGE);
				}
				if(columns==4)	{
					brick.setColor(Color.YELLOW);

				}
				if(columns==5)	{
					brick.setColor(Color.YELLOW);
				}
				if(columns==6)	{
					brick.setColor(Color.GREEN);
				}
				if (columns==7) {
					brick.setColor(Color.GREEN);
				}
				if (columns==8)	{
					brick.setColor(Color.CYAN);
				}
				if(columns==9)	{
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}

		}
	}
}

