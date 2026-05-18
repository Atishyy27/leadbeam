// feature/business/src/main/java/com/fieldflow/feature/business/data/mapper/BusinessMappers.kt
package com.fieldflow.feature.business.data.mapper

import com.fieldflow.core.database.entity.BusinessEntity
import com.fieldflow.core.network.dto.BusinessNearbyDto
import com.fieldflow.core.network.dto.BusinessDetailDto
import com.fieldflow.feature.business.domain.model.BusinessDetail
import com.google.gson.Gson

private val gson = Gson()

fun BusinessDetailDto.toEntity(existingEntity: BusinessEntity? = null): BusinessEntity {
    return BusinessEntity(
        leadbeamId = leadbeamId,
        name = name,
        lat = lat,
        long = long,
        latGrid = (lat * 10).toInt(),
        longGrid = (long * 10).toInt(),
        addressFull = addressFull,
        city = city,
        state = state,
        postalCode = postalCode,
        phonePrimary = phonePrimary,
        email = email,
        website = website,
        category = categoryPrimary,
        categoryGroup = categoryGroup,
        categoryDisplay = categoryDisplay,
        categoryGroupDisplay = categoryGroupDisplay,
        categoryGroupColor = categoryGroupColor,
        isChain = isChain,
        chainName = chainName,
        operatingStatus = operatingStatus,
        operatingHours = operatingHours?.let { gson.toJson(it) },
        rating = rating,
        reviewsCount = reviewsCount,
        overallConfidence = overallConfidence,
        dataCompleteness = dataCompleteness,
        sourceCount = sourceCount,
        socialProfiles = socialProfiles?.let { gson.toJson(it) },
        enrichmentData = enrichmentData?.let { gson.toJson(it) },
        cachedAt = System.currentTimeMillis(),
        isFavorite = existingEntity?.isFavorite ?: false,
        isHidden = existingEntity?.isHidden ?: false,
        detailsCachedAt = System.currentTimeMillis(),
        description = description,
        hoursNotes = hoursNotes,
        parkingInfo = parkingInfo,
        accessibility = accessibility,
        paymentMethods = paymentMethods?.let { gson.toJson(it) },
        photos = photos?.let { gson.toJson(it) },
        lastVerified = lastVerified
    )
}

fun BusinessEntity.toDomain(): BusinessDetail {
    return BusinessDetail(
        leadbeamId = leadbeamId,
        name = name,
        lat = lat,
        long = long,
        addressFull = addressFull,
        city = city,
        state = state,
        postalCode = postalCode,
        phonePrimary = phonePrimary,
        email = email,
        website = website,
        categoryPrimary = category,
        categoryGroup = categoryGroup,
        categoryDisplay = categoryDisplay,
        categoryGroupDisplay = categoryGroupDisplay,
        categoryGroupColor = categoryGroupColor,
        isChain = isChain,
        chainName = chainName,
        operatingStatus = operatingStatus,
        operatingHours = operatingHours,
        hoursNotes = hoursNotes,
        rating = rating,
        reviewsCount = reviewsCount,
        overallConfidence = overallConfidence,
        dataCompleteness = dataCompleteness,
        sourceCount = sourceCount,
        socialProfiles = socialProfiles,
        enrichmentData = enrichmentData,
        description = description,
        parkingInfo = parkingInfo,
        accessibility = accessibility,
        paymentMethods = paymentMethods,
        photos = photos,
        lastVerified = lastVerified,
        isFavorite = isFavorite,
        isHidden = isHidden
    )
}

fun BusinessNearbyDto.toEntity(existingEntity: BusinessEntity? = null): BusinessEntity {
    return BusinessEntity(
        leadbeamId = leadbeamId,
        name = name,
        lat = lat,
        long = long,
        latGrid = (lat * 10).toInt(),
        longGrid = (long * 10).toInt(),
        addressFull = addressFull,
        city = city,
        state = state,
        postalCode = existingEntity?.postalCode,
        phonePrimary = existingEntity?.phonePrimary,
        email = existingEntity?.email,
        website = existingEntity?.website,
        category = categoryPrimary,
        categoryGroup = categoryGroup,
        categoryDisplay = categoryDisplay,
        categoryGroupDisplay = existingEntity?.categoryGroupDisplay ?: "",
        categoryGroupColor = categoryGroupColor,
        isChain = isChain,
        chainName = existingEntity?.chainName,
        operatingStatus = existingEntity?.operatingStatus ?: "Unknown",
        operatingHours = existingEntity?.operatingHours,
        rating = rating,
        reviewsCount = existingEntity?.reviewsCount,
        overallConfidence = overallConfidence,
        dataCompleteness = existingEntity?.dataCompleteness ?: 0.0,
        sourceCount = existingEntity?.sourceCount ?: 0,
        socialProfiles = existingEntity?.socialProfiles,
        enrichmentData = existingEntity?.enrichmentData,
        cachedAt = System.currentTimeMillis(),
        isFavorite = existingEntity?.isFavorite ?: false,
        isHidden = existingEntity?.isHidden ?: false,
        detailsCachedAt = existingEntity?.detailsCachedAt,
        description = existingEntity?.description,
        hoursNotes = existingEntity?.hoursNotes,
        parkingInfo = existingEntity?.parkingInfo,
        accessibility = existingEntity?.accessibility,
        paymentMethods = existingEntity?.paymentMethods,
        photos = existingEntity?.photos,
        lastVerified = existingEntity?.lastVerified,
        lastFetched = System.currentTimeMillis()
    )
}