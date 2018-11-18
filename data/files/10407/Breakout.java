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

	// Dimensions of the canvas, in pixels, used to set up the initial size of the game. 
	
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
	public static final double BRICK_HEIGHT = 80; 
	

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 30;
	public static final double PADDLE_HEIGHT = 10;
 
	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 5;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; i.e. these are the bounds within which the ball's velocity will be selected random for every move it makes. .
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 6.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 600.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//Creates the Paddle as an Instance Variable
	private GRect paddle;
	
	//Creates a Ball as an Instance Variable
	private GOval ball;
	
	//Creates an image to be displayed when the game has ended - leaving the decision on what that image will be, until the user has either lost or won the game. 
	private GImage ImageVerdict;
	
	//Starts a tally from the original number of bricks, so the number of bricks in total columns * number of bricks in total rows can be stored as a number we can actually follow down to zero (i.e. if we use this sum later to decided when bricks = 0 using one times the other, the sum would come out zero even if one equals zero and the other still contains 1). it will be used to determine when the game is over before lives are over, and the player has won, or that the lives are over before the number of bricks has been reduced to zero; and the player has lost. 
	private int bricksLeft = 100;  
	
	//This simply displays, for the user, what the integer above calculates. 
	private GLabel brickCounter;
	
	//Gives the ball a velocity which is stored as an instance variable
	private double vx, vy;
	
	//Creates an instance variable that can randomly generate numbers 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Adds a sound to the game that will later be called so as to recreate the sound of a ball colliding with an object. 
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// 
	
	public void run() {
		// Sets the window's title bar text
		setTitle("CS 106A Breakout");

		// Sets the canvas size. 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
 
		//Sets up the game so all bricks are laid and colored, brick counter is displayed, paddle and ball are visible and awaiting players action to begin playing.
		createGameBoard(); 
		//User has started game, and brick breaker will now be played where the user, using the ball and paddle to collide with all bricks on the screen - until either all bricks have been removed (in which case they have won the game) or all lives have been lost (in which case they have lost.)
		play(); 
			}  
	
	//Adds bricks, in a 10 by 10 formation (so in total there are 100 bricks), creates and adds a paddle which will be under the users mouse control and which will interact with the ball - also created and added to the screen - so that if there is a collision the ball will reverse in direction. The ball is created with a velocity in both defined y and x direction (although the x will be random). the background is also colored in a color not used for the additon of the bricks.
	private void createGameBoard() {
		addBricks();  
		paddle = createPaddle();
		addCenteredPaddle();
		setBackground(Color.pink);
		addMouseListeners(); 
		ball = createBall();
	}
 
