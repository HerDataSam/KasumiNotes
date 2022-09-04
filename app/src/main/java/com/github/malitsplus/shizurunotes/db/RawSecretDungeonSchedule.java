package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.SecretDungeonPeriod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RawSecretDungeonSchedule {
    public int dungeon_area_id;
    public String dungeon_name;
    public String description;
    public String teaser_time;
    public String start_time;
    public String count_start_time;
    public String end_time;
    public String close_time;

    public SecretDungeonPeriod getSecretDungeonPeriod() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");
        return new SecretDungeonPeriod(
                dungeon_area_id,
                dungeon_name,
                description,
                LocalDateTime.parse(teaser_time, formatter),
                LocalDateTime.parse(start_time, formatter),
                LocalDateTime.parse(count_start_time, formatter),
                LocalDateTime.parse(end_time, formatter),
                LocalDateTime.parse(close_time, formatter)
        );
    }
}
