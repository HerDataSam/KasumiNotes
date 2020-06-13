package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Property;
import com.github.malitsplus.shizurunotes.data.UnitCoefficient;

public class RawUnitCoefficient {
    public double hp_coefficient;
    public double atk_coefficient;
    public double magic_str_coefficient;
    public double def_coefficient;
    public double magic_def_coefficient;
    public double physical_critical_coefficient;
    public double magic_critical_coefficient;
    public double wave_hp_recovery_coefficient;
    public double wave_energy_recovery_coefficient;
    public double dodge_coefficient;
    public double physical_penetrate_coefficient;
    public double magic_penetrate_coefficient;
    public double life_steal_coefficient;
    public double hp_recovery_rate_coefficient;
    public double energy_recovery_rate_coefficient;
    public double energy_reduce_rate_coefficient;
    public double skill_lv_coefficient;
    public int exskill_evolution_coefficient;
    public double overall_coefficient;
    public double accuracy_coefficient;
    public int skill1_evolution_coefficient;
    public double skill1_evolution_slv_coefficient;
    public int ub_evolution_coefficient;
    public double ub_evolution_slv_coefficient;

    public Property getProperty() {
        return new Property(
                hp_coefficient,
                atk_coefficient,
                magic_str_coefficient,
                def_coefficient,
                magic_def_coefficient,
                physical_critical_coefficient,
                magic_critical_coefficient,
                wave_hp_recovery_coefficient,
                wave_energy_recovery_coefficient,
                dodge_coefficient,
                physical_penetrate_coefficient,
                magic_penetrate_coefficient,
                life_steal_coefficient,
                hp_recovery_rate_coefficient,
                energy_recovery_rate_coefficient,
                energy_reduce_rate_coefficient,
                accuracy_coefficient
        );
    }

    public UnitCoefficient getCoefficient() {
        return new UnitCoefficient(
                getProperty(),
                skill_lv_coefficient,
                exskill_evolution_coefficient,
                overall_coefficient,
                skill1_evolution_coefficient,
                skill1_evolution_slv_coefficient,
                ub_evolution_coefficient,
                ub_evolution_slv_coefficient
        );
    }
}
