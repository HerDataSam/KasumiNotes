package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.SekaiEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RawSekaiEvent {
    public int sekai_id;
    public String name;
    public String description;
    public String boss_time_from;
    public String boss_time_to;
    public String unit_name = "";
    public int sekai_enemy_id;
    private String sekaiEventText;

    public SekaiEvent getSekaiEvent(){
        RawEnemy raw = DBHelper.get().getSekaiEnemy(sekai_enemy_id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");
        LocalDateTime startDate;
        if (boss_time_from.length() < 11) {
            LocalDate start = LocalDate.parse(boss_time_from, formatter);
            startDate = start.atTime(0, 0);
        } else {
            startDate = LocalDateTime.parse(boss_time_from, formatterTime);
        }
        LocalDateTime endDate = LocalDateTime.parse(boss_time_to, formatterTime);

        if (raw != null){
            Enemy boss = raw.getEnemy();
            String eventName = String.format("%s - %s", name, boss.name);

            return new SekaiEvent(
                    sekai_id,
                    name,
                    description,
                    startDate,
                    endDate,
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
