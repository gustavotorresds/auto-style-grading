/*
 * File: HeartBreakoutExtension.java
 * -------------------
 * Name: Tanya Watarastaporn
 * Section Leader: Nidhi Manoj
 * 
 * This file will eventually implement the game of HeartBreakout. In 
 * this file, the rainbow bricks for the game of HeartBreakout will first be
 * set up and the cupid image replaces the paddle. In the center of the rainbow 
 * bricks will be a pink heart made entirely of bricks. Then the player can play 
 * the game of HeartBreakout using for up to three turns before the results of the 
 * game are displayed with a special ending screen depending on if the player won or 
 * lost the game. Depending on whether the player manages to remove all the 
 * bricks before all of the turns are used, the player will either get a message 
 * stating he/she won or lost the game along with gifs and a looping sound effect. 
 * As the player plays the game, there will be background music, as well as sound 
 * effects when the brick is removed.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import acm.util.MediaTools;
public class HeartBreakoutExtension extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 12;

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
	public static final double PADDLE_HEIGHT = 3;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 15;

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

	// Declares the ball as an instance variable
	private GOval ball = null;

	// initializes the random generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// x velocity of the ball as an instance variable
	private double vx = 0;

	// y velocity of the ball as an instance variable
	private double vy = 0;

	// sets the bricksRemoved counter as initially 0
	private int bricksRemoved = 0;
	
	// sets the offset of the gif
	private static final int GIF_Y_OFFSET = 220;

	// sets the total amount of rows and bricks found in the bottom most
	// row of the heart arc
	private static final int NBRICKS_HEART_ARC = 5;
	
	// sets the total amount of rows and bricks found in the bottom most
	// row of the heart base
	private static final int NBRICKS_HEART_BASE = 10;
	
	// total number of pieces found in one heart arc
	private static final int HEART_ARC_BRICKS = 15;
	
	// total number of bricks found in the heart base
	private static final int HEART_BASE_BRICKS = 55;

	
	// calculates the number of bricks in the rainbow bricks by multiplying
	// the columns by rows
	private static final int RAINBOW_BRICKS = NBRICK_COLUMNS * NBRICK_ROWS;
	
	// calculates the number of total bricks in the game by adding the total rainbow 
	//bricks, and total heart arc bricks
	private static final int TOTAL_BRICKS =  2*HEART_ARC_BRICKS + HEART_BASE_BRICKS + RAINBOW_BRICKS;
	
	//private instance variable for the cupid image
	private GImage cupid = null;

	//private instance variable for the arrow image
	private GImage arrow = null;

	//private instance variable for the score label
	private GLabel scoreLabel = null;

	/**
	 * Method: run
	 * -------------------------------
	 * This run method will set the title and canvas size of HeartBreakout.
	 * The rainbow bricks will be set up first. Then the player can play 
	 * HeartbreakOut for a total of up to three turns until the results of the
	 * game are displayed, depending on if the number of bricks removed 
	 * equal the number of total bricks in the game. As the player plays the game
	 * background music will be played, and looped as the player continues to play.
	 * The music stops once the game ends, and the results of the game are displayed.
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		AudioClip backgroundMusic = MediaTools.loadAudioClip("backgroundMusic.AU");
		backgroundMusic.loop();
		backgroundMusic.play();
		setUpRainbowBricks();
		formHeartOfBricks();
		playHeartBreakout();
		displayHeartBreakoutResults(bricksRemoved, TOTAL_BRICKS);
		backgroundMusic.stop();


	}

	/**
	 * Method: create Rainbow Bricks
	 * ------------------------------------
	 * This method will create the rainbow bricks to be used in HeartBreakout.
	 * There will be two rows of bricks for each of the colors: red, 
	 * orange,yellow, green, and cyan. The bricks will be colored in that
	 * order and will be located in the center of the screen in the vertical 
	 * direction and will be offset from the top of the screen according to 
	 * the specified value of the BRICK_Y_OFFSET. The bricks will also be 
	 * separated from one another based on the specified constant BRICK_SEP.
	 */
	private void setUpRainbowBricks()	{
		for(int brickRow = 0; brickRow < NBRICK_ROWS; brickRow++)	{
			for(int brickNum = 0; brickNum < NBRICK_COLUMNS; brickNum++)	{
				double x = (getWidth() + BRICK_SEP)/2.0 - ((BRICK_WIDTH + BRICK_SEP)*(NBRICK_COLUMNS))/2.0 
						+ (BRICK_WIDTH + BRICK_SEP)*brickNum;
				double y = (BRICK_HEIGHT + BRICK_SEP)*(brickRow+1) + BRICK_Y_OFFSET - BRICK_SEP;
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);
				setColoredBricks(brickRow, 0, 11, brick, Color.MAGENTA);
				setColoredBricks(brickRow, 1, 2, brick, Color.RED);
				setColoredBricks(brickRow, 3, 4, brick, Color.ORANGE);
				setColoredBricks(brickRow, 5, 6, brick, Color.YELLOW);
				setColoredBricks(brickRow, 7, 8, brick, Color.GREEN);
				setColoredBricks(brickRow, 9, 10, brick, Color.CYAN);
			}
		}
	}
	
	/**
	 * Method: form Heart Of Bricks
	 * --------------------------------------
	 * This method will form the heart made of bricks that will be displayed
	 * in front of the rainbow bricks, and is centered in the middle of the screen
	 * in terms of width. It is offset to fit within the rainbow bricks area.
	 */
	private void formHeartOfBricks()	{
		formHeartBase();
		formPieceOfHeart(2.5*BRICK_WIDTH);
		formPieceOfHeart(-2.5*BRICK_WIDTH);
	}
	
	/**
	 * Method: form Heart Base
	 * ----------------------------------------
	 * This method will form the base of the heart made of bricks, forming a total 
	 * of 10 rows, with 10 total bricks in the bottom most row and then decreasing
	 * with each row down. The base will be colored pink and with a black outline. The
	 * heart base will also be centered in the rainbow bricks and screen.
	 */
	private void formHeartBase()	{
		for (int brickRow = NBRICKS_HEART_BASE; brickRow > 0; brickRow--)	{
			for (int brickNum = 0; brickNum < brickRow ; brickNum++) {
				double x = getWidth()/2.0 - (brickRow)*BRICK_WIDTH/2.0 + (brickNum*BRICK_WIDTH); ; // shifts initial brick
				double y = BRICK_HEIGHT * (NBRICKS_HEART_BASE - brickRow + 1) + 
						BRICK_Y_OFFSET + BRICK_SEP/2.0 + 6*BRICK_HEIGHT;
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setFillColor(Color.PINK);
				brick.setColor(Color.BLACK);
				add(brick);
			}
		}
	}
	
	/**
	 * Method: form Piece of Heart
	 * ------------------------------------
	 * @param shift
	 * This method will form one of the arcs that a heart is composed of.
	 * It will create a piece consisting of 5 rows and 5 in the bottom most row,
	 * and then decreasing with each row. The piece will be colored pink
	 * with a black outline, and will be offset by a certain height to be on top 
	 * of the heart base and within the rainbow bricks. The piece will also be 
	 * shifted horizontally depending on the value of "shift" so as to form the
	 * respective arc of the heart. 
	 */
	private void formPieceOfHeart(double shift)	{
		for (int brickRow = 0; brickRow < NBRICKS_HEART_ARC; brickRow++)	{
			for (int brickNum = 0; brickNum < (NBRICKS_HEART_ARC - brickRow); brickNum++) {
				double shiftDown = BRICK_Y_OFFSET + 2*BRICK_HEIGHT + BRICK_SEP/2.0;
				double x = getWidth()/2.0 - (NBRICKS_HEART_ARC - brickRow)*BRICK_WIDTH/2.0 
						+ (brickNum*BRICK_WIDTH);
				double y = (BRICK_HEIGHT * (NBRICKS_HEART_ARC - brickRow - 1)); 
				GRect brick = new GRect(x + shift, y + shiftDown, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setFillColor(Color.PINK);
				brick.setColor(Color.BLACK);
				add(brick);
			}
		}
	}

	/**
	 * Method: set Colored Bricks
	 * -----------------------------------
	 * This method will take in several parameters to color the rows 
	 * of bricks to their respective colors. The method takes in rows, 
	 * two row numbers, the brick, and color. Whatever row values are 
	 * passed into  the two row numbers will result in the bricks 
	 * corresponding to those row numbers being colored the same color 
	 * that is passed into the color parameter.
	 */
	private void setColoredBricks(int rows, int rowNum1, int rowNum2, GRect brick, Color color)	{
		if(rows == rowNum1 || rows == rowNum2)	{
			brick.setColor(color);
		}
	}

	/**
	 * Method: play HeartBreakout
	 * ----------------------------------
	 * This method will play the game of HeartBreakout for a total of three 
	 * turns. For each turn, the ball and paddle is reset, and the player 
	 * must click for the ball to move and therefore play the game. If the
	 * player were to remove all the bricks before all three turns have been
	 * used, this method will end
	 */
	private void playHeartBreakout()	{
		for(int turns = 0; turns < NTURNS; turns++)	{
			createBall();
			createArrow();
			createCupid();
			waitForClick();
			shootArrow();
			if(bricksRemoved == TOTAL_BRICKS)	break;
		}
	}

	/**
	 * Method: create Cupid
	 * ----------------------------------
	 * This method creates the cupid that is used to play HeartBreakout.
	 * The cupid is created using an image found online and is set 
	 * with the specified dimensions
	 */
	private void createCupid()	{
		cupid = new GImage("cupid.gif");
		cupid.setSize(65, 80);
		double x = (getWidth() - cupid.getWidth())/2.0;
		double y = getHeight() - 1.5*cupid.getHeight();
		cupid.setLocation(x, y);
		cupid.setColor(Color.WHITE);
		add(cupid);
	}

	/**
	 * Method: mouse Moved
	 * -----------------------------------
	 * This method will help allow the cupid to track the mouse 
	 * whenever the mouse is moved. If the mouse is moved to a 
	 * location that is outside the boundaries of the screen,
	 * the cupid will remain at the location where it touches the edge
	 * of the wall instead of moving beyond the walls
	 */
	public void mouseMoved(MouseEvent e)	{
		double mouseXCupid = e.getX() - cupid.getWidth()/2.0;
		double mouseYCupid = getHeight() - 1.5*cupid.getHeight();
		if(withinRightSideWall(mouseXCupid, cupid) && withinLeftSideWall(mouseXCupid))	{
			cupid.setLocation(mouseXCupid, mouseYCupid);
		} 
	}

	/**
	 * Method: create Ball
	 * -----------------------------------
	 * This method creates the ball that is used to play HeartBreakout.
	 * The ball is created with the specified constants and is 
	 * colored white so the arrow image is given the illusion of 
	 * being the one removing the bricks
	 */
	private void createBall()	{
		double x = (getWidth() - 2*BALL_RADIUS)/2.0;
		double y = (getHeight() - 2*BALL_RADIUS)/2.0;
		ball = new GOval(x, y ,2*BALL_RADIUS, 2*BALL_RADIUS);
		add(ball);
		ball.setFilled(true);
		ball.setColor(Color.WHITE);
	}

	/**
	 * Method: create Arrow
	 * ----------------------------------
	 * This method creates the arrow that is used to break bricks in
	 * HeartBreakout. The arrow is created with the specified constants 
	 * and is centered on the screen while taking into account the height
	 * of the ball the arrow image will be covering as the ball itself 
	 * actually collides with the bricks
	 */
	private void createArrow()	{
		arrow = new GImage("arrow.png");
		arrow.setSize(8, ball.getHeight()/1.5);
		double x = (getWidth() - arrow.getWidth())/2.0;
		double y = (getHeight() - arrow.getHeight())/2.0;
		arrow.setLocation(x, y);
		add(arrow);
	}

	/**
	 * Method: within Right Side Wall
	 * ----------------------------------
	 * This method is a boolean that takes in a double as a parameter.
	 * In this case the parameter will be the location of the mouse.
	 * The boolean will return true if the location of the mouse
	 * is less than the width of the screen minus the cupid width
	 */
	private boolean withinRightSideWall(double mouseX, GObject object)	{
		return mouseX < (getWidth() - object.getWidth());
	}

	/**
	 * Method: within Left Side Wall
	 * ----------------------------------
	 * This method is a boolean that takes in a double as a parameter.
	 * In this case the parameter will be the location of the mouse.
	 * The boolean will return true if the location of the mouse
	 * is greater than 0 for its x-coordinate
	 */
	private boolean withinLeftSideWall(double mouseX)	{
		return mouseX > 0;
	}

	/**
	 * Method: shoot Arrow
	 * ----------------------------------
	 * This method will set up the starting velocities for the ball
	 * and also keep changing the ball's x and y velocities according to
	 * its movements, which include collisions with certain objects
	 */
	private void shootArrow()	{
		setInitialVelocities();
		updateArrowAndBallMovements();
	}

	/**
	 * Method: update Arrow And Ball Movements
	 * -----------------------------------
	 * This method will update the arrow and ball movements while the 
	 * game is being played and the bricks have not yet all been removed.
	 * The boolean methods used for checking wall collisions are
	 * inspired by Chris's animation lecture. Once the arrow/ball hits the 
	 * bottom wall the ball, arrow and cupid will be removed and the method
	 * will break
	 */
	private void updateArrowAndBallMovements()	{
		while(bricksRemoved != TOTAL_BRICKS)	{
			arrow.move(vx, vy);
			ball.move(vx, vy);
			pause(DELAY);
			checkAndUpdateForCollisions();
			if(collideWithRightWall(ball) || collideWithLeftWall(ball)) vx = -vx;
			if(collideWithTopWall(ball)) vy = -vy;	
			if(collideWithBottomWall(ball))	{
				remove(ball);
				remove(arrow);
				remove(cupid);
				break;
			}
		}
	}

	/**
	 * Method: set Initial Velocities
	 * ----------------------------------
	 * This method will set the starting velocities of the ball.
	 * The value of the x-velocity will be randomized and range from
	 * the min and max values of the x-velocities 
	 */
	private void setInitialVelocities()	{
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
	}

	/**
	 * Method: check And Update For Collisions
	 * ----------------------------------
	 * This method will check for collisions and return a collider if 
	 * there is any and update the ball's movements accordingly. If the 
	 * collider is the paddle, the ball will move with the negative of the 
	 * absolute values of the vx and vy. If the determined to be a brick, 
	 * the ball will move in the negative vy direction and the brick will be 
	 * removed. The number of bricks removed will increase for each brick that 
	 * is removed. The ball's movements increase as the number of bricks removed
	 * are values have a remainder of 3 when divided by 10, which is defined by 
	 * the boolean isTroubleTime. The vy will increase by 2 and have a sharper 
	 * y-movement toward the bricks
	 */
	private void checkAndUpdateForCollisions()	{
		GObject collider = getCollidingObject(ball);
		if(collider == cupid)	{
			vy = -(Math.abs(vy));
		} else if(checkColliderIsBrick(collider)) {
			AudioClip popSound = MediaTools.loadAudioClip("popSound_m4a.AU");
			popSound.play();
			remove(collider);
			bricksRemoved++;
			displayCurrentScore(bricksRemoved);
			vx = -vx;
			vy = -vy;
			if(isTroubleTime(bricksRemoved))	{
				vy = vy + 2;
			}
		} 
	}
	
	/**
	 * Method: is Trouble Time
	 * -------------------------------------
	 * @param bricksRemoved
	 * @return bricksRemoved % 10 == 3
	 */
	private boolean isTroubleTime(int bricksRemoved)	{
		return bricksRemoved % 10 == 3;
	}

	/**
	 * Method: check Collider Is Brick
	 * -----------------------------------
	 * This boolean is true if the collider fits all of the constraints
	 * described below, which would exclude colliders that are the arrow,
	 * cupid, and scoreLabel. This would mean that the only collider left would
	 * be a brick.
	 */
	private boolean checkColliderIsBrick(GObject collider)	{
		return collider != null && collider != arrow && collider 
				!= cupid && collider != scoreLabel;
	}

	/**
	 * Method: collide With Right Wall
	 * -----------------------------------
	 * This boolean is true if the ball's x-location is going to 
	 * be past the right wall
	 */
	private boolean collideWithRightWall(GOval ball)	{
		return ball.getX() > (getWidth() - ball.getWidth());
	}

	/**
	 * Method: collide With Left Wall
	 * -----------------------------------
	 * This boolean is true if the ball's x-location is going to
	 * be past the left wall
	 */
	private boolean collideWithLeftWall(GOval ball)	{
		return ball.getX() < 0;
	}

	/**
	 * Method: collide With Bottom Wall
	 * ------------------------------------
	 * This boolean is true if the ball's y-location is going to
	 * be past the bottom wall
	 */
	private boolean collideWithBottomWall(GOval ball)	{
		return ball.getY() > (getHeight() - ball.getHeight());
	}

	/**
	 * Method: collide With Top Wall
	 * ------------------------------------
	 * This boolean is true if the ball's y-location is going to 
	 * be past the top wall
	 */
	private boolean collideWithTopWall(GOval ball)	{
		return ball.getY() < 0;
	}

	/**
	 * Method: get Colliding Object
	 * -------------------------------------
	 * This method will return the object the ball is colliding with
	 * due to the method that checks for a collision for each of the 
	 * ball's four corners
	 */
	private GObject getCollidingObject(GOval ball)	{
		double cornerX = ball.getX();
		double cornerY = ball.getY();
		return objectCollidingWithCorner(cornerX, cornerY);
	}

	/**
	 * Method: object Colliding With Corner
	 * -------------------------------------
	 * This method will return the element or object that is found at 
	 * each specified x and y location which is represented by whatever
	 * values are passed into the parameters for the method. The method 
	 * will check for elements at each corner but if there is nothing 
	 * found at any of the locations, the method will return null.
	 */
	private GObject objectCollidingWithCorner(double cornerX, double cornerY)	{
		if(getElementAt(cornerX, cornerY) != null)	{
			return getElementAt(cornerX, cornerY);
		} else if(getElementAt(cornerX + 2*BALL_RADIUS, cornerY) != null) {
			return getElementAt(cornerX + 2*BALL_RADIUS, cornerY);
		} else if(getElementAt(cornerX, cornerY + 2*BALL_RADIUS) != null)		{
			return getElementAt(cornerX, cornerY + 2*BALL_RADIUS);
		} else if(getElementAt(cornerX + 2*BALL_RADIUS, cornerY + 2*BALL_RADIUS) != null)		{
			return getElementAt(cornerX + 2*BALL_RADIUS, cornerY + 2*BALL_RADIUS);
		} else {
			return null;
		}
	}
	
	/**
	 * Method: display Current Score
	 * -------------------------------------
	 * @param bricksRemoved
	 * This method will create a score label by displaying a string whose value is
	 * the conversion of the value of bricksRemoved to a string. The score will be
	 * located at an x and y location that is below the cupid, and with each 
	 * brick that is removed, the score will be updated each time without any
	 * overlapping of the old score since the old score will be removed.
	 */
	private void displayCurrentScore (int bricksRemoved)	{
		String score = Integer.toString(bricksRemoved);
		scoreLabel = new GLabel(score);
		scoreLabel.setFont("Helvetica-24");
		double x = (getWidth() - scoreLabel.getX())/2.0;
		double y = (getHeight() - scoreLabel.getAscent());
		scoreLabel.setLocation(x, y);
		double labelX = scoreLabel.getX();
		double labelY = scoreLabel.getY();
		updateScore(labelX, labelY);
	}
	
	/**
	 * Method: update Score
	 * ----------------------------------------
	 * @param labelX
	 * @param labelY
	 * This method is responsible for updating the player's total score which
	 * is determined by the number of total bricks removed. If there is an old
	 * score located where the new score is supposed to appear with each brick
	 * that is removed, the old score label will be removed assuming it is not null at
	 * the locations labelX and labelY. At the same spot will then be the new 
	 * current score.
	 */
	private void updateScore(double labelX, double labelY)	{
		if(getElementAt(labelX, labelY) == null)	{
			add(scoreLabel);
		} else	{
			remove(getElementAt(labelX, labelY));
			add(scoreLabel);
		}
	}

	/**
	 * Method: display HeartBreakout Results
	 * ------------------------------------
	 * This method will remove all the of the objects left on the screen.
	 * If the number of bricks removed equal the total bricks, the screen
	 * will display an ending message that says the player has won. If not 
	 * all the bricks are removed by the end of the game, the screen will 
	 * display an ending message that says the player has lost
	 */
	private void displayHeartBreakoutResults(int bricksRemoved, int totalBricks)	{
		removeAll();
		if(bricksRemoved == totalBricks)	{
			endingScreen("YOU WON!", "winnerCupid.gif", "congrats.gif", "taDa.AU");
		} else	{
			endingScreen("YOU LOST!", "loserCupid.gif", "gameOver.gif", "sadTrombone.AU");
		}
	}
	
	/**
	 * Method: ending Screen
	 * ---------------------------------
	 * @param message
	 * @param gif1
	 * @param gif2
	 * @param audioClip
	 * This method will create a respective ending screen for the player 
	 * depending on the player's ending results. The ending screen will display 
	 * a certain message, two gifs which are specified by strings, a sound specified
	 * by the string audioClip
	 */
	private void endingScreen(String message, String gif1, String gif2, String audioClip)	{
		endingMessage(message);
		showEndingGif(gif1, 140, 170, 0);
		showEndingGif(gif2, 200, 170, GIF_Y_OFFSET);
		AudioClip endingSound = MediaTools.loadAudioClip(audioClip);
		endingSound.loop();
		endingSound.play();
	}

	/**
	 * Method: ending Message
	 * -------------------------------
	 * This method takes in a string as a parameter. The string, or
	 * message, that is passed into the method will be made into a 
	 * label that will be displayed on the screen. The message will
	 * be centered both vertically and horizontally.
	 */
	private void endingMessage(String message)	{
		GLabel label = new GLabel(message);
		label.setFont("Helvetica-36");
		double x = (getWidth() - label.getWidth())/2.0;
		double y = (getHeight() - label.getAscent())/2.0;
		label.setLocation(x, y);
		add(label);
	}
	
	/**
	 * Method: show Ending Gif
	 * -----------------------------------
	 * @param image
	 * @param width
	 * @param height
	 * @param yShift
	 * This method will display the gif to be used in the ending
	 * screen. The gif used will be specified by the String image,
	 * and the size of the gif will be specified by the width and height
	 * parameters. The gif will also be offset by the yShift parameter
	 * so it does not obscure the other gifs/graphics found in the 
	 * ending screen
	 */
	private void showEndingGif(String image,  double width, double height, double yShift)	{
		GImage endingGif = new GImage(image);
		endingGif.setSize(width, height);
		double x = (getWidth() - endingGif.getWidth())/2.0;
		double y = (getHeight())/2.0;
		endingGif.setLocation(x, y - yShift);
		add(endingGif);
	}
}
