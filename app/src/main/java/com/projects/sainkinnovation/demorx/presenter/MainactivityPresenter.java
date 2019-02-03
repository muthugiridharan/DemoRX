package com.projects.sainkinnovation.demorx.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.projects.sainkinnovation.demorx.models.MovieResponce;
import com.projects.sainkinnovation.demorx.models.Result;
import com.projects.sainkinnovation.demorx.network.NetworkClient;
import com.projects.sainkinnovation.demorx.network.NetworkInterface;
import com.projects.sainkinnovation.demorx.views.MainactivityCallback;
import com.projects.sainkinnovation.demorx.views.Movie_Fragment;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainactivityPresenter implements MainactivityPresenterCallback{

    private MainactivityCallback mainactivityCallback;
    private static final String TAG=MainactivityPresenter.class.getSimpleName();
    private List<Result> resultList=new ArrayList<>();
    private Activity activity;

    public MainactivityPresenter(MainactivityCallback mainactivityCallback) {
        this.mainactivityCallback=mainactivityCallback;
        this.activity=(Activity)mainactivityCallback;
    }

    @Override
    public void getMovies(int i) {
        getObservable(i).subscribeWith(getObserver());
    }

    @Override
    public void viewClicked(int i) {
        Result result=resultList.get(i);
        FragmentActivity fragmentActivity = (FragmentActivity) (activity);
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        Bundle bundle=new Bundle();
        bundle.putInt("KEY",result.getId());
        Movie_Fragment movie_fragment=new Movie_Fragment();
        movie_fragment.setArguments(bundle);
       movie_fragment.show(fm,"MOVIES");
        Log.i(TAG, "viewClicked: id==>"+result.getId());
    }

    private DisposableObserver<MovieResponce> getObserver(){
        return new DisposableObserver<MovieResponce>() {
            @Override
            public void onNext(MovieResponce movieResponce) {
                resultList.clear();
                mainactivityCallback.displayMovies(movieResponce.getResults());
                resultList=movieResponce.getResults();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: "+e);
                mainactivityCallback.displayError(e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    private Observable<MovieResponce> getObservable(int pageno){
        return NetworkClient.getRetrofit().create(NetworkInterface.class)
                .getMovies("4ac38ecb93bdcc6f5bf09febda8043c9","en-US",pageno,null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
