package com.oyasis.fruity.data.dto

import com.oyasis.fruity.data.model.Incident

data class AddIncidenceRequest(
        var incidenceId: String,
        var confidence: Double,
        var healthy: Boolean,
        var title: String
)


fun AddIncidenceRequest.mapToIncident(): Incident
    = Incident(incidenceId, confidence, healthy, title)