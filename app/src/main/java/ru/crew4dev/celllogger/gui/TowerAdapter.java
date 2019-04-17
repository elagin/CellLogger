package ru.crew4dev.celllogger.gui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.Tower;
import ru.crew4dev.celllogger.data.TowerList;

public class TowerAdapter extends RecyclerView.Adapter<TowerAdapter.TowerInfoViewHolder> {

    private final String TAG = "TowerAdapter";
    private final TowerList towers = new TowerList();
    private final Context context;

    public TowerAdapter(Context context) {
        this.context = context;
    }

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
    public TowerAdapter.TowerInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tower_row, viewGroup, false);
        return new TowerInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TowerAdapter.TowerInfoViewHolder holder, int position) {
        holder.bind(position);
        final Tower model = towers.get(position);
        holder.view.setBackgroundColor(model.isSelected() ? Color.GREEN : Color.WHITE);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                towers.setSelected(position);
                holder.view.setBackgroundColor(model.isSelected() ? Color.GREEN : Color.WHITE);
                ((TowerActivity) context).enableActionMark(towers.haveSelected());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return towers.size();
    }

    public class TowerInfoViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView date;
        private TextView min_diff;
        private TextView cellId;
        private TextView lac;
        private TextView dbm;

        public TowerInfoViewHolder(final @NonNull View itemView) {
            super(itemView);
            view = itemView;
            date = itemView.findViewById(R.id.date);
            min_diff = itemView.findViewById(R.id.min_diff);
            cellId = itemView.findViewById(R.id.cellId);
            lac = itemView.findViewById(R.id.lac);
            dbm = itemView.findViewById(R.id.dbm);
        }

        void bind(int position) {
            Tower item = towers.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd.MM", Locale.getDefault());
            date.setText(sdf.format(item.getDate()));
            if (item.getEndDate() != null) {
                long diffInMillies = item.getEndDate().getTime() - item.getDate().getTime();
                long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
                min_diff.setText(String.valueOf(diff));
            }
            cellId.setText("cellId: " + String.valueOf(item.getCellId()));
            lac.setText("lac: " + String.valueOf(item.getLac()));
            dbm.setText(String.valueOf(item.getDbm()) + "dB");
        }
    }
}
