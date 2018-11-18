/*
 * File: Extension.java
 * -------------------
 * Name:Rodrigo Ramos
 * Section Leader: Ben Allen
 * 
 * This program creates the playable game Breakout
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
	public static final double BRICK_HEIGHT = 7;

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
	public static final double VELOCITY_Y = 1.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 2.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 250.0;

	//Delay of label display
		public static final double DELAY1 = 1200.0;
		
	//Delay between labels between balls
	public static final double DELAY2 = 700.0;
	
	// Number of turns 
	public static final int NTURNS = 3;
	
	//instance variables
	private GRect paddle= null;
	private GOval ball= null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int BricksLeft = NBRICK_COLUMNS*NBRICK_ROWS;
	private int nTurnsLeft = NTURNS;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// sets initial setup for game
		setUpGame();
		addMouseListeners();
		playGame();		
	}

	private void playGame() {
		//Game goes on while there are bricks left and there are ball left
		while (BricksLeft != 0 && nTurnsLeft > 0) {
			for (int i=0; i<NTURNS; i++) {
				getReady();
				makeBall();
				defineInitialDirection();
				
				//ball animation
				while (true) {
					moveBall();
					checkBorders();
					checkImpact();
					if(checkIfInPlay()) {
						remove(ball);
						nTurnsLeft=nTurnsLeft-1;
						break;
					}
					//checks if there are bricks left and if there are not it exits ball animation
					if(BricksLeft == 0) {
						break;
					}
					pause(DELAY);
				}
				//checks if there are bricks left and if there are not it overrides number of balls
				//left and exits for loop of NTURNS
				if(BricksLeft == 0) {
					remove(ball);
					break;
				}
			}
		}
		//once outside it evaluates why the play game loop was exited and displays the according message 
		if (BricksLeft == 0) {
			youWin();
		}else {
			gameOver();
		}
	}
	
	private void getReady() { 
		//makes getReady Label
		makeLabel("GET READY");
		
		//makes number of ball left label
		makeLabel("YOU HAVE " + nTurnsLeft + " BALLS LEFT");
		
		//makes go label
		makeLabel("GO!");
	}
	
	//makes gameOver label
	private void gameOver() {
		makeLabel("GAME OVER");
	}
	//makes You Win label
		private void youWin() {
			makeLabel("YOU WIN!");
		}
		
	//method creates label in middle of the screen (y position moved 60 pixels past middle)
	//takes String as input. Removes label  after pausing
	private GLabel makeLabel(String s) {
		GLabel lab = new GLabel ("");
		lab.setFont("Courier-24");
		lab.setColor(Color.BLACK);
		lab.setLabel(s);
		double x = (getWidth()/2.0)-lab.getWidth()/2.0;
		double y = getHeight()/2.0+60;
		add(lab,x,y);
		pause(DELAY1);
		remove(lab);
		pause(DELAY2);
		return null;
	}
	
	//defines initial x direction and y direction (vx and vy)
	private void defineInitialDirection() {
		
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy=VELOCITY_Y;
	}
	
	//checks if ball has gone out of screen through bottom
	private boolean checkIfInPlay() {
		double ballY = ball.getY();
		return ballY>getHeight();	
	}
	
	//checks if is within borders. If it reaches a border it 
	//inverts direction (bounces)
	private void checkBorders() {
		if (isWithinBordersY()) {
			vy=-vy;
		}
		if (isWithinBordersX()) {
			vx=-vx;
		}
	}
	
	//checks if the ball is at border right and left
	private boolean isWithinBordersX() {
		return ball.getX() <= 0 || ball.getX() > getWidth()-BALL_RADIUS*2;
	}
	
	//checks if the ball is at border up 
	private boolean isWithinBordersY() {
		return ball.getY()<BALL_RADIUS*2;
	}
	
	//checks if it is touching any object.
	private void checkImpact() {
		//checks if the object is touching is a brick or the paddle
		if (brick()) {
			//checks if the  ball is touching an object with upper part
			if (isTouchingObjectVert())  {
				removeBricks();
				vy=-vy;	
			}
			//checks if the ball is touching an object with lower part
			if (isTouchingSides())  {
				removeBricks();
				vx=-vx;
			}
		}else {
			//checks if the  ball is touching an object with upper part
			if (isTouchingObjectVert())  {
				//inverts absolute value of vy. This avoids the ball bouncing within the paddle
				vy=-Math.abs(vy);	
			}
			//checks if the ball is touching an object with lower part
			if (isTouchingSides())  {
				vx=-vx;
			}
		}
	}
	
	//checks if it is touching any of the 8 points defined below
	private boolean isTouchingObjectVert() {
		return isTouchingTop() || isTouchingBase();
	}
	
	//checks if it is touching any point in the top
	private boolean isTouchingTop() {
		return isTouchingObjectBaseLeft() || isTouchingObjectBaseMidRight() || isTouchingObjectBaseRight();
	}
	
	//checks if it is touching any point in the base
	private boolean isTouchingBase() {
		return isTouchingObjectTopRight() || isTouchingObjectTopMidRight() || isTouchingObjectTopLeft();
	}
	
	private boolean isTouchingSides() {
		return isTouchingObjectBaseMidLeft() || isTouchingObjectTopMidLeft();
	}
	//Checks if it is touching top left corner
	private boolean isTouchingObjectBaseLeft() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		GObject obj = getElementAt(ballX, ballY);
		return obj != null && obj != ball;
	}
	
	//checks if it is touching middle of left side
	private boolean isTouchingObjectBaseMidLeft() {
		double ballX = ball.getX();
		double ballY = ball.getY()+BALL_RADIUS;
		GObject obj = getElementAt(ballX, ballY);
		return obj != null && obj != ball;
	}
	
	//checks if it is touching lower right corner
	private boolean isTouchingObjectTopRight() {
		double ballX = ball.getX()+BALL_RADIUS*2;
		double ballY = ball.getY() + BALL_RADIUS*2;
		GObject obj = getElementAt(ballX, ballY);
		return obj != null && obj != ball;
	}
	
	//checks if it is touching middle of lower side
	private boolean isTouchingObjectTopMidRight() {
		double ballX = ball.getX() + BALL_RADIUS;
		double ballY = ball.getY() + BALL_RADIUS*2;
		GObject obj = getElementAt(ballX, ballY);
		return obj != null && obj != ball;
	}
	
	//checks if it is touching top right corner
	private boolean isTouchingObjectBaseRight() {
		double ballX = ball.getX()+BALL_RADIUS*2;
		double ballY = ball.getY();
		GObject obj = getElementAt(ballX, ballY);
		return obj != null && obj != ball;
	}
	
	//checks if it is touching middle of top side
	private boolean isTouchingObjectBaseMidRight() {
		double ballX = ball.getX()+BALL_RADIUS;
		double ballY = ball.getY();
		GObject obj = getElementAt(ballX, ballY);
		return obj != null && obj != ball;
	}
	
	//checks if it is touching lower left corner
	private boolean isTouchingObjectTopLeft() {
		double ballX = ball.getX();
		double ballY = ball.getY() + BALL_RADIUS*2;
		GObject obj = getElementAt(ballX, ballY);
		return obj != null && obj != ball;
	}
	
	//checks if it is touching middle of right side
	private boolean isTouchingObjectTopMidLeft() {
		double ballX = ball.getX()+ BALL_RADIUS*2;
		double ballY = ball.getY() + BALL_RADIUS;
		GObject obj = getElementAt(ballX, ballY);
		return obj != null && obj != ball;
	}
	
	//checks if it is touching a brick or the paddle
	private void checkIfRemove() {
		if(brick()) {
			removeBricks();
		}
	}
	
	//checks if ball is colliding with paddle by checking if the ball is at paddle height 
	private boolean brick() {
		double ballY = ball.getY();
		return ballY<getHeight()*.7;
	}
	
	//checks every corner of the ball. In every corner it gets the
	//element present. If there is an element it removes it
	private void removeBricks() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		for (int i=0; i<3; i++) {
			for (int f=0; f<3; f++) {
				double x = ballX+f*BALL_RADIUS;
				double y = ballY+i*BALL_RADIUS;
				GObject obj = getElementAt(x,y);
				if (ObjectPresent(obj)) {
					remove(obj);
					BricksLeft=BricksLeft-1;
				}
			}
		}
	}
	
	//checks if the GObject it gets as input is null, if it is not null it returns true
	private boolean ObjectPresent(GObject obj) {
		return obj != null && obj != ball;
	}
	
	//moves ball if ball exists
	private void moveBall() {
		if(ball != null) {
			ball.move(vx, vy);	
		}	
	}

	//makes ball. uses instance variable ball
	private void makeBall() {
		double x = getWidth()/2-BALL_RADIUS;
		double y = getHeight()/2-BALL_RADIUS;
		ball = new GOval(x,y,BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);	
	}

	//sets bricks and sets paddle 
	private void setUpGame() {
		setBricks();
		setPaddle();
	}
	
	//adds paddle in middle of screen. uses instance variable paddle. 
	private void setPaddle() {
		double x = getWidth()/2-PADDLE_WIDTH/2;
		double y = getHeight()-PADDLE_Y_OFFSET;
		paddle=drawRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT,Color.BLACK);
		add(paddle);
	}

	//listens for mouse movement. Every time mouse is move the location 
	//of the paddle is updated
	public void mouseMoved(MouseEvent e) {
		//get mouse x coordinate
		double mouseX = e.getX();
		updatePaddle(mouseX);
	}
	
	//updates location of rectangle, removes previous rectangle and 
	//adds new one in new location mouseX
	private GRect updatePaddle(double mouseX) {
		double x = mouseX;
		double y = getHeight()-PADDLE_Y_OFFSET;
		//checks if paddle would extend beyond x=screen width.
		if (x>getWidth()-PADDLE_WIDTH/2.0) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH,y);
		}else {
			//checks if paddle extends past x=0
			if(x<PADDLE_WIDTH/2.0) {
				paddle.setLocation(0,y);
			}else {
				paddle.setLocation(mouseX-PADDLE_WIDTH/2.0,y);
			}
		}
		return null;
	}

	//creates set up of bricks. gets as input number of rows and columns
	private void setBricks(){
		//loop for number of rows
		for (int i = 0 ; i<NBRICK_ROWS ; i++) {
		//loop for number of bricks per row
			for (int br =0; br<NBRICK_COLUMNS;br++) {
				//sets coordinate of bricks. 
				double x = getWidth()/2-(NBRICK_COLUMNS/2.0)*BRICK_WIDTH-(NBRICK_COLUMNS/2.0-.5)*(BRICK_SEP)+br*(BRICK_WIDTH+BRICK_SEP);
				double y = BRICK_Y_OFFSET+i*(BRICK_HEIGHT+BRICK_SEP);
				//gets last digit of int i and decides what color that row of bricks needs to be.
				int digit = (i+1) % 10;
				if (digit==1 || digit==2) {
					add(drawRect(x,y,BRICK_WIDTH,BRICK_HEIGHT, Color.RED));
				}
				if (digit==3 || digit==4) {
					add(drawRect(x,y,BRICK_WIDTH,BRICK_HEIGHT, Color.ORANGE));
				}
				if (digit==5 || digit==6) {
					add(drawRect(x,y,BRICK_WIDTH,BRICK_HEIGHT, Color.YELLOW));
				}
				if (digit==7 || digit==8) {
					add(drawRect(x,y,BRICK_WIDTH,BRICK_HEIGHT, Color.GREEN));
				}
				if (digit==9 || digit==0) {
					add(drawRect(x,y,BRICK_WIDTH,BRICK_HEIGHT, Color.CYAN));
				}					
			}
		}
	}
	
	//method creates GLabel. Takes as input coordinates, dimension, and color. 
	private GRect drawRect(double x, double y, double width, double height, Color color) {
		GRect rect = new GRect(x,y,width, height);
		rect.setFilled(true);
		rect.setColor(color);
		return rect;
	}
}

