package ru.crew4dev.celllogger.gui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.crew4dev.celllogger.App;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.Tools;
import ru.crew4dev.celllogger.data.Tower;
import ru.crew4dev.celllogger.data.TowerGroup;
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
        private TextView textInfo;

        private List<TowerGroup> towerGroups;

        public TowerInfoViewHolder(final @NonNull View itemView) {
            super(itemView);
            view = itemView;
            date = itemView.findViewById(R.id.date);
            min_diff = itemView.findViewById(R.id.min_diff);
            cellId = itemView.findViewById(R.id.cellId);
            lac = itemView.findViewById(R.id.lac);
            dbm = itemView.findViewById(R.id.dbm);
            textInfo = itemView.findViewById(R.id.textInfo);
            towerGroups = App.db().collectDao().getTowerGroups();
        }

        void bind(int position) {
            Tower item = towers.get(position);
            date.setText(Tools.getDate(item.getDate()));
            SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
            if (item.getEndDate() != null) {
                long diffInMillies = item.getEndDate().getTime() - item.getDate().getTime();
                long diffMin = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
                long diffSec = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                diffSec = diffSec - (diffMin* 60);
                min_diff.setText(diffMin + ":" + diffSec);
            }
            cellId.setText("cellId: " + item.getCellId());
            lac.setText("lac: " + item.getLac());
            dbm.setText(item.getDbm() + "dB");
            for (TowerGroup tg : towerGroups) {
                if (tg.towerList.contains(item.getUid())) {
                    textInfo.setText(tg.name);
                    textInfo.setVisibility(View.VISIBLE);
                    break;
                } else {
                    textInfo.setText("");
                    textInfo.setVisibility(View.GONE);
                    Log.d(TAG, "cellId: " + item.getCellId());
                }
            }
        }
    }
}