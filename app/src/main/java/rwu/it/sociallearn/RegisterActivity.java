package rwu.it.sociallearn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnRegister;
    private ImageView imgAvatar;
    private TextView tvLogin;
    private Uri avatarUri;
    private RadioGroup genderGroup;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private Uri cameraImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        imgAvatar = findViewById(R.id.avatarImage);
        tvLogin = findViewById(R.id.tvLogin);
        genderGroup = findViewById(R.id.genderGroup);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // Gallery picker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        avatarUri = uri;
                        imgAvatar.setImageURI(uri);
                    }
                });

        // Camera picker
        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        avatarUri = cameraImageUri;
                        imgAvatar.setImageURI(cameraImageUri);
                    }
                });

        // Avatar click → show options
        imgAvatar.setOnClickListener(v -> showImagePickOptions());

        // Register button
        btnRegister.setOnClickListener(v -> registerUser());

        // Already have account → Login
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showImagePickOptions() {
        String[] options = {"Choose from Gallery", "Take Photo"};
        new AlertDialog.Builder(this)
                .setTitle("Select Avatar")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        pickImageLauncher.launch("image/*");
                    } else {
                        cameraImageUri = getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new android.content.ContentValues()
                        );
                        takePhotoLauncher.launch(cameraImageUri);
                    }
                })
                .show();
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Valid email required");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            edtPassword.setError("Password must be ≥ 6 chars");
            edtPassword.requestFocus();
            return;
        }

        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = mAuth.getCurrentUser().getUid();

                    if (avatarUri == null) {

                        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
                        String avatarKey;
                        if (selectedGenderId == R.id.rbFemale) {
                            avatarKey = "ic_female";
                        } else {
                            avatarKey = "ic_male";
                        }
                        saveUserData(uid, email, avatarKey);
                    } else {

                        saveUserData(uid, email, avatarUri.toString());
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserData(String uid, String email, String avatarValue) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("avatar", avatarValue);

        FirebaseDatabase.getInstance().getReference("Users").child(uid)
                .setValue(userMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "DB Save Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}