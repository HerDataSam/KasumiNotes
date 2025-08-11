package com.github.malitsplus.shizurunotes.data.action;

import android.annotation.SuppressLint;

import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.data.Property;
import com.github.malitsplus.shizurunotes.data.PropertyKey;
import com.github.malitsplus.shizurunotes.data.Skill;
import com.github.malitsplus.shizurunotes.user.UserSettings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

enum PercentModifier {
    percent,
    number;

    public static PercentModifier parse(int value) {
        switch (value) {
            case 2:
                return percent;
            default:
                return number;
        }
    }

    public String description() {
        switch (this) {
            case percent:
                return "%";
            default:
                return "";
        }
    }
}

enum ClassModifier {
    unknown(0),
    physical(1),
    magical(2),
    inevitablePhysical(3);

    private final int value;

    ClassModifier(int value) {
        this.value = value;
    }

    public static ClassModifier parse(int value) {
        for (ClassModifier item : ClassModifier.values()) {
            if (item.getValue() == value)
                return item;
        }
        return unknown;
    }

    public int getValue() {
        return value;
    }

    public String description() {
        switch (this) {
            case magical:
                return I18N.getString(R.string.magical);
            case physical:
                return I18N.getString(R.string.physical);
            case inevitablePhysical:
                return I18N.getString(R.string.inevitable_physical);
            default:
                return I18N.getString(R.string.unknown);
        }
    }
}

enum CriticalModifier {
    normal(0),
    critical(1);

    private final int value;

    CriticalModifier(int value) {
        this.value = value;
    }

    public static CriticalModifier parse(int value) {
        switch (value) {
            case 1:
                return critical;
            default:
                return normal;
        }
    }

    public int getValue() {
        return value;
    }
}

public class ActionParameter {
    public boolean isEnemySkill;
    public int dependActionId;
    @Nullable
    public List<Skill.Action> childrenAction;
    @Nullable
    public Property enemyProperty;
    public int actionId;
    public int classId;
    public int rawActionType;
    public int actionDetail1;
    public int actionDetail2;
    public int actionDetail3;
    public ArrayList<Integer> actionDetails = new ArrayList<>();
    public DoubleValue actionValue1;
    public DoubleValue actionValue2;
    public DoubleValue actionValue3;
    public DoubleValue actionValue4;
    public DoubleValue actionValue5;
    public DoubleValue actionValue6;
    public DoubleValue actionValue7;
    public ArrayList<Double> rawActionValues = new ArrayList<>();
    public ActionType actionType;
    public TargetParameter targetParameter;
    protected List<ActionValue> actionValues = new ArrayList<>();

