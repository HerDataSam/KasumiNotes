package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.KaiserBattle;

import java.util.ArrayList;
import java.util.List;

public class RawKaiserEvent {
    public int kaiser_boss_id;
    public String name;
    public int restriction_group_id;
    public int wave_group_id;

    public KaiserBattle getKaiserBattle() {
        RawEnemy raw = DBHelper.get().getEnemy(wave_group_id);
        List<RawRestriction> rawRestriction = DBHelper.get().getKaiserRestriction(kaiser_boss_id);
        List<Integer> charaList = new ArrayList<>();
        for(RawRestriction res: rawRestriction) {
            charaList.add(res.unit_id);
        }

        if (raw != null) {
            Enemy boss = raw.getEnemy();

            return new KaiserBattle(kaiser_boss_id, name, boss, charaList);
        }
        else
            return null;
    }
}
