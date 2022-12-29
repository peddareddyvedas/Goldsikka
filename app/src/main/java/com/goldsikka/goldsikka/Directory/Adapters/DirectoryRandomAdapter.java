package com.goldsikka.goldsikka.Directory.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.goldsikka.goldsikka.ComingSoon;
import com.goldsikka.goldsikka.Directory.DirectoryStoreDetails;
import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.interfaces.ApiDao;
import com.goldsikka.goldsikka.model.Listmodel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DirectoryRandomAdapter extends RecyclerView.Adapter<DirectoryRandomAdapter.ViewHolder> {


    Context context;
    ArrayList<Listmodel> randomlist;

    ApiDao apiDao;
    List<Listmodel> flist;
    ArrayList<Listmodel> ratinglist = new ArrayList<>();
    ArrayList<Listmodel> timeinglist = new ArrayList<>();
    String opentimings = "";
    String closetimings = "";

    public DirectoryRandomAdapter(Context context, ArrayList<Listmodel> randomlist) {
        this.context = context;
        this.randomlist = randomlist;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.activity_directory_random_adapter, parent, false);

        return new ViewHolder(view);
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "NewApi"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listmodel listmodel = randomlist.get(position);
        String name = listmodel.getStore_name();
        String discription = listmodel.getStore_description1();

        String image = listmodel.getImage();
        holder.tv_category.setText(name);
        holder.tv_discription.setText(discription);
        String tvratings = listmodel.getRating();
        Log.e("ratings", "" + tvratings);
        //  holder.tv_ratings.setText(tvratings +".0");


        if (listmodel.getRating() != null) {
            holder.ratinglayout.setVisibility(View.VISIBLE);

        } else {
            holder.ratinglayout.setVisibility(View.GONE);

        }
        holder.tv_ratings.setText(tvratings);

        String open = listmodel.getStore_open_timings();
        String close = listmodel.getStore_close_timings();
        try {
            String _24HourTime = close;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            System.out.println(_12HourSDF.format(_24HourDt));
            holder.tv_closetime.setText(_12HourSDF.format(_24HourDt));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String _24HourTime = open;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            System.out.println(_12HourSDF.format(_24HourDt));
            holder.tv_opentime.setText(_12HourSDF.format(_24HourDt));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listmodel.getImage() != null) {

            Picasso.with(context.getApplicationContext()).load(image).into(holder.iv_categoryimg);

            try {
                Picasso.with(context).load(image).into(holder.iv_categoryimg);
            } catch (Exception ignored) {
                Picasso.with(context).load(R.drawable.background_image).into(holder.iv_categoryimg);
            }

        } else {
            Picasso.with(context).load(R.drawable.background_image).into(holder.iv_categoryimg);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DirectoryStoreDetails.class);
                intent.putExtra("id", listmodel.getId());
                intent.putExtra("store_name", listmodel.getStore_name());
                intent.putExtra("mobile", listmodel.getMobile());
                intent.putExtra("latitude", listmodel.getLatitude());
                intent.putExtra("longitude", listmodel.getLongitude());
                intent.putExtra("status", listmodel.getStatus());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        //   return randomlist.size();
        int limit = 3;
        return Math.min(randomlist.size(), limit);
    }

    public void filterList(ArrayList<Listmodel> randomfilter) {
        randomlist = randomfilter;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_categoryimg;
        TextView tv_category, tv_discription, tv_ratings, tv_opentime, tv_closetime;
        CardView cardView;
        AppCompatRatingBar ratingBar;
        LinearLayout ratinglayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_category = itemView.findViewById(R.id.randomlistingText);
            iv_categoryimg = itemView.findViewById(R.id.randomlistingimg);
            tv_discription = itemView.findViewById(R.id.randomdiscription);
            iv_categoryimg.setOnClickListener(this);
            cardView = itemView.findViewById(R.id.listcard);
            tv_ratings = itemView.findViewById(R.id.ratingtext);
            ratingBar = itemView.findViewById(R.id.ratings);
            tv_opentime = itemView.findViewById(R.id.opentime);
            tv_closetime = itemView.findViewById(R.id.closetime);
            ratinglayout = itemView.findViewById(R.id.ratinglayout);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}