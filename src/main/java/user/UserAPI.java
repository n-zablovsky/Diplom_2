package user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import url.BaseUrl;

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
        String refreshToken = loginUserRequest(user).then().extract().path("refreshToken");
        return given()
                .header("Content-type", "application/json")
                .body(new LogoutRequest(refreshToken))
                .post(LOGOUT_USER_PATH);
    }

    @Step("Send DELETE request to /api/auth/user")
    public void deleteUserRequest(String accessToken) {
        setUrl();
        given()
                .header("Authorization", accessToken)
                .delete(DELETE_USER_PATH);
    }

    @Deprecated
    public void deleteUserRequest(User user) {
        try {
            String accessToken = loginUserRequest(user).then().extract().path("accessToken");
            if (accessToken != null) {
                deleteUserRequest(accessToken);
            }
        } catch (Exception e) {
            System.out.println("Failed to delete user: " + e.getMessage());
        }
    }

    @Step("Send PATCH request to /api/auth/user")
    public Response updateUserRequest(String accessToken, User user) {
        setUrl();
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(user)
                .patch(PATCH_USER_PATH);
    }

    private static class LogoutRequest {
        private final String token;

        public LogoutRequest(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}