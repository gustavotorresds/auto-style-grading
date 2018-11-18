/*
 * File: Breakout.java
 * -------------------
 * Name: Jung-Won Ha	
 * Section Leader: Shanon Reckinger
 * 
 * This file is the extended version of the original Breakout game. 
 * The gameplay is identical to the non-extended version. 
 * In the beginning, the user is prompted to click mouse to start the game.
 * If the user is able to remove all the bricks, a congratulatory message is printed.
 * If the user loses all its lives without removing all the bricks, the user becomes a loser and is notified.
 * The user's score and amount of lives is also indicated at the bottom of the screen.
 * A "kicker" element is added where after the 7th collision between the ball and paddle, the horizontal 
 * velocity of the ball is increased to increase the difficulty of gameplay.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakOutExtensions extends GraphicsProgram {

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
	public static final double VELOCITY_X_KICK = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0/100.0;

	// Number of turns 
	public static final int NTURNS = 3;

	/** Private Instance Variables**/
	private double bricks = NBRICK_COLUMNS*NBRICK_ROWS;

	private GLabel lives = null;

	private GRect brick = null;

	private GRect paddle = null;

	private GOval ball = null;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;

	private static int SCORE;
	private GLabel scoreLabel;
	private int collisions = 0;

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	private GLabel start;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//Sets up elements of game
		setUp();

		//Plays the game
		for(int turns = 0 ; turns < NTURNS ; turns ++) {
			addBall();

			startMessage();
			waitForClick();
			remove(start);

			setBallMovement();

			if(bricks == 0) {
				congratulations();
				break;
			}
			if(bricks > 0) {
				//Changes the color of the lives tracker when less than 3 lives left
				livesColor(NTURNS - 1 - turns);
			}

			/*
			 * Removes the ball from the previous loop that would still 
			 * be present below the bottom of the canvas.
			 */
			remove(ball);
		}
		if(bricks > 0) {
			gameOver();
		}
	}


	//Adds the elements necessary for the startup of the game.
	private void setUp() {
		addBricks();
		addPaddle();
		scoreTracker();
		livesTracker();
	}
	private void addBricks() {
		for(int rows = 0; rows < NBRICK_ROWS; rows ++) {
			for(int columns = 0 ; columns < NBRICK_COLUMNS ; columns ++) {

				double brickY = BRICK_Y_OFFSET + rows * (BRICK_HEIGHT + BRICK_SEP);
				double brickX = BRICK_SEP*1.5 + columns * (BRICK_SEP + BRICK_WIDTH);

				//Initially colors all the bricks cyan
				createBrick(brickX,brickY,Color.CYAN);
				//Divides the total number of rows by 5 and colors each of them a different color
				if(rows < NBRICK_ROWS / 1.25) {
					brick.setColor(Color.GREEN);
				}
				if(rows < NBRICK_ROWS / 1.67) {
					brick.setColor(Color.YELLOW);
				}
				if(rows < NBRICK_ROWS / 2.50) {
					brick.setColor(Color.ORANGE);
				}
				if(rows < NBRICK_ROWS / 5.00) {
					brick.setColor(Color.RED);
				}
			}
		}
	}
	private void createBrick(double x, double y,Color c) {
		brick=new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(c);
		add(brick);
	}
	private void addPaddle() {
		//Initial x and y coordinates for the paddle
		double paddleX = getWidth() / 2 - PADDLE_WIDTH / 2; 
		double paddleY = getHeight() - PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle = new GRect(paddleX,paddleY,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	public void mouseMoved (MouseEvent e) {
		//Sets so that the mouse moves with the center of the paddle
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double paddleX = e.getX() - PADDLE_WIDTH / 2;
		/*
		 * If the x position of the mouse is less than half the width of the paddle from the right,
		 * or if its greater than half a paddle width from the left of the screen,
		 * then it sets the x coordinate of the center of the paddle to be the x coordinate of the mouse.
		 */
		if(e.getX() < getWidth() - PADDLE_WIDTH / 2 && e.getX() > PADDLE_WIDTH / 2) {
			paddle.setLocation(paddleX,paddleY);
		}
	}

	private void addBall() {
		double ballX = getWidth() / 2 - BALL_RADIUS;
		double ballY = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval (ballX,ballY,BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}

	private void setBallMovement() {

		//Randomly sets the ball's initial x velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;

		//Changes the ball's velocity when colliding with walls
		while(ball.getY() <= getHeight()) {

			//Checks for walls
			wallCollision();

			//Conditions for how the ball should react when colliding with certain objects
			GObject collider = getCollidingObject();

			if(collider == paddle) {

				double bottomBallY = ball.getY() + BALL_RADIUS * 2;
				double topPaddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

				//Ball only changes Y velocity when its below the top of the paddle
				if(bottomBallY >= topPaddleY && bottomBallY < topPaddleY + 3) {

					//Adjusts the x and y velocity of the ball depending on what part of the paddle it collides with.
					ballVelocity();

					bounceClip.play();

					//Tracks the number of collisions between the ball and paddle
					collisions++;
				}
				//"Kicker" element: When the ball has collided with the paddle 7 times, the horizontal speed of the ball doubles
				setKicker();
			}
			//If the ball collides with something that is neither a wall nor paddle, it must be a brick
			else if(collider != null && collider != scoreLabel && collider != lives) {

				vy = -vy;

				bounceClip.play();
				remove(collider);
				bricks--;

				//Constantly updates the score at the bottom left corner of the screen
				updateScore(collider);

				//Exits the loop when all the bricks have been removed
				if( bricks == 0 ) {
					removeAll();
					break;
				}
			}
			//Updates the ball's movement
			ball.move(vx, vy);
			pause(DELAY);
		}
	}
	//Ball changes velocities when it collides with a wall
	private void wallCollision() {
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			bounceClip.play();
			vx = -vx;
		}
		if(hitTopWall(ball)){
			bounceClip.play();
			vy = -vy;
		}
	}
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - 2 * BALL_RADIUS;	
	}
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	/*
	 * Checks if the x coordinate of the ball hits the left or right edge of the paddle,
	 * which is represented by multiplying the paddle width by 0.3 or 0.7.
	 * Depending on which direction the ball is coming from when it hits the paddle, the ball's post-velocity is adjusted accordingly.
	 */
	private void ballVelocity() {
		if(vx > 0 && ball.getX() + BALL_RADIUS * 2 < paddle.getX() + PADDLE_WIDTH * 0.3) {
			vx = - vx;
			vy = - vy;
		}
		else if (vx < 0 && ball.getX() + BALL_RADIUS * 2 < paddle.getX() + PADDLE_WIDTH * 0.3) {
			vy = - vy;
		}
		else if (vx > 0 && ball.getX() > paddle.getX() + PADDLE_WIDTH * 0.7) {
			vy = - vy;
		}
		else if (vx < 0 && ball.getX() > paddle.getX() + PADDLE_WIDTH * 0.7) {
			vx = - vx;
			vy = - vy;
		}
		else vy = - vy;
	}

	//Checks if any of 4 corners of the square the ball is inscribed in has collided with something
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(),ball.getY())!= null){
			return(getElementAt(ball.getX(),ball.getY()));
		} 
		else if(getElementAt(ball.getX(),ball.getY() + 2 * BALL_RADIUS)!=null) {
			return(getElementAt(ball.getX(),ball.getY()+2 * BALL_RADIUS));
		} 
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY())!=null){
			return(getElementAt(ball.getX()+BALL_RADIUS * 2,ball.getY()));
		} 
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + 2 * BALL_RADIUS)!=null) {
			return(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + 2 * BALL_RADIUS));
		} 
		else {
			return null;
		}
	}
	//Changes the color of the lives tracker when less than 3 lives left
	private GLabel livesColor(int x) {
		lives.setLabel("Lives:" + x );
		if(x == 2) {
			lives.setColor(Color.ORANGE);
		} else if(x == 1) {
			lives.setColor(Color.RED);
		} else if(x == 0) {
			lives.setColor(Color.BLACK);
		}
		return lives;
	}
	//After the ball collides with the ball 7 or more times, the horizontal speed of the ball increases
	private void setKicker() {
		if(collisions >= 7) {
			if(vx > 0) {
				vx = VELOCITY_X_KICK;
			}
			else if(vx < 0) {
				vx = -VELOCITY_X_KICK;
			}
		}
	}
	//Indicates how many lives the user has left.
	private void livesTracker() {
		lives = new GLabel("Lives:"+NTURNS);
		lives.setFont("Tahoma-20");
		lives.setColor(Color.GREEN);
		add( lives , getWidth() - lives.getWidth() - 5 , getHeight() - lives.getAscent() / 2 );
	}
	//Keeps track of the score increase dependent on the color of brick removed 
	private void updateScore(GObject collider) {
		if(collider.getColor()==Color.CYAN) {
			SCORE ++ ;
		}else if( collider.getColor() == Color.GREEN ) {
			SCORE += 2;
		}else if( collider.getColor() == Color.YELLOW ) {
			SCORE += 3;
		}else if( collider.getColor() == Color.ORANGE ) {
			SCORE += 4;
		}else if( collider.getColor() == Color.RED ) {
			SCORE += 5;
		}
		scoreLabel.setLabel("Score:"+SCORE);
	}
	//Indicates the score at the bottom-left corner of the screen
	private void scoreTracker() {
		scoreLabel= new GLabel("Score:" + 0);
		scoreLabel.setFont("Tahoma-20");
		scoreLabel.setLocation( 5, getHeight() - scoreLabel.getAscent() / 2 );
		add(scoreLabel);
	}

	//Prints a message prompting the user to start the game
	private void startMessage() {
		start=new GLabel("Click to Start");
		start.setFont("Courier-36");
		start.setColor(Color.BLUE);
		start.setLocation( getWidth() / 2 - start.getWidth() / 2,( getHeight() / 2 - PADDLE_Y_OFFSET - start.getAscent() / 2 ));
		add(start);
	}
	//Prints a message when the user has won the game
	private void congratulations(){
		GLabel winner=new GLabel("YOU WON!!!");
		winner.setFont("Courier-52");
		winner.setColor(Color.GREEN);
		winner.setLocation( getWidth() / 2 - winner.getWidth() / 2 , getHeight() / 2 - winner.getAscent() / 2 );
		add(winner);
	}
	//Prints a message when the user has lost the game
	private void gameOver() {
		GLabel loser=new GLabel("GAME OVER");
		loser.setFont("Courier-52");
		loser.setColor(Color.RED);
		loser.setLocation( getWidth() / 2 - loser.getWidth() / 2 , getHeight() / 2 - loser.getAscent() / 2 );
		add(loser);
	}
}


