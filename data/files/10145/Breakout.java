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
import java.net.URL;
import java.util.Set;

public class Breakout extends GraphicsProgram {

	//fix getting glued to paddle
	//find out how to do absolute value 

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
	//Number of colors 
	public static final int NCOLORS=5;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GRect paddle;
	private double vx, vy;


	private GOval ball; 
	private double xballlocation;
	private double yballlocation; 
	private int lives= NTURNS;
	private int BRICK_COUNT = 100;
	private GLabel score;




	public void run() {

		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setupScreen();
		addMouseListeners();	
		gameTime();
	}

	//This method establishes a score keeper, and allows the game to run, and detects a situation that indicates winning and losing. 
	private void gameTime() {
		score = new GLabel("SCORE KEEPER: Lives Left:"+lives+"  "+ "Bricks Left:" + BRICK_COUNT, 60,60);
		add(score); 
		for (int turns=0; turns<NTURNS; turns++) {
			if (bricksLeft(BRICK_COUNT) && livesLeft(lives)) {
				playGame();
			}	
		/*	if (lives == 0 && BRICK_COUNT > 0) {
				loser(); 
			}
			else if (lives > 0 && BRICK_COUNT == 0) {
				winner();
			}*/
		}
	}

	//This method tells the user they lost and displays a gif. 
	/*private void loser() {
		add(new GLabel("YOU LOSE!",180,300));
		pause(2000);
		add (new GImage ("tenor 2.gif")); 
	}
	*/

	//This method tells the user they won and displays a gif. 
	/*private void winner() {
		add(new GLabel("YOU WIN",180,300));
		pause(2000);
		add (new GImage ("win.gif")); 
	}
	*/


	//This boolean returns true is there are lives left. 
	private boolean livesLeft(int lives) {
		if (lives > 0) return true;
		else return false;
	}

	//This boolean returns true is there are bricks left 
	private boolean bricksLeft(int BRICK_COUNT) {
		if (BRICK_COUNT > 0) return true;
		else return false;
	}
	//This method makes the ball and allows the user to play the game with it. 
	private void playGame() {
		makeball();
		playball();
	}

