package user;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static constants.Messages.RESPONSE_BODY_INCORRECT_LOGIN_DATA;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest {

    UserAPI userAPI = new UserAPI();
    User user = new User("Miss@yandex.ru","Миса","12345678");
    User incorrectData = new User("wrong_emailMiss@yandex.ru","Миса","123456789");

    @Before
    public void createUser(){
        userAPI.createUserRequest(user);
    }

    @Test
    @DisplayName("Авторизация пользователя, успешный запрос")
    public void loginUserSuccessfulTest() {
        userAPI.loginUserRequest(user).then()
                .assertThat().statusCode(200)
                .extract()
                .path("success", String.valueOf(equalTo("true")));
    }

    @Test
    @DisplayName("Авторизация пользователя, некорректный email и пароль")
    public void loginUserWrongDataTest() {
        userAPI.loginUserRequest(incorrectData).then()
                .assertThat().statusCode(401)
                .body(equalTo(RESPONSE_BODY_INCORRECT_LOGIN_DATA));
    }

    @After
    public void deleteUser() {
            userAPI.deleteUserRequest(user);
    }

}
