package com.github.herdatasam.kasuminotes.data.action;

import com.github.herdatasam.kasuminotes.R;
import com.github.herdatasam.kasuminotes.common.I18N;
import com.github.herdatasam.kasuminotes.data.Property;

import java.math.RoundingMode;

public class RatioDamageAction extends ActionParameter {

    enum HPtype{
        unknown(0),
        max(1),
        current(2);

        private int value;
        HPtype(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }

        public static HPtype parse(int value){
            for(HPtype item : HPtype.values()){
                if(item.getValue() == value)
                    return item;
            }
            return unknown;
        }
    }

    protected HPtype hptype;

    @Override
    protected void childInit() {
        super.childInit();
        actionValues.add(new ActionValue(actionValue1, actionValue2, null));
        hptype = HPtype.parse(actionDetail1);
    }

    @Override
    public String localizedDetail(int level, Property property) {
        switch (hptype){
            case max:
                return I18N.getString(R.string.Deal_damage_equal_to_s1_of_target_max_HP_to_s2,
                        buildExpression(level, RoundingMode.UNNECESSARY, property), targetParameter.buildTargetClause());
            case current:
                return I18N.getString(R.string.Deal_damage_equal_to_s1_of_target_current_HP_to_s2,
                        buildExpression(level, RoundingMode.UNNECESSARY, property), targetParameter.buildTargetClause());
            default:
                return super.localizedDetail(level, property);
        }
    }
}
