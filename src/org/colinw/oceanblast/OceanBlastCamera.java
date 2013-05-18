package org.colinw.oceanblast;

class OceanBlastCamera {
  public OceanBlastCamera(OceanBlastWorld world) {
    world_ = world;

    x_ = 50.0f;
    y_ = world_.getScreenHeight() / 2.0f;
  }

  public float getX() { return x_; }
  public float getY() { return y_; }

  public void update() {
    OceanBlastShip ship = world_.getShip();

    float sx1 = ship.getX();
    float sx2 = sx1 + world_.getWidth();
    float sx3 = sx1 - world_.getWidth();

    float xv  = ship.getXV();

    float dx1 = Math.abs(x_ - sx1);
    float dx2 = Math.abs(x_ - sx2);
    float dx3 = Math.abs(x_ - sx3);

    if (dx1 < dx2 && dx1 < dx3) {
      float new_x;

      if (xv > 0.0f)
        new_x = sx1 + 3.0f;
      else
        new_x = sx1 - 3.0f;

      x_ = (x_ + 0.1f*new_x)/1.1f;
    }
    else if (dx2 < dx3) {
      float new_x;

      if (xv > 0.0f)
        new_x = sx2 + 3.0f;
      else
        new_x = sx2 - 3.0f;

      x_ = (x_ + 0.1f*new_x)/1.1f;

      if (x_ > world_.getWidth())
        x_ = x_ - world_.getWidth();
    }
    else {
      float new_x;

      if (xv > 0.0f)
        new_x = sx3 + 3.0f;
      else
        new_x = sx3 - 3.0f;

      x_ = (x_ + 0.1f*new_x)/1.1f;

      if (x_ < 0.0f)
        x_ = world_.getWidth() + x_;
    }
  }

  private OceanBlastWorld world_;

  private float x_;
  private float y_;
}
