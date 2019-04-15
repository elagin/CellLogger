package ru.crew4dev.celllogger.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.App;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.Place;

import android.os.Bundle;

import java.util.List;

public class PlaceListActivity extends AppCompatActivity {

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private PlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new PlaceAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDate();
    }

    private void loadDate(){
        List<Place> placeList = App.db().collectDao().getPlaces();
        for(Place place: placeList){
            place.setTowerList(App.db().collectDao().getTowers(place.placeId));
            if(place.endDate == null){ //Проставляем дату последней точки
                place.endDate = place.towerList.get(place.towerList.size()-1).date;
                App.db().collectDao().update(place);
            }
        }
        adapter.clearItems();
        adapter.setItems(placeList);
        adapter.notifyDataSetChanged();
    }
}
