package ru.crew4dev.celllogger.data;

import java.util.ArrayList;
import java.util.List;

public class TowerList {
    private List<Tower> towers = new ArrayList<>();

    public boolean isExistTower(Tower tower) {
        for (Tower item : towers) {
            if (item.cellId == tower.cellId && item.lac == tower.lac)
                return true;
        }
        return false;
    }

    public void addAll(List<Tower> list){
        towers.addAll(list);
    }

    public void add(Tower tower){
        towers.add(tower);
    }

    public List<Tower> getTowers() {
        List<Tower> out = new ArrayList<>();
        for(Tower item: towers){
            out.add(item);
        }
        return out;
    }
}
