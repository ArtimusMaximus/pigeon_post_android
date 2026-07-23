package com.pigeonpost.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeonpost.android.R;
import com.pigeonpost.android.data.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class RecentNotesAdapter extends RecyclerView.Adapter<RecentNotesAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    private final List<Note> notes = new ArrayList<>();
    private final OnNoteClickListener listener;

    public RecentNotesAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<Note> newNotes) {
        notes.clear();

        if (newNotes != null) {
            notes.addAll(newNotes);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recent_note_item, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.txtNoteCategory.setText(note.getCategory());

        String preview = note.getIsPrivate()
                ? "Private note - tap to unlock"
                : note.getContent();

        holder.txtNotePreview.setText(preview);

        holder.itemView.setOnClickListener(v -> listener.onNoteClick(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView txtNoteCategory;
        TextView txtNotePreview;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNoteCategory = itemView.findViewById(R.id.txtNoteCategory);
            txtNotePreview = itemView.findViewById(R.id.txtNotePreview);
        }
    }
}