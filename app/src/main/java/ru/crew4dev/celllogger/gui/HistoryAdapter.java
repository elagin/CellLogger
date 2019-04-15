package ru.crew4dev.celllogger.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.Tower;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.TowerInfoViewHolder> {

    final String TAG = "HistoryAdapter";
    private final List<Tower> towers = new ArrayList<>();

    public void setItems(List<Tower> items) {
        towers.addAll(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        towers.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryAdapter.TowerInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tower_row, viewGroup, false);
        return new TowerInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.TowerInfoViewHolder holder, int position) {
        holder.bind(towers.get(position));
    }

    public static final SimpleDateFormat dateFullFormat = new SimpleDateFormat("HH:mm dd.MM.yy");
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    public int getItemCount() {
        return towers.size();
    }

    public class TowerInfoViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView cellId;
        private TextView lac;
        private TextView dbm;

        public TowerInfoViewHolder(final @NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            cellId = itemView.findViewById(R.id.cellId);
            lac = itemView.findViewById(R.id.lac);
            dbm = itemView.findViewById(R.id.dbm);
        }

        void bind(Tower item) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd.MM", Locale.getDefault());
            date.setText(sdf.format(item.getDate()));
            cellId.setText("cellId: " + String.valueOf(item.getCellId()));
            lac.setText("lac: " + String.valueOf(item.getLac()));
            dbm.setText(String.valueOf(item.getDbm()) + "dB");
        }
    }
}
