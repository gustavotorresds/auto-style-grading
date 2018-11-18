/*
 * File: BreakoutExtension.java
 * Name: Danielle Tang
 * Section Leader: Semir Shafi
 * -----------------------------
 * This program is an extension of the Breakout program written
 * to implement the game Breakout as created by Steve Wozniak.
 * Before game play, there are some title screen words. The game
 * has opening and closing music and sound effects for losing,
 * gaining, and bouncing a ball. Before each turn, the game waits
 * for the player to click the mouse before starting the game. The
 * velocity of the ball in the y direction is 6.0 instead. The game
 * shows the player the amount of lives left in the bottom right
 * hand of the screen represented by hearts. The ball flashes when
 * it hits the bottom of the screen before disappearing.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

	// Dimensions of the screen
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

	// The ball's vertical velocity
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	GRect paddle = null;
	
	GOval ball = null;
		
	// Velocity of the ball in the x and y directions
	private double vx, vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Number of bricks on screen
	private int nBricks;
	
	// Tells the player to click on the mouse to start the game
	private GLabel start = new GLabel("click to fire ball");
	
	private GLabel turns = new GLabel("lives: ");
	
	public GImage life1 = new GImage("heart.png", 0, 0);
	
	public GImage life2 = new GImage("heart.png", 0, 0);
	
	public GImage life3 = new GImage("heart.png", 0, 0);

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		introScreen();
		setUpGame();
		addMouseListeners();
		for (int n = 0; n < NTURNS; n++) {
			if (nBricks == 0) {
				// Notifies the player via character string that they have won and the game is over
				youWin();
				break;
			} else {
				// Removes the rightmost heart from the screen
				if (n == 1) {
					remove(life3);
				}
				// Removes the middle heart from the screen
				if (n == 2) {
					remove(life2);
				}
				add(start);
				waitForClick();
				AudioClip newLife = MediaTools.loadAudioClip("newLife.mp3");
				newLife.play();
				remove(start);
				playGame();
			}
		}
		if (nBricks == 0) {
			// Notifies the player that they have won if they win on the last turn
			youWin();
		} else {
			// Removes the last heart
			remove(life1);
			// Notifies the player that they have lost
			youLose();
		}
	}
	
	private void introScreen() {
		AudioClip openingMusic = MediaTools.loadAudioClip("openingMusic.mp3");
		openingMusic.play();
		GLabel welcome = new GLabel("WELCOME TO BREAKOUT");
		welcome.move(getWidth() / 2 - welcome.getWidth() / 2 - 30, getHeight() / 2 + welcome.getHeight() / 2);
		welcome.setFont(SCREEN_FONT);
		add(welcome);
		pause(3500);
		remove(welcome);
		GLabel play = new GLabel("LET'S PLAY");
		play.move(getWidth() / 2 - play.getWidth() / 2, getHeight() / 2 + play.getHeight() / 2);
		play.setFont(SCREEN_FONT);
		add(play);
		pause(2000);
		remove(play);
	}
	
	private void setUpGame() {
		makeBricks();
		makePaddle();
		makeTurns();
		start.move(getWidth() / 2 - start.getWidth() / 2, getHeight() / 2 - PADDLE_Y_OFFSET / 2 + start.getHeight() / 2);
	}
	
	private void makeBricks() {
		double y = BRICK_Y_OFFSET;
		// Makes 10 brick rows
		for (int i = 0; i < NBRICK_ROWS; i++) {
			double x = BRICK_SEP;
			// Makes 10 bricks in each row
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				colorBrick(i, brick);
				add(brick);
				x = x + BRICK_WIDTH + BRICK_SEP;
			}
			y = y + BRICK_HEIGHT + BRICK_SEP;
		}
		nBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	}
	
	/**Makes bricks rainbow colored (red, orange, yellow, green, cyan) with
	each color constituting two rows of bricks. */
	private void colorBrick(int i, GRect brick) {
		if (i >= 0 && i <= 1) {
			brick.setColor(Color.RED);
		}
		if (i >= 2 && i <= 3) {
			brick.setColor(Color.ORANGE);
		}
		if (i >= 4 && i <= 5) {
			brick.setColor(Color.YELLOW);
		}
		if (i >= 6 && i <= 7) {
			brick.setColor(Color.GREEN);
		}
		if (i >= 8 && i <= 9) {
			brick.setColor(Color.CYAN);
		}
	}
	
	private void makePaddle() {
		paddle = setUpPaddle();
		addPaddleToCenter();
		addMouseListeners();
	}
	
	private GRect setUpPaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	
	private void addPaddleToCenter() {
		double x = getWidth() / 2 - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}
	
	/** This method makes the paddle follow the position of the mouse on the screen */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET;
		// Sets boundaries for the position of the paddle so it does not go off-screen
		if (x >= 0 && x <= getWidth() - PADDLE_WIDTH && paddle != null) {
			paddle.setLocation(x,y);
		}
	}
	
	private void makeTurns(){
		turns.move(getWidth() - turns.getWidth() * 3, getHeight() - 5);
		add(turns);
		makeLives(); 
	}
	
	private void makeLives() {
		life1.setSize(20, 20);
		life1.setLocation(getWidth() - 65, getHeight() - 18);
		add(life1);
		life2.setSize(20, 20);
		life2.setLocation(getWidth() - 43, getHeight() - 18);
		add(life2);
		life3.setSize(20, 20);
		life3.setLocation(getWidth() - 20, getHeight() - 18);
		add(life3);
	}
	
	private void youWin() {
		GLabel win = new GLabel("YOU WIN!");
		win.move(getWidth() / 2 - win.getWidth() / 2, getHeight() / 2 - PADDLE_Y_OFFSET / 2 + win.getHeight() / 2);
		win.setFont(SCREEN_FONT);
		add(win);
		AudioClip youWin = MediaTools.loadAudioClip("youWin.mp3");
		youWin.play();
	}
	
	private void youLose() {
		GLabel lose = new GLabel("GAME OVER");
		lose.move(getWidth() / 2 - lose.getWidth() / 2, getHeight() / 2 - PADDLE_Y_OFFSET /2 + lose.getHeight() / 2);
		lose.setFont(SCREEN_FONT);
		add(lose);
		AudioClip gameOver = MediaTools.loadAudioClip("gameOver.mp3");
		gameOver.play();
	}
	
	private void playGame() {
		makeBall();
		// Gives player time before each turn (including the beginning) to react to surroundings
		pause(1200);
		moveBall(ball);
	}
	
	/** Sets initial location of ball on screen */
	private void makeBall() {
		ball = new GOval(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}
	
	private void moveBall(GOval ball) {
		vx = rgen.nextDouble(1.0, 3.0);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		// Changes ball direction when it collides against a surface in accordance with
		// the principle that the angle of incidence equals the angle of reflection
		while (true) {
			checkForCollider(bounceClip);
			if (nBricks == 0) {
				break;
			}
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				bounceClip.play();
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				bounceClip.play();
				vy = -vy;
			}
			// If ball hits bottom wall, a new turn begins
			if(hitBottomWall(ball)) {
				AudioClip loseBall = MediaTools.loadAudioClip("loseBall.mp3");
				loseBall.play();
				flashBall();
				remove(ball);
				break;
			}
			ball.move(vx, vy);
			pause(DELAY);
		}
	}
	
	private void checkForCollider(AudioClip bounceClip) {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			bounceClip.play();
			vy = -vy;
		} else  if (collider != null && collider != life1 && collider != life2 && collider != life3 && collider != turns) {
			bounceClip.play();
			vy = -vy;
			remove(collider);
			nBricks--;
		}
	}
	
	/** Checks each of the four corners of the ball for collision and
	 * returns the object at the collision if there is a collision*/
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		GObject corner1 = getElementAt(x, y);
		GObject corner2 = getElementAt(x + BALL_RADIUS * 2, y);
		GObject corner3 = getElementAt(x, y + BALL_RADIUS * 2);
		GObject corner4 = getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2);
		if (corner1 != null) {
			return corner1;
		} else if (corner2 != null) {
			return corner2;
		} else if (corner3 != null) {
			return corner3;
		} else if (corner4 != null) {
			return corner4;
		} else {
			return null;
		}
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - ball.getHeight();
	}
	
	/** Flashes the ball on the screen when it hits the bottom wall*/
	private void flashBall() {
		for (int i = 0; i < 4; i++) {
			pause(100);
			remove(ball);
			pause(100);
			add(ball);
		}
		pause(100);
	}
}