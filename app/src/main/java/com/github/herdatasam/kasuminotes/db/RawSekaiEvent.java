package com.github.herdatasam.kasuminotes.db;

import com.github.herdatasam.kasuminotes.data.SekaiEvent;

public class RawSekaiEvent {
    public int sekai_id;
    public String name;
    public String description;
    public String boss_time_from;
    public String unit_name = "";
    public int sekai_enemy_id;

    public SekaiEvent getSekaiEvent(){
        RawEnemy raw = DBHelper.get().getSekaiEnemy(sekai_enemy_id);
        if (raw != null){
            return new SekaiEvent(
                    sekai_id,
                    name,
                    description,
                    boss_time_from,
                    sekai_enemy_id,
                    unit_name,
                    name + " " + unit_name,
                    raw.getEnemy()
            );
        } else {
            return null;
        }
    }
}
