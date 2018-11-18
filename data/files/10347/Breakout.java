/*
 * File: Breakout.java
 * -------------------
 * Name:Nicki Schindler
 * Section Leader: Semir Shafi
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

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

	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	int bricksBroken;
	private static final String YOUWIN = "You Win!";
	private static final String YOULOSE = "You Lose!";

	public void run() {


		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		/*This is the setup for the game. It will create the paddle and make it follow the mouse, 
		 * and then create the entire brick setup. 
		 */
		paddle= makePaddle();
		addPaddleToCanvas(paddle);
		addMouseListeners();
		brickSetUp();
		/* This is when the actual game starts. For however many specified turns
		 * the code will create a ball and allow you to play. If you run out of 
		 * turns, and you still have bricks remaining, it will tell you you lost.
		 * If you break all of the bricks, it will tell you that you won and the game will end. 
		 * 
		 */
		for( int h=0; h<NTURNS; h++) {
			//add a new ball at the beginning of each turn. 
			ball= makeBall();
			//if # of bricks is less than 100 game continues. 
			if (bricksBroken<100) {
				play();
				// if you break all bricks with turns remaining it will tell you that you won and end. 
				if (bricksBroken==100) {
					winLabel();
					break;
				}
			}


		}
		loseLabel();


	}





/* This is the code that actually sets up the game. It 
 * randomizes the initial direction, the causes the ball to bounce of the walls, 
 * paddle, break a brick, or fall through the bottom and cause a turn to end. 
 */
	private void play() {
		bricksBroken=0;
		waitForClick();
		// randomizing initial direction in x-direction. 
		double vx =  rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		//randomizing initial direction in the y-direction. 
		double vy = 5; 		
		while (true) {
			ball.move(vx, vy);
			//if it hits the top wall, y-direction will reverse. 
			if(theTopWall(ball)) {
				vy=-vy;
			}
			//if it hits the right wall or left wall, x-direction will change. 
			else if(theRightWall(ball)||theLeftWall(ball)) {
				vx=-vx;
			}
			//if it misses the paddle, then the turn is over and the ball is removed.
			else if  (missPaddle()) {
				remove(ball);
				break;
			}
			// if it hits the paddle, the y direction is reversed. 
			else {
				GObject target = checkCollisions();
				if (target==paddle) {
					if(hitPaddle()){
						vy=-vy;
					}
				}
				// if it hits any object other than the paddle (a brick), the object is removed. 
				else if (target!=null) {
					remove(target);
					bricksBroken++;
					if(bricksBroken==100) {
						break;
					}
					vy=-vy;
				}
			}



			pause(DELAY);
		}
	}





/* These are all methods that have been extracted from play() to make it 
 * easier to read. 
 */
	private boolean hitPaddle() {
		return ball.getY()>=(getHeight()-((PADDLE_Y_OFFSET+(PADDLE_HEIGHT))+ (BALL_RADIUS*2)));
	}
	private boolean missPaddle() {
		return ball.getY()>(getHeight()-(BALL_RADIUS*2));
	}
	private boolean theLeftWall(GOval ball) {
		return ball.getX()<0;
	}
	private boolean theRightWall(GOval ball) {
		return ball.getX()>getWidth()-(BALL_RADIUS)*2;
	}
	private boolean theTopWall(GOval ball) {
		return ball.getY()<0;
	}

	/*This code checks for collisions between the ball and any object.
	 * Since there are 4 side to the ball, there must be 4 conditions to check (upper left,
	 * lower left, upper right, and lower right). If there is no object where the ball is 
	 * it will return the value null. 
	 */
	private GObject checkCollisions(){
		// checks for a collision at the top left corner. 
		if((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		//checks for a collision at the top right corner.
		else if((getElementAt((ball.getX()+BALL_RADIUS*2), ball.getY()) !=null) ) {
			return ((getElementAt((ball.getX()+BALL_RADIUS*2), ball.getY())));
		}
		//checks for a collision at the bottom left corner. 
		else if(getElementAt(ball.getX(), (ball.getY()+BALL_RADIUS*2))!= null) {
			return ((getElementAt(ball.getX(), (ball.getY()+BALL_RADIUS*2))));
		}
		//checks for a collision at the bottom right corner.
		else if(getElementAt((ball.getX()+BALL_RADIUS*2), (ball.getY()+BALL_RADIUS*2))!=null) {
			return (getElementAt((ball.getX()+BALL_RADIUS*2), (ball.getY()+BALL_RADIUS*2)));
		}
		else {
			return null;
		}
	}


	/*This code sets up the bricks according to the constant for 
	 * number of bricks per row and number of columns. Then it colors the 
	 * bricks 5 different colors. 
	 */
	private void brickSetUp() {
		for (int j=0; j<NBRICK_COLUMNS; j++) {
			for (int i=0; i<NBRICK_ROWS; i++) {
				double x= (i*(BRICK_WIDTH+BRICK_SEP));
				double y= ((BRICK_SEP+BRICK_HEIGHT)*j);
				GRect rect = new GRect(((getWidth()/2)-((NBRICK_ROWS*(BRICK_WIDTH+BRICK_SEP))/2))+(x), BRICK_Y_OFFSET + y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				if (j<2) {
					rect.setColor(Color.RED);
				}
				if (j==2 || j==3) {
					rect.setColor(Color.YELLOW);
				}
				if (j==4 || j==5) {
					rect.setColor(Color.ORANGE);
				}
				if (j==6 || j==7) {
					rect.setColor(Color.GREEN);
				}
				if (j==8 || j==9) {
					rect.setColor(Color.CYAN);
				}
				add(rect);
			}
		}
	}
	//This is the code that moves the mouse. It only moves laterally and not in the y-direction. 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = (getHeight()-PADDLE_Y_OFFSET);
		if (mouseX<getWidth()-PADDLE_WIDTH) {
			// sets the paddle at the coordinates of mouse. 
			paddle.setLocation(mouseX, mouseY);
		}
	}
	
	//This code adds the paddle to the canvas. 
	private void addPaddleToCanvas(GRect paddle) {
		double x= (0);
		double y= (getHeight()-PADDLE_Y_OFFSET);
		add(paddle, x,y);
	}
	
	//This code creates the paddle with the given constants. 
	private GRect makePaddle() {
		GRect paddle =new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}
	
	//This code creates the ball with given constants and adds it to the canvas. 
	private GOval makeBall() {
		double size= BALL_RADIUS*2;
		GOval r = new GOval(size, size);
		r.setFilled(true);
		r.setColor(Color.BLACK);
		add(r,CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
		return r;
	}
	
	//This code creates a label that will print if you win. 
	private void winLabel() {
		GLabel label = new GLabel(YOUWIN, CANVAS_WIDTH/2 , CANVAS_HEIGHT/2);
		add(label);
	}
	//This code creates a label that will print if you lose. 
	private void loseLabel() {
		GLabel label = new GLabel(YOULOSE, CANVAS_WIDTH/2 , CANVAS_HEIGHT/2);
		add(label);
	}
}

