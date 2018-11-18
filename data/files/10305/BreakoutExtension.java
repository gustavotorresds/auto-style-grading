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

public class BreakoutExtension extends GraphicsProgram {
	
	//Additional Functionality For Breakout Extension:
	// - Bounce Sound when ball hits paddle or brick
	// - Pause game at beginning, display instruction to click mouse and start when player clicks mouse
	// - Improve the user control over paddle bounces such that if ball hits near edge of paddle then ball's X velocity reverses direction
	// - Double the downward speed of ball after 7 paddle hits and again double after 14 paddle hits
	// - Keep Score with increasing weights for higher colored bricks

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
			((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP)) / NBRICK_COLUMNS);

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
	
	// Animation delay or pause time while Win or Lose Label is displayed before next game starts
	public static final double LABEL_DELAY = DELAY * 200;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Set Instance Variable Brick Color
	public Color BrickColor = Color.BLACK;
	
	// Set Instance Variable: Calculate Coordinates of UpperLeft Corner of Leftmost Brick In Row
	public double RowStartY = BRICK_Y_OFFSET;	
	public double RowStartX;

	// Instance Variable For Paddle
	GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	
	// Instance Variable For Brick Width That Will be set in run method using getWidth()
	double BrickWidth = 60;

	// Instance Variables To Track The Velocity Of The Ball
	private double vx;
	private double vy = 3;
	
	// Random Number Generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Instance Variable For Ball Coordinates
	private double BallLeftX;
	private double BallRightX;
	private double BallTopY;
	private double BallBottomY;
	
	// Instance Variable For Brick Countdown
	private int BrickCountdown;
	
	// Instance Variable For Each Round
	private int turn;
	
	// Instance Variable For Ball
	private GOval Ball = new GOval(BallLeftX, BallTopY, BALL_RADIUS*2, BALL_RADIUS*2);
	
	// Load the bounce sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	// Instance Variable For Starting Game
	private boolean Start = false;
	
	// Instance Variable For Paddle Hit Count
	private int PaddleHitCount;
	
	// Instance Variable For Player Score and Glabel Score
	private int PlayerScore = 0;
	private GLabel Score = new GLabel("Your Score Is " + PlayerScore + "!", 0,0);
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		
		// Set the canvas size.  
		setCanvasSize (getWidth(), getHeight());
		
		//Set Game To Allow Turns
		for (turn = 1; turn <= NTURNS; turn++) {
				// ==============================================================================================
				// Part I: Set Values of Core Run Variables:
			
					// Set value of Variable For Brick Width
						BrickWidth = Math.floor(((getWidth() - (NBRICK_COLUMNS + 1.0) * BRICK_SEP)) / NBRICK_COLUMNS);
					// Set value of X coordinate for first brick in Row
						RowStartX = Math.floor((getWidth()/2) - ((NBRICK_COLUMNS*BrickWidth)+((NBRICK_COLUMNS-1)*BRICK_SEP))/2);
					
					//Set BrickCountdown equal to total # of bricks
						BrickCountdown = NBRICK_COLUMNS * NBRICK_ROWS;
							
					// set Ball To Center Of Screem
						BallLeftX = getWidth()/2-BALL_RADIUS;
						BallRightX = BallLeftX + BALL_RADIUS*2;
						BallTopY = getHeight()/2-BALL_RADIUS;
						BallBottomY = BallTopY + BALL_RADIUS*2;
					
					//Set Speed and Angle of Ball Movement
						vx = rgen.nextDouble(1.0, 3.0);
						if (rgen.nextBoolean(0.5)) vx = -vx;
						
				// =====================================================================================================
				// PART II:  SET UP BRICKS ON SCREEN
					
					// Build And Display All Bricks
					// "for" loop defines the row number, uses private methods to get brick color and build row
						for (int RowNumber = 0; RowNumber < NBRICK_ROWS; RowNumber++) {
										RowStartY = BRICK_Y_OFFSET + (RowNumber * (BRICK_HEIGHT + BRICK_SEP));					
										BrickColor = getBrickColor(RowNumber);
										buildRow();
						}
									
				// =====================================================================================================
				// PART III:  CREATE and move PADDLE
						addMouseListeners();
						
				// =====================================================================================================
				// PART IV:  CREATE A BALL AND GET IT TO BOUNCE OFF THE WALLS
						Ball.setLocation(BallLeftX, BallTopY);
						Ball.setFilled(true);
						Ball.setColor(Color.BLACK);
						add(Ball);
						
					// Pause Until Mouse Is Clicked To Start Game
						printStart(turn);
						
					// Reset Paddle Hit Count
						PaddleHitCount = 0;
		
					// Create Animation
						while (true) {
								// Check Brick Counter to see if all bricks have been removed (game win)
									if (BrickCountdown == 0) {
										printWinner(turn);
										break;
									}
								update();
								pause(DELAY);
								// Reset Ball to next Location
									Ball.setLocation(BallLeftX, BallTopY);
								// If Ball paddle misses ball, take ball fully off the screen, remove remaining bricks and print Loser Label
									if (BallBottomY > getHeight()) {
										remove(Ball);
										removeBricks();
										printLoser(turn);
										break;
								}
						printScore();	
						}
			}
		}
		
	private void update() {
			// Bounce Off Walls and Off Ceiling
				if ((BallLeftX + vx < 0) || (BallRightX + vx > getWidth())) {
					vx = -vx;
				} 
				if (BallTopY + vy < 0) {
				vy = -vy;
				}
				
			// Reset Ball Coordinates To Move Ball
				BallLeftX = BallLeftX + vx;
				BallTopY = BallTopY + vy;	
				BallRightX = BallLeftX + BALL_RADIUS*2;
				BallBottomY = BallTopY + BALL_RADIUS*2;
			
			// Check whether hitting an object
			// Reset direction if hit
			// Remove brick if object hit is brick
			// Count bricks removed
				GObject collider = getCollidingObject();
				if (collider == null) {
				} else {
						if (collider == Score) {
						}	else {
						if (collider == paddle) {
							// Reverse Ball X Direction if hitting paddle on its vertical edge 
							double paddleX = collider.getX();
							if (((vx > 0) & (BallRightX - paddleX < 15)) || ((vx < 0) & (paddleX + PADDLE_WIDTH -BallLeftX < 15))) {
								vx = -vx;
							PaddleHitCount++;
							}
							BallTopY = BallTopY - 10;
							bounceClip.play();
						} else {
							// Reverse Ball X Direction if hitting brick on its vertical edge 
							double brickX = collider.getX();
							if (((vx > 0) & (BallRightX - brickX < 3)) || ((vx < 0) & (brickX + BRICK_WIDTH -BallLeftX < 3))) {
								vx = -vx;
							}
							bounceClip.play();
							Color RemovedBrickColor = collider.getColor();
							updatePlayerScore(RemovedBrickColor);
							remove (collider);
							BrickCountdown--;
							
						}
						if ((PaddleHitCount == 6) || (PaddleHitCount == 13)) {
							vy = vy * 2;
						}
						vy = -vy;
					}
				}
	}
	
	private void updatePlayerScore(Color removedBrickColor) {
			// Increase PlayerScore by amount based on color of brick removed
					if (removedBrickColor == Color.RED) {
						PlayerScore = PlayerScore + 10;
					} else {
						if (removedBrickColor == Color.ORANGE) {
							PlayerScore = PlayerScore + 8;
						} else { 
							if (removedBrickColor == Color.YELLOW) {
								PlayerScore = PlayerScore + 6;
							} else {
								if (removedBrickColor == Color.GREEN) {
									PlayerScore = PlayerScore + 4;
								} else {
									if (removedBrickColor == Color.CYAN) {
										PlayerScore = PlayerScore + 2;
									}
								}
							}
						}
					}
		
	}
	
	private GLabel printScore() {
		// Make and Print Label Displaying Updated Player Score
	 		Score.setLabel("Your Score Is " + PlayerScore + "!");
			Score.setFont(new Font("Serif", Font.BOLD, 20));
	 		double LabelWidth = Score.getWidth();
	 		double LabelHeight = Score.getAscent();
	 		Score.setLocation(60, getHeight() - PADDLE_Y_OFFSET/2 + LabelHeight/2);
	 		add(Score);
	 		return Score;
	} 		

	private void printStart(int turn) {
		// Make and Print Label Explaining Click To Start Game
	 		GLabel StartLabel = new GLabel("Click Mouse To Start Game" + turn + "!", 0,0);
	 		StartLabel.setFont(new Font("Serif", Font.BOLD, 20));
	 		double LabelWidth = StartLabel.getWidth();
	 		double LabelHeight = StartLabel.getAscent();
	 		StartLabel.setLocation(getWidth()/2 - LabelWidth/2, BRICK_Y_OFFSET/2 + LabelHeight/2);
	 		add(StartLabel);
	 		while (Start == false) {
	 			pause(DELAY);
			}
	 		remove(StartLabel);
	 		Start = false;
	} 		

	private void printWinner(int turn) {
			// Make and Print Label Proclaiming winner!
		 		GLabel Winner = new GLabel("You Won Round " + turn + "!", 0,0);
		 		Winner.setFont(new Font("Serif", Font.BOLD, 20));
		 		double LabelWidth = Winner.getWidth();
		 		double LabelHeight = Winner.getAscent();
		 		Winner.setLocation(getWidth()/2 - LabelWidth/2, getHeight()/2 + LabelHeight/2);
		 		add(Winner);
		 		if (turn < NTURNS) {
					printGetReady();
				} else {
					printGameOver();
				}
				remove(Winner);
	} 		
 	
	private void printLoser(int turn) {
	 		// Make and Print Label Giving Condolances For Loss
			// Print Get Ready if more turns and Game Over if Lost Last turn
		 	 	GLabel Loser = new GLabel("So Sorry You Lost Round "+ turn + "!", 0,0);
		 	 	Loser.setFont(new Font("Serif", Font.BOLD, 20));
		 	 	double LabelWidth = Loser.getWidth();
		 	 	double LabelHeight = Loser.getAscent();
		 	 	Loser.setLocation(getWidth()/2 - LabelWidth/2, getHeight()/2 + LabelHeight/2);
		 	 	add(Loser);
		 	 	if (turn < NTURNS) {
					printGetReady();
				} else {
					printGameOver();
				}
		 	 	remove(Loser);
	}	
 	
 	private void printGetReady() {
			// Make and Print Label Telling Player To Get Ready For Next Game
		 	 	GLabel GetReady = new GLabel("Get Ready For Next Game!", 0,0);
		 	 	GetReady.setFont(new Font("Serif", Font.BOLD, 20));
		 	 	double LabelWidth = GetReady.getWidth();
		 	 	double LabelHeight = GetReady.getAscent();
		 	 	GetReady.setLocation(getWidth()/2 - LabelWidth/2, getHeight()/2 + LabelHeight*3/2);
		 	 	add(GetReady);
		 	 	pause(LABEL_DELAY);
		 	 	remove(GetReady);
	}
 	
 	private void printGameOver() {
 			// Make and Print Label Telling Player To Get Ready For Next Game
		 	 	GLabel GameOver = new GLabel("Game Over!", 0,0);
		 	 	GameOver.setFont(new Font("Serif", Font.BOLD, 20));
		 	 	double LabelWidth = GameOver.getWidth();
		 	 	double LabelHeight = GameOver.getAscent();
		 	 	GameOver.setLocation(getWidth()/2 - LabelWidth/2, getHeight()/2 + LabelHeight*3/2);
		 	 	add(GameOver);
		 	 	pause(LABEL_DELAY);
		 	 	remove(GameOver);
	}

	private GObject getCollidingObject() {
			// may be a GObject, or null if nothing at (x, y)
				GObject maybeAnObject1 = getElementAt(BallLeftX, BallTopY);
				if (maybeAnObject1 != null) {
					return maybeAnObject1;
				} else {
						GObject maybeAnObject2 = getElementAt(BallLeftX, BallBottomY);
						if (maybeAnObject2 != null) {
							return maybeAnObject2;
						} else {
								GObject maybeAnObject3 = getElementAt(BallRightX, BallTopY);
								if (maybeAnObject3 != null) {
									return maybeAnObject3;
								} else { 
										GObject maybeAnObject4 = getElementAt(BallRightX, BallBottomY);
										if (maybeAnObject4 != null) {
											return maybeAnObject4;
										} else {
											return null;
										}
								}
						}
				}
	}
	
	private void buildRow() {
			// Build Row
				for (int BrickNumberInRow = 0; BrickNumberInRow < NBRICK_COLUMNS; BrickNumberInRow++) {
					double BrickX = RowStartX + (BrickNumberInRow*(BrickWidth + BRICK_SEP));
					GRect rect = new GRect(BrickX, RowStartY, BrickWidth, BRICK_HEIGHT);
					rect.setFilled(true);
					rect.setColor(BrickColor);
					add(rect);
				}	
	}
	
	private void removeBricks() {
			// Delete any remaining bricks in row
				for (int RowNumber = 0; RowNumber < NBRICK_ROWS; RowNumber++) {
					RowStartY = BRICK_Y_OFFSET + (RowNumber * (BRICK_HEIGHT + BRICK_SEP));
					for (int BrickNumberInRow = 0; BrickNumberInRow < NBRICK_COLUMNS; BrickNumberInRow++) {
						double BrickX = RowStartX + (BrickNumberInRow*(BrickWidth + BRICK_SEP));
						GObject maybeAnObject = getElementAt(BrickX, RowStartY);
						if (maybeAnObject != null) {
							remove(maybeAnObject);
						}
					}
				}
	}

	private Color getBrickColor(int rowNumber) {
			// Takes Row Number and Returns Row Color
				Color brickColor = Color.RED;
				if (rowNumber < 2) {
				} else {
					if (rowNumber < 4) {
						brickColor = Color.ORANGE;
					} else {
						if (rowNumber < 6) {
							brickColor = Color.YELLOW;
						} else {
							if (rowNumber < 8) {
								brickColor = Color.GREEN;
							} else { 
								brickColor = Color.CYAN;
							}	
						}
					}
				}
				return brickColor;
	}
	

	public void mouseMoved(MouseEvent e) {
		// Add Paddle To The Screen and Follow Mouse Movements
			double mouseX = e.getX();
			addPaddle(mouseX);
	}
	
	public void mousePressed(MouseEvent e) {
		// Start Game When Mouse Clicked
			Start = true;
	}
	
	private void addPaddle(double mouseX) {
		// Create paddle and keep within  left and right edge of screen
			paddle.setFilled(true);
			paddle.setColor(Color.BLACK);
			double x;
			if (mouseX >= PADDLE_WIDTH/2) {
				if (mouseX <= getWidth() - PADDLE_WIDTH/2) {
					x = mouseX - PADDLE_WIDTH/2;
				} else {
					x = getWidth() - PADDLE_WIDTH;
				}
			} else {
				x = 0;
			}
			double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
			// paddle.setLocation(x, y);
			add(paddle, x, y);
	}

}

