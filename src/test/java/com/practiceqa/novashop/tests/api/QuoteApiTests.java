package com.practiceqa.novashop.tests.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.practiceqa.novashop.api.CartItem;
import com.practiceqa.novashop.api.Quote;
import com.practiceqa.novashop.api.ShopApi;
import com.practiceqa.novashop.data.Catalogue;
import io.qameta.allure.Feature;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Feature("API: pricing a cart")
public class QuoteApiTests {
  private static final double ONE_CENT = 0.01;
  private final ShopApi api = new ShopApi();

  @Test(groups = "smoke", description = "The total is subtotal minus discount plus shipping plus tax")
  public void totalAddsUp() {
    Quote quote = api.priceOf(List.of(new CartItem(Catalogue.HEADPHONES, 1), new CartItem(Catalogue.DESK_LAMP, 2)), Catalogue.TEN_PERCENT_OFF);

    assertThat(quote.itemCount()).isEqualTo(3);
    assertThat(quote.discount()).isCloseTo(quote.subtotal() * 0.10, within(ONE_CENT));
    assertThat(quote.total()).isCloseTo(quote.subtotal() - quote.discount() + quote.shipping() + quote.tax(), within(ONE_CENT));
  }

  @Test(description = "Express shipping is charged even on a large order")
  public void expressShippingIsAlwaysCharged() {
    List<CartItem> cart = List.of(new CartItem(Catalogue.HEADPHONES, 1));

    api.quote(cart, null, "standard").then().statusCode(200).body("shipping", equalTo(0));
    api.quote(cart, null, "express").then().statusCode(200).body("shipping", equalTo(15));
  }

  @DataProvider
  public Object[][] rejectedCarts() {
    return new Object[][] {
        {"empty cart", List.of(), null, 400, "non-empty array"},
        {"unknown product", List.of(new CartItem("p999", 1)), null, 404, "Unknown product"},
        {"sold-out product", List.of(new CartItem(Catalogue.SOLD_OUT_SPEAKER, 1)), null, 409, "sold out"},
        {"zero quantity", List.of(new CartItem(Catalogue.BEANIE, 0)), null, 400, "qty"},
        {"unknown coupon", List.of(new CartItem(Catalogue.BEANIE, 1)), "FREESTUFF", 422, "not a valid coupon"},
    };
  }

  @Test(dataProvider = "rejectedCarts", description = "A cart that cannot be priced is rejected with a reason")
  public void cartIsRejectedWithAReason(String name, List<CartItem> items, String coupon, int status, String reason) {
    api.quote(items, coupon, null).then().statusCode(status).body("error", containsString(reason));
  }
}
