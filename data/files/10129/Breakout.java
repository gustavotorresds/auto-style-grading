/*
 * File: Breakout.java
 * -------------------
 * Name: Justin Senterfitt
 * Section Leader: Kate Rydberg (rydbergk)
 * 
 * This file implements the game of Breakout. I use protected throughout to allow extension and
 * reference in extra program.
 * 
 * Minor Extensions:
 *   Detects collisions using ball's radius instead of containing rectangle.
 *   Collisions with anything except GRects are purposely ignored.
 *   Has a "you won" and "you lost" label.
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

  // Offset of the paddle up from the bottom.
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
  // Per the handout I sped this up slightly for a more interesting game. Faster refresh is better
  // than a faster ball since it means less of the ball "slipping" into things.
  public static final double DELAY = 1000.0 / 90.0;

  // Number of turns 
  public static final int NTURNS = 3;

  // This is the minimum x or y component of a collision angle to trigger a bounce in that
  // respective component.
  protected static final double MINIMUM_COMPONENT_ANGLE_FOR_BOUNCE = Math.sin(Math.toRadians(22.5));

  // How accurately to check for collisions. Behavior not guaranteed for other settings.
  protected static final double DETECTION_PRECISION_DEGREES = 45;

  // Elements left once user wins.
  // Not final because extension needs different value.
  protected static int ELEMENTS_LEFT_AT_WIN = 2;

  // The paddle object. Must be seen by mouse listeners, so is instance variable.
  protected static final GRect PADDLE = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

  // Ball object, final since I never make a new one. Instance since it's used throughout the game.
  protected static final GOval BALL = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);

  // Ball's velocity. Collisions tend to affect both, and it's difficult to return 2 vars.
  // Note: Once initialized for a round, these should only have their signs, not magnitudes changed.
  // Sign changes can lead to "sticking".
  protected static double ballVx = 0;
  protected static double ballVy = 0;

  public void run() {
    playAreaSetup();
    setup();
    playGame();
  }

  /**
   * Set screen size and title.
   */
  protected void playAreaSetup() {
    // Set the window's title bar text
    setTitle("CS 106A Breakout");
    // Set the canvas size.  In your code, remember to ALWAYS use getWidth()
    // and getHeight() to get the screen dimensions, not these constants!
    setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
  }

  /**
   * Perform all one-time setup functions.
   */
  protected void setup() {
    addMouseListeners();
    createAllBricks();
    drawPaddle();
    drawBall();
    launchBall();
  }

  /**
   * Set the paddle's position as screen left <= mouse position <= screen right.
   */
  public void mouseMoved(MouseEvent e) {
    double newPaddlePosition = e.getX();
    newPaddlePosition = Math.min(newPaddlePosition, getWidth() - (PADDLE_WIDTH / 2));
    newPaddlePosition = Math.max(newPaddlePosition, PADDLE_WIDTH / 2);
    PADDLE.setCenterX(newPaddlePosition);
  }


  /**
   * Create all of the colored bricks.
   */
  protected void createAllBricks() {
    double rowWidth = (NBRICK_COLUMNS * BRICK_WIDTH) + ((NBRICK_COLUMNS - 1) * BRICK_SEP);
    double rowXOffset = (getWidth() - rowWidth) / 2;
    int row = 0;
    // Would normally loop over an array of colors but we haven't hit those yet.
    row = createBrickRows(Color.RED, row, rowXOffset, 5);
    row = createBrickRows(Color.ORANGE, row, rowXOffset, 4);
    row = createBrickRows(Color.YELLOW, row, rowXOffset, 3);
    row = createBrickRows(Color.GREEN, row, rowXOffset, 2);
    row = createBrickRows(Color.CYAN, row, rowXOffset, 1);
  }

  /**
   * Determine position for a brick row, and draw the brick row.
   * @param color The color to paint the bricks.
   * @param row The current row.
   * @param rowXOffset The X-Offset to center the brick row.
   * @param colorsRemaining The number of colors remaining including this one.
   * @return The new, current row.
   */
  protected int createBrickRows(Color color, int row, double rowXOffset, int colorsRemaining) {
    int rowsLeft = NBRICK_ROWS - row;
    // With each color, we decrease the number of rows left. Even something like 14 rows / 5 colors
    // (2.8 rows per color), will paint the first color with only 2 rows. This makes the next color
    // have 12 / 4 (3 rows per color), and everything goes normally from there.
    int rowsForColor = rowsLeft / colorsRemaining;
    rowsLeft -= rowsForColor;
    for (int i = 0; i < rowsForColor; i++) {
      drawBrickRow(color, row, rowXOffset);
      row += 1;
    }
    return row;
  }

  /**
   * Create a single row of bricks of a given color at a given row position.
   * @param color The color to paint the bricks.
   * @param row The row to place the bricks.
   * @param brickXOffset The X-Offset to center the brick row.
   */
  protected void drawBrickRow(Color color, int row, double brickXOffset) {
    double yOffset = (row * BRICK_HEIGHT) + ((row - 1) * BRICK_SEP) + BRICK_Y_OFFSET;
    for (int i = 0; i < NBRICK_COLUMNS; i++) {
      double xOffset = (i * BRICK_WIDTH) + (i * BRICK_SEP) + brickXOffset;
      drawBrick(xOffset, yOffset, color);
    }
  }

  /**
   * Create a single brick at a given location.
   * @param xOffset Brick's X-Coordinate.
   * @param yOffset Brick's Y-Coordinate.
   * @param color Brick's color.
   */
  protected void drawBrick(double xOffset, double yOffset, Color color) {
    GRect brick = new GRect(xOffset, yOffset, BRICK_WIDTH, BRICK_HEIGHT);
    brick.setFilled(true);
    brick.setFillColor(color);
    brick.setColor(color);
    add(brick);
  }

  /**
   * Draw the paddle based on constants. The object is defined as an instance variable.
   */
  protected void drawPaddle() {
    PADDLE.setFilled(true);
    // Instructions were ambiguous on whether Y_OFFSET was to top or bottom, I'm using bottom.
    PADDLE.setBottomY(getHeight() - PADDLE_Y_OFFSET);
    add(PADDLE);
  }

  /**
   * Create the ball based on constants.
   */
  protected void drawBall() {
    BALL.setFilled(true);
    BALL.setCenterLocation(getWidth() / 2, getHeight() / 2);
    add(BALL);
  }

  /**
   * Choose the ball's initial velocity.
   */
  protected void launchBall() {
    // The handout said make it an instance variable, but there doesn't seem to be a good reason
    // to do so. It's only called in this function, and only 3 times per game.
    RandomGenerator rgen = RandomGenerator.getInstance();
    ballVx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
    ballVx = rgen.nextBoolean(0.5) ? ballVx : -ballVx;
    ballVy = VELOCITY_Y;
  }

  /**
   * Play the game, and once finished determine if player won or lost.
   */
  protected void playGame() {
    int livesRemaining = NTURNS;
    livesRemaining = gameLoop(livesRemaining);
    // Handle win and loss conditions.
    if (livesRemaining <= 0) {
      drawCenteredLabel("You lost :(");
    } else {
      drawCenteredLabel("You won! :)");
    }
    remove(BALL);
  }

  /**
   * Handle game loop with actions to be taken on each refresh.
   * @param livesRemaining The current number of lives left.
   */
  protected int gameLoop(int livesRemaining) {
    while (livesRemaining > 0 && getElementCount() > ELEMENTS_LEFT_AT_WIN) {
      BALL.move(ballVx, ballVy);
      livesRemaining = checkAndHandleWallCollisions(livesRemaining);
      checkAndHandleObjectCollisions();
      pause(DELAY);
    }
    return livesRemaining;
  }

  /**
   * Bounce the ball if it hits a wall, and return true if it hit the bottom wall.
   * Note: Since neither the base-game or extension modify the ball's x/y speed (only direction)
   * this method is safe. If the ball speed were ever changed during play, this could glitch and
   * "stick" the ball to the walls.
   * @param livesRemaining The current number of lives left.
   * @return livesRemaining The current number of lives left.
   */
  protected int checkAndHandleWallCollisions(int livesRemaining) {
    double ballCenterX = BALL.getCenterX();
    double ballCenterY = BALL.getCenterY();
    // Handle both hitting top and bottom walls.
    if (ballCenterY > (getHeight() - BALL_RADIUS) || ballCenterY < BALL_RADIUS) {
      ballVy = -ballVy;
      // If the ball specifically hit the bottom wall, handle updating the user's life. We don't
      // break the loop for this, the game can proceed as-is and just incorporate the new ball.
      if (ballCenterY > (getHeight() - BALL_RADIUS)) {
        livesRemaining = handleLostBall(livesRemaining);
      }
    }
    // Handle both hitting left and right walls.
    // Not an else clause since we want to explicitly allow corner collisions.
    if (ballCenterX > (getWidth() - BALL_RADIUS) || ballCenterX < BALL_RADIUS) {
      ballVx = -ballVx;
    }
    return livesRemaining;
  }

  /**
   * Handle the ball being lost by resetting its position and updating lives left.
   * @param livesRemaining The current number of lives left.
   */
  protected int handleLostBall(int livesRemaining) {
    BALL.setCenterLocation(getWidth() / 2, getHeight() / 2);
    launchBall();
    return livesRemaining - 1;
  }

  /**
   * Check if the ball is touching any objects, and call the appropriate collision function.
   */
  protected void checkAndHandleObjectCollisions() {
    GObject collisionObject = null;
    double theta = 0;
    // Go around the edge of the ball, checking for collisions.
    for (theta = 0; theta < 2 * Math.PI; theta += Math.toRadians(DETECTION_PRECISION_DEGREES)) {
      collisionObject = getElementWithTheta(theta);
      // Stop on any theta with a collision. Since we're dealing with an imprecise game, multiple
      // collisions are beyond the physics required.
      if (collisionObject != null) {
        handleObjectCollisions(collisionObject, theta);
        break;
      }
    }
  }

  /**
   * Get an element at a certain angle from the ball, just outside its edge.
   * @param theta The angle to check.
   * @return The object found, or null.
   */
  protected GObject getElementWithTheta(double theta) {
    // Slightly increase radius to avoid catching the ball in the check.
    double xOffset = (BALL_RADIUS + 1) * Math.cos(theta);
    double yOffset = (BALL_RADIUS + 1) * Math.sin(theta);
    return getElementAt(BALL.getCenterX() + xOffset, BALL.getCenterY() +  yOffset);
  }

  /**
   * Handle any collisions that were detected.
   * @param collisionObject The object that was hit.
   * @param theta The angle of the hit.
   */
  protected void handleObjectCollisions(GObject collisionObject, double theta) {
    // Normally I'd used .equals(), but we literally want to know if this is the same address.
    if (collisionObject == PADDLE) {
      // In base game, paddle can *only* send ball up. This also nicely avoids paddle sticking.
      ballVy = -Math.abs(ballVy);
    } else {
      updateBallDirection(theta);
      // Break the brick.
      remove(collisionObject);
    }
  }

  /**
   * Update the ball's direction based on the angle of collision.
   * I get the x and y components of the angle, then check if they're negative, positive, or
   * near zero. If near zero, nothing happens to that component's velocity, otherwise the sign of
   * the velocity is changed to match the respective component's sign. This only "works" because
   * I'm only checking at angles that are multiples of 45 degrees, otherwise I'd need to do more
   * sophisticated collision math.
   * @param theta The angle of the collision.
   */
  protected void updateBallDirection(double theta) {
    double xVector = Math.cos(theta);
    double yVector = Math.sin(theta);
    // The minimum check splits the ball's circumference in 8ths, 1 each for up/down/left/right, and
    // each midpoint between them. Put another way, it makes the odds of bouncing in any of those
    // directions roughly equal.
    if (Math.abs(xVector) > MINIMUM_COMPONENT_ANGLE_FOR_BOUNCE) {
      double xSign = -Math.signum(xVector);
      ballVx = Math.abs(ballVx) * xSign;
    }
    if (Math.abs(yVector) > MINIMUM_COMPONENT_ANGLE_FOR_BOUNCE) {
      double ySign = -Math.signum(yVector);
      ballVy = Math.abs(ballVy) * ySign;
    }
  }

  /**
   * Add a given message to the center of the screen.
   * @param labelText The text to display.
   */
  protected void drawCenteredLabel(String labelText) {
    GLabel label = new GLabel(labelText);
    Font font = label.getFont();
    // Make the label bold and large, easier to read.
    label.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize() * 2));
    label.setCenterLocation(getWidth() / 2, (getHeight() / 2) + 0);
    add(label);
  }
}
