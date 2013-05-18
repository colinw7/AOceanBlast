package org.colinw.oceanblast;

class OceanBlastBBox {
  public OceanBlastBBox(float x1_, float y1_, float x2_, float y2_) {
    x1 = x1_;
    y1 = y1_;
    x2 = x2_;
    y2 = y2_;
  }

  public static boolean overlap(OceanBlastBBox bbox1, OceanBlastBBox bbox2) {
    if (bbox1.x2 < bbox2.x1 || bbox1.x1 > bbox2.x2 ||
        bbox1.y2 < bbox2.y1 || bbox1.y1 > bbox2.y2)
      return false;

    return true;
  }

  public static float distance(OceanBlastBBox bbox1, OceanBlastBBox bbox2) {
    float dx = bbox1.getMidX() - bbox2.getMidX();
    float dy = bbox1.getMidY() - bbox2.getMidX();

    return (float) Math.sqrt(dx*dx + dy*dy);
  }

  float getMidX() { return (x1 + x2)/2.0f; }
  float getMidY() { return (y1 + y2)/2.0f; }

  public float x1;
  public float y1;
  public float x2;
  public float y2;
}
