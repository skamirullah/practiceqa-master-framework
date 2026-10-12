package com.practiceqa.novashop.tests.mobile;

import static org.assertj.core.api.Assertions.assertThat;

import com.practiceqa.novashop.base.MobileTest;
import com.practiceqa.novashop.data.Card;
import com.practiceqa.novashop.data.Catalogue;
import com.practiceqa.novashop.data.Shopper;
import com.practiceqa.novashop.pages.CartPage;
import com.practiceqa.novashop.pages.OrderPage;
import com.practiceqa.novashop.pages.ProductsPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

/** The same page objects as the web suite, driven on a phone. */
@Feature("Mobile: shopping on a phone")
public class MobileShoppingTests extends MobileTest {

  @Test(groups = "smoke", description = "A shopper signs in on a phone and sees the products")
  public void shopperSignsInOnAPhone() {
    assertThat(signedIn().names()).isNotEmpty();
  }

  @Test(description = "A locked-out account is refused on a phone too")
  public void lockedOutAccountIsRefused() {
    Shopper locked = Shopper.lockedOut();

    assertThat(loginPage().submit(locked.username(), locked.password()).error()).isEqualTo("Sorry, this user has been locked out.");
  }

  @Test(description = "Search works with the on-screen layout of a phone")
  public void searchFindsAProduct() {
    ProductsPage products = signedIn().search("headphones").waitForCount(1);

    assertThat(products.names()).containsExactly("Aurora Wireless Headphones");
  }

  @Test(groups = "smoke", description = "A shopper buys a product on a phone")
  public void buysAProductOnAPhone() {
    Shopper shopper = Shopper.standard();
    CartPage cart = signedIn().addToCart(Catalogue.BEANIE).openCart();
    assertThat(cart.lineCount()).isEqualTo(1);

    OrderPage order = cart.checkout().deliverTo(shopper).payWith(Card.valid(), shopper.fullName()).acceptTerms().placeOrder();

    assertThat(order.isConfirmed()).isTrue();
    assertThat(order.orderId()).matches("NS-\\d+");
  }
}
