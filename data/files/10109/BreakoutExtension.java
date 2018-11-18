/*
 * File: Breakout.java


 * -------------------
 * Name: Shiriel King Abramson
 * Section Leader: Peter Maldonado
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle = null;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy;
	private int turn = 0;
	private int bricksGone = 0;
	private GLabel begin = new GLabel("CLICK TO BEGIN");
	private int paddleBounce = 0;
	private int score = 0;
	private GLabel scorekeeper = new GLabel ("Your score: " + score);
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//Sets up bricks, paddle, and velocity
		setUpBricks();
		paddle = makePaddle();
		addMouseListeners();	
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		
		//displays introductory message
		clickToBegin();
		scoreKeeper();
		//animation loop
		while (true) {
			moveBall();
			
			//pauses animation loop
			pause(DELAY);
			
			//tracks conditions for game to end
			if (turn == 3) {
				gameOver();
				break;
			}
			if (bricksGone == 100) {
				youWon();
				break;
			}
		}
		
	}
		
	/** Private method: setUpBricks
	 * ---
	 * Makes initial brick set-up */
		private void setUpBricks() {
			for (int row = 0; row < NBRICK_ROWS; row++) {
				for (int col = 0; col < NBRICK_COLUMNS; col++) {
					double x = (getWidth() / 2) - (5 * BRICK_WIDTH) - (4 * BRICK_SEP) + (col * BRICK_WIDTH) + (col * BRICK_SEP);
					double y = BRICK_Y_OFFSET + (row * BRICK_HEIGHT) + (row * BRICK_SEP);
					GRect brick = new GRect (x,y, BRICK_WIDTH, BRICK_HEIGHT);
					brick.setFilled(true);
					add(brick);
					if (row < 2) {
						brick.setColor(Color.RED);
					}
					if (row >= 2 && row < 4) {
						brick.setColor(Color.ORANGE);
					}
					if (row >= 4 && row < 6) {
						brick.setColor(Color.YELLOW);
					}
					if (row >= 6 && row < 8) {
						brick.setColor(Color.GREEN);
					}
					if (row >= 8) {
						brick.setColor(Color.CYAN);
					}
				}
			}
		}
		
		/**Private method: makePaddle
		 * ----
		 * makes paddle */
		private GRect makePaddle() {
			double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
			paddle = new GRect (getCenterX(), y, PADDLE_WIDTH, PADDLE_HEIGHT);
			paddle.setFilled(true);
			add(paddle);
			return paddle;
		}
		
		/**Private method: mouseMoved
		 * ----
		 * sets paddle to move with mouse*/
		public void mouseMoved (MouseEvent e) {
			int mouseX = e.getX();
			if (mouseX >= 0 && mouseX <= getWidth() - PADDLE_WIDTH) {
				paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			} else if (mouseX < 0) {
				paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			} else if (mouseX > getWidth() - PADDLE_WIDTH) {
				paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			}
		}
		

	
		/**Private method: mouseClicked
		 * ----
		 * mouse click unleashes ball*/
		public void mouseClicked (MouseEvent e) {
			remove(begin);
			if (ball==null) {
				ball = new GOval (getCenterX() - BALL_RADIUS, getCenterY() - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
				ball.setFilled(true);
				add(ball);
			}
		}
		
		/**Private method: moveBall
		 * ----
		 * ball moves. Bounces if hits wall or ceiling. Resets to center if hits ground.*/
		private void moveBall() {
			if (ball != null) {
				if (ball.getY() <= 0) {
					moveDown(); 
				} else if (ball.getX() <= 0) {
					moveRight();
				} else if (ball.getX() >= getWidth() - (2 * BALL_RADIUS)) {
					moveLeft();
				}
				ball.move(vx,vy);
				
				//resets ball to center for next round
				if (ball.getY() >= getHeight() - (2 * BALL_RADIUS)) {
					turn += 1;
					ball.setLocation(getCenterX(), getCenterY());
					scoreKeeper();
				}
				checkForCollisions();
			} 	
		}
			
		
		/**Private method: checkForCollisions
		 * ----
		 * Checks for colliding objects. If ball encounters paddle, bounces. 
		 * If ball encounters brick, removes brick and bounces. */
		private void checkForCollisions() {
			GObject object = getCollidingObject(); 
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			if (object == paddle) {
				bounceClip.play();
				moveUp();
				paddleBounce += 1;
				
				//speeds up ball after 7th hit to increase level of difficulty 
				if (paddleBounce == 7) {
					vx = 2 * vx;
				}
				
				//speeds up ball after 20th hit to increase level of difficulty
				if (paddleBounce == 20) {
					vx = 2 * vx;
				} 
			} else if (object != null && object != paddle && object != scorekeeper) {
					bounceClip.play();
					remove(object);
					bricksGone += 1;
					score += 1;
					scoreKeeper();
					vy = -vy;
				}
			ball.move(vx, vy);
		}
	
		
		/**Private method: getCollidingObject
		 * ----
		 * Checks for colliding objects at all four corners of ball.*/
		private GObject getCollidingObject() {
			GObject collider = getElementAt(ball.getX(), ball.getY());
			if (collider == null) {
				collider = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
			} 
			if (collider == null) {
				collider = getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
			} 
			if (collider == null) {
				collider = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
			} 
			return(collider);
		}
		
		/**Private method: gameOver
		 * ----
		 * Displays losing message*/
		private void gameOver() {
			GLabel done = new GLabel("GAME OVER");
			done.setFont("Courier-24");
			done.setLocation(getCenterX() - (done.getWidth() / 2), getCenterY() + (done.getWidth() / 2));
			add(done);
		}
		
		/**Private method: youWon
		 * ----
		 * Displays winning message.*/
		private void youWon() {
			GLabel win = new GLabel("YOU WON. CONGRATS!");
			win.setFont("Courier-24");
			win.setLocation(getCenterX() - (win.getWidth() / 2), getCenterY() + (win.getHeight() / 2));
			add(win);
		}
		
		/**private method: clickToBegin
		 * ----
		 * Displays message with instruction to click to begin the game. */
		private GLabel clickToBegin() {
			begin = new GLabel("CLICK TO BEGIN");
			begin.setFont("Courier-24");
			begin.setLocation(getCenterX() - (begin.getWidth() / 2), getCenterY() + (begin.getHeight() / 2));
			add(begin);
			return(begin); 
		}
		
		/**private method: scoreKeeper
		 * ----
		 * Keeps track of user's score and displays at top of screen. */
		private void scoreKeeper() {
			if (scorekeeper != null) {
				remove(scorekeeper);
			}
			scorekeeper = new GLabel ("Your score: " + score + "  Lives left: " + (3 - turn));
			scorekeeper.setFont("Courier-20");
			scorekeeper.setLocation(getCenterX() - (scorekeeper.getWidth() / 2), (BRICK_Y_OFFSET / 2));
			add(scorekeeper);
		}
		
		/**private method: moveUp
		 * ----
		 * Ball goes upward. */
		private void moveUp() {
			vy= -Math.abs(vy);
		}
		
		/**private method: moveDown
		 * ----
		 * Ball goes downward. */
		private void moveDown() {
			vy = Math.abs(vy);
		}
		
		/**private method: moveRight
		 * ----
		 * Ball travels right. */
		private void moveRight() {
			vx = Math.abs(vx);
		}
		
		/**private method: moveLeft
		 * ----
		 * Ball travels left.*/
		private void moveLeft() {
			vx = -Math.abs(vx);
		}
}

