package com.github.herdatasam.kasuminotes.data.action;

import com.github.herdatasam.kasuminotes.R;
import com.github.herdatasam.kasuminotes.common.I18N;
import com.github.herdatasam.kasuminotes.utils.Utils;
import com.github.herdatasam.kasuminotes.data.Property;

public class ChangePatternAction extends ActionParameter {
    @Override
    protected void childInit() {
        super.childInit();
    }

    @Override
    public String localizedDetail(int level, Property property) {
        switch (actionDetail1){
            case 1:
                return I18N.getString(R.string.Change_attack_pattern_to_d1_for_s2_sec,
                        actionDetail2 % 10, Utils.roundDouble(actionValue1));
            case 2:
                return I18N.getString(R.string.Change_skill_visual_effect_for_s_sec,
                        Utils.roundDouble(actionValue1));
            default:
                return super.localizedDetail(level, property);
        }
    }
}
