package com.example.proteusapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddSubjectActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private EditText editTextSubjectName;
    private EditText editTextCredits;
    private Button buttonSave;
    private Button buttonCancel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private int semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get semester from intent
        semester = getIntent().getIntExtra("semester", 1);

        // Initialize views
        initializeViews();
        setupClickListeners();

        // Update title with semester
        textViewTitle.setText("Add Subject to Semester " + semester);
    }

    private void initializeViews() {
        textViewTitle = findViewById(R.id.textViewTitle);
        editTextSubjectName = findViewById(R.id.editTextSubjectName);
        editTextCredits = findViewById(R.id.editTextCredits);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
    }

    private void setupClickListeners() {
        buttonSave.setOnClickListener(v -> saveSubject());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void saveSubject() {
        String subjectName = editTextSubjectName.getText().toString().trim();
        String creditsStr = editTextCredits.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(subjectName)) {
            editTextSubjectName.setError("Subject name is required");
            editTextSubjectName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(creditsStr)) {
            editTextCredits.setError("Credits are required");
            editTextCredits.requestFocus();
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsStr);
            if (credits <= 0) {
                editTextCredits.setError("Credits must be a positive number");
                editTextCredits.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editTextCredits.setError("Please enter a valid number");
            editTextCredits.requestFocus();
            return;
        }

        // Get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Create subject
        Subject subject = new Subject(subjectName, credits, semester);

        // Save to Firestore
        db.collection("users")
                .document(user.getUid())
                .collection("subjects")
                .add(subject)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Subject added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding subject: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}