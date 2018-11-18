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
	public static final double BRICK_HEIGHT = 10;

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
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// the mouse's x coordinate
	private double mouseX;

	// the paddle
	private GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);

	//the ball
	private GOval ball;

	//the ball's speed in the x and y direction
	private double vx, vy=5;

	//original number of bricks
	public static final int ORIGINAL_N_BRICKS = NBRICK_ROWS*NBRICK_COLUMNS;

	//starting value of the number of bricks. Not static so it can change as bricks are removed
	private int numberOfBricks = NBRICK_ROWS*NBRICK_COLUMNS;

	// how big the button is. there must always be four buttons.
	private double buttonHeight = (CANVAS_HEIGHT-10)/(2);

	// ready message before each run through
	private GLabel ready;

	// lives left message before each run through
	private GLabel livesLeft;

	//how many lives the player starts with
	private int lives=3;

	// amount by which the game will speed up when the ball hits a brick
	public static final double SPEED_ENHANCER = .075;

	private double secretSpeedEnhancer = 0;

	// standard color style colors
	private Color standardColor1 = Color.RED;
	private Color standardColor2 = Color.ORANGE;
	private Color standardColor3 = Color.YELLOW;
	private Color standardColor4 = Color.GREEN;
	private Color standardColor5 = Color.CYAN;
	private Color standardBallColor = Color.BLACK;
	private Color standardPaddleColor = Color.BLACK;

	// black and white style colors
	private Color blackAndWhiteColor1 = Color.BLACK;
	private Color blackAndWhiteColor2 = Color.GRAY;
	private Color blackAndWhiteColor3 = Color.BLACK;
	private Color blackAndWhiteColor4= Color.GRAY;
	private Color blackAndWhiteColor5 = Color.BLACK;
	private Color blackAndWhiteBallColor = Color.LIGHT_GRAY;
	private Color blackAndWhitePaddleColor = Color.DARK_GRAY;

	//tropical style colors
	private Color tropicalColor1 = Color.CYAN;
	private Color tropicalColor2 = Color.YELLOW;
	private Color tropicalColor3 = Color.MAGENTA;
	private Color tropicalColor4 = Color.ORANGE;
	private Color tropicalColor5 = Color.PINK;
	private Color tropicalBallColor = Color.GREEN;
	private Color tropicalPaddleColor = Color.BLUE;

	//fire style colors
	private Color fireColor1 = Color.RED;
	private Color fireColor2 = Color.YELLOW;
	private Color fireColor3 = Color.ORANGE;
	private Color fireColor4 = Color.YELLOW;
	private Color fireColor5 = Color.RED;
	private Color fireBallColor = Color.BLUE;
	private Color firePaddleColor = Color.GRAY;

	// GRect color variables
	private Color color1;
	private Color color2;
	private Color color3;
	private Color color4;
	private Color color5;
	private Color ballColor;
	private Color paddleColor;

	// set style prompt
	private GLabel styleQ;

	// buttons
	private GRect standardButton;
	private GRect blackAndWhiteButton;
	private GRect tropicalButton;
	private GRect fireButton;
	private GRect secretButton;

	// button labels
	GLabel standardLabel;
	GLabel blackAndWhiteLabel;
	GLabel tropicalLabel;
	GLabel fireLabel;

	// random number generator that is used to decide vx
	private RandomGenerator rgen = RandomGenerator.getInstance();

	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	/* the run method
	 * keeps track of the mouse
	 * allows the player to choose style (click anywhere you want)
	 * builds game in accordance to style
	 * plays the game until the player has zero lives or there are no more bricks
	 * displays corresponding messages
	 */
	public void run() {
		addMouseListeners();
		setStyle();
		buildGame();
		playGame();
	}

	// sets the color style
	private void setStyle() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the Canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// creates a secret button
		addSecretbutton();
		// does what it says it does
		addStyleQ();
		addButtons();
		addLabelsToButtons();
		// click will choose a style
		waitForClick();
		// clear the screen so the game can build
		removeButtonsAndLabels();
	}

	// adds the style question	
	private void addStyleQ() {
		styleQ = new GLabel("What's your style?");
		styleQ.setLocation(getWidth()*.5-styleQ.getWidth()*.5, styleQ.getAscent());
		add(styleQ);		
	}

	// adds buttons using getButton method
	private void addButtons() {
		standardButton=getButton(standardColor1, 1, 1);
		blackAndWhiteButton=getButton(blackAndWhiteColor2, 2, 1);
		fireButton=getButton(fireColor2, 1, 2);
		tropicalButton=getButton(tropicalColor3, 2, 2);
		add(standardButton);
		add(blackAndWhiteButton);
		add(tropicalButton);
		add(fireButton);
	}

	// adds labels using getLabel method
	private void addLabelsToButtons() {
		standardLabel = getLabel("standard", 1, 1);
		blackAndWhiteLabel = getLabel("Black and White", 2, 1);
		tropicalLabel = getLabel("Let's Get Tropical!", 2, 3);
		fireLabel = getLabel("FIRE", 1, 3);
		add(standardLabel);
		add(blackAndWhiteLabel);
		add(tropicalLabel);
		add(fireLabel);
	}

	//removes buttons and labels
	private void removeButtonsAndLabels() {
		remove(styleQ);
		remove(standardButton);
		remove(blackAndWhiteButton);
		remove(fireButton);
		remove(tropicalButton);
		remove(standardLabel);
		remove(blackAndWhiteLabel);
		remove(tropicalLabel);
		remove(fireLabel);
		remove(secretButton);
	}

	// takes box color, box row and box column and returns a button
	private GRect getButton(Color buttonColor, int buttonRow, int buttonColumn) {
		double buttonX;
		double buttonY;
		if (buttonRow == 1) {
			buttonX = 0;
		} else {
			buttonX = getWidth()*.5;
		}
		if (buttonColumn == 1) {
			buttonY = styleQ.getY()+10;
		} else {
			buttonY = styleQ.getY()+10+buttonHeight;
		}
		GRect button = new GRect(buttonX,buttonY, getWidth()*.5,buttonHeight);
		button.setFilled(true);
		button.setFillColor(buttonColor);
		return(button);
	}

	// takes text, row and column and returns a label
	public GLabel getLabel(String text, double labelColumn, double labelRow ) {
		GLabel label = new GLabel(text);
		double labelY = (10+buttonHeight*.5*(labelRow)+styleQ.getAscent());
		if (labelColumn == 1) {
			label.setLocation(getWidth()*.25 - label.getWidth()*.5, labelY);
		} else {
			label.setLocation(getWidth()*.75-label.getWidth()*.5, labelY);
		}
		return (label);
	}

	// sets up the initial conditions of the game taking into account color style
	private void buildGame() {
		// adds bricks
		addBricks();
		// adds paddle
		addPaddle();
		// adds ball
		addBall();
	}

	// adds rows of bricks
	// rows have various different colors based on quintile
	private void addBricks() {
		for(int j=0; j < NBRICK_ROWS; j++) {
			if(j < (NBRICK_ROWS*.2)) {
				drawRow(color1, j);
			}else if (j < (NBRICK_ROWS*.4)){
				drawRow(color2, j);
			} else if (j < (NBRICK_ROWS*.6)) {
				drawRow(color3, j);
			} else if (j < (NBRICK_ROWS*.8)) {
				drawRow(color4, j);
			}else if (j < (NBRICK_ROWS)) {
				drawRow(color5, j);
			}
		}
	}

	// draws a row with input row color and row number from top
	private void drawRow(Color rowColor, double rowNumber) {
		for (int i=0; i<NBRICK_COLUMNS; i++) {
			GRect brick = new GRect(BRICK_SEP + (BRICK_WIDTH+BRICK_SEP)*i, BRICK_Y_OFFSET+((rowNumber)*(BRICK_HEIGHT+BRICK_SEP)), BRICK_WIDTH, BRICK_HEIGHT);
			brick.setColor(rowColor);
			brick.setFilled(true);
			add(brick);
		}
	}

	// adds a paddle to the center of the screen at the bottom
	private void addPaddle() {
		paddle.setLocation(getWidth()*.5-PADDLE_WIDTH*.5, getHeight()-PADDLE_Y_OFFSET);
		paddle.setFilled(true);
		paddle.setFillColor(paddleColor);
		add(paddle);			
	}

	// adds a ball to the center of the screen
	private void addBall() {
		ball = new GOval(getWidth()*.5-BALL_RADIUS, getHeight()*.5-BALL_RADIUS,BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(ballColor);
		add(ball);
	}
	// the part where the game is played
	private void playGame() {
		while (lives > 0) {
			addPreGameMessage();
			runGame();
			if(numberOfBricks==0) {
				break;
			}
		}
		// if all the bricks are gone the player wins
		// if not the player has lost
		if (numberOfBricks==0) {
			remove(paddle);
			remove(ball);
			if(secretBoolean()) {
				showSecretMessage();
			} else {
				GLabel victory = new GLabel("you win!");
				victory.setLocation(getWidth()*.5-victory.getWidth()*.5, getHeight()*.5+victory.getAscent()*.5);
				add(victory);
			}
		} else {
			remove(paddle);
			remove(ball);
			GLabel defeat = new GLabel("you lose! Idiot!");
			defeat.setLocation(getWidth()*.5-defeat.getWidth()*.5, getHeight()*.5+defeat.getAscent()*.5);
			add(defeat);
		}
	}

	// message that pops up before each game run-through
	// 
	private void addPreGameMessage() {
		ready = new GLabel("READY? Click to begin!");
		ready.setLocation(getWidth()*.5-ready.getWidth()*.5, getHeight()*.5+BALL_RADIUS*4);
		livesLeft = new GLabel("LIVES LEFT"+" "+lives);
		livesLeft.setLocation(getWidth()*.5-livesLeft.getWidth()*.5, getHeight()*.5+BALL_RADIUS*6);
		add(ready);
		add(livesLeft);
		waitForClick();
		remove(ready);
		remove(livesLeft);
	}

	/* 
	 * assigns a random value to the ball's speed in the x direction
	 * the ball starts moving toward the bottom of the screen
	 * if the ball hits the side walls or ceiling it bounces off
	 * if the ball hits the floor the player loses a life, the ball resets, and the run-through stops
	 * if the ball uses getColider to see if it collides against the paddle or a brick
	 *  it bounces of the paddle, but only upwards. This fixes sticky paddle
	 *  it bounces off bricks and removes them
	 *  it keeps track of how many bricks there are
	 *  the ball moves a little faster as each brick is removed
	 *  if all the bricks get removed the run through ends
	 *  if the player loses a life the run through ends
	 *  makes an extremely irritating noise when it hits the paddle or a brick
	 */
	private void runGame() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while(true) {
			ball.move(vx, vy);
			if (ball.getX()+2*BALL_RADIUS >= getWidth()) {
				vx= -vx;
			}
			if (ball.getX() <= 0) {
				vx= -vx;
			}
			if (ball.getY()+2*BALL_RADIUS >= getHeight()) {
				lives = lives -1;
				ball.setLocation(getWidth()*.5-BALL_RADIUS, getHeight()*.5-BALL_RADIUS);
				break;
			}
			if (ball.getY() <= 0) {
				vy= -vy;
			}
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy=-Math.abs(vy);
				vy=vy-secretSpeedEnhancer;
				bounceClip.play();
			} else if (collider != paddle && collider != null) {
				vy=-vy;
				remove(collider);
				numberOfBricks= numberOfBricks-1;
				bounceClip.play();
				if (vy > 0) {
					vy=vy+SPEED_ENHANCER;
				} else {
					vy=vy-SPEED_ENHANCER;
				}
			}
			if(numberOfBricks== 0) {
				break;
			}
			pause(DELAY);
		}
	}
	//reads what the ball is bouncing of of
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY())!= null) {
			return(getElementAt(ball.getX(), ball.getY()));
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY())!= null) {
			return(getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()));
		} else if (getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS) != null) {
			return(getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS));
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS) != null) {
			return(getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS));
		} else {
			return(null);
		}
	}
	// keeps track of where the mouse is 
	// sets the center of the paddle to be where the x coordinate of the mouse is as the paddle stays within the bounds of the screen
	// also keeps track of where mouse is and changes button color
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		if (e.getX() <= getWidth() - PADDLE_WIDTH*.5 && e.getX() >= PADDLE_WIDTH*.5) {
			paddle.setLocation(mouseX-PADDLE_WIDTH*.5, getHeight()-PADDLE_Y_OFFSET);
		}
		if  (getElementAt(e.getX(), e.getY()) == standardButton) {
			standardButton.setFillColor(standardColor2);
		} else {
			standardButton.setFillColor(standardColor1);
		}
		if  (getElementAt(e.getX(), e.getY()) == blackAndWhiteButton) {
			blackAndWhiteButton.setFillColor(Color.WHITE);
		} else {
			blackAndWhiteButton.setFillColor(blackAndWhiteColor2);
		}
		if  (getElementAt(e.getX(), e.getY()) == tropicalButton) {
			tropicalButton.setFillColor(tropicalColor5);
		} else {
			tropicalButton.setFillColor(tropicalColor3);
		}
		if  (getElementAt(e.getX(), e.getY()) == fireButton) {
			fireButton.setFillColor(fireColor3);
		} else {
			fireButton.setFillColor(fireColor2);
		}
	}

	// if you click a button the colors get set
	public void mouseClicked(MouseEvent e2) {
		if (getElementAt(e2.getX(), e2.getY()) == standardButton) {
			setStandard();
		} else if (getElementAt(e2.getX(), e2.getY()) == blackAndWhiteButton) {
			setBlackAndWhite();
		} else if (getElementAt(e2.getX(), e2.getY()) == tropicalButton) {
			getTropical(); /*get it?*/
		} else if (getElementAt(e2.getX(), e2.getY()) == fireButton) {
			setFire();
		} else if (getElementAt(e2.getX(), e2.getY()) == secretButton) {
			doSomethingSecret();
		}
	}	
	// set colors to be the selected colors
	private void setStandard() {
		color1 = standardColor1;
		color2 = standardColor2;
		color3 = standardColor3;
		color4 = standardColor4;
		color5 = standardColor5;
		ballColor = standardBallColor;	
		paddleColor = standardPaddleColor;
	}
	private void setBlackAndWhite() {
		color1 = blackAndWhiteColor1;
		color2 = blackAndWhiteColor2;
		color3 = blackAndWhiteColor3;
		color4 = blackAndWhiteColor4;
		color5 = blackAndWhiteColor5;
		ballColor = blackAndWhiteBallColor;
		paddleColor = blackAndWhitePaddleColor;
	}
	private void getTropical() {
		color1 = tropicalColor1;
		color2 = tropicalColor2;
		color3 = tropicalColor3;
		color4 = tropicalColor4;
		color5 = tropicalColor5;
		ballColor = tropicalBallColor;
		paddleColor = tropicalPaddleColor;
	}
	private void setFire() {
		color1 = fireColor1;
		color2 = fireColor2;
		color3 = fireColor3;
		color4 = fireColor4;
		color5 = fireColor5;
		ballColor = fireBallColor;
		paddleColor = firePaddleColor;
	}




















	// adds the secret button at the top of the screen
	private void addSecretbutton() {
		secretButton = new GRect (0,0,getWidth(),22);
		add(secretButton);
	}
	// does something secret
	private void doSomethingSecret() {
		doSomethingEvenSecreter();
	}
	/* all that happens when you choose the secret level is the secret speed enhancer increases
	 * the secret speed enhancer is zero for the other styles
	 * the colors for the secret level are what you get if you don't give any colors to the buildGame method
	 * it originated as sort of a glitch
	 * I had this empty space and the game would still run if you clicked it
	 * and it was all black
	 * so I told people it was a secret level and they seemed hyped about it
	 * so I made it an actual secret level
	 */
	private void doSomethingEvenSecreter() {
		secretSpeedEnhancer = .025;
	}
	// if the speed enhancer isn't zero the player played the secret level
	private boolean secretBoolean() {
		if(secretSpeedEnhancer!=0) {
			return true;
		} else {
			return false;
		}
	}
	// you get a special message if you beat the secret level
	private void showSecretMessage() {
		GLabel secretMessage = new GLabel("You beat the secret level! Congratulations!");
		secretMessage.setLocation(getWidth()*.5-secretMessage.getWidth()*.5, getHeight()*.5+secretMessage.getAscent()*.5);
		add(secretMessage);
	}

}
