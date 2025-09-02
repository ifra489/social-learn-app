package rwu.it.sociallearn;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetScreen extends AppCompatActivity {

    private EditText edtResetEmail;
    private Button btnSendResetEmail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_screen);

        edtResetEmail = findViewById(R.id.edtResetEmail);
        btnSendResetEmail = findViewById(R.id.btnSendResetEmail);
        progressDialog = new ProgressDialog(this);

        btnSendResetEmail.setOnClickListener(v -> {
            String email = edtResetEmail.getText().toString().trim();
            if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edtResetEmail.setError("Enter valid email");
                edtResetEmail.requestFocus();
                return;
            }

            progressDialog.setMessage("Sending reset email...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Reset email sent! Check inbox/spam", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}