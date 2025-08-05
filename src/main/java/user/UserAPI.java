package user;
import url.BaseUrl;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class UserAPI extends BaseUrl {

    private final static String CREATE_USER_PATH = "/api/auth/register";
    private final static String LOGIN_USER_PATH = "/api/auth/login";
    private final static String LOGOUT_USER_PATH = "/api/v1/logout";
    private final static String DELETE_USER_PATH = "/api/auth/user";
    private final static String PATCH_USER_PATH = "/api/auth/user";

    @Step("Send POST request to /api/auth/register")
    public Response createUserRequest(User user) {
        setUrl();
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(CREATE_USER_PATH);
    }

    @Step("Send POST request to /api/auth/login")
    public Response loginUserRequest(User user) {
        setUrl();
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(LOGIN_USER_PATH);
    }

    @Step("Send POST request to /api/v1/logout")
    public Response logoutUserRequest(User user) {
        setUrl();
        String refreshToken = loginUserRequest(user).then().extract().path("token");
        if (refreshToken != null) {
            return given()
                    .header("Content-type", "application/json")
                    .body("{\n" +
                            "\"token\": \""+ refreshToken + "\"}")
                    .post(LOGOUT_USER_PATH);
        }
        return given()
                .post(LOGOUT_USER_PATH);
    }

    @Step("Send DELETE request to /api/auth/register")
    public void deleteUserRequest(User user) {
        setUrl();
        String accessToken = loginUserRequest(user).then().extract().path("accessToken");
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .delete(DELETE_USER_PATH);
            return;
        }
        given()
                .delete(DELETE_USER_PATH);
    }

    @Step("Send PATCH request to /api/auth/register")
    public Response updateUserRequest(String accessToken,User user) {
        setUrl();
        return  given()
                    .header("Authorization", accessToken)
                    .header("Content-type", "application/json")
                    .body(user)
                    .patch(PATCH_USER_PATH);

    }

}

