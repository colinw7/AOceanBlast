package org.colinw.oceanblast;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class OceanBlastActivity extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    WindowManager.LayoutParams.FLAG_FULLSCREEN);

    view = new OceanBlastView(this);

    setContentView(view);
  }

  @Override
  protected void onPause() {
    super.onPause();

    view.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();

    view.onResume();
  }

  private OceanBlastView view;
}
