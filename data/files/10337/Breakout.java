
/*
 * File: Breakout.java

 * -------------------
 * Name:Christina Sakellaris
 * Section Leader: Cat Xu
 * 
 * This file implements the game of Breakout. The user has three ball shots to "break out",
 * which is when the ball breaks through to the ceiling of the console, and the player wins!
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
	public static final double BALL_DIAM = 20;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 3.0;
	public static final double VELOCITY_X_MAX = 6.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// The ball's minimum and maximum vertical velocity.
	public static final double VELOCITY_Y_MIN = 2;
	public static final double VELOCITY_Y_MAX = 5;



	GRect brick = null;	
	GRect paddle = null;
	private double vx;
	private double vy;
	GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int brickNumber = 100;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		brickSetUp();
		paddleSetUp();
		addMouseListeners();
		ballSetUp();

		//User has three shots of the ball to break out. User must click to initially shoot the ball. 
		for (int i=0; i<NTURNS; i++) {

			waitForClick();

			vx = rgen.nextDouble (VELOCITY_X_MIN,VELOCITY_X_MAX);
			if (rgen.nextBoolean (0.5)) vx=-vx;
			vy = rgen.nextDouble (VELOCITY_Y_MIN,VELOCITY_Y_MAX);


			//Animation loop to move ball, including how ball should bounce when coming into 
			//contact with different elements. 
			while (true) {


				if (hitLeftWall(ball)) {
					vx=-vx;
				}

				if (hitRightWall(ball)) {
					vx=-vx;
				}

				if (hitCeiling(ball)) {
					vy=-vy;
				}

				if (hitPaddle(ball)==paddle) {
					vy=-vy;
				}

				if (hitBrick(ball)!=null&&hitBrick(ball)!=paddle) {
					vy=-vy;
					remove(hitBrick(ball));
					brickNumber = brickNumber--;

				}
				//This if statement displays the winner label as soon as there are no bricks left, telling the user the game is done.  . 
				if (brickNumber==0) {
					remove(ball);
					remove (paddle);
					GLabel winner = new GLabel ("YOU'RE A WINNER!");
					double x = (getWidth()-winner.getWidth())/2;
					double y = getHeight()/2-winner.getAscent()/2;

					add (winner,x,y);

				} 

				ball.move(vx, vy);

				if (ball.getY()>getHeight()) {
					ballSetUp();
					break;
				}

				pause (DELAY);
			}
		}
		//This if statement displays the loss label if there are bricks remaining after all the turns are used. 
		if (brickNumber!=0) {
			remove(ball);
			remove (paddle);
			GLabel loss = new GLabel ("GAME OVER");
			double x = (getWidth()-loss.getWidth())/2;
			double y = getHeight()/2 - loss.getAscent()/2;
			add (loss,x,y);
		}


	}

	/* hitBrick determines if the ball has hit one of the bricks by checking to see if there is
	 * an element at each corner location of the ball, and returning that element. 
	 */
	private GObject hitBrick (GObject object2) {
		object2 = getElementAt(ball.getX(),ball.getY());
		if (object2 ==null) {
			object2= getElementAt(ball.getX()+BALL_DIAM,ball.getY());
		}
		if (object2 == null) {
			object2 =getElementAt(ball.getX(),ball.getY()+BALL_DIAM);
		}
		if (object2 ==null) {
			object2 =getElementAt(ball.getX()+BALL_DIAM,ball.getY()+BALL_DIAM);
		}
		return object2;
	}

	/* hitPaddle determines if the ball has made contact with the paddle by checking if the bottom plane
	 *  of the ball has made contact with the paddle. The method is then used later to let the ball
	 *  bounce off the paddle.  
	 */
	private GObject hitPaddle (GObject object) {
		object =getElementAt(ball.getX(),ball.getY()+BALL_DIAM);

		if (object ==null) {
			object =getElementAt(ball.getX()+BALL_DIAM,ball.getY()+BALL_DIAM);
		}
		return object;
	}

	/* hitCeiling returns a boolean of whether the y-value of the wall is greater than 0, 
	 * indicating that the ball has "hit" the ceiling, so that the ball can bounce off 
	 * of the ceiling to hit bricks. 
	 */
	private boolean hitCeiling(GOval ball) {
		return ball.getY()<=0;
	}

	/*starts ball movement after click
	 * (non-Javadoc)
	 * @see acm.program.Program#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked (MouseEvent e) {

	}

	/* hitLeftWall returns a boolean of whether the x-value of the ball is less than or equal to 0,
	 * indicating that the ball has "hit" the left side of the console, and must bounce. 
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/* hitRightWall returns a boolean of whether the x-value of the ball is greater than or equal to
	 * the width of the console-minus the ball diameter. This takes into account the origin of the
	 * ball being on the ball's left side. This method indicates when the ball should bounce off 
	 * of the right wall. 
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth()-BALL_DIAM;
	}

	/* This method sets up the ball in the middle of the screen, with no movement yet. 
	 */
	private GOval ballSetUp() {
		ball=new GOval (BALL_DIAM,BALL_DIAM);
		double ballX = (getWidth()-BALL_DIAM)/2;
		double ballY = (getHeight()-BALL_DIAM)/2;
		add(ball, ballX, ballY);
		ball.setFilled(true);
		return ball;
	}

	/*This method finds the x-value of the mouse, sets x within the bounds of the console 
	 * when the mouse leaves the console window, and then sets the x-location of the paddle
	 * equal to that of the mouse so that the user can play the game by moving the paddle, 
	 * and the paddle will not leave the console.  
	 * 
	 * (non-Javadoc)
	 * @see acm.program.Program#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent e) {
		double x = e.getX();
		if (x>getWidth()-PADDLE_WIDTH) {
			x=getWidth()-PADDLE_WIDTH;
		}
		if (x<0) {
			x=0;
		}
		paddle.setLocation (x, getHeight()-PADDLE_Y_OFFSET);
	}

	/* brickSetUp sets up 10 rows with 10 bricks per row, with the designated space in between each brick.
	 * Two for loops are used to set up bricks. Bricks colors are set using if statements with 
	 * the variable i, which corresponds to the row number down, so that the bricks have 
	 * the appropriate color pattern. 
	 */
	private void brickSetUp() {
		for (int i=0; i<10 ; i++) { 

			for (int n=0; n<NBRICK_ROWS;n++) {
				brick = new GRect (BRICK_WIDTH,BRICK_HEIGHT);
				double x = n*(BRICK_WIDTH+BRICK_SEP)+(getWidth()-(NBRICK_ROWS*(BRICK_WIDTH+BRICK_SEP)-BRICK_SEP))/2;
				double y = BRICK_Y_OFFSET+(BRICK_SEP+BRICK_HEIGHT)*i;
				add(brick, x,y);
				brick.setFilled(true);
				if (i<=1) {
					brick.setColor(Color.RED);
				}
				if ((i>1)&&(i<=3)) {
					brick.setColor(Color.ORANGE);
				}
				if ((i>3)&&(i<=5)) {
					brick.setColor(Color.YELLOW);
				}
				if ((i>5)&&(i<=7)) {
					brick.setColor(Color.GREEN);
				}
				if ((i>7)&&(i<=9)) {
					brick.setColor(Color.CYAN);
				}	

			}
		}


	}

	/* paddleSetUp sets up the paddle at bottom of screen and sets location of it to the general
	 * x,y. The location of the paddle is later set by the x-value of the mouse. The paddle is
	 * always the same distance from the bottom of the console. The paddle is returned at the
	 * end of the method so it can be referenced later in the program. 
	 */
	private GRect paddleSetUp() {
		paddle = new GRect (PADDLE_WIDTH,PADDLE_HEIGHT);
		double x = (getWidth()-PADDLE_WIDTH)/2;
		double y = getHeight()-PADDLE_Y_OFFSET;
		paddle.setFilled(true);
		add (paddle, x, y);
		return paddle;
	}

}




























