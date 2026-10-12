package com.practiceqa.novashop.pages;

import com.practiceqa.novashop.data.Card;
import com.practiceqa.novashop.data.Shopper;
import io.qameta.allure.Step;
import org.openqa.selenium.support.ui.Select;

public class CheckoutPage extends BasePage {

  @Step("Fill in delivery details")
  public CheckoutPage deliverTo(Shopper shopper) {
    type(testId("first-name"), shopper.firstName());
    type(testId("last-name"), shopper.lastName());
    type(testId("email"), shopper.email());
    type(testId("phone"), shopper.phone());
    type(testId("address"), shopper.address());
    type(testId("city"), shopper.city());
    new Select(clickable(testId("state"))).selectByVisibleText(shopper.state());
    type(testId("zip"), shopper.zip());
    return this;
  }

  @Step("Pay by card")
  public CheckoutPage payWith(Card card, String nameOnCard) {
    type(testId("card-name"), nameOnCard);
    type(testId("card-number"), card.number());
    type(testId("card-expiry"), card.expiry());
    type(testId("card-cvv"), card.cvv());
    return this;
  }

  public CheckoutPage acceptTerms() {
    check(testId("accept-terms"));
    return this;
  }

  @Step("Place the order")
  public OrderPage placeOrder() {
    click(testId("place-order"));
    return new OrderPage();
  }
}
