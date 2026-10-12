package com.practiceqa.novashop.tests.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.practiceqa.novashop.base.WebTest;
import com.practiceqa.novashop.data.Shopper;
import com.practiceqa.novashop.pages.ProductsPage;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Feature("Web: signing in")
public class LoginTests extends WebTest {

  @Test(groups = "smoke", description = "A shopper signs in and sees the products")
  public void shopperSignsIn() {
    ProductsPage products = signedIn();

    assertThat(products.names()).isNotEmpty();
  }

  @DataProvider
  public Object[][] rejectedLogins() {
    Shopper shopper = Shopper.standard();
    return new Object[][] {
        {"", shopper.password(), "Username is required"},
        {shopper.username(), "", "Password is required"},
        {shopper.username(), "wrong-password", "Username and password do not match any user in this service"},
        {Shopper.lockedOut().username(), shopper.password(), "Sorry, this user has been locked out."},
    };
  }

  @Test(dataProvider = "rejectedLogins", description = "A sign-in that cannot succeed explains why")
  public void rejectedSignInExplainsWhy(String username, String password, String message) {
    assertThat(loginPage().submit(username, password).error()).isEqualTo(message);
  }
}
