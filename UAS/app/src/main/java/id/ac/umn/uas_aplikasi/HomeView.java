
package id.ac.umn.uas_aplikasi;

import java.util.List;

public interface HomeView {
    void showLoading();
    void hideLoading();
    void setMeal(List<Meals.Meal> meal);
    void setSearch(List<Meals.Meal> meals);
    void setCategory(List<Categories.Category> category);
    void onErrorLoading(String message);
}
