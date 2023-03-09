package com.minar.tasticalendar.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.minar.tasticalendar.R
import com.minar.tasticalendar.databinding.TasticalendarYearBinding
import com.minar.tasticalendar.model.TastiCalendarEvent
import com.minar.tasticalendar.utilities.formatEventList
import com.minar.tasticalendar.utilities.getThemeColor
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

/**
 * A class representing a yearly calendar.
 * <p>
 * This uses the month class to generate a grid of 12 months.
 * @see TastiCalendarMonth
 * @param context the context of the view.
 * @param attrs the set of attributes specified in the layout.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class TastiCalendarYear(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    // Custom attributes
    private var hideWeekDays: Boolean
    private var sundayFirst: Boolean
    private var showSnackBars: Boolean
    private var appearance: Int

    // Other useful variables
    private var year: Int = LocalDate.now().year
    private lateinit var monthList: MutableList<TastiCalendarMonth>
    private var binding: TasticalendarYearBinding
    private var events = mutableListOf<TastiCalendarEvent>()

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.TastiCalendarYear, 0, 0
        ).apply {
            try {
                hideWeekDays = getBoolean(R.styleable.TastiCalendarYear_tcHideWeekDays, false)
                sundayFirst = getBoolean(R.styleable.TastiCalendarYear_tcSundayFirst, false)
                showSnackBars = getBoolean(R.styleable.TastiCalendarYear_tcShowInfoSnackBars, true)
                appearance = getInteger(R.styleable.TastiCalendarYear_tcAppearance, 0)
            } finally {
                recycle()
            }
        }
        binding = TasticalendarYearBinding.inflate(LayoutInflater.from(context), this, true)
        initYear()
    }

    /**
     * Initializes the layout for the year, assigning the bindings and the visibilities.
     * <p>
     * This is used once when the layout is first created.
     */
    private fun initYear() {
        // Months
        val january = binding.tastiCalendarYearJan
        val february = binding.tastiCalendarYearFeb
        val march = binding.tastiCalendarYearMar
        val april = binding.tastiCalendarYearApr
        val may = binding.tastiCalendarYearMay
        val june = binding.tastiCalendarYearJun
        val july = binding.tastiCalendarYearJul
        val august = binding.tastiCalendarYearAug
        val september = binding.tastiCalendarYearSep
        val october = binding.tastiCalendarYearOct
        val november = binding.tastiCalendarYearNov
        val december = binding.tastiCalendarYearDec

        // Create a list of every month
        monthList = mutableListOf(
            january,
            february,
            march,
            april,
            may,
            june,
            july,
            august,
            september,
            october,
            november,
            december
        )

        // If sunday is the first day, apply this
        if (WeekFields.of(Locale.getDefault()).firstDayOfWeek.name == "SUNDAY") {
            for (month in monthList) {
                month.setSundayFirst(true)
            }
        }

        // Set the appearance (0 small default, 1 medium, 2 large, 3 xlarge)
        when (appearance) {
            0 -> return
            1 -> setAppearance(1)
            2 -> setAppearance(2)
            3 -> setAppearance(3)
        }
    }

    /**
     * Highlights the current date with a ring.
     * <p>
     * This is only used internally, to circle the current day, if the displayed year
     * is the current year.
     * @param drawable a drawable to replace the default ring, it can be null.
     * @param color a color to replace the default color (colorTertiary), it can be null.
     */
    private fun highlightCurrentDate(drawable: Drawable? = null, color: Int? = null) {
        val date = LocalDate.now()
        if (date.year != year) return
        val chosenColor =
            color ?: getThemeColor(com.google.android.material.R.attr.colorTertiary, context)
        val chosenDrawable =
            drawable ?: AppCompatResources.getDrawable(context, R.drawable.tasticalendar_ring)
        highlightDate(date, chosenColor, chosenDrawable, asForeground = true)
    }

    /**
     * Renders a given year.
     * <p>
     * This reloads the entire layout and apply the current settings,
     * it's the core method of the class. If both events and dates are not null, the
     * latter will be ignored.
     * @see TastiCalendarEvent
     * @param year the year to render, it can't be null, but it can also be negative.
     * @param events a list of TastiCalendarEvents, used to highlight a set of dates also adding
     * labels to it. It can be null.
     * @param dates a simple list of dates, used to highlight a set of dates. It can be null.
     */
    fun renderYear(
        year: Int,
        events: List<TastiCalendarEvent>? = null,
        dates: List<LocalDate>? = null
    ) {
        this.year = year
        for (month in monthList) {
            month.setYear(year)
            month.resetHighlighting()
        }

        // Highlight the dates (only if they exist in the current year)
        if (year == LocalDate.now().year) highlightCurrentDate()

        // Unify the lists to be the same list
        val finalList: MutableList<TastiCalendarEvent>? =
            if (events == null || events.isEmpty()) {
                if (dates != null && dates.isNotEmpty()) {
                    dates.map { TastiCalendarEvent(it, "") }.toMutableList()
                } else null
            } else events.toMutableList()
        if (finalList == null || finalList.isEmpty()) return
        finalList.sortBy { it.date.withYear(1970) }

        // Compute temporary lists for each day
        var currentDate = finalList[0].date
        var dayEvents = mutableListOf<TastiCalendarEvent>()
        for (event in finalList) {
            // Compute the snackbar text
            if (event.date.isEqual(currentDate.withYear(event.date.year))) {
                dayEvents.add(event)
            } else {
                dayEvents = mutableListOf()
                dayEvents.add(event)
                currentDate = event.date
            }
            // Highlight the dates
            highlightDate(
                event.date,
                getThemeColor(com.google.android.material.R.attr.colorPrimary, context),
                AppCompatResources.getDrawable(context, R.drawable.tasticalendar_circle),
                makeBold = false,
                autoOpacity = true,
                autoTextColor = true,
                snackbarText = if (showSnackBars) formatEventList(dayEvents) else ""
            )
        }
    }

    /**
     * A wrapper around the highlight function of the month.
     * <p>
     * This highlights a day in a variety of ways depending on the
     * parameters. Some parameters may not work properly in certain cases.
     *
     * @param date the date to highlight. The year is not considered, and the date is used to
     * find the correct month in the year and call its function.
     * @param color the color used to highlight the month, if no drawable is specified,
     * by default of the library, it's taken from the system.
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
     * @see TastiCalendarMonth.highlightDay
     */
    fun highlightDate(
        date: LocalDate?,
        color: Int,
        drawable: Drawable?,
        makeBold: Boolean = false,
        autoOpacity: Boolean = false,
        autoTextColor: Boolean = false,
        asForeground: Boolean = false,
        snackbarText: String = ""
    ) {
        if (date == null) return
        // Since we have the full date, check if the event should be considered in the current year
        if (date.year > year) return
        // Actually highlight the date
        monthList[date.month.value - 1].highlightDay(
            date.dayOfMonth,
            color,
            drawable,
            makeBold = makeBold,
            autoOpacity = autoOpacity,
            autoTextColor = autoTextColor,
            asForeground = asForeground,
            snackbarText = snackbarText
        )
    }

    /**
     * Add the given prefix to the message shown when tapping the month header.
     * <p>
     * This is used to display something before the number of events for each month,
     * in the year, in the form "<prefix> <events number>" (eg: "Events: 12").
     * @param prefix Int, can't be null, the id of the prefix to add to the snackbar message.
     * @param plural Boolean, false by default, if true the passed id is a plural String,
     * which will be formatted with the number of events accordingly.
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     */
    fun setSnackBarsPrefix(prefix: Int, plural: Boolean = false, refresh: Boolean = true) {
        for (month in monthList)
            month.setSnackBarsPrefix(prefix, plural, refresh)
    }

    /**
     * Sets the duration for the snackbar.
     * <p>
     * This is used to change the default duration, set to 3000 milliseconds. Wrapper
     * for the month function.
     * @param duration Int, can't be null, defines the duration in milliseconds.
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     */
    fun setSnackBarsDuration(duration: Int, refresh: Boolean = true) {
        for (month in monthList)
            month.setSnackBarsDuration(duration, refresh)
    }

    /**
     * Sets the property to display the snack bars on tap or not.
     * <p>
     * If true, it is used to display advanced information when a day or a month header is pressed.
     * @param enabled Boolean, can't be null, if true enables the advanced info parameter.
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     */
    fun setShowSnackBarsEnabled(enabled: Boolean, refresh: Boolean = true) {
        showSnackBars = enabled
        if (refresh) renderYear(year, events)
    }

    /**
     * Sets the base view for the snackbar.
     * <p>
     * It can be used to avoid unwanted behaviors when a snackbar appears. For example, the snackbar
     * will spawn below the action button by default. Wrapper for the month function.
     * @param view View, not null, it should be the base view. If the view is invalid, the binding
     * root will be used instead
     * @param refresh Boolean, true by default, if false the layout won't be refreshed.
     */
    fun setSnackBarBaseView(view: View, refresh: Boolean = true) {
        for (month in monthList)
            month.setSnackBarBaseView(view, refresh)
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
        if (enable != sundayFirst)
            for (month in monthList)
                month.setSundayFirst(enable, refresh)
    }

    /**
     * Changes the visual density of the year.
     * <p>
     * It is used to cycle between appearances and it's basically a wrapper around
     * the function of TastiCalendarMonth.
     * @param appearance Int, can't be null, 0 means small, 1 medium, 2 large, 3 extra large.
     * Every other value is ignored.
     */
    fun setAppearance(appearance: Int): Int {
        if (appearance > 3 || appearance < 0) {
            this.appearance += 1
            if (this.appearance == 4) this.appearance = 0
        } else
            this.appearance = appearance
        for (month in monthList) {
            month.setAppearance(this.appearance)
        }
        return this.appearance
    }
}