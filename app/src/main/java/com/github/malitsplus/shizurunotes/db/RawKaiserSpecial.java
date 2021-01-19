package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.KaiserBattle;

import java.util.ArrayList;
import java.util.List;

public class RawKaiserSpecial {
    public int mode;
    public int wave_group_id;

    public KaiserBattle getKaiserBattle() {
        RawEnemy raw = DBHelper.get().getEnemy(wave_group_id);
        List<RawRestriction> rawRestriction = DBHelper.get().getKaiserRestriction(2000 + mode);
        List<Integer> charaList = new ArrayList<>();
        for(RawRestriction res: rawRestriction) {
            charaList.add(res.unit_id);
        }

        if (raw != null) {
            Enemy boss = raw.getEnemy();
            String bossName = I18N.getString(R.string.text_kaiser_mode, mode);

            return new KaiserBattle(2000 + mode, bossName, boss, charaList);
        }
        else
            return null;
    }
}