    public static ActionParameter type(int rawType) {
        switch (rawType) {
            case 1:
                return new DamageAction();
            case 2:
                return new MoveAction();
            case 3:
                return new KnockAction();
            case 4:
                return new HealAction();
            case 5:
                return new CureAction();
            case 6:
                return new BarrierAction();
            case 7:
                return new ReflexiveAction();
            case 8:
            case 9:
            case 12:
            case 13:
                return new AilmentAction();
            case 10:
                return new AuraAction();
            case 11:
                return new CharmAction();
            case 14:
                return new ModeChangeAction();
            case 15:
                return new SummonAction();
            case 16:
                return new ChangeEnergyAction();
            case 17:
                return new TriggerAction();
            case 18:
                return new DamageChargeAction();
            case 19:
                return new ChargeAction();
            case 20:
                return new DecoyAction();
            case 21:
                return new NoDamageAction();
            case 22:
                return new ChangePatternAction();
            case 23:
                return new IfForChildrenAction();
            case 24:
                return new RevivalAction();
            case 25:
                return new ContinuousAttackAction();
            case 26:
                return new AdditiveAction();
            case 27:
                return new MultipleAction();
            case 28:
                return new IfForAllAction();
            case 29:
                return new SearchAreaChangeAction();
            case 30:
                return new DestroyAction();
            case 31:
                return new ContinuousAttackNearbyAction();
            case 32:
                return new EnchantLifeStealAction();
            case 33:
                return new EnchantStrikeBackAction();
            case 34:
                return new AccumulativeDamageAction();
            case 35:
                return new SealAction();
            case 36:
                return new AttackFieldAction();
            case 37:
                return new HealFieldAction();
            case 38:
                return new ChangeParameterFieldAction();
            case 39:
                return new AbnormalStateFieldAction();
            case 40:
                return new ChangeSpeedFieldAction();
            case 41:
                return new UBChangeTimeAction();
            case 42:
                return new LoopTriggerAction();
            case 43:
                return new IfHasTargetAction();
            case 44:
                return new WaveStartIdleAction();
            case 45:
                return new SkillExecCountAction();
            case 46:
                return new RatioDamageAction();
            case 47:
                return new UpperLimitAttackAction();
            case 48:
                return new RegenerationAction();
            case 49:
                return new DispelAction();
            case 50:
                return new ChannelAction();
            case 52:
                return new ChangeBodyWidthAction();
            case 53:
                return new IFExistsFieldForAllAction();
            case 54:
                return new StealthAction();
            case 55:
                return new MovePartsAction();
            case 56:
                return new CountBlindAction();
            case 57:
                return new CountDownAction();
            case 58:
                return new StopFieldAction();
            case 59:
                return new InhibitHealAction();
            case 60:
                return new AttackSealAction();
            case 61:
                return new FearAction();
            case 62:
                return new AweAction();
            case 63:
                return new LoopMotionRepeatAction();
            case 69:
                return new ToadAction();
            case 71:
                return new KnightGuardAction();
            case 72:
                return new DamageCutAction();
            case 73:
                return new LogBarrierAction();
            case 74:
                return new DivideAction();
            case 75:
                return new ActionByHitCountAction();
            case 76:
                return new HealDownAction();
            case 77:
                return new PassiveSealAction();
            case 78:
                return new PassiveDamageUpAction();
            case 79:
                return new DamageByBehaviourAction();
            case 83:
                return new ChangeSpeedOverlapAction();
            case 90:
                return new PassiveAction();
            case 91:
                return new PassiveInermittentAction();
            case 92:
                return new ChangeEnergyRecoveryRatioByDamageAction();
            case 93:
                return new IgnoreDecoyAction();
            case 94:
                return new EffectAction();
            case 95:
                return new SpyAction();
            case 96:
                return new ChangeEnergyFieldAction();
            case 97:
                return new ChangeEnergyByDamageAction();
            case 98:
                return new EnergyDamageReduceAction();
            case 99:
                return new ChangeSpeedOverwriteFieldAction();
            case 100:
                return new UnableStateGuardAction();
            case 51:
            case 80:
            case 81:
            case 82:
            default:
                return new ActionParameter();
        }
    }

