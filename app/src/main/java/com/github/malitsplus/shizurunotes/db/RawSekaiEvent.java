package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.SekaiEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RawSekaiEvent {
    public int sekai_id;
    public String name;
    public String description;
    public String boss_time_from;
    public String unit_name = "";
    public int sekai_enemy_id;
    private String sekaiEventText;

    public SekaiEvent getSekaiEvent(){
        RawEnemy raw = DBHelper.get().getSekaiEnemy(sekai_enemy_id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate startDate = LocalDate.parse(boss_time_from.substring(0, 10), formatter);

        Enemy boss = raw.getEnemy();
        String eventName = String.format("%s - %s", name, boss.name);

        if (raw != null){
            return new SekaiEvent(
                    sekai_id,
                    name,
                    description,
                    startDate,
                    sekai_enemy_id,
                    unit_name,
                    eventName,
                    boss
            );
        } else {
            return null;
        }
    }
}
