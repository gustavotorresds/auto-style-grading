/*
 * File: Breakout.java
 * -------------------
 * Name: Alex Hurtado
 * Section Leader: Garrick Fernandez
 * Date: 02/07/18
 * 
 * This program is meant to emulate the classic arcade game "Breakout." The player controls a paddle and is tasked with
 * destroying all the bricks by bouncing a ball off the paddle to destroy each brick. Click to launch the ball.
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
	public static final double VELOCITY_X_MAX = 3.0;
	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 600.0 / 60.0;
	// Number of turns 
	public static final int NTURNS = 3;

	// Instance Variables
	private GRect paddle = null;
	private GOval bullet = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// Run Method
	public void run() {
		setUpGraphics();
		int livesLeft = NTURNS;
		int remainingBricks = NBRICK_COLUMNS * NBRICK_ROWS;
		// The following animation loop runs while the user still has lives remaining and hasn't destroyed all the bricks
		while (livesLeft > 0 && remainingBricks > 0 ) {
			bullet.move(vx, vy);
			if (isWallCollision()) { // Checks if the bullet has collided with a side wall
				vx = -vx; // Bullet bounces horizontally
			} else if (bullet.getY() <= 0) { // Checks if the bullet has collided with the top wall
				vy = -vy; // Bullet bounces vertically
			} else if (bullet.getY() >= getHeight()) { // Checks if the bullet has exited through the bottom side of the canvas
				// In this case, the user has failed to prevent the bullet from falling past the paddle
				livesLeft--; // Removes a life
				if (livesLeft == 0) { // Checks for when the user has ran out of lives
					makeLabel("You've ran out of lives! Please play again!");
				}
				remove(bullet);
				setUpBullet(bullet);
				vx = 0; // Makes the bullet stationary so the user can restart the game
				vy = 0;
			}
			GObject collider = getCollidingObject(); // Checks for and returns colliding object
			if (collider == paddle) { // Checks if the colliding object is the paddle
				// The below is a fix for "sticky paddle." By coding such that the bullet will only collide with the paddle
				// if the the bullet is moving downwards ensures that the bullet cannot get stuck bouncing up and down
				// within the paddle itself
				if (vy > 0) { // The bullet must be moving downward to collide with the paddle
					bounceClip.play();
					vy = -vy; // Bounces vertically
				}
			} else if (collider != null) { // Checks if the colliding object is any other object, notably a brick
				bounceClip.play();
				vy = -vy; // Bounces vertically
				remove(collider); 
				remainingBricks--;
				if (remainingBricks == 0) { // Checks if the user has won by destroying all the bricks
					makeLabel("Congratulations! You've won! Please play again!");
				}
			}
			pause(DELAY);
		}

	}

	/**
	 * Method: Mouse Moved
	 * -------------------
	 * This method is called whenever the mouse is moved. This method moves the paddle such that the center of the
	 * paddle follows the user's mouse. This method also prevents the paddle from moving off the canvas.
	 */
	public void mouseMoved(MouseEvent e) {
		double canvasWidth = getWidth();
		double xPosition = e.getX() - (PADDLE_WIDTH / 2.0); // Horizontal adjustment for paddle position
		double yOffsetPaddle = getHeight() - PADDLE_Y_OFFSET;
		if (xPosition <= 0) { // Checks to see if the paddle goes off the left side of the canvas
			paddle.setLocation(0, yOffsetPaddle);
		} else if (xPosition + PADDLE_WIDTH >= canvasWidth) { // Checks to see if the paddle goes off the right side of the canvas
			paddle.setLocation(canvasWidth - PADDLE_WIDTH, yOffsetPaddle);
		} else {
			paddle.setLocation(xPosition, yOffsetPaddle); // Otherwise, the paddle's center is placed at the user's mouse
		}
	}

	/**
	 * Method: Mouse Clicked
	 * ---------------------
	 * This method is called whenever the mouse is clicked. This method gives the bullet its initial velocity values,
	 * launching it towards the bottom of the canvas. The horizontal velocity will be a random value between 
	 * VELOCITY_X_MIN and VELOCITY_X_MAX and may randomly be positive or negative. The vertical velocity is given by 
	 * VELOCITY_Y.
	 */
	public void mouseClicked(MouseEvent e) {
		double xCenteredBullet = (getWidth() / 2.0) - BALL_RADIUS;
		double yCenteredBullet = (getHeight() / 2.0) - BALL_RADIUS;
		// The following `if` statement checks to see if the bullet is at the center of the canvas before this method 
		// assigns the bullet values for its velocity. This check prevents the bullet from being assigned new velocity
		// values while it is already in play. That is, the user is prevented from clicking to randomly change the 
		// velocity of the bullet when the bullet has already be launched.
		if (bullet.getX() == xCenteredBullet && bullet.getY() == yCenteredBullet) {
			vy = VELOCITY_Y;
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); // randomly chooses a value for vs
			if (rgen.nextBoolean(0.5)) { // randomly decides if vx is initially positive or negative
				vx = -vx;
			}
		}
	}

	/**
	 * Method: Set Up Graphics
	 * -----------------------
	 * This method sets up all the graphics of Breakout. In particular, this method sets the size of the canvas and
	 * creates and adds the paddle, bullet, and bricks to the canvas.
	 */
	private void setUpGraphics() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		makePaddle();
		setUpPaddle(paddle);
		makeBullet();
		setUpBullet(bullet);
		setUpBricks();
	}

	/**
	 * Method: Make Paddle
	 * -------------------
	 * This method initializes a new GRect paddle at the top right corner of the canvas with width PADDLE_WIDTH and
	 * height PADDLE_HEIGHT. This method, however, does not add the paddle to the canvas.
	 * -------------------
	 * @return returns the GRect object that represents the paddle
	 */
	private GRect makePaddle() {
		paddle = new GRect(0, 0, PADDLE_WIDTH, PADDLE_HEIGHT);
		return paddle;
	}

	/**
	 * Method: Set Up Paddle
	 * ---------------------
	 * This method sets the initial location of the paddle to be horizontally centered and PADDLE_Y_OFFSET above the 
	 * bottom of the canvas. This method also sets the fill of the paddle to be black and adds the paddle to the canvas.
	 * ---------------------
	 * @param paddle is the GRect object representing the paddle created in makePaddle()
	 */
	private void setUpPaddle(GRect paddle) {
		double xCenteredPaddle = (getWidth() - PADDLE_WIDTH) / 2.0;
		double yOffsetPaddle = getHeight() - PADDLE_Y_OFFSET;
		paddle.setLocation(xCenteredPaddle, yOffsetPaddle);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	/**
	 * Method: Make Bullet
	 * -------------------
	 * This method initializes a new GOval bullet at the top right corner of the canvas with a diameter of
	 * two times BALL_RADIUS. This method, however, does not add the bullet to the canvas.
	 * -------------------
	 * @return returns the GOval object that represents the bullet
	 */
	private GOval makeBullet() {
		double ballDiameter = BALL_RADIUS * 2;
		bullet = new GOval(0, 0, ballDiameter, ballDiameter);
		return bullet;
	}

	/**
	 * Method: Set Up Bullet
	 * ---------------------
	 * This method sets the bullet's initial location at the center of the canvas. This method also sets the fill of the
	 * bullet to be black and adds the bullet to the canvas.
	 * ---------------------
	 * @param bullet is the GOval object representing a bullet created in makeBullet()
	 */
	private void setUpBullet(GOval bullet) {
		double xCenteredBullet = (getWidth() / 2) - BALL_RADIUS;
		double yCenteredBullet = (getHeight() / 2) - BALL_RADIUS;
		bullet.setLocation(xCenteredBullet, yCenteredBullet);
		bullet.setFilled(true);
		bullet.setColor(Color.BLACK);
		add(bullet);
	}
	
	/**
	 * Method: Set Up Bricks
	 * ---------------------
	 * This method creates NBRICK_ROWS rows of bricks centered horizontally and placed BRICK_Y_OFFSET from the 
	 * top of the canvas. Every two rows of bricks are also colored differently, depending on colorBrickRow().
	 */
	private void setUpBricks() {
		double halfCanvasWidth = getWidth() / 2.0;
		double xOffsetBricks = (NBRICK_COLUMNS / 2.0) * BRICK_WIDTH; // Adjustment for space occupied by bricks
		double xOffsetSpaces = ((NBRICK_COLUMNS - 1) / 2.0) * BRICK_SEP; // Adjustment for space occupied by the separation between bricks
		double xOffsetRow = halfCanvasWidth - xOffsetBricks - xOffsetSpaces; // Final horizontal adjustment
		double yOffsetRow = BRICK_HEIGHT + BRICK_SEP;
		for (int i = 0; i < NBRICK_ROWS; i++) {
			drawBrickRow(xOffsetRow, BRICK_Y_OFFSET + i * yOffsetRow, colorBrickRow(i));
		}
	}
	
	/**
	 * Method: Draw Brick Row
	 * ----------------------
	 * This method creates a row of NBRICK_COLUMNS bricks at the given location filled with the given color.
	 * ----------------------
	 * @param x is the x-coordinate of where the row of bricks will begin to be drawn
	 * @param y is the y-coordinate of where the row of bricks will begin to be drawn
	 * @param color is the color that the row of bricks will be filled with
	 */
	private void drawBrickRow(double x, double y, Color color) {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			drawBrick(x + i * (BRICK_WIDTH + BRICK_SEP), y, color);
		}
	}

	/**
	 * Method: Draw Brick
	 * ------------------
	 * This method creates a GRect representing a brick at the given location filled with the given color.
	 * The brick is then added to the canvas.
	 * ------------------
	 * @param x is the x-coordinate of where the rectangular brick will be drawn
	 * @param y is the y-coordinate of where the rectangular brick will be drawn
	 * @param color is the color that the brick will be filled with
	 */
	private void drawBrick(double x, double y, Color color) {
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);
	}
	
	/**
	 * Method: Color Brick Row
	 * -----------------------
	 * This method takes the count of the `for` loop in setUpBricks() and returns the appropriate color to color the
	 * brick row.
	 * -----------------------
	 * @param i is the count in the `for` loop of setUpBricks(). `i` also indicates that the row is the `i`th row.
	 * @return returns a color to fill the brick row with
	 */
	private Color colorBrickRow(int i) {
		if (i <= 1) { // The first two rows will be red
			return Color.RED;
		} else if (i <= 3) { // The third and fourth rows will be orange
			return Color.ORANGE;
		} else if (i <= 5) { // The fifth and sixth rows will be yellow
			return Color.YELLOW;
		} else if (i <= 7) { // The seventh and eighth rows will be green
			return Color.GREEN;
		} else { // Otherwise, all other rows will be cyan
			return Color.CYAN;
		}
	}

	/**
	 * Method: Is Wall Collision
	 * -------------------------
	 * This method checks if the bullet is colliding with the left or right walls
	 * -------------------------
	 * @return returns `true` if a condition is satisfied and `false` if neither condition is satisfied
	 */
	private boolean isWallCollision() {
		return (bullet.getX() <= 0 || bullet.getX() + (BALL_RADIUS * 2) >= getWidth());
		// The first condition checks if the bullet is colliding with the left wall
		// The second condition checks if the bullet is colliding with the right wall
	}
	
	/**
	 * Method: Make Label
	 * ------------------
	 * This method creates a label with the given text of size 14 in Sans Serif. This label is centered horizontally
	 * and placed 75% down the length of the canvas.
	 * ------------------
	 * @param string is the text you wish to be printed by the label
	 */
	private void makeLabel(String string) {
		GLabel label = new GLabel(string);
		label.setFont("SansSerif-14");
		double labelWidth = label.getWidth();
		double xOffsetLabel = (getWidth() - labelWidth) / 2;
		double yOffsetLabel = getHeight() * 0.75;
		add(label, xOffsetLabel, yOffsetLabel);
	}

	/**
	 * Method: Get Colliding Object
	 * ----------------------------
	 * This method finds the object that the bullet is colliding with at any of the bullet's 4 corner.
	 * ----------------------------
	 * @return returns the GObject colliding with either of the 4 corners or null if there is no collision
	 */
	private GObject getCollidingObject() {
		double leftX = bullet.getX();
		double topY = bullet.getY();
		double rightX = leftX + (BALL_RADIUS * 2);
		double bottomY = topY + (BALL_RADIUS * 2);
		if (getElementAt(leftX, topY) != null) { // Checks if there is a collision at top left corner
			return getElementAt(leftX, topY);
		} else if (getElementAt(rightX, topY) != null) { // Checks if there is a collision at the top right corner
			return getElementAt(rightX, topY);
		} else if (getElementAt(leftX, bottomY) != null) { // Checks if there is a collision at the bottom left corner
			return getElementAt(leftX, bottomY);
		} else if (getElementAt(rightX, bottomY) != null) { // Checks if there is a collision at the bottom right corner
			return getElementAt(rightX, bottomY);
		} else { // If all of the previous statements fail, then there is no collision occurring
			return null; // So this method returns null
		}
	}

}