    public static String description(int type) {
        switch (type) {
            case 1:
                return I18N.getString(R.string.damage_action_description);
            case 2:
                return I18N.getString(R.string.move_action_description);
            case 3:
                return I18N.getString(R.string.knock_action_description);
            case 4:
            case 5:
            case 48:
                return I18N.getString(R.string.heal_action_description);
            case 6:
                return I18N.getString(R.string.barrier_action_description);
            case 7:
                return I18N.getString(R.string.reflexive_action_description);
            case 8:
                return I18N.getString(R.string.ailment_action_description);
            case 9:
                return I18N.getString(R.string.dot_action_description);
            case 10:
                return I18N.getString(R.string.aura_action_description);
            case 11:
                return I18N.getString(R.string.charm_action_description);
            case 12:
                return I18N.getString(R.string.blind_action_description);
            case 13:
                return I18N.getString(R.string.silence_action_description);
            case 14:
                return I18N.getString(R.string.change_mode_action_description);
            case 15:
                return I18N.getString(R.string.summon_action_description);
            case 16:
                return I18N.getString(R.string.change_energy_action_description);
            case 17:
                return I18N.getString(R.string.trigger_action_description);
            case 18:
            case 19:
                return I18N.getString(R.string.damage_charge_action_description);
            case 20:
                return I18N.getString(R.string.decoy_action_description);
            case 21:
                return I18N.getString(R.string.no_damage_action_description);
            case 22:
                return I18N.getString(R.string.change_pattern_action_description);
            case 23:
            case 28:
                return I18N.getString(R.string.if_action_description);
            case 24:
                return I18N.getString(R.string.revival_action_description);
            case 26:
                return I18N.getString(R.string.additive_action_description);
            case 27:
                return I18N.getString(R.string.multiple_action_description);
            case 30:
                return I18N.getString(R.string.destroy_action_description);
            case 32:
                return I18N.getString(R.string.enchant_life_steal_action_description);
            case 33:
                return I18N.getString(R.string.enchant_strike_back_action_description);
            case 34:
                return I18N.getString(R.string.accumulative_action_description);
            case 35:
                return I18N.getString(R.string.seal_action_description);
            case 36:
                return I18N.getString(R.string.attack_field_action_description);
            case 37:
                return I18N.getString(R.string.heal_field_action_description);
            case 38:
                return I18N.getString(R.string.change_parameter_field_action_description);
            case 39:
                return I18N.getString(R.string.abnormal_state_field_action_description);
            case 42:
                return I18N.getString(R.string.loop_trigger_action_description);
            case 44:
                return I18N.getString(R.string.wave_start_idle_action_description);
            case 45:
                return I18N.getString(R.string.skill_exec_count_action_description);
            case 46:
                return I18N.getString(R.string.ratio_damage_action_description);
            case 47:
                return I18N.getString(R.string.upper_limit_attack_action_description);
            case 49:
                return I18N.getString(R.string.dispel_action_description);
            case 50:
                return I18N.getString(R.string.channel_action_description);
            case 52:
                return I18N.getString(R.string.change_body_width_action_description);
            case 53:
                return I18N.getString(R.string.if_exists_field_action_description);
            case 54:
                return I18N.getString(R.string.stealth_action_description);
            case 55:
                return I18N.getString(R.string.move_part_action_description);
            case 56:
                return I18N.getString(R.string.count_blind_action_description);
            case 57:
                return I18N.getString(R.string.count_down_action_description);
            case 58:
                return I18N.getString(R.string.stop_field_action_description);
            case 59:
                return I18N.getString(R.string.inhibit_heal_action_description);
            case 60:
                return I18N.getString(R.string.attack_seal_action_description);
            case 61:
                return I18N.getString(R.string.fear_action_description);
            case 62:
                return I18N.getString(R.string.awe_action_description);
            case 63:
                return I18N.getString(R.string.loop_motion_repeat_action_description);
            case 69:
                return I18N.getString(R.string.toad_action_description);
            case 71:
                return I18N.getString(R.string.knight_guard_action_description);
            case 72:
                return I18N.getString(R.string.damage_cut_action_description);
            case 73:
                return I18N.getString(R.string.log_barrier_action_description);
            case 74:
                return I18N.getString(R.string.divide_action_description);
            case 75:
                return I18N.getString(R.string.action_by_hit_action_description);
            case 76:
                return I18N.getString(R.string.heal_down_action_description);
            case 77:
                return I18N.getString(R.string.passive_seal_action_description);
            case 78:
                return I18N.getString(R.string.passive_damage_up_action_description);
            case 79:
                return I18N.getString(R.string.damage_by_behaviour_action_description);
            case 83:
                return I18N.getString(R.string.change_speed_overlap_description);
            case 90:
                return I18N.getString(R.string.passive_action_description);
            case 92:
                return I18N.getString(R.string.change_energy_recovery_ratio_by_damage_action_description);
            case 93:
                return I18N.getString(R.string.ignore_decoy_action_description);
            case 94:
                return I18N.getString(R.string.effect_action_description);
            case 95:
                return I18N.getString(R.string.spy_action_description);
            case 96:
                return I18N.getString(R.string.change_energy_field_action_description);
            case 97:
                return I18N.getString(R.string.change_energy_by_damage_action_description);
            case 98:
                return I18N.getString(R.string.energy_damage_reduce_action_description);
            case 99:
                return I18N.getString(R.string.change_speed_overwrite_field_action_description);
            case 100:
                return I18N.getString(R.string.unable_state_guard_action_description);
            default:
                return I18N.getString(R.string.unknown_action_description);
        }
    }

