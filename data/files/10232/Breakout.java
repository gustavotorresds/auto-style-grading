/*
 * File: Breakout.java
 * -------------------
 * Name: Caroline Dunn
 * Section Leader: Peter Maldanado 
 * 
 * This program implements the game of Breakout. First it reads out instructions
 * on a pink background (just for aesthetic affect). Then it allows the viewer
 * to choose a level that varies the speed of the ball by a multiplier.
 * Then the program creates the row of colored bricks (which work even if you vary
 * the number of bricks ), the moving paddle that tracks the mouse, and the bouncing
 * ball. It erases bricks if the ball collides with them and tracks them so it can let you
 * know if you win by erasing all of them. It also tracks the number of turns so
 * it can end if you use up your number of turns. At the end, it will either tell 
 * you that you won or that you lost. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.jmx.snmp.SnmpString;

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

	//brick
	GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);

	//paddle
	GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	//velocity of ball
	double vy=VELOCITY_Y;

	// makes the ball
	GOval ball = new GOval(BALL_RADIUS, BALL_RADIUS);

	//makes x coordinate of ball
	double ballx;

	//makes y coordinate of ball
	double bally;

	//number of bricks hit
	double numHitBrick=0;

	//number of turns
	double turns = NTURNS;

	//offset in for the rectangles for the levels
	public static final double OFFSET_X = 40;

	//spacing between rectangles for the levels
	public static final double SPACING_X = 15;

	//width of rectangle for levels
	public static final double LEVEL_WIDTH = (CANVAS_WIDTH - 2*OFFSET_X - 2*SPACING_X)/3;

	//slow factor for levels
	double slowFactor;

	//the levels as instance variables so they can be added and clicked on
	GRect level1=null;
	GLabel label1=null;
	GRect level2=null;
	GLabel label2=null;
	GRect level3=null;
	GLabel label3=null;
	GRect pinkBackground=null;

	//height of rectangle for levels
	public static final double LEVEL_HEIGHT = 60;
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		pinkBackground = makeRectangle(Color.PINK);
		add(pinkBackground);
		instructions();
		chooseLevel();
		addMouseListeners();
		while (label3!=null) {
			pause(DELAY);
		} 
		remove(pinkBackground);
		setUp();
		paddle=createPaddle();
		addPaddleToGame();
		playGame();
	}


	//allows viewer to play the Game
	private void playGame() {
		//counts the number of turns	
		for (turns=NTURNS; turns>0; turns--) {
			oneTurn();

			//it's really if turns = 0 but the variable is structured so it's one greater than in actuality
			//because it's in the for loop; tells you that you've used up all your turns 
			if (turns==1&&numHitBrick!=NBRICK_ROWS*NBRICK_COLUMNS+1) {
				GLabel label = youLose();
				add(label);
				remove(ball);
				remove(paddle);
			}
			if (numHitBrick==NBRICK_ROWS*NBRICK_COLUMNS+1) {
				break;
			}
		}
		if (numHitBrick==NBRICK_ROWS*NBRICK_COLUMNS+1) {
			flashColors();
			GLabel youWin = new GLabel("Congrats! You win!");
			youWin.setLocation(getWidth()/2-youWin.getWidth(), getHeight()/2-youWin.getHeight()/2);
			youWin.setFont("Comic Sans-25");
			add(youWin);
			remove(ball);
			remove(paddle);
		}
	}

	//one Round
	private void oneTurn() {
		makeBall();
		bouncingBall(slowFactor);
		resetGame();
	}

	//add instance variable ball to screen, and makes it move and bounce off the walls until it 
	//hits the bottom or all the bricks are gone
	private void bouncingBall(double slow) {
		double ballx=getWidth()/2 - BALL_RADIUS;
		double bally=getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT-BALL_RADIUS;
		ball.setFilled(true);
		ball.setLocation(ballx,bally);
		add(ball);

		//animates ball
		double vx = randomVelocityX(ball);

		while (!hitBottomWall(ball, vy) && numHitBrick!=NBRICK_ROWS*NBRICK_COLUMNS+1) {
			//loop of continued motion until all bricks removed or hits bottom 
			ball.pause (DELAY);
			ball.move(slow*vx, vy);

			//makes ball bounce off walls
			if (hitLeftWall(ball, vx) || hitRightWall(ball, vx)) {
				vx=-vx;
			}
			if (hitTopWall(ball, vy)) {
				vy=-vy;
			}
			//checks for collisions & breaks loop if bricks reach 0
			GObject collider = getCollidingObject(ball);
			if (collider==paddle) {
				//this takes care of the sticky paddle situation...it lets the ball
				//bounce up half of the radius if it comes in contact with it
				//so it doesn't keep sticking to the paddle
				//because it changes direction every time it touches it
				ball.setLocation(ball.getX(), ball.getY()-0.5*BALL_RADIUS);
				vy=-vy;
			}

			//when the loop runs, it adds another "brick hit" to the numHitBrick
			//constant whenever the ball hits the bottom wall, because it ends & restarts
			//the loop. So this fixes this problem so it doesn't mis-count the bricks
			//and claim the game is won before it's actually won!
			if (hitBottomWall(ball,vy)) {
				numHitBrick=numHitBrick-1;
			}
			if (collider!=paddle&&collider!=null) {
				vy=-vy;
				remove(collider);
				numHitBrick=numHitBrick+1;
			}
		}
		if (hitBottomWall(ball, vy)&&turns!=1) {
			remove (ball);
			GLabel label = addTryAgain();
			flashColors();
			add (label);
			pause(50*DELAY);
			remove(label);
			add(ball);
		} 
	}


	//resetting game after a turn
	private void resetGame() {
		//pause ball until paddle returns to starting place
		ball.pause(DELAY);
		paddle.setLocation(getWidth()/2-paddle.getWidth()/2, getHeight() - PADDLE_Y_OFFSET);
	}


	//print "Try Again"
	private GLabel addTryAgain() {
		GLabel tryAgain = new GLabel ("Try Again! You have "+(turns-1)+" turns left.");
		tryAgain.setLocation(getWidth()/2-tryAgain.getWidth()/2,getHeight()/2-tryAgain.getHeight()/2);
		tryAgain.setFont("Comic Sans-14");
		return(tryAgain);
	}

	//print "You lose :("
	private GLabel youLose() {
		GLabel youLose = new GLabel ("No more turns left. You lose :(");
		youLose.setLocation(getWidth()/2-youLose.getWidth()/2,getHeight()/2-youLose.getHeight()/2);
		youLose.setFont("Comic Sans-14");
		return(youLose);
	}

	//Making the ball bounce off the paddle & bricks at all four corners of the ball
	private GObject getCollidingObject(GObject obj) {
		obj = getElementAt(ball.getX(), ball.getY());
		if (obj!=null) {
			return obj;
		}
		obj = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		if (obj!=null) {
			return obj;
		}
		obj = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		if (obj!=null) {
			return obj;
		} 
		obj = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		if (obj!=null) {
			return obj;
		}
		return null;
	}

	//condition for hitting the left wall, so ball reverses direction
	private boolean hitLeftWall(GOval ball, double vx) {
		if (vx>0) return false;
		return ball.getX()<0;
	}

	//condition for hitting the right wall, so ball reverses direction
	private boolean hitRightWall(GOval ball, double vx) {
		if (vx<0) return false;
		return ball.getX()>=getWidth()-2*BALL_RADIUS;
	}

	//condition for hitting the top wall, so ball reverses direction
	private boolean hitTopWall(GOval ball, double vy) {
		if (vy>0) return false;
		return ball.getY()<=0;
	}

	//condition for hitting the bottom wall so ball reverses direction
	private boolean hitBottomWall(GOval ball, double vy) {
		if (vy<0) return false;
		return ball.getY() > getHeight()-2*BALL_RADIUS;
	}

	//MAKING THE BALL
	//randomizes ball's horizontal velocity
	private double randomVelocityX(GOval ball) {
		RandomGenerator rgen = RandomGenerator.getInstance();
		double vx = rgen.nextDouble(1.0,3.0);
		if (rgen.nextBoolean(0.5)) vx= -vx;
		return vx;
	}

	//makes the ball
	private GOval makeBall() {
		GOval ball = new GOval (BALL_RADIUS, BALL_RADIUS);
		return ball;
	}

	//MAKES THE PADDLE
	//allows the mouse to move the paddle
	public void mouseMoved (MouseEvent e) {
		double paddlex=e.getX()-PADDLE_WIDTH/2;
		double paddley=getHeight() - PADDLE_Y_OFFSET;
		paddle.setLocation(paddlex,paddley);
	}

	//adds the instance variable paddle to the screen 
	private void addPaddleToGame() {
		double paddlex=getWidth()/2-paddle.getWidth()/2;
		double paddley=getHeight() - PADDLE_Y_OFFSET;
		add(paddle,paddlex,paddley);
	}

	//makes the paddle, a black filled rectangle 
	private GRect createPaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	//sets up the rows of bricks 
	private void setUp() {
		for (int rows=0; rows<NBRICK_ROWS; rows++) {
			for (int columns=0; columns<NBRICK_COLUMNS;columns++) {
				brick = makeBrick();
				//For the x, it starts at the midpoint and shifts over half of a brick separation and five bricks so
				//that when it starts adding bricks from left to right they're centered in the box
				double brickx= getWidth()/2 + 0.5*BRICK_SEP- 0.5*NBRICK_ROWS*(BRICK_WIDTH +BRICK_SEP)+(BRICK_WIDTH+BRICK_SEP)*rows;
				double bricky=BRICK_Y_OFFSET+columns*(BRICK_HEIGHT+BRICK_SEP);
				brick.setLocation(brickx,bricky);
				brick.setFilled(true);

				//sets up color scheme
				if(columns<0.2*NBRICK_COLUMNS) {
					brick.setColor(Color.RED);
				}
				if (columns>=0.2*NBRICK_COLUMNS && columns<0.4*NBRICK_COLUMNS) {
					brick.setColor(Color.ORANGE);
				}
				if (columns>=0.4*NBRICK_COLUMNS && columns<0.6*NBRICK_COLUMNS) {
					brick.setColor(Color.YELLOW);
				}
				if (columns>=0.6*NBRICK_COLUMNS && columns<0.8*NBRICK_COLUMNS) {
					brick.setColor(Color.GREEN);
				}
				if (columns>=0.8*NBRICK_COLUMNS && columns<NBRICK_COLUMNS) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}

	//makes brick 
	private GRect makeBrick() {
		GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		return brick;
	}

	//FLASHING SCREEN
	//flashes colors on the screen for dramatic effect after losing a turn or winning
	private void flashColors() {
		GRect blue = makeRectangle(Color.CYAN);
		add(blue);
		pause(5*DELAY);
		remove(blue);
		GRect orange = makeRectangle(Color.ORANGE);
		add(orange);
		pause(5*DELAY);
		remove(orange);
		GRect pink = makeRectangle(Color.PINK);
		add(pink);
		pause(5*DELAY);
		remove(orange);
	}
	//makes a rectangle of variable color for the flashing screen
	private GRect makeRectangle (Color color) {
		GRect rect = new GRect (getWidth(), getHeight());
		rect.setColor(color);
		rect.setLocation(0,0);
		rect.setFilled(true);
		return(rect);
	}

	///CHOOSING THE LEVEL - this just makes the different rectangle graphics that the viewer clicks to select the level 
	private void chooseLevel() {
		pinkBackground = makeRectangle(Color.PINK);
		add(pinkBackground);
		//level 1
		level1 = addLevel(OFFSET_X);
		add(level1);
		label1 = addLabel("Level 1");
		label1.setLocation(OFFSET_X+level1.getWidth()/2-label1.getWidth()/2, getHeight()/2+label1.getHeight()/2);
		add(label1);
		//level 2
		level2 = addLevel(OFFSET_X+LEVEL_WIDTH+SPACING_X);
		add(level2);
		label2 = addLabel("Level 2");
		label2.setLocation(OFFSET_X+LEVEL_WIDTH+SPACING_X+level2.getWidth()/2-label2.getWidth()/2, getHeight()/2+label2.getHeight()/2);
		add(label2);
		//level 3
		level3 = addLevel(OFFSET_X+2*LEVEL_WIDTH+2*SPACING_X);
		add(level3);
		label3 = addLabel("Level 3");
		label3.setLocation(OFFSET_X+2*LEVEL_WIDTH+2*SPACING_X+level3.getWidth()/2-label3.getWidth()/2, getHeight()/2+label3.getHeight()/2);
		add(label3);
	}
	//level rectangle –> carries the x-value as a parameter so it can vary for organizational purposes of setting up the three rectangles
	private GRect addLevel(double levelX) {
		GRect levelRect = new GRect (LEVEL_WIDTH, LEVEL_HEIGHT);
		levelRect.setColor(Color.YELLOW);
		levelRect.setFilled(true);
		levelRect.setLocation(levelX, getHeight()/2-levelRect.getHeight()/2);
		return(levelRect);
	}

	//level label –> carries the string as the label because needs to vary so each icon can read Level 1, 2, or 3
	private GLabel addLabel(String label) {
		GLabel labelRect = new GLabel (label);
		labelRect.setFont("Comic Sans-18");
		return(labelRect);
	}

	//picks proper level based on what one is clicked
	public void mouseClicked (MouseEvent e1) {
		int x = e1.getX();
		int y = e1.getY();

		GObject obj = getElementAt(x,y);
		//the number either multiplies the speed by a fraction, doesn't change it, or doubles it,
		//so the randomly chosen speed is either increased or decreased based on the level chosen.
		if (obj==level1 || obj==label1) {
			removeSetUp(label1,label2,label3,level1,level2,level3);
			slowFactor=1;
			label3=null;
		}
		if (obj==level2 || obj==label2) {
			removeSetUp(label3,label1,label2,level1,level2,level3);
			slowFactor=2;
			label3=null;

		}
		if (obj==level3 || obj==label3) {
			removeSetUp(label3,label1,label2,level1,level2,level3);
			slowFactor=3;
			label3=null;
		}
	}

	//removes the level set up
	private void removeSetUp(GLabel labelOne, GLabel labelTwo, GLabel labelThree, GRect rect1, GRect rect2, GRect rect3) {
		remove(labelOne);
		remove(labelTwo);
		remove(labelThree);
		remove(rect1);
		remove(rect2);
		remove(rect3);	
	}
	//instructions so the viewer knows what's happening; creates a label for the title and then the instructions, which are broken
	//into pieces so they all can fit on the page
	private void instructions() {
		addTitle();
		GLabel line1 = addInstructions("The goal of this game is to hit all the bricks.");
		GLabel line2 = addInstructions ("You can pick three different levels");
		GLabel line3= addInstructions ("where 1 is the easiest. Good luck!");
	}
	
	private void addTitle() {
		GLabel title = new GLabel ("BREAKOUT");
		title.setFont("Comic Sans-48");
		title.setLocation(getWidth()/2-title.getWidth()/2,getHeight()/2+title.getHeight()/2);
		title.setColor(Color.CYAN);
		add(title);
		pause (100*DELAY);
		remove(title);
	}

	private GLabel addInstructions(String words) {
		GLabel instructions = new GLabel(words);
		instructions.setFont("Comic Sans-18");
		instructions.setLocation(getWidth()/2 - instructions.getWidth()/2,getHeight()/2-instructions.getHeight()/2);
		instructions.setColor(Color.DARK_GRAY);
		add(instructions);
		pause (100*DELAY);
		remove(instructions);
		return instructions;
	}
}
