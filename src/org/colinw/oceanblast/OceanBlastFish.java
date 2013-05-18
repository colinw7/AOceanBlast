package org.colinw.oceanblast;

import javax.microedition.khronos.opengles.GL10;

class OceanBlastFish implements OceanBlastObject {
  public OceanBlastFish(OceanBlastWorld world, int tId, float x, float y) {
    world_ = world;

    billboard_ = world_.createBillboard(tId, 12);

    init(x, y);
  }

  void init(float x, float y) {
    x_ = x;
    y_ = y;
    z_ = 0.1f;

    ya_  = world_.random(-60.0f, 60.0f);
    dya_ = 1.0f;
  }

  public void setPosition(float x, float y) { x_ = x; y_ = y; }

  public void update() {
    ya_ += dya_;

    if ((dya_ > 0.0f && ya_ >= 60.0f) || (dya_ < 0.0f && ya_ <= -60.0f))
      dya_ = -dya_;
  }

  public void draw(GL10 gl) {
    draw1(gl, false);
  }

  public void drawShadow(GL10 gl) {
    draw1(gl, true);
  }

  private void draw1(GL10 gl, boolean shadow) {
    float x = world_.worldXToGlX(x_);
    float y = world_.worldYToGlY(y_);

    billboard_.ya = ya_;

    float d = 0.0f;

    if (shadow) {
      billboard_.alpha  = 0.5f;
      billboard_.shadow = true;

      d = 0.02f;
    }
    else {
      billboard_.alpha  = 1.0f;
      billboard_.shadow = false;
    }

    billboard_.draw(gl, x + d, y - d, z_);
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

  private float ya_;
  private float dya_;

  private OceanBlastBillboard billboard_;
}