    public ActionParameter init(boolean isEnemySkill, int actionId, int dependActionId, int classId, int actionType, int actionDetail1, int actionDetail2, int actionDetail3, double actionValue1, double actionValue2, double actionValue3, double actionValue4, double actionValue5, double actionValue6, double actionValue7, int targetAssignment, int targetArea, int targetRange, int targetType, int targetNumber, int targetCount, Skill.Action dependAction, @Nullable List<Skill.Action> childrenAction) {
        this.isEnemySkill = isEnemySkill;
        this.actionId = actionId;
        this.dependActionId = dependActionId;
        this.classId = classId;
        this.rawActionType = actionType;
        this.actionType = ActionType.parse(actionType);
        this.actionDetail1 = actionDetail1;
        this.actionDetail2 = actionDetail2;
        this.actionDetail3 = actionDetail3;
        if (actionDetail1 != 0)
            actionDetails.add(actionDetail1);
        if (actionDetail2 != 0)
            actionDetails.add(actionDetail2);
        if (actionDetail3 != 0)
            actionDetails.add(actionDetail3);
        this.actionValue1 = new DoubleValue(actionValue1, eActionValue.VALUE1);
        this.actionValue2 = new DoubleValue(actionValue2, eActionValue.VALUE2);
        this.actionValue3 = new DoubleValue(actionValue3, eActionValue.VALUE3);
        this.actionValue4 = new DoubleValue(actionValue4, eActionValue.VALUE4);
        this.actionValue5 = new DoubleValue(actionValue5, eActionValue.VALUE5);
        this.actionValue6 = new DoubleValue(actionValue6, eActionValue.VALUE6);
        this.actionValue7 = new DoubleValue(actionValue7, eActionValue.VALUE7);
        if (actionValue1 != 0)
            rawActionValues.add(actionValue1);
        if (actionValue2 != 0)
            rawActionValues.add(actionValue2);
        if (actionValue3 != 0)
            rawActionValues.add(actionValue3);
        if (actionValue4 != 0)
            rawActionValues.add(actionValue4);
        if (actionValue5 != 0)
            rawActionValues.add(actionValue5);
        if (actionValue6 != 0)
            rawActionValues.add(actionValue6);
        if (actionValue7 != 0)
            rawActionValues.add(actionValue7);
        if (childrenAction != null) {
            this.childrenAction = childrenAction;
        }
        targetParameter = new TargetParameter(targetAssignment, targetNumber, targetType, targetRange, targetArea, targetCount, dependAction);
        childInit();
        return this;
    }

    protected void childInit() {
    }

    private String bracesIfNeeded(String content) {
        if (content.contains("+"))
            return String.format("(%s)", content);
        else
            return content;
    }

    public String localizedDetail(int level, Property property, Property enemyProperty) {
        this.enemyProperty = enemyProperty;
        return localizedDetail(level, property);
    }

    public String localizedDetail(int level, Property property) {
        if (rawActionType == 0) {
            return I18N.getString(R.string.no_effect);
        }
        return I18N.getString(R.string.Unknown_effect_d1_to_s2_with_details_s3_values_s4,
                rawActionType,
                targetParameter.buildTargetClause(),
                actionDetails.toString(),
                rawActionValues.toString());
    }

    public String buildExpression(int level, Property property) {
        return buildExpression(level, actionValues, null, property, false, false, false, ClassModifier.unknown, false);
    }

