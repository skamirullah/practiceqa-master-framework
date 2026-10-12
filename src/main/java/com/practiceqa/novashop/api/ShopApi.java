package com.practiceqa.novashop.api;

import static io.restassured.RestAssured.given;

import com.practiceqa.novashop.config.Config;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client for the Nova Shop API: the catalogue and the quote endpoint that prices
 * a cart with the same rules as the UI. Used by the API suite and by hybrid tests.
 */
public class ShopApi {
  private static final int TOO_MANY_REQUESTS = 429;
  private static final int MAX_RETRIES = 7;
  private static final long BACKOFF_MILLIS = 10_000;

  private static final RequestSpecification SPEC = new RequestSpecBuilder()
      .setBaseUri(Config.baseUrl())
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .addFilter(new AllureRestAssured())
      .build();

  private RequestSpecification request() {
    return given().spec(SPEC).log().ifValidationFails(LogDetail.ALL);
  }

  @Step("GET the catalogue")
  public Response catalogue() {
    return request().get("/api/shop/products");
  }

  /** Prices a cart. `coupon` and `shipping` may be null to leave them out of the request. */
  @Step("POST a cart to the quote API")
  public Response quote(List<CartItem> items, String coupon, String shipping) {
    Map<String, Object> body = new HashMap<>();
    body.put("items", items);
    if (coupon != null) body.put("coupon", coupon);
    if (shipping != null) body.put("shipping", shipping);
    return postWithBackoff(body);
  }

  public Response quote(List<CartItem> items) {
    return quote(items, null, null);
  }

  /** Prices a cart and returns the totals, failing at once if the cart is not accepted. */
  public Quote priceOf(List<CartItem> items, String coupon) {
    return quote(items, coupon, null).then().statusCode(200).extract().as(Quote.class);
  }

  /**
   * The API allows 30 quotes a minute. A 429 is retried after a pause, so a busy
   * minute slows the run down instead of failing it.
   */
  private Response postWithBackoff(Object body) {
    Response response = request().body(body).post("/api/shop/quote");
    for (int attempt = 0; response.statusCode() == TOO_MANY_REQUESTS && attempt < MAX_RETRIES; attempt++) {
      pause();
      response = request().body(body).post("/api/shop/quote");
    }
    return response;
  }

  private static void pause() {
    try {
      Thread.sleep(BACKOFF_MILLIS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
