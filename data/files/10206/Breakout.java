/*
 * File: Breakout.java
 * -------------------
 * Name: Abby McShane
 * Section Leader: Nidhi
 * 
 *This encodes for the game of breakout.
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
	public static final int NBRICK_COLUMNS = 12;

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
	public static final double DELAY = 1000 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Initialize Paddle
	private double paddleY;
	private GRect paddle=new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	//Initialize Ball
	private GOval ball=new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
	private double ballY;
	private double ballX;
	private double vx;
	private double vy;

	//Variables important for knowing when game is over
	private double bricksLeft=NBRICK_COLUMNS*NBRICK_ROWS;
	private int TURNS_LEFT=NTURNS;
	private GLabel livesLeft=new GLabel("You have " + NTURNS + " live(s) left.");

	//Random Generator
	private RandomGenerator rgen=RandomGenerator.getInstance();

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		makeAPaddle();
		add(paddle);
		addMouseListeners();
		makeBall();
		playGame();
	}

	//This makes the ball
	private void makeBall() {
		ballX=getWidth()/2-2*BALL_RADIUS;
		ballY=getHeight()/2-2*BALL_RADIUS;
		ball.setLocation(ballX, ballY);
		ball.setFilled(true);
		add(ball);
	}

	//This encodes for the game aspect.
	private void playGame() {
		while (TURNS_LEFT != 0) {
			vy=VELOCITY_Y;
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx=-vx;
			//This is the animation loop. The ball starts moving downward at a specified y velocity and a random x velocity.
			//The loop continues until the ball hits the ground.
			while (ballY <= getHeight()-2*BALL_RADIUS) {
				if (bricksLeft!=0) {
					ball.move(vx, vy);
					//retrieves ball's x and y coordinates
					ballX=ball.getX();
					ballY=ball.getY();
					//reverses x velocity if the ball hits a side wall
					if (hitXWall(ballX)) {
						vx=-vx;
					} 
					//reverses y velocity if the ball hits the top wall
					if (hitTopWall(ballY)) {
						vy=-vy;
					} 
					pause(DELAY);
					//Checks for collisions. If the colliding object is not the paddle, it removes the colliding object and
					//reverse y direction. If it is the paddle, it makes the velocity go upward (always) and does not remove the paddle.
					//This loop is set up to make sure the ball does not remove the banner that says how many lives are left.
					GObject obj=getCollidingObject(ballX,ballY);
					if (obj != null && obj != livesLeft) {
						if (obj != paddle) {
							vy=-vy;
							remove(obj);
							bricksLeft=bricksLeft-1;
						} else {
							//this addresses sticky paddle
							if (vy>0) {
								vy=-vy;
							}
						}
					}
				} else {
					youWin();
					//ballY must become a value that breaks the animation loop, so the "you win" banner only gets printed once.
					ballY=getHeight()+2*BALL_RADIUS;
				}
			}
			//This aspect lets the user know how may lives they have left after they lose a life.
			//If they've used up all of their lives, they lose and the game stops. This only happens 
			//if the player hasn't yet won the game.
			if (bricksLeft !=0) {
				TURNS_LEFT=TURNS_LEFT-1;
				if (TURNS_LEFT==0) {
					livesLeft.setLabel("You have " + TURNS_LEFT + " live(s) left.");
					youLose();
				} else {
					livesLeft.setLabel("You have " + TURNS_LEFT + " live(s) left.");
					resetBall();
					//this gives the user a bit of time to get ready for new turn.
					pause(DELAY*15);
				}
			}
		}
	}

	//This brings the ball back to the start position at the start of a new turn.
	private void resetBall() {
		ballX=getWidth()/2-2*BALL_RADIUS;
		ballY=getHeight()/2-2*BALL_RADIUS;
		ball.setLocation(ballX, ballY);
	}

	//This displays a "you win" message in the center of the canvas.
	private void youWin() {
		GLabel youWin=new GLabel("You Win!");
		youWin.setLocation(getWidth()/2-youWin.getWidth()/2, getHeight()/2+youWin.getAscent()/2);
		add(youWin);	
	}

	//This displays a "you lose" message in the center of the canvas.
	private void youLose() {
		GLabel youLose=new GLabel("You Lose!");
		youLose.setLocation(getWidth()/2-youLose.getWidth()/2, getHeight()/2+youLose.getAscent()/2);
		add(youLose);
	}

	//This checks for a colliding object at each of the ball's four corners.
	private GObject getCollidingObject(double ballX, double ballY) {
		GObject maybeABrick = null;
		for (double x=ballX;  x<=ballX+2*BALL_RADIUS; x+=2*BALL_RADIUS) {
			for (double y=ballY; y<=ballY+2*BALL_RADIUS; y+=2*BALL_RADIUS) {
				if (maybeABrick==null) {
					maybeABrick = getElementAt(x,y);
				}
			}
		}
		return(maybeABrick);
	}

	//This returns whether or not the ball hit the top wall.
	private boolean hitTopWall(double ballY) {
		return(ballY <= 0);
	}

	//This returns whether or not the ball hit a side wall.
	private boolean hitXWall(double ballX) {
		return(ballX>=getWidth()-2*BALL_RADIUS || ballX<=0);
	}

	//This makes the paddle (does not add the paddle to the screen).
	private void makeAPaddle() {
		double paddleX=getWidth()/2-BRICK_WIDTH;
		paddleY=getHeight()-BRICK_HEIGHT-PADDLE_Y_OFFSET;
		paddle.setLocation(paddleX, paddleY);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
	}

	//If the mouse is moved within the canvas, this sets the paddle to the mouse's x location.
	//The paddle will not be able to move outside the canvas screen.
	public void mouseMoved(MouseEvent e) {
		double mouseX=e.getX()-paddle.getWidth()/2;
		if (mouseX >= 0 && mouseX<=getWidth()-paddle.getWidth()) {
			paddle.setLocation(mouseX, paddleY);	
		}
	}

	//This sets up the bricks and also the "lives left" label in the top right corner.
	private void setUpBricks() {
		livesLeft.setLocation(getWidth()-livesLeft.getWidth(), livesLeft.getAscent());
		add(livesLeft);
		Color COLOR=Color.RED;
		double brickyPos= BRICK_Y_OFFSET;
		for (int rowNo=1; rowNo<NBRICK_ROWS+1; rowNo++) {
			double brickxPos=getWidth()/2-((NBRICK_COLUMNS*BRICK_WIDTH+BRICK_SEP*(NBRICK_COLUMNS-1))/2);
			for (int noBricks=0; noBricks<NBRICK_COLUMNS; noBricks++) {
				add(makeABrick(brickxPos, brickyPos, COLOR));
				brickxPos=brickxPos+BRICK_SEP+BRICK_WIDTH;
			}
			if (rowNo % 2 == 0) {
				COLOR=changeColor(COLOR);
			}
			brickyPos=brickyPos+BRICK_SEP+BRICK_HEIGHT;
		}
	}

	//This makes a brick of a certain x position, y position, and color.
	private GRect makeABrick(double brickxPos,double brickyPos,Color COLOR) {
		GRect brick=new GRect(brickxPos, brickyPos, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(COLOR);
		return(brick);
	}

	//This changes the color depending on what the original color was.
	private Color changeColor(Color COLOR) {
		if (COLOR==Color.RED) {
			COLOR=Color.ORANGE;
		} else if (COLOR==Color.ORANGE) {
			COLOR=Color.YELLOW;
		} else if (COLOR==Color.YELLOW) {
			COLOR=Color.GREEN;
		} else if (COLOR==Color.GREEN) {
			COLOR=Color.CYAN;
		} else if (COLOR==Color.CYAN) {
			COLOR=Color.RED;
		}
		return COLOR;
	}
}