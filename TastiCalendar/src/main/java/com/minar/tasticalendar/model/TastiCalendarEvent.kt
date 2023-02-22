package com.minar.tasticalendar.model

import java.time.LocalDate

// Simple data class to model an event with an optional "caption"
data class TastiCalendarEvent(
    val date: LocalDate,
    val displayText: String? = ""
)
