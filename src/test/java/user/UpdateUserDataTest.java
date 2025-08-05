package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static constants.Messages.RESPONSE_BODY_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class UpdateUserDataTest {

    UserAPI userAPI = new UserAPI();

    String newEmail = "misakisa" + Math.random() + "@yandex.ru";
    String newName = "МисаКиса";

    User user = new User("misakot@yandex.ru","МисаКот","123456789");
    User userNewEmail = new User(newEmail,"МисаКот","123456789");
    User userNewName = new User("misakot@yandex.ru",newName,"123456789");
    User userNewPassword = new User("misakot@yandex.ru","МисаКот","1234567890");

    @Before
    public void createUser(){
        userAPI.createUserRequest(user);
    }

    @Test
    @DisplayName("Изменение email пользователя на новый, с авторизацией")
    public void updateEmailToNewTest() {
        String accessToken =  userAPI.loginUserRequest(user)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .path("accessToken");

       Response dataChanged = userAPI.updateUserRequest(accessToken, userNewEmail);
       String newEmailResponse = dataChanged.then().extract().path("user.email");
       assertEquals(newEmail, newEmailResponse);

       userAPI.updateUserRequest(accessToken, user);
    }

    @Test
    @DisplayName("Изменение name пользователя на новый, с авторизацией")
    public void updateNameToNewTest() {
        String accessToken =  userAPI.loginUserRequest(user)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .path("accessToken");

        Response dataChanged = userAPI.updateUserRequest(accessToken, userNewName);
        String newNameResponse = dataChanged.then().extract().path("user.name");
        assertEquals(newName, newNameResponse);
    }

    @Test
    @DisplayName("Изменение password пользователя на новый, с авторизацией")
    public void updatePasswordToNewTest() {
        String accessToken =  userAPI.loginUserRequest(user)
                .then()
                .assertThat().statusCode(200)
                .extract()
                .path("accessToken");

        Response dataChanged = userAPI.updateUserRequest(accessToken, userNewPassword);
        Boolean newPasswordResponse = dataChanged.then().extract().path("success");
        assertEquals(true, newPasswordResponse);

        userAPI.updateUserRequest(accessToken, user);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void updateUserDataUnauthorizedTest() {
        userAPI.updateUserRequest("", userNewEmail)
                .then()
                .assertThat().statusCode(401)
                .body(equalTo(RESPONSE_BODY_UNAUTHORIZED));
    }

    @After
    public void deleteUser() {
        userAPI.deleteUserRequest(user);
    }
}
