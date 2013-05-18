package org.colinw.oceanblast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

class OceanBlastBillboard {
  OceanBlastBillboard(int tId, float s) {
    textureId = tId;

    scale = 1.0f;
    alpha = 1.0f;

    shadow = false;

    xa = 0.0f;
    ya = 0.0f;
    za = 0.0f;

    float size = 1.0f / s;

    float vertices[] = {
      -size, -size,  0,  size, -size,  0,
      -size,  size,  0,  size,  size,  0,
    };

    int one = 65536;

    int texCoords[] = {
      0, one, one, one, 0, 0, one, 0,
    };

    ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
    vbb.order(ByteOrder.nativeOrder());
    vertexBuffer = vbb.asFloatBuffer();
    vertexBuffer.put(vertices);
    vertexBuffer.position(0);

    ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
    tbb.order(ByteOrder.nativeOrder());
    textureBuffer = tbb.asIntBuffer();
    textureBuffer.put(texCoords);
    textureBuffer.position(0);
  }

  public void draw(GL10 gl, float x, float y, float z) {
    gl.glPushMatrix();

    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

    gl.glTranslatef(x, y, z);

    gl.glScalef(scale, scale, 1.0f);

    if (xa != 0.0f) gl.glRotatef(xa, 1, 0, 0);
    if (ya != 0.0f) gl.glRotatef(ya, 0, 1, 0);
    if (za != 0.0f) gl.glRotatef(za, 0, 0, 1);

    boolean light = true;

    if (! shadow) {
      gl.glColor4f(alpha, alpha, alpha, alpha);

      if (alpha < 0.9f) {
        gl.glDisable(GL10.GL_LIGHTING);

        light = false;
      }
    }
    else {
      gl.glColor4f(0, 0, 0, alpha);

      gl.glDisable(GL10.GL_LIGHTING);

      light = false;
    }

    gl.glVertexPointer  (3, GL10.GL_FLOAT, 0, vertexBuffer );
    gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, textureBuffer);

    gl.glNormal3f(0, 0, 1);

    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

    //gl.glNormal3f(0, 0, -1);
    //gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);

    if (! light)
      gl.glEnable(GL10.GL_LIGHTING);

    gl.glPopMatrix();
  }

  int         textureId;
  FloatBuffer vertexBuffer;
  IntBuffer   textureBuffer;
  float       scale;
  float       alpha;
  boolean     shadow;
  float       xa;
  float       ya;
  float       za;
}
