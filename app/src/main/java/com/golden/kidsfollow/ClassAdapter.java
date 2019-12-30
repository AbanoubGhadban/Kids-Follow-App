package com.golden.kidsfollow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.golden.kidsfollow.models.Classroom;

public class ClassAdapter extends FirestoreRecyclerAdapter<Classroom, ClassAdapter.ClassHolder> {

    ClassAdapter(@NonNull FirestoreRecyclerOptions<Classroom> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ClassHolder holder, int position, @NonNull Classroom model) {
        holder.tvTitle.setText(model.getName());
        holder.tvDescription.setText(model.getDescription());
    }

    void deleteClassroom(int position) {
        getSnapshots()
                .getSnapshot(position)
                .getReference()
                .delete();
    }

    @NonNull
    @Override
    public ClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_description_item,
                parent, false);
        return new ClassHolder(v);
    }

    class ClassHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;

        ClassHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
        }
    }
}
