package com.hexsoft.dailypoints.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

object DateUtils {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): LocalDate = LocalDate.now()

    fun toDbString(date: LocalDate): String = formatter.format(date)

    fun fromDbString(value: String): LocalDate = LocalDate.parse(value, formatter)

    fun startOfWeek(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun endOfWeek(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    fun startOfMonth(date: LocalDate): LocalDate = date.withDayOfMonth(1)

    fun endOfMonth(date: LocalDate): LocalDate = date.withDayOfMonth(date.lengthOfMonth())

    fun yearMonth(date: LocalDate): YearMonth = YearMonth.from(date)
}
