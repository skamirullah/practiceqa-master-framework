package com.practiceqa.novashop.tests.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.practiceqa.novashop.base.WebTest;
import com.practiceqa.novashop.data.Catalogue;
import com.practiceqa.novashop.pages.ProductsPage;
import io.qameta.allure.Feature;
import java.util.Comparator;
import org.testng.annotations.Test;

@Feature("Web: browsing products")
public class CatalogueTests extends WebTest {

  @Test(description = "Search narrows the list to matching products")
  public void searchFindsAProduct() {
    ProductsPage products = signedIn().search("headphones").waitForCount(1);

    assertThat(products.names()).containsExactly("Aurora Wireless Headphones");
  }

  @Test(description = "Sorting by price lists the cheapest product first")
  public void sortsByPriceLowToHigh() {
    ProductsPage products = signedIn().sortBy("price-asc");

    assertThat(products.prices()).hasSizeGreaterThan(1).isSortedAccordingTo(Comparator.naturalOrder());
  }

  @Test(description = "A sold-out product is marked and cannot be added")
  public void soldOutProductIsMarked() {
    assertThat(signedIn().isSoldOut(Catalogue.SOLD_OUT_SPEAKER)).isTrue();
  }

  @Test(description = "The cart badge counts what has been added")
  public void cartBadgeCountsItems() {
    ProductsPage products = signedIn().addToCart(Catalogue.HEADPHONES).addToCart(Catalogue.DESK_LAMP);

    assertThat(products.cartBadge()).isEqualTo("2");
  }
}
