package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.QuestArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RawQuestArea {
    public int area_id;
    public String area_name;
    public int map_type;
    public String sheet_id;
    public String que_id;
    public String start_time;
    public String end_time;

    public QuestArea getQuestArea() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");

        return new QuestArea(area_id, area_name, LocalDateTime.parse(start_time, formatter));
    }
}
