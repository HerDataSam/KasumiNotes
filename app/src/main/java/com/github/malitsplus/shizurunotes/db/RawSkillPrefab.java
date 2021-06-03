package com.github.malitsplus.shizurunotes.db;


import com.github.malitsplus.shizurunotes.data.SkillPrefab;

import java.util.List;

public class RawSkillPrefab {
    public SkillInfo Attack;
    public List<SkillInfoData> UnionBurstList;
    public List<SkillInfoData> MainSkillList;
    public List<SkillInfoData> SpecialSkillList;
    public List<SkillInfoData> SpecialSkillEvolutionList;
    public List<SkillInfoData> UnionBurstEvolutionList;
    public List<SkillInfoData> MainSkillEvolutionList;
    public List<SkillInfoData> SubUnionBurstList;

    public static final class SkillInfoData {
        public SkillInfo data;

        public SkillInfoData(SkillInfo info) {
            data = info;
        }
    }

    public static final class SkillInfo {
        public List<SkillParametersData> ActionParametersOnPrefab;
    }

    public static final class SkillParametersData {
        public SkillParameters data;
    }

    public static final class SkillParameters {
        public int Visible;
        public int ActionType;
        public List<SkillDetailsData> Details;
    }

    public static final class SkillDetailsData {
        public SkillDetails data;
    }

    public static final class SkillDetails {
        public int Visible;
        public List<SkillExecTimeData> ExecTimeForPrefab;
        public int ActionId;
    }

    public static final class SkillExecTimeData {
        public SkillExecTime data;
    }

    public static class SkillExecTime {
        public double Time;
        public int DamageNumType;
        public double Weight;
        public double DamageNumScale;

        public SkillPrefab getSkillPrefab() {
            return new SkillPrefab(Time, DamageNumType, Weight, DamageNumScale);
        }
    }
}

