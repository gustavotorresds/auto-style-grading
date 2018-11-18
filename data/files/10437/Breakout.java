/*
 * File: Breakout.java
 * -------------------
 * Name: Nicholas Gaggero
 * Section Leader: Peter M
 * 
 * This file will eventually implement the game of Breakout.
 /**
 * Add title and score board
 * Next start setting up the game:
 * 1 Bricks - use Pyramid and adjust for layout and colors
 * 2 Paddle - simple
 * 3 Ball - simple
 * Animate the game (might need to be added within setup text above):
 * 1 Paddle - x dimension follows mouse - y stays fixed
 * 2 Ball - bounces of paddle and all walls except floor
 * 3 Disappearing boxes - when hit by ball and add 1 to score
 * 4 Rules
 * Play Game:
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;
	public static final double NBRICK_COLUMNS = 10;
	public static final double NBRICK_ROWS = 10;
	public static final double BRICK_SEP = 4;
	public static final double BRICK_WIDTH = Math.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);
	public static final double BRICK_HEIGHT = 8;
	public static final double BRICK_Y_OFFSET = 70;
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;
	public static final double PADDLE_Y_OFFSET = 30;
	public static final double BALL_RADIUS = 10;
	public static final double VELOCITY_Y = 3.0;
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;
	public static final double DELAY = 1000.0 / 60.0;
	public static final double NTURNS = 3;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GOval ball;
	private GRect paddle;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setup();
		//startGame();//
	}

	private void setTitle() {
		GLabel title = new GLabel("CS 106A Breakout");
		title.setFont("SansSerif-16");
		double titleX = getWidth()/2;
		title.setLocation(titleX, 0);
		add(title);
	}

	public GObject getCollidingObject() {
		double collidingX = ball.getX();
		double collidingY = ball.getY();
		GObject collider = getElementAt(collidingX, collidingY);
		return collider;
	}

	public void setup () {
		addBricks();
		setupPaddle();
		setupBall();
	}

	private void addBricks() {
		for(int ix = 0; ix < NBRICK_ROWS; ix ++) {
			for(int iy = 0; iy < NBRICK_COLUMNS; iy++) {
				double x = (BRICK_WIDTH * iy) + (BRICK_SEP * iy) + BRICK_SEP * 2;
				double y = BRICK_Y_OFFSET + (NBRICK_ROWS - ix) * BRICK_HEIGHT + (NBRICK_ROWS - ix) * BRICK_SEP;
				GRect rect = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(rect);
				rect.setFilled(true);
				if (ix < 2) {
					rect.setColor(Color.CYAN);
				}
				if (ix > 1) {
					rect.setColor(Color.GREEN);
				}
				if (ix > 3) {
					rect.setColor(Color.YELLOW);
				}
				if (ix > 5) {
					rect.setColor(Color.ORANGE);
				}
				if (ix > 7) {
					rect.setColor(Color.RED);
				}
			}
		}
	}

	private void setupPaddle() {
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect (paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	private void setupBall() {
		double brickCount = NBRICK_COLUMNS * NBRICK_ROWS;
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		double vy = VELOCITY_Y;
		int turns = 3;
		waitForClick();
		double ballX = getWidth()/2 - BALL_RADIUS/2;
		double ballY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT-BALL_RADIUS*4;
		double ballSize = BALL_RADIUS;
		ball = new GOval(ballSize, ballSize);
		ball.setFilled(true);
		add(ball, ballX, ballY);
		while(true) {
			if(turns > 0) {
				ball.move(vx,-vy);
				if(ball.getX() > getWidth() - BALL_RADIUS) {
					vx = vx * -1;
				}
				if(ball.getX() < 0) {
					vx = vx * -1;
				}
				if(ball.getY() < 0) {
					vy = vy * -1;
				}
				if(ball.getY() > CANVAS_HEIGHT) {
					turns = turns - 1;
				}
				GObject collider = getCollidingObject();
				if(collider == paddle) {
					vy = vy * -1;
				}
				if(collider != paddle) {
					if(collider != null) {
						remove(collider);
						vy = vy * -1;
						brickCount = brickCount -1;
					}
				}
				pause(DELAY);
				if (brickCount == 0) {
					GLabel winner = new GLabel("WINNER");
					winner.setFont("SansSerif-26");
					double titleX = CANVAS_WIDTH/2 - winner.getWidth()/2 ;
					double titleY = CANVAS_HEIGHT/2;
					winner.setLocation(titleX, titleY);
					add(winner);
					break;
				}
			} if (turns == 0) {
				if (brickCount == 0) {
					GLabel loser = new GLabel("LOSER");
					loser.setFont("SansSerif-26");
					double titleX = CANVAS_WIDTH/2 - loser.getWidth()/2 ;
					double titleY = CANVAS_HEIGHT/2;
					loser.setLocation(titleX, titleY);
					add(loser);
				}
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		paddle.setLocation(e.getX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		if (e.getX() > CANVAS_WIDTH - PADDLE_WIDTH) {
			paddle.setLocation(CANVAS_WIDTH - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
		if (e.getX() < PADDLE_WIDTH/2) {
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
}
// Kill at floor variable turns = 3. while turns > 0 play the game
// 3x goes per round
// All four corners of ball



