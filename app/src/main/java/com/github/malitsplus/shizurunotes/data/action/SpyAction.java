package com.github.malitsplus.shizurunotes.data.action;

import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class SpyAction extends ActionParameter {

    enum CancelType {
        none(1),
        damaged(2);

        private int value;
        CancelType(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }

        public static SpyAction.CancelType parse(int value){
            for(SpyAction.CancelType item : SpyAction.CancelType.values()){
                if(item.getValue() == value)
                    return item;
            }
            return none;
        }
    }

    protected SpyAction.CancelType cancelType;
    protected List<ActionValue> durationValues = new ArrayList<>();

    @Override
    protected void childInit() {
        super.childInit();
        cancelType = SpyAction.CancelType.parse((int) actionDetail2);
        durationValues.add(new ActionValue(actionValue1, actionValue2, null));
    }

    @Override
    public String localizedDetail(int level, Property property) {
        String str = I18N.getString(R.string.Make_s1_invisible_for_s2_seconds,
                targetParameter.buildTargetClause(),
                buildExpression(level, durationValues, RoundingMode.UNNECESSARY, property));
        switch (cancelType) {
            case none:
                return str;
            case damaged:
                return str + I18N.getString(R.string.cancels_on_taking_damage);
            default:
                return super.localizedDetail(level, property);
        }
    }
}
