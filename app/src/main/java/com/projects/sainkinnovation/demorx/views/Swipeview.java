package com.projects.sainkinnovation.demorx.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.projects.sainkinnovation.demorx.R;
import com.projects.sainkinnovation.demorx.models.Result;
import com.projects.sainkinnovation.demorx.presenter.MainactivityPresenter;
import com.projects.sainkinnovation.demorx.utlity.TinderCard;

import java.util.List;

public class Swipeview extends AppCompatActivity implements  MainactivityCallback{

    SwipePlaceHolderView swipeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipeview);
        swipeView=findViewById(R.id.swipeView);
        MainactivityPresenter mainactivityPresenter=new MainactivityPresenter(this);
        mainactivityPresenter.getMovies();
        swipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(Swipeview.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayMovies(List<Result> results) {
        for(Result result:results){
            swipeView.addView(new TinderCard(getApplicationContext(), result, swipeView));
        }
    }

    @Override
    public void displayError(String s) {
        showToast("Error occurred : "+s);
    }

    @Override
    public void viewClicked(int position) {

    }
}
