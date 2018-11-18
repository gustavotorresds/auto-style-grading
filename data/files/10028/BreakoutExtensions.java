/*
 * File: Breakout.java
 * -------------------
 * Name: Eric Brandon Kam
 * Section Leader: Avery Wang
 * 
 * This file will eventually implement the game of Breakout. Breakout is essentially the
 * arcade game "Brick Breaker." The player gets three lives to destroy all the bricks on the
 * screen by bouncing the ball off of the paddle. If the ball contacts the wall under the paddle,
 * the player loses a life and the ball resets. This program makes heavy use of parameters.
 * Smoothest gameplay with background disabled.
 * 
 * This version of Breakout is equipped with power-ups, visual aids, and other challenges.
 * Hitting a red brick will enable the ball to be in "fire ball" mode, completely disrespecting
 * the walls of bricks. Hitting a blue brick will enable the ball to be in "homing ball" mode,
 * tethering itself to the paddle for five paddle bounces. Fire is contagious, and yes, homing
 * balls can catch "on fire," but only by hitting a red brick. Paddle width decreases by 1% of
 * the original width every time a brick is destroyed (until the paddle is at fifty percent of
 * its original width), and screen delay is multiplied by 0.95x every time a ball hits the paddle.
 * Player score is displayed at the bottom of the screen. Bricks are worth ten points each,
 * but if the player hits subsequent bricks without letting the primary ball hit the bottom of
 * the screen, then every subsequent brick is worth 1.05x the points the previous brick was worth.
 * 
 * Many other distinct power-ups exist:
 * 
 * Extra Life - grants the player an extra life
 * Money Ball - adds another ball to the screen; this ball will not count against the
 * player's life if it collides with the bottom of the screen
 * Shooters Shoot - the game pauses, and the player is able to shoot a single-use ball
 * Mega Paddle - the paddle width multiplies by 2.0x (from the width it is currently at)
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class BreakoutExtensions extends GraphicsProgram {
	
	// given constants
	public static final double CANVAS_WIDTH = 600;
	public static final double CANVAS_HEIGHT = 600;
	
	public static final int NBRICK_COLUMNS = 10;
	public static final int NBRICK_ROWS = 10;
	public static final double BRICK_SEP = 4;
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);
	public static final double BRICK_HEIGHT = 8;
	public static final double BRICK_Y_OFFSET = 70;
	
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;
	public static final double PADDLE_Y_OFFSET = 30;
	
	public static final double BALL_RADIUS = 10;
	public static final double VELOCITY_Y = 2.0;  // decreased vy for increased collision sensitivity
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 2.0;
	
	public static final double DELAY = 1000.0 / 150.0;  // decreased delay for increased frame rate
	public static final int NTURNS = 3;
	
	// added constants
	private static final String BACKGROUND_AVERY = "backgroundavery.jpg";
	private static final String BACKGROUND_CHRIS = "backgroundchris.jpg";
	private static final String BACKGROUND_TREX = "backgroundtrex.png";
	private static final String EXTRA_LIFE = "extralife.png";
	private static final String MONEY_BALL = "bigballerbrand.png";
	private static final String SHOOTERS_SHOOT = "magnums.jpg";
	private static final String MEGA_PADDLE = "oars.png";
	private static final String BIG_FONT = "Courier-56";
	private static final String SMALL_FONT = "Courier-16";
	private static final String MESSAGE_IF_WIN = "YOU WIN!!!";
	private static final String MESSAGE_IF_LOSE = "GAME OVER";
	public static final double VELOCITY_Y_BACKGROUND = 0.5;
	public static final double VELOCITY_X_BACKGROUND = 0.5;

	// added instance variables
	private GRect paddle = null;
	private GOval ball1 = null;
	private GOval ball2 = null;
	private GOval ball3 = null;
	private GOval ball4 = null;
	private GOval ball5 = null;
	private GOval homingBall1  = null;
	private GOval homingBall2  = null;
	private GImage avery = null;
	private GImage chris = null;
	private GImage trex = null;
	private GImage extraLifeLeft = null;
	private GImage extraLifeRight = null;
	private GImage moneyBallTop = null;
	private GImage moneyBallMiddle = null;
	private GImage moneyBallBottom = null;
	private GImage shootersShoot = null;
	private GImage megaPaddle = null;
	private GLabel clickToRelease = null;
	private GLabel clickToShoot = null;
	private GLabel bricksRemainingLabel = null;
	private GLabel turnsRemainingLabel = null;
	private GLabel scoreLabel = null;
	private GLabel multiplierLabel = null;
	private int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;
	private int turnsRemaining = NTURNS;
	private int homingBallsRemaining = 2;
	private int homing1Counter = 0;
	private int homing2Counter = 0;
	private int numFireBricks = 0;
	private int numHomingBricks = 0;
	private double vx1, vy1;
	private double vx2, vy2;
	private double vx3, vy3;
	private double vx4, vy4;
	private double vx5, vy5;
	private double vxc, vyc;
	private double vxa, vya;
	private double vxt, vyt;
	private double newDelay = DELAY;
	private double newPaddleWidth = PADDLE_WIDTH;
	private double score = 0.00;
	private double comboMultiplier = 1.00;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private DecimalFormat scoreFormat = new DecimalFormat("#");
	private DecimalFormat comboFormat = new DecimalFormat("#.00");
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private AudioClip applauseClip = MediaTools.loadAudioClip("applause.mp3");
	private AudioClip nsmb_fireballClip = MediaTools.loadAudioClip("nsmb_fireball.wav");
	private AudioClip bombClip = MediaTools.loadAudioClip("bomb.mp3");
	private AudioClip trexroarClip = MediaTools.loadAudioClip("trexroar.wav");
	private AudioClip nsmb_coinClip = MediaTools.loadAudioClip("nsmb_coin.wav");
	private AudioClip rubbleimpactClip = MediaTools.loadAudioClip("rubbleimpact.mp3");
	private AudioClip boulderimpactClip = MediaTools.loadAudioClip("boulderimpact.mp3");
	private AudioClip guncockClip = MediaTools.loadAudioClip("guncock.mp3");
	private AudioClip gunshootClip = MediaTools.loadAudioClip("gunshoot.mp3");
	private AudioClip fireballsoundClip = MediaTools.loadAudioClip("fireballsound.wav");
	private AudioClip thunderClip = MediaTools.loadAudioClip("thunder.wav");
	private AudioClip lightningClip = MediaTools.loadAudioClip("lightning.wav");
	private AudioClip nsmb_powerupClip = MediaTools.loadAudioClip("nsmb_power-up.wav");
	
	/* The game will set up all the graphics on the screen and then proceed to run an animation
	 * loop until the game is over: Either the player loses and runs out of lives or wins and
	 * breaks every brick. A final message is displayed according to the outcome.
	 * 
	 * Balls 2 and 3 are present in the case the primary ball collides with a "Money Ball" power-up.
	 * Animation is needed for these second and third balls on the screen, as all balls can be
	 * moving simultaneously.
	 */
	public void run() {
		setUp();
		while (!gameOver()) {
			moveBall(ball1);
			moveBall(ball2);
			moveBall(ball3);
			moveBall(ball4);
			moveBall(ball5);
			moveBackground(chris);
			moveBackground(avery);
			moveBackground(trex);
			checkForCollisions(ball1, vx1, vy1);
			checkForCollisions(ball2, vx2, vy2);
			checkForCollisions(ball3, vx3, vy3);
			checkForCollisions(ball4, vx4, vy4);
			checkForCollisions(ball5, vx5, vy5);
			pause(newDelay);
		}
		endResult();
	}

	private void setUp() {
		setTitle("CS 106A Breakout Extensions");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addBackground();
		addPaddle();
		addBall1();  // primary ball, which can be re-added to the screen
		addClickToRelease();
		addHeadsUpDisplay();
		addScoreLabel();
		addPowerUps();
		addBricks();
		addMouseListeners();
		defineBackgroundVelocities();
	}
	
	// will randomly place the images on the canvas
	// hopefully neither Chris or Avery is eaten by the t-rex
	private void addBackground() { 
		avery = new GImage(BACKGROUND_AVERY);
		avery.setSize(avery.getWidth() / 10.0, avery.getHeight() / 10.0);
		double ax = rgen.nextDouble(1.0, 4.0);
		double ay = rgen.nextDouble(10.0, 11.0);
		double averyx = getWidth() / 12.0 * ax - avery.getWidth() / 2.0;
		double averyy = getHeight() / 12.0 * ay - avery.getHeight() / 2.0;
		add(avery, averyx, averyy);
		
		chris = new GImage(BACKGROUND_CHRIS);
		chris.setSize(chris.getWidth() / 10.0, chris.getHeight() / 10.0);
		double cx = rgen.nextDouble(1.0, 4.0);
		double cy = rgen.nextDouble(6.0, 7.0);
		double chrisx = getWidth() / 12.0 * cx - chris.getWidth() / 2.0;
		double chrisy = getHeight() / 12.0 * cy - chris.getHeight() / 2.0;
		add(chris, chrisx, chrisy);
		
		trex = new GImage(BACKGROUND_TREX);
		trex.setSize(trex.getWidth() / 10.0, trex.getHeight() / 10.0);
		double tx = rgen.nextDouble(8.0, 11.0);
		double ty = rgen.nextDouble(6.0, 11.0);
		double trexx = getWidth() / 12.0 * tx - trex.getWidth() / 2.0;
		double trexy = getHeight() / 12.0 * ty - trex.getHeight() / 2.0;
		add(trex, trexx, trexy);
	}
	
	private void addPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double xposition = getWidth() / 2.0 - PADDLE_WIDTH / 2.0;
		double yposition = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, xposition, yposition);
	}
	
	// adds ball to the center of the screen
	private void addBall1() {
		ball1 = new GOval(BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
		ball1.setFilled(true);
		double xposition = getWidth() / 2.0 - BALL_RADIUS;
		double yposition = getHeight() / 2.0 - BALL_RADIUS;
		add(ball1, xposition, yposition);
	}
	
	// visual instructions
	private void addClickToRelease() { 
		clickToRelease = new GLabel("Click to Release Ball");
		clickToRelease.setFont(SMALL_FONT);
		double xposition = getWidth() / 2.0 - clickToRelease.getWidth() / 2.0;
		double yposition = ball1.getY() - BALL_RADIUS * 2.0;
		add(clickToRelease, xposition, yposition);
	}
	
	// shows bricks and lives remaining
	private void addHeadsUpDisplay() { 
		bricksRemainingLabel = new GLabel("Bricks Remaining: " + bricksRemaining);
		bricksRemainingLabel.setFont(SMALL_FONT);
		double bricksx = getWidth() / 4.0 - bricksRemainingLabel.getWidth() / 2.0;
		double bricksy = BRICK_Y_OFFSET / 2.0 + bricksRemainingLabel.getAscent() / 2.0;
		add(bricksRemainingLabel, bricksx, bricksy);
		
		turnsRemainingLabel = new GLabel("Lives Remaining: " + turnsRemaining);
		turnsRemainingLabel.setFont(SMALL_FONT);
		double turnsx = getWidth() / 4.0 * 3.0 - turnsRemainingLabel.getWidth() / 2.0;
		double turnsy = BRICK_Y_OFFSET / 2.0 + turnsRemainingLabel.getAscent() / 2.0;
		add(turnsRemainingLabel, turnsx, turnsy);
		
		multiplierLabel = new GLabel("COMBO: " + comboFormat.format(comboMultiplier) + "x");
		multiplierLabel.setFont(SMALL_FONT);
		double multiplierx = BALL_RADIUS;
		double multipliery = (getHeight() - paddle.getBottomY() + multiplierLabel.getAscent()) / 2.0 + paddle.getBottomY();
		add(multiplierLabel, multiplierx, multipliery);
	}
	
	private void addScoreLabel() {
		scoreLabel = new GLabel("SCORE: " + scoreFormat.format(score));
		scoreLabel.setFont(SMALL_FONT);
		double scorex = getWidth() / 2.0 - scoreLabel.getWidth() / 2.0;
		double scorey = (getHeight() - paddle.getBottomY() + scoreLabel.getAscent()) / 2.0 + paddle.getBottomY();
		add(scoreLabel, scorex, scorey);
	}
	
	// These will appear as logos on the screen; some will be under the bricks
	private void addPowerUps() {
		extraLifeLeft = new GImage(EXTRA_LIFE);
		extraLifeLeft.setSize(BALL_RADIUS * 3.0, BALL_RADIUS * 3.0);
		double leftx = rgen.nextDouble(BALL_RADIUS * 3.0, ball1.getX() - BALL_RADIUS * 3.0);
		double lefty = rgen.nextDouble(BRICK_Y_OFFSET + BALL_RADIUS * 8.0, paddle.getY() - BALL_RADIUS * 4.0);
		add(extraLifeLeft, leftx, lefty);
		
		extraLifeRight = new GImage(EXTRA_LIFE);
		extraLifeRight.setSize(BALL_RADIUS * 3.0, BALL_RADIUS * 3.0);
		double rightx = rgen.nextDouble(ball1.getRightX() + BALL_RADIUS * 3.0,
				getWidth() - BALL_RADIUS * 3.0);
		double righty = rgen.nextDouble(BRICK_Y_OFFSET + BALL_RADIUS * 8.0, paddle.getY() - BALL_RADIUS * 4.0);
		add(extraLifeRight, rightx, righty);
		
		moneyBallTop = new GImage(MONEY_BALL);
		moneyBallTop.setSize(BALL_RADIUS * 3.0, BALL_RADIUS * 3.0);
		double topx = getWidth() / 2.0 - moneyBallTop.getWidth() / 2.0;
		double topy = BRICK_Y_OFFSET / 2.0 - moneyBallTop.getHeight() / 2.0;
		add(moneyBallTop, topx, topy);
		
		moneyBallMiddle = new GImage(MONEY_BALL);
		moneyBallMiddle.setSize(BALL_RADIUS * 3.0, BALL_RADIUS * 3.0);
		double middlex = rgen.nextDouble(BALL_RADIUS * 3.0, getWidth() - BALL_RADIUS * 3.0);
		double middley = rgen.nextDouble(BRICK_Y_OFFSET + BALL_RADIUS * 2.0,
				BRICK_Y_OFFSET + BALL_RADIUS * 8.0);
		add(moneyBallMiddle, middlex, middley);
		
		moneyBallBottom = new GImage(MONEY_BALL);
		moneyBallBottom.setSize(BALL_RADIUS * 3.0, BALL_RADIUS * 3.0);
		double bottomx = rgen.nextDouble(BALL_RADIUS * 3.0, getWidth() - BALL_RADIUS * 3.0);
		double bottomy = rgen.nextDouble(getHeight() / 2.0 + BALL_RADIUS * 2.0,
				paddle.getY() - BALL_RADIUS * 12.0);
		add(moneyBallBottom, bottomx, bottomy);
		
		shootersShoot = new GImage(SHOOTERS_SHOOT);
		shootersShoot.setSize(BALL_RADIUS * 3.0, BALL_RADIUS * 3.0);
		double bottommiddley = rgen.nextDouble(BRICK_Y_OFFSET - BALL_RADIUS * 2.0,
				BRICK_Y_OFFSET + BALL_RADIUS * 12.0);
		double bottommiddlex = rgen.nextDouble(BALL_RADIUS * 3.0, getWidth() - BALL_RADIUS * 3.0);
		add(shootersShoot, bottommiddlex, bottommiddley);
		
		megaPaddle = new GImage(MEGA_PADDLE);
		megaPaddle.setSize(BALL_RADIUS * 3.0, BALL_RADIUS * 3.0);
		double topmiddley = rgen.nextDouble(BRICK_Y_OFFSET - BALL_RADIUS * 2.0,
				BRICK_Y_OFFSET + BALL_RADIUS *8.0);
		double topmiddlex = rgen.nextDouble(BALL_RADIUS * 3.0, getWidth() - BALL_RADIUS * 3.0);
		add(megaPaddle, topmiddlex, topmiddley);
	}
	
	/* The outside "for loop" builds rows of bricks, while the nested "for loop" builds columns
	 * of bricks. The color of a brick is chosen based on the row the brick is in.
	 * 
	 * Do NOT color bricks RED or BLUE.
	 */
	private void addBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {	
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				double xposition = (getWidth() / 2.0 -
						(NBRICK_COLUMNS / 2.0 * BRICK_WIDTH) -
						((NBRICK_COLUMNS - 1.0) / 2.0) * BRICK_SEP) +
						(j * (BRICK_WIDTH + BRICK_SEP));
				double yposition = BRICK_Y_OFFSET + (i * (BRICK_HEIGHT + BRICK_SEP));
				add(brick, xposition, yposition);
				
				if (i == 4 || i == 5) {
					brick.setColor(Color.PINK);
				}
				if (i == 3 || i == 6) {
					brick.setColor(Color.ORANGE);
				}
				if (i == 2 || i == 7) {
					brick.setColor(Color.GREEN);
				}
				if (i == 1 || i == 8) {
					brick.setColor(Color.CYAN);
				}
				if (i == 0 || i == 9) {  // yellow replaced with magenta for increased visibility
					brick.setColor(Color.MAGENTA);
				}
				
				addSuperBricks(brick, i, j);
			}
		}
	}
	
	
	// no more than 2% of the total number of bricks will be of a specific super color
	// there will be at least one of a specific super color
	private void  addSuperBricks(GRect brick, int i, int j) {
		int a = NBRICK_COLUMNS / 5 * 2;
		int b = NBRICK_COLUMNS / 5 * 3;
		int x = rgen.nextInt(1, 100);
		if (numFireBricks < ((NBRICK_COLUMNS * NBRICK_ROWS) + 1) / 50) {
			if (x == 72) {
				brick.setColor(Color.RED);
				numFireBricks++;
			}
		}
		if (numHomingBricks < ((NBRICK_COLUMNS * NBRICK_ROWS) + 1) / 50) {
			if (x == 20) {
				brick.setColor(Color.BLUE);
				numHomingBricks++;
			}
		}
		// adds a special brick of each kind to the last row if none previously exist
		if (i == NBRICK_ROWS - 1) {
			if (numFireBricks == 0) {
				if (j == a) {
					brick.setColor(Color.RED);
					numFireBricks++;
				}
			}
			if (numHomingBricks == 0) {
				if (j == b) {
					brick.setColor(Color.BLUE);
					numHomingBricks++;
				}
			}
		}
	}
	
	/* The middle of the paddle stays with the point of the cursor as the mouse moves, even as
	 * the paddle shrinks. The paddle will shrink to the same percentage as the percentage of
	 * blocks left, up until fifty percent is left.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX >= (newPaddleWidth / 2.0) && mouseX <= (getWidth() - newPaddleWidth / 2.0)) { 
			paddle.setX(mouseX - newPaddleWidth / 2.0);
		}
		if (ball5 != null && ball5.getColor() == Color.DARK_GRAY) {
			if (mouseX >= (newPaddleWidth / 2.0) && mouseX <= (getWidth() - newPaddleWidth / 2.0)) {
				ball5.setX(mouseX - BALL_RADIUS);
			}
		}
		if (homingBall1 != null) {
			if (mouseX >= (newPaddleWidth / 2.0) && mouseX <= (getWidth() - newPaddleWidth / 2.0)) {
				homingBall1.setX(mouseX - BALL_RADIUS * 4.0);
			}
		}
		if (homingBall2 != null) {
			if (mouseX >= (newPaddleWidth / 2.0) && mouseX <= (getWidth() - newPaddleWidth / 2.0)) { 
				homingBall2.setX(mouseX + BALL_RADIUS * 2.0);
			}
		}
	}
	
	// clicking the mouse gives the primary ball initial velocity components
	public void mouseClicked(MouseEvent e) {
		if (vx1 == 0 && vy1 == 0) {
			nsmb_fireballClip.play();
			remove(clickToRelease);
			vx1 = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) {
				vx1 = -vx1;
			}
			vy1 = VELOCITY_Y;
		}
	}
	
	// if a ball encounters a wall, the correct velocity component will adjust accordingly
	// this obeys the laws of incidence and reflection
	private void moveBall(GOval ball) {
		if (ball != null) {
			if (ball == ball1) { ball.move(vx1, vy1); }
			if (ball == ball2) { ball.move(vx2, vy2); }
			if (ball == ball3) { ball.move(vx3, vy3); }
			if (ball == ball4) { ball.move(vx4, vy4); }
			if (ball == ball5) { ball.move(vx5, vy5); }
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				if (ball == ball1) { vx1 = -vx1; }
				if (ball == ball2) { vx2 = -vx2; }
				if (ball == ball3) { vx3 = -vx3; }
				if (ball == ball4) { vx4 = -vx4; }
				if (ball == ball5) { vx5 = -vx5; }
			}
			if (hitTopWall(ball) || hitBottomWall(ball)) {
				if (ball == ball1) { vy1 = Math.abs(vy1); }
				if (ball == ball2) { vy2 = Math.abs(vy2); }
				if (ball == ball3) { vy3 = Math.abs(vy3); }
				if (ball == ball4) { vy4 = Math.abs(vy4); }
				if (ball == ball5) {
					vx5 = 0;
					vy5 = 0;
					remove(ball);
					ball = null;
				}
			}
		}
	}
	
	private boolean hitLeftWall(GObject object) {
		return(object.getX() <= 0);
	}
	
	private boolean hitRightWall(GObject object) {
		return(object.getRightX() >= getWidth());
	}
	
	private boolean hitTopWall(GObject object) {
		return(object.getY() <= 0);
	}
	
	private boolean hitBottomWall(GObject object) {
		return(object.getBottomY() >= getHeight());
	}
	
	// this method decides whether if and how to behave if a ball contacts an object on the canvas
	private void checkForCollisions(GOval ball, double vx, double vy) {
		if (ball != null) {
			GObject collider = getCollidingObject(ball);  // contacted object will generically be named "collider"
			collisionBrickOrPaddleOrBall(collider, ball, vx, vy);
			collisionExtraLife(collider);
			collisionMoneyBall(collider, ball, vx, vy);
			collisionShootersShoot(collider);
			collisionMegaPaddle(collider);
			collisionBottomWall(ball, vx, vy);
		}
	}
	
	// individually tests the four corners of a ball for objects until one is found 
	private GObject getCollidingObject(GOval ball) {
		GObject collider = null;
		if (collider == null) {
			collider = getElementAt(ball.getRightX(), ball.getY());
		}
		if (collider == null) {
			collider = getElementAt(ball.getX(), ball.getY());
		}
		if (collider == null) {
			collider = getElementAt(ball.getX(), ball.getBottomY());
		}
		if (collider == null) {
			collider = getElementAt(ball.getRightX(), ball.getBottomY());
		}
		return(collider);
	}
	
	/* If a ball contacts a brick, paddle, or other ball, the ball has contacted a solid object
	 * and must behave as such by bouncing off accordingly. Text on the screen and power-ups are
	 * not solid objects. An audio file will play for these solid-object collisions. The ball will
	 * change to the color of the collided solid object.
	 */
	private void collisionBrickOrPaddleOrBall(GObject collider, GOval ball, double vx, double vy) {
		if (collider != null && collider != chris && collider != avery &&
				collider != trex && collider != clickToRelease && collider != bricksRemainingLabel &&
				collider != turnsRemainingLabel && collider != scoreLabel && collider != multiplierLabel &&
				collider != extraLifeLeft && collider != extraLifeRight && collider != moneyBallTop &&
				collider != moneyBallMiddle && collider != moneyBallBottom && collider != shootersShoot &&
				collider != megaPaddle) {
			// audio
			if (collider == paddle || collider == ball1 || collider == ball2 || collider == ball3 ||
					collider == ball4 || collider == ball5) {
				bounceClip.play();
			} else {
				if (collider.getColor() != Color.RED && collider.getColor() != Color.BLUE) {	
					if (onFire(ball)) {
						fireballsoundClip.play();
					} else if (onHoming(ball)) {
						lightningClip.play();
					} else {
						int x = rgen.nextInt(1, 2);
						if (x % 2 == 0) {
							boulderimpactClip.play();
						} else {
							rubbleimpactClip.play();
						}
					}
				}
			}
			// takes color of collider
			if (!onFire(ball) && !onHoming(ball)) {
				if (collider.getColor() != Color.BLUE) {
					ball.setColor(collider.getColor());
				}
			}
			updateVelocity(collider, ball, vx, vy);
			removeBrick(collider, ball, vx, vy);
		}
	}
	
	private boolean onFire(GOval ball) {
		return(ball.getColor() == Color.RED);
	}
	
	private boolean onHoming(GOval ball) {
		return(ball.getColor() == Color.BLUE);
	}
	
	// all balls on the screen will speed up every time a ball collides with the paddle
	private void updateVelocity(GObject collider, GOval ball, double vx, double vy) {
		if (hitColliderTop(collider, ball) || hitColliderBottom(collider, ball)) {
			if (collider == paddle) {
				newDelay *= 0.95;
				if (ball == ball1) { vy1 = -Math.abs(vy1); }  // allows more accurate user control of paddle
				if (ball == ball2) { vy2 = -Math.abs(vy2); }
				if (ball == ball3) { vy3 = -Math.abs(vy3); }
				if (ball == ball4) { vy4 = -Math.abs(vy4); }
				if (ball == ball5) { vy5 = -Math.abs(vy5); }
				homingBallLifespan(ball, vx, vy); // homing balls are only "homing" for three paddle bounces
			} else {
				if (!onFire(ball)) {  // fire ball absolutely roasts through bricks
					if (ball == ball1) { vy1 = -vy1; }
					if (ball == ball2) { vy2 = -vy2; }
					if (ball == ball3) { vy3 = -vy3; }
					if (ball == ball4) { vy4 = -vy4; }
				}
			}
		}
	}
	
	/* IMPORTANT: The ball will only react when colliding with the top and bottom of objects.
	 * Therefore, the ball is able to pass horizontally through objects.
	 */
	private boolean hitColliderTop(GObject collider, GOval ball) {
		return(ball.getBottomY() >= collider.getY());
	}
	
	private boolean hitColliderBottom(GObject collider, GOval ball) {
		return(ball.getY() <= collider.getBottomY());
	}
	
	// will reset homing ball to regular black ball after five paddle bounces
	private void homingBallLifespan(GOval ball, double vx, double vy) {
		if (ball == homingBall1) {
			homing1Counter++;
			if (homing1Counter == 5) {
				ball.setColor(Color.BLACK);
				homingBall1 = null;
				vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
				if (rgen.nextBoolean(0.5)) {
					vx = -vx;
				}
				vy = VELOCITY_Y;
				if (ball == ball1) { vx1 = vx; vy1 = vy; }
				if (ball == ball2) { vx2 = vx; vy2 = vy; }
				if (ball == ball3) { vx3 = vx; vy3 = vy; }
				if (ball == ball4) { vx4 = vx; vy4 = vy; }
			}
		}
		if (ball == homingBall2) {
			homing2Counter++;
			if (homing2Counter == 5) {
				ball.setColor(Color.BLACK);
				homingBall2 = null;
				vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
				if (rgen.nextBoolean(0.5)) {
					vx = -vx;
				}
				vy = VELOCITY_Y;
				if (ball == ball1) { vx1 = vx; vy1 = vy; }
				if (ball == ball2) { vx2 = vx; vy2 = vy; }
				if (ball == ball3) { vx3 = vx; vy3 = vy; }
				if (ball == ball4) { vx4 = vx; vy4 = vy; }
			}
		}
	}
	
	// solid objects that aren't the paddle or other balls will be bricks
	private void removeBrick(GObject collider, GOval ball, double vx, double vy) {
		if (collider != paddle && collider != ball1 && collider != ball2 && collider != ball3 &&
				collider != ball4 && collider != ball5) { 
			checkIfSuperBrick(collider, ball, vx, vy);
			remove(collider);
			bricksRemaining--;
			bricksRemainingLabel.setLabel("Bricks Remaining: " + bricksRemaining);
			
			if (newPaddleWidth > PADDLE_WIDTH * 0.50) {  // paddle width decreasing
				newPaddleWidth -= PADDLE_WIDTH * 1.0 / (NBRICK_COLUMNS * NBRICK_ROWS);
				paddle.setSize(newPaddleWidth, PADDLE_HEIGHT);
			}
			
			score += 10 * comboMultiplier;
			remove(scoreLabel);
			addScoreLabel();
			
			comboMultiplier *= 1.05;
			multiplierLabel.setLabel("COMBO: " + comboFormat.format(comboMultiplier) + "x");
		}
	}
	
	private  void checkIfSuperBrick(GObject collider, GOval ball, double vx, double vy) {
		// initiates fire ball
		if (collider.getColor() == Color.RED && ball != ball5) {
			trexroarClip.play();
			ball.setColor(Color.RED);
			pause(2500);
			if (ball == ball1) { vx1*= 1.33; vy1 *= 1.33; }
			if (ball == ball2) { vx2*= 1.33; vy2 *= 1.33; }
			if (ball == ball3) { vx3*= 1.33; vy3 *= 1.33; }
			if (ball == ball4) { vx4*= 1.33; vy4 *= 1.33; }
		}
		// initiates homing ball
		if (collider.getColor() == Color.BLUE && ball != ball5 && !onFire(ball)) {
			if (homingBallsRemaining != 0) {
				thunderClip.play();
				ball.setColor(Color.BLUE);
				pause(2500);
				if (ball == ball1) { vx1*= 0; vy1 *= 0.85; }
				if (ball == ball2) { vx2*= 0; vy2 *= 0.85; }
				if (ball == ball3) { vx3*= 0; vy3 *= 0.85; }
				if (ball == ball4) { vx4*= 0; vy4 *= 0.85; }
				if (homingBall1 == null){
					homingBall1 = ball;
				} else {
					homingBall2 = ball;
				}
				homingBallsRemaining--;
			}
		}
	}
	
	// ball continues as normal, +1 life
	private void collisionExtraLife(GObject collider) {
		if (collider == extraLifeLeft || collider == extraLifeRight) {
			nsmb_coinClip.play();
			remove(collider);
			turnsRemaining++;
			turnsRemainingLabel.setLabel("Lives Remaining: " + turnsRemaining);
		}
	}
	
	/* Incident ball continues as normal, another ball is added to the screen above
	 * the incident ball. This new ball will share the y velocity and take on the opposite
	 * x velocity of the incident ball.
	 */
	private void collisionMoneyBall(GObject collider, GOval ball, double vx, double vy) {
		if (ball != ball5 && !onFire(ball)) {
			if (collider == moneyBallTop || collider == moneyBallBottom || collider == moneyBallMiddle) {
				nsmb_coinClip.play();
				if (collider == moneyBallTop) {
					addBall2(ball, vx, vy);
				}
				if (collider == moneyBallBottom) {
					addBall3(ball, vx, vy);
				}
				if (collider == moneyBallMiddle) {
					addBall4(ball, vx, vy);
				}
				remove(collider);
			}
		}
	}
	
	// ball 2 correlates with the top money ball
	private void addBall2(GOval ball, double vx, double vy) {
		ball2 = new GOval(BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
		ball2.setFilled(true);
		double xposition = ball.getX();
		double yposition = ball.getY() - BALL_RADIUS * 2.0;
		add(ball2, xposition, yposition);
		if (!onHoming(ball)) {
			vx2 = -vx;
			vy2 = vy;
		} else {
			vx2 = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) {
				vx2 = -vx2;
			}
			vy2 = vy;
		}
	}
	
	// ball 3 correlates with the bottom money ball
	private void addBall3(GOval ball, double vx, double vy) {
		ball3 = new GOval(BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
		ball3.setFilled(true);
		double xposition = ball.getX();
		double yposition = ball.getY() - BALL_RADIUS * 2.0;
		add(ball3, xposition, yposition);
		if (!onHoming(ball)) {
			vx3 = -vx;
			vy3 = vy;
		} else {
			vx3 = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) {
				vx3 = -vx3;
			}
			vy3 = vy;
		}
	}
	
	private void addBall4(GOval ball, double vx, double vy) {
		ball4 = new GOval(BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
		ball4.setFilled(true);
		double xposition = ball.getX();
		double yposition = ball.getY() - BALL_RADIUS * 2.0;
		add(ball4, xposition, yposition);
		if (!onHoming(ball)) {
			vx4 = -vx;
			vy4 = vy;
		} else {
			vx4 = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) {
				vx4 = -vx4;
			}
			vy4 = vy;
		}
	}
	
	private void collisionShootersShoot(GObject collider) {
		if (collider == shootersShoot) {
			guncockClip.play();
			remove(collider);
			addBall5();
			addClickToShoot();
			waitForClick();
			remove(clickToShoot);
			gunshootClip.play();
			ball5.setColor(Color.LIGHT_GRAY);
		}
	}
	
	private void addBall5() {
		ball5 = new GOval(BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
		ball5.setFilled(true);
		ball5.setColor(Color.DARK_GRAY);
		double xposition = paddle.getX() + paddle.getWidth() / 2.0 - BALL_RADIUS;
		double yposition = paddle.getY() - BALL_RADIUS * 2.1;
		add(ball5, xposition, yposition);
		vx5 = 0;
		vy5 = VELOCITY_Y * -1.66;
	}
	
	private void addClickToShoot() {
		clickToShoot = new GLabel("Click to Shoot");
		clickToShoot.setFont(BIG_FONT);
		double xposition = getWidth() / 2.0 - clickToShoot.getWidth() / 2.0;
		double yposition = getHeight() / 2.0;
		add(clickToShoot, xposition, yposition);
	}
	
	private void  collisionMegaPaddle(GObject collider) {
		if (collider == megaPaddle) {
			nsmb_powerupClip.play();
			remove(collider);
			newPaddleWidth *= 2.0;
			paddle.setSize(newPaddleWidth, PADDLE_HEIGHT);
		}
	}
	
	/* The primary ball will reset after hitting the bottom of the screen.
	 * Money balls will just be removed from the screen at no cost to the player.
	 * Money balls can still contact a primary ball that hasn't been clicked and released yet!
	 * If any ball collides with the bottom of the screen, the delay will reset to default.
	 */
	private void collisionBottomWall(GOval ball, double vx, double vy) {
		if (hitBottomWall(ball)) {
			newDelay = DELAY;
			if (ball == ball1) { vx1 = 0; vy1 = 0; }
			if (ball == ball2) { vx2 = 0; vy2 = 0; }
			if (ball == ball3) { vx3 = 0; vy3 = 0; }
			if (ball == ball4) { vx4 = 0; vy4 = 0; }
			if (ball == ball1) {
				turnsRemaining--;
				turnsRemainingLabel.setLabel("Lives Remaining: " + turnsRemaining);
				if (turnsRemaining != 0) {
					bombClip.play();
					remove(ball);
					addBall1();
					addClickToRelease();
					comboMultiplier = 1.00;
					multiplierLabel.setLabel("COMBO: " + comboFormat.format(comboMultiplier) + "x");
				}
			} else {
				remove(ball);
				ball = null;
			}
		}
	}
	
	private boolean gameOver() {
		return(bricksRemaining == 0 || turnsRemaining == 0);
	}
	
	// centered text informing the player of a win or loss
	// the screen will pause indefinitely when this message is displayed
	private void endResult() { 
		GLabel winOrLose = new GLabel("");
		if (bricksRemaining == 0) {
			winOrLose.setLabel(MESSAGE_IF_WIN);
			applauseClip.play();
		} else {
			winOrLose.setLabel(MESSAGE_IF_LOSE);
			trexroarClip.play();
		}
		winOrLose.setFont(BIG_FONT);
		double xposition = getWidth() /2.0 - winOrLose.getWidth() / 2.0;
		double yposition = getHeight() / 2.0 + winOrLose.getAscent() / 2.0;
		add(winOrLose, xposition, yposition);
	}
	
	// if an image encounters a wall, the correct velocity component will adjust accordingly
	// this obeys the laws of incidence and reflection
	private void moveBackground(GImage image) {
		if (image != null) {
			if (image == chris) { image.move(vxc, vyc); }
			if (image == avery) { image.move(vxa, vya); }
			if (image == trex) { image.move(vxt, vyt); }
			if (hitLeftWall(image) || hitRightWall(image)) {
				if (image == chris) { vxc = -vxc; }
				if (image == avery) { vxa = -vxa; }
				if (image == trex) { vxt = -vxt; }
			}
			if (hitTopWall(image) || hitBottomWall(image)) {
				if (image == chris) { vyc = -vyc; }
				if (image == avery) { vya = -vya; }
				if (image == trex) { vyt = -vyt; }
			}
		}
	}
	
	private void defineBackgroundVelocities() {
		vxc = VELOCITY_X_BACKGROUND;
		if (rgen.nextBoolean(0.5)) {
			vxc = -vxc;
		}
		vyc = VELOCITY_Y_BACKGROUND;
		if (rgen.nextBoolean(0.5)) {
			vyc = -vyc;
		}
		
		vxa = VELOCITY_X_BACKGROUND;
		if (rgen.nextBoolean(0.5)) {
			vxa = -vxa;
		}
		vya = VELOCITY_Y_BACKGROUND;
		if (rgen.nextBoolean(0.5)) {
			vya = -vya;
		}
		
		vxt = VELOCITY_X_BACKGROUND;
		if (rgen.nextBoolean(0.5)) {
			vxt = -vxt;
		}
		vyt = VELOCITY_Y_BACKGROUND;
		if (rgen.nextBoolean(0.5)) {
			vyt = -vyt;
		}
	}
	
}
