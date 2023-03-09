package com.minar.tasticalendar.utilities

import com.minar.tasticalendar.model.TastiCalendarEvent

// Given a list of events, cumulate their texts in a single string
fun formatEventList(events: List<TastiCalendarEvent>, separator: String? = ","): String {
    val sb = StringBuilder()
    for (event in events) {
        if (!event.displayText.isNullOrBlank() && events.lastIndex != events.indexOf(event))
            sb.append("${event.displayText}${separator} ")
        if (!event.displayText.isNullOrBlank() && events.lastIndex == events.indexOf(event))
            sb.append(event.displayText)
    }
    return sb.toString().trim()
}