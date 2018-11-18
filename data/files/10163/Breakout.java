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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 0.5;
	public static final double VELOCITY_X_MAX = 1.5;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	GRect paddle;
	GOval ball;
	private RandomGenerator rgen=RandomGenerator.getInstance();
	private double vx;
	private double vy;
	private int brickCounter=0;

	public void run() {
		loadBreakout();	
		waitForClick();
		
		for(int i=0;i<NTURNS;i++) {
			moveBall();
			ball.setLocation((getWidth()/2)-(BALL_RADIUS),(getHeight()/2)-(BALL_RADIUS));
		}
		addMouseListeners();	
	}
	public void mouseMoved(MouseEvent e) {
		//mouse move just moves the paddle
		int x=e.getX();
		if(x<getWidth()-PADDLE_WIDTH/2) {
			x=e.getX();
		paddle.setLocation(e.getX(),getHeight()-PADDLE_Y_OFFSET);
		}
	}
	private GRect makePaddle() {
		double x=(getWidth()/2)-(BRICK_WIDTH/2);
		double y=(getHeight()-PADDLE_Y_OFFSET	);
		GRect paddle= new GRect(x,y, BRICK_WIDTH,BRICK_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}
	private GOval makeBall() {
		double x=(getWidth()/2)-(BALL_RADIUS);
		double y=(getHeight()/2)-(BALL_RADIUS);
		GOval ball =new GOval(x,y,BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		return ball;
	}
	private void moveBall() {
		vx =rgen.nextDouble(1.0,3.0);
		if (rgen.nextBoolean(0.5))vx=-vx;
		vy=VELOCITY_Y;
		//while ball is not at bottom of screen
		while(ball.getY()+(BALL_RADIUS*2)<getHeight()) {
			//attempting to end game after brick 100 is gone
			if (brickCounter>=100) {
				break;
			}else {
				if(ball.getX()>=0 && ball.getX()+BALL_RADIUS*2<=CANVAS_WIDTH) {
					//while ball x is between two walls 
					if(ball.getY()>=0) {
						ball.move(vx, vy);
						pause(DELAY);
						testForElements();
					}else {
						vy=-vy;
						ball.move(vx, vy);
						pause(DELAY);
						testForElements();
					}
					//if ball hits side walls
				}else {

					if(ball.getY()>=0) {
						vx=-vx;
						ball.move(vx, vy);
						pause(DELAY);
						testForElements();

					}else {
						vy=-vy;
						vx=-vx;
						ball.move(vx, vy);
						pause(DELAY);
						testForElements();
					}
				}
			}
		}
	}
	//goal is to pass each variable for each edge of circle through object tests 
	private void testForElements() {
		//test upper left
		objectTest(ball.getX(),ball.getY());
		//tests upper Right
		objectTest(ball.getX()+BALL_RADIUS*2,ball.getY()+BALL_RADIUS*2);
		//tests lower Left
		objectTest(ball.getX(),ball.getY()+BALL_RADIUS*2);
		//tests lower Right
		objectTest(ball.getX()+BALL_RADIUS*2,getY()+BALL_RADIUS*2);
	}
	//test a single location on ball for object, if paddle then bounce off, 
	//if brick present then take AND bounce off
	private void objectTest(double x, double y) {
		GObject maybeAnObject=getElementAt(x,y);
		if(maybeAnObject!=null) {
			if(maybeAnObject==paddle) {
				vy=-vy;
				ball.move(vx, vy);
				pause(DELAY);
			}else {
				//this means object must be brick
				// change in y direction
				//remove brick
				//count brick removed 
				vy=-vy;
				ball.move(vx, vy);
				pause(DELAY);
				remove(maybeAnObject);
				brickCounter=brickCounter+1;		
				}
			}
		}	
	private void loadBreakout() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		makeBricks();
		paddle=makePaddle();
		ball=makeBall();	
	}
	private void makeBricks() {

		for(int vert=0; vert<NBRICK_ROWS; vert++) {

			for (int i=0; i <NBRICK_COLUMNS; i++) {
				int n=1+1*vert;
				double x=(getWidth()/2)-(BRICK_SEP/2)-((BRICK_WIDTH+BRICK_SEP)*4)-BRICK_WIDTH+(BRICK_WIDTH+BRICK_SEP)*i;
				double y=BRICK_Y_OFFSET+(BRICK_HEIGHT+BRICK_SEP)*vert;
				GRect rect=new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				rect.setFilled(true);

				switch(n) {
				case 1:
					rect.setColor(Color.RED);
					break;
					
				case 2: 
					rect.setColor(Color.RED);
					break;
				case 3: 
					rect.setColor(Color.ORANGE);
					break;
				case 4: 
					rect.setColor(Color.ORANGE);
					break;
				case 5:
					rect.setColor(Color.YELLOW);
					break;
				case 6:
					rect.setColor(Color.YELLOW);
					break;
				case 7:
					rect.setColor(Color.GREEN);
					break;
				case 8:
					rect.setColor(Color.GREEN);
					break;
				case 9:
					rect.setColor(Color.CYAN);
					break;
				case 10:
					rect.setColor(Color.CYAN);
					break;
				}
				add(rect);
			}
		}
	}
}








	
		
	
	
		