    public String buildExpression(int level, RoundingMode roundingMode, Property property) {
        return buildExpression(level, actionValues, roundingMode, property, false, false, false, ClassModifier.unknown, false);
    }

    public String buildExpression(int level, List<ActionValue> actionValues, RoundingMode roundingMode, Property property) {
        return buildExpression(level, actionValues, roundingMode, property, false, false, false, ClassModifier.unknown, false);
    }

    @SuppressLint("DefaultLocale")
    public String buildExpression(int level,
                                  List<ActionValue> actionValues,
                                  RoundingMode roundingMode,
                                  Property property,
                                  boolean isHealing,
                                  boolean isSelfTPRestoring,
                                  boolean isDamageAction,
                                  ClassModifier damageClass,
                                  boolean hasBracesIfNeeded) {
        if (actionValues == null)
            actionValues = this.actionValues;
        if (roundingMode == null)
            roundingMode = RoundingMode.DOWN;
        if (property == null)
            property = new Property();

        String equation = "";
        if (UserSettings.get().getExpression() == UserSettings.EXPRESSION_EXPRESSION) { // && !isEnemySkill
            StringBuilder expression = new StringBuilder();
            for (ActionValue value : actionValues) {
                StringBuilder part = new StringBuilder();
                if (value.initial != null && value.perLevel != null) {
                    double initialValue = Double.parseDouble(value.initial);
                    double perLevelValue = Double.parseDouble(value.perLevel);
                    if (initialValue == 0 && perLevelValue == 0) {
                        continue;
                    } else if (initialValue == 0) {
                        part.append(String.format("%s * %s (%d)", perLevelValue, I18N.getString(R.string.SLv), level));
                    } else if (perLevelValue == 0) {
                        if (actionValues.size() == 1) {
                            part.append(" ");
                        } else if (value.key == null && roundingMode != RoundingMode.UNNECESSARY) {
                            BigDecimal bigDecimal = new BigDecimal(initialValue);
                            part.append(bigDecimal.setScale(0, roundingMode).intValue());
                        } else {
                            part.append(initialValue);
                        }
                    } else {
                        part.append(String.format("%s + %s * %s (%d)", initialValue, perLevelValue, I18N.getString(R.string.SLv), level));
                    }
                    if (value.key != null) {
                        if (initialValue == 0 && perLevelValue == 0) {
                            continue;
                        } else if (initialValue == 0 || perLevelValue == 0) {
                            part.append(String.format(" * %s (%d)", value.key.description(), (int) property.getItem(value.key)));
                        } else {
                            //part.setLength(0);
                            part.insert(0, "(");
                            part.append(String.format(") * %s (%d)", value.key.description(), (int) property.getItem(value.key)));
                        }
                    }
                }
                if (part.length() != 0) {
                    expression.append(part).append(" + ");
                }
            }
            if (expression.length() == 0) {
                equation = "0";
            } else {
                expression.delete(expression.lastIndexOf(" +"), expression.length());
                equation = (hasBracesIfNeeded ? bracesIfNeeded(expression.toString()) : expression.toString());
                /*
                if (enemyProperty != null && (isDamageAction || isHealing || isSelfTPRestoring)) {
                    int ratio = 0;
                    String ratioClass = "";
                    if (isDamageAction) {
                        ratioClass = "defRatio";
                        if (damageClass == ClassModifier.physical || damageClass == ClassModifier.inevitablePhysical) {
                            ratio = enemyProperty.getDef();
                        } else if (damageClass == ClassModifier.magical) {
                            ratio = enemyProperty.getAtk();
                        } else {
                            ratio = enemyProperty.getDef(); // TODO
                        }
                    } else if (isHealing) {
                        ratioClass = "healRatio";
                        ratio = property.getHpRecoveryRate();
                    } else {
                        ratioClass = "tpRatio";
                        ratio = property.getEnergyRecoveryRate();
                    }
                    equation += String.format(" * %s (%d)", ratioClass, ratio);
                }*/
                if (!(actionValues.size() == 1 && Double.parseDouble(actionValues.get(0).perLevel) == 0.0))
                    equation += " = ";
                else
                    equation = "";
            }
        } else if (UserSettings.get().getExpression() == UserSettings.EXPRESSION_ORIGINAL) {
            StringBuilder expression = new StringBuilder();
            for (ActionValue value : actionValues) {
                StringBuilder part = new StringBuilder();
                if (value.initial != null && value.perLevel != null) {
                    double initialValue = Double.parseDouble(value.initial);
                    double perLevelValue = Double.parseDouble(value.perLevel);
                    if (initialValue == 0 && perLevelValue == 0) {
                        continue;
                    } else if (initialValue == 0) {
                        part.append(String.format("##%s##%s * %s (%d)", value.perLevelValue.description, perLevelValue, I18N.getString(R.string.SLv), level));
                    } else if (perLevelValue == 0) {
                        if (actionValues.size() == 1) {
                            part.append(String.format("##%s##", value.initialValue.description));
                        } else if (value.key == null && roundingMode != RoundingMode.UNNECESSARY) {
                            BigDecimal bigDecimal = new BigDecimal(initialValue);
                            part.append(String.format("##%s##%s", value.initialValue.description, bigDecimal.setScale(0, roundingMode).intValue()));
                        } else {
                            part.append(String.format("##%s##%s", value.initialValue.description, initialValue));
                        }
                    } else {
                        part.append(String.format("##%s##%s + ##%s##%s * %s (%d)", value.initialValue.description, initialValue, value.perLevelValue.description, perLevelValue, I18N.getString(R.string.SLv), level));
                    }
                    if (value.key != null) {
                        if (initialValue == 0 && perLevelValue == 0) {
                            continue;
                        } else if (initialValue == 0 || perLevelValue == 0) {
                            part.append(String.format(" * %s (%d)", value.key.description(), (int) property.getItem(value.key)));
                        } else {
                            //part.setLength(0);
                            part.insert(0, "(");
                            part.append(String.format(") * %s (%d)", value.key.description(), (int) property.getItem(value.key)));
                        }
                    }
                }
                if (part.length() != 0) {
                    expression.append(part).append(" + ");
                }
            }
            if (expression.length() == 0) {
                equation = "0";
            } else {
                expression.delete(expression.lastIndexOf(" +"), expression.length());
                equation = (hasBracesIfNeeded ? bracesIfNeeded(expression.toString()) : expression.toString());
                /*
                if (enemyProperty != null && (isDamageAction || isHealing || isSelfTPRestoring)) {
                    int ratio = 0;
                    String ratioClass = "";
                    if (isDamageAction) {
                        ratioClass = "defRatio"; // TODO: strings
                        if (damageClass == ClassModifier.physical || damageClass == ClassModifier.inevitablePhysical) {
                            ratio = enemyProperty.getDef();
                        } else if (damageClass == ClassModifier.magical) {
                            ratio = enemyProperty.getMagicDef();
                        } else {
                            ratio = enemyProperty.getDef(); // TODO
                        }
                    } else if (isHealing) {
                        ratioClass = "healRatio";
                        ratio = property.getHpRecoveryRate();
                    } else {
                        ratioClass = "tpRatio";
                        ratio = property.getEnergyRecoveryRate();
                    }
                    equation += String.format(" * %s (%d)", ratioClass, ratio);
                }*/
                if (!(actionValues.size() == 1 && Double.parseDouble(actionValues.get(0).perLevel) == 0.0))
                    equation += " = ";
            }
        }
        BigDecimal fixedValue = new BigDecimal("0.0");
        for (ActionValue value : actionValues) {
            BigDecimal part = new BigDecimal("0.0");
            if (value.initial != null && value.perLevel != null) {
                BigDecimal initialValue = new BigDecimal(value.initial);
                BigDecimal perLevelValue = new BigDecimal(value.perLevel);
                part = initialValue.add(perLevelValue.multiply(new BigDecimal(level)));
            }
            if (value.key != null) {
                part = part.multiply(BigDecimal.valueOf(property.getItem(value.key)));
            }
            fixedValue = fixedValue.add(part);
        }

        if (roundingMode == RoundingMode.UNNECESSARY)
            return equation + fixedValue.stripTrailingZeros().toPlainString();

        BigDecimal bigDecimal = fixedValue;

        // TODO: do this first or not?
        String calculatedValue = "";
        /*
        if (enemyProperty != null && (isDamageAction || isHealing || isSelfTPRestoring)) {
            double value = fixedValue;
            double ratio = 0;
            if (isDamageAction) {
                if (damageClass == ClassModifier.physical || damageClass == ClassModifier.inevitablePhysical) {
                    ratio = CalcUtils.Companion.getDefRatio(enemyProperty.getDef());
                } else if (damageClass == ClassModifier.magical) {
                    ratio = CalcUtils.Companion.getDefRatio(enemyProperty.getMagicDef());
                } else if (damageClass == ClassModifier.unknown) {
                    ratio = CalcUtils.Companion.getDefRatio(enemyProperty.getDef());
                }
            }
            else if(isHealing){
                ratio = (property.hpRecoveryRate / 100 + 1);
            }
            else {
                ratio= (property.energyRecoveryRate / 100 + 1);
            }
            value *= ratio;
            BigDecimal valueBD = new BigDecimal(value);
            BigDecimal ratioBD = new BigDecimal(ratio);

            calculatedValue = String.format(" * %s = %s",
                    ratioBD.setScale(0, roundingMode).intValue(), // TODO: rounding mode
                    valueBD.setScale(0, roundingMode).intValue());
        }*/

        return equation + bigDecimal.setScale(0, roundingMode).intValue() + calculatedValue;
    }

