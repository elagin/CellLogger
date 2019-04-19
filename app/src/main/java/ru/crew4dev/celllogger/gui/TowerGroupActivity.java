package ru.crew4dev.celllogger.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import java.util.List;

import ru.crew4dev.celllogger.App;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.TowerGroup;

public class TowerGroupActivity extends AppCompatActivity {

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private TowerGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tower_group);

        adapter = new TowerGroupAdapter(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData(){
        List<TowerGroup> list = App.db().collectDao().getTowerGroups();
        adapter.clearItems();
        adapter.setItems(list);
        adapter.notifyDataSetChanged();
    }

    protected void onResume() {
        loadData();
        super.onResume();
    }
}
