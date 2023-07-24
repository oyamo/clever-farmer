package com.oyasis.fruity.data.dto

import com.oyasis.fruity.data.model.Farm

data class AddFarmRequest (        var ownerId: String,
                                   var farmId: String,
                                   var farmTitle: String,
                                   var farmLat: Double,
                                   var farmLong: Double)


fun AddFarmRequest.mapToFarm() : Farm {
    return Farm(
            ownerId, farmId, farmTitle, farmLat, farmLong
    )
}