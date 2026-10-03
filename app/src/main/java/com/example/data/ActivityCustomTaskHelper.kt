package com.example.data

import org.json.JSONArray
import org.json.JSONObject

object ActivityCustomTaskHelper {

    fun createEmpty20Slots(): List<ActivityCustomTask> {
        return (1..20).map {
            ActivityCustomTask(
                slotIndex = it,
                title = "",
                isActive = false,
                diamondReward = 5
            )
        }
    }

    fun parseJson(json: String?): List<ActivityCustomTask> {
        val list = createEmpty20Slots().toMutableList()
        if (json.isNullOrBlank()) return list

        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val slot = obj.optInt("slotIndex", i + 1)
                val title = obj.optString("title", "")
                val isActive = obj.optBoolean("isActive", false)
                val reward = obj.optInt("diamondReward", 5)
                val targetIndex = (slot - 1).coerceIn(0, 19)
                list[targetIndex] = ActivityCustomTask(
                    slotIndex = slot,
                    title = title,
                    isActive = isActive,
                    diamondReward = reward
                )
            }
        } catch (_: Exception) {}

        return list
    }

    fun toJson(list: List<ActivityCustomTask>): String {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("slotIndex", item.slotIndex)
                put("title", item.title)
                put("isActive", item.isActive)
                put("diamondReward", item.diamondReward)
            }
            array.put(obj)
        }
        return array.toString()
    }
}
