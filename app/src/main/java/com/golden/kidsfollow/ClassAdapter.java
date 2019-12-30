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
import com.google.firebase.firestore.DocumentSnapshot;

public class ClassAdapter extends FirestoreRecyclerAdapter<Classroom, ClassAdapter.ClassHolder> {

    private OnClassroomClickedListener listener;

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onClassRoomClicked(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    interface OnClassroomClickedListener {
        void onClassRoomClicked(DocumentSnapshot documentSnapshot, int position);
    }

    void setOnClassroomClickedListener(OnClassroomClickedListener listener) {
        this.listener = listener;
    }

    void removeOnItemClickedListener(OnClassroomClickedListener listener) {
        if (this.listener == listener)
            this.listener = null;
    }
}
