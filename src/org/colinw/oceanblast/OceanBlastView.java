package org.colinw.oceanblast;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class OceanBlastView extends GLSurfaceView {
  public OceanBlastView(OceanBlastActivity activity) {
    super(activity);

    renderer_ = new OceanBlastRenderer(activity);

    //setEGLContextClientVersion(2);
    setRenderer(renderer_);

    //setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
  }

  public boolean onTouchEvent(final MotionEvent event) {
    final int midX = 500;

    int action = (event.getAction() & MotionEvent.ACTION_MASK);

    boolean left    = false;
    int     leftId  = 0;
    int     leftX   = 0;
    int     leftY   = 0;
    boolean right   = false;
    int     rightId = 0;
    int     rightX  = 0;
    int     rightY  = 0;

    for (int i = 0; i < event.getPointerCount(); i++) {
      int mouseX = (int) event.getX(i);
      int mouseY = (int) event.getY(i);

      if (mouseX < midX) {
        left    = true;
        leftId  = i;
        leftX   = mouseX;
        leftY   = mouseY;
      }
      else {
        right   = true;
        rightId = i;
        rightX  = mouseX;
        rightY  = mouseY;
      }
    }

    OceanBlastWorld world = renderer_.getWorld();

    if (left) {
      if      (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
        pressX_ = leftX;
        pressY_ = leftY;
      }
      else if (action == MotionEvent.ACTION_MOVE) {
        int dx = leftX - pressX_;
        int dy = leftY - pressY_;

        OceanBlastShip ship = world.getShip();

        if      (dy >  8) ship.moveDown();
        else if (dy < -8) ship.moveUp  ();
        else              ship.stopUp  ();

        if      (dx >  8) ship.moveRight();
        else if (dx < -8) ship.moveLeft ();
      }
      else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
        int dx = leftX - pressX_;

        if (dx < 4 && world.isGameOver())
          world.newGame();

        //renderer_.stopShipUp();

        OceanBlastShip ship = world.getShip();

        if      (dx >  64) ship.flipRight();
        else if (dx < -64) ship.flipLeft ();
      }
    }
    if (right) {
      if      (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
        OceanBlastShip ship = world.getShip();

        ship.fire();
      }
      else if (action == MotionEvent.ACTION_MOVE) {
      }
      else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
      }
    }

    return true;
  }

  private OceanBlastRenderer renderer_;

  private int pressX_;
  private int pressY_;
}