//Creates an ongoing loop determining how long the game will be "played" (i.e. the user can control the paddle and try to collide it with balls) based on predetermined lives and brick numbers, established before the loop is initiated. The loop will keep the ball moving, and keep it in play to potentially collide with bricks, until is broken. it is broken indefinitely by either the ball crossing the y boundary where the paddle is offset from for the third time (i.e. all lives running out). the loop is broken to return to the top of the play method twice before this happens. the other finite case, occurs when the user has removed all bricks. in this case the game is over because the user has won. the user will be then be notified with text and image, either way.
	private void play() {
		int livesUsed = 0;  
		addBrickCounter(); 
		add(brickCounter);
		while(livesUsed < NTURNS) {
			if(bricksLeft == 0) { 
				break;
				}
				addBallToScreen(getWidth()/2, getHeight()/2);  
				userSelfStart(); 
				initializeBallVelocity();
				//this while loop allows the user to keep the ball in play using their paddle and breaks when they have missed the ball or when there are no more bricks.
				while(true) {
					moveBall();
						if(ball.getY() > getHeight()) {
							remove(ball); 
							livesUsed++;
							break;
							}  
								if(bricksLeft == 0) { 
									textVerdict("YOU WIN !!");
									pause(1000); 
									ImageVerdict = new GImage("giphy.gif"); 
									addImage();
									break; 
									} 
						
					}
			}   
			if(bricksLeft > 0) {
				textVerdict("GAME OVER"); 
				pause(1000);
				ImageVerdict = new GImage("lose.gif");   
				addImage();
				}
		} 
		
	//Displays for the user how many bricks were originally present - how many they have hit with their ball and removed from screen. they will work from 100 to 0 in this case.
	private GLabel addBrickCounter() {
		int x = 15;
		int y = 20; 
		brickCounter = new GLabel("Bricks Left To Hit: " + bricksLeft, x, y); 
		brickCounter.setFont("bold-12");
		return brickCounter; 
		}

	//Displays an explicit image with its import linked to the GImage instance variable, based on whether they won or lost - and rely whichever verdict this was.
	private GImage addImage() {  
		ImageVerdict.setSize(getWidth(), getHeight()/2);  
		add(ImageVerdict); 
		return(ImageVerdict);
		}

	//Displays the same verdict as above but as text, and located center screen with defined size.
	private GLabel textVerdict(String verdict) {
		GLabel notification = new GLabel(verdict);
		notification.setFont("bold-70");
		double x = getWidth()/2 - notification.getWidth()/2;
		double y = (getHeight()*2)/3 + notification.getAscent()/2;
		add(notification, x, y);
		return notification;
		}

	//Waits before proceeding through the program until the user has actively clicked the screen. this occurs ever time the ball is readded to the screen.
	private void userSelfStart() { 
		GLabel notifyUser = new GLabel("Click anywhere to start");
		add(notifyUser, getWidth()/2, getHeight()/2 - 2*BALL_RADIUS);
		waitForClick();
		remove(notifyUser);
		}

	//Creates an interaction between the ball and any other object, aside from labels, on the screen. furthermore, it distinguishes between whether this interaction is between the ball and the paddle, or brick. What happens next is also based on this answer. If the collided object is within the dimensions of the paddle, the ball reverses y velocity, if the object is a brick this reversal also happens, but the brick is also removed from the screen.  
	private void checkWhatColliderWas(GObject collider) {
		double m = getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT + BALL_RADIUS*2);
		double n = m + 12; 
		//doesn't work to ask this to only work when ball hits exact top of paddle, because it moves a displacement of 3y every move, so here we look for collision anywhere the ball touches in this one movement
		if(collider == paddle && ball.getY() >= m && ball.getY() <= n) { 
			println("vx:" + vx + "vy:" + vy + "x:" + ball.getX() + "y:" + ball.getY());
			bounceClip.play();
			vy = -vy; 
				} else if(collider != null && collider != paddle && collider != brickCounter) {
					bounceClip.play(); 
					remove(collider); 
					//Updates brick counter so for every brick removed it displays previous number of bricks - 1 and displays new, correct value
					remove(brickCounter); 
					vy = -vy;  
					bricksLeft--;  
					addBrickCounter(); 
					add(brickCounter);
				}
		} 
 
	//sets up the change in y and change in x for each ball movement, using an initial downward y movement, and random x movement which will reverse every other move the ball makes. 
	private void initializeBallVelocity() { 
		vy = 3.0; 
		//Speeds up the ball x velocity if the user passes a half way point in removing the bricks
		if(bricksLeft < (NBRICK_COLUMNS*NBRICK_ROWS)/2) {
				vx = rgen.nextDouble(4.0, 6.0); 
		}
			else {
					vx = rgen.nextDouble(1.0, 3.0);
				}
		if(rgen.nextBoolean(0.5))
			vx = - vx;
	}
	
	//Takes these initialized ball x and y velocity and actually moves the ball by these so it can be seen to move fluidly on the screen. Reverses ball y if it collides with either paddle, top screen, or brick, and reverses x value when collision occurs with a brick or left/right wall - all so as to keep the ball in play.  
	private void moveBall() {  
		ball.move(vx, vy);  
		//check if next position on vx will be left wall. if at left wall it means the ball had been traveling with a negative velocity i.e. negative x so use - (-vx) to check relation to wall.
		//same for the right wall, except ball will have been traveling with positive x up until boundary and so signs need to be reversed
		//we want to reverse the trajectory of the ball to create this appearance of the ball bouncing of the wall
		 
			if((ball.getX() - vx <= 0 && vx< 0) || (ball.getX() + vx >= CANVAS_WIDTH - BALL_RADIUS*2 && vx > 0)) {
				bounceClip.play();
				vx = -vx; 
				}  
		//same idea of top boundary as canvas boundary though as ball can be missed by paddle and fall past bottom boundary register this instead as life lost and dont reverse trajectory 
				if((ball.getY() + vy <= 0 && vy < 0)) { 
					bounceClip.play();
					vy = -vy; 
					}   
						GObject collider = getCollidingObject();
						checkWhatColliderWas(collider); 
						pause(DELAY);
						}
	
