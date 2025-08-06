package url;
import io.restassured.RestAssured;

public abstract class BaseUrl {
    private final static String URL = "https://stellarburgers.nomoreparties.site";

    protected void setUrl() {
        RestAssured.baseURI = URL;
    }

}

