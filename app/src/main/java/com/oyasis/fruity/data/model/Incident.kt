package com.oyasis.fruity.data.model

data class Incident(
        var incidenceId: String,
        var confidence: Double,
        var healthy: Boolean,
        var title: String
)