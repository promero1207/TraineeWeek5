package com.app.paul.newsapp.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.paul.newsapp.News;
import com.app.paul.newsapp.R;

import java.io.InputStream;
import java.util.List;

public class AdapterRvMainNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener listener;
    private List<News> adapterList;


    public AdapterRvMainNews(List<News> list, OnItemClickListener listener) {
        adapterList = list;
        this.listener = listener;
    }


    @Override
    public int getItemViewType(int position) {

        if (position == adapterList.size() - 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0) {
            ViewHolder h = (ViewHolder) holder;
            h.headline.setText(adapterList.get(position).getHeadline());
            h.section.setText(adapterList.get(position).getSection());
        }
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView headline;
        TextView section;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.item_headline);
            section = itemView.findViewById(R.id.item_section);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Integer position);
    }



    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}
