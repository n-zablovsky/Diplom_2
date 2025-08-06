package constants;

public class Messages {

    public static final String RESPONSE_BODY_USER_EXISTS = "{\"success\":false,\"message\":\"User already exists\"}";

    public static final String RESPONSE_BODY_REQUIRED_FIELD_IS_EMPTY = "{\"success\":false,\"message\":\"Email, password and name are required fields\"}";

    public static final String RESPONSE_BODY_INCORRECT_LOGIN_DATA = "{\"success\":false,\"message\":\"email or password are incorrect\"}";

    public static final String RESPONSE_BODY_UNAUTHORIZED = "{\"success\":false,\"message\":\"You should be authorised\"}";

    public static final String RESPONSE_BODY_EMPTY_ORDER = "Ingredient ids must be provided";

}
