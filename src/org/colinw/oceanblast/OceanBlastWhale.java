package org.colinw.oceanblast;

import javax.microedition.khronos.opengles.GL10;

class OceanBlastWhale implements OceanBlastObject {
  public OceanBlastWhale(OceanBlastWorld world, int lTId[], int rTId[], float x, float y) {
    world_ = world;

    lbillboard_ = new OceanBlastBillboard [4];
    rbillboard_ = new OceanBlastBillboard [4];

    for (int i = 0; i < 4; ++i) {
      lbillboard_[i] = world_.createBillboard(lTId[i], 2);
      rbillboard_[i] = world_.createBillboard(rTId[i], 2);
    }

    init(x, y);
  }

  public void init(float x, float y) {
    x_ = x;
    y_ = y;
    z_ = 0.1f;

    xv_ = 0.03f;
    yv_ = 0.01f;

    tnum_ = 0;

    ticks_ = 0;
  }

  public void setPosition(float x, float y) { x_ = x; y_ = y; }

  public void draw(GL10 gl) {
    draw1(gl, false);
  }

  public void drawShadow(GL10 gl) {
    draw1(gl, true);
  }

  private void draw1(GL10 gl, boolean shadow) {
    float x = world_.worldXToGlX(x_);
    float y = world_.worldYToGlY(y_);

    float d = 0.0f;

    OceanBlastBillboard billboard;

    if (xv_ > 0) {
      if (tnum_ > 3)
        billboard = rbillboard_[7 - tnum_];
      else
        billboard = rbillboard_[tnum_];
    }
    else {
      if (tnum_ > 3)
        billboard = lbillboard_[7 - tnum_];
      else
        billboard = lbillboard_[tnum_];
    }

    if (shadow) {
      billboard.alpha  = 0.5f;
      billboard.shadow = true;

      d = 0.02f;
    }
    else {
      billboard.alpha  = 1.0f;
      billboard.shadow = false;
    }

    billboard.draw(gl, x + d, y - d, z_);
  }

  public void update() {
    // move
    x_ += xv_;

    if      (x_ < 0.0f             ) x_ = world_.getWidth() + x_;
    else if (x_ > world_.getWidth()) x_ = x_ - world_.getWidth();

    y_ += yv_;

    if      (y_ < 0.0f              ) yv_ = -yv_;
    else if (y_ > world_.getHeight()) yv_ = -yv_;

    ++ticks_;

    if (ticks_ > 8) {
      ++tnum_;

      if (tnum_ > 7)
        tnum_ = 0;

      float r = world_.random(0.0f, 100.0f);

      if (r > 95.0f) {
        xv_ = -xv_;

        tnum_ = 0;
      }

      ticks_ = 0;
    }
  }

  public OceanBlastBBox getBBox() {
    float x1 = world_.worldXToGlX(x_ - 0.5f);
    float y1 = world_.worldYToGlY(y_ - 0.5f);
    float x2 = world_.worldXToGlX(x_ + 0.5f);
    float y2 = world_.worldYToGlY(y_ + 0.5f);

    return new OceanBlastBBox(x1, y1, x2, y2);
  }

  private OceanBlastWorld world_;

  private float x_;
  private float y_;
  private float z_;

  private float xv_;
  private float yv_;

  private int tnum_;
  private int ticks_;

  private OceanBlastBillboard lbillboard_[];
  private OceanBlastBillboard rbillboard_[];
}
