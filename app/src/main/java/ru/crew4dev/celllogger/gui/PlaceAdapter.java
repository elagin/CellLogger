package ru.crew4dev.celllogger.gui;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.Place;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    final String TAG = "HistoryAdapter";
    private final List<Place> places = new ArrayList<>();

    public void setItems(List<Place> items) {
        places.addAll(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        places.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_row, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.PlaceViewHolder holder, int position) {
        holder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        private TextView startDate;
        private TextView endDate;
        private TextView textTowerCount;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            startDate = itemView.findViewById(R.id.textViewStartDate);
            endDate = itemView.findViewById(R.id.textViewEndDate);
            textTowerCount = itemView.findViewById(R.id.textTowerCount);
        }

        void bind(Place item) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd.MM", Locale.getDefault());
            startDate.setText(sdf.format(item.getStartDate()));
            if(item.getEndDate() != null)
                endDate.setText(sdf.format(item.getEndDate()));
            else
                endDate.setText("Не известно");
            textTowerCount.setText(String.valueOf(item.towerList.size()));
        }
    }

    @RequiresApi(28)
    private static class OnUnhandledKeyEventListenerWrapper implements View.OnUnhandledKeyEventListener {
        private ViewCompat.OnUnhandledKeyEventListenerCompat mCompatListener;

        OnUnhandledKeyEventListenerWrapper(ViewCompat.OnUnhandledKeyEventListenerCompat listener) {
            this.mCompatListener = listener;
        }

        public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
            return this.mCompatListener.onUnhandledKeyEvent(v, event);
        }
    }
}
