package com.projects.sainkinnovation.demorx.views;

import com.projects.sainkinnovation.demorx.models.Result;
import com.projects.sainkinnovation.demorx.models.SelectedMovie;

import java.util.List;

public interface MovieCallback {
    void showToast(String s);
    void displayMovies(SelectedMovie selectedMovie);
    void displayError(String s);
}
