package org.colinw.oceanblast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLUtils;

class OceanBlastWorld {
  public OceanBlastWorld(OceanBlastRenderer renderer) {
    renderer_ = renderer;

    width_  = 100.0f;
    height_ = 10.0f;

    gl_xmin_ = -1.0f;
    gl_ymin_ = -1.0f;
    gl_xmax_ =  1.0f;
    gl_ymax_ =  1.0f;

    screen_width_  = 10.0f;
    screen_height_ = 10.0f;

    random_ = new Random(/* seed */);

    gameOver_    = false;
    aliveSquids_ = 10;
    level_       = 1;

    camera_ = new OceanBlastCamera(this);
  }

  public void setGlRange(float xmin, float ymin, float xmax, float ymax) {
    gl_xmin_ = xmin;
    gl_ymin_ = ymin;
    gl_xmax_ = xmax;
    gl_ymax_ = ymax;

    screen_width_ = 5.0f*(gl_xmax_ - gl_xmin_);
  }

  public void setBgTexture(int bg1TId, int bg2TId, int waterTId) {
    bg1TId_   = bg1TId;
    bg2TId_   = bg2TId;
    waterTId_ = waterTId;
  }

  public void setGameOverTexture(int gameOverTId) {
    gameOverTId_ = gameOverTId;
  }

  public void setSandTextures(int sandTId[]) {
    sandTId_ = sandTId;
  }

  public void setShipTextures(int shipLTId, int shipRTId) {
    shipLTId_ = shipLTId;
    shipRTId_ = shipRTId;
  }

  public void setSquidTextures(int squidTId[]) {
    squidTId_ = squidTId;
  }

  public void setBulletTextures(int shipBulletLTId, int shipBulletRTId, int squidBulletRTId, int squidBulletLTId) {
    shipBulletLTId_  = shipBulletLTId;
    shipBulletRTid_  = shipBulletRTId;
    squidBulletLTId_ = squidBulletLTId;
    squidBulletRTid_ = squidBulletRTId;
  }

  public void setStarFishTextures(int starFishTId) {
    starFishTId_ = starFishTId;
  }

  public void setFishTextures(int fish1TId, int fish2TId, int fish3TId) {
    fishTId_ = new int [3];

    fishTId_[0] = fish1TId;
    fishTId_[1] = fish2TId;
    fishTId_[2] = fish3TId;
  }

  public void setWhaleTextures(int whaleLTId[], int whaleRTId[]) {
    whaleLTId_ = whaleLTId;
    whaleRTId_ = whaleRTId;
  }

  public void setFontTexture(int fontTId) {
    fontTId_ = fontTId;
  }

  public void setGameOver(boolean gameOver) {
    gameOver_ = gameOver;
  }

  public boolean isGameOver() {
    return gameOver_;
  }

  public void addObjects() {
    bg1Board_ = createBillboard(bg1TId_, 1);
    bg2Board_ = createBillboard(bg2TId_, 1);

    waterBoard_ = createBillboard(waterTId_, 1);

    waterBoard_.alpha = 0.3f;

    ship_ = new OceanBlastShip(this, shipLTId_, shipRTId_);

    sand_ = new OceanBlastSand(this, sandTId_);

    squids_ = new OceanBlastSquid [numSquids_];

    for (int i = 0; i < numSquids_; ++i) {
      float x  = random(0.0f, 100.0f);
      float y  = random(2.5f, 7.5f);
      float xv = random(-0.1f, 0.1f);
      float yv = random(0.0f, 0.1f);
      float s  = random(4.0f, 6.0f);

      squids_[i] = new OceanBlastSquid(this, squidTId_, x, y, s, xv, yv);
    }

    starFishes_ = new OceanBlastStarFish [numStarFishes_];

    for (int i = 0; i < numStarFishes_; ++i) {
      float x = random(0.0f, 100.0f);
      float y = random(0.5f, 1.2f);

      starFishes_[i] = new OceanBlastStarFish(this, starFishTId_, x, y);
    }

    fishes_ = new OceanBlastFish [numFishes_];

    for (int i = 0; i < numFishes_; ++i) {
      float x = random(0.0f, 100.0f);
      float y = random(3.0f, 10.0f);

      int f = random(0, 2);

      fishes_[i] = new OceanBlastFish(this, fishTId_[f], x, y);
    }

    float x = random(0.0f, 100.0f);
    float y = random(3.0f, 10.0f);

    whale_ = new OceanBlastWhale(this, whaleLTId_, whaleRTId_, x, y);

    squidBillBoard_    = createBillboard(squidTId_[0], 10.0f);
    shipBillBoard_     = createBillboard(shipLTId_   , 10.0f);
    starFishBillBoard_ = createBillboard(starFishTId_, 10.0f);

    gameOverBillBoard_ = createBillboard(gameOverTId_, 5.0f);

    font_ = new OceanBlastFont(this, fontTId_);
  }

