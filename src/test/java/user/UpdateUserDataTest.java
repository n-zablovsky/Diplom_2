package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static constants.Messages.RESPONSE_BODY_UNAUTHORIZED;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class UpdateUserDataTest {

    private final Faker faker = new Faker();
    private UserAPI userAPI = new UserAPI();
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
        accessToken = userAPI.loginUserRequest(user)
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Изменение email пользователя на новый, с авторизацией")
    @Description("Проверка изменения email авторизованного пользователя")
    public void updateEmailToNewTest() {
        String newEmail = faker.internet().emailAddress();
        User userNewEmail = new User(newEmail, user.getName(), user.getPassword());

        Response dataChanged = userAPI.updateUserRequest(accessToken, userNewEmail);
        String newEmailResponse = dataChanged.then().extract().path("user.email");
        assertEquals(newEmail, newEmailResponse);
    }

    @Test
    @DisplayName("Изменение name пользователя на новый, с авторизацией")
    @Description("Проверка изменения name авторизованного пользователя")
    public void updateNameToNewTest() {
        String newName = faker.name().fullName();
        User userNewName = new User(user.getEmail(), newName, user.getPassword());

        Response dataChanged = userAPI.updateUserRequest(accessToken, userNewName);
        String newNameResponse = dataChanged.then().extract().path("user.name");
        assertEquals(newName, newNameResponse);
    }

    @Test
    @DisplayName("Изменение password пользователя на новый, с авторизацией")
    @Description("Проверка изменения password авторизованного пользователя")
    public void updatePasswordToNewTest() {
        String newPassword = faker.internet().password(8, 12);
        User userNewPassword = new User(user.getEmail(), user.getName(), newPassword);

        Response dataChanged = userAPI.updateUserRequest(accessToken, userNewPassword);
        Boolean newPasswordResponse = dataChanged.then().extract().path("success");
        assertEquals(true, newPasswordResponse);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверка попытки изменения данных пользователя без авторизации")
    public void updateUserDataUnauthorizedTest() {
        User userNewEmail = new User(faker.internet().emailAddress(), user.getName(), user.getPassword());
        userAPI.updateUserRequest("", userNewEmail)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body(equalTo(RESPONSE_BODY_UNAUTHORIZED));
    }

    @After
    public void tearDown() {
        userAPI.deleteUserRequest(user);
    }
}