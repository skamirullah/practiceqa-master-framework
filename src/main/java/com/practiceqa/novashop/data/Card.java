package com.practiceqa.novashop.data;

/** A payment card. These are test numbers that only the practice shop accepts. */
public record Card(String number, String expiry, String cvv) {

  public static Card valid() {
    return new Card("4242424242424242", "12/30", "123");
  }
}