  public void newGame() {
    ship_.init();

    for (int i = 0; i < numSquids_; ++i) {
      float x  = random(0.0f, 100.0f);
      float y  = random(2.5f, 7.5f);
      float xv = random(-0.1f*level_, 0.1f*level_);
      float yv = random(0.0f, 0.1f);

      squids_[i].init(x, y, xv, yv);
    }

    for (int i = 0; i < numStarFishes_; ++i) {
      float x = random(0.0f, 100.0f);
      float y = random(0.5f, 1.2f);

      starFishes_[i].init(x, y);
    }

    for (int i = 0; i < numFishes_; ++i) {
      float x = random(0.0f, 100.0f);
      float y = random(3.0f, 10.0f);

      fishes_[i].init(x, y);
    }

    float x = random(0.0f, 100.0f);
    float y = random(3.0f, 10.0f);

    whale_.init(x, y);

    gameOver_    = false;
    aliveSquids_ = 10;
    level_       = 1;
  }

  public int getLevel() {
    return level_;
  }

  public void nextLevel() {
    ++level_;

    for (int i = 0; i < numSquids_; ++i) {
      float x  = random(0.0f, 100.0f);
      float y  = random(2.5f, 7.5f);
      float xv = random(-0.1f, 0.1f);
      float yv = random(0.0f, 0.1f);

      squids_[i].init(x, y, xv, yv);
    }

    aliveSquids_ = 10;
  }

  public OceanBlastBullet createShipBullet() {
    return new OceanBlastBullet(this, shipBulletLTId_, shipBulletRTid_, 6);
  }

  public OceanBlastBullet createSquidBullet() {
    return new OceanBlastBullet(this, squidBulletLTId_, squidBulletRTid_, 16);
  }

  public OceanBlastShip getShip() {
    return ship_;
  }

  public void update() {
    if (aliveSquids_ == 0)
      nextLevel();

    float cx = camera_.getX();
    float cy = camera_.getY();

    screen_xmin_ = cx - screen_width_ /2.0f;
    screen_ymin_ = cy - screen_height_/2.0f;
    screen_xmax_ = cx + screen_width_ /2.0f;
    screen_ymax_ = cy + screen_height_/2.0f;

    if (screen_xmin_ < 0.0f) {
      screen_xmin_ = width_ + screen_xmin_;
      screen_xmax_ = width_ + screen_xmax_;
    }

    wrap_ = (screen_xmax_ > width_);

    xf_ = (gl_xmax_ - gl_xmin_)/(screen_xmax_ - screen_xmin_);
    yf_ = (gl_ymax_ - gl_ymin_)/(screen_ymax_ - screen_ymin_);

    camera_.update();

    if (! gameOver_)
      ship_.update();

    for (int i = 0; i < numFishes_; ++i)
      fishes_[i].update();

    whale_.update();

    for (int i = 0; i < numStarFishes_; ++i)
      starFishes_[i].update();

    for (int i = 0; i < numSquids_; ++i)
      squids_[i].update();
  }

