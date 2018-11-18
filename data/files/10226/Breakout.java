/*
 * File: Breakout.java   
 * -------------------
 * Name: Elina Thadhani 
 * Section Leader:Vineet Kosaraju 
 * 
 * This file implements the game of breakout. The user tries to 
 * bounce a ball off a paddle by moving the mouse, and that ball, 
 * when it hits a set of bricks at the top of the screen, removes 
 * those bricks. The goal of the game is to remove all the bricks on
 * the screen. The user has three tries to win the game, each try being 
 * ending when the user fails to hit the ball with the paddle (or if the user wins).
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

	// Instance variable for the velocity of the ball in the 
	// horizontal direction (vx) and in the vertical direction (vy)
	double vx;
	double vy;

	// random instance variable serves as a random generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Instance variable to keep track of bricks remaining in the game 
	double count;
	
	// Instant variable to keep track of the number of tries remaining in the game 
	int tries = NTURNS;
	
	public void run() {

		
		// Sets up the bricks, the paddle, and ball for the game
		setUpGame();
		
		// Allows the user to play the game 
		playGameWithTries();
		
		// Ends the game 
		endGame();
	}
	/* 
	 * This method sets up the interface of the game. The method sets the title of the
	 * game as "CS 106A Breakout", sets the canvas size of the game, the rainbow brick 
	 * set up with the preset number of bricks, the paddle that tracks the mouse, and the ball. 
	 * The paddle will move as the user moves the mouse, and the ball is placed at the center of 
	 * the canvas before the start of the game. 
	 * Precondition: None 
	 * Postcondition: The canvas of set size, with the title CS 106A Breakout is displayed, and 
	 * the set number of bricks colored in a rainbow pattern (two rows of red, orange, yellow, green, and cyan) 
	 * are displayed a set distance from the top of the screen. The paddle moves in the x direction 
	 * as the mouse is moved, and the ball of set radius is placed in the center of the canvas. 
	 */
	private void setUpGame () {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size for the game 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		createMovingPaddle();
		ball=makeBall();
		addBallToCenter(ball);
	}

	/* 
	 * This method sets up the rainbow patterned bricks, a set distance from the top of the 
	 * canvas and from each other.The color changes every two rows. 
	 * Precondition: None 
	 * Postcondition: bricks are displayed a set distance from the top of the canvas 
	 * such that the color of the rows switch every two rows, and the bricks are a set 
	 * distance away from the top of the canvas. 
	 */
	private void setUpBricks() {
		int rownumber = 0;
		for (int i=NBRICK_ROWS; i>0; i--) { 			//this loop serves for the number of rows
			rownumber++; 
			for (int n=NBRICK_COLUMNS; n>0; n--) {		// this loop serves for the number of columns
				double x= (BRICK_SEP/2+(BRICK_WIDTH+BRICK_SEP)*(NBRICK_COLUMNS-n)+(getWidth()-NBRICK_COLUMNS*(BRICK_WIDTH+BRICK_SEP))/2);
				double y= BRICK_Y_OFFSET+(NBRICK_ROWS-i)*(BRICK_HEIGHT+BRICK_SEP); 
				GRect rect = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				if (rownumber>10) {						// resets the color pattern if number of rows is greater than 10 
					rownumber=1;
				}
				rect.setColor(getColorForRow(rownumber));		// sets the color of the brick based on the rownumber 
				add (rect, x,y);
			}
		}
	}
	/* 
	 * This method returns the appropriate color for the brick being added based on the 
	 * row number of the brick. The method returns a type Color. As the row number increases, 
	 * the pattern follows that of a rainbow, with the color changing every two row numbers. 
	 * Precondition: None 
	 * Postcondition: A color is returned based on the row number put into the parameter of the method. 
	 */
	private Color getColorForRow(int num) {
		Color color = null;
		if (num==1||num==2) {
			color = Color.RED;
		}
		else if (num==3||num==4) {
			color =  Color.ORANGE;
		}
		else if (num==5||num==6) {
			color = Color.YELLOW;
		}
		else if(num==7||num==8) {
			color = Color.GREEN;
		}
		else if(num==9||num==10) {
			color = Color.CYAN;
		}
		return color;
	}
	
	/* 
	 * This method makes a paddle, adds it to the center of the screen and then allows the 
	 * paddle to move as the mouse moves. The paddle only moves in the x direction, and it 
	 * is a set distance above the bottom of the screen. 
	 * Precondition: None 
	 * Postcondition: The paddle is added to the bottom of the screen and moves in the 
	 * x direction as the mouse moves. 
	 */
	private void createMovingPaddle() {
		paddle = makePaddle();
		addPaddleToCenter(paddle);
		addMouseListeners();
	}
	
	/* 
	 * This method makes the paddle for the game and then returns the paddle for animation. 
	 * The paddle is of set width and height and is filled. 
	 * Precondition: None 
	 * Postcondition: the paddle of set width and height is created and returned for animation. 
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	/* 
	 * This method adds a paddle to the horizontal center of the canvas, a set height 
	 * above the bottom of the canvas. 
	 * Precondition: None 
	 * Postcondition: The paddle is added to the canvas, in the center of the width, and 
	 * a set distance above the bottom of the canvas. 
	 */
	private void addPaddleToCenter(GRect paddle) {
		double x= getWidth()/2-PADDLE_WIDTH/2;
		double y=getHeight()-PADDLE_Y_OFFSET;
		add (paddle,x,y);
	}
	
	/* 
	 * This method tracks the movement of the mouse, and has the paddle move from side to side
	 * (not up or down) across the screen as the mouse moves. The paddle stops at the left and 
	 * at the right wall. This method allows the user to move the paddle with the mouse. 
	 * Precondition: The paddle has been created and added to the screen, and returned for animation.  
	 * Postcondition: The paddle moves as the mouse moves, and the x coordinate of the paddle 
	 * changes, while the y coordinate does not: the paddle only moves from side to side. 
	 */

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

	/* 
	 * This method makes the ball of a set radius for the game. The ball is then returned 
	 * for animation. This method returns the type GOval. 
	 * Precondition: None 
	 * Postcondition: a ball of set radius size is created and returned for animation. 
	 */
	private GOval makeBall() {
		GOval ball = new GOval (2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setFilled(true);
		return ball; 
	}

	/* 
	 * This method adds a ball to the center of the canvas. The ball is of set radius. 
	 * Precondition: the ball has been created 
	 * Postcondition: the ball is added to the center of the canvas 
	 */
	private void addBallToCenter( GOval ball) {
		double x=getWidth()/2-BALL_RADIUS;
		double y=getHeight()/2-BALL_RADIUS;
		add (ball,x,y);
	}

	/* 
	 * This method allows the user to play the game of breakout with a set number of attempts. 
	 * While there are bricks left and the user has not used up all the tries, the user can play the 
	 * game, with the game resetting with the ball in the center after each attempt. 
	 * Precondition: the canvas has been set up with the paddle, ball, and bricks. 
	 * Postcondition: the user can play the game with the set number of tries until all tries are used
	 * or until the user wins the game and there are no more bricks left. 
	 */
	private void playGameWithTries() {
		tries = NTURNS;	
		count = NBRICK_COLUMNS * NBRICK_ROWS;
		while (tries>0 && count!= 0) {
			playOneGame(); 				// allows the user to play until the ball goes through the bottom
			tries= tries -1; 			// counts a try 
			pause (3*DELAY);
			addBallToCenter(ball);		// resets the game with the ball in the middle 
		}
	}
	/* 
	 * This method implements one attempt of the game. The ball begins to move when the user 
	 * clicks the mouse, and then as long as the ball does not go through the bottom, and there 
	 * are bricks left, the user can use the paddle to bounce the ball upwards to remove bricks. 
	 * Precondition: The game is set up with the paddle, moving in response to the mouse, the ball 
	 * and the bricks. 
	 * Postcondition: The user can play one attempt of the game, and this method ends when the user 
	 * either does not hit the ball with the paddle and thus the ball goes through the bottom of the 
	 * screen, or when all the bricks have been removed. 
	 */
	private void playOneGame() {
		vx= rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy= VELOCITY_Y;
		count = NBRICK_COLUMNS * NBRICK_ROWS;
		waitForClick();								// allows the user to click to start the game 
		ball.move(vx, vy);
		pause(DELAY); 								// delay for animation 
		while (ball.getY()<= getHeight() && count>0) {
			bounceOffWalls();						// has the ball bounce off walls 
			respondAtCorners();						// checks the corners of the ball for collisions
			ball.move(vx, vy);						// updates the direction of the ball 
			pause(DELAY);							// delay for animation 
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
	
	/* 
	 * This method had the ball bounce off the top, left and right walls. 
	 * If the x coordinate of the ball is at the edge of the canvas, the ball will 
	 * "bounce" and move in the opposite horizontal direction. If the y coordinate of the ball is 
	 * at the edge of the canvas, the ball with "bounce and move in the opposite vertical direction, 
	 * but only at the top wall. 
	 * Precondition: The ball has been added to the canvas and returned for animation. 
	 * The canvas size has been set. 
	 * Postcondition: The ball will bounce off the left, right and top walls by reversing direction. 
	 */
	private void bounceOffWalls() {
		if (hitTopWall(ball)) {
			vy=-vy;
			ball.move(vx,vy);
			pause (DELAY);				// animation delay 
		}
		if (hitLeftWall(ball)||hitRightWall(ball)) {
			vx=-vx;
			ball.move(vx,vy);
			pause (DELAY);				// animation delay 
		}
	}
	
	/* 
	 * This method checks the corners of the ball, and respond should there be an object at 
	 * one of the four corners of the ball. These corners are the coordinates of the circumscribed 
	 * square around the ball. Only the bottom two corners are checked for a paddle collision, as the top 
	 * of the ball should not bounce off the paddle. All four corners are checked for a brick collision. 
	 * Precondition: the ball has been added to the canvas, and returned for animation, and is moving. 
	 * Postcondition: the ball will respond by changing direction upon hitting an object
	 * and removing the object if the object is a brick. 
	 */
	private void respondAtCorners() {
		if (getElementAt(ball.getX(), ball.getY())!=null && getElementAt(ball.getX(), ball.getY())!= paddle) {   // checks top left corner 
			respondToCollision(ball.getX(), ball.getY());
		}else if (getElementAt (ball.getX(), ball.getY()+2*BALL_RADIUS) != null) { 						// checks bottom left corner 
			respondToCollision( ball.getX(), ball.getY()+2*BALL_RADIUS);
		}else if (getElementAt (ball.getX()+2*BALL_RADIUS, ball.getY()) != null && getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()) != paddle) {    // checks top right corner
			respondToCollision(ball.getX()+2*BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS) != null) {			// checks bottom right corner 
			respondToCollision(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		}
	}
	
	/* 
	 * This method has the ball respond to a collision. This method is parameterized with the x and 
	 * y location at which the collision should be checked. If the collision is with a brick (and not 
	 * the paddle), the brick will be removed, and move in the opposite vertical direction. If the 
	 * collision is with the paddle, the ball will move in the opposite vertical direction, but not 
	 * remove the paddle. 
	 * Precondition: the ball is added to the canvas, returned for animation, and moving.
	 * Postcondition: if the ball has collided with a brick, or the paddle, the direction of the
	 * ball movement is changed, and if the object is a brick, the brick is removed. Note this method 
	 * does not actually move the ball in the new direction, but does update the vertical and horizontal 
	 * velocity variables. 
	 */
	private void respondToCollision (double x, double y) {
		GObject collider = getElementAt(x, y);
		if (collider != null && collider != paddle ) {		// if the ball hits a brick 
			vy=-vy;
			remove (collider);
			count=count-1;					// updates the count of bricks left 
			ball.move(vx, vy);
			pause(DELAY);
		} else if (collider ==paddle) {
			vy=-vy; 
		}
	}
	
	/* 
	 * This method displays the end labels depending on the ending outcome of the game. 
	 * If the user wins the game and there are no bricks remaining, then the label "Congrats 
	 * you've won Breakout! :)" is displayed. If all tries have been used and the ball has 
	 * dropped below the paddle, then the end label, "Game Over :(" is displayed. 
	 * Precondition: The game has been played by the user. 
	 * Postcondition: Depending on the outcome of the game, the proper end label is 
	 * displayed in the center of the screen and the ball has been removed from the screen. 
	 */
	private void endGame() {
		if (count==0) {   			// no bricks remaining 
			remove (ball);
			displayEndLabel("Congrats, you've won Breakout! :)");
		}else if (ball.getY()>=getHeight()||tries == 0) {
			remove(ball);
			displayEndLabel("GAME OVER :(");
		}
	}
	
	/* 
	 * This method adds a label with a string text to the center of the screen. Thus, the 
	 * text wanted can be inputted and the label will be added to the center of the canvas. 
	 * Precondition: None 
	 * Postcondition: a label with text is added to the center of the screen, with Sans Serif font, 
	 * size 15. 
	 */
	private void displayEndLabel(String nword) {
		GLabel label = new GLabel(nword);
		label.setFont ("SansSerif-15");
		add (label,getWidth()/2-label.getWidth()/2, getHeight()/2-label.getAscent()/2);
	}
}
 
