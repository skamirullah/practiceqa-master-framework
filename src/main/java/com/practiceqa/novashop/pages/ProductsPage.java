package com.practiceqa.novashop.pages;

import io.qameta.allure.Step;
import java.util.List;

public class ProductsPage extends BasePage {

  @Step("Search for {term}")
  public ProductsPage search(String term) {
    type(testId("search-input"), term);
    return this;
  }

  @Step("Sort by {order}")
  public ProductsPage sortBy(String order) {
    select(testId("sort-select"), order);
    return this;
  }

  /** Waits until exactly `count` products are listed, i.e. until a search or filter has been applied. */
  public ProductsPage waitForCount(int count) {
    wait.until(driver -> driver.findElements(testId("product-card")).size() == count);
    return this;
  }

  public List<String> names() {
    return texts(testId("product-name"));
  }

  public List<Double> prices() {
    return texts(testId("product-price")).stream().map(BasePage::dollars).toList();
  }

  public boolean isSoldOut(String productId) {
    return isPresent(testId("sold-out-" + productId));
  }

  @Step("Add product {productId} to the cart")
  public ProductsPage addToCart(String productId) {
    click(testId("add-to-cart-" + productId));
    visible(testId("remove-from-cart-" + productId));
    return this;
  }

  public String cartBadge() {
    return text(testId("cart-badge"));
  }

  @Step("Open the cart")
  public CartPage openCart() {
    click(testId("cart-link"));
    waitForUrl("/shop/cart");
    return new CartPage();
  }
}
