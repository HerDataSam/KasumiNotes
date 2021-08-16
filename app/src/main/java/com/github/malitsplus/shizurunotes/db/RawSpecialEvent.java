package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.SpecialBattle;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class RawSpecialEvent {
    public int boss_id;
    public String name;
    public String max_raid_hp;
    public int restriction_group_id;
    public int wave_group_id;

    public int enemy_id_1;
    public int enemy_id_2;
    public int enemy_id_3;
    public int enemy_id_4;
    public int enemy_id_5;

    public SpecialBattle getKaiserBattle() {
        List<RawRestriction> rawRestriction = DBHelper.get().getKaiserRestriction(boss_id);
        List<Integer> charaList = new ArrayList<>();
        for(RawRestriction res: rawRestriction) {
            charaList.add(res.unit_id);
        }
        return commonPart(charaList);
    }

    public SpecialBattle getLegionBattle() {
        List<RawLegionEffectUnit> rawLegionEffectUnits
                = DBHelper.get().getLegionEffect(boss_id);
        List<Integer> charaList = new ArrayList<>();
        for (RawLegionEffectUnit effectUnit: rawLegionEffectUnits) {
            charaList.add(effectUnit.unit_id);
        }
        return commonPart(charaList);
    }

    public SpecialBattle commonPart(List<Integer> charaList) {
        List<RawEnemy> rawEnemyList = DBHelper.get().getEnemy(Lists.newArrayList(enemy_id_1, enemy_id_2, enemy_id_3, enemy_id_4, enemy_id_5));
        List<Enemy> enemyList = new ArrayList<>();
        for (RawEnemy raw: rawEnemyList) {
            enemyList.add(raw.getEnemy());
        }

        if (enemyList.size() > 0) {
            long hp;
            try {
                hp = Long.parseLong(max_raid_hp);
            } catch (Exception e) {
                hp = 0L;
            }
            return new SpecialBattle(boss_id, name, enemyList, hp, charaList);
        }
        else
            return null;

    }
}
