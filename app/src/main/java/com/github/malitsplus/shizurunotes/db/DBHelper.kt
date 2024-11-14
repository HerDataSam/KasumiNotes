package com.github.malitsplus.shizurunotes.db

import android.annotation.SuppressLint
import android.app.Application
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.utils.FileUtils
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.LogUtils
import com.github.malitsplus.shizurunotes.utils.Utils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

class DBHelper private constructor(
    val application: Application
) : SQLiteOpenHelper(application, Statics.DB_FILE_NAME, null, DB_VERSION) {

    companion object {
        const val DB_VERSION = 1

        @Volatile
        private lateinit var instance: DBHelper

        fun with(application: Application): DBHelper {
            synchronized (DBHelper::class.java) {
                instance = DBHelper(application)
            }
            return instance
        }

        @JvmStatic
        fun get(): DBHelper = instance
    }

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        dropAll(db)
        onCreate(db)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> cursor2List(
        cursor: Cursor,
        theClass: Class<*>
    ): List<T>? {
        val result: MutableList<T> = mutableListOf()
        val arrField = theClass.declaredFields
        try {
            while (cursor.moveToNext()) {
                if (cursor.isBeforeFirst) {
                    continue
                }
                val bean = theClass.newInstance()
                for (f in arrField) {
                    val columnName = f.name
                    val columnIdx = cursor.getColumnIndex(columnName)
                    if (columnIdx != -1) {
                        if (!f.isAccessible) {
                            f.isAccessible = true
                        }
                        val type = f.type
                        if (type == Byte::class.javaPrimitiveType) {
                            f[bean] = cursor.getShort(columnIdx).toByte()
                        } else if (type == Short::class.javaPrimitiveType) {
                            f[bean] = cursor.getShort(columnIdx)
                        } else if (type == Int::class.javaPrimitiveType) {
                            f[bean] = cursor.getInt(columnIdx)
                        } else if (type == Long::class.javaPrimitiveType) {
                            f[bean] = cursor.getLong(columnIdx)
                        } else if (type == String::class.java) {
                            f[bean] = cursor.getString(columnIdx)
                        } else if (type == ByteArray::class.java) {
                            f[bean] = cursor.getBlob(columnIdx)
                        } else if (type == Boolean::class.javaPrimitiveType) {
                            f[bean] = cursor.getInt(columnIdx) == 1
                        } else if (type == Float::class.javaPrimitiveType) {
                            f[bean] = cursor.getFloat(columnIdx)
                        } else if (type == Double::class.javaPrimitiveType) {
                            f[bean] = cursor.getDouble(columnIdx)
                        }
                    }
                }
                result.add(bean as T)
            }
        } catch (e: InstantiationException) {
            e.printStackTrace()
            return null
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            return null
        } finally {
            cursor.close()
        }
        return result
    }

    /***
     * 准备游标
     * @param tableName 表名
     * @param key WHERE [key] IN ([keyValue])
     * @param keyValue WHERE [key] IN ([keyValue])
     * @return 存有数据的游标
     */
    @SuppressLint("Recycle")
    private fun prepareCursor(
        tableName: String,
        key: String?,
        keyValue: List<String>?
    ): Cursor? {
        if (!FileUtils.checkFile(
                FileUtils.getDbFilePath())) return null
        val db = readableDatabase ?: return null
        return if (key == null || keyValue == null || keyValue.isEmpty()) {
            db.rawQuery("SELECT * FROM $tableName ", null)
        } else {
            val paraBuilder = StringBuilder()
            paraBuilder.append("(")
            for (s in keyValue) {
                if (!TextUtils.isEmpty(s)) {
                    paraBuilder.append("?")
                    paraBuilder.append(", ")
                }
            }
            paraBuilder.delete(paraBuilder.length - 2, paraBuilder.length)
            paraBuilder.append(")")
            db.rawQuery(
                "SELECT * FROM $tableName WHERE $key IN $paraBuilder ",
                keyValue.toTypedArray()
            )
        }
    }
    /******************* Method For Use  */
    /***
     * 由表名和类名无条件从数据库获取实体列表
     * @param tableName 表名
     * @param theClass 类名
     * @param <T> theClass的类
     * @return 生成的实体列表
    </T> */
    private fun <T> getBeanList(
        tableName: String,
        theClass: Class<*>
    ): List<T>? {
        val cursor = prepareCursor(tableName, null, null) ?: return null
        return cursor2List(cursor, theClass)
    }

    /***
     * 由表名、类名、条件键值从数据库获取实体列表
     * @param tableName 表名
     * @param theClass 类名
     * @param key WHERE [key] IN ([keyValue])
     * @param keyValues WHERE [key] IN ([keyValue])
     * @param <T> theClass的类
     * @return 生成的实体列表
    </T> */
    private fun <T> getBeanList(
        tableName: String,
        theClass: Class<*>,
        key: String?,
        keyValues: List<String>?
    ): List<T>? {
        val cursor = prepareCursor(tableName, key, keyValues) ?: return null
        return cursor2List(cursor, theClass)
    }

    /***
     * 由表名、类名、条件键值从数据库获取单个实体
     * @param tableName 表名
     * @param theClass 类名
     * @param key WHERE [key] IN ([keyValue])
     * @param keyValue WHERE [key] IN ([keyValue])
     * @param <T> theClass的类
     * @return 生成的实体
    </T> */
    private fun <T> getBean(
        tableName: String,
        theClass: Class<*>,
        key: String?,
        keyValue: String
    ): T? {
        val cursor =
            prepareCursor(tableName, key, listOf(keyValue)) ?: return null
        val data: List<T>? = cursor2List(cursor, theClass)
        return if (data?.isNotEmpty() == true) data[0] else null
    }

//    /***
//     * 由SQL语句、SQL中的键值从数据库获取单个实体
//     * @param sql SQL语句
//     * @param keyValue IN (?) => ?=keyValue
//     * @param theClass 类名
//     * @param <T> theClass的类
//     * @return 生成的实体
//    </T> */
//    @SuppressLint("Recycle")
//    private fun <T> getBeanByRaw(
//        sql: String?,
//        keyValue: String,
//        theClass: Class<*>
//    ): T? {
//        if (!Utils.checkFile(Statics.DB_PATH + Statics.DB_FILE)) return null
//        val cursor =
//            readableDatabase.rawQuery(sql, arrayOf(keyValue)) ?: return null
//        val data: List<T>? = cursor2List(cursor, theClass)
//        return if (data?.isNotEmpty() == true) data[0] else null
//    }

    /***
     * 由SQL语句、SQL中的键值从数据库获取单个实体
     * @param sql SQL语句
     * @param keyValue IN (?) => ?=keyValue
     * @param theClass 类名
     * @param <T> theClass的类
     * @return 生成的实体
    </T> */
    @SuppressLint("Recycle")
    private fun <T> getBeanByRaw(
        sql: String?,
        theClass: Class<*>
    ): T? {
        if (!FileUtils.checkFile(
                FileUtils.getDbFilePath())) return null
        try {
            val cursor =
                sql?.let { readableDatabase.rawQuery(it, null) } ?: return null
            val data: List<T>? = cursor2List(cursor, theClass)
            return if (data?.isNotEmpty() == true) data[0] else null
        } catch (e: Exception) {
            LogUtils.file(
                LogUtils.E, "getBeanByRaw", e.message, e.stackTrace)
            return null
        }
    }

//    /***
//     * 由SQL语句无条件从数据库获取实体列表
//     * @param sql SQL语句
//     * @param theClass 类名
//     * @param <T> theClass的类
//     * @return 生成的实体列表
//    </T> */
//    @SuppressLint("Recycle")
//    private fun <T> getBeanListByRaw(
//        sql: String?,
//        theClass: Class<*>
//    ): List<T>? {
//        if (!Utils.checkFile(Statics.DB_PATH + Statics.DB_FILE)) return null
//        val cursor = readableDatabase.rawQuery(sql, null) ?: return null
//        return cursor2List(cursor, theClass)
//    }

    /***
     * 由SQL语句无条件从数据库获取实体列表
     * @param sql SQL语句
     * @param keyValue 替换？的值
     * @param theClass 类名
     * @param <T> theClass的类
     * @return 生成的实体列表
    </T> */
    @SuppressLint("Recycle")
    private fun <T> getBeanListByRaw(
        sql: String?,
        theClass: Class<*>
    ): List<T>? {
        if (!FileUtils.checkFile(
                FileUtils.getDbFilePath())) return null
        try {
            val cursor =
                sql?.let { readableDatabase.rawQuery(it, null) } ?: return null
            return cursor2List(cursor, theClass)
        } catch (e: Exception) {
            LogUtils.file(
                LogUtils.E, "getBeanListByRaw", e.message, e.stackTrace)
            return null
        }
    }

    /***
     * 删除所有表
     * @param db
     */
    private fun dropAll(db: SQLiteDatabase) {
        val sqls: MutableList<String> =
            ArrayList()
        val op = "DROP TABLE IF EXISTS "
        for (field in this.javaClass.declaredFields) {
            if (field.name.startsWith("TABLE_NAME")) {
                try {
                    sqls.add(op + field[this])
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
        for (sql in sqls) {
            db.execSQL(sql)
        }
    }

    /***
     * 删除表
     * @param tableName
     * @return
     */
    fun dropTable(tableName: String): Boolean {
        return try {
            writableDatabase.execSQL("DROP TABLE IF EXISTS $tableName")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /***
     * 获取查询语句的第一行第一列值
     * @param sql
     * @return
     */
    private fun getOne(sql: String?): String? {
        if (!FileUtils.checkFile(
                FileUtils.getDbFilePath())) return null
        val cursor = sql?.let { readableDatabase.rawQuery(it, null) } ?: return null
        cursor.moveToNext()
        val result = cursor.getString(0)
        cursor.close()
        return result
    }

    /***
     * count
     * @param sql
     * @return
     */
    private fun getCount(sql: String?): Int? {
        if (!FileUtils.checkFile(
                FileUtils.getDbFilePath())) return null
        val cursor = sql?.let { readableDatabase.rawQuery(it, null) } ?: return null
        cursor.moveToFirst()
        val result = cursor.getInt(0)
        cursor.close()
        return result
    }

    /***
     * 获取 int-string map
     * @param sql
     * @return
     */
    private fun getIntStringMap(
        sql: String,
        key: String?,
        value: String?
    ): MutableMap<Int, String>? {
        if (!FileUtils.checkFile(
                FileUtils.getDbFilePath())) return null
        val cursor = readableDatabase.rawQuery(sql, null)
        val result: MutableMap<Int, String> = HashMap()
        while (cursor.moveToNext()) {
            val keyIndex = cursor.getColumnIndex(key)
            val valueIndex = cursor.getColumnIndex(value)
            if (keyIndex >= 0 && valueIndex >= 0)
                result[cursor.getInt(keyIndex)] = cursor.getString(valueIndex)
        }
        cursor.close()
        return result
    }

    private fun getIntLocalDateTimeMap(
        sql: String,
        key: String?,
        value: String?
    ): MutableMap<Int, LocalDateTime>? {
        if (!FileUtils.checkFile(
                FileUtils.getDbFilePath())) return null
        val cursor = readableDatabase.rawQuery(sql, null)
        val result: MutableMap<Int, LocalDateTime> = HashMap()
        while (cursor.moveToNext()) {
            val keyIndex = cursor.getColumnIndex(key)
            val valueIndex = cursor.getColumnIndex(value)
            if (keyIndex >= 0 && valueIndex >= 0)
                result[cursor.getInt(keyIndex)] = LocalDateTime.parse(cursor.getString(valueIndex))
        }
        cursor.close()
        return result
    }

    /************************* public field **************************/

    /***
     * 获取角色基础数据
     */
    fun getCharaBase(): List<RawUnitBasic>? {
        val isConvertible = getOne("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='unit_conversion'")!! != "0"

        val uc_id = if (isConvertible)
            """,uc.unit_id 'unit_conversion_id'"""
        else
            """"""
        val uc_table = if (isConvertible)
            """LEFT JOIN unit_conversion as uc ON ud.unit_id = uc.original_unit_id"""
        else
            """"""

        return getBeanListByRaw(
            """
                SELECT ud.unit_id
                ,ud.unit_name
                ,ud.kana
                ,ud.prefab_id
                ,ud.move_speed
                ,ud.search_area_width
                ,ud.atk_type
                ,ud.normal_atk_cast_time
                ,ud.guild_id
                ,ud.comment
                ,ud.start_time
                ,up.age
                ,up.guild
                ,up.race
                ,up.height
                ,up.weight
                ,up.birth_month
                ,up.birth_day
                ,up.blood_type
                ,up.favorite
                ,up.voice
                ,up.catch_copy
                ,up.self_text
                $uc_id
                ,IFNULL(au.unit_name, ud.unit_name) 'actual_name' 
                FROM unit_data AS ud 
                JOIN unit_profile AS up ON ud.unit_id = up.unit_id 
                LEFT JOIN actual_unit_background AS au ON substr(ud.unit_id,1,4) = substr(au.unit_id,1,4)
                $uc_table
                WHERE ud.comment <> ''
                AND ud.unit_id < 400000 
                """,
            RawUnitBasic::class.java
        )
    }

    /***
     * 获取角色星级数据
     */
    fun getUnitRarity(unitId: Int): RawUnitRarity? {
        return getBeanByRaw<RawUnitRarity>(
            """
                SELECT * 
                FROM unit_rarity 
                WHERE unit_id=$unitId 
                ORDER BY rarity DESC 
                """,
            RawUnitRarity::class.java
        )
    }

    /***
     * 获取角色星级数据
     */
    fun getUnitRarityList(unitId: Int): List<RawUnitRarity>? {
        return getBeanListByRaw(
            """
                SELECT * 
                FROM unit_rarity 
                WHERE unit_id=$unitId 
                ORDER BY rarity DESC 
                """,
            RawUnitRarity::class.java
        )
    }

    /***
     * 获取角色剧情数据
     */
    fun getCharaStoryStatus(charaId: Int): List<RawCharaStoryStatus>? {
        return getBeanListByRaw(
            """
                SELECT a.*, d.love_level
                FROM chara_story_status AS a 
                INNER JOIN story_detail AS d ON d.story_id = a.story_id
                WHERE chara_id_1 = $charaId 
                OR chara_id_2 = $charaId 
                OR chara_id_3 = $charaId 
                OR chara_id_4 = $charaId 
                OR chara_id_5 = $charaId 
                OR chara_id_6 = $charaId 
                OR chara_id_7 = $charaId 
                OR chara_id_8 = $charaId 
                OR chara_id_9 = $charaId 
                OR chara_id_10 = $charaId 
                """,
            RawCharaStoryStatus::class.java
        )
    }

    /***
     * 获取角色Rank汇总数据
     * @param unitId 角色id
     * @return
     */
    fun getCharaPromotionStatus(unitId: Int): List<RawPromotionStatus>? {
        return  getBeanListByRaw(
            """
                SELECT * 
                FROM unit_promotion_status 
                WHERE unit_id=$unitId 
                ORDER BY promotion_level DESC 
                """,
            RawPromotionStatus::class.java
        )
    }

    fun getCharaPromotionBonus(unitId: Int): List<RawPromotionStatus>? {
        return getBeanListByRaw(
            """
                SELECT * 
                FROM promotion_bonus 
                WHERE unit_id=$unitId 
                ORDER BY promotion_level DESC 
                """,
            RawPromotionStatus::class.java
        )
    }

    /***
     * 获取角色装备数据
     * @param unitId 角色id
     * @return
     */
    fun getCharaPromotion(unitId: Int): List<RawUnitPromotion>? {
        return getBeanListByRaw(
            """
                SELECT * 
                FROM unit_promotion 
                WHERE unit_id=$unitId
                ORDER BY promotion_level DESC 
                """,
            RawUnitPromotion::class.java
        )
    }

    /***
     * 获取装备数据
     * @param slots 装备ids
     * @return
     */
    fun getEquipments(slots: ArrayList<Int>?): List<RawEquipmentData>? {
        return getBeanListByRaw(
            """
                SELECT 
                a.*
                ,b.max_equipment_enhance_level 
                FROM equipment_data a, 
                ( SELECT promotion_level, max( equipment_enhance_level ) max_equipment_enhance_level FROM equipment_enhance_data GROUP BY promotion_level ) b 
                WHERE a.promotion_level = b.promotion_level 
                AND a.equipment_id IN ( ${Utils.splitIntegerWithComma(slots)} ) 
                """,
            RawEquipmentData::class.java
        )
    }

    /***
     * 获取所有装备数据
     */
    fun getEquipmentAll(): List<RawEquipmentData>? {
        return getBeanListByRaw(
            """
                SELECT 
                a.* 
                ,ifnull(b.max_equipment_enhance_level, 0) 'max_equipment_enhance_level'
                ,e.description 'catalog' 
                ,substr(a.equipment_id,3,1) * 10 + substr(a.equipment_id,6,1) 'rarity' 
                ,f.condition_equipment_id_1
                ,f.consume_num_1
                ,f.condition_equipment_id_2
                ,f.consume_num_2
                ,f.condition_equipment_id_3
                ,f.consume_num_3
                ,f.condition_equipment_id_4
                ,f.consume_num_4
                ,f.condition_equipment_id_5
                ,f.consume_num_5
                ,f.condition_equipment_id_6
                ,f.consume_num_6
                ,f.condition_equipment_id_7
                ,f.consume_num_7
                ,f.condition_equipment_id_8
                ,f.consume_num_8
                ,f.condition_equipment_id_9
                ,f.consume_num_9
                ,f.condition_equipment_id_10
                ,f.consume_num_10
                FROM equipment_data a  
                LEFT JOIN ( SELECT promotion_level, max( equipment_enhance_level ) max_equipment_enhance_level FROM equipment_enhance_data GROUP BY promotion_level ) b ON a.promotion_level = b.promotion_level 
                LEFT JOIN equipment_enhance_rate AS e ON a.equipment_id=e.equipment_id
                LEFT JOIN equipment_craft AS f ON a.equipment_id = f.equipment_id
                WHERE a.equipment_id < 113000 or (a.equipment_id > 1000000 and a.equipment_id < 11000000)
                ORDER BY a.require_level DESC, a.equipment_id ASC 
                """,//substr(a.equipment_id,3,1) * 10 + substr(a.equipment_id,6,1) DESC,
            RawEquipmentData::class.java
        )
    }

    /***
     * 获取装备强化数据
     * @param slots 装备ids
     * @return
     */
    fun getEquipmentEnhance(slots: ArrayList<Int>?): List<RawEquipmentEnhanceData>? {
        return getBeanListByRaw(
            """
                SELECT * 
                FROM equipment_enhance_rate 
                WHERE equipment_id IN ( ${Utils.splitIntegerWithComma(slots)} ) 
                """,
            RawEquipmentEnhanceData::class.java
        )
    }

    /***
     * 获取装备强化数据
     * @param id 装备ids
     * @return
     */
    fun getEquipmentEnhance(equipmentId: Int): RawEquipmentEnhanceData? {
        return getBeanByRaw(
            """
                SELECT * 
                FROM equipment_enhance_rate 
                WHERE equipment_id = $equipmentId 
                """,
            RawEquipmentEnhanceData::class.java
        )
    }

    /***
     * 获取所有装备强化数据
     */
    fun getEquipmentEnhance(): List<RawEquipmentEnhanceData>? {
        return getBeanListByRaw(
            """
                SELECT * 
                FROM equipment_enhance_rate 
                """,
            RawEquipmentEnhanceData::class.java
        )
    }

    /***
     * 获取专属装备数据
     * @param unitId 角色id
     * @return
     */
    fun getUniqueEquipment(unitId: Int): List<RawUniqueEquipmentData>? {
        var uniqueEquipmentTableName = "unit_unique_equipment"
        val uniqueEquipmentTableCount = getOne("""
            SELECT COUNT(*) 
            FROM sqlite_master 
            WHERE type='table' 
            AND name='$uniqueEquipmentTableName'"""
        )
        if (!uniqueEquipmentTableCount.equals("1")) {
            uniqueEquipmentTableName = "unit_unique_equip"
        }

        return getBeanListByRaw(
            """
                SELECT e.*
                ,c.item_id_1
                ,c.consume_num_1
                ,c.item_id_2
                ,c.consume_num_2
                ,c.item_id_3
                ,c.consume_num_3
                ,c.item_id_4
                ,c.consume_num_4
                ,c.item_id_5
                ,c.consume_num_5
                ,c.item_id_6
                ,c.consume_num_6
                ,c.item_id_7
                ,c.consume_num_7
                ,c.item_id_8
                ,c.consume_num_8 
                ,c.item_id_9
                ,c.consume_num_9
                ,c.item_id_10
                ,c.consume_num_10
                ,u.equip_slot
                FROM unique_equipment_data AS e 
                JOIN $uniqueEquipmentTableName AS u ON e.equipment_id=u.equip_id 
                LEFT JOIN unique_equipment_craft AS c ON e.equipment_id=c.equip_id
                WHERE u.unit_id=$unitId 
                """,
            RawUniqueEquipmentData::class.java
        )
    }

    /***
     * 获取专属装备强化数据
     * @param unitId 角色id
     * @return
     */
    fun getUniqueEquipmentEnhance(unitId: Int, equipSlot: Int): List<RawUniqueEquipmentEnhanceData>? {
        var enhanceTableName = "unique_equip_enhance_rate"
        val enhanceTableCount = getOne("""
            SELECT COUNT(*) 
            FROM sqlite_master 
            WHERE type='table' 
            AND name='$enhanceTableName'"""
        )
        if (!enhanceTableCount.equals("1")) {
            enhanceTableName = "unique_equipment_enhance_rate"
        }

        var uniqueEquipmentTableName = "unit_unique_equipment"
        val uniqueEquipmentTableCount = getOne("""
            SELECT COUNT(*) 
            FROM sqlite_master 
            WHERE type='table' 
            AND name='$uniqueEquipmentTableName'"""
        )
        if (!uniqueEquipmentTableCount.equals("1")) {
            uniqueEquipmentTableName = "unit_unique_equip"
        }

        return getBeanListByRaw(
            """
                SELECT e.* 
                FROM $enhanceTableName AS e 
                JOIN $uniqueEquipmentTableName AS u ON e.equipment_id=u.equip_id 
                WHERE u.unit_id=$unitId and u.equip_slot=$equipSlot
                """,
            RawUniqueEquipmentEnhanceData::class.java
        )
    }

    fun getUniqueEquipmentRankUp(unitId: Int): List<RawUniqueEquipmentRankUp>? {
        return getBeanListByRaw(
            """
                SELECT r.*
                FROM unique_equipment_rankup as r
                """,
            RawUniqueEquipmentRankUp::class.java
        )
    }

    /***
     * Unlock rarity 6
     * @param unitID
     * @return
     */
    fun getUnlockRarity6(unitId: Int): List<RawUnlockRarity6>? {
        return getBeanListByRaw(
            """
                SELECT * 
                FROM unlock_rarity_6 as u
                WHERE u.unit_id=$unitId
                AND u.unlock_level > 0
                """,
            RawUnlockRarity6::class.java
        )
    }

    fun getExEquipmentAll(): List<RawEquipmentData>? {
        val count = getOne("""
            SELECT COUNT(*) 
            FROM sqlite_master 
            WHERE type='table' 
            AND name='ex_equipment_data'"""
        )
        if (!count.equals("1")) {

            return null
        } else
            return null
    }

    /***
     * Get item data
     * @param itemId
     * @return
     */
    fun getItemData(itemId: Int): RawItemData? {
        return getBeanByRaw(
            """
                SELECT *
                FROM item_data as i
                WHERE i.item_id=$itemId
                """,
            RawItemData::class.java
        )
    }

    /***
     * 获取角色技能数据
     * @param unitId
     * @return
     */
    fun getUnitSkillData(unitId: Int): RawUnitSkillData? {
        return getBeanByRaw<RawUnitSkillData>(
            """
                SELECT * 
                FROM unit_skill_data 
                WHERE unit_id=$unitId 
                """,
            RawUnitSkillData::class.java
        )
    }

    /***
     * 获取技能数据
     * @param skillId
     * @return
     */
    fun getSkillData(skillId: Int): RawSkillData? {
        return getBeanByRaw<RawSkillData>(
            """
                SELECT * 
                FROM skill_data 
                WHERE skill_id=$skillId 
                """,
            RawSkillData::class.java
        )
    }

    /***
     * 获取技能动作数据
     * @param actionId
     * @return
     */
    fun getSkillAction(actionId: Int): RawSkillAction? {
        return getBeanByRaw<RawSkillAction>(
            """
                SELECT * 
                FROM skill_action 
                WHERE action_id=$actionId 
                """,
            RawSkillAction::class.java
        )
    }

    fun getSkillActionClasses(): List<RawSkillAction>? {
        return getBeanListByRaw(
            """
                select DISTINCT action_type 
                from skill_action 
				where action_id < 200000000
                order by action_type ASC                
                """,
            RawSkillAction::class.java
        )
    }

    /***
     * 获取行动顺序
     * @param unitId
     * @return
     */
    fun getUnitAttackPattern(unitId: Int): List<RawUnitAttackPattern>? {
        return getBeanListByRaw(
        """
            SELECT * 
            FROM unit_attack_pattern 
            WHERE unit_id=$unitId 
            ORDER BY pattern_id 
            """,
            RawUnitAttackPattern::class.java
        )
    }

    /***
     * 获取会战期次
     * @param
     * @return
     */
    fun getClanBattlePeriod(): List<RawClanBattlePeriod>? {
        return getBeanListByRaw(
            """
                SELECT cbp.* 
                FROM clan_battle_period as cbp
				INNER JOIN clan_battle_schedule as cbs
				ON cbp.clan_battle_id = cbs.clan_battle_id
                WHERE cbp.clan_battle_id > 1014 
                ORDER BY cbp.clan_battle_id DESC
                """,
                RawClanBattlePeriod::class.java
        )
    }

    /***
     * 获取会战期次
     * @param
     * @return
     */
    fun getClanBattleTrainingPeriod(): List<RawClanBattlePeriod>? {
        return getBeanListByRaw(
            """
                SELECT 
                training_id,
                clan_battle_id,
                battle_start_time as start_time,
                battle_end_time as end_time
                FROM clan_battle_training_schedule 
                WHERE clan_battle_id > 1030 
                ORDER BY clan_battle_id DESC
                """,
            RawClanBattlePeriod::class.java
        )
    }

    /***
     * 获取会战phase
     * @param
     * @return
     */
    fun getClanBattlePhase(clanBattleId: Int): List<RawClanBattlePhase>? {
        return getBeanListByRaw(
            """
                SELECT DISTINCT 
                phase 
                ,wave_group_id_1 
                ,wave_group_id_2 
                ,wave_group_id_3 
                ,wave_group_id_4 
                ,wave_group_id_5 
                FROM clan_battle_2_map_data WHERE clan_battle_id=$clanBattleId 
                ORDER BY phase DESC 
                """,
            RawClanBattlePhase::class.java
        )
    }

    /***
     * 获取wave
     * @param
     * @return
     */
    fun getWaveGroupData(waveGroupList: List<Int>): List<RawWaveGroup>? {
        return getBeanListByRaw(
                """
                    SELECT * 
                    FROM wave_group_data 
                    WHERE wave_group_id IN ( %s ) 
                    """.format(waveGroupList.toString()
                        .replace("[", "")
                        .replace("]", "")),
            RawWaveGroup::class.java
        )
    }

    /***
     * 获取wave
     * @param
     * @return
     */
    fun getWaveGroupData(waveGroupId: Int): RawWaveGroup? {
        return getBeanByRaw(
            """
            SELECT * 
            FROM wave_group_data 
            WHERE wave_group_id = $waveGroupId 
            """,
            RawWaveGroup::class.java
        )
    }

    /***
     * 获取enemyList
     * @param
     * @return
     */
    fun getEnemy(enemyIdList: List<Int>): List<RawEnemy>? {
        val enemyParameter = if (UserSettings.get().getUserServer() == "jp")
                "(SELECT * FROM enemy_parameter UNION ALL SELECT * FROM sre_enemy_parameter)"
            else
                "enemy_parameter"
        return getBeanListByRaw(
                """
                    SELECT 
                    a.* 
                    ,b.union_burst 
                    ,b.union_burst_evolution 
                    ,b.main_skill_1 
                    ,b.main_skill_evolution_1 
                    ,b.main_skill_2 
                    ,b.main_skill_evolution_2 
                    ,b.ex_skill_1 
                    ,b.ex_skill_evolution_1 
                    ,b.main_skill_3 
                    ,b.main_skill_4 
                    ,b.main_skill_5 
                    ,b.main_skill_6 
                    ,b.main_skill_7 
                    ,b.main_skill_8 
                    ,b.main_skill_9 
                    ,b.main_skill_10 
                    ,b.ex_skill_2 
                    ,b.ex_skill_evolution_2 
                    ,b.ex_skill_3 
                    ,b.ex_skill_evolution_3 
                    ,b.ex_skill_4 
                    ,b.ex_skill_evolution_4 
                    ,b.ex_skill_5 
                    ,b.sp_skill_1 
                    ,b.ex_skill_evolution_5 
                    ,b.sp_skill_2 
                    ,b.sp_skill_3 
                    ,b.sp_skill_4 
                    ,b.sp_skill_5 
                    ,c.child_enemy_parameter_1 
                    ,c.child_enemy_parameter_2 
                    ,c.child_enemy_parameter_3 
                    ,c.child_enemy_parameter_4 
                    ,c.child_enemy_parameter_5 
                    ,u.prefab_id 
                    ,u.atk_type 
                    ,u.normal_atk_cast_time
					,u.search_area_width
                    ,u.comment
                    ,u.unit_name
                    FROM 
                    unit_skill_data b 
                    ,$enemyParameter a 
                    LEFT JOIN enemy_m_parts c ON a.enemy_id = c.enemy_id 
                    LEFT JOIN unit_enemy_data u ON a.unit_id = u.unit_id 
                    WHERE 
                    a.unit_id = b.unit_id 
                    AND a.enemy_id in ( %s )  
                    """.format(enemyIdList.toString()
                        .replace("[", "")
                        .replace("]", "")),
                RawEnemy::class.java
        )
    }

    /***
     * 获取第一个enemy
     * @param
     * @return
     */
    fun getEnemy(enemyId: Int): RawEnemy? {
        return getEnemy(listOf(enemyId))?.get(0)
    }

    /***
     * 获取敌人抗性值
     * @param
     * @return
     */
    fun getResistData(resistStatusId: Int): RawResistData? {
        return getBeanByRaw<RawResistData>(
            """
                SELECT * 
                FROM resist_data 
                WHERE resist_status_id=$resistStatusId  
                """,
            RawResistData::class.java
        )
    }

    /***
     * 获取友方召唤物
     */
    fun getUnitMinion(minionId: Int): RawUnitMinion? {
        return getBeanByRaw<RawUnitMinion>(
            """
                SELECT
                a.*,
                b.union_burst,
                b.union_burst_evolution,
                b.main_skill_1,
                b.main_skill_evolution_1,
                b.main_skill_2,
                b.main_skill_evolution_2,
                b.ex_skill_1,
                b.ex_skill_evolution_1,
                b.main_skill_3,
                b.main_skill_4,
                b.main_skill_5,
                b.main_skill_6,
                b.main_skill_7,
                b.main_skill_8,
                b.main_skill_9,
                b.main_skill_10,
                b.ex_skill_2,
                b.ex_skill_evolution_2,
                b.ex_skill_3,
                b.ex_skill_evolution_3,
                b.ex_skill_4,
                b.ex_skill_evolution_4,
                b.ex_skill_5,
                b.sp_skill_1,
                b.ex_skill_evolution_5,
                b.sp_skill_2,
                b.sp_skill_3,
                b.sp_skill_4,
                b.sp_skill_5
            FROM
                unit_skill_data b,
                unit_data a
            WHERE
                a.unit_id = b.unit_id
                AND a.unit_id = $minionId
                """,
            RawUnitMinion::class.java
        )
    }

    /***
     * 获取敌方召唤物
     */
    fun getEnemyMinion(enemyId: Int): RawEnemy? {
        return getBeanByRaw<RawEnemy>(
            """
                SELECT
                d.unit_name,
                d.prefab_id,
                d.search_area_width,
                d.atk_type,
                d.move_speed,
                a.*,
                b.*,
                d.normal_atk_cast_time,
                c.child_enemy_parameter_1,
                c.child_enemy_parameter_2,
                c.child_enemy_parameter_3,
                c.child_enemy_parameter_4,
                c.child_enemy_parameter_5
                FROM
                enemy_parameter a
                JOIN unit_skill_data AS b ON a.unit_id = b.unit_id
                JOIN unit_enemy_data AS d ON a.unit_id = d.unit_id
                LEFT JOIN enemy_m_parts c ON a.enemy_id = c.enemy_id
                WHERE a.enemy_id = $enemyId
                """,
            RawEnemy::class.java
        )
    }

    fun getStills() {

    }

    /***
     * 获取迷宫bossList
     * @param
     * @return
     */
    fun getDungeons(): List<RawDungeon>? {
        val count = getOne("""SELECT COUNT(*) 
                                FROM sqlite_master 
                                WHERE type='table' 
                                AND name='dungeon_area'""")
        if (!count.equals("1")) {
            return getBeanListByRaw(
                """
                SELECT
                a.dungeon_area_id,
                a.dungeon_name,
                a.description,
                b.*
                FROM
                dungeon_area_data AS a 
                JOIN wave_group_data AS b ON a.wave_group_id=b.wave_group_id 
                ORDER BY a.dungeon_area_id DESC 
                """,
                RawDungeon::class.java
            )
        }
        return getBeanListByRaw(
            """
                SELECT
                    a.dungeon_area_id 'dungeon_area_id',
                    a.dungeon_name 'dungeon_name',
                    a.description 'description',
                    sp.mode 'mode',
                    w.*
                FROM
                    dungeon_area AS a
                JOIN dungeon_quest_data AS b ON a.dungeon_area_id = b.dungeon_area_id
                AND b.quest_type = 4
                JOIN dungeon_special_battle AS sp ON b.quest_id = sp.quest_id
                JOIN wave_group_data AS w ON sp.wave_group_id = w.wave_group_id
                UNION ALL
                SELECT
                    a.dungeon_area_id,
                    a.dungeon_name,
                    a.description,
                    0 AS 'mode',
                    w.*
                FROM
                    dungeon_area AS a
                JOIN dungeon_quest_data AS b ON a.dungeon_area_id = b.dungeon_area_id
                AND b.quest_type = 3
                JOIN wave_group_data AS w ON b.wave_group_id = w.wave_group_id
                ORDER BY
                    a.dungeon_area_id DESC,
                    sp.mode DESC
                """,
            RawDungeon::class.java
        )
    }


    /***
     * SecretDungeonSchedules
     * @param
     * @return
     */
    fun getSecretDungeonPeriods(): List<RawSecretDungeonSchedule>? {
        val count = getOne("""SELECT COUNT(*) 
                                FROM sqlite_master 
                                WHERE type='table' 
                                AND name='secret_dungeon_schedule'""")
        if (!count.equals("1")) {
            return null
        }
        return getBeanListByRaw(
            """
                SELECT
                    a.dungeon_name 'dungeon_name',
                    a.description 'description',
					sch.*
                FROM dungeon_area AS a
				JOIN secret_dungeon_schedule AS sch ON a.dungeon_area_id = sch.dungeon_area_id
                ORDER BY
                    a.dungeon_area_id DESC
                """,
            RawSecretDungeonSchedule::class.java
        )
    }

    /***
     * SecretDungeonWaves
     * @param
     * @return
     */
    fun getSecretDungeons(dungeonAreaId: Int): List<RawSecretDungeon>? {
        val count = getOne("""SELECT COUNT(*) 
                                FROM sqlite_master 
                                WHERE type='table' 
                                AND name='secret_dungeon_quest_data'""")
        if (!count.equals("1")) {
            return null
        }
        return getBeanListByRaw(
            """
                SELECT
                    a.dungeon_area_id 'dungeon_area_id',
                    b.difficulty 'difficulty',
					b.floor_num 'floor_num',
                    w.*,
					sch.*
                FROM
                    dungeon_area AS a
                JOIN secret_dungeon_quest_data AS b ON a.dungeon_area_id = b.dungeon_area_id
                AND (b.quest_type = 5 or b.quest_type = 3)
                JOIN wave_group_data AS w ON b.wave_group_id = w.wave_group_id
				JOIN secret_dungeon_schedule AS sch ON a.dungeon_area_id = sch.dungeon_area_id
                WHERE a.dungeon_area_id = $dungeonAreaId
                ORDER BY
                    a.dungeon_area_id DESC,
                    b.difficulty DESC,
                    b.floor_num DESC
                """,
            RawSecretDungeon::class.java
        )
    }

    /***
     * Add Sekai Event Lists
     * @param
     * @return
     */
    fun getSekaiEvents(): List<RawSekaiEvent>? {
        return getBeanListByRaw(
            """
                SELECT
                a.sekai_id,
                b.name,
                b.description,
                b.boss_time_from,
                b.boss_time_to,
                m.sekai_enemy_id
                FROM sekai_schedule AS a,
                sekai_top_data AS b
                LEFT JOIN sekai_boss_mode AS m ON b.sekai_boss_mode_id=m.sekai_boss_mode_id
                WHERE a.sekai_id=b.sekai_id AND b.boss_hp_from <> 0 
                ORDER BY b.id asc
                """,
            //5
            RawSekaiEvent::class.java
        )
    }

    /***
     * 获取 Sekai Enemy
     * @param
     * @return
     */
    fun getSekaiEnemy(enemyIdList: List<Int>): List<RawEnemy>? {
        return getBeanListByRaw(
            """
                    SELECT 
                    a.* 
                    ,b.union_burst 
                    ,b.union_burst_evolution 
                    ,b.main_skill_1 
                    ,b.main_skill_evolution_1 
                    ,b.main_skill_2 
                    ,b.main_skill_evolution_2 
                    ,b.ex_skill_1 
                    ,b.ex_skill_evolution_1 
                    ,b.main_skill_3 
                    ,b.main_skill_4 
                    ,b.main_skill_5 
                    ,b.main_skill_6 
                    ,b.main_skill_7 
                    ,b.main_skill_8 
                    ,b.main_skill_9 
                    ,b.main_skill_10 
                    ,b.ex_skill_2 
                    ,b.ex_skill_evolution_2 
                    ,b.ex_skill_3 
                    ,b.ex_skill_evolution_3 
                    ,b.ex_skill_4 
                    ,b.ex_skill_evolution_4 
                    ,b.ex_skill_5 
                    ,b.sp_skill_1 
                    ,b.ex_skill_evolution_5 
                    ,b.sp_skill_2 
                    ,b.sp_skill_3 
                    ,b.sp_skill_4 
                    ,b.sp_skill_5 
                    ,c.child_enemy_parameter_1 
                    ,c.child_enemy_parameter_2 
                    ,c.child_enemy_parameter_3 
                    ,c.child_enemy_parameter_4 
                    ,c.child_enemy_parameter_5 
                    ,u.prefab_id 
                    ,u.atk_type 
                    ,u.normal_atk_cast_time
					,u.search_area_width
                    ,u.unit_name
                    FROM 
                    unit_skill_data b 
                    ,sekai_enemy_parameter a 
                    LEFT JOIN enemy_m_parts c ON a.sekai_enemy_id = c.enemy_id 
                    LEFT JOIN unit_enemy_data u ON a.unit_id = u.unit_id 
                    WHERE 
                    a.unit_id = b.unit_id 
                    AND a.sekai_enemy_id in ( %s )  
                    """.format(enemyIdList.toString()
                .replace("[", "")
                .replace("]", "")),
            RawEnemy::class.java
        )
    }

    /***
     * 获取第一个 Sekai enemy
     * @param
     * @return
     */
    fun getSekaiEnemy(enemyId: Int): RawEnemy? {
        return getSekaiEnemy(listOf(enemyId))?.get(0)
    }

    fun getSpecialEventCount(): Int {
        return getOne(
        """
            SELECT COUNT(*) 'num'
            FROM sqlite_master 
            WHERE type = 'table' 
            AND name in ('legion_quest_data', 'kaiser_quest_data')
            """
        ).let {
            it!!.toInt()
        }
    }

    fun getKaiserEvent(): List<RawSpecialEvent>? {
        return getBeanListByRaw(
            """
            SELECT
            a.kaiser_boss_id 'boss_id',
            * FROM kaiser_quest_data as a
			join wave_group_data as b on a.wave_group_id=b.wave_group_id
            WHERE map_type = 1
            """.trimIndent(),
        RawSpecialEvent::class.java)
    }

    fun getKaiserSpecialBossGuess(): Int {
        val event: List<RawSpecialEvent>? = getBeanListByRaw(
            """
            SELECT
            kaiser_boss_id 'boss_id',
            * FROM kaiser_quest_data
            WHERE map_type = 2
            """.trimIndent(),
            RawSpecialEvent::class.java)
        return if (event.isNullOrEmpty())
            0
        else
            event[0].boss_id
    }

    fun getKaiserSpecial(): List<RawSpecialEventBoss>? {
        return getBeanListByRaw(
            """
            SELECT
            * FROM kaiser_special_battle as a
			join wave_group_data as b on a.wave_group_id=b.wave_group_id
            """.trimIndent(),
            RawSpecialEventBoss::class.java)
    }

    fun getKaiserRestriction(group: Int): List<RawRestriction>? {
        return getBeanListByRaw(
            """
            SELECT * FROM kaiser_restriction_group
            WHERE restriction_group_id = $group
            """.trimIndent(),
            RawRestriction::class.java
        )
    }

    fun getLegionEvent(): List<RawSpecialEvent>? {
        return getBeanListByRaw(
            """
            SELECT
            a.legion_boss_id 'boss_id',
            * FROM legion_quest_data as a
			join wave_group_data as b on a.wave_group_id=b.wave_group_id
            WHERE map_type = 1
            """.trimIndent(),
            RawSpecialEvent::class.java
        )
    }

    fun getLegionSpecialBossGuess(): Int {
        val event: List<RawSpecialEvent>? = getBeanListByRaw(
            """
            SELECT
            legion_boss_id 'boss_id',
            * FROM legion_quest_data
            WHERE map_type = 2
            """.trimIndent(),
            RawSpecialEvent::class.java)
        return if (event.isNullOrEmpty())
            0
        else
            event[0].boss_id
    }

    fun getLegionSpecial(): List<RawSpecialEventBoss>? {
        return getBeanListByRaw(
            """
            SELECT
            * FROM legion_special_battle as a
			join wave_group_data as b on a.wave_group_id=b.wave_group_id
            """.trimIndent(),
            RawSpecialEventBoss::class.java)
    }

    fun getLegionEffect(bossId: Int): List<RawLegionEffectUnit>? {
        return getBeanListByRaw(
            """
            SELECT * FROM legion_effective_unit
            WHERE legion_boss_id = $bossId
            """.trimIndent(),
            RawLegionEffectUnit::class.java
        )
    }

    /***
     * Quest Area
     */
    fun getQuestAreas(): List<RawQuestArea>? {
        return getBeanListByRaw(
            """
                SELECT * FROM quest_area_data WHERE area_id < 14000
                """,
            RawQuestArea::class.java
        )
    }

    /***
     * 获取所有Quest
     */
    fun getQuests(): List<RawQuest>? {
        return getBeanListByRaw(
            """
                SELECT * FROM quest_data WHERE quest_id < 14000000 ORDER BY daily_limit ASC, quest_id DESC 
                """,
            RawQuest::class.java
        )
    }

    /***
     * 获取掉落奖励
     */
    fun getEnemyRewardData(dropRewardIdList: List<Int>): List<RawEnemyRewardData>? {
        return getBeanListByRaw(
            """
                SELECT * 
                FROM enemy_reward_data 
                WHERE drop_reward_id IN ( %s ) 
                """.format(dropRewardIdList.toString()
                .replace("[", "")
                .replace("]", "")),
            RawEnemyRewardData::class.java
        )
    }

    /***
     * 获取campaign日程
     */
    fun getCampaignSchedule(nowTimeString: String?): List<RawScheduleCampaign>? {
        var sqlString = " SELECT * FROM campaign_schedule WHERE id < 5000"
        nowTimeString?.let {
            sqlString += " AND end_time > '$it' "
        }
        return getBeanListByRaw(sqlString, RawScheduleCampaign::class.java)
    }

    /***
     * 获取free gacha日程
     */
    fun getFreeGachaSchedule(nowTimeString: String?): List<RawScheduleFreeGacha>? {
        var sqlString = " SELECT * FROM campaign_freegacha "
        nowTimeString?.let {
            sqlString += " WHERE end_time > '$it' "
        }
        return getBeanListByRaw(sqlString, RawScheduleFreeGacha::class.java)
    }

    fun getGachaSchedule(nowTimeString: String?): List<RawGachaData>? {
        var sqlString = """ 
            SELECT * FROM gacha_data WHERE gacha_id > 30000 AND gacha_id NOT BETWEEN 60000 AND 70000 AND gacha_id NOT BETWEEN 90000 AND 100000 AND gacha_id NOT BETWEEN 120000 AND 130000 
            """
        nowTimeString?.let {
            sqlString += " AND end_time > '$it' "
        }
        return getBeanListByRaw(sqlString, RawGachaData::class.java)
    }

    fun getGachaExchangeLineup(exchangeId: Int): List<RawGachaExchangeLineup>? {
        val sqlString = """
            SELECT *
            FROM gacha_exchange_lineup
            WHERE exchange_id = $exchangeId
        """
        return getBeanListByRaw(sqlString, RawGachaExchangeLineup::class.java)
    }

    /***
     * 获取露娜塔日程
     */
    fun getSecretDungeonSchedule(nowTimeString: String?): List<RawSecretDungeonSchedule>? {
        var sqlString = " SELECT * FROM secret_dungeon_schedule "
        nowTimeString?.let {
            sqlString += " WHERE end_time > '$it' "
        }
        return getBeanListByRaw(sqlString, RawSecretDungeonSchedule::class.java)
    }

    /***
     * 获取hatsune日程
     */
    fun getHatsuneSchedule(nowTimeString: String?): List<RawScheduleHatsune>? {
        var sqlString = """
            SELECT a.event_id, a.teaser_time, a.start_time, a.end_time, b.title 
            FROM hatsune_schedule AS a JOIN event_story_data AS b ON a.event_id = b.value
            """
        nowTimeString?.let {
            sqlString += " WHERE a.end_time > '$it' "
        }
        sqlString += " ORDER BY a.event_id DESC "
        return getBeanListByRaw(sqlString, RawScheduleHatsune::class.java)
    }

    /***
     * 获取hatsune一般boss数据
     */
    fun getHatsuneBattle(eventId: Int): List<RawHatsuneBoss>? {
        return getBeanListByRaw(
            """
            SELECT
            a.*
            FROM
            hatsune_boss a
            WHERE
            event_id = $eventId 
            AND area_id <> 0 
            """,
            RawHatsuneBoss::class.java
        )
    }

    /***
     * 获取hatsune SP boss数据
     */
    fun getHatsuneSP(eventId: Int): List<RawHatsuneSpecialBattle>? {
        return getBeanListByRaw(
            """
            SELECT
            a.*
            FROM hatsune_special_battle a
            WHERE event_id = $eventId
            """,
            RawHatsuneSpecialBattle::class.java
        )
    }

    /***
     * 获取hatsune Item数据
     */
    fun getHatsuneItem(eventId: Int): List<RawHatsuneItem>? {
        return getBeanListByRaw(
            """
            SELECT
            a.*
            FROM hatsune_item a
            WHERE event_id = $eventId
            """,
            RawHatsuneItem::class.java
        )
    }

    fun getSrtPanel(): List<RawSrtPanel>? {
        return getBeanListByRaw(
            """
            SELECT * FROM srt_panel ORDER BY panel_id, reading_id ASC
            """,
            RawSrtPanel::class.java
        )
    }

    /***
     * 获取露娜塔日程
     */
    fun getTowerSchedule(nowTimeString: String?): List<RawTowerSchedule>? {
        var sqlString = " SELECT * FROM tower_schedule "
        nowTimeString?.let {
            sqlString += " WHERE end_time > '$it' "
        }
        return getBeanListByRaw(sqlString, RawTowerSchedule::class.java)
    }

    /***
     * Get Tower Area
     */
    fun getTowerArea(): List<RawTowerArea>? {
        return getBeanListByRaw(
            """
            SELECT
            a.tower_area_id,
            a.max_floor_num,
            a.cloister_quest_id,
			b.tower_ex_quest_id,
			b.wave_group_id,
			c.start_time
            FROM
            tower_area_data a,
			tower_ex_quest_data b
			LEFT JOIN tower_schedule c ON a.tower_area_id = c.max_tower_area_id
			WHERE a.tower_area_id = b.tower_area_id
            """,
            RawTowerArea::class.java
        )
    }

    /***
     * Get Tower Area
     */
    fun getTowerWave(waveGroupList: List<Int>): List<RawTowerWave>? {
        return getBeanListByRaw(
            """
            SELECT
            a.*
            FROM
            tower_wave_group_data a
            WHERE wave_group_id IN ( %s ) 
            """.format(waveGroupList.toString()
                .replace("[", "")
                .replace("]", "")),
            RawTowerWave::class.java
        )
    }

    /***
     * Tower Enemy List
     * @param
     * @return
     */
    fun getTowerEnemy(enemyIdList: List<Int>): List<RawEnemy>? {
        return getBeanListByRaw(
            """
                    SELECT 
                    a.* 
                    ,b.union_burst 
                    ,b.union_burst_evolution 
                    ,b.main_skill_1 
                    ,b.main_skill_evolution_1 
                    ,b.main_skill_2 
                    ,b.main_skill_evolution_2 
                    ,b.ex_skill_1 
                    ,b.ex_skill_evolution_1 
                    ,b.main_skill_3 
                    ,b.main_skill_4 
                    ,b.main_skill_5 
                    ,b.main_skill_6 
                    ,b.main_skill_7 
                    ,b.main_skill_8 
                    ,b.main_skill_9 
                    ,b.main_skill_10 
                    ,b.ex_skill_2 
                    ,b.ex_skill_evolution_2 
                    ,b.ex_skill_3 
                    ,b.ex_skill_evolution_3 
                    ,b.ex_skill_4 
                    ,b.ex_skill_evolution_4 
                    ,b.ex_skill_5 
                    ,b.sp_skill_1 
                    ,b.ex_skill_evolution_5 
                    ,b.sp_skill_2 
                    ,b.sp_skill_3 
                    ,b.sp_skill_4 
                    ,b.sp_skill_5 
                    ,c.child_enemy_parameter_1 
                    ,c.child_enemy_parameter_2 
                    ,c.child_enemy_parameter_3 
                    ,c.child_enemy_parameter_4 
                    ,c.child_enemy_parameter_5 
                    ,u.prefab_id 
                    ,u.atk_type 
                    ,u.normal_atk_cast_time
					,u.search_area_width
                    ,u.comment
                    ,u.unit_name
                    FROM 
                    unit_skill_data b 
                    ,tower_enemy_parameter a 
                    LEFT JOIN enemy_m_parts c ON a.enemy_id = c.enemy_id 
                    LEFT JOIN unit_enemy_data u ON a.unit_id = u.unit_id 
                    WHERE 
                    a.unit_id = b.unit_id 
                    AND a.enemy_id in ( %s )  
                    """.format(enemyIdList.toString()
                .replace("[", "")
                .replace("]", "")),
            RawEnemy::class.java
        )
    }

    /***
     * Get Tower Boss
     */

    fun getTowerEnemyBoss(start: Int, end: Int): Int {
        return getOne(
            """
                SELECT
                a.unit_id
                FROM tower_enemy_parameter a
                WHERE a.enemy_id >= $start AND a.enemy_id < $end
                ORDER BY a.hp DESC LIMIT 1
                """)?.toInt() ?: 0
    }

    /***
     * Get Tower Cloister
     */
    fun getTowerCloister(id: Int): RawTowerCloister? {
        return getBeanByRaw(
            """
            SELECT
            a.*
            FROM
            tower_cloister_quest_data a
			WHERE a.tower_cloister_quest_id = $id
            """,
            RawTowerCloister::class.java
        )
    }


    /***
     * 获取装备碎片
     */
    fun getEquipmentPiece(): List<RawEquipmentPiece>? {
        return getBeanListByRaw(
            """
            SELECT 
            a.*, 
            b.equipment_id 'crafted'
            FROM equipment_data as a
            LEFT JOIN equipment_craft as b on a.equipment_id = b.condition_equipment_id_1 
            WHERE a.equipment_id >= 113000
            """,
            RawEquipmentPiece::class.java
        )
    }

    fun getEquipmentPiece(pieceId: Int): RawEquipmentPiece? {
        return getBeanByRaw("""
            SELECT 
            a.*, 
            b.equipment_id 'crafted'
            FROM equipment_data as a
            LEFT JOIN equipment_craft as b on a.equipment_id = b.condition_equipment_id_1 
            WHERE a.equipment_id = $pieceId
            """,
            RawEquipmentPiece::class.java
        )
    }

    fun getUnitCoefficient(): RawUnitCoefficient? {
        return getBeanByRaw("SELECT * FROM unit_status_coefficient",
            RawUnitCoefficient::class.java
        )
    }

    /***
     * 获取异常状态map
     * @param
     * @return
     */
    val ailmentMap: Map<Int, String>?
        get() = getIntStringMap(
            "SELECT * FROM ailment_data ",
            "ailment_id",
            "ailment_name"
        )

    val maxCharaLevel: Int
        get() {
            return try {
                if (UserSettings.get().exceedMaxLevels)
                    getOne("SELECT max(team_level) FROM experience_team")?.toInt()?.plus(10) ?: 0
                else
                    getOne("SELECT max(team_level) FROM experience_team")?.toInt() ?: 0
            }
            catch (e: Exception) {
                0
            }
        }

    val maxCharaRank: Int
        get() {
            return try {
                getOne("SELECT max(promotion_level) FROM unit_promotion")?.toInt() ?: 0
            }
            catch (e: Exception) {
                0
            }
        }

    val maxUniqueEquipmentLevel: Int
        get() {
            return try {
                if (UserSettings.get().exceedMaxLevels && UserSettings.get().getUserServer() != "jp")
                    maxCharaContentsLevel / 10 * 10
                else
                    getOne("SELECT max(enhance_level) FROM unique_equipment_enhance_data")?.toInt() ?: 0
            }
            catch (e: Exception) {
                0
            }
        }

    val maxEnemyLevel: Int
        get() {
            return try {
                    getOne("SELECT MAX(level) FROM enemy_parameter")?.toInt() ?: 0
                }
                catch (e: Exception) {
                    0
                }
        }

    fun maxUnitRarity(unitId: Int): Int {
        val result = getOne("SELECT max(rarity) FROM unit_rarity WHERE unit_id=$unitId")
        return result?.toInt() ?: 5
    }

    private fun area2Level(area: Int): Int {
        // ref: 8 - 7/5(80) | 9 - 8/3(85) | 10 - 8/5(90) | 11 - 9/3(95) | 12 - 9/5(100) | 13 - 10/3(102)...
        // ref: 22 - 13/3(127) | 23 - 13/4(130) | 24 - 13/5(133) | 25 - 14/3(136)
        return when (area) {
            in 0..8 -> 80
            in 9..12 -> 40 + area * 5
            in 13..14 -> 63 + area * 3
            in 15..16 -> 62 + area * 3
            in 17..57 -> 61 + area * 3
            else -> if (!isAreaEnd(area)) {
                area * 6 - 113
            } else {
                area * 6 - 110
            }
        }
    }

    private fun area2Rank(area: Int): Int {
        // ref: 8 - 7/5(80) | 9 - 8/3(85) | 10 - 8/5(90) | 11 - 9/3(95) | 12 - 9/5(100) | 13 - 10/3(102)...
        // ref: 22 - 13/3(127) | 23 - 13/4(130) | 24 - 13/5(133) | 25 - 14/3(136)
        val areaEndValue = if (!isAreaEnd(area))
            -1
        else
            0
        return when (area) {
            in 0..8 -> 7
            in 9..12 -> ceil(area.toDouble() / 2.0).toInt() + 3
            in 13..57 -> ceil(area.toDouble() / 3.0).toInt() + 5
            else -> ceil((area.toDouble() * 2 + areaEndValue) / 3.0).toInt() - 14
        }
    }

    private fun area2Equipment(area: Int): Int {
        // ref: 8 - 7/5(80) | 9 - 8/3(85) | 10 - 8/5(90) | 11 - 9/3(95) | 12 - 9/5(100) | 13 - 10/3(102)...
        // ref: 22 - 13/3(127) | 23 - 13/4(130) | 24 - 13/5(133) | 25 - 14/3(136)
        val areaEndValue = if (!isAreaEnd(area))
            1
        else
            2
        return when (area) {
            in 0..8 -> 5
            in 9..12 -> (area + 1).rem(2).times(2) + 3
            in 13..57 -> (area + 2).rem(3) + 3
            else -> (area * 2 + areaEndValue).rem(3) + 3
        }
    }

    val areaTimeMap: Map<Int, String>
        get() {
            val formatter = DateTimeFormatter.ofPattern(I18N.getString(R.string.db_date_format))
            var sqlString = "SELECT substr(area_id, 4, 2) 'area', start_time FROM quest_area_data "
            sqlString += "WHERE area_id < 12000 AND start_time > '" + LocalDateTime.now().format(formatter) + "' "
            sqlString += "AND start_time < '2040/12/31 0:00:00'" // for error handling of chinese server
            sqlString += "ORDER BY area ASC"

            val sqlMap = mutableMapOf<Int, String>()
            sqlMap[maxCharaContentArea] = LocalDateTime.now().format(formatter)
            try {
                getIntStringMap(sqlString, "area", "start_time")?.let { sqlMap.putAll(it) }
            } catch (e: Exception) {
                //
            }

            return sqlMap.toMap()
        }

    val areaLevelMap: Map<Int, Int>
        get() {
            val areaToLevel = mutableMapOf<Int, Int>()
            for (i in maxCharaContentArea..maxArea) {
                areaToLevel[i] = area2Level(i)
            }
            return areaToLevel.toMap()
        }

    val areaRankMap: Map<Int, Int>
        get() {
            val areaToRank = mutableMapOf<Int, Int>()
            for (i in maxCharaContentArea..maxArea) {
                areaToRank[i] = area2Rank(i)
            }
            return areaToRank.toMap()
        }

    val areaEquipmentMap: Map<Int, Int>
        get() {
            val areaToEquipmentNumber = mutableMapOf<Int, Int>()
            for (i in maxCharaContentArea..maxArea) {
                areaToEquipmentNumber[i] = area2Equipment(i)
            }
            return areaToEquipmentNumber.toMap()
        }

    val maxArea: Int
        get() {
            var sqlString = "SELECT max(area_id) FROM quest_area_data WHERE area_id < 12000 "
            sqlString += "AND start_time < '2040/12/31 0:00:00'" // for error handling of chinese server

            return try {
                getOne(sqlString)?.toInt()?.rem(100) ?: 0
                }
                catch (e: Exception) {
                    0
                }
        }

    private fun isAreaEnd(area: Int): Boolean {
        return getOne("""
            SELECT count(*) 
            FROM quest_condition_data 
            WHERE condition_quest_id_1 = 99999999 
            and quest_id / 1000 = 11000+$area
        """).equals("0")
    }

    val maxCharaContentArea: Int
        get() {
            val formatter = DateTimeFormatter.ofPattern(I18N.getString(R.string.db_date_format))
            var sqlString = "SELECT max(area_id) FROM quest_area_data "
            sqlString += "WHERE area_id < 12000 AND start_time < '" + LocalDateTime.now()
                .format(formatter) + "'"

            return try {
                    getOne(sqlString)?.toInt()?.rem(100) ?: 0
                }
                catch (e: Exception) {
                    0
                }
        }

    val maxCharaContentsLevel: Int
        get() {
            return try {
                if (UserSettings.get().exceedMaxLevels && UserSettings.get().getUserServer() != "jp")
                    area2Level(maxCharaContentArea) + 36
                else
                    area2Level(maxCharaContentArea) + 10
            } catch (e:Exception) {
                maxCharaLevel
            }
        }

    val maxCharaContentsRank: Int
        get() {
            return try {
                area2Rank(maxCharaContentArea)
            } catch (e:Exception) {
                maxCharaRank
            }
        }

    val maxRank: Int
        get() {
            return area2Rank(maxArea)
        }

    val maxCharaContentsEquipment: Int
        get() {
            return try {
                area2Equipment(maxCharaContentArea)
            } catch (e: Exception) {
                3
            }
        }

    /***
     * 随机生成16位随机英数字符串
     * @return
     */
    val randomId: String
        get() {
            val str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val random = Random()
            val sb = StringBuffer()
            for (i in 0..15) {
                val number = random.nextInt(36)
                sb.append(str[number])
            }
            return sb.toString()
        }
}