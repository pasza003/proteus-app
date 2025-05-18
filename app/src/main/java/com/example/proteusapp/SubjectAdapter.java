package com.example.proteusapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<Subject> subjects;
    private OnSubjectActionListener listener;

    public interface OnSubjectActionListener {
        void onSubjectTakenChanged(Subject subject, boolean taken);
        void onSubjectRemoved(Subject subject);
    }

    public SubjectAdapter(List<Subject> subjects, OnSubjectActionListener listener) {
        this.subjects = subjects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public void updateSubjects(List<Subject> newSubjects) {
        this.subjects = newSubjects;
        notifyDataSetChanged();
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSubjectName;
        private TextView textViewCredits;
        private CheckBox checkBoxTaken;
        private Button buttonRemove;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubjectName = itemView.findViewById(R.id.textViewSubjectName);
            textViewCredits = itemView.findViewById(R.id.textViewCredits);
            checkBoxTaken = itemView.findViewById(R.id.checkBoxTaken);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }

        public void bind(Subject subject) {
            textViewSubjectName.setText(subject.getName());
            textViewCredits.setText(subject.getCredits() + " Credits");
            checkBoxTaken.setChecked(subject.isTaken());

            checkBoxTaken.setOnClickListener(v -> {
                CheckBox checkBox = (CheckBox) v;
                boolean isChecked = checkBox.isChecked();
                if (listener != null) {
                    listener.onSubjectTakenChanged(subject, isChecked);
                }
            });

            buttonRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubjectRemoved(subject);
                }
            });
        }
    }
}
