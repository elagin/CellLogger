package ru.crew4dev.celllogger.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import ru.crew4dev.celllogger.App;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.Place;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class PlaceListActivity extends AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        adapter = new PlaceAdapter(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.place_list_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, TowerActivity.class);
        startActivity(intent, null);
        return super.onOptionsItemSelected(item);
    }

    private void loadData(){
        List<Place> placeList = App.db().collectDao().getPlaces();
        for(Place place: placeList){
            place.setTowerList(App.db().collectDao().getTowers(place.placeId));
//            if(place.endDate == null && place.towerList.size() > 0){ //Проставляем дату последней точки
//                place.endDate = place.towerList.get(place.towerList.size()-1).date;
//                App.db().collectDao().update(place);
//            }
        }
        adapter.clearItems();
        adapter.setItems(placeList);
        adapter.notifyDataSetChanged();
    }

    protected void onResume() {
        loadData();
        super.onResume();
    }
}
