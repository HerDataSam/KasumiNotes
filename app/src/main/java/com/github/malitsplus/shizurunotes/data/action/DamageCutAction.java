package com.github.malitsplus.shizurunotes.data.action;

import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DamageCutAction extends ActionParameter {

    enum DamageType {
        physics(1),
        magic(2),
        all(3);

        private int value;
        DamageType(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }

        public static DamageType parse(int value){
            for(DamageType item : DamageType.values()){
                if(item.getValue() == value)
                    return item;
            }
            return physics;
        }

    }

    protected DamageType damageType;
    protected List<ActionValue> durationValues = new ArrayList<>();

    @Override
    protected void childInit() {
        super.childInit();
        damageType = DamageType.parse((int) actionDetail1);
        actionValues.add(new ActionValue(actionValue1, actionValue2, null));
        durationValues.add(new ActionValue(actionValue3, actionValue4, null));
    }

    @Override
    public String localizedDetail(int level, Property property) {
        switch (damageType) {
            case physics:
                return I18N.getString(R.string.Reduce_s1_physical_damage_taken_by_s2_for_s3,
                        buildExpression(level, actionValues, RoundingMode.UNNECESSARY, property),
                        targetParameter.buildTargetClause(),
                        buildExpression(level, durationValues, RoundingMode.UNNECESSARY, property));
            case magic:
                return I18N.getString(R.string.Reduce_s1_magical_damage_taken_by_s2_for_s3,
                        buildExpression(level, actionValues, RoundingMode.UNNECESSARY, property),
                        targetParameter.buildTargetClause(),
                        buildExpression(level, durationValues, RoundingMode.UNNECESSARY, property));
            case all:
                return I18N.getString(R.string.Reduce_s1_all_damage_taken_by_s2_for_s3,
                        buildExpression(level, actionValues, RoundingMode.UNNECESSARY, property),
                        targetParameter.buildTargetClause(),
                        buildExpression(level, durationValues, RoundingMode.UNNECESSARY, property));
            default:
                return super.localizedDetail(level, property);
        }
    }
}
