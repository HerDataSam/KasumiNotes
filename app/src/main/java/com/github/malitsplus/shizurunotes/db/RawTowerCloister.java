package com.github.malitsplus.shizurunotes.db;

import java.util.ArrayList;
import java.util.List;

public class RawTowerCloister {
    public int tower_cloister_quest_id;
    public int daily_limit;
    public int limit_time;
    public int recovery_hp_rate;
    public int recovery_tp_rate;
    public int start_tp_rate;
    public int fix_reward_group_id;
    public int drop_reward_group_id;
    public int background_1;
    public int wave_group_id_1;
    public int background_2;
    public int wave_group_id_2;
    public int background_3;
    public int wave_group_id_3;
    public String wave_bgm;
    public int reward_image_1;
    public int reward_image_2;
    public int reward_image_3;
    public int reward_image_4;
    public int reward_image_5;
    public int background;
    public int bg_position;

    public List<Integer> getWaveGroupId() {
        List<Integer> waveGroup = new ArrayList<>();
        waveGroup.add(wave_group_id_1);
        waveGroup.add(wave_group_id_2);
        waveGroup.add(wave_group_id_3);

        return waveGroup;
    }
}
