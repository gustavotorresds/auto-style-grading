/*
 * File: Breakout.java
 * -------------------
 * Name:Jess De Suza
 * Section Leader: Rhea Karuturi
 * 
 * This file implements the game of Breakout, with extensions.
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
	
	private static final double DAMPING = 0.85;
	/* The damping from the force of static friction */
	private static final double STATIC_FRICTION = 0.05;
	/* The damping from the force of friction */
	private static final double FRICTION = 0.999;

	// Private instance variables, accessible to the entire code.
	private int score = 0;
	private GLabel startMessage;
	private GRect paddle;
	private GOval ball;
	private GLabel scorelabel;
	private double vx, vy;
	private double x, y;
	private int paddleHit;
	// Calculates the number of bricks remaining
	private int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;
	private static final int NUMBER_OF_BRICKS = NBRICK_COLUMNS * NBRICK_ROWS;
	
	//Audio sounds for the ball bouncing, the game music and the win or lose sounds.
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	AudioClip music = MediaTools.loadAudioClip("bensound-moose.au");
	AudioClip winSound = MediaTools.loadAudioClip("audience_applause-matthiew11-1206899159.au");
	AudioClip loseSound = MediaTools.loadAudioClip("uuuuuu-paula-1357936016.au");
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	
	// Color themes for the game
	private int gameTheme = 0;  // current theme.
	private final int DEFAULT = 1;
	private final int FUNKY = 2;
	
	// Theme select labels
	private GLabel defaultTheme, funkyTheme;
	
	// Game state
	private int currentState;

	private final int THEME_SELECT = 1;
	private final int PLAYING = 2;

	
	
	public void run() {
		// Sets the score of the player
		int score = NUMBER_OF_BRICKS - bricksRemaining;
		
		// Initialises the score of the player as 0 
		scorelabel = new GLabel("score = " + score);
		scorelabel.setCenterLocation(getWidth()/2, getHeight()-440);
		
		// Sets up the game
		setUpGame();
		addMouseListeners();
		
		// waits for user to click before launching ball, and removes start message
		waitForClick();
		remove (startMessage);
		if (playGame()) {
			winSequence();
		} else {
			loseSequence();
		}			
	}
	
	// adds image and sounds appropriate for winning the game
	private void winSequence() {
		removeAll();
		GImage youwin = new GImage("youwin.jpg");
		youwin.setBounds(0, 0,getWidth(),getHeight());
		add(youwin);
		music.stop();
		winSound.play();
	}
	
	
	// adds image and sounds appropriate for losing the game
	private void loseSequence() {
		removeAll();
		GImage youlose = new GImage("loser.jpg");
		youlose.setBounds(0, 0,getWidth(),getHeight());
		add(youlose);
		music.stop();
		loseSound.play();
	}
	
	private void setUpGame() {
		
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		
		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Centers the bricks
		x = (getWidth()-NBRICK_COLUMNS*BRICK_WIDTH-(BRICK_SEP*NBRICK_COLUMNS-BRICK_SEP))/2.0; 
	 	y = BRICK_Y_OFFSET;
	 	
	 	// Theme select menu
	 	currentState = THEME_SELECT;
	 	GLabel themeSelect = new GLabel("Select a theme:");
	 	
	 	// Tells the loser the rules of the game
	 	GLabel turns = new GLabel("You have " + NTURNS + " lives. Use them well!");
	 	GLabel lives = new GLabel("When you lose one, an obstacle appears.");
		turns.setCenterLocation(getWidth()/2, getHeight()/4+180);
		lives.setCenterLocation(getWidth()/2, getHeight()/4+200);
		
		// puts a frame around the current themeselect state
		GImage frame = new GImage("frame.jpg");
		frame.setBounds(0, 0,getWidth(),getHeight());
		add(frame);
		
		// Declares what the two themes of the game are 
	 	defaultTheme = new GLabel("Default");
	 	funkyTheme = new GLabel("Funky");
	 	
	 	// Sets the location of the theme select labels 
	 	themeSelect.setCenterLocation(getWidth()/2, getHeight()/4);
	 	defaultTheme.setCenterLocation(getWidth()/4, getHeight()/2);
	 	funkyTheme.setCenterLocation(getWidth() * 3 / 4, getHeight()/2);
	 	add(themeSelect);
	 	add(turns);
	 	add(lives);
	 	add(defaultTheme);
	 	add(funkyTheme);
	 	
	 	// Pauses whilst the current state is still the home screen
	 	while (currentState == THEME_SELECT) {
	 		pause(0);
	 	}
	 	
	 	
	 	// When the user clicks, it removes the themeselect labels 
	 	remove(themeSelect);
	 	remove(defaultTheme);
	 	remove(funkyTheme);
	 	remove(turns);
	 	remove(lives);
	 	remove(frame);
	 			
	 	// Sets up the game screen 
	 	setUpGameScreen();
	}
	
	
	private void setUpGameScreen() {
		startMessage = new GLabel("Click to start.");
		startMessage.setCenterLocation(getWidth()/2, getHeight()/4+200);
		add(startMessage);
		add(scorelabel);
	 	createBricks();
		createPaddle();
		createBall();
		
	}

	/**
	 *  If the user clicks a theme option, the current state changes from themeselect to 
	 *  playing the game.
	 */
	public void mouseClicked(MouseEvent e) {
		if (currentState == THEME_SELECT) {
			GObject theme = getElementAt(e.getX(), e.getY());
			if (theme != null) {
				if (theme == funkyTheme) {
					gameTheme = FUNKY;
					currentState = PLAYING;
				}
				else if (theme == defaultTheme) {
					gameTheme = DEFAULT;
					currentState = PLAYING;
				}
			}
		}
	}
	
	// Sets up the bricks and rows with alternating colors every two rows, avoiding the same colors repeating
	private void createBricks() {
		Color brickColor = null;
		for (int i = 0; i < NBRICK_ROWS/2; i++) {
			brickColor = nextColor(brickColor);
			setUpBricks(brickColor);
		}
	}

	
	/**
	 *  Randomly selects the next color in the funky game theme, 
	 *  but sticks to original colors in default theme. Returns the next color, avoiding 
	 *  repeating colors.
	 *
	 */
	private Color nextColor(Color color) {
		switch (gameTheme) {
			case FUNKY:
				Color next;
				
				// Ensures that the do while loop is done at least once, when there is no color initially.
				do {
					next = rgen.nextColor();
				} while (next == color);
				return next;

			default:
				if (color == null) return Color.RED;
				if (color == Color.RED) return Color.ORANGE;
				if (color == Color.ORANGE) return Color.YELLOW;
				if (color == Color.YELLOW) return Color.GREEN;
				if (color == Color.GREEN) return Color.CYAN;
		}
		return null;
	}

	private void setUpBricks(Color color) {
		
		/**
		 * Creates the bricks set up.
		 * x is centered in the middle of the screen, with the same number of bricks either side.
		 * y is declared as being the height of the screen take away the height of a single brick.  
		 *  Creates bricks for each row, taking away one each time. 
		 */
		for (int rows = 0; rows < 2; rows++) {
			for (int bricks = 0; bricks < NBRICK_COLUMNS; bricks++) {
				GRect rect = new GRect(x, y,BRICK_WIDTH,BRICK_HEIGHT);
				rect.setFilled(true);
				rect.setFillColor(color);
				rect.setColor(color);
				add(rect);
				x += BRICK_WIDTH+BRICK_SEP;
			}
			x = (getWidth()-NBRICK_COLUMNS*BRICK_WIDTH-(BRICK_SEP*NBRICK_COLUMNS-BRICK_SEP))/2.0;;  
			y+=BRICK_HEIGHT+BRICK_SEP;
			
		}
	
	}
	
	// Creates and positions paddle
	private void createPaddle() {
		paddle = new GRect(getWidth()/2, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	
	
	public void mouseMoved(MouseEvent e) {
		
		/**
		 * As the mouse hovers above the labels, they get bolder and bigger in the themeselect state.
		 * Same as mousereporter code.
		 */
		
		if (currentState == THEME_SELECT) {
			GObject label = getElementAt(e.getX(), e.getY());
			
			if (label == defaultTheme) {
				defaultTheme.setFont("SanSerif-bold-15");
				defaultTheme.setCenterX(getWidth()/4);
			} else {
				defaultTheme.setFont("SanSerif-plain-12");
				defaultTheme.setCenterX(getWidth()/4);
			}
			
			if (label == funkyTheme) {
				funkyTheme.setFont("SanSerif-bold-15");
				funkyTheme.setCenterX(getWidth()*3/4);
			} else {
				funkyTheme.setFont("SanSerif-plain-12");
				funkyTheme.setCenterX(getWidth()*3/4);
			}
		}
		else {
			
			/**
			 * Playing mode. Moves the paddle along with the mouse. If the paddle gets
			 * to the edge of the screen, then the x values adjust so that it stays within the screen. 
			 */
			double p = e.getX();
			if (p<getWidth()-PADDLE_WIDTH ) {
				p = e.getX();
			} else {
				p = getWidth()-PADDLE_WIDTH;
			}
			paddle.setLocation(p,getHeight()-PADDLE_Y_OFFSET);
		}
	}
	
	
	// Creates and positions the ball
	private void createBall() {
		ball = new GOval(getWidth() /2-BALL_RADIUS/2,getHeight()/2+BALL_RADIUS/2,BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	// Plays the game for any given number of turns. Then returns a boolean depending on if game won or lost. 
	private boolean playGame() {
		for (int i=0; i<NTURNS; i++) {
			music.play();
			if (playTurn()) {
				return true;
			}
			
			/**creates a random x and y value for the meme 'come at me' to pop up on the screen each time 
			 * a life is lost. This meme acts as another obstaccle brick to destroy.
			 */
			double rx = rgen.nextDouble(100, 200);
			double ry = rgen.nextDouble(100, 200);
			GImage comeatme = new GImage("comeatme.jpg");
			comeatme.setBounds(rx,ry,120,70);
			add(comeatme);
		}
		return false;
	}
	
	private boolean playTurn() {
		
		// reset the ball position to the center at the start of each turn
		ball.setLocation(getWidth()/2-BALL_RADIUS/2,getHeight()/2+BALL_RADIUS/2);
		vy = 3.0;
		
		// generates a random velocity in the x direction
		vx = rgen.nextDouble(1.0, 3.0);
		
		// Equal probabilities of a positive or negtve x velocity
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		while (bricksRemaining > 0) {
			
			// Switches the x velocity of ball if the ball hits the right or left wall
			if (ball.getRightX() >= getWidth() || ball.getX() <= 0) {
				bounceClip.play();
				vx = -vx;
			}
			
			// Switches the y velocity of the ball if it hits the top wall
			if (ball.getY() <= 0) {
				bounceClip.play();
				vy = -vy;
			}			
			
			// Returns 'false'/game lost if the ball hits the bottom. 
			if (ball.getBottomY() >= getHeight()) {
				bounceClip.play();
				return false;
			}
			
			// Checks for collisions before the ball continues to move 
			checkCollisions();
			ball.move(vx,vy);
			pause(DELAY);
		}
		//returns true if the turn is won 
		return true;
		
	}
	
	
	
	private boolean checkCollisions() {
		
		/**
		 * Nested for loop which checks for collisions on all four corners of the ball.
		 * Returns with true if the ball has collided with a brick.
		 *If not, it returns false. 
		 */
		for (double dx=0; dx<=ball.getWidth(); dx+=ball.getWidth()) {
			for (double dy=0; dy<=ball.getHeight(); dy+=ball.getHeight()) {
				if (collisions(ball.getX() + dx,ball.getY() + dy)) {
					bounceClip.play();
					return true;
				}
			}
		}
		return false;	
	}
	
	/**
	 * The boolean returns true if the ball has collided with an element.
	 * If it is the paddle, it only switches the vertical direction.
	 * If it is a brick, it switches the vertical direction and also removes the element.
	 */
	private boolean collisions(double x,double y) {
		GObject collider = getElementAt(x,y);
		if (collider != null) {
			if(collider != paddle) {
				if (collider != scorelabel) {
					remove(collider);
				}
				bricksRemaining--;
				score++;
				
				// resets score when they've hit a brick, and changes vertical velocity
				scorelabel.setLabel("score = " + score);
				if (collider == scorelabel) {
					vy = vy;
				} else {
					vy = -vy;
				}
			} else if (collider == paddle){
				vy = -Math.abs(vy);
				paddleHit++;
				
				// speeds up the velocity after hitting the paddle every 5 times
				if (paddleHit % 5 == 0) {
					vy = vy*1.2;
				}
				

			} 
			return true;
		} 
		return false;
		
	}
}			
