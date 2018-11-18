/*
 * File: Breakout.java
 * -------------------
 * Name: Anya Miller
 * Section Leader: Maggie Davis
 * 
 * This file implements a basic version of the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas
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

	//all instance variables 
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private int paddleBounce;
	private int tilesLeft = NBRICK_COLUMNS*NBRICK_ROWS;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//call method to set up game board
		setUpGame();

		//set up paddle
		makePaddle(); 
		addMouseListeners();

		//start the game!
		for(int turn=0;turn<NTURNS;turn++) {
			makeBall();
			waitForClick();
			PlayBall();
			if(tilesLeft==0) {
				turn=NTURNS;
				
			}
		}
	}


	//method to play the game until tiles are gone
	private void PlayBall() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy=3.0;

		//sends ball downwards in a random direction
		while(ball!=null) {
			ball.move(vx,vy);
			pause(DELAY);
	
			//bounce off walls
			if(ball.getX()>=getWidth()-BALL_RADIUS*2 || ball.getX()<=0) {
				vx=-vx;
				ball.move(vx, vy);
			}
			if(ball.getY()<=0) {
				vy=-vy;
			}
			//bounce off tiles and paddle, remove tile if hit 
			GObject collider = getCollidingObject();
			if (collider != null) {
				vy=-vy;
				if(collider!=paddle) {
					remove(collider);
					tilesLeft = tilesLeft-1;
				}
				if(collider==paddle) {
					ball.move(vx,vy);
					//record paddle bounces 
					paddleBounce = paddleBounce+1;
				
				}
				if(tilesLeft==0) {
					remove(ball);
					break;
				}
			}

			if (ball.getY()+BALL_RADIUS>getHeight()-PADDLE_Y_OFFSET) {
				remove(ball);
				ball=null;
			}
		}
	}

	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		double d = BALL_RADIUS*2;
		//r=right, l=left
		//t=top, b=bottom
		GObject tr = getElementAt(x,y);
		GObject tl = getElementAt(x+d,y);
		GObject br = getElementAt(x,y+d);
		GObject bl = getElementAt(x+d,y+d);


		//look at all corners to see if ball is in contact with object
		if (tr!=null) {
			if (tr==paddle){
				return paddle;
			}else return(tr);
		}else 
			if (tl!=null) {
				if (getElementAt(x+d,y)==paddle){
					return paddle;
				}else return(tl);
			}else 
				if (br!=null) {
					if (br==paddle){
						return paddle;
					}else return(br);
				}else 
					if (bl!=null) {
						if (bl==paddle){
							return paddle;
						}else return(bl);
					}else return null;
	}

	//formats ball and puts on screen
	private void makeBall() {
		ball= new GOval(BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);

		double ballX = getWidth()/2-BALL_RADIUS;
		double ballY = getHeight()/2-BALL_RADIUS;
		add(ball,ballX,ballY);
	}


	//constructs paddle 
	private void makePaddle() {
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		double x= getWidth()/2-(PADDLE_WIDTH/2);
		double y = getHeight()-PADDLE_Y_OFFSET;
		add(paddle,x,y);
	}
	
	//makes paddle follow mouse
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		paddle.setLocation(x-PADDLE_WIDTH/2,getHeight()-PADDLE_Y_OFFSET);

		//prevent paddle from going off screen 
		if(x<=PADDLE_WIDTH/2) {
			paddle.setLocation(0,getHeight()-PADDLE_Y_OFFSET);
		}
		if(x>=getWidth()-PADDLE_WIDTH/2) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH,getHeight()-PADDLE_Y_OFFSET);
		}
	}

	//sets up game tiles 
	private void setUpGame() {
		//tells program where to start making bricks so that row will be centered
		double startRowCentered= getWidth()/2.0-((NBRICK_COLUMNS/2.0)*BRICK_WIDTH+(BRICK_SEP*NBRICK_COLUMNS/2.0));
		//make rows
		for(int row= 0; row<NBRICK_ROWS; row++) {
			for(int col= 0; col<NBRICK_COLUMNS;col++) {
				GRect tile= new GRect(startRowCentered+col*(BRICK_WIDTH+BRICK_SEP ),(BRICK_Y_OFFSET+((BRICK_HEIGHT+BRICK_SEP)*row))-BRICK_HEIGHT,BRICK_WIDTH, BRICK_HEIGHT);
				add(tile);

				//color rows different color every 2 rows (start repeating after 10 rows)
				tile.setFilled(true);
				if(row%10==0.0 || row%10==1.0) {
					tile.setColor(Color.RED);
					tile.setFillColor(Color.RED);
				}
				if(row%10==2.0 || row%10==3.0) {
					tile.setColor(Color.ORANGE);
					tile.setFillColor(Color.ORANGE);
				}
				if(row%10==4.0 || row%10==5.0) {
					tile.setColor(Color.YELLOW);
					tile.setFillColor(Color.YELLOW);
				}
				if(row%10==6.0 || row%10==7.0) {
					tile.setColor(Color.GREEN);
					tile.setFillColor(Color.GREEN);
				}
				if(row%10==8.0 || row%10==9.0) {
					tile.setColor(Color.CYAN);
					tile.setFillColor(Color.CYAN);
				}
			}
		}
	}

}




