package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.common.Statics;
import com.github.malitsplus.shizurunotes.data.Chara;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RawUnitBasic {
    private static final int[] DRAWABLE_RESOURCES = {
            R.drawable.mic_chara_icon_error, // Index 0 (unused, but keeps indices aligned)
            R.drawable.unit_talent_001,     // Index 1
            R.drawable.unit_talent_002,     // Index 2
            R.drawable.unit_talent_003,     // Index 3
            R.drawable.unit_talent_004,     // Index 4
            R.drawable.unit_talent_005      // Index 5
    };
    public int unit_id;
    public int unit_conversion_id;
    public String unit_name;
    public int prefab_id;
    public int move_speed;
    public int search_area_width;
    public int atk_type;
    public double normal_atk_cast_time;
    public int guild_id;
    public int talent_id;
    public String comment;
    public String start_time;
    public String age;
    public String guild;
    public String race;
    public String height;
    public String weight;
    public String birth_month;
    public String birth_day;
    public String blood_type;
    public String favorite;
    public String voice;
    public String catch_copy;
    public String self_text;
    public String actual_name;
    public String kana;

    public void setCharaBasic(Chara chara) {
        chara.setCharaId(unit_id / 100);

        chara.setUnitId(unit_id);
        chara.setUnitConversionId(unit_conversion_id);
        chara.unitName = unit_name;
        chara.setPrefabId(prefab_id);
        chara.setSearchAreaWidth(search_area_width);
        chara.setAtkType(atk_type);
        chara.setTalent(talent_id);

        chara.setMoveSpeed(move_speed);
        chara.setNormalAtkCastTime(normal_atk_cast_time);
        chara.actualName = actual_name;
        chara.age = age;
        chara.setGuildId(guild_id);

        chara.guild = guild;
        chara.race = race;
        chara.height = height;
        chara.weight = weight;
        chara.birthMonth = birth_month;

        chara.birthDay = birth_day;
        chara.bloodType = blood_type;
        chara.favorite = favorite;
        chara.voice = voice;
        chara.catchCopy = catch_copy;

        chara.setComment(comment);
        chara.kana = kana;
        chara.setSelfText(self_text == null ? "" : self_text.replaceAll("\\\\n", "\n"));

        //需要处理的字串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");
        chara.startTime = LocalDateTime.parse(start_time, formatter);

        if (LocalDateTime.now().isBefore(chara.startTime))
            chara.startTimeStr = I18N.getString(R.string.text_update_date, chara.startTime.format(DateTimeFormatter.ofPattern("yy/MM/dd")));
        else
            chara.startTimeStr = "";
        chara.iconUrl = String.format(Locale.US, Statics.ICON_URL, prefab_id + 30);
        chara.imageUrl = String.format(Locale.US, Statics.FULL_IMAGE_URL, prefab_id + 30);

        int talent = talent_id;
        if (talent > 5)
            talent = 0;
        chara.setTalentIcon(DRAWABLE_RESOURCES[talent]);

        if (search_area_width < 300) {
            chara.position = "1";
            chara.positionString = I18N.getString(R.string.ui_chip_position_forward);
            chara.setPositionIcon(R.drawable.position_forward);
        } else if (search_area_width < 600) {
            chara.position = "2";
            chara.positionString = I18N.getString(R.string.ui_chip_position_middle);
            chara.setPositionIcon(R.drawable.position_middle);
        } else {
            chara.position = "3";
            chara.positionString = I18N.getString(R.string.ui_chip_position_rear);
            chara.setPositionIcon(R.drawable.position_rear);
        }
    }
}
