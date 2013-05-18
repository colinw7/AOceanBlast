package org.colinw.oceanblast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class OceanBlastFont {
  public OceanBlastFont(OceanBlastWorld world, int tId) {
    world_ = world;

    textureId = tId;

    float x_size = 0.05f;
    float y_size = 0.08f;

    float vertices[] = {
      -x_size, -y_size,  0,  x_size, -y_size,  0,
      -x_size,  y_size,  0,  x_size,  y_size,  0,
    };

    ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
    vbb.order(ByteOrder.nativeOrder());

    vertexBuffer = vbb.asFloatBuffer();
    vertexBuffer.put(vertices);
    vertexBuffer.position(0);

    int tx[] = { 0, 6500, 13000, 19500, 26000, 32500, 39000, 45500, 52000, 58500, 65000 };
    int ty   = 10000;

    textureBuffers = new IntBuffer [10];

    for (int i = 0; i < 10; ++i) {
      int tx1 = tx[i];
      int tx2 = tx[i + 1];

      int texCoords[] = { tx1, ty, tx2, ty, tx1, 0, tx2, 0 };

      ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
      tbb.order(ByteOrder.nativeOrder());

      textureBuffers[i] = tbb.asIntBuffer();
      textureBuffers[i].put(texCoords);
      textureBuffers[i].position(0);
    }
  }

  public void drawNumber(GL10 gl, float x, float y, float z, int i) {
    final int digits[] = new int [20];

    digits[0] = i % 10;

    int pos = 1;

    while (i >= 10) {
      i /= 10;

      digits[pos] = i % 10;

      ++pos;
    }

    for (int j = pos - 1; j >= 0; --j) {
      drawDigit(gl, x, y, z, digits[j]);

      x += 0.1f;
    }
  }

  public void drawDigit(GL10 gl, float x, float y, float z, int i) {
    gl.glPushMatrix();

    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

    gl.glTranslatef(x, y, z);

    gl.glVertexPointer  (3, GL10.GL_FLOAT, 0, vertexBuffer     );
    gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, textureBuffers[i]);

    gl.glColor4f(1, 1, 1, 1);

    gl.glNormal3f(0, 0, 1);
    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

    gl.glNormal3f(0, 0, -1);
    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);

    gl.glPopMatrix();
  }

  OceanBlastWorld world_;
  int             textureId;
  FloatBuffer     vertexBuffer;
  IntBuffer       textureBuffers[];
}
