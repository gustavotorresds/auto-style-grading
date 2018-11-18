/*
 * Extensions:
 * 		- Life Counter
 * 		- Score Counter
 * 		- Speeds up after 7 paddle hits with a notification
 * 		- Game Over or You Won screen cap
 * 		- Background Music
 * 		- bounce, dying, winning, and new life audio
 * 		- ball is replaced with an image of my RA's face
 * 		- improved edge detection
 */
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.MediaTools;
import acm.util.RandomGenerator;

public class BreakoutExtended extends GraphicsProgram {
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
	public static final double PADDLE_WIDTH =  60; // 420;
	public static final double PADDLE_HEIGHT = 10; // 380 ;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	//Dimensions of the Game Over & You Won! Tile
	public static final double TILE_WIDTH = 350;
	public static final double TILE_HEIGHT = 100;

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
	public static final String SCREEN_FONT = "SansSerif-BOLD";

	//image file used for the ball
	public static final Image AKSHAY = MediaTools.loadImage("akshay.png"); 

	//instance variables
	private int npaddle_hit; //number of times the ball hits the paddle
	private int brickCount; //number of bricks left
	private GRect paddle;
	private GImage akshayBall;
	
	//for display
	private int life = NTURNS; //life count
	private GLabel lifeCounter; 
	private GLabel scoreCounter;
	private int points; //score count
	private GLabel speedUpNotification ;
	private double vx, vy; //x-component and y-component of velocity of ball
	private RandomGenerator rgen = RandomGenerator.getInstance(); //gives back random numbers


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setBricks(); //makes the rainbow rows of bricks
		setGame(); 
		addMouseListeners (); //for the paddle
	}

	/**
	 * method: setGame()
	 * --------------------
	 * 1. make a score & lives counter
	 * 2. make the paddle which will be controlled by the user
	 * 3. add the moving ball to the center of the screen to begin with
	 * 4. runs the game
	 * 		- ball bounces around
	 * 		- re-updates when life lost
	 * 		- keeps going until max lives used or all bricks gone
	 */
	private void setGame() {
		AudioClip music = MediaTools.loadAudioClip("backGroundMusic.mp3");
		music.play();

		makeScoreCounter();
		makeLivesCounter();
		makePaddle();
		akshayBall = makeBall();
		moveBallToCenter (akshayBall);
		runGame();
	}

	/**
	 * Method: newLife() 
	 * ----------------------
	 * Only called if 
	 */
	private void newLife () {
		pause (DELAY *50) ; //small delay before resetting ball
		AudioClip newLife = MediaTools.loadAudioClip("newLife.wav");
		newLife.play (); 
		
		//if the player loses a life before the 8th hit, remove the speed up Notification 
		if (speedUpNotification!=null) {
			remove (speedUpNotification);
		}
		moveBallToCenter(akshayBall);
		updateLivesCounter();
		npaddle_hit = 0;
		runGame(); //speed resets 
	}


	/**
	 * Method: YouWon
	 * ----------------------------
	 * When all the bricks have been removed, displays "You Won !"
	 * and the youWon song plays
	 */
	private void YouWon() {
		AudioClip youWon = MediaTools.loadAudioClip("youWon.wav");
		youWon.play (); 
		
		double x = (getWidth() - TILE_WIDTH) /2;
		double y = (getHeight() - TILE_HEIGHT)/2;
		GRect tile = new GRect (x, y, TILE_WIDTH, TILE_HEIGHT);
		tile.setFilled (true);
		add (tile);

		//creating the text of the label
		GLabel YouWon = new GLabel ("Congratulations!");
		YouWon.setFont("SCREEN_FONT-35");
		YouWon.setColor(Color.PINK);

		//centering the text
		double textX = x + ((TILE_WIDTH - YouWon.getWidth())/2 );	
		double textY = y + (TILE_HEIGHT + YouWon.getAscent())/2;
		add (YouWon, textX, textY);
	}


	/**
	 * Method: GameOver
	 * ----------------------------
	 * When lives are maxxed out, displays "Game Over" on the Screen
	 * game over audio plays
	 */
	private void GameOver() {
		//makes a blue tile for text to go over
		AudioClip gameOver = MediaTools.loadAudioClip("gameOver.wav");
		gameOver.play (); 
		
		double x = (getWidth() - TILE_WIDTH) /2;
		double y = (getHeight() - TILE_HEIGHT)/2;
		GRect tile = new GRect (x, y, TILE_WIDTH, TILE_HEIGHT);
		tile.setFilled (true);
		add (tile);

		//creating the text of the label
		GLabel GameOver = new GLabel ("Game Over");
		GameOver.setFont("SCREEN_FONT-40");
		GameOver.setColor(Color.WHITE);

		//centering the text
		double textX = x + ((TILE_WIDTH - GameOver.getWidth())/2 );	
		double textY = y + (TILE_HEIGHT + GameOver.getAscent())/2;
		add (GameOver, textX, textY);
	}


	/**
	 * Method: makeScoreCounter
	 * -----------------------
	 * Adds text to screen for the score count
	 */
	private void makeScoreCounter() {
		//creating the text of the label
		scoreCounter = new GLabel ("Score: "+ points);
		scoreCounter.setFont("SCREEN_FONT-15");

		add (scoreCounter, 15, 36);
	}

	/**
	 * Method: addPoints
	 * -----------------------
	 * The color of the bricks have corresponding point values with the red brick having the highest value
	 * Updates the points to keep track of score
	 */
	private void addPoints(GObject collider) {
		if (collider.getColor() == Color.RED) {
			points += 5;
		}
		if (collider.getColor() == Color.ORANGE) {
			points += 4;
		}
		if (collider.getColor() == Color.YELLOW) {
			points += 3;
		}
		if (collider.getColor() == Color.GREEN) {
			points += 2;
		}
		if (collider.getColor() == Color.CYAN) {
			points += 1;
		}
	}

	/**
	 * Method: updateScoreCounter
	 * ---------------------------
	 * Whenever points are added, the score count is updated
	 */
	private void updateScoreCounter() {
		scoreCounter.setLabel ("Score: "+ points);
	}

	/**
	 * Method: makeLivesCounter
	 * -----------------------
	 * Adds text to screen for the life count
	 */
	private void makeLivesCounter() {
		//creating the text of the label
		lifeCounter = new GLabel ("Lives: "+ life);
		lifeCounter.setFont("SCREEN_FONT-15");

		add (lifeCounter, 15, 20); 
	}

	/**
	 * Method: updateLivesCounter()
	 * ----------------------------
	 * Whenever a life is lost, the life counter is updated
	 */
	private void updateLivesCounter() {
		lifeCounter.setLabel ("Lives: "+ life);
	}

	/**
	 * Method: mouseMoved ()
	 * ------------------------------
	 * Makes it so the paddle moves in correlation the movement of the mouse
	 */
	public void mouseMoved (MouseEvent e){
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT ;  //y coordinate of paddle will stay fixed
		int mouseX = e.getX(); //x-coordinate is dependent on mouse movement
		double maxX = getWidth() - PADDLE_WIDTH; //the max coordinate cursor is allowed to go

		//exception where user is putting the cursor beyond the boundary of the canvas
		//makes sure entire paddle is always visible
		if (mouseX > maxX) {
			paddle.setLocation(maxX, y);
		}
		else {
			paddle.setLocation(mouseX,y);
		}
	}

	/**
	 * method: makePaddle()
	 * ---------------------------
	 * Creates a paddle (same look as the bricks but black) which the user will be able to control
	 */
	public void makePaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT; //the paddle is slightly elevated from the bottom of the screen
		add (paddle,0, y);
	}

	/**
	 * Method: isGameRunning ()
	 * ---------------------------
	 * Returns boolean
	 * true if:
	 * 		1. brick count is not 0
	 * 		2. the ball did not hit the bottom wall
	 */
	private boolean isGameRunning () {
		return (brickCount !=0 && !hitBottomWall());
	}


	/**
	 * Method: runGame() 
	 * --------------------------
	 * Continually has the ball moving
	 * Rebounds off walls and paddles
	 * If the ball hits a brick, it will remove the brick and change trajectory
	 */
	private void runGame() {	
		//sets initial velocities
		vx = rgen.nextDouble (VELOCITY_X_MIN ,VELOCITY_X_MAX); //random double in the range of 1.0 to 3.0
		if (rgen.nextBoolean (0.5)) { 
			vx = -vx; //sets vx to negative half of the time
		}
		vy = VELOCITY_Y; //y velocity is constant

		waitForClick(); //only starts when user has clicked canvas
		while (isGameRunning()) {  //movement until game is temporarily stopped
			moveBall();
		}

		life--; //one life closer to maxxing out
		//when the game is not running, check for three conditions:
		if (hitBottomWall() && life > 0) { //ball hit the bottom but user still has lives left
			newLife();
		}
		else if  (hitBottomWall() && life ==0){ //ball hit the bottom and user has max-ed out of lives
			updateLivesCounter();
			GameOver();
		}
		else { //all bricks have been removed
			YouWon();
		}
	}

	/**
	 * Method: getCollidingObject
	 * -------------------------
	 * Checks points of the box GImage is enclosed in and see if it overlaps with any objects
	 * If so, the method returns this object
	 * Otherwise, it will return null
	 */
	private GObject getCollidingObject() {
		double ballWidth = akshayBall.getWidth();
		double ballHeight =akshayBall.getHeight();
		//x and y coordinate of the top left corner of ball's box
		double x = akshayBall.getX();
		double y = akshayBall.getY();

		
		if (getElementAt (x ,y) != null) {//checks top left corner
			return (getElementAt (x , y)); 
		}
		else if (getElementAt (x + ballWidth, y) != null) {//checks top right corner
			return (getElementAt (x + ballWidth, y));  
		}
		else if (getElementAt (x + ballWidth, y + ballHeight) != null) { // checks bottom left corner
			return (getElementAt (x + ballWidth, y+ ballHeight)); 
		}
		else if (getElementAt (x + ballWidth, y + ballHeight) != null) { // checks bottom right corner
			return (getElementAt (x + ballWidth, y+ ballHeight));
		}
		else if (getElementAt (x + ballWidth/2, y) != null) { // checks top middle
			return (getElementAt (x + ballWidth/2, y ));
		}
		else if (getElementAt (x, y + ballHeight/2) != null) { // checks right middle
			return (getElementAt (x , y + ballHeight/2 ));
		}
		else if (getElementAt (x + ballWidth/2, y + ballHeight) != null) { // checks bottom middle
			return (getElementAt (x + ballWidth/2 , y + ballHeight ));
		}
		else if (getElementAt (x , y + ballHeight/2) != null) { // checks bottom middle
			return (getElementAt (x, y + ballHeight/2 ));
		}
		else {
			return (null);
		}
	}


	/**
	 * Method: moveBall()
	 * --------------------------
	 * Precondition: only called if there are bricks present and the ball did not hit the bottom wall
	 * Postcondition:
	 * 		Changes trajectory when top, right, or left wall is hit
	 * 		Changes trajectory to upwards when a paddle is hit
	 * 		Temporarily stops game if bottom wall is hit
	 * 		If a brick is hit, removes the brick and changes trajectory
	 */
	private void moveBall() {
		//bouncing sound
		AudioClip bounceClip = MediaTools.loadAudioClip("boing.wav");
		
		GObject collider  = getCollidingObject(); //object that ball will collide into
		
		//changes trajectory any time the ball hits a wall or paddle
		if (hitLeftWall() || hitRightWall() ) { 
			vx = -vx;
		}	
		else if (hitTopWall()) {
			//to debug for if ball gets weirdly stuck at top of the world
			akshayBall.setLocation (akshayBall.getX(), 0);
			vy = -vy;
		}
		else if (collider == paddle) {
			bounceClip.play(); //plays "boing" audio

			//this is to debug for when the ball seems to get stuck between paddle
			//as soon as it hits the paddle, it sets the balll location to exactly on top of the paddle
			akshayBall.setLocation (akshayBall.getX(), getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET - akshayBall.getHeight());

			npaddle_hit ++; 
			if (npaddle_hit == 7) { //once the player hits the paddle seven times, the speed ramps up
				speedUp();
			}
			if (npaddle_hit == 8) { //notification goes away after next hit
				remove (speedUpNotification);
			}
			
			vy = -vy; //also changes trajectory
		}

		//if the ball hits a brick, it removes the brick and changes trajectory
		//brick will be any collider that is not a paddle, lifeCounter, or scoreCounter
		else if (collider != paddle && collider !=lifeCounter && collider != akshayBall && collider != scoreCounter && collider != null) {
			bounceClip.play(); //plays "boing" audio
			//since it hit a brick, points go up 
			brickCount--; 
			addPoints(collider);
			updateScoreCounter();
			remove (collider);

			vy= -vy;
		}

		//update frame
		akshayBall.move(vx, vy); //update ball position
		//println (akshayBall.isVisible());
		pause (DELAY); //delays the movement so that the person can see  
	}


	/**
	 * method: speedUp
	 * ---------------------
	 * ramps up the speed of the ball if the player hits the paddle seven times
	 * Accompanied by a notification
	 */
	private void speedUp() {
		//doubling the horizontal velocity
		vx = rgen.nextDouble (VELOCITY_X_MIN*4 ,VELOCITY_X_MAX*4);

		//letting the user know that the game has sped up
		speedUpNotification = new GLabel ("Speed Up!");
		speedUpNotification.setFont("SCREEN_FONT-30");

		//centering the text across the screen
		double textX = (getWidth() - speedUpNotification.getWidth())/2 ;	
		double textY = 30;
		add (speedUpNotification, textX, textY);
	}

	/**
	 * Methods: hitBottomWall, hitTopWall, hitRightWall, hitLeftWall
	 * ------------------------------------------------------
	 * series of checks to see if the ball hits any walls
	 * returns true or false
	 */
	private boolean hitBottomWall() {
		return (akshayBall.getY() >= (getHeight() - akshayBall.getHeight()));
	}

	private boolean hitTopWall() {
		return (akshayBall.getY()<=0);		
	}

	private boolean hitRightWall() {
		return (akshayBall.getX() >= (getWidth()-akshayBall.getWidth())) ;	
	}

	private boolean hitLeftWall() {
		return (akshayBall.getX()<=0) 	;	
	}

	/**
	 * Method: moveBallToCenter
	 * ---------------------------
	 * @parameter akshay's face as the ball
	 * @return places the ball in the center of the canvas
	 */
	private void moveBallToCenter (GImage akshayBall) {
		double x = (getWidth() - (akshayBall.getWidth())) /2 ;
		double y = (getHeight() + (akshayBall.getHeight())) /2 ;
		akshayBall.setLocation(x, y);
	}

	/**
	 * Method: makeBall
	 * ---------------------------
	 * Will return a gImage of my RA's face
	 */
	private GImage makeBall () {
		GImage akshayBall = new GImage(AKSHAY); 
		akshayBall.scale(0.2);
		add (akshayBall);
		return akshayBall;
	}


	/**
	 * Method: setBricks
	 * --------------------------
	 * Creates ten rows of bricks each separated by a set small space
	 * The colors will alternate every two rows to make a rainbow like pattern
	 */
	private void setBricks() {
		Color color = null; //color of bricks 
		//rowWidth = (brick width * num of bricks in row) + (space between bricks * num of spaces)
		double rowWidth = (BRICK_WIDTH * NBRICK_COLUMNS) + ( BRICK_SEP * (NBRICK_COLUMNS-1));  

		for (int row=0; row < NBRICK_COLUMNS; row ++) {
			double x = (getWidth() - rowWidth) /2; //centers the row against the canvas size
			double y = BRICK_Y_OFFSET + (BRICK_HEIGHT *row) + (BRICK_SEP *row); //sets the y coordinate of the row 
			for (int column = 0; column < NBRICK_ROWS; column ++) {
				//assigning colors to the rows
				if (row< 2) {
					color = Color.RED;
				} 
				else if (row >= 2 && row < 4) {
					color = Color.ORANGE;
				}
				else if (row>=4 && row <6) {
					color = Color.YELLOW;
				}
				else if (row >=6 && row < 8) {
					color = Color.GREEN;
				}
				else if (row >= 8 ) {
					color = Color.CYAN;
				}

				GRect brick = makeBrick (color); // creates a brick of the right color
				add (brick,x,y); // adds it to the world
				brickCount++; 
				x += BRICK_WIDTH + BRICK_SEP; //advances to next x coordinate for the brick
			}
		}

	}

	/**
	 * Method: makeBrick
	 * @param color (red, orange, yellow, green, cyan)
	 * ---------------------------
	 * @return will return a filled rectangle with given dimensions 
	 */
	private GRect makeBrick (Color color) {
		GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor (color);
		return brick;
	}

}

