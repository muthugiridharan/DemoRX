package com.projects.sainkinnovation.demorx.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.projects.sainkinnovation.demorx.R;
import com.projects.sainkinnovation.demorx.adaptor.Movieadaptor;
import com.projects.sainkinnovation.demorx.models.MovieResponce;
import com.projects.sainkinnovation.demorx.models.Result;
import com.projects.sainkinnovation.demorx.presenter.MainactivityPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainactivityCallback{

    private RecyclerView rvContent;
    Movieadaptor movieadaptor;
    MainactivityPresenter mainactivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvContent=findViewById(R.id.rvContent);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
         mainactivityPresenter=new MainactivityPresenter(this);
        mainactivityPresenter.getMovies();
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayMovies(List<Result> results) {
            if(results!=null){
                movieadaptor = new Movieadaptor(results, MainActivity.this);
                rvContent.setAdapter(movieadaptor);
            }else{
                showToast("No items found");
            }
    }

    @Override
    public void displayError(String s) {
        showToast("Error occurred : "+s);
    }

    @Override
    public void viewClicked(int position) {
        mainactivityPresenter.viewClicked(position);
    }
}
