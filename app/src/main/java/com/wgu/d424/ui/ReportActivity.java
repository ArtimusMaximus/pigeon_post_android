package com.wgu.d424.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.wgu.d424.R;
import com.wgu.d424.data.db.AppDatabase;
import com.wgu.d424.data.entities.CategoryCount;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private TextView txtTotalNotes;
    private TextView txtOldestNewest;
    private TextView txtMostUsedCategory;
    private TextView txtAverageLength;
    private TextView txtCategoryBreakdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        txtTotalNotes = findViewById(R.id.txtTotalNotes);
        txtOldestNewest = findViewById(R.id.txtOldestNewest);
        txtMostUsedCategory = findViewById(R.id.txtMostUsedCategory);
        txtAverageLength = findViewById(R.id.txtAverageLength);
        txtCategoryBreakdown = findViewById(R.id.txtCategoryBreakdown);

        MaterialButton backBtn = findViewById(R.id.btnBackFromReport);
        backBtn.setOnClickListener(view -> finish());

        loadReport();
    }

    private void loadReport() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);

            int totalNotes = db.noteDao().getTotalNoteCount();
            List<CategoryCount> categoryCounts = db.noteDao().getNoteCountByCategory();
            Long oldestDate = db.noteDao().getOldestNoteDate();
            Long newestDate = db.noteDao().getNewestNoteDate();
            String mostUsedCategory = db.noteDao().getMostUsedCategory();
            Double averageLength = db.noteDao().getAverageNoteLength();

            String categoryBreakdown = buildCategoryBreakdown(categoryCounts);

            runOnUiThread(() -> {
                txtTotalNotes.setText("Overall Quantity of Notes: " + totalNotes);

                txtOldestNewest.setText(
                        "Oldest Note: " + formatDate(oldestDate) +
                                "\nMost Current Note: " + formatDate(newestDate)
                );

                txtMostUsedCategory.setText(
                        "Most Used Category: " +
                                (mostUsedCategory == null ? "N/A" : mostUsedCategory)
                );

                txtAverageLength.setText(
                        "Average Note Length: " +
                                (averageLength == null ? "0" : String.format(Locale.US, "%.1f", averageLength)) +
                                " characters"
                );

                txtCategoryBreakdown.setText(categoryBreakdown);
            });
        }).start();
    }

    private String buildCategoryBreakdown(List<CategoryCount> categoryCounts) {
        if (categoryCounts == null || categoryCounts.isEmpty()) {
            return "No category data available.";
        }

        StringBuilder builder = new StringBuilder();

        for (CategoryCount item : categoryCounts) {
            builder.append(item.category)
                    .append(": ")
                    .append(item.noteCount)
                    .append("\n");
        }

        return builder.toString().trim();
    }

    private String formatDate(Long timestamp) {
        if (timestamp == null || timestamp == 0) {
            return "N/A";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return formatter.format(new Date(timestamp));
    }
}