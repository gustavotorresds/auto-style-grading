/*
 * File: Breakout.java
 * -------------------
 * Name:Julia Lee
 * Section Leader:Julia Daniel
 * 
 * This file implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;


	private GRect paddle = null;

	private double vx,vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private int totalBricks = (NBRICK_COLUMNS*NBRICK_ROWS);

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		paddle= createPaddle();
		addMouseListeners();
		setUpGame();
		playGame();
	}

	private void playGame(){
		//This for loop allows the player three turns
		for(int i=0; i<3;i++) {	
			makeBall();
		}
		if (totalBricks==0) {
			GLabel end = new GLabel("_");
			end.setLocation((getWidth()/2)-(end.getWidth()/2),getHeight()/2);
			end.setColor(Color.WHITE);
			add(end);
			println("_");
		}else {
			GLabel gameOver = new GLabel("Game Over");
			gameOver.setLocation((getWidth()/2)-(gameOver.getWidth()/2),getHeight()/2);
			add(gameOver);
		}
	}

	private void makeBall() {
		//This creates the ball
		GOval ball = new GOval((getWidth()/2)-BALL_RADIUS,(getWidth()/2)-BALL_RADIUS, BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		//This sets up the velocities of the ball 
		vx= rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx=-vx;
		vy=VELOCITY_Y;
		//The animation loop below animates the ball, starting the game
		boolean ballSafe =true;
		while(ballSafe) {
			ball.move(vx, vy);
			//These variables keep track of the ball's position. 
			double bx=ball.getX();
			double by=ball.getY();
			//This pause makes the animation visible
			pause(DELAY);
			//These two lines check if the object is hitting anything and react to it.
			GObject thingHit = collisionCheck(bx,by);
			reactToObject(thingHit);
			//These if statements cause the ball to bounce off the walls.
			if(bx>(getWidth()-(2*BALL_RADIUS)) || bx<(0)) {
				vx=vx*(-1);
			}
			if (by<(0)) {
				vy=vy*(-1);
			}
			//This if statement brings the program back up to the for loop in playGame if the bottom wall is hit.
			if (by>(getHeight()-(2*BALL_RADIUS))) {
				remove(ball);
				ballSafe=false;
			}
			if (totalBricks==0) {
				ballSafe=false;
				GLabel youWin = new GLabel("You Win!");
				youWin.setLocation((getWidth()/2)-(youWin.getWidth()/2),getHeight()/2);
				add(youWin);
			}
		}
	}


	//collisionCheck monitors what the ball is colliding with and proceeds from there.
	private GObject collisionCheck(double bx, double by) {
		//This series of if and else statements checks for objects at each corner while
		//the ball is moving, and stores an object if one is found.
		if((getElementAt(bx,by)!=null)){
			GObject thingHit = (getElementAt(bx,by));
			return thingHit;
		}
		else if(getElementAt((bx+(2*BALL_RADIUS)),by)!=null){
			GObject thingHit =getElementAt((bx+(2*BALL_RADIUS)),by);
			return thingHit;
		}
		else if (getElementAt(bx,by+(2*BALL_RADIUS))!=null) {
			GObject thingHit =(getElementAt(bx,by+(2*BALL_RADIUS)));
			return thingHit;
		}
		else if (getElementAt(bx+(2*BALL_RADIUS),by+(2*BALL_RADIUS))!= null) {
			GObject thingHit =(getElementAt(bx+(2*BALL_RADIUS),by+(2*BALL_RADIUS)));
			return thingHit;
		}
		else {
			return null; 
		}

	}

	private void reactToObject(GObject thingHit) {
		//This if statement checks if the found object is a paddle and bounces off of it if so.
		if (thingHit == paddle) {
			vy=vy*(-1);
		}
		//This if statement checks if the found object is a brick and bounces off of it after deleting it if so.
		else if (thingHit != null) {
			remove (thingHit);
			totalBricks=totalBricks-1;
			vy=vy*(-1);
		}
	}

	private void setUpGame() {
		createBricks();
		//for clarity understand that the mouseEvent and the paddle created for it fall in this
		//category as well but cannot be called within this private method.
	}

	private void createBricks() {
		for(int i = 0; i < NBRICK_ROWS; i++){
			for (int j=0; j< NBRICK_COLUMNS; j++) {
				GRect brick = new GRect (
						((getWidth()-((NBRICK_COLUMNS * BRICK_WIDTH)+(BRICK_SEP*(NBRICK_COLUMNS-1))))/2)+ (j*(BRICK_WIDTH+BRICK_SEP)),
						(((BRICK_Y_OFFSET+(NBRICK_ROWS*BRICK_HEIGHT))+(BRICK_SEP*(NBRICK_ROWS-1))))-(i*(BRICK_SEP+BRICK_HEIGHT)),
						BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				//These if statements set the color of the bricks, creating a rainbow pattern. 
				if(i<2) {
					brick.setColor(Color.CYAN);
				}
				if(1 < i & i< 4) {
					brick.setColor(Color.GREEN);
				}
				if(3<i & i<6) {
					brick.setColor(Color.YELLOW);
				}
				if(5<i & i<8) {
					brick.setColor(Color.ORANGE);
				}
				if (7<i & i<10) {
					brick.setColor(Color.RED);
				}
				add(brick);
			}
		}
	}
	private GRect createPaddle() {
		GRect paddle=new GRect ((getWidth()-PADDLE_WIDTH)/2,getHeight()-PADDLE_Y_OFFSET,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}

	public void mouseMoved(MouseEvent e) {
		int x= e.getX();
		if (x<0+PADDLE_WIDTH) {
			paddle.setLocation(x,getHeight()-PADDLE_Y_OFFSET);
		}else {
			paddle.setLocation(x-PADDLE_WIDTH,getHeight()-PADDLE_Y_OFFSET);	
		}
	}
}
