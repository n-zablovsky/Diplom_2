package order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import url.BaseUrl;

import static io.restassured.RestAssured.given;

public class OrderAPI extends BaseUrl {

    private final static String GET_ORDERS_PATH = "/api/orders";
    private final static String CREATE_ORDER_PATH = "/api/orders";

    @Step("Send POST request to /api/orders")
    public Response createOrderRequest(String accessToken, Order order) {
        setUrl();
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(CREATE_ORDER_PATH);
    }

    @Step("Send GET request to /api/orders")
    public Response getOrderRequest(String accessToken) {
        setUrl();
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(GET_ORDERS_PATH);
    }



}
