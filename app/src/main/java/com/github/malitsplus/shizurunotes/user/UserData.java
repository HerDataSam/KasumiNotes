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

    public Map<String, List<MyCharaData>> myCharaData;

    public static final class MyCharaData {
        public int charaId;
        public int rarity;
        public int level;
        public int rank;
        public List<Integer> equipment;
        public int uniqueEquipment;

        MyCharaData(int charaId, int rarity, int level, int rank, List<Integer> equipment, int uniqueEquipment) {
            this.charaId = charaId;
            this.rarity = rarity;
            this.level = level;
            this.rank = rank;
            this.equipment = equipment;
            this.uniqueEquipment = uniqueEquipment;
        }
    }

}


