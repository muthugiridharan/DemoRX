package com.projects.sainkinnovation.demorx.presenter;

import com.projects.sainkinnovation.demorx.models.MovieResponce;
import com.projects.sainkinnovation.demorx.models.SelectedMovie;
import com.projects.sainkinnovation.demorx.network.NetworkClient;
import com.projects.sainkinnovation.demorx.network.NetworkInterface;
import com.projects.sainkinnovation.demorx.views.MainactivityCallback;
import com.projects.sainkinnovation.demorx.views.MovieCallback;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MoviePresenter implements MoviePresenterCallback {

    private MovieCallback movieCallback;

    public MoviePresenter(MovieCallback movieCallback){
        this.movieCallback=movieCallback;
    }

    @Override
    public void getData(int Id) {
        getObservable(Id).subscribeWith(getObserver());
    }

    private DisposableObserver<SelectedMovie> getObserver(){
        return  new DisposableObserver<SelectedMovie>() {
            @Override
            public void onNext(SelectedMovie selectedMovie) {
                movieCallback.displayMovies(selectedMovie);
            }

            @Override
            public void onError(Throwable e) {
                movieCallback.displayError(e.toString());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<SelectedMovie> getObservable(int id){
        return NetworkClient.getRetrofit().create(NetworkInterface.class)
                .getMovie(id,"4ac38ecb93bdcc6f5bf09febda8043c9","en-US")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
