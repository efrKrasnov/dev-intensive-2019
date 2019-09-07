package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var diff: Long = this.time - date.time
    var isBefore = diff < 0
    var absDiff:Long = diff.absoluteValue
    return when (absDiff) {
        in 0 * SECOND..1 * SECOND -> {
            "только что"
        }
        in 1 * SECOND..45 * SECOND -> {
            if (isBefore) {
                "несколько секунд назад"
            } else {
                "через несколько секунд"
            }
        }
        in 45 * SECOND..75 * SECOND -> {
            if (isBefore) {
                "минуту назад"
            } else {
                "через минуту"
            }
        }
        in 75 * SECOND..45 * MINUTE -> {
            val intervalString: String = TimeUnits.MINUTE.plural(Math.round(absDiff.toDouble() / MINUTE).toInt())
            if (isBefore) {
                "$intervalString назад"
            } else {
                "через $intervalString"
            }
        }
        in 45 * MINUTE..75 * MINUTE -> {
            if (isBefore) {
                "час назад"
            } else {
                "через час"
            }
        }
        in 75 * MINUTE..22 * HOUR -> {
            val intervalString: String = TimeUnits.HOUR.plural(Math.round(absDiff.toDouble() / HOUR).toInt())
            if (isBefore) {
                "$intervalString назад"
            } else {
                "через $intervalString"
            }
        }
        in 22 * HOUR..26 * HOUR -> {
            if (isBefore) {
                "день назад"
            } else {
                "через день"
            }
        }
        in 26 * HOUR..360 * DAY -> {
            val intervalString: String = TimeUnits.DAY.plural(Math.round(absDiff.toDouble() / DAY).toInt())
            if (isBefore) {
                "$intervalString назад"
            } else {
                "через $intervalString"
            }
        }
        else -> {
            if (isBefore) {
                "более года назад"
            } else {
                "более чем через год"
            }
        }
    }
}

enum class TimeUnits {
    SECOND {
        override fun plural(value: Int): String {
            return "$value ${textForPlural(value)}"
        }

        private fun textForPlural(value: Int): String {
            var tempValue: Int = value.absoluteValue % 100
            return when (tempValue) {
                0 ->
                    "секунд"
                1 ->
                    "секунду"
                in 2..4 ->
                    "секунды"
                in 5..20 ->
                    "секунд"
                else -> textForPlural(tempValue % 10)
            }
        }
    },
    MINUTE {
        override fun plural(value: Int): String {
            return "$value ${textForPlural(value)}"
        }

        private fun textForPlural(value: Int): String {
            var tempValue: Int = value.absoluteValue % 100
            return when (tempValue) {
                0 ->
                    "минут"
                1 ->
                    "минуту"
                in 2..4 ->
                    "минуты"
                in 5..20 ->
                    "минут"
                else -> textForPlural(tempValue % 10)
            }
        }
    },
    HOUR {
        override fun plural(value: Int): String {
            return "$value ${textForPlural(value)}"
        }

        private fun textForPlural(value: Int): String {
            var tempValue: Int = value.absoluteValue % 100
            return when (tempValue) {
                0 ->
                    "часов"
                1 ->
                    "час"
                in 2..4 ->
                    "часа"
                in 5..20 ->
                    "часов"
                else -> textForPlural(tempValue % 10)
            }
        }
    },
    DAY {
        override fun plural(value: Int): String {
            return "$value ${textForPlural(value)}"
        }

        private fun textForPlural(value: Int): String {
            var tempValue: Int = value.absoluteValue % 100
            return when (tempValue) {
                0 ->
                    "дней"
                1 ->
                    "день"
                in 2..4 ->
                    "дня"
                in 5..20 ->
                    "дней"
                else -> textForPlural(tempValue % 10)
            }
        }
    };

    abstract fun plural(value: Int): String
}

fun Date.shortFormat(): String {
    val pattern = if(this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}