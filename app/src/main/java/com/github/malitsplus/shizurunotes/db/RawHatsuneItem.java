package com.github.malitsplus.shizurunotes.db;

import com.github.malitsplus.shizurunotes.data.Enemy;
import com.github.malitsplus.shizurunotes.data.GeneralItem;
import com.github.malitsplus.shizurunotes.data.Item;
import com.github.malitsplus.shizurunotes.data.ItemType;
import com.github.malitsplus.shizurunotes.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RawHatsuneItem {
    public int event_id;
    public int boss_ticket_id;
    public int gacha_ticket_id;
    public int unit_material_id_1;
    public int unit_material_id_2;
    public int unit_material_id_3;
    public int unit_material_id_4;
    public int unit_material_id_5;
    public int unit_material_id_6;
    public int unit_material_id_7;
    public int unit_material_id_8;
    public int unit_material_id_9;
    public int unit_material_id_10;

    public List<Item> unitMaterials () {
        List<Item> materials = new ArrayList<Item>();
        for (int i = 1; i <= 10; i++){
            int itemId = (int) Utils.getValueFromObject(this, "unit_material_id_" + i);
            if (itemId != 0){
                RawItemData rawItem = DBHelper.get().getItemData(itemId);
                assert rawItem != null;
                materials.add(
                        new GeneralItem(
                                rawItem.item_id,
                                rawItem.item_name,
                                ItemType.GENERAL_ITEM,
                                rawItem.description,
                                rawItem.item_id,
                                99
                        )
                );
            }
        }
        return materials;
    }
}
