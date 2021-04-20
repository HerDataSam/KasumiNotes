package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.R;
import com.github.malitsplus.shizurunotes.common.I18N;
import com.github.malitsplus.shizurunotes.common.Statics;
import com.github.malitsplus.shizurunotes.data.Chara;
import com.github.malitsplus.shizurunotes.user.UserSettings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RawUnitBasic {
    public int unit_id;
    public String unit_name;
    public int prefab_id;
    public int move_speed;
    public int search_area_width;
    public int atk_type;
    public double normal_atk_cast_time;
    public int guild_id;
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

    public void setCharaBasic(Chara chara){
        chara.setCharaId(unit_id / 100);

        chara.setUnitId(unit_id);
        chara.unitName = unit_name;
        chara.setPrefabId(prefab_id);
        chara.setSearchAreaWidth(search_area_width);
        chara.setAtkType(atk_type);

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
        if (UserSettings.get().getUserServer().equals("kr")) {
            if (unit_id == 110001 || unit_id == 110301) {
                chara.startTime = LocalDateTime.parse("2021/03/26 15:00:00", formatter);
            } else if (unit_id == 110401 || unit_id == 110601) {
                chara.startTime = LocalDateTime.parse("2021/04/25 15:00:00", formatter);
            }
        }// TODO: THIS IS A TEMPORAL SOLUTION
        if (LocalDateTime.now().isBefore(chara.startTime))
            chara.startTimeStr = I18N.getString(R.string.text_update_date, chara.startTime.format(DateTimeFormatter.ofPattern("yy/MM/dd")));
        else
            chara.startTimeStr = "";
        chara.iconUrl = String.format(Locale.US, Statics.ICON_URL, prefab_id + 30);
        chara.imageUrl = String.format(Locale.US, Statics.IMAGE_URL, prefab_id + 30);

        if(search_area_width < 300) {
            chara.position = "1";
            chara.setPositionIcon(R.drawable.position_forward);
        } else if(search_area_width > 300 && search_area_width < 600){
            chara.position = "2";
            chara.setPositionIcon(R.drawable.position_middle);
        } else if(search_area_width > 600) {
            chara.position = "3";
            chara.setPositionIcon(R.drawable.position_rear);
        }
    }
}
