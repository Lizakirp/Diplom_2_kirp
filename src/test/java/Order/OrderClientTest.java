package Order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.ApiOrder;
import order.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import сlient.ApiClient;
import сlient.Client;
import сlient.ClientRandomData;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class OrderClientTest {
    Client client;
    Order order;
    private ApiClient apiClient;
    private ApiOrder apiOrder;
    private String bearerToken;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        client = new ClientRandomData().getRandomUser();
        apiOrder = new ApiOrder();
        order = new Order();
    }

    @After
    public void tearDown() {
        if (bearerToken == null) return;
        apiClient.deleteUser(bearerToken);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка, что можно получить список заказов, когда пользователь авторизован")
    public void checkOrdersUserWithAuthTest() {
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        ApiClient.userLogin(client);
        ApiOrder.createOrder(order, bearerToken);
        ValidatableResponse responseOrdersUser = apiOrder.getUserOrdersWithAuth(bearerToken);
        responseOrdersUser.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Проверка, что нельзя получить список заказов, когда пользователь не авторизован")
    public void checkOrdersUserWithoutAuthTest() {
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        ValidatableResponse responseOrders = apiOrder.getUserOrdersWithoutAuth();
        responseOrders.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false))
                .and().body("message", is("You should be authorised"));
    }
}
