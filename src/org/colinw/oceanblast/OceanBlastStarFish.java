package org.colinw.oceanblast;

import javax.microedition.khronos.opengles.GL10;

class OceanBlastStarFish implements OceanBlastObject {
  public OceanBlastStarFish(OceanBlastWorld world, int textureId, float x, float y) {
    world_ = world;

    billboard_ = world_.createBillboard(textureId, 8);

    init(x, y);
  }

  public void init(float x, float y) {
    x_ = x;
    y_ = y;
    z_ = 0.1f;

    yv_ = -0.05f;

    targetted_ = false;
    falling_   = false;
    dead_      = false;
  }

  public float getX() { return x_; }
  public float getY() { return y_; }

  public void setPosition(float x, float y) { x_ = x; y_ = y; }

  public boolean isTargetted() { return targetted_; }

  public void setTargetted(boolean targetted) { targetted_ = targetted; }

  public boolean isFalling() { return falling_; }

  public void setFalling(boolean falling) { falling_ = falling; }

  public boolean isDead() { return dead_; }

  public void setDead(boolean dead) { dead_ = dead; }

  public void draw(GL10 gl) {
    if (dead_) return;

    float x = world_.worldXToGlX(x_);
    float y = world_.worldYToGlY(y_);

    billboard_.draw(gl, x, y, z_);
  }

  public OceanBlastBBox getBBox() {
    float x1 = world_.worldXToGlX(x_ - 0.5f);
    float y1 = world_.worldYToGlY(y_ - 0.5f);
    float x2 = world_.worldXToGlX(x_ + 0.5f);
    float y2 = world_.worldYToGlY(y_ + 0.5f);

    return new OceanBlastBBox(x1, y1, x2, y2);
  }

  public void update() {
    if (dead_) return;

    if (falling_) {
      y_ += yv_;

      if (y_ < 0.1f) {
        y_       = 0.1f;
        yv_      = 0.0f;
        falling_ = false;
      }
    }
  }

  private OceanBlastWorld world_;

  private float x_;
  private float y_;
  private float z_;
  private float yv_;

  private boolean targetted_;
  private boolean falling_;
  private boolean dead_;

  private OceanBlastBillboard billboard_;
}
