package com.projects.sainkinnovation.demorx.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.projects.sainkinnovation.demorx.R;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        findViewById(R.id.btnRetrofit).setOnClickListener(this);
        findViewById(R.id.btnSwipeview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRetrofit:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
            case R.id.btnSwipeview:
                startActivity(new Intent(getApplicationContext(),Swipeview.class));
                break;
        }
    }
}
