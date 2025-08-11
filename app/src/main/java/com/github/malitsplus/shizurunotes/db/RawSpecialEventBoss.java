package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.SpecialBattle;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class RawSpecialEventBoss {
    public int mode;
    public int purpose_count;
    public int trigger_hp;
    public int wave_group_id;

    public int enemy_id_1;
    public int enemy_id_2;
    public int enemy_id_3;
    public int enemy_id_4;
    public int enemy_id_5;

    int boss_id = 0;
    String boss_name = "";

    public SpecialBattle getKaiserBattle() {
        boss_id = DBHelper.get().getKaiserSpecialBossGuess();

        List<RawRestriction> rawRestriction = DBHelper.get().getKaiserRestriction(boss_id);
        List<Integer> charaList = new ArrayList<>();
        for (RawRestriction res : rawRestriction) {
            charaList.add(res.unit_id);
        }

        boss_name = I18N.getString(R.string.text_kaiser_mode, mode);
        return commonPart(charaList);
    }

    public SpecialBattle getLegionBattle() {
        boss_id = DBHelper.get().getKaiserSpecialBossGuess();

        List<RawLegionEffectUnit> rawLegionEffectUnits = DBHelper.get().getLegionEffect(boss_id);
        List<Integer> charaList = new ArrayList<>();
        for (RawLegionEffectUnit effectUnit : rawLegionEffectUnits) {
            charaList.add(effectUnit.unit_id);
        }

        boss_name = I18N.getString(R.string.text_boss_mode, mode);
        return commonPart(charaList);
    }

    private SpecialBattle commonPart(List<Integer> charaList) {
        List<RawEnemy> rawEnemyList = DBHelper.get().getEnemy(Lists.newArrayList(enemy_id_1, enemy_id_2, enemy_id_3, enemy_id_4, enemy_id_5));
        List<Enemy> enemyList = new ArrayList<>();
        for (RawEnemy raw : rawEnemyList) {
            enemyList.add(raw.getEnemy());
        }

        if (enemyList.size() > 0) {
            if (purpose_count > 0)
                boss_name += " (count: " + purpose_count + ")";
            else if (trigger_hp > 0)
                boss_name += " (HP: " + trigger_hp + "%)";
            return new SpecialBattle(boss_id, boss_name, enemyList, 0, charaList);
        } else
            return null;
    }
}