  public void draw(GL10 gl) {
    //gl_ = gl;

    bg1Board_.draw(gl, -1.0f, 0.0f, 0.02f);
    bg2Board_.draw(gl,  1.0f, 0.0f, 0.02f);
  //bgBoard_ .draw(gl,  2.0f, 0.0f, 0.02f);

    //------

    whale_.drawShadow(gl);

    for (int i = 0; i < numFishes_; ++i)
      fishes_[i].drawShadow(gl);

    for (int i = 0; i < numSquids_; ++i)
      squids_[i].drawShadow(gl);

    ship_.drawShadow(gl);

    //------

    whale_.draw(gl);

    for (int i = 0; i < numFishes_; ++i)
      fishes_[i].draw(gl);

    sand_.draw(gl);

    int numStarFish = 0;

    for (int i = 0; i < numStarFishes_; ++i) {
      if (! starFishes_[i].isDead()) numStarFish++;

      starFishes_[i].draw(gl);
    }

    aliveSquids_ = 0;

    for (int i = 0; i < numSquids_; ++i) {
      if (! squids_[i].isDead()) aliveSquids_++;

      squids_[i].draw(gl);
    }

    ship_.draw(gl);

    //drawBillBoard(gl, 0.0f, 0.0f, 0.02f, waterBoard_);

    for (int i = 0; i < ship_.getNumLives(); ++i)
      shipBillBoard_.draw(gl, -1.5f + i*0.2f, -0.8f, 0.02f);

    font_.drawNumber(gl, 0.0f, -0.8f, 0.02f, numStarFish);

    starFishBillBoard_.draw(gl, 0.25f, -0.8f, 0.02f);

    font_.drawNumber(gl, 1.3f, -0.8f, 0.02f, aliveSquids_);

    squidBillBoard_.draw(gl, 1.55f, -0.8f, 0.02f);

    if (gameOver_)
      gameOverBillBoard_.draw(gl, 0.0f, 0.0f, 0.02f);
  }

  public void drawBBox(GL10 gl, OceanBlastBBox bbox, float z) {
    drawLine(gl, bbox.x1, bbox.y1, bbox.x2, bbox.y1, z);
    drawLine(gl, bbox.x2, bbox.y1, bbox.x2, bbox.y2, z);
    drawLine(gl, bbox.x2, bbox.y2, bbox.x1, bbox.y2, z);
    drawLine(gl, bbox.x1, bbox.y2, bbox.x1, bbox.y1, z);
  }

  public void drawLine(GL10 gl, float x1, float y1, float x2, float y2, float z) {
    gl.glBindTexture(GL10.GL_TEXTURE_2D, blankTId_);

    float vertices[] = { x1, y1, z, x2, y2, z };

    short indices[] = { 0, 1 };

    ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
    vbb.order(ByteOrder.nativeOrder());
    FloatBuffer vertexBuffer = vbb.asFloatBuffer();
    vertexBuffer.put(vertices);
    vertexBuffer.position(0);

    ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
    ibb.order(ByteOrder.nativeOrder());
    ShortBuffer indexBuffer = ibb.asShortBuffer();
    indexBuffer.put(indices);
    indexBuffer.position(0);

    gl.glDisable(GL10.GL_LIGHTING);

    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

    gl.glColor4f(1, 1, 1, 1);

    gl.glDrawElements(GL10.GL_LINES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);

    gl.glEnable(GL10.GL_LIGHTING);
  }

  public OceanBlastBillboard createBillboard(int textureId, float scale) {
    return new OceanBlastBillboard(textureId, scale);
  }

  //public void drawBillBoard(GL10 gl, float x, float y, float z, OceanBlastBillboard board) {
  //  board.draw(gl, x, y, z);
  //}

