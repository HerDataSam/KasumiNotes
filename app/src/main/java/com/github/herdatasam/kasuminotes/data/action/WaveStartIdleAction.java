package com.github.herdatasam.kasuminotes.data.action;

import com.github.herdatasam.kasuminotes.R;
import com.github.herdatasam.kasuminotes.common.I18N;
import com.github.herdatasam.kasuminotes.data.Property;

public class WaveStartIdleAction extends ActionParameter {
    @Override
    protected void childInit() {
        super.childInit();
    }

    @Override
    public String localizedDetail(int level, Property property) {
        return I18N.getString(R.string.Appear_after_s_sec_since_wave_start,
                actionValue1);
    }
}
