package com.pimenov.crm.domain.agent

import java.util.Calendar

/**
 * Local regex-based parser for Russian-language intents.
 * Handles common patterns like "напомни завтра сделать отчет"
 * without any network call.
 */
class IntentParser {

    data class ParsedIntent(
        val taskTitle: String,
        val reminderAt: Long?,
        val isReminder: Boolean
    )

    /**
     * Attempts to parse a reminder or task intent from user text.
     * Returns null if the text doesn't match known patterns.
     */
    fun tryParse(text: String): ParsedIntent? {
        val lower = text.lowercase().trim()

        val reminderMatch = REMINDER_PATTERN.find(lower)
        if (reminderMatch != null) {
            val afterKeyword = lower.substringAfter(reminderMatch.value).trim()
            val (timeSpec, action) = extractTimeAndAction(afterKeyword)
            val title = cleanTaskTitle(action)
            if (title.isBlank()) return null

            val reminderAt = timeSpec?.let { resolveTime(it) }
            return ParsedIntent(
                taskTitle = title.replaceFirstChar { it.uppercase() },
                reminderAt = reminderAt,
                isReminder = true
            )
        }

        val taskMatch = TASK_PATTERN.find(lower)
        if (taskMatch != null) {
            val afterKeyword = lower.substringAfter(taskMatch.value).trim()
            val (timeSpec, action) = extractTimeAndAction(afterKeyword)
            val title = cleanTaskTitle(action)
            if (title.isBlank()) return null

            val reminderAt = timeSpec?.let { resolveTime(it) }
            return ParsedIntent(
                taskTitle = title.replaceFirstChar { it.uppercase() },
                reminderAt = reminderAt,
                isReminder = reminderAt != null
            )
        }

        return null
    }

    private fun extractTimeAndAction(text: String): Pair<String?, String> {
        for (pattern in TIME_PATTERNS) {
            val match = pattern.find(text)
            if (match != null) {
                val timeSpec = match.value.trim()
                val action = text.removeRange(match.range).trim()
                return timeSpec to action
            }
        }
        return null to text
    }

