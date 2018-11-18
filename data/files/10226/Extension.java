/*
 * File: Breakout.java  
 * -------------------
 * Name: Elina Thadhani 
 * Section Leader:Vineet Kosaraju 
 * 
 * This file implements the game of breakout with extensions. The program will display the number 
 * of tries remaining, the number of bricks remaining, and will play a sound upon collision. 
 * When the user wins, fireworks are displayed on the screen. 
 */

import acm.graphics.*; 
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Extension extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 1;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 1;

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
	public static final double VELOCITY_Y = 3;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Instance variable for the paddle to be tracked 
	GRect paddle = null;

	// Instance variable for the ball to be tracked
	GOval ball=null;

	// Instance variable for velocity of the ball 
	double vx;
	double vy;

	public static final int TRIES_IN_GAME=3;


	// random instance variable serves as a random generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();


	// Instance variable to keep track of bricks remaining 
	int count;

	// Instance variable for labels

	GLabel bricklabel; 
	GLabel trieslabel; 

	// Instance variable to keep track of the number of tries remaining
	int tries;
	
	// Instance variable to add sound effects to the ball 
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		playGameWithTries();
		//endGame();
	}

	private void setUpBricks() {
		int rownumber = 0;
		for (int i=NBRICK_ROWS; i>0; i--) {
			rownumber++; 
			for (int n=NBRICK_COLUMNS; n>0; n--) {
				double x= (BRICK_SEP/2+(BRICK_WIDTH+BRICK_SEP)*(NBRICK_COLUMNS-n)+(getWidth()-NBRICK_COLUMNS*(BRICK_WIDTH+BRICK_SEP))/2);
				double y= BRICK_Y_OFFSET+(NBRICK_ROWS-i)*(BRICK_HEIGHT+BRICK_SEP); 
				GRect rect = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				if (rownumber>10) {
					rownumber=1;
				}
				if (rownumber==1||rownumber==2) {
					rect.setColor(Color.RED);
				}
				if (rownumber==3||rownumber==4) {
					rect.setColor(Color.ORANGE);
				}
				if (rownumber==5||rownumber==6) {
					rect.setColor(Color.YELLOW);
				}
				if(rownumber==7||rownumber==8) {
					rect.setColor(Color.GREEN);
				}
				if(rownumber==9||rownumber==10) {
					rect.setColor(Color.CYAN);
				}
				add (rect, x,y);
			}
		}
	}
	private void  colorBricks(double row) {

	}
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	private void addPaddleToCenter(GRect paddle) {
		double x= getWidth()/2-PADDLE_WIDTH/2;
		double y=getHeight()-PADDLE_Y_OFFSET;
		add (paddle,x,y);
	}

	public void  mouseMoved (MouseEvent e) {
		double x= e.getX()-PADDLE_WIDTH/2;
		double y=getHeight()-PADDLE_Y_OFFSET;
		if (getWidth()-(x+PADDLE_WIDTH)<0) {
			x=getWidth()-PADDLE_WIDTH;
		} else if (x<0) {
			x=0;
		}
		paddle.setLocation ( x, y);
	}

	private void createMovingPaddle() {
		paddle = makePaddle();
		addPaddleToCenter(paddle);
		addMouseListeners();
	}

	private GOval makeBall() {
		GOval ball = new GOval (2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setFilled(true);
		return ball; 
	}

	private void addBallToCenter( GOval ball) {
		double x=getWidth()/2-BALL_RADIUS;
		double y=getHeight()/2-BALL_RADIUS;
		add (ball,x,y);
	}

	private void setUpGame () {
		setUpBricks();
		createMovingPaddle();
		ball=makeBall();
		addBallToCenter(ball);
	}

	private void playGame () {
		vx= rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy= VELOCITY_Y;
		setUpLabels("Bricks Remaining = " +count, "Tries Remaining =" +tries);
		waitForClick();
		ball.move(vx, vy);
		pause(DELAY); 
		while (ball.getY()<= getHeight() && count>0) {
			checkForWalls();
			checkForCollision();
			ball.move(vx, vy);
			pause(DELAY); 
			bricklabel.setLabel("Bricks Remaining = " +count); 
		}	
	}


	// returns if the ball should bounce off the Top Wall 
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	//returns if the ball should bounce off the Left Wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	// returns if ball should bounce off the Right Wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean hitBottomWall (GOval ball) {
		return ball.getY() >= getHeight()-2*ball.getWidth();
	}

	private void checkForWalls() {
		if (hitTopWall(ball)) {
			vy=-vy;
			ball.move(vx,vy);
			pause (DELAY);
		}
		if (hitLeftWall(ball)||hitRightWall(ball)) {
			vx=-vx;
			ball.move(vx,vy);
			pause (DELAY);
		}
	}

	private void checkForCollision() {
		if (getElementAt(ball.getX(), ball.getY())!=null && getElementAt(ball.getX(), ball.getY())!= paddle) {
			respondToCollision(ball.getX(), ball.getY());
		}else if (getElementAt (ball.getX(), ball.getY()+2*BALL_RADIUS) != null) {
			respondToCollision( ball.getX(), ball.getY()+2*BALL_RADIUS);
		}else if (getElementAt (ball.getX()+2*BALL_RADIUS, ball.getY()) != null && getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()) != paddle) {
			respondToCollision(ball.getX()+2*BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS) != null) {
			respondToCollision(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		}
	}

	private void respondToCollision (double x, double y) {
		GObject collider = getElementAt(x, y);
		if (collider != null && collider != paddle && collider != trieslabel && collider != bricklabel) {
			vy=-vy;
			bounceClip.play();
			remove (collider);
			count=count-1;
			ball.move(vx, vy);
			pause(DELAY);
		} else if (collider ==paddle) {
			vy= -1.10*VELOCITY_Y;  
			bounceClip.play(); 
		}
	}
	private void setUpLabels(String nword, String pword) {
		bricklabel = new GLabel(nword);
		bricklabel.setFont("SansSerif-12");
		add (bricklabel, getWidth()-1.1*bricklabel.getWidth(), 1.1*bricklabel.getAscent());
		trieslabel = new GLabel (pword);
		trieslabel.setFont("SansSerif-12");
		add (trieslabel,0,1.1*trieslabel.getAscent());
	}

	private void endGame() {
		if (count==0) {
			remove (ball);
			displayEndLabel("Congrats, you've won Breakout! :)");
			makeFireworks();
		}
		if (ball.getY()>=getHeight() || tries==0) {
			remove (ball);
			displayEndLabel("Sorry, you've lost Breakout! :( ");
		}
	}
	private void displayEndLabel(String nword) {
		GLabel label = new GLabel(nword);
		label.setFont ("SansSerif-15");
		add (label,getWidth()/2-label.getWidth()/2, getHeight()/2-label.getAscent()/2);
	}
	private void playGameWithTries() {
		tries = TRIES_IN_GAME;
		count = NBRICK_COLUMNS * NBRICK_ROWS;
		while (tries>0 && count!= 0) {
			playGame();
			tries= tries -1; 
			trieslabel.setLabel("Tries Remaining =" +tries);
			pause (3*DELAY);
			addBallToCenter(ball);
			remove(trieslabel);
			remove(bricklabel);
		}
		endGame();
		pause (DELAY*4);
		removeAll();
		endGame();
	}
	private void makeLine(double x, double y) {
		double radius2 = rgen.nextDouble(10,100);
		double angle2 = rgen.nextDouble (0,360);
		double startx = x;
		double starty= y;
		double endx= radius2*Math.cos(angle2)+startx;
		double endy= radius2*Math.sin(angle2)+starty;
		GLine line = new GLine (startx,starty,endx,endy);
		line.setColor(rgen.nextColor());
		add (line);
		pause (DELAY/4);
	}
	private void makeFireworks() {
		for (int n=1; n<40; n++) {
			double p = rgen.nextDouble(0,getWidth());
			double m = rgen.nextDouble (0,getHeight());
			for (int i=0; i<30; i++){
				makeLine(p,m);
			}
			if (n%10 == 0) {
				removeAll();
				endGame();
			}
			bounceClip.play();

		}
	}
}

