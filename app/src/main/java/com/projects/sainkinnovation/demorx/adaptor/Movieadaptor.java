package com.projects.sainkinnovation.demorx.adaptor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.projects.sainkinnovation.demorx.R;
import com.projects.sainkinnovation.demorx.models.Result;
import com.projects.sainkinnovation.demorx.utlity.MoviesmodelCallback;
import com.projects.sainkinnovation.demorx.views.MainactivityCallback;

import java.util.List;

public class Movieadaptor extends RecyclerView.Adapter<Movieadaptor.Viewholder>{

    private final Context context;
    private List<Result> movieList;
    private MainactivityCallback mainactivityCallback;

    public Movieadaptor(List<Result> movieList, Context context) {
        this.movieList=movieList;
        this.context=context;
        this.mainactivityCallback=(MainactivityCallback)context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.moviecard,viewGroup,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {
        holder.tvTitle.setText(movieList.get(position).getTitle());
        holder.tvOverview.setText(movieList.get(position).getOverview());
        holder.tvReleaseDate.setText(movieList.get(position).getReleaseDate());
        Glide.with(context).load("https://image.tmdb.org/t/p/w500/"+movieList.get(position).getPosterPath()).into(holder.ivMovie);
        holder.ltParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainactivityCallback.viewClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        }else{
            Bundle o = (Bundle) payloads.get(position);
            Result result=o.getParcelable("Result");
            Log.i("Adaptor", "onBindViewHolder: data==>"+ new Gson().toJson(result));
            holder.tvTitle.setText(movieList.get(position).getTitle());
            holder.tvOverview.setText(movieList.get(position).getOverview());
            holder.tvReleaseDate.setText(movieList.get(position).getReleaseDate());
            Glide.with(context).load("https://image.tmdb.org/t/p/w500/"+movieList.get(position).getPosterPath()).into(holder.ivMovie);
            holder.ltParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainactivityCallback.viewClicked(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void updateArrayList(List<Result> list){
        MoviesmodelCallback moviesmodelCallback=new MoviesmodelCallback(list,movieList);
        DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff(moviesmodelCallback);
        diffResult.dispatchUpdatesTo(this);
        movieList.clear();
        movieList.addAll(list);
//        notifyDataSetChanged();

    }

    class Viewholder extends RecyclerView.ViewHolder{
        TextView tvTitle,tvOverview,tvReleaseDate;
        ImageView ivMovie;
        CardView ltParent;
    Viewholder(@NonNull View itemView) {
        super(itemView);
        tvTitle =  itemView.findViewById(R.id.tvTitle);
        tvOverview = itemView.findViewById(R.id.tvOverView);
        tvReleaseDate = itemView.findViewById(R.id.tvReleaseDate);
        ivMovie =itemView.findViewById(R.id.ivMovie);
        ltParent=itemView.findViewById(R.id.ltParent);
    }
}
}
