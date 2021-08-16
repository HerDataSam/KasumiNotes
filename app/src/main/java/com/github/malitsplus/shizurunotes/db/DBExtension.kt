package com.github.malitsplus.shizurunotes.db

import android.content.Context
import androidx.room.*

@Dao
interface ActionPrefabDao {
    @Query("select * from action_prefab WHERE action_id = :actionId")
    fun getActionPrefab(actionId: Int): Array<ActionPrefab>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActionPrefab(vararg prefab: ActionPrefab)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllActionPrefabs(prefabs: List<ActionPrefab>)
}

@Entity(tableName = "action_prefab", primaryKeys = ["action_id", "count"])
data class ActionPrefab(
    @ColumnInfo(name = "action_id") var actionId: Int,
    var count: Int,
    var time: Double,
    @ColumnInfo(name = "damage_num_type") var damageNumType: Int,
    var weight: Double,
    @ColumnInfo(name = "damage_num_scale") var DamageNumScale: Double
)

@Dao
interface DatabaseInfoDao {

}

@Database(entities = [ActionPrefab::class], version = 1, exportSchema = false)
abstract class ExtensionDB:RoomDatabase() {
    abstract fun actionPrefabDao(): ActionPrefabDao

    companion object {
        @Volatile
        private var INSTANCE: ExtensionDB? = null

        fun getDB(context: Context): ExtensionDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExtensionDB::class.java,
                    "extension.db"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }

}

