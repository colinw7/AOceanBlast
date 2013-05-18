package org.colinw.oceanblast;

import javax.microedition.khronos.opengles.GL10;

class OceanBlastBullet implements OceanBlastObject {
  public OceanBlastBullet(OceanBlastWorld world, int textureLId, int textureRId, int scale) {
    world_ = world;

    init();

    lBillboard_ = world_.createBillboard(textureLId, scale);
    rBillboard_ = world_.createBillboard(textureRId, scale);
  }

  public void init() {
    x_ = 50.0f;
    y_ =  5.0f;
    z_ =  0.4f;

    xv_ = 0.1f;
    yv_ = 0.0f;

    dead_ = true;

    life_ = 0;
  }

  public boolean getDead() { return dead_; }

  public void setDead(boolean dead) { dead_ = dead; }

  public boolean isAlive() { return (! dead_ && life_ > 0); }

  public void emit(float x, float y, float xv, float yv) {
    dead_ = false;
    life_ = 100;

    x_  = x;
    y_  = y;
    xv_ = xv;
    yv_ = yv;
  }

  public void draw(GL10 gl) {
    if (dead_) return;

    float x = world_.worldXToGlX(x_);
    float y = world_.worldYToGlY(y_);

    if (xv_ > 0.0)
      rBillboard_.draw(gl, x, y, z_);
    else
      lBillboard_.draw(gl, x, y, z_);

    //-----

    //OceanBlastBBox bbox = getBBox();

    //world_.drawBBox(gl, bbox, z_);
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

    x_ += xv_;

    if      (x_ < 0.0f             ) x_ = world_.getWidth() + x_;
    else if (x_ > world_.getWidth()) x_ = x_ - world_.getWidth();

    y_ += yv_;

    --life_;

    if (life_ < 0) dead_ = true;
  }

  private OceanBlastWorld world_;

  private float   x_;
  private float   y_;
  private float   z_;
  private float   xv_;
  private float   yv_;
  private boolean dead_;
  private int     life_;

  private OceanBlastBillboard lBillboard_;
  private OceanBlastBillboard rBillboard_;
}