//Creates a solid ball from defined radius and returns the creation
	private GOval createBall() {
		GOval ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		return ball;
		}
	
//Actually adds the ball to the screen so it can be seen and followed by the user. it adds every time from center x, y location with specific color
	private void addBallToScreen(double x, double y) { 
		ball.setLocation(x, y);
		ball.setColor(Color.gray);
		add(ball);  
		}

	//Defines a reaction to user mouse movements. here the paddle will be seen to follow the users mouse; with its location updating as the mouse's does
	public void mouseMoved(MouseEvent e) {
		double x = e.getX(); 
		double y = getHeight() - PADDLE_Y_OFFSET; 
			if(x > CANVAS_WIDTH -PADDLE_WIDTH) {
				paddle.setLocation(CANVAS_WIDTH-PADDLE_WIDTH, y);
				} 	else { 
						paddle.setLocation(x, y); 
						}  
			}
	
	//Adds paddle to screen at centered location
	private void addCenteredPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2;
		double y = getHeight()- PADDLE_Y_OFFSET; 
		add(paddle, x, y);
		}
  	 
	//Creates the paddle that will be added to screen and controlled by user, with specific dimensions and of solid fill
	private GRect createPaddle() { 
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true); 
		return paddle;
		}
	
	//Defines the edges of the ball as feelers for whether it has come in contact with an object - based on any of the 4 following corners: top left top right, bottom right, bottom left
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();   
		if(getElementAt(x, y + 2*BALL_RADIUS) != null) {
			return getElementAt(x, y + 2*BALL_RADIUS);  
			}	
				else if(getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS) != null) {
					return getElementAt(x + 2*BALL_RADIUS, y); 
					
					} 	else if(getElementAt(x + 2*BALL_RADIUS, y) != null) {
							return getElementAt(x + 2*BALL_RADIUS, y);  
								
							}	else if(getElementAt(x, y) != null) {
									return getElementAt(x, y);			
									}  
									else {
										return null;
										} 
		}
	
	//Adds a column to 10 bricks to each of 10 rows and colors them 5 different colors with 2 rows of the same color
	private void addBricks() { 
		//Finds where to start adding first column of bricks so the collection of 10 bricks will be centered
				double halfWidthOfTotalBricks = NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)/2; 
				double centerScreenX = getWidth()/2;
				double firstColumn = centerScreenX - halfWidthOfTotalBricks;
				
				for(double col= 0; col< NBRICK_COLUMNS; col++) {
					
					for(double row = 0; row < NBRICK_ROWS; row++) { 
						
					double x = firstColumn + ((BRICK_WIDTH + BRICK_SEP)*col);
					double y = BRICK_Y_OFFSET + ((BRICK_HEIGHT +BRICK_SEP)*row); 
						if(row == 0 || row == 1) {
								layBricks(x, y, Color.RED);	
									} if(row == 2 || row == 3) {
										layBricks(x, y, Color.ORANGE);	
										}
											if(row == 4 || row == 5) {
												layBricks(x, y, Color.YELLOW); 
												}
													if(row == 6 || row == 7) {
														layBricks(x, y, Color.GREEN);
														}
															if(row >= NBRICK_ROWS - 2) {
																layBricks(x, y, Color.CYAN);
																} 
					}
				}
				}		
	
//Creates the bricks with defined width and height and color c, to later be placed in specific formation
	private void layBricks(double x, double y, Color c) {
		GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setFilled(true);
		rect.setColor(c);
		add(rect); 
		} 
	
} 
