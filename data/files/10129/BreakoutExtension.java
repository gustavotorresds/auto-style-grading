/*
 * File: BreakoutExtension.java
 * -------------------
 * Name: Justin Senterfitt
 * Section Leader: Kate Rydberg (rydbergk)
 * 
 * This file extends the game of Breakout.
 * 
 * Extensions:
 *   Intro text
 *   Lives left label
 *   Scoring and score label
 *   Clicker Kicker (increase speed and points with every click)
 *   Color values (higher bricks are worth more)
 *   Bricks and paddle play sound
 *   Paddle uses same polar bouncing as bricks.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends Breakout {

  // A label to hold the user's score. Instance variable along with things like Paddle and Ball,
  // since it'd be passed through everything otherwise. Final since we only make one.
  private final GLabel SCORE_LABEL = new GLabel("Score: " + 0);

  // The players life label. Instance variable along with things like Paddle and Ball, since it'd
  // be passed through everything otherwise. Final since we only make one.
  private final GLabel LIFE_LABEL = new GLabel("Lives: " + NTURNS);

  // How much labels should be inset from the screen's sides and bottom.
  private static double LABEL_OFFSET_X_AND_Y = 5;

  // Scores per brick.
  private static int RED_BRICK_VALUE = 5;
  private static int ORANGE_BRICK_VALUE = 4;
  private static int YELLOW_BRICK_VALUE = 3;
  private static int GREEN_BRICK_VALUE = 2;
  private static int CYAN_BRICK_VALUE = 1;

  // Number of elements left on screen when player wins.
  private static int ELEMENTS_LEFT_AT_WIN = 4;

  // How much to speed up with each click.
  public static final double SPEED_FACTOR = 1.25;

  // The user's current score multipliers. Has to be instance variable since is accessed by mouse
  // events.
  private static double scoreMultiplier = 1;

  // Modified delay, only used in extension. Has to be instance variable since is accessed by mouse
  // events.
  private static double currentDelay = DELAY;

  /**
   * Perform all one-time setup functions, including waiting for user to start.
   */
  @Override
  protected void setup() {
    // These aren't created in preStartSetup to avoid instance variables to hold them.
    GLabel instructionLabel = drawCenteredLabel(
        "Click your mouse when ready!", 2, 45);
    GLabel kickerLabel = drawCenteredLabel(
        "Click again to speed up the ball and win more points.", 1, 0);
    preStartSetup();
    waitForClick();
    // Do everything that needs to happen once the user is ready.
    remove(instructionLabel);
    remove(kickerLabel);
    drawBall();
    launchBall();
  }

  /**
   * Do most everything that needs to be done before the user can press play.
   */
  protected void preStartSetup() {
    // These normally update on click, so we need to back out the effect of the first click.
    // Alternative would be to alter game's starting constants, which would be harder to read.
    currentDelay *= SPEED_FACTOR;
    scoreMultiplier /= SPEED_FACTOR * SPEED_FACTOR;
    // Add graphical elements.
    addMouseListeners();
    createAllBricks();
    drawPaddle();
    drawScoreAndLifeLabel();
  }

  /**
   * Handle the clicker-kicker by increasing game's refresh time, rather than ball speed. Changing
   * ball speed mid-round has a lot of nasty side effects (e.g., ball shooting through bricks).
   */
  public void mousePressed(MouseEvent e) {
    currentDelay /= SPEED_FACTOR;
    scoreMultiplier *= SPEED_FACTOR * SPEED_FACTOR;
  }

  /**
   * Create a score and life label.
   */
  protected void drawScoreAndLifeLabel() {
    // Score label.
    SCORE_LABEL.setBottomY(getHeight() - LABEL_OFFSET_X_AND_Y);
    SCORE_LABEL.setX(LABEL_OFFSET_X_AND_Y);
    add(SCORE_LABEL);
    // Life label.
    LIFE_LABEL.setBottomY(getHeight() - LABEL_OFFSET_X_AND_Y);
    LIFE_LABEL.setX(getWidth() - LIFE_LABEL.getWidth() - LABEL_OFFSET_X_AND_Y);
    add(LIFE_LABEL);
  }

  /**
   * Handle game loop with actions to be taken on each refresh.
   * @param livesRemaining The current number of lives left.
   */
  protected int gameLoop(int livesRemaining) {
    int score = 0;
    while (livesRemaining > 0 && getElementCount() > ELEMENTS_LEFT_AT_WIN) {
      BALL.move(ballVx, ballVy);
      livesRemaining = checkAndHandleWallCollisions(livesRemaining);
      score = checkAndHandleObjectCollisions(score);
      pause(currentDelay);
    }
    return livesRemaining;
  }

  /**
   * Check if the ball is touching any objects, and call the appropriate collision function.
   * @param score The player's current score.
   */
  protected int checkAndHandleObjectCollisions(int score) {
    GObject collisionObject = null;
    double theta = 0;
    // Go around the edge of the ball, checking for collisions.
    for (theta = 0; theta < 2 * Math.PI; theta += Math.toRadians(DETECTION_PRECISION_DEGREES)) {
      collisionObject = getElementWithTheta(theta);
      // Stop on any theta with a collision. Since we're dealing with an imprecise game, multiple
      // collisions are beyond the physics required.
      if (collisionObject != null) {
        score = handleObjectCollisions(collisionObject, theta, score);
        break;
      }
    }
    return score;
  }

  /**
   * Handle any collisions that were detected.
   * @param collisionObject The object that was hit.
   * @param theta The angle of the hit.
   * @param score The player's current score.
   */
  protected int handleObjectCollisions(GObject collisionObject, double theta, int score) {
    updateBallDirection(theta);
    // This doesn't seem too compute intensive to load at each bounce. Otherwise would declare in
    // gameLoop and pass through.
    MediaTools.loadAudioClip("bounce.au").play();
    if (collisionObject != PADDLE) {
      remove(collisionObject);
      score = updateScore(collisionObject, score);
    }
    return score;
  }

  /**
   * Handle the ball being lost by resetting its position and updating lives left.
   * @param livesRemaining The current number of lives left.
   */
  @Override
  protected int handleLostBall(int livesRemaining) {
    BALL.setCenterLocation(getWidth() / 2, getHeight() / 2);
    launchBall();
    LIFE_LABEL.setText("Lives: " + livesRemaining);
    return livesRemaining - 1;
  }

  /**
   * Update the score and score label based on multiplier and brick color.
   * @param collisionObject The brick the ball collided with.
   * @param score The player's current score.
   */
  protected int updateScore(GObject collisionObject, int score) {
    if (collisionObject.getColor().equals(Color.RED)) {
      score += RED_BRICK_VALUE * scoreMultiplier;
    } else if (collisionObject.getColor().equals(Color.ORANGE)) {
      score += ORANGE_BRICK_VALUE * scoreMultiplier;
    } else if (collisionObject.getColor().equals(Color.YELLOW)) {
      score += YELLOW_BRICK_VALUE * scoreMultiplier;
    } else if (collisionObject.getColor().equals(Color.GREEN)) {
      score += GREEN_BRICK_VALUE * scoreMultiplier;
    } else if (collisionObject.getColor().equals(Color.CYAN)) {
      score += CYAN_BRICK_VALUE * scoreMultiplier;
    }
    SCORE_LABEL.setText("Score: " + Math.round(score));
    return score;
  }

  /**
   * Get an element at a certain angle from the ball, just outside its edge.
   * @param theta The angle to check.
   * @return The object found, or null.
   */
  @Override
  protected GObject getElementWithTheta(double theta) {
    // Slightly increase radius to avoid catching the ball in the check.
    double xOffset = (BALL_RADIUS + 1) * Math.cos(theta);
    double yOffset = (BALL_RADIUS + 1) * Math.sin(theta);
    GObject obj = getElementAt(BALL.getCenterX() + xOffset, BALL.getCenterY() +  yOffset);
    if (obj instanceof GLabel) {
      return null;
    }
    return obj;
  }

  /**
   * Add a given message to the center of the screen.
   * @param labelText The text to display.
   * @param sizeMultiplier How large to make the label.
   * @param yOffset How far to offset the label vertically to allow multiple on screen.
   */
  protected GLabel drawCenteredLabel(String labelText, int sizeMultiplier, int yOffset) {
    GLabel label = new GLabel(labelText);
    Font font = label.getFont();
    label.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize() * sizeMultiplier));
    label.setCenterLocation(getWidth() / 2, (getHeight() / 2) + yOffset);
    add(label);
    return label;
  }
}