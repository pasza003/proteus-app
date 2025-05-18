package com.example.proteusapp;

import com.google.firebase.Timestamp;

public class Subject {
    private String id;
    private String name;
    private int credits;
    private int semester;
    private boolean taken;
    private Timestamp dateAdded;

    public Subject() {}

    public Subject(String name, int credits, int semester) {
        this.name = name;
        this.credits = credits;
        this.semester = semester;
        this.taken = false;
        this.dateAdded = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public boolean isTaken() { return taken; }
    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public Timestamp getDateAdded() { return dateAdded; }
    public void setDateAdded(Timestamp dateAdded) { this.dateAdded = dateAdded; }
}