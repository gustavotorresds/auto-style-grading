/*
 * File: Breakout.java
 * -------------------
 * Name: Kara Eng
 * Section Leader: Ben Allen
 * 
 * This file will eventually implement the game of Breakout. in this extension the different bricks mean a different numbre of points 
 * when you hit the cyan, it's only one point
 * when you hit the green, it turns to cyan and then when you hit it again it dies. so it's worth two points 
 * each color only has one row of itself because it takes longer to take off all the bricks 
 * also the bricks will be placed randomly. Also everytime you get rid a brick, the ball goes a little faster. 
 * 
 * additionally, when you start the game and you want more or less rows of bricks, you can type '0' to take away brick rows (they'll still be 
 * placed randomly) or you can type '1' and add more rows of bricks 
 * 
 * Possible bug: sometimes the ball seems to go through multiple bricks and doesn't seem to switch directions but i think that might be in the 
 * original game as well? this is because a few of the elements have the same x and y? 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
//TODO: make sure that it knows when to end the game 
public class BreakoutExtension extends GraphicsProgram {
	// Dimensions of the canvas, in pixels
		// These should be used when setting up the initial size of the game,
		// but in later calculations you should use getWidth() and getHeight()
		// rather than these constants for accurate size information.
		public static final double CANVAS_WIDTH = 420;
		public static final double CANVAS_HEIGHT = 600;

		// Number of bricks in each row
		public static final int NBRICK_COLUMNS = 10;

		// Number of rows of bricks, can edit it and it will just cycle through colors 
		public static int numRows = 15;

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
		public static final double PADDLE_Y_OFFSET = 60;

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

		// Font to use for on-screen text 
		public static final String SCREEN_FONT = "SansSerif-BOLD-18";

		//gets the width of the row, including spacing on edges and in between 
		//then centers it in the console 
		private static double initialX = CANVAS_WIDTH/2 - BRICK_SEP*(NBRICK_COLUMNS-1)/2 - BRICK_WIDTH*NBRICK_COLUMNS/2;
		//makes the first row like six bricks down 
		private static double initialY = BRICK_HEIGHT * 6; 
		//making this an instance variable allows mouselistener to edit the paddle's location 
		private static GRect paddle;
		//making this an instance variable allows you to see how it interacts with other objects in this game
		private static GOval ball; 
		
		private static RandomGenerator rgen = RandomGenerator.getInstance();
		
		
		//velocity of x 
		private static double vx; 
		//velocity of y 
		private static double vy = 5; 
		
		//number of lives left 
		private static int livesLeft = 3;
		private static GLabel label; 
		private static GLabel labelScore; 
		
		//counts how many bricks you had left before you lost 
		private static double bricksKilled = 0; 
		
		//counts how many bricks there are on the course 
		private static int bricksInCourse =0; 
		//counts how many cyan bricks you've gotten rid of 
		private static int cyanKilled = 0; 
		
		//this way you can control how many rows of bricks you want 
		private static GLabel less; 
		private static GLabel more; 
		
		//this boolean is so that you only take off one life everytime it touches the bottom 
		//because your ball continues to move below the getHeight() even after you take off a life
		//so the program would want to take off as many lives as the ball moved, which we don't want 
		private static boolean penalized = false; 
		
		public void run() {
			// Set the window's title bar text
			setTitle("CS 106A Breakout Extension");
	
			// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
			// and getHeight() to get the screen dimensions, not these constants!
			setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
	
			//displays how many lives you have left 
			displayInstructions();
			addKeyListeners();
			addMouseListeners();
			setUp(); 	
			waitForClick();
			moveBall();
		}
		
		private void setUp() {
			bricksKilled = 0; 
			livesLeft = 3; 
			bricksInCourse = 0; 
			cyanKilled = 0; 
			//resets the y value 
			initialY = BRICK_HEIGHT * 6; 
			showLives();
			setUpBricks();
			createPaddle();
			createBall();
		}
		
		public void keyTyped (KeyEvent k) {
			if (k.getKeyChar() == KeyEvent.VK_SPACE && livesLeft>0) {
				ball.setLocation(getWidth()/2 - BALL_RADIUS*2, CANVAS_HEIGHT/2 - BALL_RADIUS*2);
				//makes it so the player can lose a life again because they're back to trying again 
				penalized = false; 
			}
			//this loop will make sure that the user doesn't go crazy adding all these rows 
			
				if (k.getKeyChar()==KeyEvent.VK_1) {
					numRows++;
					removeAll();
					setUp(); 
				}
				if (k.getKeyChar()==KeyEvent.VK_0) {
					numRows--; 
					removeAll();
					setUp(); 
				}			
		}
		
		
		private void displayInstructions() {
			GLabel label1 = new GLabel ("Click to start the game.");
			label1.setLocation(getWidth()/2-label1.getWidth()/2, getHeight()/2 + label1.getAscent()/2);
			add(label1); 

			GLabel label3 = new GLabel ("Press the space bar to try again.");
			label3.setLocation(getWidth()/2-label3.getWidth()/2, getHeight()/2 + label3.getAscent()/2+ label1.getAscent());
			add(label3); 
			waitForClick();
			remove(label1); 
			remove(label3);
		}
		//TODO: figure out how to make it so the paddle can handle it when you go faster
		//TODO: figure out how to reset everything on the screen once you lose 
		
		private void showLives() {
			label = new GLabel("Lives Left: " + livesLeft);
			label.setFont("Courier-14");
			label.setColor(Color.BLUE);
			
			// add the label to an arbitrary place on the screen 
			add(label, BRICK_WIDTH, getHeight() - label.getAscent()/2);	
			labelScore = new GLabel("Score: " + livesLeft);
			labelScore.setFont("Courier-14");
			labelScore.setColor(Color.BLUE);
			
			// add the label to an arbitrary place on the screen 
			add(labelScore, BRICK_WIDTH*7, getHeight() - label.getAscent()/2);	
		}

		//this is a separate method that way when you lose and try again, you just move the ball back to the stop 
		private void moveBall() {
			ball.setLocation(getWidth()/2 - BALL_RADIUS*2, CANVAS_HEIGHT/2 - BALL_RADIUS*2);
			
			//how fast it moves side to side 
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
				while (true) {
				ball.move(vx, vy);
				bounceBack();
				killBricks();
				pause(DELAY); 
				if (livesLeft == 0) {
					removeAll(); 
					GLabel label5 = new GLabel ("Sorry you lost. Final score: " + bricksKilled);
					label5.setLocation(getWidth()/2-label5.getWidth()/2, getHeight()/2 + label5.getAscent()/2);
					add(label5); 
					break; 
				}
				if (cyanKilled == bricksInCourse) {
					GLabel label6 = new GLabel ("Congratulations! You won!");
					label6.setLocation(getWidth()/2-label6.getWidth()/2, getHeight()/2 + label6.getAscent()/2);
					add(label6); 
					break; 
				}
			}
		}
		
		//this creates the ball
		private void createBall() {
			//places ball in the middle of the screen
			ball = new GOval (getWidth()/2 - BALL_RADIUS*2, CANVAS_HEIGHT/2 - BALL_RADIUS*2, BALL_RADIUS*2, BALL_RADIUS*2); 
			ball.setFilled(true);
			add(ball); 
		}
		
		private GObject getCollidingObject() {
			double ballX = ball.getX();
			double ballY = ball.getY();
			
			//checks first corner of ball 
			GObject mayhapsBrick = getElementAt(ballX, ballY);
			//checks top right corner of ball 
			if (mayhapsBrick == null) {
				mayhapsBrick = getElementAt(ballX+BALL_RADIUS*2, ballY);
			}
			//checks bottom right corner
			if (mayhapsBrick == null) {
				mayhapsBrick = getElementAt(ballX+BALL_RADIUS*2, ballY + BALL_RADIUS*2);
			}
			//checks bottom left corner
			if (mayhapsBrick == null) {
				mayhapsBrick = getElementAt(ballX, ballY + BALL_RADIUS*2);
			}
			//makes sure its not the paddle
			if (mayhapsBrick == paddle || mayhapsBrick == label || mayhapsBrick == labelScore || 
					mayhapsBrick == less || mayhapsBrick == more) {
				return null; 
			}
			return mayhapsBrick; 
		}
		
		//takes out the bricks if you hit em 
		private void killBricks() {		
			GObject mystery = getCollidingObject(); 
			//sees if there's anything there and makes sure it's not the paddle 
			if (mystery != null) {
				bricksKilled++; 
				if (mystery.getColor() == Color.CYAN) {
					remove(mystery);
					cyanKilled++; 
				}
				if (mystery.getColor() == Color.GREEN) {
					mystery.setColor(Color.CYAN);
				}
				if (mystery.getColor() == Color.YELLOW) {
					mystery.setColor(Color.GREEN);
				}
				if (mystery.getColor() == Color.ORANGE) {
					mystery.setColor(Color.YELLOW);
				}
				if (mystery.getColor() == Color.RED) {
					mystery.setColor(Color.ORANGE);
				}
				//makes it go faster every time you touch a brick 
				vy+=.01; 
			}
			//checks to see if you got all of the bricks! 
			
			}

		
		//creates the paddle and adds it to the screen 
		private void createPaddle() {
			paddle = new GRect (getWidth()/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
			paddle.setFilled(true);
			add(paddle); 
		}
		
		//method that makes ball bounce every time it runs into something
		//and deducts a life if you hit the bottom 
		private void bounceBack() {
			double ballX = ball.getX();
			double ballY = ball.getY();
			double ballX2 = ball.getX() + BALL_RADIUS*2;

			//makes it bounce the other way when it hits a wall on the sides 
			if (ballX >= getWidth() - BALL_RADIUS*2 || ballX <=0) {
				vx = -vx;
			}
			//makes it bounce the other way when it hits the top wall 
			//Note: not else if statements in case you hit a corner 
			if (ballY <= 0) {
				vy = -vy;
			}
			
			//BUG: lives don't decrease once you pass vy = 4 because ballY never equals getHeight based on how much the ball moves
			//by, however couldn't figure out a way to fix that because the ball continues to move below getHeight even after you take off 
			//a life
			if (!penalized) {
				if (ballY + BALL_RADIUS*2 >= getHeight()) {
					livesLeft--;
					penalized = true; 
				}
			}
			
			//if the bottom of the ball hits the top of the paddle
			//and if ballX is in between paddles edges
			if (ballY + BALL_RADIUS*2 <= paddle.getY() +vy && ballY + BALL_RADIUS*2 >= paddle.getY() 
					&& (ballX >= paddle.getX() && ballX <= paddle.getX() + PADDLE_WIDTH)) {
				vy = -Math.abs(vy);
			}
			//checks to see if right side of the ball hit the paddle or not 
			else if (ballY + BALL_RADIUS*2 <= paddle.getY() +vy && ballY + BALL_RADIUS*2 >= paddle.getY() 
					&& (ballX2 >= paddle.getX() && ballX2 <= paddle.getX() + PADDLE_WIDTH)) {
				vy = -Math.abs(vy);
			}
			if (ballY <= paddle.getY() +vy && ballY >= paddle.getY() 
					&& (ballX >= paddle.getX() && ballX <= paddle.getX() + PADDLE_WIDTH)) {
				vy = -Math.abs(vy);
			}
			//checks to see if right side of the ball hit the paddle or not 
			else if (ballY <= paddle.getY() +vy && ballY >= paddle.getY() 
					&& (ballX2 >= paddle.getX() && ballX2 <= paddle.getX() + PADDLE_WIDTH)) {
				vy = -Math.abs(vy);
			}
			
			
			if (getCollidingObject() != null) {
				//this one is so that you bounce off of bricks too 
				double deadBrickY = getCollidingObject().getY();
				//has to check if you hit top or bottom
				if (deadBrickY <= ballY || deadBrickY + BRICK_HEIGHT >=ballY) {
					vy = -vy;
				}
			}
		}
		
		//this method makes it so that the paddle follows your mouse's x coordinates 
		public void mouseMoved(MouseEvent e) {
			double mouseX = e.getX();
			//makes sure that the paddle stays in the console 
			if (mouseX>=0 && mouseX + PADDLE_WIDTH <= getWidth()) {
				paddle.setX(mouseX); 
			}
			//makes sure that lives left display is up to date 
			label.setLabel("Lives left: " +livesLeft);
			labelScore.setLabel("Score: " +bricksKilled);
		}
		
		//sets up all the bricks, if you want more than ten bricks, it'll cycle through the colors again 
		private void setUpBricks() {
			for (int c = 1; c<=numRows; c++) {
				buildRows(); 
			}
		}
		//method that builds the row and sets the color. 
		//this also edits intialY so that it will be set up for the next rows 
		//now it'll be completely random what color it will be and the set up is going to be a random number of bricks 
		private void buildRows() {
			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				//makes it so the color will be random for the brick 
				int c = rgen.nextInt(1,5);
				//makes it so its a 50/50 chance whether or not that brick will be there 
				if (rgen.nextBoolean(.25)) {
					double spacing = BRICK_WIDTH + BRICK_SEP; 
					GRect rect = new GRect (initialX + i*spacing, initialY, BRICK_WIDTH, BRICK_HEIGHT);
					rect.setFilled(true);
					//sets the color based at random. also sets up bricksInCourse so that it coordinates with the number of times you have 
					//to hit the bricks to win 
					if (c == 1) {
						rect.setColor(Color.RED);
					} else if (c == 2) {
						rect.setColor(Color.ORANGE);
					} else if (c == 3) {
						rect.setColor(Color.YELLOW);
					} else if (c == 4) {
						rect.setColor(Color.GREEN);
						//this one includes c == 0 for when it equals 20! 
					} else if (c == 5 || c == 0){
						rect.setColor(Color.cyan);
					}
					add(rect); 
					bricksInCourse ++; 
					
				}
			}
			initialY = initialY + BRICK_HEIGHT + BRICK_SEP;
		}
		
	}
