package com.projects.sainkinnovation.demorx.network;

import com.projects.sainkinnovation.demorx.models.MovieResponce;
import com.projects.sainkinnovation.demorx.models.SelectedMovie;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkInterface {

        @GET("movie/popular")
        Observable<MovieResponce> getMovies(@Query("api_key") String api_key,@Query("language") String language,
                                            @Query("page") int page,@Query("region") String region);

        @GET("movie/{movieId}")
        Observable<SelectedMovie> getMovie(@Path("movieId") int movieId,
                                           @Query("api_key") String api_key,@Query("language") String language);

}
