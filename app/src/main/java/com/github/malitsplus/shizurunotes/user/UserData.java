package com.github.malitsplus.shizurunotes.user;

import java.util.List;
import java.util.Map;

public final class UserData {

    public List<Integer> lastEquipmentIds;
    public Map<String, Integer> contentsMaxArea;
    public Map<String, Integer> contentsMaxLevel;
    public Map<String, Integer> contentsMaxRank;
    public Map<String, Integer> contentsMaxEquipment;

    // the last chara list filter
    public String AttackType;
    public String Position;
    public String Sort;
    public boolean isAsc;

}
