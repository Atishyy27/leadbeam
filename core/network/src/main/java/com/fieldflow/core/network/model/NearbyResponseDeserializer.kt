package com.fieldflow.core.network.model

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import com.fieldflow.core.network.dto.NearbyResponse
import com.fieldflow.core.network.dto.BusinessNearbyDto

class NearbyResponseDeserializer : JsonDeserializer<NearbyResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: com.google.gson.JsonDeserializationContext
    ): NearbyResponse {
        val obj = json.asJsonObject
        
        // Safeguard tracking to pull the array block from root or nested structures
        val businessesElement = when {
            obj.has("businesses") -> obj.get("businesses")
            obj.has("data") && obj.getAsJsonObject("data").has("businesses") -> 
                obj.getAsJsonObject("data").get("businesses")
            else -> null
        }

        val listType = object : TypeToken<List<BusinessNearbyDto>>() {}.type
        val businessesList = if (businessesElement != null && !businessesElement.isJsonNull) {
            context.deserialize<List<BusinessNearbyDto>>(businessesElement, listType) ?: emptyList()
        } else {
            emptyList()
        }

        return NearbyResponse(
            businesses = businessesList,
            totalCount = obj.get("total_count")?.asInt ?: businessesList.size,
            bounds = null
        )
    }
}