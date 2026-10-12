package com.practiceqa.novashop.base;

import com.practiceqa.novashop.driver.Target;

/** A test that runs on a phone: Android, iOS or phone emulation, depending on `platform`. */
public abstract class MobileTest extends UiTest {

  @Override
  protected Target target() {
    return Target.MOBILE;
  }
}
