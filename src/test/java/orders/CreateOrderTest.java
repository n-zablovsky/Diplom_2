package orders;

import ingredients.IngredientsAPI;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import net.datafaker.Faker;
import order.Order;
import order.OrderAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserAPI;

import java.util.List;

import static constants.Messages.*;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateOrderTest {

    private final Faker faker = new Faker();
    private OrderAPI orderAPI = new OrderAPI();
    private UserAPI userAPI = new UserAPI();
    private IngredientsAPI ingredientsAPI = new IngredientsAPI();
    private Order order;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = new User(
                faker.internet().emailAddress(),
                faker.name().fullName(),
                faker.internet().password(8, 12)
        );
        userAPI.createUserRequest(user);
        order = new Order();
        accessToken = userAPI.loginUserRequest(user)
                .then()
                .extract()
                .path("accessToken");
    }

    private void setIngredientsList() {
        List<String> ingredients = ingredientsAPI.getIngredientsRequest()
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("data._id");
        List<String> ingredientsList = order.getIngredients();
        ingredientsList.add(ingredients.get(0));
        ingredientsList.add(ingredients.get(2));
        ingredientsList.add(ingredients.get(4));
        ingredientsList.add(ingredients.get(6));
        ingredientsList.add(ingredients.get(8));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами авторизованным пользователем")
    @Description("Проверка успешного создания заказа авторизованным пользователем с валидными ингредиентами")
    public void createOrderSuccessfulTest() {
        setIngredientsList();
        Response response = orderAPI.createOrderRequest(accessToken, order);

        boolean orderResponse = response
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("success");

        int orderNumber = response
                .then()
                .extract()
                .path("order.number");

        assertTrue(orderResponse);
        assertNotNull(orderNumber);
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами неавторизованным пользователем")
    @Description("Проверка создания заказа неавторизованным пользователем с валидными ингредиентами")
    public void createOrderUnauthorizedTest() {
        setIngredientsList();
        Response response = orderAPI.createOrderRequest("", order);

        boolean orderResponse = response
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("success");

        int orderNumber = response
                .then()
                .extract()
                .path("order.number");

        assertTrue(orderResponse);
        assertNotNull(orderNumber);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка попытки создания заказа без указания ингредиентов")
    public void createOrderWoIngredientsTest() {
        Response response = orderAPI.createOrderRequest(accessToken, order);

        boolean orderResponse = response
                .then()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .path("success");

        String message = response
                .then()
                .extract()
                .path("message");

        assertFalse(orderResponse);
        assertEquals(RESPONSE_BODY_EMPTY_ORDER, message);
    }

    @Test
    @DisplayName("Создание заказа с некорректными ингредиентами")
    @Description("Проверка попытки создания заказа с невалидными ингредиентами")
    public void createOrderWrongIngredientsTest() {
        List<String> ingredients = ingredientsAPI.getIngredientsRequest()
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("data._id");
        List<String> ingredientsList = order.getIngredients();
        ingredientsList.add(ingredients.get(0).replaceAll("0", "X"));
        ingredientsList.add(ingredients.get(1).replaceAll("f", "X"));

        orderAPI.createOrderRequest(accessToken, order)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {
        userAPI.deleteUserRequest(user);
    }
}