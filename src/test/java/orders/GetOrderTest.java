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

import static constants.Messages.RESPONSE_BODY_UNAUTHORIZED;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class GetOrderTest {

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
                .statusCode(SC_OK)
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
        ingredientsList.add(ingredients.get(1));
        ingredientsList.add(ingredients.get(3));
        ingredientsList.add(ingredients.get(5));
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    @Description("Проверка получения списка заказов авторизованным пользователем")
    public void getOrderByAuthorizedUserTest() {
        setIngredientsList();
        orderAPI.createOrderRequest(accessToken, order);

        Response getOrdersResponse = orderAPI.getOrderRequest(accessToken);
        getOrdersResponse.then().statusCode(SC_OK);
        boolean isSuccess = getOrdersResponse.then().extract().path("success");
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    @Description("Проверка попытки получения списка заказов неавторизованным пользователем")
    public void getOrderByUnauthorizedUserTest() {
        setIngredientsList();
        Response getOrdersResponse = orderAPI.getOrderRequest("");
        getOrdersResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .body(equalTo(RESPONSE_BODY_UNAUTHORIZED));
        boolean isSuccess = getOrdersResponse.then().extract().path("success");
        assertFalse(isSuccess);
    }

    @After
    public void tearDown() {
        userAPI.deleteUserRequest(user);
    }
}