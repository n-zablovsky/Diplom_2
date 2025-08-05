package ingredients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import url.BaseUrl;

import static io.restassured.RestAssured.given;

public class IngredientsAPI extends BaseUrl {

    private final static String GET_INGREDIENTS_PATH = "/api/ingredients";

    @Step("Send GET request to /api/ingredients")
    public Response getIngredientsRequest() {
        setUrl();
        return given()
                .get(GET_INGREDIENTS_PATH);
    }
}