  public int createStringBitmap(GL10 gl, Context context, String str) {
    // calc text size
    Paint textPaint = new Paint();
    textPaint.setTextSize(48);
    textPaint.setAntiAlias(true);
    textPaint.setARGB(0xff, 0xff, 0xff, 0xff);

    Rect r = new Rect();

    textPaint.getTextBounds(str, 0, str.length(), r);

    int w = r.right  - r.left;
    int h = r.bottom - r.top;

    int s = Math.max(w, h);

    int s1 = 2;

    while (s1 < s) s1 *= 2;

    // create bitmap
    Bitmap bitmap = Bitmap.createBitmap(s1, s1, Bitmap.Config.ARGB_4444);

    // get canvas to draw on bitmap
    Canvas canvas = new Canvas(bitmap);

    bitmap.eraseColor(0);

    // draw the text
    canvas.drawText(str, (s1 - w)/2, (s1 - h)/2, textPaint);
    //canvas.drawText(str, 0, 20, textPaint);

    //Generate one texture pointer...
    int textures[] = new int [1];

    gl.glGenTextures(1, textures, 0);

    //...and bind it to our array
    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

    //Create Nearest Filtered Texture
    //gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    //gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

    //Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
    //gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
    //gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

    //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

    gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
    gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

    //Clean up
    bitmap.recycle();

    return textures[0];
  }

  public void playShotSound() {
    renderer_.playShotSound();
  }

  public void playExplodeSound() {
    renderer_.playExplodeSound();
  }

  public float worldXToGlX(float x) {
    if (wrap_ && x < screen_width_)
      x += width_;

    return xf_*(x - screen_xmin_) + gl_xmin_;
  }

  public float worldYToGlY(float y) {
    return yf_*(y - screen_ymin_) + gl_ymin_;
  }

  public float random(float low, float high) {
    return random_.nextFloat()*(high - low) + low;
  }

  public int random(int low, int high) {
    return random_.nextInt(high - low + 1) + low;
  }

  public float getWidth () { return width_ ; }
  public float getHeight() { return height_; }

  public float getScreenWidth () { return screen_width_ ; }
  public float getScreenHeight() { return screen_height_; }

  public int getNumAliens() { return numSquids_; }

  public OceanBlastSquid getAlien(int i) { return squids_[i]; }

  public int getNumHumans() { return numStarFishes_; }

  public OceanBlastStarFish getHuman(int i) { return starFishes_[i]; }

  public static boolean overlap(OceanBlastObject obj1, OceanBlastObject obj2) {
    OceanBlastBBox bbox1 = obj1.getBBox();
    OceanBlastBBox bbox2 = obj2.getBBox();

    return OceanBlastBBox.overlap(bbox1, bbox2);
  }

  private OceanBlastRenderer renderer_;

  //private GL10 gl_;

  private float width_;
  private float height_;

  int bg1TId_;
  int bg2TId_;
  int waterTId_;

  int sandTId_[];

  int shipLTId_;
  int shipRTId_;

  int squidTId_[];

  int shipBulletLTId_ ;
  int shipBulletRTid_ ;
  int squidBulletLTId_;
  int squidBulletRTid_;

  int starFishTId_;

  int fishTId_[];

  int whaleLTId_[];
  int whaleRTId_[];

  int gameOverTId_;

  int fontTId_;

  int blankTId_;

  OceanBlastCamera camera_;

  OceanBlastBillboard bg1Board_;
  OceanBlastBillboard bg2Board_;
  OceanBlastBillboard waterBoard_;

  OceanBlastShip ship_;
  OceanBlastSand sand_;

  OceanBlastFont font_;

  int numSquids_ = 10;

  OceanBlastSquid squids_[];

  int numStarFishes_ = 10;

  OceanBlastStarFish starFishes_[];

  int numFishes_ = 40;

  OceanBlastFish fishes_[];

  OceanBlastWhale whale_;

  OceanBlastBillboard squidBillBoard_;
  OceanBlastBillboard shipBillBoard_;
  OceanBlastBillboard starFishBillBoard_;

  OceanBlastBillboard gameOverBillBoard_;

  private float gl_xmin_;
  private float gl_ymin_;
  private float gl_xmax_;
  private float gl_ymax_;

  private float screen_width_;
  private float screen_height_;

  private float   screen_xmin_;
  private float   screen_ymin_;
  private float   screen_xmax_;
  private float   screen_ymax_;
  private boolean wrap_;

  private float xf_;
  private float yf_;

  private boolean gameOver_;
  private int     aliveSquids_;

  private int level_;

  private Random random_;
}
