package com.practiceqa.novashop.pages;

import io.qameta.allure.Step;

public class CartPage extends BasePage {

  @Step("Apply coupon {code}")
  public CartPage applyCoupon(String code) {
    type(testId("coupon-input"), code);
    click(testId("apply-coupon"));
    visible(testId("coupon-message"));
    return this;
  }

  public String couponMessage() {
    return text(testId("coupon-message"));
  }

  public int lineCount() {
    return atLeast(testId("cart-item"), 1).size();
  }

  public double subtotal() {
    return dollars(text(testId("subtotal")));
  }

  public double discount() {
    return dollars(text(testId("discount")));
  }

  public double tax() {
    return dollars(text(testId("tax")));
  }

  public double total() {
    return dollars(text(testId("total")));
  }

  @Step("Go to checkout")
  public CheckoutPage checkout() {
    click(testId("checkout-button"));
    waitForUrl("/shop/checkout");
    return new CheckoutPage();
  }
}
