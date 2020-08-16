package com.github.malitsplus.shizurunotes.ui.base

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.*
import com.github.malitsplus.shizurunotes.ui.analyze.AnalyzeViewModel
import com.github.malitsplus.shizurunotes.ui.shared.EquipmentAllKey

data class SpaceVT(
    override val data: Int = 25,
    override val layoutId: Int = R.layout.item_space,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Int>

data class DividerVT(
    override val data: Int = 0,
    override val layoutId: Int = R.layout.item_divider,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Int>

data class CharaProfileVT(
    override val data: Chara,
    override val layoutId: Int = R.layout.item_chara_profile,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Chara>

data class CharaUniqueEquipmentVT(
    override val data: Equipment,
    override val layoutId: Int = R.layout.item_chara_unique_equipment,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Equipment>

data class CharaRarity6StatusVT(
    override val data: List<Rarity6Status>,
    override val layoutId: Int = R.layout.item_chara_rarity_6,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<List<Rarity6Status>>

data class CharaRankEquipmentAllVT(
    override val data: List<EquipmentAllKey>,
    override val layoutId: Int = R.layout.item_chara_equipment_all,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<List<EquipmentAllKey>>

data class CharaRankEquipmentVT(
    override val data: Map.Entry<Int, List<Equipment>>,
    override val layoutId: Int = R.layout.item_chara_rank_equipment,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Map.Entry<Int, List<Equipment>>>

data class EquipmentBasicVT(
    override val data: Equipment,
    override val layoutId: Int = R.layout.item_equipment_basic,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Equipment>

data class ItemBasicVT(
    override val data: Item,
    override val layoutId: Int = R.layout.item_item_basic,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Item>

data class PropertyVT(
    override val data: Map.Entry<PropertyKey, Int>,
    override val layoutId: Int = R.layout.item_property,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Map.Entry<PropertyKey, Int>>

data class EquipmentLevelVT(
    override val data: Equipment,
    override val layoutId: Int = R.layout.item_equipment_level,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Equipment>

data class EquipmentCraftVT(
    override val data: Map.Entry<Item, Int>,
    override val layoutId: Int = R.layout.item_equipment_craft_num,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Map.Entry<Item, Int>>

data class EquipmentCharaLinkVT(
    override val data: CharaEquipmentLink,
    override val layoutId: Int = R.layout.item_equipment_chara_link,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<CharaEquipmentLink>

data class TextTagVT(
    override val data: String,
    override val layoutId: Int = R.layout.item_text_tag,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<String>

data class EnemyBasicVT(
    override val data: Enemy,
    override val layoutId: Int = R.layout.item_enemy_basic,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Enemy>

data class EnemyChildVT(
    override val data: Enemy,
    override val layoutId: Int = R.layout.item_enemy_child,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Enemy>

data class AttackPatternVT(
    override val data: AttackPattern.AttackPatternItem,
    override val layoutId: Int = R.layout.item_attack_pattern,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<AttackPattern.AttackPatternItem>

data class EnemySkillVT(
    override val data: Skill,
    override val layoutId: Int = R.layout.item_enemy_skill,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Skill>

data class StringIntVT(
    override val data: Map.Entry<String, Int>,
    override val layoutId: Int = R.layout.item_resist_property,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Map.Entry<String, Int>>

data class MinionBasicVT(
    override val data: Minion,
    override val layoutId: Int = R.layout.item_minion_basic,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Minion>

data class CharaListVT(
    override val data: Chara,
    override val layoutId: Int = R.layout.item_chara,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Chara>

data class InGameStatComparisonVT(
    override val data: InGameStatComparison,
    override val layoutId: Int = R.layout.item_comparison_in_game_stats,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<InGameStatComparison>

data class DescriptionVT(
    override val data: String,
    override val layoutId: Int = R.layout.item_description,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<String>

data class QuestAreaVT(
    override val data: QuestArea,
    override val layoutId: Int = R.layout.item_quest_area,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<QuestArea>

data class HatsuneStageVT(
    override val data: HatsuneStage,
    override val layoutId: Int = R.layout.item_hatsune_stage,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<HatsuneStage>

data class HatsuneWaveVT(
    override val data: Map.Entry<String, WaveGroup>,
    override val layoutId: Int = R.layout.item_hatsune_wave,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Map.Entry<String, WaveGroup>>

data class TowerAreaVT(
    override val data: TowerArea,
    override val layoutId: Int = R.layout.item_tower_area,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<TowerArea>

data class TowerWaveVT(
    override val data: Map.Entry<String, WaveGroup>,
    override val layoutId: Int = R.layout.item_tower_wave,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Map.Entry<String, WaveGroup>>

data class PropertyGroupVT(
    override val data: Property,
    override val layoutId: Int = R.layout.item_property_group,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<Property>

data class AnalyzePanelVT(
    override val data: AnalyzeViewModel,
    override val layoutId: Int = R.layout.item_analyze_adjust,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<AnalyzeViewModel>

data class HintTextVT(
    override val data: String,
    override val layoutId: Int = R.layout.item_hint_text,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<String>

data class TodayEventVT(
    override val data: EventSchedule,
    override val layoutId: Int = R.layout.item_today_schedule,
    override val isUserInteractionEnabled: Boolean = false
) : ViewType<EventSchedule>

data class CharaIconVT(
    override val data: Chara,
    override val layoutId: Int = R.layout.item_grid_icon_chara,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Chara>

data class CharaTargetVT(
    override val data: Chara,
    override val layoutId: Int = R.layout.item_my_chara_target,
    override val isUserInteractionEnabled: Boolean = true
) : ViewType<Chara>