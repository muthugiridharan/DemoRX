package com.projects.sainkinnovation.demorx.views;

import com.projects.sainkinnovation.demorx.models.MovieResponce;
import com.projects.sainkinnovation.demorx.models.Result;

import java.util.List;

public interface MainactivityCallback {
    void showToast(String s);
    void displayMovies(List<Result> results);
    void displayError(String s);
    void viewClicked(int position);
}
