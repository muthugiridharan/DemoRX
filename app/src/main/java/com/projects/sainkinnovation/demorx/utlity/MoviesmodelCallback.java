package com.projects.sainkinnovation.demorx.utlity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.projects.sainkinnovation.demorx.models.Result;

import java.util.List;

public class MoviesmodelCallback extends DiffUtil.Callback {

    private List<Result> newList;
    private  List<Result> oldList;

    public MoviesmodelCallback(List<Result> newList, List<Result> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldList.get(i).getId()==newList.get(i).getId();
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        int result = newList.get(i).compareTo(oldList.get(i1));
        return result == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //return super.getChangePayload(oldItemPosition, newItemPosition);
        Result resultNew=newList.get(newItemPosition);
        Result resultOld=oldList.get(oldItemPosition);
        Bundle bundle=new Bundle();
        if(resultNew.getId()!=resultOld.getId()){
                bundle.putParcelable("Result",resultNew);
        }
        if (bundle.size() == 0) {
            return null;
        }
        return bundle;
    }
}
