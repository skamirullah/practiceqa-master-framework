package com.practiceqa.novashop.base;

import com.practiceqa.novashop.data.Shopper;
import com.practiceqa.novashop.driver.DriverManager;
import com.practiceqa.novashop.driver.Target;
import com.practiceqa.novashop.pages.LoginPage;
import com.practiceqa.novashop.pages.ProductsPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base for every test that drives the UI. Subclasses only say what they drive
 * (a desktop browser or a phone); starting and ending the session is handled here.
 */
public abstract class UiTest {

  protected abstract Target target();

  @BeforeMethod(alwaysRun = true)
  public void startSession() {
    DriverManager.start(target());
  }

  @AfterMethod(alwaysRun = true)
  public void endSession() {
    DriverManager.quit();
  }

  protected LoginPage loginPage() {
    return new LoginPage().open();
  }

  /** Most tests start signed in, on the product list. */
  protected ProductsPage signedIn() {
    return loginPage().signIn(Shopper.standard());
  }
}
