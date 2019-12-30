package com.golden.kidsfollow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.golden.kidsfollow.models.Kid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class NewKidActivity extends AppCompatActivity {
    public static String CLASSROOM_ID_EXTRA = "CLASSROOM_ID_EXTRA";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri filePath;

    private EditText etKidName;
    private EditText etKidNote;
    private ImageView ivKidPhoto;
    private Button btnSave;

    FirebaseStorage storage;
    StorageReference storageReference;
    CollectionReference kidsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_kid);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        etKidName = findViewById(R.id.etKidName);
        etKidNote = findViewById(R.id.etKidNote);
        ivKidPhoto = findViewById(R.id.ivKidPhoto);
        btnSave = findViewById(R.id.btnSaveKid);

        if (getIntent() == null || getIntent().getStringExtra(CLASSROOM_ID_EXTRA) == null)
            finish();

        kidsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("classrooms")
                .document(getIntent().getStringExtra(CLASSROOM_ID_EXTRA))
                .collection("kids");
    }

    public void onSaveClicked(View view) {
        String name = etKidName.getText().toString().trim();
        String note = etKidNote.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Name Field is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (filePath == null)
            saveKid(new Kid(name, note));
        else
            uploadImageAndSave(name, note);
    }

    public void onChoosePhotoClicked(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivKidPhoto.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                Toast.makeText(getApplicationContext(), "Failed to Load Image " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveKid(Kid kid) {
        kidsRef.add(kid)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Kid Added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Exception exception = task.getException();
                            if (exception == null)
                                Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadImageAndSave(final String name, final String note) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());

        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();

                        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    saveKid(new Kid(name, note, task.getResult().toString(), task.getResult().toString()));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Couldn't get url", Toast.LENGTH_SHORT).show();
                                    btnSave.setEnabled(true);
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(NewKidActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        btnSave.setEnabled(true);
                    }
                });
    }
}
