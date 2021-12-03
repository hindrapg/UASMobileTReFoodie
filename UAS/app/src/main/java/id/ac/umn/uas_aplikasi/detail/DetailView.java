package id.ac.umn.uas_aplikasi.detail;

import java.util.List;

import id.ac.umn.uas_aplikasi.Categories;
import id.ac.umn.uas_aplikasi.Meals;

public interface DetailView {
    void showLoading();
    void hideLoading();
    void setMeal(Meals.Meal meal);
    void setCategory(List<Categories.Category> category);
    void onErrorLoading(String message);
}
