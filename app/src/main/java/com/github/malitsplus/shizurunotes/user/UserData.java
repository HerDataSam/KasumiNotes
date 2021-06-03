package com.github.malitsplus.shizurunotes.user;

import com.github.malitsplus.shizurunotes.data.SkillPrefab;

import java.util.List;
import java.util.Map;

public final class UserData {

    public String lastInfoMessage;

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
        public int loveLevel;
        public List<Integer> skillLevels;
        public boolean isBookmarkLocked;

        MyCharaData(int charaId, int rarity, int level, int rank, List<Integer> equipment, int uniqueEquipment, int loveLevel, List<Integer> skillLevels, boolean isBookmarkLocked) {
            this.charaId = charaId;
            this.rarity = rarity;
            this.level = level;
            this.rank = rank;
            this.equipment = equipment;
            this.uniqueEquipment = uniqueEquipment;
            this.loveLevel = loveLevel;
            this.skillLevels = skillLevels;
            this.isBookmarkLocked = isBookmarkLocked;
        }
    }

    public Map<String, Extension> extensionMap;

    public static final class Extension {
        public String title;
        public String madeBy;
        public String version;

        Extension(String title, String madeBy, String version) {
            this.title = title;
            this.madeBy = madeBy;
            this.version = version;
        }
    }

    public Map<Integer, Nickname> nicknames;

    public static final class Nickname {
        public String shortestNickname;
        public String shortNickname;

        Nickname(String shortestNickname, String shortNickname) {
            this.shortestNickname = shortestNickname;
            this.shortNickname = shortNickname;
        }
    }

    public Map<Integer, List<SkillPrefab>> skillPrefabs;

}


