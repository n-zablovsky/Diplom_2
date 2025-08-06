package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static constants.Messages.RESPONSE_BODY_INCORRECT_LOGIN_DATA;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest {

    private final Faker faker = new Faker();
    private UserAPI userAPI = new UserAPI();
    private User user;
    private String correctPassword;

    @Before
    public void createUser() {
        correctPassword = faker.internet().password(8, 12);
        user = new User(
                faker.internet().emailAddress(),
                faker.name().fullName(),
                correctPassword
        );
        userAPI.createUserRequest(user);
    }

    @Test
    @DisplayName("Авторизация пользователя, успешный запрос")
    @Description("Проверка успешной авторизации пользователя с валидными данными")
    public void loginUserSuccessfulTest() {
        userAPI.loginUserRequest(user)
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("success", String.valueOf(equalTo("true")));
    }

    @Test
    @DisplayName("Авторизация пользователя, некорректный email")
    @Description("Проверка попытки авторизации с неверным email")
    public void loginUserWrongEmailTest() {
        User incorrectEmail = new User(
                "wrong_" + user.getEmail(),
                user.getName(),
                correctPassword
        );
        userAPI.loginUserRequest(incorrectEmail)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body(equalTo(RESPONSE_BODY_INCORRECT_LOGIN_DATA));
    }

    @Test
    @DisplayName("Авторизация пользователя, некорректный пароль")
    @Description("Проверка попытки авторизации с неверным паролем")
    public void loginUserWrongPasswordTest() {
        User incorrectPassword = new User(
                user.getEmail(),
                user.getName(),
                "wrong_" + correctPassword
        );
        userAPI.loginUserRequest(incorrectPassword)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body(equalTo(RESPONSE_BODY_INCORRECT_LOGIN_DATA));
    }

    @After
    public void deleteUser() {
        userAPI.deleteUserRequest(user);
    }
}