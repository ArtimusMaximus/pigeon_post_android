package com.pigeonpost.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeonpost.android.R;
import com.pigeonpost.android.data.remote.dto.AiSourceResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AiSourceAdapter
        extends RecyclerView.Adapter<AiSourceAdapter.SourceViewHolder> {

    private final List<AiSourceResponse> sources =
            new ArrayList<>();

    public void setSources(
            List<AiSourceResponse> newSources
    ) {
        sources.clear();

        if (newSources != null) {
            sources.addAll(newSources);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SourceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_ai_source,
                                parent,
                                false
                        );

        return new SourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull SourceViewHolder holder,
            int position
    ) {
        AiSourceResponse source =
                sources.get(position);

        String title = source.getTitle();

        if (title == null || title.trim().isEmpty()) {
            title = "Note " + source.getNoteId();
        }

        holder.sourceTitle.setText(title);

        String content = source.getContent();

        holder.sourceContent.setText(
                content == null || content.trim().isEmpty()
                        ? "No note content available."
                        : content
        );

        Double distance = source.getDistance();

        if (distance == null) {
            holder.sourceMatch.setVisibility(View.GONE);
        } else {
            holder.sourceMatch.setVisibility(View.VISIBLE);

            holder.sourceMatch.setText(
                    String.format(
                            Locale.getDefault(),
                            "Semantic distance: %.3f",
                            distance
                    )
            );
        }
    }

    @Override
    public int getItemCount() {
        return sources.size();
    }

    static class SourceViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView sourceTitle;
        private final TextView sourceContent;
        private final TextView sourceMatch;

        public SourceViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            sourceTitle =
                    itemView.findViewById(
                            R.id.sourceTitle
                    );

            sourceContent =
                    itemView.findViewById(
                            R.id.sourceContent
                    );

            sourceMatch =
                    itemView.findViewById(
                            R.id.sourceMatch
                    );
        }
    }
}