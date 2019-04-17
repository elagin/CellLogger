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

    public void addAll(List<Tower> list) {
        towers.clear();
        towers.addAll(list);
    }

    public void add(Tower tower) {
        towers.add(tower);
    }

    public List<Tower> getTowers() {
        List<Tower> out = new ArrayList<>();
        for (Tower item : towers) {
            out.add(item);
        }
        return out;
    }

    public Tower getLast() {
        if (towers.size() > 0)
            return towers.get(towers.size() - 1);
        else
            return null;
    }

    public int size() {
        return towers.size();
    }

    public void clear() {
        towers.clear();
    }

    public Tower get(int position) {
        return towers.get(position);
    }

    public boolean haveSelected() {
        for (Tower tower : towers) {
            if (tower.isSelected())
                return true;
        }
        return false;
    }

    public void setSelected(int position) {
        for (Tower item : towers) {
            if (item.cellId == towers.get(position).cellId) {
                item.setSelected(!item.isSelected());
            }
        }
    }
}
