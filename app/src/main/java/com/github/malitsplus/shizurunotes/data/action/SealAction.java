package com.github.malitsplus.shizurunotes.data.action;

import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;
import com.github.malitsplus.shizurunotes.utils.Utils;
import com.github.malitsplus.shizurunotes.R;

public class SealAction extends ActionParameter {
    @Override
    protected void childInit() {
        super.childInit();
    }

    @Override
    public String localizedDetail(int level, Property property) {
        if(actionValue4.value >= 0)
            return I18N.getString(R.string.Add_s1_mark_stacks_max_s2_ID_s3_on_s4_for_s5_sec,
                    Utils.roundDownDouble(actionValue4.value),
                    Utils.roundDownDouble(actionValue1.value),
                    Utils.roundDownDouble(actionValue2.value),
                    targetParameter.buildTargetClause(),
                    Utils.roundDouble(actionValue3.value));
        else
            return I18N.getString(R.string.Remove_s1_mark_stacks_ID_s2_on_s3,
                    Utils.roundDownDouble(-actionValue4.value),
                    Utils.roundDownDouble(actionValue2.value),
                    targetParameter.buildTargetClause());
    }
}
