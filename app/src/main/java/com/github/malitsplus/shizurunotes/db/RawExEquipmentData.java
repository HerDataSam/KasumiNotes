package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Property;

public class RawExEquipmentData {
    public int ex_equipment_id;
    public String name;
    public String description;
    public int rarity;
    public int category;
    public int restriction_id;
    public Boolean clan_battle_equip_flag;
    public double default_hp;
    public double max_hp;
    public double default_atk;
    public double max_atk;
    public double default_magic_str;
    public double max_magic_str;
    public double default_def;
    public double max_def;
    public double default_magic_def;
    public double max_magic_def;
    public double default_physical_critical;
    public double max_physical_critical;
    public double default_magic_critical;
    public double max_magic_critical;
    public double default_wave_hp_recovery;
    public double max_wave_hp_recovery;
    public double default_wave_energy_recovery;
    public double max_wave_energy_recovery;
    public double default_dodge;
    public double max_dodge;
    public double default_physical_penetrate;
    public double max_physical_penetrate;
    public double default_magic_penetrate;
    public double max_magic_penetrate;
    public double default_life_steal;
    public double max_life_steal;
    public double default_hp_recovery_rate;
    public double max_hp_recovery_rate;
    public double default_energy_recovery_rate;
    public double max_energy_recovery_rate;
    public double default_energy_reduce_rate;
    public double max_energy_reduce_rate;
    public double default_accuracy;
    public double max_accuracy;
    public int passive_skill_id_1;
    public int passive_skill_id_2;
    public int passive_skill_power;

    public Property getDefaultProperty() {
        return new Property(
                default_hp,
                default_atk,
                default_magic_str,
                default_def,
                default_magic_def,
                default_physical_critical,
                default_magic_critical,
                default_wave_hp_recovery,
                default_wave_energy_recovery,
                default_dodge,
                default_physical_penetrate,
                default_magic_penetrate,
                default_life_steal,
                default_hp_recovery_rate,
                default_energy_recovery_rate,
                default_energy_reduce_rate,
                default_accuracy
        );
    }

    public Property getMaxProperty() {
        return new Property(
                max_hp,
                max_atk,
                max_magic_str,
                max_def,
                max_magic_def,
                max_physical_critical,
                max_magic_critical,
                max_wave_hp_recovery,
                max_wave_energy_recovery,
                max_dodge,
                max_physical_penetrate,
                max_magic_penetrate,
                max_life_steal,
                max_hp_recovery_rate,
                max_energy_recovery_rate,
                max_energy_reduce_rate,
                max_accuracy
        );
    }
/*
    public Property getDefaultProperty(){
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
 */
}
