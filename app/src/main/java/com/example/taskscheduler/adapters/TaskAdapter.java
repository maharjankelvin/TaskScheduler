package com.example.taskscheduler.adapters;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskscheduler.R;
import com.example.taskscheduler.models.Task;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private static final int VIEW_TYPE_FCFS = 0;
    private static final int VIEW_TYPE_SJF = 1;
    private static final int VIEW_TYPE_PRIORITY = 2;

    private List<Task> tasks = new ArrayList<>();
    private String sortingAlgorithm = "First-Come-First-Served (FCFS)"; // Default sorting algorithm
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    private OnItemClickListener listener;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public interface OnEditClickListener {
        void onEditClick(Task task);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener editListener) {
        this.editListener = editListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void setSortingAlgorithm(String algorithm) {
        this.sortingAlgorithm = algorithm;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        switch (sortingAlgorithm) {
            case "Shortest Job First (SJF)":
                return VIEW_TYPE_SJF;
            case "Priority":
                return VIEW_TYPE_PRIORITY;
            default:
                return VIEW_TYPE_FCFS;
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView titleTextView;
        private final TextView categoryTextView;
        private final TextView priorityTextView;
        private final TextView durationTextView;
        private final TextView subtitleTextView;
        private final ImageView editIcon;
        private final ImageView deleteIcon;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            priorityTextView = itemView.findViewById(R.id.priorityTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            subtitleTextView = itemView.findViewById(R.id.subtitleTextView);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);

            cardView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(tasks.get(position));
                }
            });
            
            editIcon.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (editListener != null && position != RecyclerView.NO_POSITION) {
                    editListener.onEditClick(tasks.get(position));
                }
            });

            deleteIcon.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (deleteListener != null && position != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(tasks.get(position));
                }
            });
        }

        void bind(Task task) {
            // Set title
            titleTextView.setText(task.getTitle());

            // Set subtitle based on sorting algorithm
            switch (sortingAlgorithm) {
                case "First-Come-First-Served (FCFS)":
                    subtitleTextView.setText("Created: " + dateFormat.format(task.getCreatedAt()));
                    break;
                case "Shortest Job First (SJF)":
                    subtitleTextView.setText(task.getDescription());
                    break;
                case "Priority":
                    subtitleTextView.setText(task.getDescription());
                    break;
            }

            // Set category text with color
            categoryTextView.setText(task.getCategory());
            categoryTextView.setTextColor(getCategoryColor(task.getCategory()));
            
            // Set priority text with color
            String priorityText = "Priority: " + getPriorityText(task.getPriority());
            priorityTextView.setText(priorityText);
            
            // Set duration with highlighting for SJF
            if (sortingAlgorithm.equals("Shortest Job First (SJF)")) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString duration = new SpannableString("⏱ " + task.getDuration() + " minutes");
                duration.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, duration.length(), 0);
                builder.append(duration);
                durationTextView.setText(builder);
            } else {
                durationTextView.setText(task.getDuration() + " minutes");
            }

            // Set priority color for Priority sorting
            if (sortingAlgorithm.equals("Priority")) {
                int color;
                switch (task.getPriority()) {
                    case 3: // High
                        color = ContextCompat.getColor(itemView.getContext(), R.color.priority_high);
                        break;
                    case 2: // Medium
                        color = ContextCompat.getColor(itemView.getContext(), R.color.priority_medium);
                        break;
                    case 1: // Low
                        color = ContextCompat.getColor(itemView.getContext(), R.color.priority_low);
                        break;
                    default:
                        color = Color.BLACK;
                }
                priorityTextView.setTextColor(color);
            } else {
                priorityTextView.setTextColor(Color.BLACK);
            }
        }

        private String getPriorityText(int priority) {
            switch (priority) {
                case 1:
                    return "Low";
                case 2:
                    return "Medium";
                case 3:
                    return "High";
                default:
                    return "Unknown";
            }
        }

        private int getCategoryColor(String category) {
            switch (category) {
                case "Work":
                    return ContextCompat.getColor(itemView.getContext(), R.color.category_work);
                case "Home":
                    return ContextCompat.getColor(itemView.getContext(), R.color.category_home);
                default:
                    return ContextCompat.getColor(itemView.getContext(), R.color.teal_700);
            }
        }
    }
} 