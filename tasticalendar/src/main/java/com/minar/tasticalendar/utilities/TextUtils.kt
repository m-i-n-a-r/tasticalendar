package com.minar.tasticalendar.utilities

import com.minar.tasticalendar.model.TastiCalendarEvent

// Given a list of events, cumulate in a single string their texts
fun formatEventList(events: List<TastiCalendarEvent>, separator: String? = ","): String {
    val sb = StringBuilder()
    for (event in events) {
        if (!event.displayText.isNullOrBlank()) sb.append("${event.displayText}${separator} ")
    }
    return sb.toString().trim()
}