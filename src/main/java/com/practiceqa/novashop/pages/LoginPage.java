package com.practiceqa.novashop.pages;

import com.practiceqa.novashop.data.Shopper;
import io.qameta.allure.Step;

public class LoginPage extends BasePage {

  @Step("Open Nova Shop")
  public LoginPage open() {
    open("/shop");
    visible(testId("login-button"));
    return this;
  }

  /** Submits the form without expecting to get in; used by negative tests. */
  public LoginPage submit(String username, String password) {
    type(testId("username"), username);
    type(testId("password"), password);
    click(testId("login-button"));
    return this;
  }

  @Step("Sign in as {shopper.username}")
  public ProductsPage signIn(Shopper shopper) {
    submit(shopper.username(), shopper.password());
    waitForUrl("/shop/products");
    return new ProductsPage();
  }

  public String error() {
    return text(testId("login-error"));
  }
}
