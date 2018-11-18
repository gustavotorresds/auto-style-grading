/*
 * File: BreakoutExtension.java
 * Name: Julianne Crawford
 * Section Leader: Peter Maldonado
 * -------------------------------------------------------------------
 * Purpose: This program creates the classic arcade game, Breakout.
 * Breakout is an interactive game where the user tries to break all 
 * of the bricks at the top of the window using a ball and paddle. 
 * The user controls the paddle at the bottom of the window and tries
 * to bounce the ball up towards the bricks. The ball can bounce off 
 * the top and side walls, but if the ball falls past the paddle and 
 * hits the bottom wall, the turn ends. The player gets three turns 
 * before the game ends. If the player is able to break all of the  
 * bricks within these three turns, the player wins. The program can
 * be easily altered to accommodate different brick, paddle, and ball
 * settings. 
 * -------------------------------------------------------------------
 * Extensions: The BreakoutExtension program also offers the following extensions:
 * 1. Adds sounds: A bounce sound is played any time the ball hits the bricks 
 * or the paddle. A win sound is played when the player wins the game. A lose sound 
 * is played every time the player loses a life. 
 * 2. Adds messages: messages are added to let the player know when they have won 
 * and when the game is over. 
 * 3. Improved user control over bounces: This extension offers enhanced user control
 * over the paddle. The user can control which way the ball will bounce off of the 
 * paddle depending on where the ball hits the paddle. For example, if the ball hits the 
 * right half of the paddle, the ball will bounce to the right. 
 * 4. Speed increase: As the player breaks more bricks, the ball's velocity increases to 
 * make the game more challenging. 
 * 5. Score: This extension program keeps track of the player's score. Bricks are assigned 
 * points based on their color. 
 * 6. Counter Displays: This extension program adds 3 new counter displays to show the 
 * player how many bricks are left to break, their score, and how many turns they have left.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

/* Dimensions of the canvas, in pixels */
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

/* Number of bricks in each row */
	public static final int NBRICK_COLUMNS = 10;

/* Number of rows of bricks */
	public static final int NBRICK_ROWS = 10;

/* Total number of bricks */
	public static final int NBRICKS = NBRICK_COLUMNS*NBRICK_ROWS;

/* Separation between neighboring bricks, in pixels */
	public static final double BRICK_SEP = 4;

/* Width of each brick, in pixels */
	public static final double BRICK_WIDTH = 
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS;

/* Height of each brick, in pixels */
	public static final double BRICK_HEIGHT = 8;

/* Offset of the top brick row from the top, in pixels */
	public static final double BRICK_Y_OFFSET = 70;

/* Dimensions of the paddle */
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

/* Offset of the paddle up from the bottom */
	public static final double PADDLE_Y_OFFSET = 30;

/* Radius of the ball in pixels */
	public static final double BALL_RADIUS = 10;

/* Color of the ball */
	public static final Color BALL_COLOR = Color.BLACK; 

/* The ball's vertical velocity */
	public static final double VELOCITY_Y = 3.0;

/* The ball's minimum and maximum horizontal velocity */
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

/* Animation delay or pause time between ball moves (ms) */
	public static final double DELAY = 800.0 / 60.0;

/* Number of turns */
	public static final int NTURNS = 3;

/* Delay in between turns (ms) */
	public static final double TURN_DELAY = 500.0;

/* Rows in color pattern */
	private static final int COLOR_PATTERN = 10; 

