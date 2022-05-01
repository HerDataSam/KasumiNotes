package com.github.malitsplus.shizurunotes.data.action;

import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ChangeEnergyFieldAction extends ActionParameter {

    protected List<ActionValue> durationValues = new ArrayList<>();

    @Override
    protected void childInit() {
        actionValues.add(new ActionValue(actionValue1, actionValue2, null));
        durationValues.add(new ActionValue(actionValue3, actionValue4, null));
    }

    @Override
    public String localizedDetail(int level, Property property) {
        switch (actionDetail1) {
            case 1:
                return I18N.getString(R.string.Summon_a_field_of_radius_d1_at_position_of_s2_to_restore_s3_TP_per_second_for_s4_seconds,
                    (int)actionValue5.value,
                    targetParameter.buildTargetClause(),
                    buildExpression(level, RoundingMode.UP, property),
                    buildExpression(level, durationValues, RoundingMode.UNNECESSARY, property));
            default:
                return I18N.getString(R.string.Summon_a_field_of_radius_d1_at_position_of_s2_to_lose_s3_TP_per_second_for_s4_seconds,
                    (int)actionValue5.value,
                    targetParameter.buildTargetClause(),
                    buildExpression(level, RoundingMode.UP, property),
                    buildExpression(level, durationValues, RoundingMode.UNNECESSARY, property));

        }
    }
}
