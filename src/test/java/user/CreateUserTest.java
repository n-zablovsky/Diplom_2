package user;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;

import static constants.Messages.RESPONSE_BODY_REQUIRED_FIELD_IS_EMPTY;
import static constants.Messages.RESPONSE_BODY_USER_EXISTS;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {

    UserAPI userAPI = new UserAPI();
    User user = new User("Miss@yandex.ru", "Миса", "12345678");
    User userWoPassword = new User("Miss@yandex.ru", "Миса", "");
    User userWoEmail = new User("", "Симба", "12345678");
    User userWoName = new User("Miss@yandex.ru", "", "12345678");

    @Test
    @DisplayName("Создание пользователя, успешный запрос")
    public void createUserSuccessfulTest() {
        userAPI.createUserRequest(user).then()
                .assertThat().statusCode(SC_OK)
                .extract()
                .path("success", String.valueOf(equalTo("true")));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createUserDuplicateTest() {
        userAPI.createUserRequest(user).then()
                .assertThat().statusCode(SC_OK);
        userAPI.createUserRequest(user).then()
                .assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body(equalTo(RESPONSE_BODY_USER_EXISTS));
    }

    @Test
    @DisplayName("Создание пользователя, не заполнен email")
    public void createUserWoEmailTest() {
        userAPI.createUserRequest(userWoEmail).then()
                .assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body(equalTo(RESPONSE_BODY_REQUIRED_FIELD_IS_EMPTY));
    }

    @Test
    @DisplayName("Создание пользователя, не заполнен password")
    public void createUserWoPasswordTest() {
        userAPI.createUserRequest(userWoPassword).then()
                .assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body(equalTo(RESPONSE_BODY_REQUIRED_FIELD_IS_EMPTY));
    }

    @Test
    @DisplayName("Создание пользователя, не заполнен name")
    public void createUserWoNameTest() {
        userAPI.createUserRequest(userWoName).then()
                .assertThat().statusCode(SC_FORBIDDEN)
                .and()
                .body(equalTo(RESPONSE_BODY_REQUIRED_FIELD_IS_EMPTY));
    }

    @After
    public void deleteUser() {
        try {
            // Удаляем только успешно созданного пользователя
            if (user.getEmail() != null && !user.getEmail().isEmpty()
                    && user.getPassword() != null && !user.getPassword().isEmpty()) {
                String accessToken = userAPI.loginUserRequest(user)
                        .then()
                        .extract()
                        .path("accessToken");

                if (accessToken != null) {
                    userAPI.deleteUserRequest(accessToken);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to delete user: " + e.getMessage());
        }
    }
}