package com.fieldflow.core.network.dto

import com.google.gson.annotations.SerializedName

data class CategoriesResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: CategoriesData
)

data class CategoriesData(
    @SerializedName("groups") val groups: List<CategoryGroupDto>
)

data class CategoryGroupDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("display") val display: String,
    @SerializedName("color") val color: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("categories") val categories: List<CategoryDto>
)

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("display") val display: String
)