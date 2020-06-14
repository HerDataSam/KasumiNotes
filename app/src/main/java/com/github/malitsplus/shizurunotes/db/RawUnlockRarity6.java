package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Property;

public class RawUnlockRarity6 {
    public int unit_id;
    public int slot_id;
    public int unlock_level;
    public int unlock_flag;
    public int consume_gold;
    public int material_type;
    public int material_id;
    public int material_count;
    public int hp;
    public int atk;
    public int magic_str;
    public int def;
    public int magic_def;
    public int physical_critical;
    public int magic_critical;
    public int wave_hp_recovery;
    public int wave_energy_recovery;
    public int dodge;
    public int physical_penetrate;
    public int magic_penetrate;
    public int life_steal;
    public int hp_recovery_rate;
    public int energy_recovery_rate;
    public int energy_reduce_rate;
    public int accuracy;

    public Property getProperty(){
        return new Property(
                hp,
                atk,
                magic_str,
                def,
                magic_def,
                physical_critical,
                magic_critical,
                wave_hp_recovery,
                wave_energy_recovery,
                dodge,
                physical_penetrate,
                magic_penetrate,
                life_steal,
                hp_recovery_rate,
                energy_recovery_rate,
                energy_reduce_rate,
                accuracy
        );
    }
}
