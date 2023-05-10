package сlient;

import customer.Burger;
import customer.Methods;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.CoreMatchers.is;

public class ApiClient extends Methods {
    public static ValidatableResponse userLogin(Client client) {
        return given()
                .spec(Burger.requestSpecification())
                .and()
                .body(client)
                .when()
                .post(Methods.LOGIN)
                .then();
    }

    public ValidatableResponse userReg(Client client) {
        return given()
                .spec(Burger.requestSpecification())
                .and()
                .body(client)
                .when()
                .post(Methods.CREATE_USER)
                .then();
    }

    public ValidatableResponse deleteUser(String bearerToken) {
        return given()
                .spec(Burger.requestSpecification())
                .headers("Authorization", bearerToken)
                .delete(Methods.DELETE_USER)
                .then()
                .statusCode(SC_ACCEPTED)
                .and().body("message", is("User successfully removed"));
    }

    public ValidatableResponse updateDataUserWithAuth(ClientNewData userRandomData, String bearerToken) {
        return given()
                .spec(Burger.requestSpecification())
                .header("Authorization", bearerToken)
                .contentType(ContentType.JSON)
                .and()
                .body(userRandomData)
                .when()
                .patch(Methods.PATCH_USER)
                .then();
    }

    public ValidatableResponse updateDataUserWithoutAuth(ClientNewData userRandomData) {
        return given()
                .spec(Burger.requestSpecification())
                .and()
                .body(userRandomData)
                .patch(Methods.PATCH_USER)
                .then();
    }
}
