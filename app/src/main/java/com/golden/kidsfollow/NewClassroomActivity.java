package com.golden.kidsfollow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.golden.kidsfollow.models.Classroom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewClassroomActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etDescription;
    private Button btnSave;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_classroom);
        setTitle("Add Classroom");

        etName = findViewById(R.id.etClassroomName);
        etDescription = findViewById(R.id.etClassroomDescription);
        btnSave = findViewById(R.id.btnSaveClassroom);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void saveClassroom(View view) {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Both Name and Description fields are required", Toast.LENGTH_LONG).show();
            return;
        }

        CollectionReference classroomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("classrooms");

        btnSave.setEnabled(false);
        classroomsRef.add(new Classroom(name, description))
        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Classroom Added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (task.getException() == null) {
                        Toast.makeText(getApplicationContext(), "Unknown Exception", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
