package com.github.herdatasam.kasuminotes.data.action;

import com.github.herdatasam.kasuminotes.R;
import com.github.herdatasam.kasuminotes.common.I18N;
import com.github.herdatasam.kasuminotes.data.Property;

public class StopFieldAction extends ActionParameter {
    @Override
    protected void childInit() {
        super.childInit();
    }

    @Override
    public String localizedDetail(int level, Property property) {
        return I18N.getString(R.string.Remove_field_of_skill_d1_1_represents_the_first_skill_in_this_list_effect_d2,
                actionDetail1 / 100 % 10,
                actionDetail1 % 10);
    }
}
