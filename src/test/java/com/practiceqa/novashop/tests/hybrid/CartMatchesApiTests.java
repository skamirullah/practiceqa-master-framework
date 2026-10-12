package com.practiceqa.novashop.tests.hybrid;

import static org.assertj.core.api.Assertions.assertThat;

import com.practiceqa.novashop.api.CartItem;
import com.practiceqa.novashop.api.Quote;
import com.practiceqa.novashop.api.ShopApi;
import com.practiceqa.novashop.base.WebTest;
import com.practiceqa.novashop.data.Catalogue;
import com.practiceqa.novashop.pages.CartPage;
import io.qameta.allure.Feature;
import java.util.List;
import org.testng.annotations.Test;

/**
 * Hybrid test: builds a cart in the browser, asks the API to price the same
 * cart, and checks that the screen shows exactly what the API calculated.
 */
@Feature("Hybrid: the UI agrees with the API")
public class CartMatchesApiTests extends WebTest {
  private final ShopApi api = new ShopApi();

  @Test(groups = "smoke", description = "Cart totals on screen equal the quote API's totals")
  public void cartTotalsMatchTheQuoteApi() {
    CartPage cart = signedIn().addToCart(Catalogue.HEADPHONES).addToCart(Catalogue.DESK_LAMP).openCart()
        .applyCoupon(Catalogue.TEN_PERCENT_OFF);

    Quote quote = api.priceOf(
        List.of(new CartItem(Catalogue.HEADPHONES, 1), new CartItem(Catalogue.DESK_LAMP, 1)), Catalogue.TEN_PERCENT_OFF);

    assertThat(cart.subtotal()).isEqualTo(quote.subtotal());
    assertThat(cart.discount()).isEqualTo(quote.discount());
    assertThat(cart.tax()).isEqualTo(quote.tax());
    assertThat(cart.total()).isEqualTo(quote.total());
  }
}
