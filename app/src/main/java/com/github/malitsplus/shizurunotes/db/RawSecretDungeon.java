package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.SecretDungeon;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawSecretDungeon {
    public int dungeon_area_id;
    public String dungeon_name;
    public String description;
    public int difficulty;
    public int floor_num;
    public int wave_group_id;
    public int enemy_id_1;
    public int enemy_id_2;
    public int enemy_id_3;
    public int enemy_id_4;
    public int enemy_id_5;
    public String start_time;
    public String end_time;

    public SecretDungeon getSecretDungeon() {
        List<RawEnemy> rawEnemyList = DBHelper.get().getEnemy(new ArrayList<>(Arrays.asList(enemy_id_1, enemy_id_2, enemy_id_3, enemy_id_4, enemy_id_5)));
        List<Enemy> enemyList = new ArrayList<>();
        for (RawEnemy raw : rawEnemyList) {
            enemyList.add(raw.getEnemy());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");

        if (enemyList.size() > 0) {
            return new SecretDungeon(
                    dungeon_area_id,
                    wave_group_id,
                    difficulty,
                    floor_num,
                    enemyList,
                    LocalDateTime.parse(start_time, formatter),
                    LocalDateTime.parse(end_time, formatter)
            );
        } else {
            return null;
        }
    }
}
