package com.oyasis.fruity.data.dto

import com.oyasis.fruity.data.model.Incident

class IncidenceResponse (
    var incidenceId: String,
    var confidence: Double,
    var healthy: Boolean,
    var title: String
)


fun IncidenceResponse.mapToIncident(): Incident {
    return Incident(incidenceId, confidence, healthy, title)
}