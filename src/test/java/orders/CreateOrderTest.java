package orders;

import ingredients.IngredientsAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import order.Order;
import order.OrderAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserAPI;

import java.util.List;

import static constants.Messages.*;
import static org.junit.Assert.*;

public class CreateOrderTest {

    OrderAPI orderAPI = new OrderAPI();
    UserAPI userAPI = new UserAPI();
    IngredientsAPI ingredientsAPI = new IngredientsAPI();

    private Order order;

    User user = new User("PetFed@email.com","Пётр","123456789");

    @Before
    public void createUser(){
        userAPI.createUserRequest(user);
        order = new Order();
    }

    public void setIngredientsList(){
        List<String> ingredients = ingredientsAPI.getIngredientsRequest().then().assertThat().statusCode(200)
                .extract().path("data._id");
        List <String> ingredientsList = order.getIngredients();
        ingredientsList.add(ingredients.get(0));
        ingredientsList.add(ingredients.get(2));
        ingredientsList.add(ingredients.get(4));
        ingredientsList.add(ingredients.get(6));
        ingredientsList.add(ingredients.get(8));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами авторизованным пользователем")
    public void createOrderSuccessfulTest() {
        setIngredientsList();
        String accessToken =  userAPI.loginUserRequest(user)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .path("accessToken");

       Response response = orderAPI.createOrderRequest(accessToken, order);

        boolean orderResponse = response
                .then()
                .assertThat()
                .statusCode(200)
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
    public void createOrderUnauthorizedTest() {
        setIngredientsList();
        Response response = orderAPI.createOrderRequest("", order);

        boolean orderResponse = response
                .then()
                .assertThat()
                .statusCode(200)
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
    public void createOrderWoIngredientsTest() {
        String accessToken =  userAPI.loginUserRequest(user)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .path("accessToken");

        Response response = orderAPI.createOrderRequest(accessToken, order);

        boolean orderResponse = response
                .then()
                .assertThat()
                .statusCode(400)
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
    public void createOrderWrongIngredientsTest() {
        String accessToken =  userAPI.loginUserRequest(user)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .path("accessToken");

        List<String> ingredients = ingredientsAPI.getIngredientsRequest().then().assertThat().statusCode(200)
                .extract().path("data._id");
        List <String> ingredientsList = order.getIngredients();
        ingredientsList.add(ingredients.get(0).replaceAll("0", "X"));
        ingredientsList.add(ingredients.get(1).replaceAll("f", "X"));

        Response response = orderAPI.createOrderRequest(accessToken, order);
        response.then().assertThat().statusCode(500);

    }

    @After
    public void deleteUser() {
        userAPI.deleteUserRequest(user);
    }
}
