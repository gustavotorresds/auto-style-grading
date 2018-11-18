/*
 * File: Breakout.java
 * -------------------
 * Name: Gary Schwartz garys1
 * Section Leader: Ben Barnett
 * 
 * This file will implements the game of Breakout.
 * 
 * Extensions: Play has the option to restart the game after he either wins or loses.
 * lives displayed as balls at bottom right, with a possibility of regaining lives by hitting stars that pop up. 
 * When you win, background goes crazy and displays a label. If you hit the corner of the paddle it reverses x direction and sends the 
 * ball back where it came from. Also, the ball can detect hitting the side of the bricks and reverse dx once it has done so.
 * Pretty realistic gameplay epecially happy I fixed bug where ball would plow through a group of bricks. Label to keep score.
 * I've added extra "sensors" to the ball. Also, bounce sound clip plays for bounces. 
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
	public static final double BRICK_WIDTH =
			(CANVAS_WIDTH - (NBRICK_COLUMNS - 1) * BRICK_SEP) / NBRICK_COLUMNS;

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
	public static final double DELAY = 1000.0 / 100.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";


	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		addMouseListeners();
		buildArena();
		playGame();
	}

	/**
	 * This Method creates the ball, gets the ball bouncing and starts game-play.
	 */
	private void addBallAndStartLabel() {
		addBall();
		addClickLabel();
	}

	/**
	 * Adds the label that tells player to click to play then removes it when he clicks, allowing gameplay because next method to be called is playGame();
	 */
	private void addClickLabel() {
		GLabel click = new GLabel ("Click to play");
		click.setFont(SCREEN_FONT);
		click.setLocation(getWidth()/2 - click.getWidth()/2, getHeight()* 2/3.0);
		add (click);		
		waitForClick();
		remove (click);
	}

	/**
	 * This method keeps the game going by bouncing the ball around the screen. It checks for impact with walls and objects through
	 * helper methods. This is where most of the game happens.
	 */
	private void playGame() {
		initializeBallDirection();
		while (!gameWon && !gameLost) {
			ball.move(vx, vy);
			pause(DELAY);
			sideHitTest();					//ball bounces when hits side walls
			objectCollideTest();				//checks to see if ball hits any objects (beside walls).		
			moveStarLife();
		}
	}

	/**
	 * Initializes ball direction.
	 */
	private void initializeBallDirection() {
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = 3.0;
		if (rgen.nextBoolean()) {  //half the time the ball starts heading to the right, half the time to the left
			vx = -vx;
		}		
	}

	/**
	 * This method moves the star life bonus until it leaves the screen then it removes it. 
	 */
	private void moveStarLife() {
		if (star!= null && star.getX() > getWidth()) {	
			remove (star);
			star = null;
		}
		if (star != null) {			
			star.move(1, 0);
		}		
	}

	/**
	 * This Method deals with the objects on the screen according to which one the ball collided with. i.e. if paddle, ball bounces, 
	 * if brick, destroys brick and then bounces.
	 */
	private void objectCollideTest() {
		GObject collidedObject = getCollidedObject(ball.getX(), ball.getY());
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		if (collidedObject == paddle) {
			bounceClip.play();
			vy = -Math.abs(vy);
			//if ball hits the corner of the paddle, it will reverse vx so the game is less boring.
			if (sideOfObjectIsHit (collidedObject)) {
				vx = -vx;
			}
		}

		//if you hit the star you get your life back, returns life counter to screen depending on which life you're on. 
		if (collidedObject==star && collidedObject!=null) {
			remove (star);
			if (ballsLeft==2) add (life1);
			if (ballsLeft==1) add (life2);
			if (ballsLeft==0) add (life3);
			ballsLeft++;
		}

		if (collidedObjectIsBrick (collidedObject)) {
			bounceClip.play();
			remove (collidedObject);
			bricksLeft--;
			resetScore();
			if (bricksLeft==0) {
				gameWon = true;   //one condition of ball continuing to move was gameWon = false; The other was gameLost = false;
				winGame();
			}
			checkForSideCollisions(collidedObject);
		}
	}

	/**
	 * This method checks for side collisions and acts depending on which side is hit, this makes a big difference in making the gameplay
	 * realistic and deals with the "plowing through a group of bricks" bug.
	 * @param collidedObject
	 */
	private void checkForSideCollisions(GObject collidedObject) {
		if (rightSideOfBrickIsHit (collidedObject)) {
			vx = Math.abs(vx);
		} else if (leftSideOfBrickIsHit (collidedObject)) {
			vx = -Math.abs(vx);
		} else if (!sideOfObjectIsHit (collidedObject)) {
			vy = -vy; 			//don't want y direction reversing if brick hit on the side, only if hit on top or bottom, hence second condition.
		}		
	}

	private boolean leftSideOfBrickIsHit(GObject collidedObject) {
		if ((ball.getX() + BALL_RADIUS) <= collidedObject.getX() && vx > 0) {
			return true;
		}		
		return false;
	}

	private boolean rightSideOfBrickIsHit(GObject collidedObject) {
		if (ball.getX() + BALL_RADIUS >= (collidedObject.getX() + BRICK_WIDTH) && vx<0) {
			return true;
		}		
		return false;
	}

	private boolean collidedObjectIsBrick(GObject collidedObject) {
		if (collidedObject != paddle && collidedObject!=null && collidedObject != life1 && collidedObject != score &&
				collidedObject !=life2 && collidedObject != life3 && collidedObject!=star) return true;
		else return false;
	}

	/**
	 * This Method displays the sequence when the player wins the game.
	 */
	private void winGame() {
		GLabel winner = new GLabel(("YOU WIN!"));
		winner.setFont(SCREEN_FONT);
		winner.setLocation(getWidth()/2 - winner.getWidth()/2, getHeight()/2 + winner.getAscent()/2);
		add (winner);
		//makes background go crazy with colors when you win for a little while 
		for(int i = 0; i < 1000; i++) {
			setBackground(rgen.nextColor());
			pause(5);
		}
		askToPlayAgain();
	}

	/**
	 * This Method decides whether the ball hit the object it collided with on the side of that object or not, returns a boolean, true if it does hit the side.
	 * @param collidedObject
	 * @return boolean
	 */
	private boolean sideOfObjectIsHit(GObject collidedObject) {
		if (collidedObject == paddle) {
			if ((ball.getX() + BALL_RADIUS) <= paddle.getX() && vx>0) {
				return true;
				//if ball collides with paddle and more than half of the ball is to the left of the left part of the paddle, should mean either hits corner or side of paddle
				//only runs if ball was moving to the right (positive direction).
			} else if (ball.getX() + BALL_RADIUS > paddle.getX() + PADDLE_WIDTH && vx<0) {
				return true;
				//if ball collides with paddle and more than half of the ball is to the right of the right part of the paddle, should mean either hits corner or side of paddle
				//only runs if ball was moving to the left (negative direction).
			}
		}

		//hits brick
		if (collidedObject != paddle) {
			if ((ball.getX() + BALL_RADIUS) <= collidedObject.getX() && vx > 0) {
				return true;
			} else if (ball.getX() + BALL_RADIUS >= (collidedObject.getX() + BRICK_WIDTH) && vx<0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This Method returns the object that the ball collided with.
	 */
	private GObject getCollidedObject(double x, double y) {

		if (getElementAt (x,y) != null) return getElementAt (x,y);										//top left corner of object adjusted to be slightly closer to actual ball
		if (getElementAt (x, y + 2*BALL_RADIUS) != null) return getElementAt (x,y + BALL_RADIUS * 2);				//bottom left corner of object
		if (getElementAt (x-1, y + BALL_RADIUS - 4) != null) return getElementAt (x-1, y + BALL_RADIUS - 4); //middle left offset by one so doesn't return ball
		if (getElementAt (x-1, y + BALL_RADIUS + 4) != null) return getElementAt (x-1, y + BALL_RADIUS + 4); //middle left offset by one so doesn't return ball

		if (getElementAt (x + 2*BALL_RADIUS, y) != null) return getElementAt (x + BALL_RADIUS * 2, y);		//top right corner of object adjusted to be slightly closer to actual ball
		if (getElementAt (x + 2*BALL_RADIUS, y + BALL_RADIUS * 2) != null) return getElementAt (x + BALL_RADIUS * 2,y + BALL_RADIUS * 2);	//bottom right corner of object
		if (getElementAt (x + 2*BALL_RADIUS + 1, y + BALL_RADIUS + 4) != null) return getElementAt (x + BALL_RADIUS * 2 + 1, y + BALL_RADIUS + 4); //middle right offset by one so doesn't return ball
		if (getElementAt (x + 2*BALL_RADIUS + 1, y + BALL_RADIUS - 4) != null) return getElementAt (x + BALL_RADIUS * 2 + 1, y + BALL_RADIUS - 4); //middle right offset by one so doesn't return ball
		return null;
	}

	/**
	 * This Method checks whether the object has come into contact with walls or other objects and acts accordingly
	 */
	private void sideHitTest() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		if (ball.getX()<=0 || (ball.getX() + BALL_RADIUS * 2) >= getWidth()) {
			bounceClip.play();
			if ((ball.getX() + BALL_RADIUS * 2) >= getWidth()) { 			//ball hits right wall
				vx = -Math.abs (vx);							//don't want the ball to vibrate along the wall so made absolute value.
			}
			if (ball.getX() <= 0) {										//ball hits left wall
				vx = Math.abs (vx);
			}
		}

		//the following implements actions that are common to the ball hitting the top as well as bottom of the screen.
		if (ball.getY() <= 0 || (ball.getY() + BALL_RADIUS * 2) >= getHeight()) {
			if (ball.getY() <= 0) {
				bounceClip.play();
				vy = Math.abs(vy);
			}
		}

		//the following implements actions that should take place when ball hits the bottom of the screen
		if ((ball.getY() + BALL_RADIUS * 2) >= getHeight()) {
			remove (ball);
			ballsLeft--;
			if (star!= null) remove (star);  //I only give the player a chance to regain a life only for one life or until the star goes off the screen.
			handleLivesLeft();
			if (ballsLeft!=0) {			//resets the ball in middle and waits for click to proceed with game.
				addBallAndStartLabel();
			}
		}
	}

	/**
	 * This method keeps track of lives left as well as creating balls representing those lives. It implements the endGame method
	 * if there are no lives left. It also creates stars that the player can use to regain lives (only one chance).
	 */
	private void handleLivesLeft() {
		//The following switch statement deals with the lives the player has left appearing as balls on the bottom right
		switch (ballsLeft) {
		case (2): remove (life1);
		addStarLife(); break;
		case (1): remove (life2); break;
		case (0): remove (life3); 
		gameLost = true; //so ball will stop moving, that was condition of its continued bouncing around
		loseGame();		
		}		
	}

	/**
	 * This method adds a moving star to the canvas when the first life is lost to give the player a chance to gain it back.
	 * I might move it to when there is only one life left though when the player is most desperate. 
	 */
	private void addStarLife() {
		star = new GStar(18);
		star.setFilled(true);
		star.setColor(rgen.nextColor());
		star.setLocation(rgen.nextDouble(0,getWidth()/2 - star.getWidth()), rgen.nextDouble(getHeight()/2, 
				getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - 10));
		add (star);		
	}

	/**
	 * This Method adds a label indicating you've lost when you lose the game.
	 */
	private void loseGame() {
		GLabel loser = new GLabel(("YOU LOSE!"));
		loser.setFont(SCREEN_FONT);
		loser.setLocation(getWidth()/2 - loser.getWidth()/2, getHeight()/2 + loser.getAscent()/2);
		add (loser);	
		askToPlayAgain();
	}

	/**
	 * When the player either wins or loses, has the option to play again.
	 */
	private void askToPlayAgain() {
		GLabel playAgain = new GLabel ("Click to Play Again");
		playAgain.setFont (SCREEN_FONT);
		playAgain.setLocation(getWidth()/2 - playAgain.getWidth()/2, 3 * getHeight() / 4);
		add (playAgain);
		waitForClick();
		removeAll();
		ballsLeft = 3;
		gameWon = false;
		gameLost = false;
		bricksLeft = 0;
		this.run();
	}

	/**
	 * This Method adds a ball to the center of the screen.
	 */
	private void addBall() {
		ball = new GOval(getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add (ball);
	}

	/**
	 * This Method moves the paddle along with mouse movement and keeps it from going off the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double x = e.getX() - PADDLE_WIDTH/2;
		if (paddleOnScreen(e)) {
			paddle.setLocation(x, y);
		}
	}

	/**
	 * This method checks whether the paddle is still fully on the screen, if it is, it returns true, if not false.
	 * @param MouseEvent e
	 * @return boolean
	 */
	private boolean paddleOnScreen(MouseEvent e) {
		return (e.getX()+PADDLE_WIDTH/2)<getWidth() && (e.getX() - PADDLE_WIDTH/2)>0;
	}

	/**
	 * This Method sets the size of the application, adds the bricks, ball, and paddle to the screen.
	 */
	private void buildArena() {
		setTitle("CS 106A Breakout");
		setSize((int)CANVAS_WIDTH, (int)CANVAS_HEIGHT);
		setUpBricks();
		setUpPaddle();
		addLives();
		addScoreLabel();
		addBallAndStartLabel();
	}

	/**
	 * This resets the score each time a brick is removed.
	 */
	private void resetScore() {
		remove (score);
		addScoreLabel();
	}

	/**
	 * This makes a label containing score of player (how many bricks he has hit).
	 */
	private void addScoreLabel() {
		score = new GLabel ("Bricks left: " + (bricksLeft));
		score.setFont(SCREEN_FONT);
		score.setLocation(10, getHeight() - score.getDescent());
		add (score);
	}

	/**
	 * This method puts the "lives" on the screen, the little black balls on the bottom left that tell you how many balls you have left.
	 */
	private void addLives() {
		double SPACE_BETWEEN_LIVES = 5;
		double LIFE_DIAMETER = BALL_RADIUS;
		double x = getWidth() - LIFE_DIAMETER - SPACE_BETWEEN_LIVES;
		double y = getHeight() - LIFE_DIAMETER - SPACE_BETWEEN_LIVES;
		life3 = new GOval (x, y, LIFE_DIAMETER, LIFE_DIAMETER);
		life3.setFilled(true);
		add (life3);
		life2 = new GOval (x - LIFE_DIAMETER - SPACE_BETWEEN_LIVES, y, LIFE_DIAMETER, LIFE_DIAMETER);
		life2.setFilled(true);
		add (life2);
		life1 = new GOval (x - LIFE_DIAMETER * 2 - SPACE_BETWEEN_LIVES * 2, y, BALL_RADIUS, BALL_RADIUS);
		life1.setFilled(true);
		add (life1);
	}

	/**
	 * This Method sets up the paddle.
	 */
	private void setUpPaddle() {
		paddle = new GRect(getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT,PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);
	}

	/**
	 * This Method sets up the bricks of the game.
	 */
	private void setUpBricks() {
		for(int i=0; i<NBRICK_ROWS; i++) {
			for(int j=0; j<NBRICK_COLUMNS; j++) {
				double x = (getWidth()/2-BRICK_WIDTH*(NBRICK_COLUMNS/2.0)-(BRICK_SEP * (NBRICK_COLUMNS-1)/2.0)) + (BRICK_WIDTH + BRICK_SEP)*j;
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT+BRICK_SEP)*i;
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				switch (i) {
				case 0:case 1: brick.setColor(Color.RED); break;

				case 2:case 3: brick.setColor(Color.ORANGE); break;

				case 4:case 5: brick.setColor(Color.YELLOW); break;

				case 6:case 7: brick.setColor(Color.GREEN); break;

				case 8:case 9: brick.setColor(Color.CYAN); break;

				}
				add (brick);
				bricksLeft++;
			}
		}
	}
	private GRect paddle;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GOval ball;
	private double vx;
	private double vy;
	private int bricksLeft;
	private int ballsLeft = 3;
	private GOval life1;
	private GOval life2;
	private GOval life3;
	private GStar star;
	private GLabel score;
	private boolean gameLost;
	private boolean gameWon;
}
