package com.github.malitsplus.shizurunotes.db

class DBExtensionRepository(private val actionPrefabDao: ActionPrefabDao) {

    fun getActionPrefab(actionId: Int): List<ActionPrefab> {
        return actionPrefabDao.getActionPrefab(actionId).toList()
    }

    //@Suppress("RedundantSuspendModifier")
    //@WorkerThread
    //suspend
    fun insertActionPrefab(actionPrefab: ActionPrefab) {
        actionPrefabDao.insertActionPrefab(actionPrefab)
    }

    //@Suppress("RedundantSuspendModifier")
    //@WorkerThread
    //suspend
    fun insertActionPrefabs(actionPrefabs: List<ActionPrefab>) {
        actionPrefabDao.insertAllActionPrefabs(actionPrefabs)
    }
}