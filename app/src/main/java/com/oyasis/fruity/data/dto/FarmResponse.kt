package com.oyasis.fruity.data.dto

import com.oyasis.fruity.data.model.Farm


data class FarmResponse(
        var ownerId: String,
        var farmId: String,
        var farmTitle: String,
        var farmLat: Double,
        var farmLong: Double)


fun FarmResponse?.mapToFarm(): Farm? {
    if(this == null) return null
    return Farm(
            ownerId,
            farmId,
            farmTitle,
            farmLat,
            farmLong
    )
}
