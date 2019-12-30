package com.golden.kidsfollow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.golden.kidsfollow.models.Classroom;
import com.golden.kidsfollow.models.Kid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class KidsActivity extends AppCompatActivity {
    public static String CLASSROOM_ID_EXTRA = "CLASSROOM_ID_EXTRA";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private KidAdapter kidsAdapter;
    FirebaseUser currentUser;
    DocumentReference classRoomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids);

        setTitle("Kids");
        initActivity();
    }

    void initActivity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || (bundle.getString(CLASSROOM_ID_EXTRA) == null)) {
            handleClassroomNotFound();
            return;
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String classroomId = bundle.getString(CLASSROOM_ID_EXTRA);
        classRoomRef = db.collection("users")
                .document(currentUser.getUid())
                .collection("classrooms")
                .document(classroomId);

        classRoomRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Classroom classroom;

                            if (document == null || !document.exists() ||
                                    (classroom = document.toObject(Classroom.class)) == null) {
                                handleClassroomNotFound();
                                return;
                            }

                            setTitle(classroom.getName() + " Kids");
                        } else {
                            handleClassroomNotFound();
                        }
                    }
                });
        setupRecycleView();

        findViewById(R.id.fabAddKid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewKidActivity.class);
                intent.putExtra(NewKidActivity.CLASSROOM_ID_EXTRA, classRoomRef.getId());
                startActivity(intent);
            }
        });
    }

    private void setupRecycleView() {
        Query query = classRoomRef.collection("kids");
        FirestoreRecyclerOptions<Kid> options = new FirestoreRecyclerOptions.Builder<Kid>()
                .setQuery(query, Kid.class)
                .build();
        kidsAdapter = new KidAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.rvKids);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(kidsAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                kidsAdapter.deleteKid(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        kidsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        kidsAdapter.stopListening();
    }

    private void handleClassroomNotFound() {
        Toast.makeText(getApplicationContext(), "Classroom not found", Toast.LENGTH_SHORT).show();
        finish();
    }
}
