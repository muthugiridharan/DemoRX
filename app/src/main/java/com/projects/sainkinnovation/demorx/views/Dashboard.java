package com.projects.sainkinnovation.demorx.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.projects.sainkinnovation.demorx.R;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    TextView tvTittle,tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        findViewById(R.id.btnRetrofit).setOnClickListener(this);
        findViewById(R.id.btnSwipeview).setOnClickListener(this);
        findViewById(R.id.btnMap).setOnClickListener(this);
        tvTittle=findViewById(R.id.tvTittle);
        tvContent= findViewById(R.id.tvContent);
        if(getIntent().getExtras()!=null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Log.i("Dashboard", "onNewIntent: tittle==>" + bundle.getString("Tittle"));
                if (bundle.containsKey("Tittle")) {
                    tvTittle.setText(bundle.getString("Tittle"));
                }

                if (bundle.containsKey("Content")) {
                    tvContent.setText(bundle.getString("Content"));
                }
            }
        }
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
            case R.id.btnMap:
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                break;
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        Bundle bundle=intent.getExtras();
//        if(bundle!=null){
//            Log.i("Dashboard", "onNewIntent: tittle==>"+bundle.getString("Tittle"));
//            if(bundle.containsKey("Tittle")){
//                tvTittle.setText(bundle.getString("Tittle"));
//            }
//
//            if(bundle.containsKey("Content")){
//                tvContent.setText(bundle.getString("Content"));
//            }
//        }
//    }
}