/* Dimensions of restart button (pixels) */
	public static final double RESTART_WIDTH = 100;
	public static final double RESTART_HEIGHT = 30; 

	public void run() {
		setUpGame(); 
		playGame(); 
	}

	/* Method: setUpGame
	 * ------------------------
	 * This method sets up the breakout game for the player.
	 */
	private void setUpGame() {
		setUpCanvas(); 
		setUpBricks(); 
		setUpPaddle(); 
		setUpDisplays();
	}

	/* Method: setUpCanvas
	 * --------------------
	 * The setUpCanvas method sets the windows's title bar text and 
	 * the canvas size
	 */
	private void setUpCanvas() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
	}

	/* Method: addBricks
	 * -------------------
	 * The addBricks method adds rows of bricks based on the defined 
	 * number of rows and columns. The rows of bricks are collored 
	 * based on a 10-row color pattern (red, red, orange orange, yellow, 
	 * yellow, green, green, cyan, cyan). 
	 */
	private void setUpBricks() {
		for(int row = 0; row < NBRICK_ROWS; row++) {
			Color color = Color.RED; 
			double colorCounter = Math.floor(row/COLOR_PATTERN);
			if (row <= 1 + 10*colorCounter) {
				color = Color.RED;
			} else if (row > 1 + 10*colorCounter && row <= 3 + 10*colorCounter) {
				color = Color.ORANGE; 
			} else if (row > 3 + 10*colorCounter && row <= 5 + 10*colorCounter) {
				color = Color.YELLOW;
			} else if (row > 5 + 10*colorCounter && row <= 7 + 10*colorCounter) {
				color = Color.GREEN;
			} else {
				color = Color.CYAN; 
			}
			for(int col = 0; col < NBRICK_COLUMNS; col++) {
				double brickX = BRICK_SEP + (BRICK_WIDTH+BRICK_SEP)*col; 
				double brickY = BRICK_Y_OFFSET+ (BRICK_HEIGHT+BRICK_SEP)*row;
				GRect brick = makeRect(color,BRICK_WIDTH,BRICK_HEIGHT); 
				add(brick,brickX,brickY); 
			}
		}
	}

	/* Method: setUpPaddle
	 * --------------------
	 * The setUpPaddle method adds a rectangular paddle and makes it track the mouse's 
	 * movements in the x-direction. 
	 */
	private void setUpPaddle() {
		paddle = makeRect(Color.BLACK,PADDLE_WIDTH,PADDLE_HEIGHT);
		add(paddle,getWidth()/2-PADDLE_WIDTH/2,getHeight()-PADDLE_Y_OFFSET);
		addMouseListeners();
	}

	/* Method: setUpDisplays
	 * ----------------------
	 * The setUpDisplays method initiates the counter displays in the window to show 
	 * the player how many bricks and turns they have left, and their score. 
	 */
	private void setUpDisplays() {
		updateBricksLeftDisplay(); 
		updateTurnDisplay(); 
		updateScoreDisplay(); 
	}
	
	/* Method: makeRectangle
	 * ------------------------
	 * The makeRectangle method makes and returns a rectangle with specified 
	 * width, height, and color. 
	 * Parameters: 
	 * color - the rectangle color 
	 * rectWidth - the rectangle width (in pixels) 
	 * rectHeight - the rectangle height (in pixels) 
	 */
	private GRect makeRect(Color color, double rectWidth, double rectHeight) {
		GRect rect = new GRect(rectWidth,rectHeight); 
		rect.setFilled(true);
		rect.setColor(color);
		return rect; 
	}
	
	/* Method: mouseMoved
	 * ------------------------
	 * The mouseMoved method is called any time the mouse moves in
	 * the program screen. In this instance specifically, this method 
	 * moves the paddle in accordance with the mouse's x-coordinate. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if(mouseX <= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(mouseX,getHeight()-PADDLE_Y_OFFSET);
		}
	}
	
	/* Method: playGame
	 * ------------------------
	 * The playGame method allows the user to play the game. A game consists
	 * of three turns. 
	 */
	private void playGame() {
		for(turn = 0; turn < NTURNS; turn++) {
			remove(turnDisplay); 
			updateTurnDisplay();
			addBall();
			playTurn(); 		
		}
		if(brickCount != 0) {
			removeAll(); 
			GLabel gameOver = printCenteredMessage("Game Over :(","SansSerif-20");
			add(gameOver);
		}
	}

	/* Method: addBall
	 * ----------------
	 * The addBall method creates a circle representing the ball at the center of 
	 * the playing window. This method also initiates the ball's velocities in the 
	 * x and y directions. 
	 */
	private void addBall() {
		double ballX = getWidth()/2-BALL_RADIUS;
		double ballY = getHeight()/2-BALL_RADIUS;
		ball = new GOval(ballX,ballY,2*BALL_RADIUS,2*BALL_RADIUS); 
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
		waitForClick(); 

		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX); 
		if(rgen.nextBoolean(0.5)) vx = -vx; 
		vy = 3.0;
	}
	
	/* Method playTurn
	 * -----------------
	 * The playTurn method initiates the start of a turn. 
	 */
	private void playTurn() {
		AudioClip Lose = MediaTools.loadAudioClip("LoseSound.mp3");
		while(ball.getY() <= getHeight()-2*BALL_RADIUS) {
			ball.move(vx,vy);
			checkForWalls();
			checkForCollisions();
			remove(bricksLeftDisplay); 
			remove(scoreDisplay);
			updateBricksLeftDisplay();
			updateScoreDisplay(); 
			if(brickCount == 0) {
				removeAll(); 
				winGraphic();
				turn = NTURNS; 
				break; 
			}
			double changingDelay = increaseSpeed(); 
			pause(changingDelay); 
		}
		if(brickCount!=0) {
			Lose.play();
		}
		pause(TURN_DELAY/2);
		remove(ball);
		pause(TURN_DELAY);
	}

	/* Method: checkForWalls
	 * ----------------------
	 * The checkForWalls method checks if the ball has hit a wall. If it 
	 * has hit the top wall this method reverses the ball's y-velocity. If
	 * it hits either of the side walls, this method reverses the ball's 
	 * x-velocity. If the ball hits the bottom wall, the turn ends. 
	 * 
	 */
	private void checkForWalls() {
		boolean topWallBounds = ball.getY() <= 0;
		boolean rightWallBounds = ball.getX() >= getWidth()-2*BALL_RADIUS;
		boolean leftWallBounds = ball.getX() <= 0;

		if(topWallBounds) {
			vy = -vy; 
		} else if(rightWallBounds || leftWallBounds){
			vx = -vx; 
		}
	}

	/* Method: checkForCollisions
	 * ---------------------------
	 * The checkForCollisions method checks if the ball has collided 
	 * with either the paddle or a brick. If it collides with the 
	 * paddle, the y-velocity is reversed. Furthermore, depending on 
	 * where the ball collides with the paddle determines the ball's
	 * x-velocity. If the ball collides with a brick, the y-velocity 
	 * is reversed, the brick is removed, and the score is increased.
	 */
	private void checkForCollisions() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce2.mp3");
		GObject collider = getCollidingObject(); 
		double ballPaddleRelationship = Math.abs(ball.getX()-paddle.getX())/PADDLE_WIDTH;
		double paddleMiddle = (PADDLE_WIDTH/2-BALL_RADIUS)/PADDLE_WIDTH;		
		if (collider == paddle) {
			bounceClip.play(); 
			vy = -Math.abs(vy); 
			if(ballPaddleRelationship < paddleMiddle) {
				vx = -rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX); 
			} else if (ballPaddleRelationship == paddleMiddle){
				vx = 0;
			} else if (ballPaddleRelationship > paddleMiddle){
				vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX); 
			}
		} else if(collider == bricksLeftDisplay || collider == turnDisplay || collider == scoreDisplay) {
		} else if(collider != null) {
			bounceClip.play(); 
			remove(collider); 
			brickCount--; 
			vy = -vy; 
			vx = rgen.nextDouble(1.0,3.0); 
			if(rgen.nextBoolean(0.5)) vx = -vx;
			// assign score factor based on brick color 
			if(collider.getColor() == Color.RED) {
				scoreFactor = 5; 
			} else if(collider.getColor() == Color.ORANGE) {
				scoreFactor = 4; 
			} else if(collider.getColor() == Color.YELLOW) {
				scoreFactor = 3; 
			} else if(collider.getColor() == Color.GREEN) {
				scoreFactor = 2; 
			} else {
				scoreFactor = 1;
			}
			score += scoreFactor; 
		}
	}

	/* Method: getCollidingObject
	 * ---------------------------
	 * The getCollidingObject method returns the element located at the ball's 
	 * four corners. If there is no element at any of its four corners, it 
	 * returns null. 
	 */
	private GObject getCollidingObject() {
		GObject upperLeftCornerObject = getElementAt(ball.getX(),ball.getY());
		GObject upperRightCornerObject = getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()); 
		GObject lowerLeftCornerObject = getElementAt(ball.getX(),ball.getY()+2*BALL_RADIUS); 
		GObject lowerRightCornerObject = getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS); 
		if (upperLeftCornerObject != null) {
			return upperLeftCornerObject; 
		} else if (upperRightCornerObject != null) {
			return upperRightCornerObject; 
		} else if (lowerLeftCornerObject != null) {
			return lowerLeftCornerObject; 
		} else if (lowerRightCornerObject != null) {
			return lowerRightCornerObject;
		} else {
			return null; 
		}
	}

	/* Method: updateBrickDisplay
	 * -----------------------------
	 * The updateBrickDisplay method updates the display in the window
	 * to show the player how many bricks are left to break. 
	 */
	private void updateBricksLeftDisplay() {
		bricksLeftDisplay = printCenteredMessage("Bricks Left: " + brickCount,"SansSerif-14"); 
		add(bricksLeftDisplay); 
		bricksLeftDisplay.setLocation(0.05*getWidth(),0.025*getHeight() + bricksLeftDisplay.getAscent());
	}
	
	/* Method: updateTurnDisplay
	 * -----------------------------
	 * The updateTurnDisplay method updates the display in the window 
	 * to show the player how many turns they have left. 
	 */
	private void updateTurnDisplay() {
		int turnsLeft = NTURNS - (turn); 
		turnDisplay = printCenteredMessage("Turns Left: " + turnsLeft,"SansSerif-14"); 
		add(turnDisplay); 
		turnDisplay.setLocation(0.95*getWidth()-turnDisplay.getWidth(),0.025*getHeight()+turnDisplay.getAscent());
	}
	
	/* Method: updateScoreDisplay
	 * --------------------------
	 * The updateScoreDisplay method updates the player's score in the 
	 * window based on the color brick the player hits. 
	 */
	private void updateScoreDisplay() {
		scoreDisplay = printCenteredMessage("Score: " + score, "SansSerif-14");
		add(scoreDisplay); 
		scoreDisplay.setLocation((getWidth()-scoreDisplay.getWidth())/2,0.025*getHeight() + scoreDisplay.getAscent()); 
	}
	
	/* Method: increaseSpeed
	 * ----------------------
	 * The increaseSpeed method increases the velocity of the ball 
	 * as more bricks are broken. 
	 */
	private double increaseSpeed() {
		if(brickCount >= NBRICKS-NBRICKS/4) {
			return DELAY; 
		} else if(brickCount < NBRICKS-NBRICKS/4 && brickCount >= NBRICKS-2*NBRICKS/4){
			return DELAY*3/4; 
		} else if (brickCount < NBRICKS-2*NBRICKS/4 && brickCount >= NBRICKS-3*NBRICKS/4){
			return DELAY*2/3; 
		} else {
			return DELAY*1/2; 
		}
	}
	
	/* Method: printCenteredMessage
	 * ---------------------
	 * The printCentered Message method returns a message to the center 
	 * of the screen 
	 * Parameters: 
	 * message - the message you want displayed in the window 
	 */
	private GLabel printCenteredMessage(String message, String textSize) {
		GLabel label = new GLabel(message);
		label.setFont(textSize);
		double labelX = (getWidth()-label.getWidth())/2; 
		double labelY = (getHeight()+label.getAscent())/2;
		label.setLocation(labelX,labelY);
		return label; 
	}

	/* Method: winGraphic
	 * The winGraphic method displays an exciting graphic and message 
	 * when the player successfully breaks all of the bricks. 
	 */
	private void winGraphic() {
		AudioClip winSound = MediaTools.loadAudioClip("winSound.mp3");
		winSound.play(); 
		for(int i = 0; i < 14; i++){
			GRect rect1 = makeRect(rgen.nextColor(),getWidth(),getHeight()); 
			add(rect1,(getWidth()-rect1.getWidth())/2,(getHeight()-rect1.getHeight())/2);
			GRect rect2 = makeRect(rgen.nextColor(),0.8*getWidth(),0.8*getHeight()); 
			add(rect2,(getWidth()-rect2.getWidth())/2,(getHeight()-rect2.getHeight())/2);
			GRect rect3 = makeRect(rgen.nextColor(),0.6*getWidth(),0.6*getHeight()); 
			add(rect3,(getWidth()-rect3.getWidth())/2,(getHeight()-rect3.getHeight())/2);
			GRect rect4 = makeRect(rgen.nextColor(),0.4*getWidth(),0.4*getHeight()); 
			add(rect4,(getWidth()-rect4.getWidth())/2,(getHeight()-rect4.getHeight())/2);
			GRect rect5 = makeRect(rgen.nextColor(),0.2*getWidth(),0.2*getHeight()); 
			add(rect5,(getWidth()-rect5.getWidth())/2,(getHeight()-rect5.getHeight())/2);
			pause(DELAY*10); 
		}
		GLabel youWin = printCenteredMessage("You Win!","SansSerif-20");
		add(youWin); 
	}
	
/* private instance variables */ 
	private GRect paddle = null; 
	private double vx, vy; 
	private RandomGenerator rgen = RandomGenerator.getInstance(); 
	private GOval ball = null; 
	private int turn; 
	private int brickCount = NBRICKS; 
	private GLabel bricksLeftDisplay = null; 
	private GLabel turnDisplay = null; 
	private GLabel scoreDisplay = null;
	private int scoreFactor; 
	private int score; 
}