/*
 * File: Breakout.java
 * -------------------
 * Name: Alex Elifas
 * Section Leader: Julia Daniel
 * ----------------------------
 *  This program makes a breakout game and allows 
 *  the user to play the game by moving the paddle 
 *  from the left to the right of the graphic windows.
 *  The game is setup at the center of the graphic windows.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import static java.awt.Color.*;

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
	public static final int BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity in pixels / second.
	public static final double VELOCITY_Y = 300.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	//  Instant variables 
	private GRect paddle;
	private GOval ball;
	private double vx;
	private double vy;
	private int score;
	private boolean cheating = false;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int numberBricks;
	private int numberOfLives;

	// Removes all the bricks before resetting the game
	private ArrayList<GRect> bricks = new ArrayList<>();
	private GLabel statusMessage = new GLabel("");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		playTheGame();
	}

	/* Method: Play The Game
	 * ---------------------
	 * This method creates the game, adds it to
	 * the screen and allows user to play it. If 
	 * the ball doesn't bounce on the paddle and instead
	 * on the bottom wall of the screen. The game exits,
	 * displays the score and prompts the user to restart the game. 
	 * If the user plays the game until the very last brick, it displays 
	 * a congratulation note.
	 */
	private void playTheGame() {
		makeTheGame();
		resetGame();
		while (true) {
			int value = ballSimulation();
			if (value == 1) {
				win();
			} else if (value == 2) {
				--numberOfLives;
				if (numberOfLives <= 0) {
					lose();
				} else {
					loseALife();
				}
			} else {
				pause(DELAY);
			}
		}
	}

	/* Method: Make The Game
	 * ---------------------
	 * This method adds all the components necessary 
	 * for the game setup ( bricks,paddle and wall)
	 * It also dictates how the ball moves and interacts 
	 * with different objects on the graphic window.
	 * It also keeps track of the score and displays it on the 
	 * graphic window.
	 * 
	 */
	private void makeTheGame(){
		createThePaddle();
		createBall();
		addStatusLabel();
	}

	/* Method: Create Paddle
	 * ---------------------
	 * This method creates the paddle and adds it at the 
	 * bottom of the window.The GRect is declared as an instant 
	 * variable preventing it from being created all the time 
	 * the paddle moves.
	 */
	private void createThePaddle() {
		double x = getWidth()/2 - PADDLE_WIDTH;
		double y = getHeight()-(PADDLE_Y_OFFSET+ PADDLE_HEIGHT/2);
		paddle = new GRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	/* Method: Create Ball
	 *  -------------------
	 * This method creates the ball at the center of the graphic 
	 * window.The GOval is declared as an instant variable preventing 
	 * it from being created all the time the ball moves.
	 */
	private void createBall() {
		double d = 2*BALL_RADIUS;
		ball = new GOval(d,d);
		ball.setFilled(true);
		add(ball);
	}
	/* Method: Add Status Label
	 * ------------------------
	 * This method adds the score status at the bottom of the 
	 * graphic window just below the paddle.
	 * 
	 */
	private void addStatusLabel() {
		statusMessage.setFont("Helvetica-24");
		add(statusMessage);
	}

	/* Method: Reset Game
	 * ------------------
	 * This method reconstruct the entire game.
	 * It removes all remaining bricks and the ball in case the user 
	 * loses the game, this prevents new bricks from being added on top 
	 * of the remaining ones.
	 * Dictates when the game starts which is after the user clicks on
	 * the graphic window.
	 */

	private void resetGame() {
		double widthBricks = NBRICK_COLUMNS * BRICK_WIDTH +
				(NBRICK_COLUMNS - 1) * BRICK_SEP;
		setUpBricks((int) ((getWidth() - widthBricks) / 2), BRICK_Y_OFFSET);
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball.setLocation(x, y);
		numberBricks = NBRICK_ROWS * NBRICK_COLUMNS;
		updateScore(0);
		numberOfLives = 3;
		setStatus("Click to start playing!");
		waitForClick();
		createBallMovement();
	}
	/* Method: Set Up Bricks
	 * ---------------------
	 * Removes old bricks before adding new ones.
	 * Adds new colored rows of bricks on the graphic window.
	 * 
	 */
	private void setUpBricks(int x, int y) {
		for (GRect rect : bricks) {
			remove(rect);
		}
		bricks.clear();

		Color[] colors = new Color[] {RED, ORANGE, YELLOW, GREEN, CYAN};
		for (int r = 0; r < NBRICK_ROWS; r++) {
			Color color = colors[(r*colors.length)/NBRICK_ROWS];// Works for any number of Bricks Rows
			addRow(x, (int) Math.round(r*(BRICK_HEIGHT+BRICK_SEP)) + y, color);
		}
	}

	/* Method: Add Row
	 * ---------------
	 * Adds color filled bricks to the graphic windows.
	 * 
	 */
	private void addRow(int x, int y, Color color) {
		for (int c=0; c< NBRICK_COLUMNS; c++) {
			GRect brick = new GRect(x+(BRICK_WIDTH+BRICK_SEP)*c,y,BRICK_WIDTH,BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			bricks.add(brick);
			add(brick);
		}
	}

	/* Method: Update Score
	 * --------------------
	 * Updates the score status at the bottom every time the 
	 * ball breaks a brick.
	 * 
	 */
	private void updateScore(int newScore) {
		score = newScore;
		setStatus("Score: "+newScore);
	}

	/* Method: Create Ball Movement
	 * --------------------------
	 *  Dictates how the ball moves, its speed and change
	 *  in the direction of the velocity vector.
	 */
	private void createBallMovement() {
		vx = rgen.nextDouble(100.0, 300.0) * DELAY / 1000.0;
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		vy = VELOCITY_Y * DELAY / 1000.0;
	}

	/* Method: Ball Simulation
	 * -----------------------
	 * This methods defines how the ball interacts with the walls of 
	 * the graphic window, the paddle and the bricks.
	 * The ball bounces on the paddle and all the walls except the bottom wall. 
	 * If the ball hits the bottom wall, the user loses a life.
	 * The ball breaks the bricks when it bounces on them. 
	 * Extension:
	 * ----------
	 * It keeps track of the score points. 
	 */
	private int ballSimulation() {

		ball.move(vx, vy);
		if (cheating) {
			paddle.setLocation(ball.getCenterX() - paddle.getWidth()/2, paddle.getY());
		}
		if (ball.getX() < 0 || ball.getX() + ball.getWidth() > getWidth()) {
			vx *= -1;
		}
		if (ball.getY() < 0) {
			vy *= -1;
		}
		if(ball.getY() > getHeight()) {
			return 2;
		}

		GPoint[] points = {
				new GPoint(ball.getX(), ball.getY()),
				new GPoint(ball.getX() + ball.getWidth(), ball.getY()),
				new GPoint(ball.getX(), ball.getY() + ball.getHeight()), 
				new GPoint(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight())
		};

		for (GPoint point : points) {
			GObject obj = getElementAt(point);
			if (obj instanceof GRect) { // Removes all GRect objects except the paddle
				vy *= -1;
				if (obj != paddle) {
					remove(obj);
					updateScore(score + 1);
					numberBricks -= 1;
					if (numberBricks == 0 ) {
						return 1; // User wins the game.
					}
				} else {
					ball.move(0, -ball.getHeight()/2);
				}
				break;
			}
		}
		return 0;
	}

	/* Method: Win
	 * ---------------
	 * This method adds a congratulation message on 
	 * the graphic window and prompts the user to play again.
	 * If the user decides to play again, it resets the 
	 * entire game setup.
	 * 
	 */
	private void win() {
		setStatus("Hooray!!!, You won! Score: " + score);
		waitForClick();
		setStatus("Click on the graphic window to play again");
		waitForClick();
		resetGame();
	}

	/* Method: Lose A Life
	 * -------------------
	 * This method notifies the user the number of lives
	 * still remaining. 
	 * It doesn't reset the game, rather recreates the 
	 * ball only. The score  and number of bricks will be 
	 * carried over from the previous life.
	 * 
	 */
	private void loseALife() {
		setStatus("You still have "+ numberOfLives + (numberOfLives <= 1 ? " life" : " lives"));
		waitForClick();
		setStatus("Click on the window to continue playing");
		ball.setLocation((getWidth()-ball.getWidth())/2, (getHeight() - ball.getHeight())/2);
		waitForClick();
		updateScore(score);
		createBallMovement();
	}

	/* Method: Lose
	 * ---------------
	 * This method prompts the user he/she  lost the game.
	 * If the user decides to play again, it resets the entire
	 *  game setup.
	 * 
	 */
	private void lose() {
		setStatus("You lost! Score: "+ score);
		waitForClick();
		resetGame();
	}

	/* Extension:
	 * ---------
	 * This method listens if R key is being pressed,
	 * if R key is pressed, it allows the user to cheat
	 * the game.
	 * Pressing R key automatically allows the paddle to 
	 * know the coordinates of the ball, therefore the
	 * ball never bounces on the bottom wall. 
	 * 
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_R) {
			cheating = !cheating;
		}
	}

	/* Adds labels on the graphic windows.
	 * 
	 */

	private void setStatus(String message) {
		statusMessage.setLabel(message);
		statusMessage.setLocation((getWidth() - statusMessage.getWidth())/2, paddle.getBottomY() + 20);
	}

	/* If R key is pressed at any time in the game,
	 * This method sends the coordinate of the ball 
	 * to the paddle allowing the user to cheat the game. 
	 * 
	 */
	public void mouseMoved(MouseEvent e) {
		if (paddle == null) return;
		double LastX = e.getX()-PADDLE_WIDTH/2;
		double LastY = getHeight()-(PADDLE_Y_OFFSET+ PADDLE_HEIGHT/2);
		if (!cheating) {
			paddle.setLocation(LastX,LastY);
		}
	}
}
