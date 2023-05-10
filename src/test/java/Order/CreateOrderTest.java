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

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class CreateOrderTest {
    Client client;
    Order order;
    ApiClient apiClient;
    ApiOrder apiOrder;
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
    @DisplayName("Создание заказа авторизованным пользователем")
    @Description("Проверка, что можно создать заказ, если пользователь авторизован")
    public void createOrderTest() {
        fillIngredientList();
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        ApiClient.userLogin(client);
        ValidatableResponse responseCreateOrder = ApiOrder.createOrder(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }


    @Test
    @DisplayName("Создание заказа неавторизованным пользователем")
    @Description("Проверка, что можно создать заказ, если пользователь не авторизован")
    public void createOrderWithoutAuthTest() {
        fillIngredientList();
        ValidatableResponse responseCreateOrder = ApiOrder.createOrderWithoutAuth(order);
        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без добавления ингредиентов авторизованным пользователем")
    @Description("Проверка, что нельзя создать заказ, если не добавить ингредиенты")
    public void createOrderWithoutIngredientTest() {
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        ValidatableResponse responseCreateOrder = ApiOrder.createOrder(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST).body("success", is(false))
                .and().body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ c неверным хешем ингредиентов авторизованным пользователем")
    @Description("Проверка, что нельзя создать заказ, если указать неверный хеш ингредиентов")
    public void createOrderWithWrongHashIngredientTest() {
        fillWrongHashIngredientList();
        ValidatableResponse responseReg = apiClient.userReg(client);
        bearerToken = responseReg.extract().path("accessToken");
        ValidatableResponse responseCreateOrder = ApiOrder.createOrder(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }


    private void fillIngredientList() {
        ValidatableResponse validatableResponse = ApiOrder.getAllIngredients();
        List<String> list = validatableResponse.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(0));
        ingredients.add(list.get(4));
        ingredients.add(list.get(2));
        ingredients.add(list.get(0));
    }

    private void fillWrongHashIngredientList() {
        ValidatableResponse validatableResponse = ApiOrder.getAllIngredients();
        List<String> list = validatableResponse.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(0));
        ingredients.add(list.get(4).repeat(1));
        ingredients.add(list.get(2).repeat(2));
        ingredients.add(list.get(0));
    }
}
