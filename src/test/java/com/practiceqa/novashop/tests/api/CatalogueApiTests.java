package com.practiceqa.novashop.tests.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;

import com.practiceqa.novashop.api.ShopApi;
import io.qameta.allure.Feature;
import java.util.List;
import org.testng.annotations.Test;

@Feature("API: catalogue")
public class CatalogueApiTests {
  private final ShopApi api = new ShopApi();

  @Test(groups = "smoke", description = "The catalogue lists products with positive prices")
  public void catalogueListsProducts() {
    List<Double> prices = api.catalogue().then().statusCode(200).extract().jsonPath().getList("products.price", Double.class);

    assertThat(prices).isNotEmpty().allMatch(price -> price > 0);
  }

  @Test(description = "Product ids are unique")
  public void productIdsAreUnique() {
    List<String> ids = api.catalogue().jsonPath().getList("products.id", String.class);

    assertThat(ids).doesNotHaveDuplicates();
  }

  @Test(description = "The pricing rules are published: coupons, shipping and tax")
  public void pricingRulesArePublished() {
    api.catalogue().then()
        .body("coupons.code", hasItems("SAVE10", "FLAT20"))
        .body("shipping.express", greaterThan(0))
        .body("taxRate", greaterThan(0f));
  }
}
