package com.practiceqa.novashop.tests.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.practiceqa.novashop.base.WebTest;
import com.practiceqa.novashop.data.Card;
import com.practiceqa.novashop.data.Catalogue;
import com.practiceqa.novashop.data.Shopper;
import com.practiceqa.novashop.pages.CartPage;
import com.practiceqa.novashop.pages.OrderPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("Web: checking out")
public class CheckoutTests extends WebTest {

  @Test(groups = "smoke", description = "A shopper buys headphones with a coupon")
  public void buysWithACoupon() {
    Shopper shopper = Shopper.standard();
    CartPage cart = signedIn().search("headphones").waitForCount(1).addToCart(Catalogue.HEADPHONES).openCart()
        .applyCoupon(Catalogue.TEN_PERCENT_OFF);
    assertThat(cart.discount()).isEqualTo(13.00);

    OrderPage order = cart.checkout().deliverTo(shopper).payWith(Card.valid(), shopper.fullName()).acceptTerms().placeOrder();

    assertThat(order.isConfirmed()).isTrue();
    assertThat(order.orderId()).matches("NS-\\d+");
  }

  @Test(description = "An unknown coupon is rejected and nothing is taken off")
  public void unknownCouponIsRejected() {
    CartPage cart = signedIn().addToCart(Catalogue.BEANIE).openCart();
    double before = cart.total();

    cart.applyCoupon("FREESTUFF");

    assertThat(cart.couponMessage()).contains("not a valid coupon");
    assertThat(cart.total()).isEqualTo(before);
  }
}
