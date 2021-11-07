package com.mineinabyss.idofront.time

import com.mineinabyss.idofront.time.TimeSpan.TimeType.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TimeSpanSerializer::class)
class TimeSpan(
    val value: Number,
    val type: TimeType = SECONDS //TODO think of what hte best default would be
) {
    private val doubleMillis: Double get() = inMillis.toDouble()
    val inMillis: Long = (value.toDouble() * type.inMillis).toLong()

    val inTicks: Long get() = inMillis / TICKS.inMillis
    val inSeconds: Double get() = doubleMillis / SECONDS.inMillis
    val inMinutes: Double get() = doubleMillis / MINUTES.inMillis
    val inHours: Double get() = doubleMillis / HOURS.inMillis
    val inDays: Double get() = doubleMillis / DAYS.inMillis
    val inWeeks: Double get() = doubleMillis / WEEKS.inMillis
    val inMonths: Double get() = doubleMillis / MONTHS.inMillis

    enum class TimeType(val ext: String, val inMillis: Long) {
        MILLIS("ms", 1),
        TICKS("t", 50),
        SECONDS("s", 1000),
        MINUTES("m", SECONDS.inMillis * 60),
        HOURS("h", MINUTES.inMillis * 60),
        DAYS("d", HOURS.inMillis * 24),
        WEEKS("w", DAYS.inMillis * 7),
        MONTHS("mo", DAYS.inMillis * 30),
    }

    override fun toString(): String {
        return "$value${type.ext}"
    }

    companion object {
        @JvmStatic
        fun fromString(string: String): TimeSpan {
            val splitAt = string.indexOfFirst { it.isLetter() }.takeIf { it > 0 } ?: string.length
            val value = string.take(splitAt).toDouble()
            val ext = string.drop(splitAt)
            return TimeSpan(value, TimeType.values().firstOrNull { it.ext == ext }
                ?: error("$string is not a valid timespan"))
        }
    }
}

val Number.millis get() = TimeSpan(toDouble(), MILLIS)
val Number.ticks get() = TimeSpan(toDouble(), TICKS)
val Number.seconds get() = TimeSpan(toDouble(), SECONDS)
val Number.minutes get() = TimeSpan(toDouble(), MINUTES)
val Number.hours get() = TimeSpan(toDouble(), HOURS)
val Number.days get() = TimeSpan(toDouble(), DAYS)
val Number.weeks get() = TimeSpan(toDouble(), WEEKS)
val Number.months get() = TimeSpan(toDouble(), MONTHS)

object TimeSpanSerializer : KSerializer<TimeSpan> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Time", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TimeSpan) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): TimeSpan =
        TimeSpan.fromString(decoder.decodeString())
}
