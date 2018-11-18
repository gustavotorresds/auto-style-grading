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

public class EXTENDEDBREAKOUTFINAL extends GraphicsProgram {

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
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;


	//INSTANCE VARIABLES
	private GRect paddle;
	private double vx, vy;
	private RandomGenerator rgen=RandomGenerator.getInstance();
	private GOval ball;
	private int collisionCounter=NBRICK_COLUMNS*NBRICK_ROWS;
	private int lives=3;





	public void run() {
		addMouseListeners();
		setup();
		playGame();

	}


	private void setup() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createColorChunks();
		paddle=createPaddle();
		addPaddleToCenter();
		createBall();

	}


	/*
	 * This method creates the setup of the bricks. It works to by repeatedly creating
	 * two rows of a specified color.
	 */
	private void createColorChunks() {
		double initialX=getWidth()-(BRICK_WIDTH*NBRICK_ROWS+BRICK_SEP*(NBRICK_ROWS+1));
		double yDistance=BRICK_HEIGHT+BRICK_SEP; 	//this is the distance between the y values of each brick
		for (int col=0; col<2;col++) {
			for (int row=0; row<NBRICK_ROWS; row++) {
				double x= initialX+row*(BRICK_WIDTH+BRICK_SEP);
				double y=BRICK_Y_OFFSET+col*(BRICK_HEIGHT+BRICK_SEP);
				makeBricks(x, y,Color.RED);			//fix this method, use %
				makeBricks(x,y+2*yDistance,Color.ORANGE);
				makeBricks(x,y+4*yDistance,Color.YELLOW);
				makeBricks(x,y+6*yDistance,Color.GREEN);
				makeBricks(x,y+8*yDistance,Color.CYAN);

			}
		}
	}
	/*
	 * This method creates the bricks with the information that is sent through the
	 * makeColorChunk method.
	 */
	private void makeBricks(double x, double y, Color color) {

		GRect rect=new GRect (x,y, BRICK_WIDTH,BRICK_HEIGHT);



		rect.setColor(color);
		rect.setFilled(true);
		add(rect);
	}

	/*
	 * This method creates a paddle and puts it in the middle
	 * of the screen.
	 */
	private GRect createPaddle() {

		GRect paddle= new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		return (paddle);
	}
	private void addPaddleToCenter() {
		double xCenter=(getWidth()-PADDLE_WIDTH)/2;
		double yCenter=getHeight()-PADDLE_Y_OFFSET;
		paddle.setLocation(xCenter, yCenter);
		add (paddle);
	}

	/*
	 * This method runs when the mouse moves and moves the paddle back and forth
	 * while not letting it exceed the boundaries of the canvas
	 */
	public void mouseMoved(MouseEvent e) {
		double x=e.getX()-PADDLE_WIDTH*1/2;
		double y=getHeight()-PADDLE_Y_OFFSET;
		if (x<0) {
			x=0;
		}
		if(x>=getWidth()-PADDLE_WIDTH) {
			x=getWidth()-PADDLE_WIDTH;
		}
		paddle.setLocation(x,y);
	}

	private GOval createBall() {
		double xCenter=getWidth()/2-BALL_RADIUS;
		double yCenter=getHeight()/2-BALL_RADIUS;
		ball=new GOval(xCenter, yCenter, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add (ball);
		return ball;
	}

	private void playGame() {	
		GLabel beginning=new GLabel("Click to begin!");
		beginning.setColor(Color.RED);
		beginning.setFont("Courier-36");
		add(beginning, getWidth()/2-beginning.getWidth()/2, getHeight()/2-beginning.getHeight()/2);
		waitForClick();
		remove(beginning);
		randomStart();

		while (lives>0) {
			collision();
			bouncingBall();
			if (collisionCounter<=0) {
				break;
			}
		}
		remove (ball);
		GLabel endMessage;
		if (lives>0) {
			winCelebration();
			endMessage=new GLabel ("Congratulations, you win!");
		}else {
			endMessage=new GLabel ("Sorry, you lost");
		}
		add(endMessage, getWidth()/2-(endMessage.getWidth()/2), getHeight()/2-(endMessage.getHeight()));
	}


	
	private void randomStart() {
		vx=rgen.nextDouble(1.0,3.0);
		vy=VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) { 
			vx=-vx;
		}
	}

	/*
	 * This method moves the ball around the screen, without taking into
	 * account collisions with the bricks or paddle 
	 */

	private void bouncingBall() {

		if(hitTopWall()) {
			vy=-vy;
		}
		if(hitLeftWall()|| hitRightWall()){
			vx=-vx;
		}
		if(hitBottomWall()) {
			remove(ball);
			lives--;


			if (lives>0) {
				createBall();
				GLabel livesCounter= new GLabel ("Lives remaining: " + lives);
				add(livesCounter, getWidth()/2-livesCounter.getWidth()/2,getHeight()/2-livesCounter.getHeight());
				waitForClick();
				remove(livesCounter);
			}
		}
		ball.move(vx, vy);
		pause(DELAY);
	}

	/*
	 * This method creates the ball
	 */



	private boolean hitBottomWall() {
		return ball.getY()>getHeight()-ball.getHeight();
	}

	/*
	 * If the top wall is hit, this method returns true that will
	 * make dy=-dy in the bouncingBall method
	 */
	private boolean hitTopWall() {
		return ball.getY()<=0;
	}

	private boolean hitRightWall() {
		return ball.getX()>=getWidth()-ball.getWidth();
	}

	private boolean hitLeftWall() {
		return ball.getX()<=0;
	}

	private void collision() {
		AudioClip bounceClip=MediaTools.loadAudioClip ("bounce.au");
		GObject collider=getCollidingObject();



		if (collider==paddle) {

			if (vy==Math.abs(vy)) {
				vy=-vy;
				if (ball.getX()<paddle.getX()+(1/8*PADDLE_WIDTH) && vx==Math.abs(vx)) {	//improves control over bounces
					vx=-vx;
				}
				if  (ball.getX()+2*BALL_RADIUS>paddle.getX()+(PADDLE_WIDTH-5) && vx==(Math.abs(vx)*-1)) {	//improves control over bounces
					vx=-vx;
				}
				bounceClip.play();
			}
		}else if(collider!=null) {
			vy=-vy;
			remove(collider);
			bounceClip.play();
			collisionCounter--;
		}

	}

	private GObject getCollidingObject() {
		GObject object;

		object=getElementAt(ball.getX(),ball.getY());
		if (object != null) {
			return object;
		}
		object=getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());

		if (object !=null) {	
			return object;
		}
		object=getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		if (object !=null) {	
			return object;
		}
		object=getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		if (object !=null){
			return object;
		}
		return null;
	}

	private void winCelebration() {
		GRect colorFlashes=new GRect (0, 0, getWidth(), getHeight());
		add(colorFlashes);
		colorFlashes.setFilled(true);
		GLabel congrats;
		for (int i=0; i<5; i++) {

			//awful decomposition and messy, but very fun

			colorFlashes.setColor(Color.ORANGE);
			pause(100);
			congrats=new GLabel("YESSS!!!", 50,100);
			add (congrats);
			congrats.setFont("Courier-24");
			congrats.setColor(Color.WHITE);
			colorFlashes.setColor(Color.CYAN);
			pause(100);
			congrats=new GLabel("YOU WIN!!!", 100,150);
			add(congrats);
			congrats.setFont("Courier-24");
			congrats.setColor(Color.WHITE);
			colorFlashes.setColor(Color.PINK);
			pause(100);
			congrats=new GLabel("YOU'RE THE BEST!!", 150,200);
			add(congrats);
			congrats.setFont("Courier-24");
			congrats.setColor(Color.WHITE);
			colorFlashes.setColor(Color.MAGENTA);
			pause(100);
			congrats=new GLabel("106A RULES!!", 100,250);
			add(congrats);
			congrats.setFont("Courier-24");
			congrats.setColor(Color.WHITE);
			colorFlashes.setColor(Color.GREEN);
			pause(100);
			congrats=new GLabel("BREAKOUT IS FUN!", 50,300);
			add(congrats);
			congrats.setFont("Courier-24");
			congrats.setColor(Color.WHITE);
			colorFlashes.setColor(Color.ORANGE);
			pause (100);
			congrats=new GLabel("HI BRAHM", 100,350);
			add(congrats);
			congrats.setFont("Courier-24");
			congrats.setColor(Color.WHITE);
			colorFlashes.setColor(Color.CYAN);
			pause (100);
			congrats=new GLabel("SWAG YOLO", 150,400);
			add(congrats);
			congrats.setFont("Courier-24");
			congrats.setColor(Color.WHITE);
			colorFlashes.setColor(Color.PINK);
			pause (100);
			congrats=new GLabel("HIP HIP HOORAY!!!!", 200,450);
			add(congrats);
			congrats.setFont("Courier-24");
			congrats.setColor(Color.WHITE);



		}
		remove (colorFlashes);

	}
	
}



























