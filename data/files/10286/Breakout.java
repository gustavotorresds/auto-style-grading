/*
 * File: Breakout.java
 * -------------------
 * Name: Ryan Tran
 * Section Leader: Vineet Kosaraju 
 * 
 * This file will eventually implement the game of Breakout.
 * 
 * The following code will play the popular game of Breakout, in which there is a paddle at the bottom of the screen, a ball, and an array of blocks in the top of the screen. The game will be initiated by the click of a mouse and the paddle will be controlled by the movement of the mouse. The ultimate goal is to stop the ball from touching the bottom of the screen, while colliding the ball into the bricks in order to break them. Ultimately, the user will have 3 lives to try to break all of the blocks at the top of the screen. Ball can also bounce against the wall of the canvas on which the game is played. 
 * Precondition: Set up includes the array of bricks and the moving functional and controllable paddle.
 * Postcondition: A loss is when all lives of the players have been depleted by the ball touching the bottom of the screen x amount of times, without having broken all of the bricks. A win results when all bricks have been broken. NOTE: At end, during win screen, there is a special extension embedded.
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

	// Animation delay or pause time between ball moves (ms) (original: 1000.0 / 60.0)
	public static final double DELAY = 1000.0 / 70.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//Extension * Minimum and Maximum Radius of Random Circle Extension after game win*
	private static final double MIN_RADIUS=1;
	private static final double MAX_RADIUS=25;
	
	//Creates "instance" variable for paddle (so it can be used by multiple methods)
	private GRect paddle = null;
	
	// Declare instance variable rgen (random-number generator) - used by multiple methods & rgen is always defined as an instance variable.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Creates "instance" variable for ball so it can be used by multiple methods
	private GOval ball = null;
	
	//Declare vx and vy as instance variables
	private double vx, vy;
	
	//Declare score as an instance variable (used by multiple methods)
	private int score;
	
	//Declare number of turns left as instance variable (will be used later to find the number of turns left - initialized at NTURNS value) - used within multiple methods.
	private int numberTurnsLeft=NTURNS;
	
	//Instance variables for turnsLeftLabel & scoreLabel & win Label (used within multiple methods inc. extension)
	private GLabel turnsLeftLabel = null;
	private GLabel win = new GLabel ("");
	private GLabel scoreLabel = new GLabel ("");
	
	//Declares collider as an instance variable for use upon multiple methods
	private GObject collider = null;

	//Number of Bricks  (used within multiple methods - inc. extensions)
	private int numberOfBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	
	//Sound Extension 
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	//Start Message Extension - necessary b/c used within multiple methods
	GLabel startLabel = null;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		// Phases are split up into a setUpPhase (dealing with brick layout and paddle) for the code and a playPhase (dealing with collisions and ball movement and bounce)
		setUpPhase();
		playPhase();
	}

	/**
	 * setUpPhase sets up the layout of the game, including the on-screen labels, the brick layout, and the paddle.
	 * Precondition: none. (although from a coding standpoint, some of labels should be specifically made instance variables already)
	 * Postcondition: Bricks, labels (start, turnsLeft, and score), and paddle will all be displayed on the screen. Only moving object will be the paddle, which moves based on the position/movement of the mouse.
	 */
	private void setUpPhase() {
		addLabels();
		setUpBricks();
		setUpPaddle();
	}
	
	/**
	 * addLabels will add the three labels (startMessage, turnsLeftLabel, and scoreKeeper) on to the screen.
	 * Precondition: Labels should be defined as instance variables previously, and also have GLabel type methods associated with them that modify them (and will be called upon by this method).
	 * Postcondition: Labels are added to the screen according to instructions within the GLabel methods for each label.
	 */
	private void addLabels() {
		add(startMessage()); //Extension
		add(turnsLeftLabel());
		add(scoreKeeper());
	}
	
	/**
	 * startMessage will modify the previously defined startLabel GLabel in order so that it can be added to the screen in addLabels() function. These specifications include x and y coordinates, font, and text.
	 * Precondition: Label should be previously defined before as an instance variable.
	 * Postcondition: startLabel will be modified in order to have it easily called on as a method later on, and simply be added. The coordinates, font, and text for the label will be detailed within this method.
	 */
	// Extension (start message)
	private GLabel startMessage() {
		startLabel = new GLabel ("Start?");
		startLabel.setFont("TimesRoman-30");
		double startX = (getWidth()-startLabel.getWidth())/2;
		double startY = (getHeight()-startLabel.getAscent())/2;
		startLabel.setLocation(startX,startY);
		return startLabel;
	}
	
	/**
	 * Mouse public type method that removes startLabel when clicked so that game can begin (without start label's interference.)
	 * Precondition: startLabel was on screen when game is first opened.
	 * Postcondition: startLabel is removed after mouse is clicked, and game begins.
	 */
	// removes start message when mouse clicks
	public void mouseClicked(MouseEvent e) {
		remove(startLabel);
	}
	
	/**
	 * setUpBricks sets up the bricks layout and arrangement according to game instructions provided (displaced from the top by BRICK_Y_OFFSET vertically, and with the entire row centered horizontally. Each brick is also separated equally in horizontal and vertical directions.
	 * Precondition: No bricks will be on the screen. (NBRICK_ROWS, NBRICK_COLUMNS, BRICK_SEP, etc. should be defined before this method)
	 * Postcondition: Bricks will be added to the screen, centered in the screen horizontally and displaced downwards from the top by BRICK_Y_OFFSET vertically - with equal separation between each brick in the vertical and horizontal directions.
	 */
	private void setUpBricks() {
		for (int r=0; r<NBRICK_ROWS; r++) {
			for (int c=0; c<NBRICK_COLUMNS; c++) {
				double totalWidthRow = (NBRICK_COLUMNS*BRICK_WIDTH)+((NBRICK_COLUMNS-1)*BRICK_SEP);
				double brickX = (getWidth()-totalWidthRow)/2;
				double brickY = BRICK_Y_OFFSET;
				GRect brick = new GRect(brickX+(c*(BRICK_WIDTH+BRICK_SEP)),brickY+(r*(BRICK_HEIGHT+BRICK_SEP)),BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				setColorBricks(brick, r); //sets the color of the bricks based on its row number- Decomposed into another method
				add(brick);
			}
		}	
	}

	/**
	 * setColorBricks will set the color of bricks in each row in a rainbow-like alternating manner. Every two consecutive brick rows will have the same color, running the order RED, ORANGE, YELLOW, GREEN, CYAN (even as row number changes).
	 * Precondition: Brick will have been added to the screen and filled, but not yet colored.
	 * Postcondition: Brick will be colored now too depending on the row it is in, based on the order described above.
	 */
	private void setColorBricks(GRect brick, int r) {
		//Readjusts for rows greater than 10 for the if loop.
		while (r>9) {
			r=r-10;
		}
		if (r<2) {
			brick.setColor(Color.RED);
		} else if (r<4) {
			brick.setColor(Color.ORANGE);
		} else if (r<6) {
			brick.setColor(Color.YELLOW);
		} else if (r<8) {
			brick.setColor(Color.GREEN);
		} else {
			brick.setColor(Color.CYAN);
		}
	}
	
	/**
	 * setUpPaddle will make the paddle with the specified length and width (makePaddle) and also make the paddle move according to mouse movement (addMouseListeners).
	 * Precondition: No paddle will be on the screen, not yet added.
	 * Postcondition: A fully-functional paddle with the proper specifications will be on the screen, which will move controlled by mouse movement. (The specifications will be defined above in private static final type variables.)
	 */
	private void setUpPaddle() {
		makePaddle();
		addMouseListeners();
	}
	
	/**
	 * makePaddle will make the paddle based on the specifications given in the private static final type variables above. 
	 * Precondition: No paddle will be on the screen.
	 * Postcondition: A paddle will be centered on the screen in the x-direction and above the bottom wall by PADDLE_Y_OFFSET, but it will not yet be able to move. 
	 */
	private void makePaddle() {
		double paddleStartX = (getWidth()-PADDLE_WIDTH)/2;
		double paddleY = getHeight()-(PADDLE_Y_OFFSET+PADDLE_HEIGHT);
		paddle = new GRect (paddleStartX,paddleY,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/**
	 * mouseMoved will make paddle move (only in the x-direction because y direction will be fixed) based on the movement of the mouse, so that paddle can be controlled by the mouse.
	 * Precondition: Paddle must be made, and paddle is on screen, but not yet capable of moving. addMouseListeners must also be in setUpPaddle() method.
	 * Postcondition: Paddle will now be able to move (in the x-direction only) dependent on movement of the mouse. Mouse will be centered on the paddle. Paddle will also be limited, and will not be able to move off of the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		double paddleNewX = e.getX()-(paddle.getWidth()/2);
		if ((paddleNewX+paddle.getWidth())<=getWidth() && paddleNewX>=0) {
				paddle.setLocation(paddleNewX,paddle.getY());
		}
	}
	
	/**
	 * playPhase is the part after the setup phase, that will actually describe the mechanics that allow for the game to be played, the ball to move and collide.
	 * Precondition: Set-Up phase must be done (with functional paddle and the bricks set up and ready for play.)
	 * Postcondition: Game will be ready for play, and all mechanics will work, with balls bouncing off of the wall and objects, and colliding with the bricks, breaking them.
	 */
	private void playPhase() {
		ballDynamics();
	}
	
	/**
	 * ballDynamics will make the ball, and also establish its pattern of movement, allowing it to bounce off walls and objects, and collide with bricks and break them.
	 * Precondition: None. Ball will not yet have been made or described in terms of motion.
	 * Postcondition: Ball will be made, completely functional, and be ready for play, bouncing off of walls and objects, and colliding with bricks.
	 */
	private void ballDynamics() {
			makeBall();
			moveBall();
	}
	
	/**
	 * makeBall will make the ball according to the specifications given, as well as center the ball in the exact middle of the screen vertically and horizontally. 
	 * Precondition: Ball will not yet have been made.
	 * Postcondition: Ball of BALL_RADIUS given will now be made and added to the exact middle of the screen. Ball will also be filled.
	 */
	private void makeBall() {
		double ballX = getWidth()/2-BALL_RADIUS;
		double ballY = getHeight()/2-BALL_RADIUS;
		ball = new GOval (ballX, ballY, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}
	
	/**
	 * moveBall describes the motion of the ball, defining the initial vx and vy of the ball, as well as moving the ball initially and also throughout the entire span of the ball's life. 
	 * Precondition: Ball should have been made and centered in middle of screen.
	 * Postcondition: Ball will start off by moving downwards at a random vx and a set vy after the mouse has been clicked by player. Ball will also bounce off of walls, unless it hits the bottom wall. Ball will also collide with other objects on the screen, such as paddle and bricks, breaking the bricks.
	 */
	private void moveBall() {
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5) ) {
			vx = -vx;
		}
		vy=VELOCITY_Y;
		waitForClick();
		while(true) {
			ball.move(vx,vy);
			ifBallHitsWall();
			ifCollision();
			pause(DELAY);
		}
	}
	
	/**
	 * ifBallHitsWall describes the resulting motion of the ball, if the ball hits a wall. The ball will bounce off of the wall if it hits the top or side walls, but will result in a loss if it hits the bottom wall.
	 * Precondition: Ball must have been made and movement of ball must have already been initialized. Ball, however, will not yet be capable of bouncing off of the walls.
	 * Postcondition: Ball will now be bale to bounce off of walls it touches (except for the bottom wall) - which will react differently to, by being removed from the screen and decreasing total amount of lives by 1.
	 */
	private void ifBallHitsWall() {
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx=-vx;
		} else if(hitTopWall(ball)) {
			vy=-vy;
		} else if(hitBottomWall(ball)) {
			if (numberOfBricks<=0) {
				vy=-vy;					// This is in the case that all of the bricks are gone from the screen, indicating that the user has won. This will be useful because the ball will be unable to exit the screen, which will make the generateCircles extension much more interesting.
			} else {
				ifHitsBottom();
			}
		}
	}

	/**
	 * ifHitsBottom describes the set of action that will happen to the ball if it touches the bottom wall, namely, it will be removed from the screen. If there are still lives left, another ball will be added, but if no more lives remain, the conclusive label reading "You lose." will appear.
	 * Precondition: Ball will simply move into the bottom wall, and continue to move forever downwards off of the screen.
	 * Postcondition: Ball will be removed as soon as it hits the bottom wall and life ("numberTurnsLeft") will drop by 1. If lives still remain, another ball will be added, and play will resume, but if no more balls are available, then the words "You lsoe" will appear onto the scren.
	 */
	private void ifHitsBottom() {
		remove(ball);
		turnCounter();
		add (lose());
	}
	
	/**
	 * turnCounter will keep count of the amount of lives that the user still has left, and will react accordingly to the amount of lives that the player still has left.
	 * Precondition: Ball will have hit the bottom of the screen and removed. A label will be on the screen reading the amount of lives the player still has left.
	 * Postcondition: Lives (numberTurnsLeft) will be decreased by 1 if ball hits the bottom wall. Label will now read the new amount of lives (the previous minus one). If lives still remain, another ball will be made and moved, so that play can resume.
	 */
	private void turnCounter() {
		numberTurnsLeft=numberTurnsLeft-1; //keeps track of amount of lives left for the player. decreases by 1 every time ball hits the bottom wall.
		if (numberTurnsLeft>= 0) {
			turnsLeftLabel.setLabel("Lives Left: " + numberTurnsLeft); //Label will read newly updated amount of lives (as a result of loss of one life).
		}
		while (numberTurnsLeft>0) {
			makeBall();
			moveBall();
		}
	}
	
	/**
	 * turnsLeftLabel will modify a label to read how many lives/turns the player still has left. This value will start off at the total number of turns the user has, denoted N_TURNS.
	 * Precondition: None. 
	 * Postcondition: Method for label for turnsLeft will be created, with a now set text, font, and location. Text will read the amount of turns/lives the player has left.
	 */
	// Extension (turns Left Label)
	private GLabel turnsLeftLabel() {
		turnsLeftLabel = new GLabel ("Lives Left: " + NTURNS);
		turnsLeftLabel.setFont("TimesRoman-14");
		turnsLeftLabel.setLocation(turnsLeftLabel.getWidth()/3,getHeight()-turnsLeftLabel.getAscent());
		return turnsLeftLabel;
	}
	
	/**
	 * GLabel loss will appear upon player running out of lives completely. lose() will modify this loss label based on specifications experimented on, to make sure it appears in right location, with good font, and text.
	 * Precondition: No loss label will have yet been added to the screen. 
	 * Postcondition: loss label will be modified and ready to add to the screen. Loss label can be easily called upon by simply adding lose(), this private method.
	 */
	private GLabel lose() {
		GLabel loss = new GLabel ("You Lose. :c");
		loss.setFont("TimesRoman-30");
		double lossX = (getWidth()-loss.getWidth())/2;
		double lossY = (getHeight()-loss.getAscent())/2;
		loss.setLocation(lossX,lossY);
		return loss;
	}
	
	/**
	 * Boolean that tests if the ball has hit the bottom wall. Returns false or true.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY()+ball.getHeight()>=getHeight();
	}
	
	/**
	 * Boolean that tests if the ball has hit the top wall. Returns false or true.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY()<=0;
	}
	
	/**
	 * Boolean that tests if the ball has hit the right-side wall. Returns false or true.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX()+ball.getWidth()>=getWidth();
	}
	
	/**
	 * Boolean that tests if the ball has hit the left-side wall. Returns false or true.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX()<=0;
	}
	
	/**
	 * ifCollision details the motion of the ball after it collides with certain given objects. 
	 * Precondition: Ball will have collided with an object; a set of rules for collisions needs to be defined.
	 * Postcondition: If it collides with a paddle, ball will simply reverse directions of vy unless it hits the edge, in which case it will actually change vx as well. If collider is turnsLeftLabel or scoreLabel, nothing will happen. If collider is the win label, random circles will be generated (EXTENSION) and if collider is a brick or a circle, then that brick/circle will be removed and another set of rules will also take place (described below).
	 */
	private void ifCollision() {
		collider = getCollidingObject();
		if (collider != null) {	
			bounceClip.play();
			if (collider == paddle) {
				if (vy>0) {
					edge();
					vy=-vy;
				}
			} else if (collider==turnsLeftLabel || collider==scoreLabel) {
			
			} else if (collider == win) {
				generateCircles();
				vy=-vy;
			} else {
				ifOtherObject();
			}
		}
	}
	
	/**
	 * ifOtherObject is a private method that explains what happens when the ball hits a "random circle" or during gameplay, a brick. 
	 * Precondition: Ball hits the brick or circle, a set of rules is needed to describe motion of ball after collision as well as what happens to the game as a result of this collision.
	 * Postcondition: Depending on color of brick hit, a certain number of points will be added to player's score shown in the bottom-right hand corner. The collider will be removed. Brick will be counted toward one brick closer to win. Game difficulty will change after a certain amount of bricks are hit, and when all bricks are hit, a "win" message will be displayed.
	 */
	private void ifOtherObject() {
		scoreCounter(collider); //EXTENSION
		remove (collider);
		brickCounter();
		gameDifficulty();
		winCondition();
		vy=-vy;
	}
	
	/**
	 * vx will change as a result of the ball hitting the edge of the paddle.
	 * Precondition: Ball hits the edge of the paddle.
	 * Postcondition: If the right side of the ball hits the left side of the paddle while ball is moving to the left, then vx direction of ball will change. If left side of ball hits right side of paddle while ball is moving to right, then vx direction of ball will change.
	 */
	//Extension Changing vx of bounce 
	private void edge() {
		double paddleXRightEdge = paddle.getX()+PADDLE_WIDTH;
		double paddleXLeftEdge = paddle.getX();
		double paddleYTopEdge = paddle.getY();
		if (getElementAt(paddleXRightEdge,paddleYTopEdge-1) != null && vx<0 && getElementAt(ball.getX(),ball.getY()+(BALL_RADIUS*2)+1) != null) {
			vx=-vx;
		} else if (getElementAt(paddleXLeftEdge,paddleYTopEdge-1) != null && vx>0 && getElementAt(ball.getX()+(BALL_RADIUS*2),ball.getY()+(BALL_RADIUS*2)+1) != null) {
			vx=-vx;
		}
	}
	

	/**
	 * scoreKeeper Label displays the score in lower-right hand corner of the screen. Initializes the scoreLabel when no bricks have been hit yet.
	 * Precondition: None.
	 * Postcondition: Label will appear showing the score in the game in the lower-right hand corner.
	 */
	private GLabel scoreKeeper() {
		score = 0;
		scoreLabel.setLabel("Your score: " + score);
		scoreLabel.setFont("TimesRoman-14");
		scoreLabel.setLocation(getWidth()-(scoreLabel.getWidth()+25),getHeight()-scoreLabel.getAscent());
		return scoreLabel;
	}
	
	/**
	 * scoreCounter keeps track of the score of the player, depending on the color of the object hit. scoreCounter will also update scoreLabel to keep track of the score for the game. 
	 * Precondition: Brick is hit.
	 * Postcondition: Depending on color of brick hit, the scoreCounter will add a certain amount of points for the color of the brick hit to the total score. scoreCounter will also update the scoreLabel to read the new score in the game.
	 */
	private void scoreCounter(GObject collider) {
		if (collider.getColor() == Color.CYAN) {
			score=score+1;
		} else if (collider.getColor() == Color.GREEN) {
			score=score+2;
		} else if (collider.getColor() == Color.YELLOW) {
			score=score+3;
		} else if (collider.getColor() == Color.ORANGE) {
			score=score+5;
		} else if (collider.getColor() == Color.RED) {
			score=score+7;
		}
		scoreLabel.setLabel("Your score: " + score);
	}
	
	/**
	 * brickCounter will subtract 1 for every brick hit.
	 * Precondition: Brick is hit.
	 * Postcondition: numberOfBricks is subtracted by 1 to keep track of the amount of bricks left in the game.
	 */
	private int brickCounter() {
		numberOfBricks = numberOfBricks -1;
		return numberOfBricks;
	}

	/**
	 * gameDifficulty will change the difficulty of the game by increasing the vy of the ball by 1.03 x for every brick hit after the number of bricks left falls below a third of the original number of bricks.
	 * Precondition: 2/3 of the amount of the original number of bricks remain.
	 * Postcondition: For every additional brick hit, the vy will increase by 1.03 times in order to increase the difficulty of the game.
	 */
	private void gameDifficulty() {
		if (numberOfBricks <= (2*(NBRICK_COLUMNS*NBRICK_ROWS)/3)) {
			vy=vy*1.03; //increase speed by 1.03 times for every brick hit after number of bricks left falls below a third of the original number of bricks.
		}
	}
	
	/**
	 * winCondition details the win condition of the game, where number of bricks remaining is 0.
	 * Precondition: No more bricks remain on the screen.
	 * Postcondition: method win() which is a GLabel method is added, resulting in the words "You Win! :3" on the screen.
	 */
	private void winCondition() {
		if (numberOfBricks == 0) {
			add (win());
		}
	}
	
	/**
	 * GLabel method is created under the name win() that modifies the previously defined instance variable GLabel win in order to add it to the screen after all of the blocks have disappeared in a method called winCOndition().
	 * Precondition: win is previously defined as an instance variable. No win message has been added to the screen yet. All bricks are gone.
	 * Postcondition: Through winCondition() method returning to this GLabel method, the GLabel is added to the center of the screen with chosen font and message.
	 */
	private GLabel win() {
		win.setLabel("You Win! :3");
		win.setFont("TimesRoman-30");
		double winX = (getWidth()-win.getWidth())/2;
		double winY = (getHeight()-win.getAscent())/2;
		win.setLocation(winX,winY);
		return win;
	}
	
	/**
	 * getCollidingObject() finds out whether there are any objects touching one of the four corners of the ball. 
	 * Precondition: none.
	 * Postcondition: If object is touching, one of the four corners of the ball, hitObject will name this object in contact with the ball. If no objects are touching on of the four corners, then hitObject will be null/ method getCollidingObject() will return a null. This null or presence of an object will dictate later on whether or not to initiate method collision().
	 */
	private GObject getCollidingObject() {
		GObject hitObject = null;
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			hitObject = getElementAt(ball.getX(),ball.getY()); //top-left Corner
		} else if (getElementAt(ball.getX()+(2*BALL_RADIUS),ball.getY()) != null) {
			hitObject = getElementAt(ball.getX()+(2*BALL_RADIUS),ball.getY());  //top-right corner
		} else if (getElementAt(ball.getX(),ball.getY()+(2*BALL_RADIUS)) != null) {
			hitObject = getElementAt(ball.getX(),ball.getY()+(2*BALL_RADIUS)); //bottom-left corner
		} else if (getElementAt(ball.getX()+(2*BALL_RADIUS),ball.getY()+(2*BALL_RADIUS)) != null) {
			hitObject = getElementAt(ball.getX()+(2*BALL_RADIUS),ball.getY()+(2*BALL_RADIUS)); //bottom-right corner
		} 
		return hitObject;
	}
	
	/**
	 * generateCircles will generate circles of random radius and position throughout the screen after a victory by the player, where all of the blocks are broken. generateCircles is initiated by ball hitting the win label.
	 * Precondition: All blocks have been broken. Win label is hit by the ball.
	 * Postcondition: A random circle is generated onto the screen, that is actually also breakable by the ball.
	 */
	//Extension: Generate Random Circles after win
	private void generateCircles() {
		double radius = rgen.nextDouble(MIN_RADIUS,MAX_RADIUS);
		double xCircle = rgen.nextDouble(0, getWidth() - 2 * radius);
		double yCircle = rgen.nextDouble(0,getHeight() - 2 * radius);
		GOval circle = new GOval(xCircle, yCircle, 2 * radius, 2 * radius);
		circle.setFilled(true);
		circle.setColor(rgen.nextColor());
		add(circle);
	}
}
