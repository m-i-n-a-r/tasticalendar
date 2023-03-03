package com.minar.tasticalendar.model

import java.time.LocalDate

/**
 * A simple class to wrap a date and a label/description for the date.
 * <p>
 * It is used to populate the yearly or monthly calendar, and to display an optional label
 * when the day is pressed.
 * @param date LocalDate, the complete date of the event, it can't be null.
 * @param displayText the text associated with the date. If null, it will be
 * initialized to an empty string.
 */
data class TastiCalendarEvent(
    val date: LocalDate,
    val displayText: String? = ""
)
