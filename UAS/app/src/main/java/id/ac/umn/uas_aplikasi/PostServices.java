package id.ac.umn.uas_aplikasi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PostServices {
    @GET("random.php")
    Call<Meals> getMeal();

    @GET("categories.php")
    Call<Categories> getCategories();

    @GET("filter.php")
    Call<Meals> getMealByCategory(@Query("c") String category);

    @GET("search.php")
    Call<Meals> getMealByName(@Query("s") String category);
}
