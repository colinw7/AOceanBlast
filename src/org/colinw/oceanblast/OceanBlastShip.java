package org.colinw.oceanblast;

import javax.microedition.khronos.opengles.GL10;

class OceanBlastShip implements OceanBlastObject {
  public OceanBlastShip(OceanBlastWorld world, int textureLId, int textureRId) {
    world_ = world;

    //------

    billboardL_ = world_.createBillboard(textureLId, 3);
    billboardR_ = world_.createBillboard(textureRId, 3);

    //------

    bullets_ = new OceanBlastBullet [numBullets_];

    for (int i = 0; i < numBullets_; ++i)
      bullets_[i] = world_.createShipBullet();

    init();
  }

  public void init() {
    x_ = 50.0f;
    y_ =  5.0f;
    z_ =  0.3f;

    xv_ = 0.1f;
    yv_ = 0.0f;

    lives_ = 5;

    dead_ = false;

    for (int i = 0; i < numBullets_; ++i)
      bullets_[i].init();
  }

  public float getX() { return x_; }
  public float getY() { return y_; }

  public float getXV() { return xv_; }
  public float getYV() { return yv_; }

  public int getNumLives() { return lives_; }

  public boolean isDead() { return dead_; }

  public void moveUp  () { if (rotating_) return; yv_ =  0.1f; }
  public void moveDown() { if (rotating_) return; yv_ = -0.1f; }
  public void stopUp  () { if (rotating_) return; yv_ =  0.0f; }

  public void moveLeft() {
    if (rotating_) return;

    float xv = xv_;

    xv_ -= 0.01; if (xv_ < -0.3) xv_ = -0.3f;

    if (xv > 0.0f && xv_ <=  0.05f) { xv_ =  0.05f; /* changeDirection(); */ }
  //if (xv < 0.0f && xv_ >= -0.05f) { xv_ = -0.05f; /* changeDirection(); */ }
  }

  public void moveRight() {
    if (rotating_) return;

    float xv = xv_;

    xv_ += 0.01; if (xv_ > 0.3f) xv_ = 0.3f;

  //if (xv > 0.0f && xv_ <=  0.05f) { xv_ =  0.05f; /* changeDirection(); */ }
    if (xv < 0.0f && xv_ >= -0.05f) { xv_ = -0.05f; /* changeDirection(); */ }
  }

  public void flipLeft () { if (rotating_) return; if (xv_ >= 0.0f) {xv_ =  0.1f; changeDirection(); } }
  public void flipRight() { if (rotating_) return; if (xv_ <= 0.0f) {xv_ = -0.1f; changeDirection(); } }

  public OceanBlastBBox getBBox() {
    float x1 = world_.worldXToGlX(x_ - 0.5f);
    float y1 = world_.worldYToGlY(y_ - 0.5f);
    float x2 = world_.worldXToGlX(x_ + 0.5f);
    float y2 = world_.worldYToGlY(y_ + 0.5f);

    return new OceanBlastBBox(x1, y1, x2, y2);
  }

  public void fire() {
    if (dead_ || rotating_) return;

    for (int i = 0; i < numBullets_; ++i) {
      if (bullets_[i].isAlive()) continue;

      float xv = (xv_ > 0 ? world_.random(0.3f, 0.6f) : world_.random(-0.6f, -0.3f));
      float yv = 0.0f;

      float y = y_ - world_.random(0.4f, 0.6f);

      if (xv_ > 0)
        bullets_[i].emit(x_ + 1.7f, y, xv, yv);
      else
        bullets_[i].emit(x_ - 1.7f, y, xv, yv);

      world_.playShotSound();

      break;
    }
  }

  public void die() {
    if (lives_ > 0)
      --lives_;
    else {
      dead_ = true;

      world_.setGameOver(true);
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

    OceanBlastBillboard billboard = (xv_ > 0.0f ? billboardR_ : billboardL_);

    float ya = 0.0f;

    if (rotating_)
      ya = 180 - rotate_count_;

    billboard.ya = ya;

    float d = 0.0f;

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

    //-----

    for (int i = 0; i < numBullets_; ++i)
      bullets_[i].draw(gl);
  }

  public void update() {
    if (dead_) return;

    if (rotating_) {
      rotate_count_ -= 10;

      if (rotate_count_ <= 0) {
        xv_ = -xv_;

        //x_ = 100.0f - x_;

        rotating_ = false;
      }
    }
    else {
      x_ += xv_;

      if      (x_ < 0.0f             ) x_ = world_.getWidth() + x_;
      else if (x_ > world_.getWidth()) x_ = x_ - world_.getWidth();

      y_ += yv_;

      if      (y_ < 0.0f              ) y_ = 0.0f;
      else if (y_ > world_.getHeight()) y_ = world_.getHeight();
    }

    for (int i = 0; i < numBullets_; ++i) {
      OceanBlastBullet bullet = bullets_[i];

      if (bullet.getDead()) continue;

      bullet.update();

      for (int j = 0; j < world_.getNumAliens(); ++j) {
        OceanBlastSquid alien = world_.getAlien(j);

        if (alien.isDead()) continue;

        if (OceanBlastWorld.overlap(bullet, alien)) {
          alien.explode();

          bullet.setDead(true);

          break;
        }
      }
    }
  }

  void changeDirection() {
    rotating_     = true;
    rotate_count_ = 180;
  }

  private OceanBlastWorld world_;

  private float   x_;
  private float   y_;
  private float   z_;
  private float   xv_;
  private float   yv_;
  private boolean rotating_;
  private int     rotate_count_;

  private OceanBlastBillboard billboardL_;
  private OceanBlastBillboard billboardR_;

  private int              numBullets_ = 5;
  private OceanBlastBullet bullets_[];

  private int     lives_;
  private boolean dead_;
}
