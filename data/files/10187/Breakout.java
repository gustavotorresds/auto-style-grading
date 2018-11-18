/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
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

	/* CS 106A Winter 2018
	 * Author: Paul Handal
	 */
	
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
	
	// Class Variables for objects
	// 1.a define brick
	private GRect brick = null;
	// 2.a Define paddle
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	// 3.a Define ball
	private GOval ball = new GOval(2*BALL_RADIUS,2*BALL_RADIUS);
	// 3.b Define variable for whether the ball is moving or not
	private boolean ballMoving = false;
	// 4.a Define actual velocity variables for the ball and for mouse location, and declare randomizer
	private double velX, velXTurn, velY, mouseX, mouseY;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	// 5.a Define amount of lives, or turns left, and bricks left for winning case
	private int turnsLeft = NTURNS;
	private int bricksLeft = NBRICK_COLUMNS*NBRICK_ROWS;
	// 6 Define  a multiplier, and a brick counter for score purposes. The multiplier will speed up velX every time a brick is hit
	private double score = 0;
	private int brickCounter;
	private double streakMult = 1;
	private double colorMult = 1;
	// 7. Add audio clip
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	// 8. Add difficulty
	private double diffMult = 1;
	// labels that need to be up or removed during the game
	private GLabel difficulty = new GLabel("Easy");
	private GLabel easy = new GLabel("Easy Difficulty");
	private GLabel medium = new GLabel("Medium Difficulty");
	private GLabel hard = new GLabel("Hard Difficulty");
	private GLabel instr1 = new GLabel("Move mouse to paddle to start game.");
	private GLabel instr2 = new GLabel("Select difficuty at the top: ");
	// 9 Display score and lives left
	private GLabel scoreBoard = new GLabel("Score: 0");
	private GLabel livesLeft = new GLabel("Turns: 3");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		// 1. Create bricks
		for(int i = 0; i < NBRICK_COLUMNS; i++) {
			createBrickRow(i);
		}
		
		// 8. Setup text
		textSetup();
		
		// 2. Create paddle and make it move horizontally with mouse
		paddle.setFilled(true);
		add(paddle,(getWidth()-PADDLE_WIDTH)/2,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		
		GLabel finalMess = new GLabel("");
		
		gameStarter();
		
		// 5. Create loop for turns
		while (turnsLeft > 0) {
			// 2.1 Set Listeners
			addMouseListeners();
			
			// 6. Set value of brickCounter, which resets every time a life is lost
			brickCounter = 0;
			
			// 3. Create ball, with ability to move
			add(ball,paddle.getX()+PADDLE_WIDTH/2,paddle.getY()-2*BALL_RADIUS);
		
			// 3.2 Create a timer in the middle of the screen before ball starts moving
			// startTimer();
			
			// Use turn starter to begin a turn 
			turnStarter();
			
			// 4. 1 Set initial velocity
			velY = -VELOCITY_Y;
			velXTurn = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
			velX = velXTurn;
			if(rgen.nextBoolean(0.5)) {
				velX = -velX;
			}
		
			// 3.2 Create loop for ball movement
			while(ballMoving && bricksLeft > 0) {
				//3.4 Have ball move and then delay
				ball.move(velX, velY);
				// 4.2 Check for wall and objects
				ballBounce();
			
				pause(DELAY/diffMult);
			}
			
			// Subtract turns left at the end of a turn
			if(ballMoving == false) {
				turnsLeft = turnsLeft - 1;
			}
			
			remove(livesLeft);
			livesLeft.setLabel("Turns: " + turnsLeft);
			add(livesLeft, getWidth() - livesLeft.getWidth(), livesLeft.getAscent());
			
			// Check for winning conditions first, in case user won on last turn, display appropriate message
			if(bricksLeft == 0) {
				remove(livesLeft);
				remove(scoreBoard);
				finalMess.setLabel("You Won!");
				add(finalMess,0,0);
				finalMess.setColor(Color.BLUE);
				finalMess.setCenterLocation(getWidth()/2,getHeight()/2);
				turnsLeft = 0;
			} else if(turnsLeft == 0) {
				remove(livesLeft);
				finalMess.setLabel("You Lose! :(");
				add(finalMess,0,0);
				finalMess.setColor(Color.RED);
				finalMess.setCenterLocation(getWidth()/2,getHeight()/2);
			}
		}
		GLabel finalScore = new GLabel("Final Score: " + score);
		finalScore.setColor(Color.GREEN);
		add(finalScore,0,getHeight());
		finalScore.setCenterLocation(getWidth()/2,finalMess.getY() + finalMess.getHeight());
	}
	
	private void gameStarter() {
		while(ballMoving == false) {
			if(getElementAt(mouseX,mouseY) == paddle) {
				removeGameLabels();
				ballMoving = true;
			}
		}
	}

	private void textSetup() {
		// 8.2 Setup labels for the three difficulties	
		easy.setColor(Color.GREEN);
		add(easy, 0 , BRICK_Y_OFFSET - 10);
		medium.setColor(Color.ORANGE);
		add(medium, 0 , BRICK_Y_OFFSET - 10);
		medium.setCenterX(getWidth()/2);
		hard.setColor(Color.RED);
		add(hard, getWidth() - hard.getWidth() , BRICK_Y_OFFSET - 10);
		// Labels for instructions
		instr1.setColor(Color.BLUE);
		instr2.setColor(Color.BLUE);
		add(instr1, 0, getHeight()/2 - instr1.getAscent() - 5);
		add(instr2, 0, getHeight()/2);
		instr1.setCenterX(getWidth()/2);
		instr2.setCenterX(getWidth()/2);
		// Permanent labels
		scoreBoard.setColor(Color.BLUE);
		livesLeft.setColor(Color.RED);
		add(scoreBoard, 0 , scoreBoard.getAscent());
		add(livesLeft, getWidth() - livesLeft.getWidth(), livesLeft.getAscent());
	}

	private void turnStarter() {
		add(instr1, 0, getHeight()/2 - instr1.getAscent() - 5);
		instr1.setCenterX(getWidth()/2);
		while(ballMoving == false) {
			if(getElementAt(mouseX,mouseY) == paddle) {
				remove(instr1);
				ballMoving = true;
			}
		}
	}
	
	private void breakBrick(GObject collider) {
		// Score calculator and brick remover
		Color scoreColor = collider.getColor();
		if (scoreColor == Color.CYAN) {
			colorMult = 1;
		} else if (scoreColor == Color.GREEN) {
			colorMult = 1.5;
		} else if (scoreColor == Color.MAGENTA) {
			colorMult = 2;
		} else if (scoreColor == Color.ORANGE) {
			colorMult = 2.5;
		} else if (scoreColor == Color.RED) {
			colorMult = 3;
		}
		score = score + 100 * colorMult * streakMult * diffMult;
		brickCounter = brickCounter + 1;
		streakMult = 1 + brickCounter/50.0;
		bricksLeft = bricksLeft - 1; 
		velX = streakMult*velXTurn;
		remove(scoreBoard);
		scoreBoard.setLabel("Score: " + score);
		add(scoreBoard, 0 , scoreBoard.getAscent());
		remove(collider);
		bounceClip.play();
	}
	
	private void ballBounce() {
		// 4.2a Get ball coordinates
		double ballX = ball.getX();
		double ballY = ball.getY();
		
		// 4.2b Check for objects
		GObject rect = null;
		boolean stayInLoop = true;
		int i = 0;
		while (i < 2 && stayInLoop == true) {
			int j = 0;
			while (j < 2 && stayInLoop == true) {
				// Check for elements at four corners
				rect = getElementAt(ballX + i*2*BALL_RADIUS, ballY + j*2*BALL_RADIUS);
				if (rect != null) {
					if (rect == paddle) {
						smartPaddle(ballX,ballY);
					} else if(rect != scoreBoard && rect != livesLeft){
						// Brick interactions
						bounceOffBrick(rect, ballX, ballY);
					}
					// exit if an element is found
					stayInLoop = false;
				}
				j = j+1;
			}
			i = i+1;
		}
		// 4.2c Check for walls
		if(ballX < 0) {
			bounceRight();
		} else if(ballX + 2*BALL_RADIUS >= getWidth()){
			bounceLeft();
		} else if(ballY <= 0) {
			bounceDown();
		} else if (ballY + 2*BALL_RADIUS >= getHeight()) {
			// End current game and decrease turns/lives
			ballMoving = false;
		}
	}
	
	private void smartPaddle(double ballX, double ballY) {
		int bottomCounter = 0;
		GObject ballBottom = null;
		bounceUp();
		// Determine where the paddle is relative to the bottom of the ball
		for (int i = 0 ; i < 3 ; i++ ) {
			ballBottom = getElementAt(ballX + i*BALL_RADIUS, ballY + 2*BALL_RADIUS);
			if (ballBottom == paddle) {
				bottomCounter = bottomCounter + 1;
			}
		}
		if (bottomCounter == 1) {
			// Check whether the bottom right corner of the ball is the only one hitting the paddle, therefore hitting the left 
			// corner of the paddle
			if (ballBottom == paddle) {
				bounceLeft();
			} else { // if not, only the bottom left corner must be hitting the paddle on it's right corner
				bounceRight();
			}
		} 
	}

	private void bounceDown() {
		// This method is different from bounceVert to deal with some errors arising from using bounceVert on the top wall
		velY = Math.abs(velY);		
	}

	private void bounceUp() {
		// This method is different from bounceVert to deal with some errors arising from using bounceVert on the paddle
		velY = -Math.abs(velY);
	}
	
	private void bounceLeft() {
		// This method is different from bounceHoriz to deal with some errors arising from using bounceHoriz on the wall
		velX = -Math.abs(velX);
	}
	
	private void bounceRight() {
		// This method is different from bounceHoriz to deal with some errors arising from using bounceHoriz on the wall
		velX = Math.abs(velX);
	}
	
	private void bounceOffBrick(GObject rect, double ballX, double ballY) {
		// Setup what to do when the ball runs into a brick
		// Check the ball's 4 corners
		int diffBricks = 1;
		int cornerCount = 0;
		GObject test = null;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				test = getElementAt(ballX+i*2*BALL_RADIUS,ballY+j*2*BALL_RADIUS);
				if (test != null) {
					cornerCount = cornerCount + 1;
					if (test != rect) {
						diffBricks = diffBricks+1;
					}
				}
			}
		}
		
		GObject TLbrick = getElementAt(ballX, ballY);
		GObject TRbrick = getElementAt(ballX + 2*BALL_RADIUS, ballY);
		GObject BLbrick = getElementAt(ballX, ballY + 2*BALL_RADIUS);
		GObject BRbrick = getElementAt(ballX + 2*BALL_RADIUS, ballY + 2*BALL_RADIUS);
		test = null;
		
		
		// Case A. Cases when there's one brick, in one corner
		if (cornerCount == 1) {
			caseA(TLbrick,TRbrick,BLbrick,BRbrick,ballX,ballY);
			
		// Case B. When there's three bricks, three corners
		} else if (diffBricks == 3) {
			caseB(TLbrick,TRbrick,BLbrick,BRbrick);
			
		} else if(cornerCount == 2) {
			caseC(TLbrick,TRbrick,BLbrick,BRbrick,ballX,ballY, diffBricks);
		}
	}
	
	private void caseC(GObject TLbrick, GObject TRbrick, GObject BLbrick, GObject BRbrick, double ballX, double ballY, int diffBricks) {
		// Part of bounceOffBrick
		// Two corners, one two blocks. There are 5 possibilities,  1. One block taking two corners, break that block and bounce in 
		// only one direction. 2. There being two blocks next to each other, with the ball being much closer to one block, 
		// breaking only that block. 3. Similar to two, but the ball being almost in the middle of the two blocks (test == null),
		// therefore breaking both. 4. Similar to the previous two,but there was a third undetected block in the middle, only this 
		// block is broken. 4. The two blocks being on opposite corners, therefore both must be destroyed and the ball must 
		// bounce diagonally. 
		
		// Case C.1
		if(diffBricks < 2) {
			if (TLbrick != null && TRbrick != null) {
				// Only top, bounce down
				breakBrick(TLbrick);
				bounceVert();
			} else if (BRbrick != null && TRbrick != null) {
				// Only right, bounce left
				breakBrick(TRbrick);
				bounceHoriz();
			} else if (TLbrick != null && BLbrick != null) {
				// Only left, bounce right
				breakBrick(BLbrick);
				bounceHoriz();
			} else if (BRbrick != null && BLbrick != null) {
				// Only bottom, bounce up
				breakBrick(BRbrick); 
				bounceVert();
			}
		} else { 
			//cases C2-C4, two blocks in the same x or y plane are detected
			GObject test = null;
			if (TLbrick != null && TRbrick != null) {
				test = getElementAt(ballX + BALL_RADIUS, ballY);
				bounceVert();
				if (test == null) { // If test is null, remove TLbrick and TRbrick (C3), else, remove test. (C2 and C4)
					breakBrick(TLbrick);
					breakBrick(TRbrick);
				} else {
					breakBrick(test);
				}
			} else if (TLbrick != null && BLbrick != null) {
				test = getElementAt(ballX, ballY + BALL_RADIUS);
				bounceHoriz();
				if (test == null) { // If test is null, remove TLbrick and BLbrick (C3), else, remove test. (C2 and C4)
					breakBrick(TLbrick);
					breakBrick(BLbrick);
				} else {
					breakBrick(test);
				}
			} else if (BRbrick != null && BLbrick != null) {
				test = getElementAt(ballX + BALL_RADIUS, ballY + 2 * BALL_RADIUS);
				bounceVert();
				if (test == null) { // If test is null, remove BRbrick and BLbrick (C3), else, remove test. (C2 and C4)
					breakBrick(BRbrick);
					breakBrick(BLbrick);
				} else {
					breakBrick(test);
				}
			} else if (BRbrick != null && TRbrick != null) {
				test = getElementAt(ballX + 2 * BALL_RADIUS, ballY + BALL_RADIUS);
				bounceHoriz();
				if (test == null) { // If test is null, remove BRbrick and TRbrick (C3), else, remove test. (C2 and C4)
					breakBrick(TRbrick);
					breakBrick(BRbrick);
				} else {
					breakBrick(test);
				}
				// C5
			} else if(TLbrick == null && BRbrick == null) {
				bounceVert();
				bounceHoriz();
				breakBrick(TRbrick);
				breakBrick(BLbrick);
			} else if(TRbrick == null && BLbrick == null) {
				bounceVert();
				bounceHoriz();
				breakBrick(TLbrick);
				breakBrick(BRbrick);
			}
		}
	}
	
	private void caseB(GObject TLbrick, GObject TRbrick, GObject BLbrick, GObject BRbrick) {
		// Part of bounceOffBrick
		bounceVert();
		bounceHoriz();
		if (BRbrick == null) {
			// bounce down and right, ignore brick that can't be hit
			breakBrick(TRbrick);
			breakBrick(BLbrick);
		} else if (BLbrick == null) {
			// bounce down and left, ignore brick that can't be hit
			breakBrick(BRbrick);
			breakBrick(TLbrick);
		} else if (TRbrick == null) {
			// bounce up and right, ignore brick that can't be hit
			breakBrick(BRbrick);
			breakBrick(TLbrick);
		} else if (TLbrick != null) {
			// bounce up and left, ignore brick that can't be hit
			breakBrick(TRbrick);
			breakBrick(BLbrick);
		}
	}
	
	private void caseA(GObject TLbrick, GObject TRbrick, GObject BLbrick, GObject BRbrick, double ballX, double ballY) {
		// Part of bounceOffBrick()
		if (TLbrick != null) {
			// Only top left, bounce down and right
			breakBrick(TLbrick);
			bounceRight();
			bounceDown();
		} else if (TRbrick != null) {
			// Only top right, bounce down and left
			breakBrick(TRbrick);
			bounceLeft();
			bounceDown();
		} else if (BLbrick != null) {
			// Only bottom left, bounce up and right
			breakBrick(BLbrick);
			bounceRight();
			bounceUp();
		} else if (BRbrick != null) {
			// Only bottom-right, bounce up and left
			breakBrick(BRbrick); 
			bounceLeft();
			bounceDown();
		}
		
	}
	
	private void bounceVert() {
		// Method so ball can bounce off blocks
		velY = -velY;
	}

	private void bounceHoriz() {
		// Method to bounce off certain blocks
		velX = -velX;	
	}
	
	public void mouseMoved(MouseEvent e) {
		// 2.2 Define Method for paddle movement and game start
		mouseX = e.getX();
		mouseY = e.getY();
		double paddleCenter = mouseX - PADDLE_WIDTH/2;
		paddle.setLocation(paddleCenter,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		// 3.3 Move ball with paddle before movement starts
		if(!ballMoving) {
			ball.setY(paddle.getY()-2*BALL_RADIUS);
			ball.setCenterX(paddle.getX()+PADDLE_WIDTH/2);
			// 8.1 Set difficulty before ball starts moving
			difficultySelector();
			if (getElementAt(mouseX,mouseY) == paddle) {
				removeGameLabels();
				ballMoving = true;
			}
		}
	}

	private void removeGameLabels() {
		remove(difficulty);
		remove(instr1);
		remove(instr2);
		remove(easy);
		remove(medium);
		remove(hard);
	}

	private void difficultySelector() {
		// 8.3 Set it so that the difficulty changes depending on the last selected Label
		remove(difficulty);
		GObject difficultySelect = getElementAt(mouseX,mouseY);
		if (difficultySelect == easy) {
			diffMult = 1.0;
			difficulty.setLabel("Easy");
			difficulty.setColor(Color.GREEN);
		} else if(difficultySelect == medium) {
			diffMult = 1.5;
			difficulty.setLabel("Medium");
			difficulty.setColor(Color.ORANGE);
		} else if(difficultySelect == hard) {
			diffMult = 2.0;
			difficulty.setLabel("Hard");
			difficulty.setColor(Color.RED);
		}
		if (turnsLeft == 3) {
			add(difficulty, 0, getHeight()/2 + instr1.getAscent() + 5);
			difficulty.setCenterX(getWidth()/2);
		}
	}
		
	private void createBrickRow(int i) {
		//  1.1 create a Row of bricks
		for(int j = 0; j < NBRICK_ROWS; j++) {
			double brickX = BRICK_SEP + j*(BRICK_WIDTH + BRICK_SEP);
			double brickY = BRICK_Y_OFFSET + i*(BRICK_HEIGHT + BRICK_SEP);
			setupBrick(i);
			add(brick,brickX,brickY);
		}
	}

	private void setupBrick(int i) {
		//  1.2 Setup bricks and color
		brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		if (i < 2) {
			brick.setFillColor(Color.RED);
			brick.setColor(Color.RED);
		}	else if (i < 4){
			brick.setFillColor(Color.ORANGE);
			brick.setColor(Color.ORANGE);
		}	else if (i < 6) {
			brick.setFillColor(Color.MAGENTA);
			brick.setColor(Color.MAGENTA);
		}	else if (i < 8) {
			brick.setFillColor(Color.GREEN);
			brick.setColor(Color.GREEN);
		}	else {
			brick.setFillColor(Color.CYAN);
			brick.setColor(Color.CYAN);
		}
	}
}
