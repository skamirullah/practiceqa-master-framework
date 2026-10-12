package com.practiceqa.novashop.base;

import com.practiceqa.novashop.driver.Target;

/** A test that runs in a desktop browser. */
public abstract class WebTest extends UiTest {

  @Override
  protected Target target() {
    return Target.WEB;
  }
}