    private fun resolveTime(timeSpec: String): Long? {
        val cal = Calendar.getInstance()
        val lower = timeSpec.lowercase()

        // "в HH:MM" or "в H:MM"
        val clockMatch = CLOCK_PATTERN.find(lower)

        when {
            lower.contains("сегодня") -> {
                // already today
            }
            lower.contains("завтра") -> {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            lower.contains("послезавтра") -> {
                cal.add(Calendar.DAY_OF_YEAR, 2)
            }
            lower.contains("через полчаса") || lower.contains("через 30 минут") -> {
                cal.add(Calendar.MINUTE, 30)
                return cal.timeInMillis
            }
            lower.contains("через минуту") || lower.contains("через минутку") -> {
                cal.add(Calendar.MINUTE, 1)
                return cal.timeInMillis
            }
            lower.contains("через час") -> {
                cal.add(Calendar.HOUR_OF_DAY, 1)
                return cal.timeInMillis
            }
            THROUGH_MINUTES_PATTERN.find(lower) != null -> {
                val raw = THROUGH_MINUTES_PATTERN.find(lower)!!.groupValues[1]
                val mins = parseNumber(raw)
                if (mins != null) {
                    cal.add(Calendar.MINUTE, mins)
                    return cal.timeInMillis
                }
            }
            THROUGH_HOURS_PATTERN.find(lower) != null -> {
                val raw = THROUGH_HOURS_PATTERN.find(lower)!!.groupValues[1]
                val hours = parseNumber(raw)
                if (hours != null) {
                    cal.add(Calendar.HOUR_OF_DAY, hours)
                    return cal.timeInMillis
                }
            }
            lower.contains("понедельник") -> setNextDayOfWeek(cal, Calendar.MONDAY)
            lower.contains("вторник") -> setNextDayOfWeek(cal, Calendar.TUESDAY)
            lower.contains("сред") -> setNextDayOfWeek(cal, Calendar.WEDNESDAY)
            lower.contains("четверг") -> setNextDayOfWeek(cal, Calendar.THURSDAY)
            lower.contains("пятниц") -> setNextDayOfWeek(cal, Calendar.FRIDAY)
            lower.contains("суббот") -> setNextDayOfWeek(cal, Calendar.SATURDAY)
            lower.contains("воскресень") -> setNextDayOfWeek(cal, Calendar.SUNDAY)
            else -> {
                // No recognizable date → default to tomorrow 9:00
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (clockMatch != null) {
            val h = clockMatch.groupValues[1].toIntOrNull() ?: 9
            val m = clockMatch.groupValues[2].toIntOrNull() ?: 0
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
        } else {
            // Default to 9:00
            cal.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR)
            cal.set(Calendar.MINUTE, 0)
        }
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        // If the time already passed today, bump to tomorrow
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return cal.timeInMillis
    }

    private fun setNextDayOfWeek(cal: Calendar, targetDay: Int) {
        val today = cal.get(Calendar.DAY_OF_WEEK)
        var diff = targetDay - today
        if (diff <= 0) diff += 7
        cal.add(Calendar.DAY_OF_YEAR, diff)
    }

    private fun cleanTaskTitle(text: String): String {
        var cleaned = text.trim()
        // Remove leading prepositions/particles
        for (prefix in STRIP_PREFIXES) {
            if (cleaned.lowercase().startsWith(prefix)) {
                cleaned = cleaned.drop(prefix.length).trim()
            }
        }
        // Remove trailing punctuation
        cleaned = cleaned.trimEnd('.', '!', ',', ';')
        return cleaned
    }

    companion object {
        private const val DEFAULT_HOUR = 9

        private val REMINDER_PATTERN = Regex(
            "(?:напомни|напомнить|напоминание|не забыть|не забудь|разбуди|разбудить|разбудите|поставь будильник|поставь таймер|поставь напоминание|предупреди|уведоми|уведомить|сигнал через|алярм)"
        )

        private val TASK_PATTERN = Regex(
            "(?:нужно|надо|необходимо|добавь задачу|создай задачу|поставь задачу)"
        )

        private val CLOCK_PATTERN = Regex(
            "в\\s+(\\d{1,2}):(\\d{2})"
        )

        private val WORD_OR_DIGIT = "(?:\\d+|один|одну|одного|два|две|двух|три|трёх|трех|четыре|четырёх|четырех|пять|шесть|семь|восемь|девять|десять|пол)"

        private val THROUGH_MINUTES_PATTERN = Regex(
            "через\\s+($WORD_OR_DIGIT)\\s*минут"
        )

        private val THROUGH_HOURS_PATTERN = Regex(
            "через\\s+($WORD_OR_DIGIT)\\s*час"
        )

        private val WORD_NUMBER_MAP = mapOf(
            "один" to 1, "одну" to 1, "одного" to 1,
            "два" to 2, "две" to 2, "двух" to 2,
            "три" to 3, "трёх" to 3, "трех" to 3,
            "четыре" to 4, "четырёх" to 4, "четырех" to 4,
            "пять" to 5, "шесть" to 6, "семь" to 7,
            "восемь" to 8, "девять" to 9, "десять" to 10,
            "пол" to 0 // handled separately for "полчаса"
        )

        private fun parseNumber(raw: String): Int? {
            return raw.toIntOrNull() ?: WORD_NUMBER_MAP[raw.lowercase()]
        }

        private val TIME_PATTERNS = listOf(
            Regex("послезавтра\\s*(?:в\\s+\\d{1,2}:\\d{2})?"),
            Regex("завтра\\s*(?:в\\s+\\d{1,2}:\\d{2})?"),
            Regex("сегодня\\s*(?:в\\s+\\d{1,2}:\\d{2})?"),
            Regex("в\\s+(?:понедельник|вторник|среду|четверг|пятницу|субботу|воскресенье)\\s*(?:в\\s+\\d{1,2}:\\d{2})?"),
            Regex("через\\s+$WORD_OR_DIGIT\\s*(?:минут|час)\\w*"),
            Regex("через\\s+полчаса"),
            Regex("через\\s+минутк?у"),
            Regex("через\\s+час"),
            Regex("в\\s+\\d{1,2}:\\d{2}")
        )

        private val STRIP_PREFIXES = listOf(
            "что нужно ", "что надо ", "чтобы ",
            "мне ", "о том что ", "о том, что ",
            "про то что ", "про то, что ",
            "что ", "про "
        )
    }
}
