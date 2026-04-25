package com.example.spectraoil;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<HistoryItem> list;
    Context context;

    public HistoryAdapter(Context context, List<HistoryItem> list) {
        this.context = context;
        this.list = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView oilName, statusText;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);

            oilName = itemView.findViewById(R.id.oilName);
            statusText = itemView.findViewById(R.id.statusText);
            icon = itemView.findViewById(R.id.statusIcon);

            itemView.setOnLongClickListener(v -> {

                int pos = getAdapterPosition();

                new AlertDialog.Builder(context)
                        .setTitle("Delete Scan")
                        .setMessage("Delete this scan?")
                        .setPositiveButton("Delete", (d, w) -> {

                            list.remove(pos);
                            notifyItemRemoved(pos);

                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                return true;
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.history_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HistoryItem item = list.get(position);

        holder.oilName.setText(item.oilType);
        holder.statusText.setText(item.status);
        holder.icon.setImageResource(item.icon);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}