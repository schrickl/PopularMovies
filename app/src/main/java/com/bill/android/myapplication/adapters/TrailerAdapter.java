package com.bill.android.myapplication.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bill.android.myapplication.R;
import com.bill.android.myapplication.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private static final String YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_DEFAULT = "/default.jpg";
    private Context mContext;
    private ArrayList<Trailer> mTrailerList;

    public TrailerAdapter(Context context, ArrayList<Trailer> itemList) {
        mContext = context;
        this.mTrailerList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        TrailerAdapter.ViewHolder myViewHolder = new TrailerAdapter.ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.ViewHolder holder, int position) {
        Trailer trailer = mTrailerList.get(position);
        String thumbnailUrl = YOUTUBE_THUMBNAIL_BASE_URL + trailer.getKey() + YOUTUBE_THUMBNAIL_DEFAULT;
        final Intent app = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
        final Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));

        Picasso.get().load(thumbnailUrl).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mContext.startActivity(app);
                } catch (ActivityNotFoundException anfe) {
                    mContext.startActivity(web);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailerList == null ? 0 : mTrailerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView thumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.iv_thumb);
        }
    }
}
