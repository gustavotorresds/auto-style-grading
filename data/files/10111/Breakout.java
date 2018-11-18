/*
 * File: Breakout.java
 * -------------------
 * Name: Catherine Areklett
 * Section Leader: Ben Barnett
 * Breakout with extensions 
 * 
 * This file will eventually implement the game of Breakout.
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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 2.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy;
	private int counter;
	private int liveslost = 0;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//creates game board
		setUpGame();
		//plays Breakout
		playBreakout();
			//if player removes all 100 bricks, the game will show "Winner!"
			if (counter == 100) {
				GLabel winner = new GLabel ("Winner!", CANVAS_WIDTH/4, CANVAS_HEIGHT/2);
				winner.setFont("Helvetica-50");
				winner.setColor(Color.RED);
				add (winner);
			}
			//if player does not removes all 100 bricks before three tries, the game will show "You Lose :("
			}
	
	private void setUpColoredBricks() {
		double center = getWidth()/2-(BRICK_WIDTH * NBRICK_COLUMNS)/2;
		double yPlacement = BRICK_HEIGHT + BRICK_SEP;
		double xPlacement = BRICK_WIDTH + BRICK_SEP;
		
		//generates the bricks in the y-direction for nine additional rows, changing the colors every two rows
		for(int j = 0; j < NBRICK_ROWS; j++) {
			GRect brickY = new GRect (center - BRICK_WIDTH/2,BRICK_Y_OFFSET-BRICK_SEP + (yPlacement)*(j+1),BRICK_WIDTH,BRICK_HEIGHT);
			add(brickY);	
				if (j < 2) {
					brickY.setColor(Color.red);
					brickY.setFilled(true);
					} else if(j < 4) {
						brickY.setColor(Color.orange); brickY.setFilled(true);
					} else if(j < 6) {
						brickY.setColor(Color.yellow); brickY.setFilled(true);
					} else if(j < 8) {
						brickY.setColor(Color.green);brickY.setFilled(true);
					}	else if(j < 10) {
						brickY.setColor(Color.cyan);brickY.setFilled(true);
				}
				
		//generates the bricks in the x direction for nine additional columns, changing the colors every two rows
				int nextrow = 0;
				for(int k = nextrow; k < NBRICK_COLUMNS; k++) {
					GRect brickX = new GRect (center-BRICK_WIDTH/2 + (xPlacement)*j,BRICK_Y_OFFSET-BRICK_HEIGHT/2 + (yPlacement)*(nextrow+1),BRICK_WIDTH,BRICK_HEIGHT);
					add(brickX);
					nextrow = nextrow + 1;
						if (nextrow <= 2) {
							brickX.setColor(Color.red);brickX.setFilled(true);
						} else if(nextrow <= 4) {
							brickX.setColor(Color.orange);brickX.setFilled(true);
						} else if(nextrow <= 6) {
							brickX.setColor(Color.yellow);brickX.setFilled(true);
						} else if(nextrow <= 8) {
							brickX.setColor(Color.green); brickX.setFilled(true);
						}	else if(nextrow <= 10) {
							brickX.setColor(Color.cyan);brickX.setFilled(true);
						}
				}
		}
	}
		//creates the paddle 
		private GRect setUpPaddle() {
			GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
			paddle.setFilled(true);
			return paddle;
		}
	
		//centers the paddle in the bottom of the screen
		private void paddleInCenter(GRect paddle) {
			double centerX = (getWidth()/2 - PADDLE_WIDTH/2);
			double centerY = (getHeight() - PADDLE_Y_OFFSET);
			add (paddle,centerX,centerY);
		}
		
		//allows the paddle to move with the mouse
		public void mouseMoved(MouseEvent e) {
			double mouseX = e.getX();
			double mouseY = (getHeight() - PADDLE_Y_OFFSET);
				if (mouseX > 0 && mouseX < getWidth() - PADDLE_WIDTH) {
					paddle.setLocation(mouseX,mouseY);
				}
		}
	
		//creates the game ball
		private GOval setUpBall() {
			GOval ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
			ball.setColor(rgen.nextColor());
			ball.setFilled(true);
			return ball;
		}
	
		//centers the ball in the middle of screen to begin play
		private void ballInCenter(GOval ball) {
			double ballcenterX = (getWidth()/2 - PADDLE_WIDTH/2+BALL_RADIUS*2);
			double ballcenterY = ((getHeight() - PADDLE_Y_OFFSET-BALL_RADIUS*2)/2);
			add(ball, ballcenterX,ballcenterY);
		}	
	
		//compiles game setup
		private void setUpGame() {
			setUpColoredBricks();
			paddle = setUpPaddle();
			paddleInCenter(paddle);
			addMouseListeners();
			ball = setUpBall();
			ballInCenter(ball);
		}	
		
		//create ball velocity
		private void determineBallVelocity() {
			vy = VELOCITY_Y;
			vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx = -vx;
		}
	
		//enables ball movement and changes the direction of the ball movement under certain conditions
		private void moveBall() {
			double belowPaddle = paddle.getBottomY() + PADDLE_HEIGHT/2;
			pause(DELAY);
			if (ball.getBottomY() < belowPaddle && liveslost < NTURNS) {
				//starts ball movement in the downwards direction
				ball.move(vx,vy);  
				//ball will bounce of the right and left walls
				if (ball.getX() < 0 || ball.getX() > getWidth() - BALL_RADIUS*2) {
					vx = -vx;
					}
				//ball will bounce off the top
				if(ball.getY() < 0) {
					vy = -vy;
					}
				//if ball hits the paddle, the ball will bounce off and move in the upwards direction
				if (checkForObject() == paddle) {
					//if ball hits paddle, a sound effect will play
					bounceClip.play();
					ball.setColor(rgen.nextColor());
					ball.setFilled(true);
					paddle.setColor(rgen.nextColor());
					paddle.setFilled(true);
					vy = - Math.abs(vy);  
				//checks to see if ball hit a brick (will remove brick and bounce back)
				}else if (checkForObject() != null) {
					remove(checkForObject()); 
					counter = counter +1;
					vy = -vy;
					}
				//counts lives lost until three lives have been used up
				} else {
					liveslost = liveslost + 1;
					ballInCenter(ball);
					pause(DELAY);
					
				//displays "You Lose :(" if all three lives have been used
				} if (liveslost >= NTURNS) {
					GLabel loser = new GLabel ("You Lose :(", getWidth()/4, getHeight()/4);
					loser.setFont("Helvetica-50");
					loser.setColor(Color.RED); 
					add (loser);
				}
		} 
		
		//compiles game play
		private void playBreakout() {
			determineBallVelocity();
				while(true) {
					moveBall();
					if(counter == 100) {
						break;
					}
				}
		}
	
		//checks to see if any corner of the ball hit an object
		private GObject checkForObject() {
			//top left corner of ball
			if(getElementAt(ball.getX(), ball.getY()) != null) {
				return getElementAt(ball.getX(), ball.getY());
			} 
			//top right corner of ball
			if (getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY()) != null) {
				return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
			} 
			//bottom left corner of ball
			if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2) != null) {
				return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
			}
			//bottom right corner of ball
			if (getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2) != null) {
				return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
			} else //paddle
				return null; 
		}
}









			

	
