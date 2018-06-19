package com.bill.android.myapplication.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bill.android.myapplication.R;
import com.bill.android.myapplication.models.Movie;
import com.bill.android.myapplication.models.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Review> mReviewList;

    public ReviewAdapter(Context context, ArrayList<Review> itemList) {
        mContext = context;
        this.mReviewList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        ReviewAdapter.ViewHolder myViewHolder = new ReviewAdapter.ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = mReviewList.get(position);

        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewList == null ? 0 : mReviewList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder  {
        TextView author;
        TextView content;

        ViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_author);
            content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