    protected List<ActionValue> getActionValues() {
        return actionValues;
    }

    protected void setActionValues(List<ActionValue> actionValues) {
        this.actionValues = actionValues;
    }

    public enum eActionValue {
        VALUE1,
        VALUE2,
        VALUE3,
        VALUE4,
        VALUE5,
        VALUE6,
        VALUE7,
        VALUE_NULL;

        public String description() {
            switch (this) {
                case VALUE1:
                    return I18N.getString(R.string.value1);
                case VALUE2:
                    return I18N.getString(R.string.value2);
                case VALUE3:
                    return I18N.getString(R.string.value3);
                case VALUE4:
                    return I18N.getString(R.string.value4);
                case VALUE5:
                    return I18N.getString(R.string.value5);
                case VALUE6:
                    return I18N.getString(R.string.value6);
                case VALUE7:
                    return I18N.getString(R.string.value7);
                default:
                    return "NULL";
            }
        }
    }

    public static class DoubleValue {
        public double value;
        public String description;
        public eActionValue index;

        public DoubleValue(double value, eActionValue index) {
            this.value = value;
            this.index = index;
            this.description = index.description();
        }

        public String valueString() {
            return String.valueOf(value);
        }

        public DoubleValue multiply(double m) {
            this.value = this.value * m;
            return this;
        }
    }

    protected static class ActionValue {
        protected String initial;
        protected String perLevel;
        protected PropertyKey key;
        protected DoubleValue initialValue;
        protected DoubleValue perLevelValue;

        protected ActionValue(DoubleValue initial, DoubleValue perLevel, PropertyKey key) {
            this.initialValue = initial;
            this.perLevelValue = perLevel;
            this.initial = initial.valueString();
            this.perLevel = perLevel.valueString();
            this.key = key;
        }

        protected ActionValue(double initial, double perLevel, eActionValue vInitial, eActionValue vPerLevel, PropertyKey key) {
            this.initialValue = new DoubleValue(initial, vInitial);
            this.perLevelValue = new DoubleValue(perLevel, vPerLevel);
            this.initial = String.valueOf(initial);
            this.perLevel = String.valueOf(perLevel);
            this.key = key;
        }
    }
}