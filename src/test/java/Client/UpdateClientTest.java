package Client;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import сlient.ApiClient;
import сlient.Client;
import сlient.ClientNewData;
import сlient.ClientRandomData;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class UpdateClientTest {
    Client client;
    ClientNewData userNewData;
    private ApiClient apiClient;
    private String bearerToken;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        client = new ClientRandomData().getRandomUser();
        userNewData = new ClientNewData();
    }

    @After
    public void tearDown() {
        if (bearerToken == null) return;
        apiClient.deleteUser(bearerToken);
    }

    @Test
    @DisplayName("Изменение данных авторизованного пользователя")
    @Description("Проверка, что можно изменить данные, когда пользователь авторизован")
    public void updateUserTest() {
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        ValidatableResponse responseUpdate = apiClient.updateDataUserWithAuth(userNewData.random(), bearerToken);
        responseUpdate.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @DisplayName("Изменение данных незалогиненного пользователя")
    @Description("Проверка, что нельзя изменить данные, когда пользователь не залогинен")
    public void updateUserWithoutAuthTest() {
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        ValidatableResponse responseUpdate = apiClient.updateDataUserWithoutAuth(userNewData);
        responseUpdate.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false))
                .and().body("message", is("You should be authorised"));
    }
}
