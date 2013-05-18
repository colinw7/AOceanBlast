package org.colinw.oceanblast;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

public class OceanBlastRenderer implements GLSurfaceView.Renderer {
  public OceanBlastRenderer(Context context) {
    context_ = context;
  }

  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    //startTime = System.currentTimeMillis();
    //fpsStartTime = startTime;
    //numFrames = 0;

    // set lighting
    float lightAmbient[] = new float[] { 0.2f, 0.2f, 0.2f, 1 };
    float lightDiffuse[] = new float[] { 1, 1, 1, 1 };
    float lightPos    [] = new float[] { 1, 1, 1, 1 };

    gl.glEnable(GL10.GL_LIGHTING);
    gl.glEnable(GL10.GL_LIGHT0);

    gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
    gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
    gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);

    // set material
    float matAmbient[] = new float[] { 1, 1, 1, 1 };
    float matDiffuse[] = new float[] { 1, 1, 1, 1 };

    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);

    // set options
    //gl.glEnable(GL10.GL_DEPTH_TEST);
    //gl.glDepthFunc(GL10.GL_LEQUAL);
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

    gl.glDisable(GL10.GL_DITHER);

    gl.glEnable(GL10.GL_BLEND);

    gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

    // Enable textures
    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    gl.glEnable(GL10.GL_TEXTURE_2D);

    world_ = new OceanBlastWorld(this);

    int sandTId[] = new int [16];

    sandTId[ 0] = loadTexture(gl, context_, R.drawable.sand_01);
    sandTId[ 1] = loadTexture(gl, context_, R.drawable.sand_02);
    sandTId[ 2] = loadTexture(gl, context_, R.drawable.sand_03);
    sandTId[ 3] = loadTexture(gl, context_, R.drawable.sand_04);
    sandTId[ 4] = loadTexture(gl, context_, R.drawable.sand_05);
    sandTId[ 5] = loadTexture(gl, context_, R.drawable.sand_06);
    sandTId[ 6] = loadTexture(gl, context_, R.drawable.sand_07);
    sandTId[ 7] = loadTexture(gl, context_, R.drawable.sand_08);
    sandTId[ 8] = loadTexture(gl, context_, R.drawable.sand_09);
    sandTId[ 9] = loadTexture(gl, context_, R.drawable.sand_10);
    sandTId[10] = loadTexture(gl, context_, R.drawable.sand_11);
    sandTId[11] = loadTexture(gl, context_, R.drawable.sand_12);
    sandTId[12] = loadTexture(gl, context_, R.drawable.sand_13);
    sandTId[13] = loadTexture(gl, context_, R.drawable.sand_14);
    sandTId[14] = loadTexture(gl, context_, R.drawable.sand_15);
    sandTId[15] = loadTexture(gl, context_, R.drawable.sand_16);

    int water1TId = loadTexture(gl, context_, R.drawable.water_texture_01);
    int water2TId = loadTexture(gl, context_, R.drawable.water_texture_02);
    int shipLTId  = loadTexture(gl, context_, R.drawable.sub_02);
    int shipRTId  = loadTexture(gl, context_, R.drawable.sub_01);

    int squidTId[] = new int [5];

    squidTId[0] = loadTexture(gl, context_, R.drawable.squid_01);
    squidTId[1] = loadTexture(gl, context_, R.drawable.squid_02);
    squidTId[2] = loadTexture(gl, context_, R.drawable.squid_03);
    squidTId[3] = loadTexture(gl, context_, R.drawable.squid_04);
    squidTId[4] = loadTexture(gl, context_, R.drawable.squid_05);

    int shipBulletLTId  = loadTexture(gl, context_, R.drawable.torpedo_l);
    int shipBulletRTId  = loadTexture(gl, context_, R.drawable.torpedo_r);
    int alienBulletLTId = loadTexture(gl, context_, R.drawable.alien_shot_01);
    int alienBulletRTId = alienBulletLTId;
    int starFishTId     = loadTexture(gl, context_, R.drawable.starfish);
    int fish1TId        = loadTexture(gl, context_, R.drawable.fish1);
    int fish2TId        = loadTexture(gl, context_, R.drawable.fish2);
    int fish3TId        = loadTexture(gl, context_, R.drawable.fish3);

    int whaleLTId[] = new int [4];
    int whaleRTId[] = new int [4];

    whaleLTId[0] = loadTexture(gl, context_, R.drawable.whale_01_l);
    whaleLTId[1] = loadTexture(gl, context_, R.drawable.whale_02_l);
    whaleLTId[2] = loadTexture(gl, context_, R.drawable.whale_03_l);
    whaleLTId[3] = loadTexture(gl, context_, R.drawable.whale_04_l);

    whaleRTId[0] = loadTexture(gl, context_, R.drawable.whale_01_r);
    whaleRTId[1] = loadTexture(gl, context_, R.drawable.whale_02_r);
    whaleRTId[2] = loadTexture(gl, context_, R.drawable.whale_03_r);
    whaleRTId[3] = loadTexture(gl, context_, R.drawable.whale_04_r);

    int waterTextureTId = loadTexture(gl, context_, R.drawable.water_texture);

    int gameOverTId = loadTexture(gl, context_, R.drawable.game_over);

    int fontTId = loadTexture(gl, context_, R.drawable.font);

    world_.setBgTexture(water1TId, water2TId, waterTextureTId);
    world_.setSandTextures(sandTId);
    world_.setShipTextures(shipLTId, shipRTId);
    world_.setSquidTextures(squidTId);
    world_.setBulletTextures(shipBulletLTId, shipBulletRTId, alienBulletLTId, alienBulletRTId);
    world_.setStarFishTextures(starFishTId);
    world_.setFishTextures(fish1TId, fish2TId, fish3TId);
    world_.setWhaleTextures(whaleLTId, whaleRTId);
    world_.setGameOverTexture(gameOverTId);
    world_.setFontTexture(fontTId);

    //int scoreTId = world_.createStringBitmap(gl, context_, "Score:");
    //int livesTId = world_.createStringBitmap(gl, context_, "Lives:");

    //scoreText_ = world_.createBillboard(scoreTId, 4);
    //livesText_ = world_.createBillboard(livesTId, 4);

    world_.addObjects();

    soundPool_ = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

    shotSoundId_    = soundPool_.load(context_, R.raw.shot, 1);
    explodeSoundId_ = soundPool_.load(context_, R.raw.explosion, 1);
  }

  public void onSurfaceChanged(GL10 gl, int width, int height) {
    gl.glViewport(0, 0, width, height);

    gl.glMatrixMode(GL10.GL_PROJECTION);

    gl.glLoadIdentity();

    float r = (1.0f*width)/height;

    //GLU.gluPerspective(gl, 45.0f, r, 1, 100f);
    gl.glOrthof(-r, r, -1.0f, 1.0f, -10.0f, 10.0f);

    world_.setGlRange(-r, -1.0f, r, 1.0f);
  }

  public void onDrawFrame(GL10 gl) {
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

    gl.glMatrixMode(GL10.GL_MODELVIEW);

    gl.glLoadIdentity();

    gl.glTranslatef(0.0f, 0.0f, -3.0f);

    //world_.drawBillBoard(gl, -1.4f, 0.7f, 0.1f, scoreText_);
    //world_.drawBillBoard(gl,  1.4f, 0.7f, 0.1f, livesText_);

    world_.update();

    world_.draw(gl);

/*
    // Keep track of number of frames drawn
    numFrames++;

    long fpsElapsed = System.currentTimeMillis() - fpsStartTime;

    if (fpsElapsed > 5 * 1000) { // every 5 seconds
      float fps = (numFrames * 1000.0F) / fpsElapsed;
      Log.d(TAG, "Frames per second: " + fps + " (" + numFrames +
            " frames in " + fpsElapsed + " ms)");
      fpsStartTime = System.currentTimeMillis();
      numFrames = 0;
    }
*/
  }

  public OceanBlastWorld getWorld() {
    return world_;
  }

  public void moveShipUp() {
    OceanBlastShip ship = world_.getShip();

    ship.moveUp();
  }

  public void moveShipDown() {
    OceanBlastShip ship = world_.getShip();

    ship.moveDown();
  }

  public void stopShipUp() {
    OceanBlastShip ship = world_.getShip();

    ship.stopUp();
  }

  public void moveShipRight() {
    OceanBlastShip ship = world_.getShip();

    ship.moveRight();
  }

  public void moveShipLeft() {
    OceanBlastShip ship = world_.getShip();

    ship.moveLeft();
  }

  public void flipShipLeft() {
    OceanBlastShip ship = world_.getShip();

    ship.flipLeft();
  }

  public void flipShipRight() {
    OceanBlastShip ship = world_.getShip();

    ship.flipRight();
  }

  public void shipFire() {
    OceanBlastShip ship = world_.getShip();

    ship.fire();
  }

  public void playShotSound() {
    soundPool_.play(shotSoundId_, 1, 1, 0, 0, 1);
  }

  public void playExplodeSound() {
    soundPool_.play(explodeSoundId_, 1, 1, 0, 0, 1);
  }

  private static int loadTexture(GL10 gl, Context context, int resource) {
    IntBuffer textures = IntBuffer.allocate(1);

    gl.glGenTextures(1, textures);

    int id = textures.get(0);

    gl.glBindTexture(GL10.GL_TEXTURE_2D, id);

    Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resource);

    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

    //int filter = GL10.GL_NEAREST /* GL10.GL_LINEAR */;

    gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
    gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

    bmp.recycle();

    return id;
  }

  private void checkGlError(GL10 gl, String op) {
    int error;

    while ((error = gl.glGetError()) != GL10.GL_NO_ERROR) {
      Log.e(TAG, op + ": glError " + error);

      throw new RuntimeException(op + ": glError " + error);
    }
  }

  private Context context_;

  //private long startTime;
  //private long fpsStartTime;
  //private long numFrames;

  private OceanBlastWorld world_;

  //private OceanBlastBillboard scoreText_;
  //private OceanBlastBillboard livesText_;

  private SoundPool soundPool_;
  private int       shotSoundId_;
  private int       explodeSoundId_;

  private static String TAG = "OceanBlastRenderer";
}
