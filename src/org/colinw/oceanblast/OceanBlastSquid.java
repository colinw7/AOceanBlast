package org.colinw.oceanblast;

import javax.microedition.khronos.opengles.GL10;

class OceanBlastSquid implements OceanBlastObject {
  public OceanBlastSquid(OceanBlastWorld world, int tId[], float x, float y, float s, float xv, float yv) {
    world_ = world;

    billboards_ = new OceanBlastBillboard [5];

    for (int i = 0; i < 5; ++i)
      billboards_[i] = world_.createBillboard(tId[i], s);

    //------

    bullets_ = new OceanBlastBullet [numBullets_];

    for (int i = 0; i < numBullets_; ++i)
      bullets_[i] = world_.createSquidBullet();

    //------

    init(x, y, xv, yv);
  }

  public void init(float x, float y, float xv, float yv) {
    x_ = x;
    y_ = y;
    z_ = 0.2f;

    xv_ = xv;
    yv_ = yv;

    dead_  = false;
    dying_ = 0;

    starFish_ = null;
    captured_ = false;

    animTicks_ = 0;
    fireTicks_ = 0;

    billboardNum_ = 0;

    for (int i = 0; i < numBullets_; ++i)
      bullets_[i].init();
  }

  public void fire() {
    if (dead_ || captured_) return;

    OceanBlastShip ship = world_.getShip();

    OceanBlastBBox shipBBox = ship.getBBox();
    OceanBlastBBox bbox     = getBBox();

    float dx = shipBBox.getMidX() - bbox.getMidX();
    float dy = shipBBox.getMidY() - bbox.getMidY();

    if (Math.abs(dx) > 10.0f) return;

    float a = (float) Math.atan2(dy, dx);

    float xv = (float) (0.1*Math.cos(a));
    float yv = (float) (0.1*Math.sin(a));

    for (int i = 0; i < numBullets_; ++i) {
      if (bullets_[i].isAlive()) continue;

      if (xv_ > 0)
        bullets_[i].emit(x_ + 1.5f, y_, xv, yv);
      else
        bullets_[i].emit(x_ - 1.5f, y_, xv, yv);

      break;
    }
  }

  public void draw(GL10 gl) {
    draw1(gl, false);
  }

  public void drawShadow(GL10 gl) {
    draw1(gl, true);
  }

