package com.golden.kidsfollow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.golden.kidsfollow.models.Kid;
import com.golden.kidsfollow.utils.AppController;

public class KidAdapter extends FirestoreRecyclerAdapter<Kid, KidAdapter.KidHolder> {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public KidAdapter(@NonNull FirestoreRecyclerOptions<Kid> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull KidHolder holder, int position, @NonNull Kid model) {
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        holder.tvTitle.setText(model.getName());
        holder.tvDescription.setText(model.getNote());
        holder.ivThumbnail.setImageUrl(model.getThumbnailUrl(), imageLoader);
    }

    @NonNull
    @Override
    public KidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.title_description_thumbnail_item, parent, false);
        return new KidHolder(view);
    }

    void deleteKid(int adapterPosition) {
        getSnapshots().getSnapshot(adapterPosition).getReference().delete();
    }

    class KidHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        NetworkImageView ivThumbnail;

        KidHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            ivThumbnail = itemView.findViewById(R.id.ivItemThumbnail);
        }
    }
}
