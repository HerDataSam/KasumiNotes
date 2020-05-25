package com.github.malitsplus.shizurunotes.data.action;

import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;
import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;

import java.math.RoundingMode;

public class MultipleAction extends ActionParameter {
    @Override
    protected void childInit() {
        actionValues.add(new ActionValue(actionValue2, actionValue3, null));
    }

    @Override
    public String localizedDetail(int level, Property property) {
        return super.localizedDetail(level, property);
    }
}
