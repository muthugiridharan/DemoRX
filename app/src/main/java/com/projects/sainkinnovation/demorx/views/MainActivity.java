package com.projects.sainkinnovation.demorx.views;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.projects.sainkinnovation.demorx.R;
import com.projects.sainkinnovation.demorx.adaptor.Movieadaptor;
import com.projects.sainkinnovation.demorx.models.Result;
import com.projects.sainkinnovation.demorx.presenter.MainactivityPresenter;
import com.projects.sainkinnovation.demorx.utlity.WrapContentLinearLayoutManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainactivityCallback{

    private RecyclerView rvContent;
    Movieadaptor movieadaptor;
    MainactivityPresenter mainactivityPresenter;
    //LinearLayoutManager manager;
    WrapContentLinearLayoutManager manager;
    boolean isScrolling = false;
    ProgressBar progressBar;
    int currentItems, totalItems, scrollOutItems;
    int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvContent=findViewById(R.id.rvContent);
        //manager=new LinearLayoutManager(getApplicationContext());
        manager =new WrapContentLinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        rvContent.setLayoutManager(manager);
        progressBar=findViewById(R.id.pbProgress);
         mainactivityPresenter=new MainactivityPresenter(this);
        mainactivityPresenter.getMovies(i);
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = recyclerView.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    progressBar.setVisibility(View.VISIBLE);
                    mainactivityPresenter.getMovies(i++);
                    Log.i("MainActivity", "onScrolled:  page No==>"+i);
                }
            }
        });
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayMovies(List<Result> results) {
            if(results!=null){
                progressBar.setVisibility(View.GONE);
                if(movieadaptor!=null){
                        movieadaptor.updateArrayList(results);
                }else {
                    movieadaptor = new Movieadaptor(results, MainActivity.this);
                    rvContent.setAdapter(movieadaptor);
                }
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
