package com.practiceqa.novashop.driver;

import com.practiceqa.novashop.config.Config;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * The one place that knows how to start a session. Desktop browsers, Android,
 * iOS and phone emulation all come back as a WebDriver, which is why the same
 * page objects serve the web and the mobile suites.
 */
public final class DriverFactory {
  private static final Dimension DESKTOP_WINDOW = new Dimension(1366, 900);
  private static final Map<String, Object> NO_PASSWORD_POPUPS = Map.of(
      "credentials_enable_service", false,
      "profile.password_manager_enabled", false,
      "profile.password_manager_leak_detection", false);

  private DriverFactory() {}

  public static WebDriver create(Target target) {
    return target == Target.WEB ? web() : mobile();
  }

  // ---- Web ----

  private static WebDriver web() {
    Capabilities options = switch (Config.browser()) {
      case "chrome" -> chromeOptions();
      case "firefox" -> firefoxOptions();
      case "edge" -> edgeOptions();
      default -> throw new IllegalArgumentException("Unsupported browser: " + Config.browser() + " (use chrome, firefox or edge)");
    };
    WebDriver driver = Config.gridUrl().isBlank() ? local(options) : new RemoteWebDriver(url(Config.gridUrl()), options);
    driver.manage().window().setSize(DESKTOP_WINDOW);
    return driver;
  }

  private static ChromeOptions chromeOptions() {
    ChromeOptions options = new ChromeOptions();
    if (Config.headless()) options.addArguments("--headless=new");
    // Chrome's password pop-ups steal focus after a login, so later typing would be lost.
    options.setExperimentalOption("prefs", NO_PASSWORD_POPUPS);
    return options;
  }

  private static FirefoxOptions firefoxOptions() {
    FirefoxOptions options = new FirefoxOptions();
    if (Config.headless()) options.addArguments("-headless");
    return options;
  }

  private static EdgeOptions edgeOptions() {
    EdgeOptions options = new EdgeOptions();
    if (Config.headless()) options.addArguments("--headless=new");
    return options;
  }

  private static WebDriver local(Capabilities options) {
    if (options instanceof ChromeOptions chrome) return new ChromeDriver(chrome);
    if (options instanceof FirefoxOptions firefox) return new FirefoxDriver(firefox);
    return new EdgeDriver((EdgeOptions) options);
  }

  // ---- Mobile ----

  private static WebDriver mobile() {
    return switch (Config.platform()) {
      case "android" -> android();
      case "ios" -> ios();
      case "emulation" -> emulation();
      default -> throw new IllegalArgumentException("Unsupported platform: " + Config.platform() + " (use android, ios or emulation)");
    };
  }

  /** Chrome on an Android device or emulator. */
  private static WebDriver android() {
    UiAutomator2Options options = new UiAutomator2Options()
        .setDeviceName(Config.deviceName())
        .withBrowserName("Chrome")
        .setNewCommandTimeout(Duration.ofSeconds(120));
    options.setCapability("goog:chromeOptions", Map.of("args", new String[] {"--no-first-run", "--disable-fre"}));
    options.setCapability("appium:chromedriverAutodownload", true);
    if (!Config.deviceUdid().isBlank()) options.setUdid(Config.deviceUdid());
    if (!Config.platformVersion().isBlank()) options.setPlatformVersion(Config.platformVersion());
    return new AndroidDriver(url(Config.appiumUrl()), options);
  }

  /** Safari on an iPhone or simulator. */
  private static WebDriver ios() {
    XCUITestOptions options = new XCUITestOptions()
        .setDeviceName(Config.deviceName())
        .withBrowserName("Safari")
        .setNewCommandTimeout(Duration.ofSeconds(120));
    if (!Config.deviceUdid().isBlank()) options.setUdid(Config.deviceUdid());
    if (!Config.platformVersion().isBlank()) options.setPlatformVersion(Config.platformVersion());
    return new IOSDriver(url(Config.appiumUrl()), options);
  }

  /** Desktop Chrome with a phone's screen, touch events and user agent. Needs no Appium or device. */
  private static WebDriver emulation() {
    ChromeOptions options = chromeOptions();
    options.setExperimentalOption("mobileEmulation", Map.of("deviceName", Config.emulationDevice()));
    return new ChromeDriver(options);
  }

  private static URL url(String value) {
    try {
      return URI.create(value).toURL();
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Not a valid URL: " + value, e);
    }
  }
}
