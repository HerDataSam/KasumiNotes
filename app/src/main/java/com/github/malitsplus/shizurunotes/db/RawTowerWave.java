package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.WaveGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawTowerWave {
    public int id;
    public int wave_group_id;
    public int odds;
    public int enemy_id_1;
    public int enemy_id_2;
    public int enemy_id_3;
    public int enemy_id_4;
    public int enemy_id_5;

    public WaveGroup getWaveGroup(boolean needEnemy) {
        WaveGroup waveGroup = new WaveGroup(id, wave_group_id);
        if (needEnemy) {
            List<Enemy> enemyList = new ArrayList<>();
            List<RawEnemy> rawEnemyList = DBHelper.get().getTowerEnemy(new ArrayList<>(Arrays.asList(enemy_id_1, enemy_id_2, enemy_id_3, enemy_id_4, enemy_id_5)));
            if (rawEnemyList != null) {
                for (RawEnemy rawEnemy : rawEnemyList) {
                    enemyList.add(rawEnemy.getEnemy());
                }
                waveGroup.setEnemyList(enemyList);
            }
        }

        return waveGroup;
    }
}
