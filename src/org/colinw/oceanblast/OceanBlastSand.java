package org.colinw.oceanblast;

import javax.microedition.khronos.opengles.GL10;

class OceanBlastSand {
  public OceanBlastSand(OceanBlastWorld world, int textureIds[]) {
    world_ = world;

    num_x_ = 16;

    billboards_ = new OceanBlastBillboard [num_x_];

    x_ = new float [num_x_];

    float dx = 100.0f/num_x_;

    for (int i = 0; i < num_x_; ++i) {
      billboards_[i] = world_.createBillboard(textureIds[i], 1.58f);

      x_[i] = i*dx;
    }
  }

  public void draw(GL10 gl) {
    for (int i = 0; i < num_x_; ++i) {
      float x = world_.worldXToGlX(x_[i]);

      if (x < -3.0)
        x = world_.worldXToGlX(x_[i] + 100.0f);

      //float y = world_.worldYToGlY(y_);

      billboards_[i].draw(gl, x, -0.38f, z_);
    }
  }

  public void update() {
  }

  private OceanBlastWorld world_;

  private int   num_x_;
  private float x_[];

  private float z_ = 0.05f;

  private OceanBlastBillboard billboards_[];
}
