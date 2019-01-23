package com.projects.sainkinnovation.demorx.views;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.projects.sainkinnovation.demorx.R;
import com.projects.sainkinnovation.demorx.models.Result;
import com.projects.sainkinnovation.demorx.models.SelectedMovie;
import com.projects.sainkinnovation.demorx.presenter.MoviePresenter;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Movie_Fragment extends DialogFragment implements  MovieCallback{


    public Movie_Fragment() {
        // Required empty public constructor
    }

    TextView tvOverView,tvMovieName;
    ImageView ivProfileimage,ivPoster;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_movie_, container, false);
        Objects.requireNonNull(getActivity()).getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        tvMovieName=view.findViewById(R.id.tvMovieName);
        tvOverView=view.findViewById(R.id.tvOverView);
        ivProfileimage=view.findViewById(R.id.ivProfileimage);
        ivPoster=view.findViewById(R.id.ivPoster);
        Bundle bundle=getArguments();
        MoviePresenter moviePresenter=new MoviePresenter(Movie_Fragment.this);
        moviePresenter.getData(bundle.getInt("KEY"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// handle back button naviagtion
                dismiss();
            }
        });
        toolbar.setTitle("Movies");
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayMovies(SelectedMovie selectedMovie) {
        setImage(selectedMovie.getPosterPath(),ivPoster);

        tvMovieName.setText(selectedMovie.getOriginalTitle());
        tvOverView.setText(selectedMovie.getOverview());
        setImage(selectedMovie.getPosterPath(),ivPoster);

    }

    private void setImage(String posterPath, ImageView imageView) {
        Glide.with(Objects.requireNonNull(getContext())).load("https://image.tmdb.org/t/p/w500/"+posterPath).into(imageView);
    }

    @Override
    public void displayError(String s) {
        showToast(s);
    }
}
