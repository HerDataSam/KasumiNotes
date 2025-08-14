package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.SecretDungeonPeriod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class SecretDungeonSchedule(
    @Column("dungeon_area_id") val dungeonAreaId: Int,
    @Column("dungeon_name") val dungeonName: String,
    val description: String,
    @Column("teaser_time") val teaserTime: String,
    @Column("start_time") val startTime: String,
    @Column("count_start_time") val countStartTime: String,
    @Column("end_time") val endTime: String,
    @Column("close_time") val closeTime: String,
) {
    val secretDungeonPeriod: SecretDungeonPeriod
        get() {
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");
            return SecretDungeonPeriod(
                    dungeonAreaId,
                    dungeonName,
                    description,
                    LocalDateTime.parse(teaserTime, formatter),
                    LocalDateTime.parse(startTime, formatter),
                    LocalDateTime.parse(countStartTime, formatter),
                    LocalDateTime.parse(endTime, formatter),
                    LocalDateTime.parse(closeTime, formatter)
            );
        }
}
