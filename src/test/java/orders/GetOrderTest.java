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

import static constants.Messages.RESPONSE_BODY_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class GetOrderTest {

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
        ingredientsList.add(ingredients.get(1));
        ingredientsList.add(ingredients.get(3));
        ingredientsList.add(ingredients.get(5));
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    public void getOrderByAuthorizedUserTest() {
        setIngredientsList();
        String accessToken =  userAPI.loginUserRequest(user)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .path("accessToken");

       orderAPI.createOrderRequest(accessToken, order);

        Response getOrdersResponse = orderAPI.getOrderRequest(accessToken);
        getOrdersResponse.then().assertThat().statusCode(200);
        boolean isSuccess = getOrdersResponse.then().extract().path("success");
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    public void getOrderByUnauthorizedUserTest() {
        setIngredientsList();
        Response getOrdersResponse = orderAPI.getOrderRequest("");
        getOrdersResponse.then().assertThat().statusCode(401);
        getOrdersResponse.then().body(equalTo(RESPONSE_BODY_UNAUTHORIZED));
        boolean isSuccess = getOrdersResponse.then().extract().path("success");
        assertFalse(isSuccess);
    }

    @After
    public void deleteUser() {
        userAPI.deleteUserRequest(user);
    }

}
