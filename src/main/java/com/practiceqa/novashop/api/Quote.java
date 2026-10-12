package com.practiceqa.novashop.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** What the quote API says a cart costs. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Quote(double subtotal, double discount, double shipping, double tax, double total, int itemCount, String coupon) {}
