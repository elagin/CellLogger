package ru.crew4dev.celllogger.gui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.App;
import ru.crew4dev.celllogger.Constants;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.Tools;
import ru.crew4dev.celllogger.data.Place;
import ru.crew4dev.celllogger.gui.modeles.interfaces.Delete;

import static androidx.core.content.ContextCompat.startActivity;
import static ru.crew4dev.celllogger.gui.dialogs.DeleteConfirm.showConfirm;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    final String TAG = "TowerAdapter";
    private final List<Place> places = new ArrayList<>();
    private final Context context;

    public PlaceAdapter(Context context) {
        this.context = context;
    }

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

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, Delete {

        private TextView startDate;
        private TextView endDate;
        private TextView textTowerCount;
        private TextView placeName;
        private final MenuItem.OnMenuItemClickListener onEditMenu = menuItem -> {
            if (menuItem.getTitle().equals(context.getResources().getString(R.string.delete))) {
                Place place = places.get(getAdapterPosition());
                showConfirm(context, this, place.placeId, "Удалить заявку от: " + place.getStartDate() + "?");
            }
            return true;
        };

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            startDate = itemView.findViewById(R.id.textViewStartDate);
            endDate = itemView.findViewById(R.id.textViewEndDate);
            placeName = itemView.findViewById(R.id.placeName);
            textTowerCount = itemView.findViewById(R.id.textTowerCount);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(v -> {
                int i = getAdapterPosition();
                Place place = places.get(i);
                Intent intent = new Intent(itemView.getContext(), TowerActivity.class);
                intent.putExtra(Constants.PLACE_ID, place.placeId);
                startActivity(itemView.getContext(), intent, null);
            });
        }

        void bind(Place item) {
            startDate.setText(Tools.getDate(item.getStartDate()));
            if (item.getEndDate() != null)
                endDate.setText(Tools.getDate(item.getEndDate()));
            else
                endDate.setText("Не известно");
            textTowerCount.setText("Башни: " + String.valueOf(item.towerList.size()));
            placeName.setText(item.getName());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Ignore = menu.add(this.getAdapterPosition(), v.getId(), 2, context.getResources().getString(R.string.delete));
            Ignore.setOnMenuItemClickListener(onEditMenu);
        }

        @Override
        public void positive(long placeId) {
            App.db().collectDao().deleteTowers(placeId);
            App.db().collectDao().deletePlace(placeId);
            for(Place item: places){
                if(item.placeId.equals(placeId)) {
                    places.remove(item);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void negative() {
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
