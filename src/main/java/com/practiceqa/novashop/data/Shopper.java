package com.practiceqa.novashop.data;

/** Test accounts of the practice shop and the details used at checkout. All data is fictional. */
public record Shopper(String username, String password, String firstName, String lastName, String email,
                      String phone, String address, String city, String state, String zip) {
  private static final String PASSWORD = "Demo@123";

  public static Shopper standard() {
    return named("demo_user");
  }

  /** An account that exists but is not allowed to sign in. */
  public static Shopper lockedOut() {
    return named("locked_user");
  }

  private static Shopper named(String username) {
    return new Shopper(username, PASSWORD, "Ada", "Lovelace", "ada@example.com", "5551234567",
        "1 Analytical Way", "Austin", "Texas", "73301");
  }

  public String fullName() {
    return firstName + " " + lastName;
  }
}
