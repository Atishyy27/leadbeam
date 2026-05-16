package com.fieldflow.core.network.model

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class NearbyResponseDeserializer : JsonDeserializer<NearbyResponse> {
    override fun deserialize(
        json: JsonElement, 
        typeOfT: Type, 
        context: com.google.gson.JsonDeserializationContext
    ): NearbyResponse {
        val obj = json.asJsonObject
        val type = obj.get("type")?.asString ?: throw JsonParseException("Missing type")

        return when (type) {
            "individual" -> {
                val listType = object : TypeToken<List<BusinessDto>>() {}.type
                val businesses: List<BusinessDto> = context.deserialize(obj.get("businesses"), listType)
                NearbyResponse.Individual(businesses)
            }
            "clustered" -> {
                val listType = object : TypeToken<List<ClusterDto>>() {}.type
                val clusters: List<ClusterDto> = context.deserialize(obj.get("clusters"), listType)
                NearbyResponse.Clustered(clusters)
            }
            else -> throw JsonParseException("Unknown polymorphic type: $type")
        }
    }
}