	//This method contains the animation loop, allowing the game to be played. 
	private void playball() {
		vy=VELOCITY_Y;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		waitForClick();
		while(bricksLeft(BRICK_COUNT)) {
			if (stayinbounds()==false) {
				break;
			}
			checkSurroundings();
			ball.move(vx, vy);
			pause(DELAY);
		}

	}
	//This boolean will return true, unless the ball "hits" the lower bound-- indicating the player has lost a life.
	//The "Lives left" count will be updated accordingly. 
	//This also ensures the ball bounces off the walls.
	private boolean stayinbounds() {
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
			//AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			//bounceClip.play();
		}
		if(hitTopWall(ball)) {
			vy = -vy;
			//AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			//bounceClip.play();
		}
		if (hitBottomWall(ball)) {
			remove(ball);
			lives--;
			//score.setLabel("SCORE KEEPER: Lives Left:"+lives+"  "+ "Bricks Left:" + BRICK_COUNT);
			return false;
		}
		return true;

	}

	//This method allows the ball to bounce off the paddle when it hits it, and breaks the bricks when it hits them. 
	//The BRICK_COUNT will be updated accordingly. 
	//This method also makes sure the score keeper is "protected"
	private void checkSurroundings() {
		GObject collider = getCollidingObject();
		if (collider!= null) {
			if (collider == paddle) {
				vy = - (Math.abs(vy));
				//This ensures there is no "sticky" paddle. 
			//	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				//bounceClip.play();
			} else if (collider != score) {
				breakBrick(collider);
				//BRICK_COUNT--;
				//score.setLabel("SCORE KEEPER: Lives Left:"+lives+"  "+ "Bricks Left:" + BRICK_COUNT);
			}
		}
	}

	//This method checks around the ball to see if it collided with an object, that being the paddle or a brick. 
	private GObject getCollidingObject() {
		GObject collider = null;
		if (checkupperLeft()!= null) {
			collider=checkupperLeft();

		}
		if (checkupperRight()!= null) {
			collider= checkupperRight();

		}
		if (checklowerRight()!=null) {
			collider=checklowerRight();	

		}
		if (checklowerLeft()!= null) {
			collider=checklowerLeft();
		}
		return collider;
	}
	//This method checks the upper left of the ball for a collision with an object. 
	private GObject checkupperLeft() {
		GObject collider= getElementAt(ball.getX(), ball.getY());
		if (collider!=null) {
			return collider;
		} else {
			return null;
		}
	}
	//This method checks the upper right of the ball for a collision with an object. 
	private GObject checkupperRight() {
		GObject collider= getElementAt(ball.getX()+(2*BALL_RADIUS), ball.getY());
		if (collider!=null) {
			return collider;
		} 
		return null;
	}

	//This method checks the lower left of the ball for a collision with an object. 
	private GObject checklowerLeft() {
		GObject collider= getElementAt(ball.getX(), ball.getY()+(2*BALL_RADIUS));
		if (collider!=null) {
			return collider;
		}
		return null;
	}

	//This method checks lower right of the ball for a collision with an object. 
	private GObject checklowerRight() {
		GObject collider= getElementAt(ball.getX()+(2*BALL_RADIUS), ball.getY()+(2*BALL_RADIUS));
		if (collider!=null) {
			return collider;
		}
		return null;
	}

	//This method breaks the object- once it is confirmed that is it a brick. 
	//This method also updates the BRICK_COUNT accordingly. 
	private void breakBrick(GObject collider) {
		if (collider!=null) {
			remove(collider);
			//BRICK_COUNT--;
			vy = -vy;
		}
	}


	//This boolean establishes what the bottom wall is- and will return true if the ball hits it. 
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight()-PADDLE_Y_OFFSET;
	}

	//This boolean establishes what the top wall is- and will return true if the ball hits it. 
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= BALL_RADIUS;
	}
	//This boolean establishes what the right wall is- and will return true if the ball hits it. 
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - BALL_RADIUS*2;
	}
	//This boolean establishes what the left wall is- and will return true if the ball hits it. 
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	//This method sets up the world of the game. 
	private void setupScreen() {
		makeBricks();
		makepaddle();
	}

	//This method makes the colored bricks, and adds them to the screen. 
	private void makeBricks() {
		for (int j=1; j<=NBRICK_ROWS; j++) {
			if (j<=NBRICK_ROWS/NCOLORS) {
				buildrow(j,Color.RED);
			}else if (j<=(2*(NBRICK_ROWS/NCOLORS))) {
				buildrow(j,Color.ORANGE);
			} else if (j<=(3*(NBRICK_ROWS/NCOLORS))) {
				buildrow(j,Color.YELLOW);
			} else if (j<=(4*(NBRICK_ROWS/NCOLORS))) {
				buildrow(j,Color.GREEN);
			} else if (j<=(5*(NBRICK_ROWS/NCOLORS))){
				buildrow(j,Color.CYAN);
			}
		}
	}




	//This method builds a single row. 
	private void buildrow(int count,Color color) {	
		double y= BRICK_Y_OFFSET+BRICK_HEIGHT + (count*(BRICK_HEIGHT+BRICK_SEP));
		double x= ((getWidth()/2-((BRICK_WIDTH+BRICK_SEP) *NBRICK_COLUMNS/2))+BRICK_SEP/2);	
		for (int i=0; i<NBRICK_COLUMNS; i++) {
			GRect brick = new GRect(x+i*(BRICK_WIDTH+BRICK_SEP),y,BRICK_WIDTH,BRICK_HEIGHT); 
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
		}
	}

	//This method builds the paddle. 
	private void makepaddle() {	
		double paddleX=((getWidth()/2)-(PADDLE_WIDTH/2));
		double paddleY=(getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		paddle=new GRect(paddleX,paddleY,PADDLE_WIDTH,PADDLE_HEIGHT);
		add(paddle);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
	}

	//This method allows the user to control the paddle placement through a mouse event. 
	public void mouseMoved(MouseEvent e) {
		double mouseXlocation = e.getX();
		double centerPaddleXlocation= (paddle.getX()+(PADDLE_WIDTH/2));
		if (movePaddle(mouseXlocation,centerPaddleXlocation)) {
			paddle.setLocation(mouseXlocation-PADDLE_WIDTH/2,paddle.getY());
		}

	}	
	//This method ensures that the paddle cannot move off the screen. 
	private boolean movePaddle (double mouseXlocation,double centerPaddleXlocation) {
		return (mouseXlocation < (getWidth()-PADDLE_WIDTH/2))&&(mouseXlocation>PADDLE_WIDTH/2);
	}

	//This method constructs the ball and centers it in the screen 
	private GOval makeball() {
		xballlocation= getWidth()/2 -BALL_RADIUS;
		yballlocation=getHeight()/2- BALL_RADIUS;
		ball= new GOval (xballlocation, yballlocation, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK); 
		add(ball);
		return(ball);
	}
}





