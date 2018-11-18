/*
 * File: Breakout.java
 * -------------------
 * Name: Thomas Thach
 * Section Leader: Kaitlyn Lagattuta
 * 
 * This program encapsulates a chapter in the epic saga of Walter White, 
 * high school chemistry teacher turned mastermind meth lord. 
 * Follow this game of Breakout as we chronicle a tale of triumph,
 * tragedy, and utter exhilaration. Do you have what it takes to be the best?
 * Or will you fall like all the rest? 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 700; //originally 420
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10; //originally 10

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 5; //originally 10 but 5 is also a good number

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 16; //originally 8

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 20; //originally 10

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 70;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static double VELOCITY_Y = 5.0; //originally 3.0

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 5;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	GRect paddle = null; 
	GOval ball = null; 
	GRect brick; 
	
	private GLabel gameOver = new GLabel("You got caught! GAME OVER!");
	private int deaths = 0; 
	
	private GLabel youWin = new GLabel("YOU WIN!");
	private int brickBreaks = 0; 
	
	private GLabel clickToStart = new GLabel("Click anywhere to start the game!");
	private GLabel speedometer = new GLabel(""); 
	
	//All these labels go into the instruction screen. 
	GLabel line1 = new GLabel("Walt & Jesse need your help to build their Blue Sky empire!");
	GLabel line2 = new GLabel("You have 5 lives, but don't get caught--or it's game over!");
	GLabel powerupLabel = new GLabel("Powerups");
	GLabel SaulLine = new GLabel("SAUL: Even drug dealers need lawyers, right? Increases paddle size!");
	GLabel GusLine = new GLabel("GUS: Can you keep up with the kingpin's pace? Increases ball speed!");
	GLabel HankLine = new GLabel("HANK: Watch out for the DEA! Decreases paddle size!");
	GLabel MikeLine = new GLabel("MIKE: Count on Mike the Fixer to save the day! Restores a life!");
	GLabel WalterLine = new GLabel("WALT: Restore some order in the lab! Decreases ball speed!");
	GLabel JesseLine = new GLabel("JESSE: Your volatile partner in crime... Better hope for the best!");
	GLabel line3 = new GLabel("Dying to throw in some spice? Try clicking to give your cooking a little 'kick'!");
	GLabel line4 = new GLabel("Or use the side of the paddle to dial it up a notch!");
	GLabel line5 = new GLabel("Just be warned--you might not be able to handle the heat!"); 
	
	private double vx; 
	private double vy = VELOCITY_Y; 
	
	double paddleWidth = PADDLE_WIDTH; 
	
	private RandomGenerator rgen = RandomGenerator.getInstance(); 
	
	//All these images have to be imported to be implemented into the game! 
	GImage WalterWhite = new GImage("WalterWhiteDuckface.jpg");
	GImage BreakoutBad = new GImage("BreakoutBad.png");
	GImage InstructionsBackground = new GImage("InstructionsBackground.jpg");
	GImage CookingTime = new GImage("CookingTime.jpg");
	GImage Caught = new GImage("Caught.jpg");
	GImage Arrested = new GImage("Arrested.jpg");
	GImage ThatsAll = new GImage("ThatsAll.jpg");
	
	GImage Saul = new GImage("BetterCallSaul.jpg");
	double SaulHeight = Saul.getHeight();
	
	GImage Gus = new GImage("GusFring.jpg");
	GImage Hank = new GImage("Hank.jpg");
	GImage Mike = new GImage("Mike.jpg");
	GImage Jesse = new GImage("Jesse.png");
	GImage Walter = new GImage("Walter.jpg");
	GImage Skyler = new GImage("Skyler.jpg");
	
	//All these booleans exist so that the program can identify when 
	//certain power-ups are present so that multiple power-ups don't 
	//flood the screen simultaneously. 
	private boolean powerupPresent = false; 
	private boolean youWinPresent = false; 
	private boolean SaulPresent = false;
	private boolean GusPresent = false; 
	private boolean HankPresent = false; 
	private boolean MikePresent = false; 
	private boolean WalterPresent = false;
	private boolean JessePresent = false; 
	
	private boolean Instructions = true; 
	private boolean readyToStart = false; 

	//These are all the sound clips that were imported to construct
	//this digital Breaking Bad universe! 
	SoundClip Intro = new SoundClip("BreakingBadIntro.mp3");
	SoundClip Theme = new SoundClip("BreakingBad Theme.mp3");
	SoundClip Tamacun = new SoundClip("Tamacun.mp3");
	SoundClip WaltScream = new SoundClip("WaltScream.mp3"); 
	SoundClip Lawyer = new SoundClip("Lawyer.mp3");
	SoundClip HornetsNest = new SoundClip("HornetsNest.mp3");
	SoundClip AManProvides = new SoundClip("AManProvides.mp3");
	SoundClip HangTough = new SoundClip("HangTough.mp3");
	SoundClip HesOurGuy = new SoundClip("HesOurGuy.mp3");
	SoundClip WhoKnocks = new SoundClip("WhoKnocks.mp3");
	SoundClip YeahScience = new SoundClip("YeahScience.mp3");
	SoundClip ItWasYou = new SoundClip("ItWasYou.mp3");
	SoundClip MirandaRights = new SoundClip("MirandaRights.mp3");
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//This sets the dimensions for a lot of the backgrounds
		//that take up the entire screen.
		CookingTime.setBounds(0, 0, getWidth(), getHeight());
		
		WalterWhite.setBounds(0, 0, getWidth(), getHeight());
		
		BreakoutBad.setBounds(0, 0, getWidth(), getHeight());
		add(BreakoutBad);
		
		Caught.setBounds(0, 0, getWidth(), getHeight());
		Arrested.setBounds(0, 0, getWidth(), getHeight());
		ThatsAll.setBounds(0, 0, getWidth(), getHeight());
		 
		paddle = new GRect(0, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT); 
		
		Intro.play(); 
		openingScreen();
		addMouseListeners();
		
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx; 
		
		//This animates the ball and all the power-ups in the game. 
		while (true) {
			moveBall();
			pause(DELAY);
			if (powerupPresent == true) {
				movePowerup();
			}
		}
	}
	
	//This generates the opening text that prompts the user to click on the screen to start!
	private void openingScreen() { 
		clickToStart.setFont("Century Gothic-16");
		clickToStart.setColor(Color.GREEN);
		double clickToStartWidth = clickToStart.getWidth();
		add(clickToStart, (getWidth() - clickToStartWidth)/2, getHeight()/8); 
	}
	//This method constructs a pile of "product" at the top of the screen depending 
	//on the number of rows and columns specified. It also colors the bricks to form 
	//Walter White's renowned 99.1% Blue Sky!
	
	private void buildBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				double x = BRICK_WIDTH*j + BRICK_SEP*j;
				double y = BRICK_Y_OFFSET + BRICK_HEIGHT*i + BRICK_SEP*i;
				double margin = (getWidth() - (BRICK_WIDTH*NBRICK_COLUMNS + (BRICK_SEP*NBRICK_COLUMNS - BRICK_SEP)))/2;
				brick = new GRect(x + margin, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (i%10 == 0 || i%10 == 3 || i%10 == 6 || i%10 == 9) {
					brick.setColor(Color.CYAN);
				} else if (i%10 == 1 || i%10 == 4 || i%10 == 7) {
					brick.setColor(Color.CYAN);
				} else if (i%10 == 2 || i%10 == 5 || i%10 == 8) {
					brick.setColor(Color.CYAN);
				} 
				add(brick);
			}
		}
	}
	
	//This mouse event is what controls the paddle. 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX(); 
		double mouseY = e.getY();
		if (mouseX >= PADDLE_WIDTH/2 && mouseX <= getWidth()-PADDLE_WIDTH/2) {
			paddle.setLocation(mouseX-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET); 
			paddle.setFilled(true);
			paddle.setColor(Color.WHITE);
			add(paddle);
		}
	}
	
	//This method allows the user to move from the initial screen to the 
	//instructions screen to the gameplay screen, as well as initiate 
	//the creation of a ball if one is not present. 
	public void mouseClicked(MouseEvent e) { 
		if (Instructions == true) {
			remove(clickToStart);
			remove(BreakoutBad);
			InstructionsScreen();
			Instructions = false;
		} else if (powerupLabel != null && ball == null && deaths == 0) {
			removeInstructions(); 
			readyToStart = true; 
			add(CookingTime);
			buildBricks(); 
			Intro.stop(); 
			Theme.stop(); 
			Theme.play();
		}
		if (ball == null && readyToStart == true) {
			double xBall = getWidth()/2 - BALL_RADIUS; 
			double yBall = getHeight()/2 - BALL_RADIUS;
			double ballDiameter = BALL_RADIUS * 2; 
			ball = new GOval(xBall, yBall, ballDiameter, ballDiameter); 
			ball.setFilled(true);
			ball.setColor(Color.PINK);
			add(ball); 
		}
	}
	
	//This method generates all of the instructions for the user 
	//to understand how this whole operation works! 
	private void InstructionsScreen() { 
		add(InstructionsBackground);
		InstructionsBackground.setBounds(0,0,getWidth(),getHeight());
		
		add(line1);
		line1.setFont("Century Gothic-24"); 
		line1.setColor(Color.CYAN);
		double line1Width = line1.getWidth();
		add(line1, (getWidth() - line1Width)/2, getHeight()/15);
		
		add(line2);
		line2.setFont("Century Gothic-22"); 
		line2.setColor(Color.RED);
		double line2Width = line2.getWidth();
		add(line2, (getWidth() - line2Width)/2, getHeight()/8);
		
		add(powerupLabel);
		powerupLabel.setFont("Century Gothic-34"); 
		powerupLabel.setColor(Color.WHITE);
		double powerupLabelWidth = powerupLabel.getWidth();
		add(powerupLabel, (getWidth() - powerupLabelWidth)/2, getHeight()/4.8);
		
		add(SaulLine);
		SaulLine.setFont("Century Gothic-14"); 
		SaulLine.setColor(Color.CYAN);
		add(SaulLine, BRICK_WIDTH*2.5, BRICK_WIDTH*2.5);
		
		add(GusLine);
		GusLine.setFont("Century Gothic-14"); 
		GusLine.setColor(Color.RED);
		add(GusLine, BRICK_WIDTH, BRICK_WIDTH*3.5);
		
		add(HankLine);
		HankLine.setFont("Century Gothic-14"); 
		HankLine.setColor(Color.CYAN);
		add(HankLine, BRICK_WIDTH*2.5, BRICK_WIDTH*4.5);
		
		add(MikeLine);
		MikeLine.setFont("Century Gothic-14"); 
		MikeLine.setColor(Color.GREEN);
		add(MikeLine, BRICK_WIDTH, BRICK_WIDTH*5.5);
		
		add(WalterLine);
		WalterLine.setFont("Century Gothic-14"); 
		WalterLine.setColor(Color.CYAN);
		add(WalterLine, BRICK_WIDTH*2.5, BRICK_WIDTH*6.5);
		
		add(JesseLine);
		JesseLine.setFont("Century Gothic-14"); 
		JesseLine.setColor(Color.GREEN);
		add(JesseLine, BRICK_WIDTH, BRICK_WIDTH*7.5);
		
		add(line3);
		line3.setFont("Century Gothic-16"); 
		line3.setColor(Color.MAGENTA);
		double line3Width = line3.getWidth();
		add(line3, (getWidth() - line3Width)/2, getHeight()/1.12);
		
		add(line4);
		line4.setFont("Century Gothic-16"); 
		line4.setColor(Color.ORANGE);
		double line4Width = line4.getWidth();
		add(line4, (getWidth() - line4Width)/2, getHeight()/1.07);
		
		add(line5);
		line5.setFont("Century Gothic-16"); 
		line5.setColor(Color.RED);
		double line5Width = line5.getWidth();
		add(line5, (getWidth() - line5Width)/2, getHeight()/1.03);
		
		add(Saul);
		Saul.setBounds(BRICK_WIDTH,BRICK_WIDTH*2,BRICK_WIDTH, BRICK_WIDTH);
		
		add(Gus);
		Gus.setBounds(getWidth()-BRICK_WIDTH*2,BRICK_WIDTH*3,BRICK_WIDTH, BRICK_WIDTH);
		
		add(Hank);
		Hank.setBounds(BRICK_WIDTH,BRICK_WIDTH*4,BRICK_WIDTH, BRICK_WIDTH);
		
		add(Mike);
		Mike.setBounds(getWidth()-BRICK_WIDTH*2,BRICK_WIDTH*5,BRICK_WIDTH, BRICK_WIDTH);
		
		add(Walter);
		Walter.setBounds(BRICK_WIDTH,BRICK_WIDTH*6,BRICK_WIDTH, BRICK_WIDTH);
		
		add(Jesse);
		Jesse.setBounds(getWidth()-BRICK_WIDTH*2,BRICK_WIDTH*7,BRICK_WIDTH, BRICK_WIDTH);
	}
	
	//This method removes all of the instructions from the screen so that the gameplay
	//can start afterward.
	private void removeInstructions() {
		remove(InstructionsBackground);
		remove(line1);
		remove(line2);
		remove(line3);
		remove(line4);
		remove(line5);
		remove(powerupLabel);
		remove(SaulLine);
		remove(GusLine);
		remove(HankLine);
		remove(MikeLine);
		remove(WalterLine);
		remove(JesseLine);
		
		remove(Saul);
		remove(Gus);
		remove(Hank);
		remove(Mike);
		remove(Walter);
		remove(Jesse);
	}
	
	//This moves the ball and ensures that the ball only removes bricks when it collides with them.
	//It also handles the generation of random power-ups when some bricks are removed.
	private void moveBall() { 
		if (ball != null) {
			ball.move (vx, vy);
			speedometer(); 
			GObject ballCollision = getCollidingObject();
			if (ballCollision != null) {
				if (ballCollision != paddle && ballCollision != WalterWhite && ballCollision != BreakoutBad && ballCollision != youWin && ballCollision != gameOver && ballCollision != CookingTime && ballCollision != Saul && ballCollision != Gus && ballCollision != Hank && ballCollision != Mike && ballCollision != Skyler && ballCollision != Jesse && ballCollision != Walter && ballCollision != Saul && ballCollision != Caught && ballCollision != Arrested && ballCollision != ThatsAll) {
					vy = -vy;
					SoundClip Kapow = new SoundClip("Gunshot.wav");
					Kapow.setVolume(0.1); 
					Kapow.play(); 
				}
				else if (ballCollision == paddle) {
					if (vy > 0) {
						vy = -vy; 
					} else {
						vy = -vy;
						vx = -vx * 1.25; 
					}
				} 
				if (ballCollision != paddle && ballCollision != WalterWhite && ballCollision != BreakoutBad && ballCollision != youWin && ballCollision != gameOver && ballCollision != CookingTime && ballCollision != Saul && ballCollision !=Gus && ballCollision != Hank && ballCollision != Mike && ballCollision != Skyler && ballCollision != Jesse && ballCollision != Walter && ballCollision != Saul && ballCollision != Caught && ballCollision != Arrested && ballCollision != ThatsAll) {
					remove(ballCollision);
					int Powerup = rgen.nextInt(12);
					if (Powerup == 1) {
						if (!powerupPresent) {
							Saul.setBounds(5,5,BRICK_WIDTH, BRICK_WIDTH);
							powerupPresent = true;
							SaulPresent = true; 
							add(Saul);
							}
					} else if (Powerup == 2 || Powerup == 3) {
						if (!powerupPresent) {
							Gus.setBounds(BRICK_WIDTH*2,5,BRICK_WIDTH, BRICK_WIDTH);
							powerupPresent = true;
							GusPresent = true; 
							add(Gus); 
							}
					} else if (Powerup == 4 || Powerup == 5) {
						if (!powerupPresent) {
							Hank.setBounds(BRICK_WIDTH*3,5,BRICK_WIDTH, BRICK_WIDTH);
							powerupPresent = true;
							HankPresent = true; 
							add(Hank); 
							}
					} else if (Powerup == 6) {
						if (!powerupPresent) {
							Mike.setBounds(BRICK_WIDTH*4,5,BRICK_WIDTH, BRICK_WIDTH);
							powerupPresent = true;
							MikePresent = true; 
							add(Mike); 
							}
					}  else if (Powerup == 7) {
						if (!powerupPresent) {
							Walter.setBounds(BRICK_WIDTH*5,5,BRICK_WIDTH, BRICK_WIDTH);
							powerupPresent = true;
							WalterPresent = true; 
							add(Walter); 
							} 
					}  else if (Powerup == 8 || Powerup == 9) {
						if (!powerupPresent) {
							Jesse.setBounds(BRICK_WIDTH*6,5,BRICK_WIDTH, BRICK_WIDTH);
							powerupPresent = true;
							JessePresent = true; 
							add(Jesse); 
							}
					}
					brickCounter(); 
				}
			}	
			if (ball.getY() <= 0) {
				vy = -vy; 	
			}
			if (ball.getX() >= getWidth() - BALL_RADIUS*2 || ball.getX() <= 0) {
				vx = -vx;
			}
		}
		
		if (ball != null) {
			if (ball.getY() >= getHeight() - BALL_RADIUS*2) {
				remove(ball); 
				ball = null;
				deathCounter();
				AManProvides.stop();
				HangTough.stop();
				HesOurGuy.stop();
				Lawyer.stop();
				WhoKnocks.stop();
				YeahScience.stop(); 
				HornetsNest.stop();
				HornetsNest.play();
			} 
		}
	}
	
	//This handles how the power-ups move down the screen, as well as what happens
	//when the player successfully collects the power-up. It also removes the power-up
	//if the player fails to collect them.
	public void movePowerup() {
		if (SaulPresent == true) {
			Saul.move (0, VELOCITY_Y);
			if (Saul.getY() >= getHeight()) {
				remove(Saul); 
				powerupPresent = false; 
				SaulPresent = false; 
			}
			if (SaulColliding()) {
				paddleWidth = paddleWidth * 1.25; 
				paddle.setSize(paddleWidth, PADDLE_HEIGHT);
				remove(Saul);
				powerupPresent = false;
				SaulPresent = false; 
				Lawyer.play(); 
			}
		} else if (GusPresent == true) {
			Gus.move (0, VELOCITY_Y);
			if (Gus.getY() >= getHeight()) {
				remove(Gus); 
				powerupPresent = false; 
				GusPresent = false; 
			}
			if (GusColliding()) {
				vx = vx*1.25;
				vy = vy*1.25; 
				remove(Gus);
				powerupPresent = false;
				GusPresent = false; 
				AManProvides.setVolume(3);
				AManProvides.play();
			}
		} else if (HankPresent == true) {
			Hank.move (0, VELOCITY_Y);
			if (Hank.getY() >= getHeight()) {
				remove(Hank); 
				powerupPresent = false; 
				HankPresent = false; 
			}
			
			if (HankColliding()) {
				paddleWidth = paddleWidth * 0.5; 
				paddle.setSize(paddleWidth, PADDLE_HEIGHT);
				remove(Hank);
				powerupPresent = false;
				HankPresent = false; 
				HesOurGuy.setVolume(2.5);
				HesOurGuy.play();
			}
		} else if (MikePresent == true) {
			Mike.move (0, VELOCITY_Y);
			if (Mike.getY() >= getHeight()) {
				remove(Mike); 
				powerupPresent = false; 
				MikePresent = false; 
			}
			if (MikeColliding()) {
				if (deaths > 0) {
					deaths--; 
				}
				remove(Mike);
				powerupPresent = false;
				MikePresent = false; 
				HangTough.play();
			}
		}  else if (WalterPresent == true) {
			Walter.move (0, VELOCITY_Y);
			if (Walter.getY() >= getHeight()) {
				remove(Walter); 
				powerupPresent = false; 
				WalterPresent = false; 
			}
			
			if (WalterColliding()) {
				vx = vx * 0.75;
				vy = vy * 0.75;
				remove(Walter);
				powerupPresent = false;
				WalterPresent = false; 
				WhoKnocks.play(); 
			}
		} else if (JessePresent == true) {
			Jesse.move (0, VELOCITY_Y);
			if (Jesse.getY() >= getHeight()) {
				remove(Jesse); 
				powerupPresent = false; 
				JessePresent = false; 
			}
			
			if (JesseColliding()) {
				int science = rgen.nextInt(8);
				if (science == 0 || science == 1) {
					paddleWidth = paddleWidth * 1.5; 
					paddle.setSize(paddleWidth, PADDLE_HEIGHT);
				} else if (science == 2) {
					paddleWidth = paddleWidth * 0.75; 
					paddle.setSize(paddleWidth, PADDLE_HEIGHT);
				} else if (science == 3) {
					vx = vx * -1.5; 
				} else if (science == 4 || science == 5) {
					vy = vy * 0.75; 
				} else {
					vx = vx * 1.25;
					vy = vy * 1.5; 
					paddleWidth = PADDLE_WIDTH; 
					paddle.setSize(paddleWidth, PADDLE_HEIGHT);
				}
				remove(Jesse);
				powerupPresent = false;
				JessePresent = false; 
				YeahScience.play(); 
			}
		}
	}	
	
	//These booleans are used to detect if the paddle has actually made "contact"
	//with the power-ups!
	private boolean SaulColliding() { 
		if (Saul.getY() > (getHeight()-PADDLE_Y_OFFSET -BRICK_WIDTH) && paddle.getX() < (BRICK_WIDTH + 5)) {
			return true; 
		}
		return false; 
	}
	
	private boolean GusColliding() { 
		if (Gus.getY() > (getHeight()-PADDLE_Y_OFFSET -BRICK_WIDTH) && paddle.getX() > (BRICK_WIDTH + 5) && paddle.getX() < (BRICK_WIDTH*2 + 5 + PADDLE_WIDTH)){
			return true; 
		}
		return false; 
	}
	
	private boolean HankColliding() { 
		if (Hank.getY() > (getHeight()-PADDLE_Y_OFFSET -BRICK_WIDTH) && paddle.getX() > (BRICK_WIDTH*2 + 5) && paddle.getX() < (BRICK_WIDTH*3 + 5 + PADDLE_WIDTH)){
			return true; 
		}
		return false; 
	}
	
	private boolean MikeColliding() { 
		if (Mike.getY() > (getHeight()-PADDLE_Y_OFFSET -BRICK_WIDTH) && paddle.getX() > (BRICK_WIDTH*3 + 5) && paddle.getX() < (BRICK_WIDTH*4 + 5 + PADDLE_WIDTH)){
			return true; 
		}
		return false; 
	}
	
	private boolean WalterColliding() { 
		if (Walter.getY() > (getHeight()-PADDLE_Y_OFFSET -BRICK_WIDTH) && paddle.getX() > (BRICK_WIDTH*4 + 5) && paddle.getX() < (BRICK_WIDTH*5 + 5 + PADDLE_WIDTH)){
			return true; 
		}
		return false; 
	}
	
	private boolean JesseColliding() { 
		if (Jesse.getY() > (getHeight()-PADDLE_Y_OFFSET -BRICK_WIDTH) && paddle.getX() > (BRICK_WIDTH*5 + 5) && paddle.getX() < (BRICK_WIDTH*6 + 5 + PADDLE_WIDTH)){
			return true; 
		}
		return false; 
	}
	
	public void mousePressed(MouseEvent f) {
		if (ball != null) {
			if (vy <= VELOCITY_Y*2.5) {
			vy = vy + vy * 0.1;
			} else {
				vy = VELOCITY_Y;
			}
		}
	}
	
	//This speedometer might come in handy with letting the user know how fast the ball 
	//is currently moving in the vertical direction. It might help prevent some unexpected
	//surprises!
	private void speedometer() {
		speedometer.setFont("Courier-12");
		speedometer.setColor(Color.BLACK);
		double speedometerWidth = speedometer.getWidth();
		double speedometerHeight = speedometer.getHeight();
		speedometer.setLabel("Current vertical speed: " + vy);
		speedometer.setColor(Color.WHITE);
		add(speedometer, (getWidth() - speedometerWidth)/2, getHeight() - speedometerHeight/2); 
	}
	
	//This makes sure that the ball can register collisions in all four corners.
	private GObject getCollidingObject() {
		GObject ballCollision = getElementAt(ball.getX(), ball.getY()); 
		if (ballCollision != null) {
			return ballCollision;
		} 
		ballCollision = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		if (ballCollision != null) {
			return ballCollision;
		} 
		ballCollision = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		if (ballCollision != null) {
			return ballCollision;
		} 
		ballCollision = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		if (ballCollision != null) {
			return ballCollision;
		} 
		return ballCollision; 
	}
	
	//This tracks the number of times that the user has lost a life, and it initiates the proper 
	//speed-ups as the user progresses thru the game. 
	private void deathCounter() {
		deaths++;
		if (deaths >= NTURNS && youWinPresent == false) { 
			gameOver.setFont("Century Gothic-30");
			gameOver.setColor(Color.RED);
			double gameOverWidth = gameOver.getWidth();
			remove(CookingTime);
			Theme.stop();
			AManProvides.stop();
			HangTough.stop();
			HesOurGuy.stop();
			Lawyer.stop();
			WhoKnocks.stop();
			YeahScience.stop(); 
			HornetsNest.stop();
			Tamacun.stop();
			int whichDeath = rgen.nextInt(2);
			if (whichDeath == 0) {
				MirandaRights.stop(); 
				add(Caught); 
				add(gameOver, (getWidth() - gameOverWidth)/2, getHeight()/1.35);
				ItWasYou.rewind(); 
				ItWasYou.play(); 
			} else if (whichDeath == 1) {
				ItWasYou.stop(); 
				add(Arrested);
				add(gameOver, (getWidth() - gameOverWidth)/2, getHeight()/1.35);
				MirandaRights.rewind(); 
				MirandaRights.play();
			}
		}
	}
	//Tracks what effects occur as the user creates product! 
	private void brickCounter() {
		brickBreaks++;
		if (brickBreaks == (NBRICK_COLUMNS * NBRICK_ROWS)/10) {
			vx = vx * 1.25; 
		}
		if (brickBreaks == (NBRICK_COLUMNS * NBRICK_ROWS)/5) {
			vy = vy * 1.25; 
		}
		if (brickBreaks == (NBRICK_COLUMNS * NBRICK_ROWS)/2) {
			Theme.stop();
			AManProvides.stop();
			HangTough.stop();
			HesOurGuy.stop();
			Lawyer.stop();
			WhoKnocks.stop();
			YeahScience.stop(); 
			HornetsNest.stop();
			Tamacun.play();
			vx = -vx * 1.5; 
			vy = vy *  1.5; 
		}
		if (brickBreaks == NBRICK_COLUMNS * NBRICK_ROWS) {
			victory();
		}
	}
	//What happens when the user wins!!! 
	private void victory() {
		youWin.setFont("Courier-48");
		youWin.setColor(Color.BLUE);
		double gameOverWidth = youWin.getWidth();
		remove(CookingTime);
		remove(ball);
		AManProvides.stop();
		HangTough.stop();
		HesOurGuy.stop();
		Lawyer.stop();
		WhoKnocks.stop();
		YeahScience.stop(); 
		HornetsNest.stop();
		Theme.stop(); 
		Tamacun.stop(); 
		int whichWin = rgen.nextInt(2);
		if (whichWin == 0) {
			add(WalterWhite);
			add(youWin, (getWidth() - gameOverWidth)/2, getHeight()/1.95);
			youWinPresent = true;
			AudioClip Heisenberg = MediaTools.loadAudioClip("HEISENBERG - Say My Name.mp3");
			Heisenberg.play();
		} else if (whichWin == 1) {
			add(ThatsAll);
			youWin.setColor(Color.WHITE);
			add(youWin, (getWidth() - gameOverWidth)/2, getHeight()/1.2);
			youWinPresent = true;
			AudioClip Heisenberg = MediaTools.loadAudioClip("HEISENBERG - Say My Name.mp3");
			Heisenberg.play();
		}
	}
}