  private void draw1(GL10 gl, boolean shadow) {
    if (dead_) return;

    float x = world_.worldXToGlX(x_);
    float y = world_.worldYToGlY(y_);

    if (dying_ > 0) {
      float s = (dying_ + 1)/100.0f;

      billboards_[billboardNum_].scale = s;
    }

    float d = 0.0f;

    if (shadow) {
      billboards_[billboardNum_].alpha  = 0.5f;
      billboards_[billboardNum_].shadow = true;

      d = 0.02f;
    }
    else {
      billboards_[billboardNum_].alpha  = 1.0f;
      billboards_[billboardNum_].shadow = false;
    }

    billboards_[billboardNum_].draw(gl, x + d, y - d, z_);

    //-----

    for (int i = 0; i < numBullets_; ++i)
      bullets_[i].draw(gl);
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

    // finish dying
    if (dying_ > 0) {
      --dying_;

      if (dying_ == 0)
        dead_ = true;
    }
    else {
      // move
      x_ += xv_;

      if      (x_ < 0.0f             ) x_ = world_.getWidth() + x_;
      else if (x_ > world_.getWidth()) x_ = x_ - world_.getWidth();

      y_ += yv_;

      // no human so just bounce
      if (! captured_) {
        if      (y_ < 0.0f              ) yv_ = -yv_;
        else if (y_ > world_.getHeight()) yv_ = -yv_;
      }
      else {
        // escaped so kill human and hunt again
        if (y_ > world_.getHeight()) {
          starFish_.setDead(true);

          captured_ = false;
          starFish_    = null;

          xv_ = world_.random(-0.1f, 0.1f);
          yv_ = world_.random(-0.01f, 0.1f);
        }
      }

      // search for human
      if (! captured_) {
        // search for uncaptured human in range
        if (starFish_ == null) {
          OceanBlastBBox bbox = getBBox();

          float           minDist  = 0;
          OceanBlastStarFish minHuman = null;

          for (int i = 0; i < world_.getNumHumans(); ++i) {
            OceanBlastStarFish human = world_.getHuman(i);

            if (human.isDead() || human.isTargetted()) continue;

            float dist = OceanBlastBBox.distance(bbox, human.getBBox());

            if (minHuman == null || dist < minDist) {
              minDist  = dist;
              minHuman = human;
            }
          }

          if (minHuman != null && minDist < 10.0f) {
            starFish_ = minHuman;

            starFish_.setTargetted(true);
          }
        }

        // head for human
        if (starFish_ != null) {
          OceanBlastBBox humanBBox = starFish_.getBBox();
          OceanBlastBBox bbox      = getBBox();

          float dx = humanBBox.getMidX() - bbox.getMidX();
          float dy = humanBBox.getMidY() - bbox.getMidY();

          if (Math.abs(dx) > 10.0f) return;

          float a = (float) Math.atan2(dy, dx);

          xv_ = (float) (0.1*Math.cos(a));
          yv_ = (float) (0.1*Math.sin(a));

          //float hx = human_.getX();
          //float hy = human_.getY();

          //float dx = hx - x_;
          //float dy = hy - y_;

          //if (dx < 0) { if (xv_ > 0) xv_ = -xv_; } else if (dx > 0) { if (xv_ < 0) xv_ = -xv_; }
          //if (dy < 0) { if (yv_ > 0) yv_ = -yv_; } else if (dy > 0) { if (yv_ < 0) yv_ = -yv_; }

          // if at human then capture and head up
          if (Math.abs(dx) <= Math.abs(xv_) && Math.abs(dy) <= Math.abs(yv_)) {
            captured_ = true;

            xv_ = 0;
            yv_ = world_.random(0.01f, 0.04f);
          }
        }
      }
      // move captured human to current position
      else {
        starFish_.setPosition(x_, y_);
      }

      // update animation
      ++animTicks_;

      if (animTicks_ >= 20) {
        ++billboardNum_;

        if (billboardNum_ >= 5)
          billboardNum_ = 0;

        animTicks_ = 0;
      }

      // update aim and fire
      ++fireTicks_;

      if (fireTicks_ > 100) {
        fire();

        fireTicks_ = 0;
      }
    }

    // update bullets
    OceanBlastShip ship = world_.getShip();

    if (! ship.isDead()) {
      for (int i = 0; i < numBullets_; ++i) {
        OceanBlastBullet bullet = bullets_[i];

        if (bullet.getDead()) continue;

        bullet.update();

        if (OceanBlastWorld.overlap(bullet, ship)) {
          ship.die();

          bullet.setDead(true);
        }
      }
    }
  }

  public boolean isDead() {
    return dead_ || (dying_ > 0);
  }

  public void explode() {
    if (! dead_) {
      if (starFish_ != null) {
        starFish_.setTargetted(false);

        starFish_.setFalling(true);
      }

      dying_ = 100;

      xv_ = 0.0f;
      yv_ = 0.0f;

      world_.playExplodeSound();
    }
  }

  private OceanBlastWorld world_;

  private float x_;
  private float y_;
  private float z_;
  private float xv_;
  private float yv_;

  private int animTicks_;
  private int fireTicks_;

  private int                 billboardNum_;
  private OceanBlastBillboard billboards_[];

  private int              numBullets_ = 2;
  private OceanBlastBullet bullets_[];

  private boolean dead_;
  private int     dying_;

  private OceanBlastStarFish starFish_;
  private boolean            captured_;
}
