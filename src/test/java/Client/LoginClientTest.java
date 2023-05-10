package Client;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import сlient.ApiClient;
import сlient.Client;
import сlient.ClientRandomData;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class LoginClientTest {
    Client client;
    private ApiClient apiClient;
    private String bearerToken;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        client = new ClientRandomData().getRandomUser();
    }

    @After
    public void tearDown() {
        if (bearerToken == null) return;
        apiClient.deleteUser(bearerToken);
    }

    @Test
    @DisplayName("Логин пользователя с валидными данными")
    @Description("Проверка, что можно залогиниться с валидными данными")
    public void loginUserTest() {
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        ValidatableResponse responseLogin = ApiClient.userLogin(client);
        responseLogin.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @DisplayName("Логин пользователя с неправильным email")
    @Description("Проверка, что можно нельзя залогиниться с неправильным email")
    public void loginUserWithWrongEmailTest() {
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        client.setEmail(client.getEmail() + "test");
        ValidatableResponse responseLogin = ApiClient.userLogin(client);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .and().body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя с неправильным паролем")
    @Description("Проверка, что можно нельзя залогиниться с неправильным email")
    public void loginUserWithWrongPasswordTest() {
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        client.setPassword(client.getPassword() + "test");
        ValidatableResponse responseLogin = ApiClient.userLogin(client);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .and().body("message", is("email or password are incorrect"));
    }
}
