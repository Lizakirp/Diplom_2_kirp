package order;

import customer.Burger;
import customer.Methods;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class ApiOrder extends Methods {
    public static ValidatableResponse createOrder(Order order, String bearerToken) {
        return given()
                .spec(Burger.requestSpecification())
                .headers("Authorization", bearerToken)
                .body(order)
                .post(Methods.CREATE_ORDER)
                .then();
    }

    public static ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .spec(Burger.requestSpecification())
                .body(order)
                .post(Methods.CREATE_ORDER)
                .then();
    }

    public static ValidatableResponse getAllIngredients() {
        return given()
                .spec(Burger.requestSpecification())
                .get(Methods.INGREDIENT)
                .then();
    }

    public ValidatableResponse getUserOrdersWithAuth(String bearerToken) {
        return given()
                .spec(Burger.requestSpecification())
                .headers("Authorization", bearerToken)
                .get(Methods.USER_ORDERS)
                .then();
    }

    public ValidatableResponse getUserOrdersWithoutAuth() {
        return given()
                .spec(Burger.requestSpecification())
                .get(Methods.USER_ORDERS)
                .then();
    }
}
