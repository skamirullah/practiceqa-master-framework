package com.practiceqa.novashop.driver;

import org.openqa.selenium.WebDriver;

/**
 * Holds one driver session per thread, so web tests can run in parallel and
 * page objects never need a driver passed around between threads.
 */
public final class DriverManager {
  private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

  private DriverManager() {}

  public static void start(Target target) {
    DRIVER.set(DriverFactory.create(target));
  }

  public static WebDriver driver() {
    WebDriver driver = DRIVER.get();
    if (driver == null) throw new IllegalStateException("No driver session has been started on this thread");
    return driver;
  }

  public static boolean isRunning() {
    return DRIVER.get() != null;
  }

  public static void quit() {
    WebDriver driver = DRIVER.get();
    if (driver != null) {
      driver.quit();
      DRIVER.remove();
    }
  }
}
