package com.example.proteusapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements SubjectAdapter.OnSubjectActionListener {

    private static final String TAG = "HomeActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TabLayout tabLayoutSemesters;
    private RecyclerView recyclerViewSubjects;
    private FloatingActionButton fabAddSubject;
    private Button buttonLogout;

    private SubjectAdapter subjectAdapter;
    private List<Subject> subjects;
    private int currentSemester = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();

        setupSemesterTabs();
        setupRecyclerView();
        setupClickListeners();

        loadSubjects(currentSemester);
    }

    private void initializeViews() {
        tabLayoutSemesters = findViewById(R.id.tabLayoutSemesters);
        recyclerViewSubjects = findViewById(R.id.recyclerViewSubjects);
        fabAddSubject = findViewById(R.id.fabAddSubject);
        buttonLogout = findViewById(R.id.buttonLogout);
    }

    private void setupSemesterTabs() {
        for (int i = 1; i <= 6; i++) {
            tabLayoutSemesters.addTab(tabLayoutSemesters.newTab().setText("Semester " + i));
        }

        tabLayoutSemesters.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentSemester = tab.getPosition() + 1;
                loadSubjects(currentSemester);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        subjects = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjects, this);
        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSubjects.setAdapter(subjectAdapter);
    }

    private void setupClickListeners() {
        buttonLogout.setOnClickListener(v -> logout());

        fabAddSubject.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSubjectActivity.class);
            intent.putExtra("semester", currentSemester);
            startActivity(intent);
        });
    }

    private void loadSubjects(int semester) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            logout();
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("subjects")
                .whereEqualTo("semester", semester)
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subjects.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Subject subject = document.toObject(Subject.class);
                        subject.setId(document.getId());
                        subjects.add(subject);
                    }
                    subjectAdapter.updateSubjects(subjects);
                    Log.d(TAG, "Loaded " + subjects.size() + " subjects for semester " + semester);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading subjects", e);
                    Toast.makeText(this, "Error loading subjects: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onSubjectTakenChanged(Subject subject, boolean taken) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        subject.setTaken(taken);

        db.collection("users")
                .document(user.getUid())
                .collection("subjects")
                .document(subject.getId())
                .update("taken", taken)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Subject updated successfully");
                    Toast.makeText(this, "Subject " + (taken ? "marked as taken" : "unmarked"),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating subject", e);
                    Toast.makeText(this, "Error updating subject: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    loadSubjects(currentSemester);
                });
    }

    @Override
    public void onSubjectRemoved(Subject subject) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users")
                .document(user.getUid())
                .collection("subjects")
                .document(subject.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Subject deleted successfully");
                    Toast.makeText(this, "Subject removed", Toast.LENGTH_SHORT).show();
                    loadSubjects(currentSemester);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting subject", e);
                    Toast.makeText(this, "Error removing subject: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubjects(currentSemester);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            logout();
        }
    }
}