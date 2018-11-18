/*
 * File: FinalExtension.java
 * -------------------
 * Name: Dylan Junkin
 * Section Leader: Ben Allen
 * This program is called Breakout and replicates a game created
 * by Steve Wozniak. The game creates a set of "bricks" that remain
 * at the top of the screen and are narrowly separated, while the user
 * directs a paddle along the bottom of the screen by moving their mouse.
 * A ball is created in the middle of the screen and when the user clicks
 * to begin, the ball moves towards the paddle which bounces the ball towards
 * the bricks. If a collision between the bricks and the ball occurs, the colliding
 * brick is removed and the ball reverses its vertical direction. Similarly, hitting 
 * the left or right walls reverses the horizontal direction, while hitting the 
 * paddle or the top reverses the vertical direction. If the ball crosses the bottom
 * of the screen, it is removed and then if the user has turns remaining, another ball
 * is created and the user begins a new turn. If the user eliminates all the bricks
 * before using all their turns, they win. If they fail to eliminate all the bricks
 * and lose their turns, they lose.
 * 
 * The extension additionally features an intro screen with three buttons depicting
 * the three different background colors that the game can have. The user clicks to
 * roll a "dice" which determines the background color. At the end of the game two pictures
 * can pop up. If the user wins a picture of Chris Piech's lovely face emerges. If they lose
 * a sad face of a crying puppy pops up instead.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class FinalExtension extends GraphicsProgram {

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

	//For Extension Intro Screen
	private static final double BUTTON_WIDTH = 110;
	private static final double BUTTON_HEIGHT = 100;
	private static final double SEPERATION = 30;

	//Instance Variables are declared here
	private GRect paddle;
	private GOval ball;
	private GLabel end;
	private int brickcount, diceRoll;
	private double vx, vy;
	private RandomGenerator rgen;

	public void run() {

		// Here some of the instance variables are initialized
		rgen = RandomGenerator.getInstance();
		paddle = null;
		ball = null;
		brickcount = 0;

		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createIntro();
		makeGame();
		addMouseListeners(); // Adds mouse listeners for events
		waitForClick();
		for(int i = NTURNS; i > 0; i--) { //The loop runs for the number of turns specified by NTURNS
			runGame();
			if(i!=1 && brickcount != 0) { //Restarts by telling user the number of remaining turns and making new ball
				displayEnd("YOU HAVE " + (i-1) + " TURN(S) LEFT");
				waitForClick();
				remove(end);
				makeBall();
			}
		}
		if(brickcount == 0){ //If user has eliminated all bricks (Win)
			removeAll();
			displayEnd("GAME FINISHED: YOU WON");
			pause(2000);
			makeImage("Won.jpg");
		}
		if(brickcount != 0){ //If user has not eliminated all bricks (Lose)
			removeAll();
			displayEnd("GAME FINISHED: YOU LOST");
			pause(2000);
			makeImage("Lose.jpg");
		}

	}

	private void makeImage(String string) {
		GImage image = new GImage(string);
		add(image);
	}

	private void createIntro() {
		diceRoll = rgen.nextInt(1,3);
		double rx2 = (getWidth()-BUTTON_WIDTH)/2;
		double rx1 = rx2 - SEPERATION - BUTTON_WIDTH;		
		double rx3 = rx2 + BUTTON_WIDTH + SEPERATION;
		double ry = getHeight()-SEPERATION-BUTTON_HEIGHT;
		double ry2 = (getHeight()-BUTTON_HEIGHT)/2;
		double lx1 = rx1 + (BUTTON_WIDTH/2);
		double lx2 = rx2 + (BUTTON_WIDTH/2);
		double lx3 = rx3 + (BUTTON_WIDTH/2);
		double lx4 = getWidth()/2;
		double ly = ry + (BUTTON_HEIGHT/2);
		double ly1 = ry2 + (BUTTON_HEIGHT/2);

		createButton(rx1, ry, lx1, ly, "1: Black");
		createButton(rx3, ry, lx2, ly, "2: Pink");
		createButton(rx2, ry, lx3, ly, "3: Grey");
		createButton(rx2, ry2, lx4, ly1, "CLICK TO ROLL DICE");
		waitForClick();
		removeAll();
		
		if(diceRoll == 1) {
			setBackground(Color.BLACK);
		}
		if(diceRoll == 2) {
			setBackground(Color.PINK);
		}
		if(diceRoll == 3) {
			setBackground(Color.GRAY);
		}
		
	}

	private void createButton(double x, double y, double lx, double ly, String w) {
		GRect rect = new GRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
		add(rect);
		GLabel label = new GLabel(w);
		label.move(lx-label.getWidth()/2, ly + label.getAscent()/2);
		add(label);

	}

	/*The method displayEnd takes in a string as a parameter and then creates a label
	 * with the String's text, centers it in the middle of the screen, and adds it to the
	 * screen. In this program it is called at the end of each turn and at the end of the game.
	 */
	private void displayEnd(String finalmessage) {
		end = new GLabel (finalmessage);
		end.move(getWidth()/2 - end.getWidth()/2, getHeight()/2 + end.getAscent()/2);
		add(end);
	}

	/* The runGame method provides the animation for the ball and directs its motion
	 * determining if it collides with the walls, the paddle, or a brick. If the ball
	 * hits the top wall its y velocity is reversed thus making it "bounce". If it hits 
	 * the bottom wall, the ball is removed and the loop is broken out of. If the ball
	 * hits the right or left walls the x velocity is reversed causing it to "bounce" as
	 * well. To determine if the ball hits an object, the x and y positions on all four
	 * corners of the square the ball is inscribed in are tested. If any of these four
	 * positions touch an object and the object is not the paddle, the object is removed and
	 * the ball's y velocity is flipped. If the object is the paddle, the object is not removed
	 * but the y velocity is still flipped.
	 */
	private void runGame() {
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(.5)) vx = -vx;
		vy = 3.0;
		boolean hitBottom = false;

		while(brickcount > 0 && hitBottom == false) { //Continues while the bottom has not been hit and there are bricks present
			if(lOrRHit()) { //Checks for left and right wall hit
				vx = -vx;
			}
			if(tOrBHit()) { //Checks for top or bottom wall hit
				if(ball.getY()>=getHeight()-(BALL_RADIUS*2)) {
					remove(ball); //Breaks out of loop if ball hits bottom wall.
					hitBottom = true;
				} else {
					vy = -vy;
				}
			}

			ball.move(vx, vy);
			pause(DELAY);
			double x = ball.getX();
			double y = ball.getY();
			if(getElementAt(x,y)!=null){ //Checks NW corner of ball's square.
				GObject collision = getElementAt(x, y);
				if(collision != paddle && collision != ball) {
					remove(collision); //Removes brick (repeats in steps below)
					brickcount--; //Keeps track of bricks to determine if game is won (repeats in steps below)
					vy = -vy;
				} 
				ball.move(vx, vy);
				pause(DELAY);

			} else if(getElementAt(x, y + 2*BALL_RADIUS) != null) { //Checks SW corner of ball's square.
				GObject collision = getElementAt(x, y + 2*BALL_RADIUS );
				if(collision == paddle) {
					vy = -vy;
				} else {
					if(collision != ball) {
						remove(collision);
						brickcount--;
						vy = -vy;	
					}
				}
				ball.move(vx, vy);
				pause(DELAY);

			} else if(getElementAt(x + 2*BALL_RADIUS, y) != null) { //Checks NE Corner of ball's square.
				GObject collision = getElementAt(x + 2*BALL_RADIUS, y);
				if(collision != paddle && collision != ball) {
					remove(collision);
					brickcount--;
					vy = -vy;
				}
				ball.move(vx, vy);
				pause(DELAY);

			} else if(getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS) != null) { //Checks SE corner of ball's square.
				GObject collision = getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS);
				if(collision == paddle) {
					vy = -vy;
				} else {
					if(collision != ball) {
						remove(collision);
						brickcount--;
						vy = -vy;
					}
				}
			}
			ball.move(vx, vy);
			pause(DELAY);
		}
	}

	/* tOrBHit is a method that determines if the ball (an instance variable)
	 * hits the top or bottom wall of screen. It is a boolean method and returns 
	 * true if either of these conditions occurs. It specifies that if the position of the
	 * ball's bottom edge is greater than the bottom screen or top edge is less than
	 * the top edge of the screen, true is returned (which then causes the runGame
	 * method to reverse the y velocity. 
	 */
	private boolean tOrBHit() {
		return ball.getY() < 0 || ball.getY()>getHeight()-(BALL_RADIUS*2);
	}

	/* lOrRHit is a method that determines if the ball (an instance variable)
	 * hits the left or right wall of screen. It is a boolean method and returns 
	 * true if either of these conditions occurs. It specifies that if the position of the
	 * ball's right edge is greater than the right screen or left edge is less than
	 * the left edge of the screen, true is returned (which then causes the runGame
	 * method to reverse the x velocity. 
	 */
	private boolean lOrRHit() {
		return ball.getX()<0 || ball.getX()>getWidth()-(BALL_RADIUS*2);
	}

	/* makeBall is a method that returns a GOVAL. It is called as the game is set up
	 * and returns a GOVal this is stored as the instance variable. The ball that is created
	 * is centered on the screen and colored black. It is also called after the ball is removed
	 * when a turn ends. 
	 */
	private void makeBall() {
		double x = (getWidth()/2-BALL_RADIUS);
		double y = (getHeight()/2-BALL_RADIUS);
		ball = new GOval (x,y,BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		if(diceRoll == 1) {
			ball.setColor(Color.WHITE);
		}
		add(ball);
	}

	/* makePaddle is a method that returns the GRect that is the paddle. It is called
	 * as the game is set up and the paddle it creates is returned to the makeGame 
	 * method where it is stored in the instance variable paddle. 
	 */
	private void makePaddle() {

		paddle = new GRect(getWidth()/2-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		if(diceRoll == 1) {
			paddle.setColor(Color.WHITE);
		}
		add(paddle);

	}

	/* This mouseMoved method is activated when the user moves the mouse. It uses
	 * the x location of the user's mouse to set the location of the paddle (as
	 * long as it is within the bounds of the screen). The y location is not
	 * changed given the user's mouse's y location.
	 */
	public void mouseMoved(MouseEvent event) {
		double x = event.getX();
		if(x > 0 && x < getWidth()-PADDLE_WIDTH){
			paddle.setLocation(x, getHeight()-PADDLE_Y_OFFSET);
		}
	}

	/* The makeGame method of the program takes in no parameters and returns nothing.
	 * What it does is set up the bricks in the game and assign a value to the instance
	 * variables ball and paddle (by calling makePaddle and makeBall). To set up the bricks
	 * the method uses nested for loops and creates the bricks given the number of rows and columns
	 * specified as constants. To assign color to the bricks, a variables counts the rows and then
	 * every two rows, switches to another color before restarting after 10 rows. The makeGame method
	 * also counts the number of bricks laid which is used to determine if the user wins. 
	 */
	private void makeGame() {

		int colorRow = 0;
		for(int i = NBRICK_ROWS; i > 0; i--) {
			colorRow++;
			for(int f = NBRICK_COLUMNS; f > 0; f--) {
				GRect rect = new GRect((BRICK_SEP/2 +(BRICK_WIDTH+BRICK_SEP)*(NBRICK_COLUMNS-f)+ (getWidth()-NBRICK_COLUMNS*(BRICK_WIDTH+BRICK_SEP))/2),BRICK_Y_OFFSET + (BRICK_SEP+BRICK_HEIGHT)*(NBRICK_ROWS-i), BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				if(colorRow == 1 || colorRow == 2) {
					rect.setColor(Color.RED);
				} else if(colorRow == 3|| colorRow == 4) {
					rect.setColor(Color.ORANGE);
				} else if(colorRow == 5|| colorRow ==6) {
					rect.setColor(Color.YELLOW);
				} else if(colorRow == 7|| colorRow == 8) {
					rect.setColor(Color.GREEN);
				} else if(colorRow == 9|| colorRow == 10) {
					rect.setColor(Color.CYAN);
				}
				add(rect);
				brickcount++; //Counts the bricks laid
			}
			if(colorRow == 10) { //Restarts the colors after 10 rows
				colorRow = 0;
			}
		}
		makePaddle();
		makeBall();

	}

}
