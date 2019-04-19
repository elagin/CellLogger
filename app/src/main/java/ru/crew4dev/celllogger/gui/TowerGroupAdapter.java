package ru.crew4dev.celllogger.gui;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.TowerGroup;
import ru.crew4dev.celllogger.gui.modeles.interfaces.Delete;

public class TowerGroupAdapter  extends RecyclerView.Adapter<TowerGroupAdapter.TowerGroupViewHolder>{

    final String TAG = "TowerAdapter";
    private final List<TowerGroup> list = new ArrayList<>();
    private final Context context;

    public TowerGroupAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<TowerGroup> items) {
        list.addAll(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        list.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TowerGroupAdapter.TowerGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tower_group_row, parent, false);
        return new TowerGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TowerGroupAdapter.TowerGroupViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TowerGroupViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, Delete {

        private TextView textName;
        private final MenuItem.OnMenuItemClickListener onEditMenu = menuItem -> {
            if (menuItem.getTitle().equals(context.getResources().getString(R.string.delete))) {
                TowerGroup group = list.get(getAdapterPosition());
                //showConfirm(context, this, place.placeId, "Удалить заявку от: " + place.getStartDate() + "?");
            }
            return true;
        };

        public TowerGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
//            itemView.setOnCreateContextMenuListener(this);
//            itemView.setOnClickListener(v -> {
//                int i = getAdapterPosition();
//                TowerGroup place = list.get(i);
//                Intent intent = new Intent(itemView.getContext(), TowerActivity.class);
//                intent.putExtra(Constants.PLACE_ID, place.placeId);
//                startActivity(itemView.getContext(), intent, null);
//            });
        }

        void bind(TowerGroup item) {
            textName.setText(item.name);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Ignore = menu.add(this.getAdapterPosition(), v.getId(), 2, context.getResources().getString(R.string.delete));
            Ignore.setOnMenuItemClickListener(onEditMenu);
        }

        @Override
        public void positive(long placeId) {
//            App.db().collectDao().deleteTowers(placeId);
//            App.db().collectDao().deletePlace(placeId);
//            for (Place item : list) {
//                if (item.placeId.equals(placeId)) {
//                    list.remove(item);
//                    notifyDataSetChanged();
//                    break;
//                }
//            }
        }

        @Override
        public void negative() {
        }
    }
}