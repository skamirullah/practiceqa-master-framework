package com.practiceqa.novashop.pages;

public class OrderPage extends BasePage {

  /** Placing an order takes a couple of seconds; this waits for the confirmation to appear. */
  public boolean isConfirmed() {
    return visible(testId("order-success")).isDisplayed();
  }

  public String orderId() {
    return text(testId("order-id"));
  }
}
