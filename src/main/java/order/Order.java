package order;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String _id;
    private String status;
    private String number;
    private String createdAt;
    private String updatedAt;
    private List<String> ingredients;

    public Order(String _id, String status, String number, String createdAt, String updatedAt, List<String> ingredients) {
        this._id = _id;
        this.status = status;
        this.number = number;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ingredients = ingredients;
    }

    public Order() {
        ingredients = new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public String getStatus() {
        return status;
    }

    public String getNumber() {
        return number;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
