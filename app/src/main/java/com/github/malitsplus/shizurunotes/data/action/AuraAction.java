package com.github.malitsplus.shizurunotes.data.action;

import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;
import com.github.malitsplus.shizurunotes.data.PropertyKey;
import com.github.malitsplus.shizurunotes.R;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AuraAction extends ActionParameter {

    enum AuraType{
        none(-1),
        maxHP(0),
        atk(1),
        def(2),
        magicStr(3),
        magicDef(4),
        dodge(5),
        physicalCritical(6),
        magicalCritical(7),
        energyRecoverRate(8),
        lifeSteal(9),
        moveSpeed(10),
        physicalCriticalDamage(11),
        magicalCriticalDamage(12),
        accuracy(13),
        receivedCriticalDamage(14),
        receivedDamage(15),
        receivedPhysicalDamage(16),
        receivedMagicalDamage(17);

        private int value;
        AuraType(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }

        public static AuraType parse(int value){
            for(AuraType item : AuraType.values()){
                if(item.getValue() == value)
                    return item;
            }
            return none;
        }

        public String description(){
            switch (this){
                case atk: return PropertyKey.atk.description();
                case def: return PropertyKey.def.description();
                case magicStr: return PropertyKey.magicStr.description();
                case magicDef: return PropertyKey.magicDef.description();
                case dodge: return PropertyKey.dodge.description();
                case physicalCritical: return PropertyKey.physicalCritical.description();
                case magicalCritical: return PropertyKey.magicCritical.description();
                case energyRecoverRate: return PropertyKey.energyRecoveryRate.description();
                case lifeSteal: return PropertyKey.lifeSteal.description();
                case moveSpeed: return I18N.getString(R.string.Move_Speed);
                case physicalCriticalDamage: return I18N.getString(R.string.Physical_Critical_Damage);
                case magicalCriticalDamage: return I18N.getString(R.string.Magical_Critical_Damage);
                case accuracy: return PropertyKey.accuracy.description();
                case receivedCriticalDamage: return I18N.getString(R.string.Received_Critical_Damage);
                case receivedDamage: return I18N.getString(R.string.received_damage);
                case receivedPhysicalDamage: return I18N.getString(R.string.received_physical_damage);
                case receivedMagicalDamage: return I18N.getString(R.string.received_magical_damage);
                case maxHP: return I18N.getString(R.string.max_HP);
                default: return "";
            }
        }
    }

    enum AuraActionType{
        raise,
        reduce;

        public static AuraActionType parse(int value){
            if(value % 10 == 1)
                return reduce;
            else
                return raise;
        }

        public String description(){
            switch (this){
                case raise: return I18N.getString(R.string.Raise);
                case reduce: return I18N.getString(R.string.Reduce);
                default: return "";
            }
        }
        public AuraActionType toggle() {
            switch (this) {
                case raise: return reduce;
                case reduce: return raise;
            }
            return raise;
        }
    }

    enum BreakType{
        Unknown(-1),
        Normal(1),
        Break(2);

        private int value;
        BreakType(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }

        public static BreakType parse(int value){
            for(BreakType item : BreakType.values()){
                if(item.getValue() == value)
                    return item;
            }
            return Unknown;
        }
    }

    protected PercentModifier percentModifier;
    protected RoundingMode roundingMode = RoundingMode.UP;
    protected List<ActionValue> durationValues = new ArrayList<>();
    protected AuraActionType auraActionType;
    protected AuraType auraType;
    protected BreakType breakType;
    protected boolean isConstant = false;

    @Override
    protected void childInit() {
        percentModifier = PercentModifier.parse((int) actionValue1.value);
        actionValues.add(new ActionValue(actionValue2, actionValue3, null));
        durationValues.add(new ActionValue(actionValue4, actionValue5, null));
        auraActionType = AuraActionType.parse(actionDetail1);
        auraType = AuraType.parse(actionDetail1 % 1000 / 10);
        if (actionDetail1 / 1000 == 1)
            isConstant = true;
        breakType = BreakType.parse(actionDetail2);
        if (auraType == AuraType.receivedCriticalDamage
            || auraType == AuraType.receivedMagicalDamage
            || auraType == AuraType.receivedPhysicalDamage
            || auraType == AuraType.receivedDamage) {
            auraActionType = auraActionType.toggle();
            percentModifier = PercentModifier.percent;
        }
        //if (percentModifier == PercentModifier.percent)
        //    roundingMode = RoundingMode.UNNECESSARY;
    }

    @Override
    public String localizedDetail(int level, Property property) {
        switch (breakType){
            case Break:
                return I18N.getString(R.string.s1_s2_s3_s4_s5_during_break,
                        auraActionType.description(), targetParameter.buildTargetClause(), buildExpression(level, roundingMode, property), percentModifier.description(), auraType.description());
            default:
                return I18N.getString(R.string.s1_s2_s3_s4_s5_for_s6_sec_s7,
                        auraActionType.description(),
                        targetParameter.buildTargetClause(),
                        buildExpression(level, roundingMode, property),
                        percentModifier.description(),
                        auraType.description(),
                        buildExpression(level, durationValues, RoundingMode.UNNECESSARY, property),
                        isConstant ? I18N.getString(R.string.this_buff_is_constant) : "");
        }
    }
}
