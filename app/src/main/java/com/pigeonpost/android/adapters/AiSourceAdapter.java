package com.pigeonpost.android.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
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

//        String title = source.getTitle();
//
//        if (title == null || title.trim().isEmpty()) {
//            title = "Note " + source.getNoteId();
//        }
//
//        holder.sourceTitle.setText(title);

        String content = source.getContent();

        holder.sourceContent.setText(
                content == null || content.trim().isEmpty()
                        ? "No note content available."
                        : content
        );

        String categoryName = source.getCategoryName();
        holder.sourceCategory.setText(
                categoryName == null || categoryName.trim().isEmpty()
                        ? "Uncategorized"
                        : categoryName
        );
        String categoryColor = source.getCategoryColor();
        int parsedColor;
        try {
            parsedColor =
                    categoryColor == null
                            || categoryColor.trim().isEmpty()
                            ? Color.WHITE
                            : Color.parseColor(categoryColor);
        } catch (IllegalArgumentException exception) {
            parsedColor = Color.WHITE;
        }

        holder.sourceCategory.setBackgroundTintList(
                ColorStateList.valueOf(parsedColor)
        );
        holder.sourceCategory.setTextColor(
                getReadableTextColor(parsedColor)
        );

        Double distance = source.getDistance();

        int matchColor =
                distance == null
                        ? Color.WHITE
                        : getMatchColor(distance);

        holder.sourceMatch.setVisibility(View.VISIBLE);

        holder.sourceMatch.setText(
                distance == null
                        ? "Unknown Match"
                        : getMatchLabel(distance)
        );

        holder.sourceMatch.setBackgroundTintList(
                ColorStateList.valueOf(matchColor)
        );

        holder.sourceMatch.setTextColor(
                getReadableTextColor(matchColor)
        );

        holder.sourceCard.setStrokeColor(
                ColorStateList.valueOf(matchColor)
        );

        holder.itemView.setOnClickListener(view ->
                showSourceDetailsDialog(
                        view,
                        source
                )
        );
    }

    @Override
    public int getItemCount() {
        return sources.size();
    }
    private String getMatchLabel(
            Double distance
    ) {
        if (distance == null) {
            return "Unknown Match";
        }

        if (distance < 0.25) {
            return "Excellent Match";
        }

        if (distance < 0.40) {
            return "Strong Match";
        }

        if (distance < 0.60) {
            return "Related Note";
        }

        return "Possible Match";
    }
    private int getMatchColor(
            Double distance
    ) {
        if (distance == null) {
            return Color.GRAY;
        }

        if (distance < 0.25) {
            return Color.parseColor("#00C853");
        }

        if (distance < 0.40) {
            return Color.parseColor("#7CB342");
        }

        if (distance < 0.60) {
            return Color.parseColor("#FBC02D");
        }

        return Color.parseColor("#EF5350");
    }
    private String formatDate(
            String createdAt
    ) {
        if (createdAt == null || createdAt.trim().isEmpty()) {
            return "";
        }

        try {
            java.time.OffsetDateTime dateTime =
                    java.time.OffsetDateTime.parse(createdAt);

            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern(
                            "MMM d, yyyy",
                            Locale.getDefault()
                    );

            return dateTime.format(formatter);

        } catch (java.time.format.DateTimeParseException exception) {
            return "";
        }
    }
    private void showSourceDetailsDialog(
            View view,
            AiSourceResponse source
    ) {
        String category =
                source.getCategoryName() == null
                        || source.getCategoryName().trim().isEmpty()
                        ? "Uncategorized"
                        : source.getCategoryName();

        String createdDate =
                formatDate(source.getCreatedAt());

        String matchLabel =
                getMatchLabel(source.getDistance());

        String distanceText =
                source.getDistance() == null
                        ? "Unavailable"
                        : String.format(
                        Locale.getDefault(),
                        "%.4f",
                        source.getDistance()
                );

        String message =
                "Category\n"
                        + category
                        + "\n\nCreated\n"
                        + (createdDate.isEmpty()
                        ? "Unavailable"
                        : createdDate)
                        + "\n\nMatch Quality\n"
                        + matchLabel
                        + "\n\nEmbedding Distance\n"
                        + distanceText;

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(
                view.getContext()
        )
                .setTitle("Source Details")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }
    private int getReadableTextColor(
            int backgroundColor
    ) {
        double darkness =
                1
                        - (
                        0.299 * Color.red(backgroundColor)
                                + 0.587 * Color.green(backgroundColor)
                                + 0.114 * Color.blue(backgroundColor)
                ) / 255;

        return darkness >= 0.5
                ? Color.WHITE
                : Color.BLACK;
    }

    static class SourceViewHolder
            extends RecyclerView.ViewHolder {

//        private final TextView sourceTitle;
        private final TextView sourceContent;
        private final TextView sourceMatch;
        private final TextView sourceCategory;
        private final TextView sourceDate;
        private final MaterialCardView sourceCard;
//        private final View sourceMatchIndicator;

        public SourceViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

//            sourceTitle =
//                    itemView.findViewById(
//                            R.id.sourceTitle
//                    );

            sourceContent =
                    itemView.findViewById(
                            R.id.sourceContent
                    );

            sourceMatch =
                    itemView.findViewById(
                            R.id.sourceMatch
                    );

            sourceCategory = itemView.findViewById(
                    R.id.sourceCategory
            );

            sourceDate = itemView.findViewById(
                    R.id.sourceDate
            );

            sourceCard =
                    itemView.findViewById(
                            R.id.sourceCard
                    );

//            sourceMatchIndicator =
//                    itemView.findViewById(
//                            R.id.sourceMatchIndicator
//                    );
        }
    }
}