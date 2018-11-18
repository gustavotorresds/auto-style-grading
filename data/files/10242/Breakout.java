/*
 * File: Breakout.java
 * -------------------
 * Name: Sophie Decoppet
 * Section Leader: Nidhi Manoj
 * 
 * This file will eventually implement the game of Breakout.
 * 
 * I used the given vy velocity and the given delay time. These can easily be changed by changing them in the variables
 * at the top of the program.
 */
import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels. These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight() rather than these constants for accurate 
	// size information.
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
	public static double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Instance variables for the program
	private GRect paddle;
	private double MouseX;
	private GOval ball=new GOval (2*BALL_RADIUS, 2*BALL_RADIUS);
	private double vx;
	private double vy=VELOCITY_Y;
	private GRect brick;
	private RandomGenerator rgen = new RandomGenerator();
	private int lives=NTURNS;
	private GLabel livesCount;
	private int nbricks=NBRICK_COLUMNS*NBRICK_ROWS;
	private int score = 0;
	private GLabel scoreCount;
	private GObject collider;
	private int npaddle;

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUp();
		addMouseListeners();
		playGame();
	}

	private void endOfGame() {
		removeAll();
		makeLabel("End of game! You lost :(");
	}

	//This is a general method for creating a label. This method will be used to create the End of Game message
	//and the Win messsage.
	private GLabel makeLabel(String string) {
		GLabel message = new GLabel(string);
		message.setFont("Arial-bold-28");
		add(message, (getWidth()-message.getWidth())/2, (getHeight()-message.getAscent())/2);
		return null;
	}

	//This sets up the bricks, the paddle, the ball, the score count, and the lives count.
	private void setUp() {
		setUpBricks();
		setUpPaddle();
		setUpBall();
		setUpScoreCount();
		addLivesCount();
	}

	//This creates the label that indicates your score. The location of the score label doesn't 
	//change yet depending on the location of your mouse.
	private void setUpScoreCount() {
		scoreCount = new GLabel ("Score: " + score);
		scoreCount.setFont("Calibri-12");
		add(scoreCount, 0, getHeight()-PADDLE_Y_OFFSET+PADDLE_HEIGHT+scoreCount.getAscent());
	}

	//This sets up all your bricks. It is general enough that it will repeat the rainbow depending on the number 
	//of rows of bricks.
	private void setUpBricks() {
		for(int i=0; i<NBRICK_ROWS; i++) {
			for(int j=0; j<NBRICK_COLUMNS; j++) {
				double xinitial=(getWidth()-(NBRICK_COLUMNS*BRICK_WIDTH+(NBRICK_COLUMNS-1)*BRICK_SEP))/2;
				brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(i%10==0 || i%10==1) {
					brick.setColor(Color.RED);
				}
				if(i%10==2 || i%10==3){
					brick.setColor(Color.ORANGE);
				} 
				if(i%10==4 || i%10==5) {
					brick.setColor(Color.YELLOW);
				} 
				if(i%10==6 || i%10==7) {
					brick.setColor(Color.GREEN);
				} 
				if(i%10==8 || i%10==9) {
					brick.setColor(Color.CYAN);
				}
				add(brick, xinitial+j*(BRICK_WIDTH+BRICK_SEP), BRICK_Y_OFFSET+i*(BRICK_HEIGHT+BRICK_SEP));
			}
		}
	}

	//This sets up the paddle for the first time. The location doesn't yet change depending on your mouse.
	private void setUpPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, 0, getHeight()-PADDLE_Y_OFFSET);
	}

	//This changes the location of your paddle and your score count depending on the location of your mouse.
	public void mouseMoved(MouseEvent e) {
		MouseX=e.getX();
		if(MouseX<getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(MouseX, getHeight()-PADDLE_Y_OFFSET);
			scoreCount.setLocation(MouseX, getHeight()-PADDLE_Y_OFFSET+PADDLE_HEIGHT+scoreCount.getAscent());
		}
	}

	//This sets up the ball.
	private void setUpBall() {
		ball.setFilled(true);
		add(ball,getWidth()/2, getHeight()/2);
	}

	//This is the most essential method of the program. It allows you to play the game. It starts out by 
	//waiting for your click, then it generates a random vx. 
	//We then introduce an animation loop which allows the game to go on only for as long as you have lives
	//and there are still bricks.
	private void playGame() {
		waitForClick();
		generateVx();
		while(lives>0 && nbricks>0) {
			makeBallMove();
			makeBallBounceWalls();
			removeCollisionsBlocks();
			bottomWallCollisionCase();
			winCase();
		}
	}

	//This updates the lives count on the top left corner of the screen.
	private void updateLivesCount() {
		livesCount.setLabel("Lives left: " + lives);
	}

	//This adds a lives count label at the top left corner of the screen.
	private void addLivesCount() {
		livesCount = new GLabel ("Lives left: " + lives);
		livesCount.setFont("Calibri-12");
		add(livesCount, 0, livesCount.getAscent());
	}

	//This takes care of the case where you win. It displays a You Win! message.
	private void winCase() {
		if(nbricks==0) {
			removeAll();
			makeLabel("You Win! Yayyy!! :)");
		}
	}

	//This method takes care of the case where the ball hits the bottom wall. If there are still lives left
	//it will set up the world again (but does not replenish the bricks). If the there are no more lives, it 
	//will display an end of game message. It also makes the speed of the ball go back to its initial speed,
	//and updates the counter of how many times you hit the paddle.
	private void bottomWallCollisionCase() {
		if(ball.getY()>(getHeight()-2*BALL_RADIUS)){
			lives--;
			generateVx();
			updateLivesCount();
			setUpBall();
			updateDelay();
			if(lives !=0) {
				waitForClick();
			} else {
				endOfGame();
			}
		}
	}

	//This method makes the updates the speed of the ball after you collided with the bottom wall. It makes it 
	//go back to the original speed. We use an integer instead of a double because it will tell us the number 
	//of times the speed was effectively doubled.
	private void updateDelay() {
		int doubleSpeed=npaddle/10;
		DELAY=DELAY*(2^doubleSpeed);
	}

	//generates a random vx velocity
	private void generateVx() {
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx=-vx;
		}
	}

	//this is the basic code for making your ball move. It actually moves because this method
	//is placed within the animation loop (see comments about playGame method)
	private void makeBallMove() {
		ball.move(vx, vy);
		pause(DELAY);
	}

	//This makes the ball bounce off the 2 side walls and the top wall.
	private void makeBallBounceWalls() {
		if(ball.getY()<0) {
			vy=-vy;
		}
		if(ball.getX()>(getWidth()-2*BALL_RADIUS) || ball.getX()<0) {
			vx=-vx;
		}
	}

	//This method both removes bricks once the ball has collided with them and allows the ball 
	//to bounce of the paddle.
	private void removeCollisionsBlocks() {
		collider = getColliderObject();
		//This makes the paddle bounce off the paddle.
		if(collider == paddle) {
			//This vy>0 condition assures that the ball won't get stuck on the paddle if you 
			//collide with the ball sideways (one of the concerns on the handout).
			//This part of the method also counts how many times you hit the paddle, and doubles
			//the speed of the ball once you have hit the paddle 10 times.
			npaddle++;
			if(vy>0) {
				bounceClip.play();
				vy=-vy;
			}		
			if(npaddle%10==0) {
				DELAY=DELAY/2;
			}
		} else if(collider != null && collider !=scoreCount) {
			//This makes the bricks disappear (and makes sure that it won't make the Score Count Label disappear).
			//It also makes the ball bounce when it hits a brick, updates the score every time you hit a brick,
			//and keeps track of the number of bricks.
			remove(collider);
			bounceClip.play();
			updateScore();
			if(vy<0){
				vy=-vy;
			}
			nbricks--;
		}
	}

	//This assigns a score to each brick you hit. The red bricks count most. They are worth 5 points. The orange are
	//worth 4, the yellow 3 and so on.
	private void updateScore() {
		if(collider.getColor() == Color.RED) {
			score=score+5;
		}
		if(collider.getColor() == Color.ORANGE) {
			score=score+4;
		}
		if(collider.getColor() == Color.YELLOW) {
			score=score+3;
		}
		if(collider.getColor() == Color.GREEN) {
			score=score+2;
		}
		if(collider.getColor() == Color.CYAN) {
			score=score+1;
		}
		scoreCount.setLabel("Score: " + score);
	}

	//This method allows us to see if the ball collides with anything and returns whatever obejct it collides with.
	//If it doesn't collide with anything, it will return null.
	private GObject getColliderObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if(collider == null) {
			collider = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
			if(collider == null) {
				collider = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
				if(collider == null) {
					collider = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
				}
			}
		}
		return(collider);
	}
}
