package com.minar.tasticalendar.core

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.minar.tasticalendar.R
import com.minar.tasticalendar.databinding.TasticalendarMonthBinding
import com.minar.tasticalendar.model.TastiCalendarEvent
import com.minar.tasticalendar.model.TcSundayHighlight
import com.minar.tasticalendar.utilities.formatEventList
import com.minar.tasticalendar.utilities.getBestContrast
import com.minar.tasticalendar.utilities.getThemeColor
import com.minar.tasticalendar.utilities.showSnackbar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*

/**
 * A class representing a month.
 * <p>
 * It can be used alone, or in combination with TastiCalendarYear to
 * render a yearly calendar. Many properties are customizable.
 * @see TastiCalendarYear
 * @param context the context of the view.
 * @param attrs the set of attributes specified in the layout.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class TastiCalendarMonth(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    // Custom attributes
    private var month = 0
    private var hideWeekDays: Boolean
    private var sundayAppearance = 0
    private var sundayFirst: Boolean
    private var showSnackBars: Boolean
    private var appearance = 0

    // Other useful variables
    private var dateWithChosenMonth: LocalDate
    private lateinit var cellsList: MutableList<TextView>
    private lateinit var weekDaysList: MutableList<TextView>
    private lateinit var monthTitle: TextView
    private var binding: TasticalendarMonthBinding
    private var eventCount = 0
    private var snackBarsPluralFormatting = false
    private var snackBarsPrefix: Int? = null
    private var snackBarsBaseView: View? = null
    private var snackBarsDuration: Int = 3000

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.TastiCalendarMonth, 0, 0
        ).apply {
            try {
                month = getInteger(R.styleable.TastiCalendarMonth_tcMonth, 0)
                hideWeekDays = getBoolean(R.styleable.TastiCalendarMonth_tcHideWeekDays, false)
                sundayAppearance = getInteger(R.styleable.TastiCalendarMonth_tcSundayHighlight, 0)
                sundayFirst = getBoolean(R.styleable.TastiCalendarMonth_tcSundayFirst, false)
                showSnackBars = getBoolean(R.styleable.TastiCalendarMonth_tcShowInfoSnackBars, true)
                appearance = getInteger(R.styleable.TastiCalendarMonth_tcAppearance, 0)
            } finally {
                recycle()
            }
        }
        binding = TasticalendarMonthBinding.inflate(LayoutInflater.from(context), this, true)
        dateWithChosenMonth = LocalDate.now().withMonth(month + 1).withDayOfMonth(1)
        initMonth()
        renderMonth(dateWithChosenMonth)
    }

    /**
     * Initializes the layout for the month, assigning the bindings and the visibilities.
     * <p>
     * This is used once when the layout is first created.
     */
    private fun initMonth() {
        // Week days
        val weekDayOne = binding.tastiCalendarWeekDayOne
        val weekDayTwo = binding.tastiCalendarWeekDayTwo
        val weekDayThree = binding.tastiCalendarWeekDayThree
        val weekDayFour = binding.tastiCalendarWeekDayFour
        val weekDayFive = binding.tastiCalendarWeekDayFive
        val weekDaySix = binding.tastiCalendarWeekDaySix
        val weekDaySeven = binding.tastiCalendarWeekDaySeven
        weekDaysList = mutableListOf(
            weekDayOne, weekDayTwo, weekDayThree, weekDayFour, weekDayFive, weekDaySix, weekDaySeven
        )

        // Month cells
        val cell1 = binding.tastiCalendarCell1
        val cell2 = binding.tastiCalendarCell2
        val cell3 = binding.tastiCalendarCell3
        val cell4 = binding.tastiCalendarCell4
        val cell5 = binding.tastiCalendarCell5
        val cell6 = binding.tastiCalendarCell6
        val cell7 = binding.tastiCalendarCell7
        val cell8 = binding.tastiCalendarCell8
        val cell9 = binding.tastiCalendarCell9
        val cell10 = binding.tastiCalendarCell10
        val cell11 = binding.tastiCalendarCell11
        val cell12 = binding.tastiCalendarCell12
        val cell13 = binding.tastiCalendarCell13
        val cell14 = binding.tastiCalendarCell14
        val cell15 = binding.tastiCalendarCell15
        val cell16 = binding.tastiCalendarCell16
        val cell17 = binding.tastiCalendarCell17
        val cell18 = binding.tastiCalendarCell18
        val cell19 = binding.tastiCalendarCell19
        val cell20 = binding.tastiCalendarCell20
        val cell21 = binding.tastiCalendarCell21
        val cell22 = binding.tastiCalendarCell22
        val cell23 = binding.tastiCalendarCell23
        val cell24 = binding.tastiCalendarCell24
        val cell25 = binding.tastiCalendarCell25
        val cell26 = binding.tastiCalendarCell26
        val cell27 = binding.tastiCalendarCell27
        val cell28 = binding.tastiCalendarCell28
        val cell29 = binding.tastiCalendarCell29
        val cell30 = binding.tastiCalendarCell30
        val cell31 = binding.tastiCalendarCell31
        val cell32 = binding.tastiCalendarCell32
        val cell33 = binding.tastiCalendarCell33
        val cell34 = binding.tastiCalendarCell34
        val cell35 = binding.tastiCalendarCell35
        val cell36 = binding.tastiCalendarCell36
        val cell37 = binding.tastiCalendarCell37
        // Create a list of every cell
        cellsList = mutableListOf(
            cell1,
            cell2,
            cell3,
            cell4,
            cell5,
            cell6,
            cell7,
            cell8,
            cell9,
            cell10,
            cell11,
            cell12,
            cell13,
            cell14,
            cell15,
            cell16,
            cell17,
            cell18,
            cell19,
            cell20,
            cell21,
            cell22,
            cell23,
            cell24,
            cell25,
            cell26,
            cell27,
            cell28,
            cell29,
            cell30,
            cell31,
            cell32,
            cell33,
            cell34,
            cell35,
            cell36,
            cell37
        )
    }

    /**
     * Renders a given range of days.
     * <p>
     * This is used to hide unnecessary cells.
     * @param monthRange the range of days to render.
     */
    private fun renderDays(monthRange: Range<Int>) {
        val min = monthRange.lower
        val max = monthRange.upper

        // Render the month numbers with a leading space for single digit numbers
        for (i in min..max) {
            val dayValue = i - min + 1
            // Manage single digit dates differently
            val dayNumber = if (dayValue <= 9) " $dayValue" else dayValue.toString()
            cellsList[i].text = dayNumber
            cellsList[i].visibility = View.VISIBLE
            // Accessibility related info
            try {
                val correspondingDate =
                    LocalDate.of(dateWithChosenMonth.year, dateWithChosenMonth.month - 1, dayValue)
                val formatter: DateTimeFormatter =
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                cellsList[i].contentDescription = correspondingDate.format(formatter)
            } catch (_: Exception) {
            }
        }
        // Hide unnecessary cells, also resetting the text to avoid false positive when highlighting
        if (min != 0) for (i in 0 until min) {
            cellsList[i].visibility = View.INVISIBLE
            cellsList[i].text = ""
        }
        when (dateWithChosenMonth.month) {
            Month.NOVEMBER, Month.APRIL, Month.JUNE, Month.SEPTEMBER -> {
                for (i in (30 + min) until cellsList.size) {
                    cellsList[i].visibility = View.INVISIBLE
                    cellsList[i].text = ""
                }
            }
            Month.FEBRUARY -> {
                val leapIndex = if (dateWithChosenMonth.isLeapYear) 29 else 28
                for (i in (leapIndex + min) until cellsList.size) {
                    cellsList[i].visibility = View.INVISIBLE
                    cellsList[i].text = ""
                }
            }
            else -> {
                for (i in (31 + min) until cellsList.size) {
                    cellsList[i].visibility = View.INVISIBLE
                    cellsList[i].text = ""
                }
            }
        }
    }

    /**
     * Highlights a given date in the month.
     * <p>
     * This highlights a day in a variety of ways depending on the
     * parameters. Some parameters may not work properly in certain cases.
     *
     * @param day Int and not null, represents the day to highlight. If the month
     * doesn't have the day, nothing will be highlighted.
     * @param color the color used to highlight the month, used on the text itself
     * if no drawable is specified.
     * By default of the library, it's taken from the system.
     * @param drawable a drawable used as background, replacing the default colored circle.
     * It can be null.
     * @param makeBold Boolean, false by default, if true the day text will be in bold style. It has some problems,
     * since when the font is bold, it loses the monospace feature.
     * @param autoOpacity Boolean, false by default, if true allow different opacity levels of the background,
     * depending on how many times the day has been highlighted before.
     * @param autoTextColor Boolean, false by default, if true the text color will be computed
     * automatically to grant the best contrast available.
     * @param asForeground Boolean, false by default, if true the drawable or color will be used as
     * foreground, thus covering the text, totally or partially.
     * @param snackbarText String, a text to display if the day cell is clicked, empty by default. If empty
     * or null, the cell won't react to clicks.
     */
    fun highlightDay(
        day: Int,
        color: Int,
        drawable: Drawable? = null,
        makeBold: Boolean = false,
        autoOpacity: Boolean = false,
        autoTextColor: Boolean = false,
        asForeground: Boolean = false,
        snackbarText: String = ""
    ) {
        // Update the global event count
        eventCount += 1
        var currentAlpha = 0
        // The textview will be hidden if the day doesn't exist in the current month
        for (cell in cellsList) {

            // Check, for each cell, if it's the wanted day and it's visible
            if (cell.text.trim() == day.toString() && cell.visibility == View.VISIBLE) {
                // Day found, now highlight it accordingly
                if (drawable == null) {
                    // No drawable, color the text
                    cell.setTextColor(color)
                } else {
                    // Use the drawable as background or foreground
                    if (asForeground) {
                        cell.foreground = drawable
                        cell.foregroundTintList = ColorStateList.valueOf(color)
                    }
                    // In case of background, compute the opacity
                    else {
                        if (autoOpacity) {
                            if (cell.background != null) currentAlpha = cell.background.alpha
                        }
                        cell.background = drawable
                        cell.backgroundTintList = ColorStateList.valueOf(color)

                        if (autoOpacity) {
                            if (currentAlpha > 185) cell.background.alpha = 255
                            else cell.background.alpha = currentAlpha + 70
                        } else cell.background.alpha = 255

                        // Apply the automatic text color if requested
                        if (autoTextColor) {
                            cell.setTextColor(
                                getBestContrast(
                                    color,
                                    context,
                                    cell.background.alpha,
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                                        resources.configuration.isNightModeActive
                                    else null
                                )
                            )
                        }
                    }
                }
                // The font will change since monospace doesn't have a bold style
                if (makeBold) {
                    // This will result in a uglier layout and unaligned texts
                    cell.setTypeface(null, Typeface.BOLD)
                }

                // Display a snackbar on tap if the text exists
                if (snackbarText.isNotBlank()) {
                    cell.setOnClickListener {
                        // Simply uppercase the first letter of the message, if it isn't already
                        showSnackbar(
                            snackbarText.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            },
                            snackBarsBaseView ?: binding.root,
                            snackBarsDuration
                        )
                    }
                }

                // Break the for cycle, since the correct day has been found
                break
            }
        }
    }

    /**
     * Renders a given month in a given year.
     * <p>
     * This reloads the entire layout and apply the current settings,
     * it's the core method of the class.
     *
     * @param monthDate the date with the given month or year, if null the initial date is used.
     * @param events a list of TastiCalendarEvents, used to highlight a set of dates also adding
     * labels to it. It can be null.
     * @param dates a simple list of dates, used to highlight a set of dates. It can be null.
     */
    fun renderMonth(
        monthDate: LocalDate = dateWithChosenMonth,
        events: List<TastiCalendarEvent>? = null,
        dates: List<LocalDate>? = null
    ) {
        // Set the letters for the week days
        val monday = DayOfWeek.MONDAY
        val tuesday = DayOfWeek.TUESDAY
        val thursday = DayOfWeek.THURSDAY
        val wednesday = DayOfWeek.WEDNESDAY
        val friday = DayOfWeek.FRIDAY
        val saturday = DayOfWeek.SATURDAY
        val sunday = DayOfWeek.SUNDAY
        val locale = Locale.getDefault()
        if (!hideWeekDays) {
            var sundayIndex = 6
            if (!sundayFirst) {
                weekDaysList[0].text = monday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[1].text = tuesday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[2].text = wednesday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[3].text = thursday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[4].text = friday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[5].text = saturday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[6].text = sunday.getDisplayName(TextStyle.NARROW, locale)
            } else {
                sundayIndex = 0
                weekDaysList[0].text = sunday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[1].text = monday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[2].text = tuesday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[3].text = wednesday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[4].text = thursday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[5].text = friday.getDisplayName(TextStyle.NARROW, locale)
                weekDaysList[6].text = saturday.getDisplayName(TextStyle.NARROW, locale)
            }
            // Set the highlighting style for sunday
            when (sundayAppearance) {
                0 -> {
                    weekDaysList[sundayIndex].setTypeface(null, Typeface.NORMAL)
                    weekDaysList[sundayIndex].setTextColor(
                        getThemeColor(
                            com.google.android.material.R.attr.colorOnBackground,
                            context
                        )
                    )
                    weekDaysList[sundayIndex].alpha = .85f
                }
                1 -> {
                    weekDaysList[sundayIndex].setTypeface(null, Typeface.BOLD)
                    weekDaysList[sundayIndex].setTextColor(
                        getThemeColor(
                            com.google.android.material.R.attr.colorOnBackground,
                            context
                        )
                    )
                    weekDaysList[sundayIndex].alpha = .85f
                }
                2 -> {
                    weekDaysList[sundayIndex].setTypeface(null, Typeface.NORMAL)
                    weekDaysList[sundayIndex].setTextColor(
                        getThemeColor(
                            com.google.android.material.R.attr.colorTertiary,
                            context
                        )
                    )
                }
                3 -> {
                    weekDaysList[sundayIndex].setTypeface(null, Typeface.BOLD)
                    weekDaysList[sundayIndex].setTextColor(
                        getThemeColor(
                            com.google.android.material.R.attr.colorTertiary,
                            context
                        )
                    )
                }
            }
        } else {
            weekDaysList[0].visibility = View.GONE
            weekDaysList[1].visibility = View.GONE
            weekDaysList[2].visibility = View.GONE
            weekDaysList[3].visibility = View.GONE
            weekDaysList[4].visibility = View.GONE
            weekDaysList[5].visibility = View.GONE
            weekDaysList[6].visibility = View.GONE
        }
        // Some resetting logic
        if (monthDate.year != dateWithChosenMonth.year) {
            eventCount = 0
            binding.tastiCalendarMonthName.setTextColor(
                getThemeColor(
                    com.google.android.material.R.attr.colorSecondary,
                    context
                )
            )
        }
        val firstDayDate = monthDate.withDayOfMonth(1)
        dateWithChosenMonth = firstDayDate

        // Set the number and name (capitalized) for the month (from range 0-11 to 1-12)
        val firstDayOfWeekForChosenMonth = firstDayDate.dayOfWeek
        monthTitle = binding.tastiCalendarMonthName
        monthTitle.text =
            firstDayDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }


        if (!sundayFirst)
        // Case 1: monday is the first day of the week
            when (firstDayOfWeekForChosenMonth) {
                DayOfWeek.MONDAY -> renderDays(Range(0, 30))
                DayOfWeek.TUESDAY -> renderDays(Range(1, 31))
                DayOfWeek.WEDNESDAY -> renderDays(Range(2, 32))
                DayOfWeek.THURSDAY -> renderDays(Range(3, 33))
                DayOfWeek.FRIDAY -> renderDays(Range(4, 34))
                DayOfWeek.SATURDAY -> renderDays(Range(5, 35))
                DayOfWeek.SUNDAY -> renderDays(Range(6, 36))
                else -> {}
            }
        else
        // Case 2: sunday is the first day of the week
            when (firstDayOfWeekForChosenMonth) {
                DayOfWeek.SUNDAY -> renderDays(Range(0, 30))
                DayOfWeek.MONDAY -> renderDays(Range(1, 31))
                DayOfWeek.TUESDAY -> renderDays(Range(2, 32))
                DayOfWeek.WEDNESDAY -> renderDays(Range(3, 33))
                DayOfWeek.THURSDAY -> renderDays(Range(4, 34))
                DayOfWeek.FRIDAY -> renderDays(Range(5, 35))
                DayOfWeek.SATURDAY -> renderDays(Range(6, 36))
                else -> {}
            }

        // Show snack bars on month header press
        if (showSnackBars) {
            // Set the appropriate header
            monthTitle.setOnClickListener {
                showSnackbar(
                    getSnackMessage(),
                    snackBarsBaseView ?: binding.root,
                    snackBarsDuration
                )
            }
        } else {
            // Else remove any click listener
            monthTitle.setOnClickListener(null)
        }

        // Unify the lists to be the same list
        val finalList: MutableList<TastiCalendarEvent>? =
            if (events == null || events.isEmpty()) {
                if (dates != null && dates.isNotEmpty()) {
                    dates.map { TastiCalendarEvent(it, "") }.toMutableList()
                } else null
            } else events.toMutableList()
        if (finalList == null || finalList.isEmpty()) return
        finalList.sortBy { it.date.withYear(1970) }

        // Highlight the events
        var currentDate = finalList[0].date
        var dayEvents = mutableListOf<TastiCalendarEvent>()
        for (event in finalList) {
            // Compute the snackbar text
            if (event.date.withYear(1970).isEqual(currentDate.withYear(1970))) {
                dayEvents.add(event)
            } else {
                dayEvents = mutableListOf()
                dayEvents.add(event)
                currentDate = event.date.withYear(1970)
            }
            // Highlight the dates
            highlightDay(
                event.date.dayOfMonth,
                getThemeColor(com.google.android.material.R.attr.colorPrimary, context),
                AppCompatResources.getDrawable(context, R.drawable.tasticalendar_circle),
                makeBold = false,
                autoOpacity = true,
                autoTextColor = true,
                snackbarText = if (showSnackBars) formatEventList(dayEvents) else ""
            )
        }

        // Set the appearance (0 small default, 1 medium, 2 large, 3 xlarge)
        when (appearance) {
            0 -> colorize()
            1 -> setAppearance(1)
            2 -> setAppearance(2)
            3 -> setAppearance(3)
        }
    }

    /**
     * Obtains the message to display in the snackbar, dynamically
     */
    private fun getSnackMessage(): String {
        var prefix = ""
        try {
            prefix = if (snackBarsPluralFormatting && snackBarsPrefix != null) {
                String.format(
                    context.resources.getQuantityString(snackBarsPrefix!!, eventCount),
                    eventCount
                )
            } else context.getString(snackBarsPrefix!!)
        } catch (_: Exception) {
            snackBarsPrefix = null
            snackBarsPluralFormatting = false
        }

        // Build the final, formatted message for the month header
        val snackMessage = if (prefix.isNotEmpty())
            if (snackBarsPluralFormatting) prefix
            else "$prefix $eventCount"
        else "-> $eventCount"
        // Capitalize the first character
        val capitalizedMessage = snackMessage.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT)
            else it.toString()
        }
        return capitalizedMessage
    }

    /**
     * Changes the visual density of the month.
     * <p>
     * It is used to obtain different visualization styles.
     * @param appearance Int, can't be null, 0 means small, 1 medium, 2 large, 3 extra large.
     * Every other value is ignored.
     */
    fun setAppearance(appearance: Int) {
        when (appearance) {
            0 -> {
                for (cell in cellsList) {
                    val textColor = cell.currentTextColor
                    cell.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelMedium)
                    cell.setPadding(3, 3, 3, 3)
                    cell.typeface = Typeface.MONOSPACE
                    cell.setTextColor(textColor)
                }
                for (day in weekDaysList) {
                    val textColor = day.currentTextColor
                    day.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelMedium)
                    day.setTextColor(textColor)
                }
                monthTitle.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
            }
            1 -> {
                for (cell in cellsList) {
                    val textColor = cell.currentTextColor
                    cell.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
                    cell.setPadding(8, 8, 8, 8)
                    cell.typeface = Typeface.MONOSPACE
                    cell.setTextColor(textColor)
                }
                for (day in weekDaysList) {
                    val textColor = day.currentTextColor
                    day.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
                    day.setTextColor(textColor)
                }
                monthTitle.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge)
            }
            2 -> {
                for (cell in cellsList) {
                    val textColor = cell.currentTextColor
                    cell.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge)
                    cell.setPadding(12, 12, 12, 12)
                    cell.typeface = Typeface.MONOSPACE
                    cell.setTextColor(textColor)
                }
                for (day in weekDaysList) {
                    val textColor = day.currentTextColor
                    day.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge)
                    day.setTextColor(textColor)
                }
                monthTitle.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineSmall)
            }
            3 -> {
                for (cell in cellsList) {
                    val textColor = cell.currentTextColor
                    cell.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium)
                    cell.setPadding(16, 16, 16, 16)
                    cell.typeface = Typeface.MONOSPACE
                    cell.setTextColor(textColor)
                }
                for (day in weekDaysList) {
                    val textColor = day.currentTextColor
                    day.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium)
                    day.setTextColor(textColor)
                }
                monthTitle.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineLarge)
            }
            else -> return
        }
        colorize()
    }

    /**
     * Forces sunday to be displayed as the first day of the week.
     * <p>
     * This is used to force sunday as the first day of the week. If this method isn't called, the
     * first day of the week is automatically taken from the default locale.
     * @param appearance Enum of type TcAppearance, can't be null, specifies the selected
     * highlighting type
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     * @see TcSundayHighlight
     */
    fun setSundayHighlight(appearance: TcSundayHighlight, refresh: Boolean = true) {
        sundayAppearance = appearance.ordinal
        if (refresh) renderMonth()
    }

    /**
     * Forces sunday to be displayed as the first day of the week.
     * <p>
     * This is used to force sunday as the first day of the week. If this method isn't called, the
     * first day of the week is automatically taken from the default locale.
     * @param enable Boolean, can't be null, if true sets sunday as the first day of the week
     * for the current month.
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     */
    fun setSundayFirst(enable: Boolean, refresh: Boolean = true) {
        if (enable != sundayFirst) {
            sundayFirst = enable
            if (refresh) renderMonth()
        }
    }

    /**
     * Sets the weekdays row visibility.
     * <p>
     * This is used to show or hide the weekdays row. Useful to obtain compact layouts.
     * @param enable Boolean, can't be null, if true hides the week days row
     * for the current month.
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     */
    fun setHideWeekDays(enable: Boolean, refresh: Boolean = true) {
        if (enable != hideWeekDays) hideWeekDays = enable
        if (refresh) renderMonth()
    }

    /**
     * Add the given prefix to the message shown when tapping the month header.
     * <p>
     * This is used to display something before the number of events in the month,
     * in the form "<prefix> <events number>" (eg: "Events: 12").
     * @param prefix Int, can't be null, the id of the prefix to add to the snackbar message.
     * @param plural Boolean, false by default, if true the passed id is a plural String,
     * which will be formatted with the number of events accordingly.
     */
    fun setSnackBarsPrefix(prefix: Int, plural: Boolean = false, refresh: Boolean = true) {
        snackBarsPrefix = prefix
        snackBarsPluralFormatting = plural
        if (refresh) renderMonth()
    }

    /**
     * Sets the base view for the snackbar.
     * <p>
     * It can be used to avoid unwanted behaviors when a snackbar appears. For example, the snackbar
     * will spawn below the action button by default
     * @param view View, not null, it should be the base view. If the view is invalid, the binding
     * root will be used instead
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     */
    fun setSnackBarBaseView(view: View, refresh: Boolean = true) {
        snackBarsBaseView = view
        if (refresh) renderMonth()
    }

    /**
     * Sets the duration for the snackbar.
     * <p>
     * This is used to change the default duration, set to 3000 milliseconds.
     * @param duration Int, can't be null, defines the duration in milliseconds.
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     */
    fun setSnackBarsDuration(duration: Int, refresh: Boolean = true) {
        snackBarsDuration = duration
        if (refresh) renderMonth()
    }

    /**
     * Renders the same month for a different year.
     * <p>
     * Basically a wrapper around renderMonth, used to quickly switch only the year.
     * @param year Int, can't be null, represents the year to display this month into.
     */
    fun setYear(year: Int) = renderMonth(dateWithChosenMonth.withYear(year))

    /**
     * Resets the highlighting for the current month, thus removing every event from it.
     * <p>
     * It can be used to display a new set of events, or a different month.
     */
    fun resetHighlighting() {
        for (cell in cellsList) {
            if (cell.background != null) {
                cell.background.alpha = 0
                cell.background = null
                cell.setOnClickListener(null)
            }
            cell.setTextColor(
                getThemeColor(
                    com.google.android.material.R.attr.colorOnBackground,
                    context
                )
            )
            cell.foreground = null
        }
    }

    /**
     * Mainly used when the appearance changes, it colorizes some elements in the layout.
     * <p>
     * This is used to colorize the current month, the other months, and to slightly change
     * the weekdays text opacity (.85).
     */
    private fun colorize() {
        if (dateWithChosenMonth.month == LocalDate.now().month && dateWithChosenMonth.year == LocalDate.now().year) {
            monthTitle.setTextColor(
                getThemeColor(
                    com.google.android.material.R.attr.colorTertiary,
                    context
                )
            )
        } else monthTitle.setTextColor(
            getThemeColor(
                com.google.android.material.R.attr.colorSecondary,
                context
            )
        )
        for (weekDay in weekDaysList) weekDay.alpha = .85f
    }
}