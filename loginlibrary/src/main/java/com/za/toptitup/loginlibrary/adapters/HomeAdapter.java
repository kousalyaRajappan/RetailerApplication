package com.za.toptitup.loginlibrary.adapters;



import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.ActivityListModel;

import java.util.List;




public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{

    private final Context context;
    private final List<ActivityListModel> activityListModels;

    public HomeAdapter(Context context, List<ActivityListModel> activityListModels) {
        this.context = context;
        this.activityListModels = activityListModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        holder.name.setText(activityListModels.get(position).getTxt1());
        holder.image.setImageResource(activityListModels.get(position).getImage1());

    }



    @Override
    public int getItemCount() {
        return activityListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }
}
