package com.axoid.retailbankingdummy.feature.dashboard

// in :feature:dashboard/src/main/java/com/axoid/retailbankingdummy/feature/dashboard/Converters.kt

import androidx.room.TypeConverter
import java.math.BigDecimal

class Converters {
    @TypeConverter
    fun fromString(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun bigDecimalToString(bigDecimal: BigDecimal?): String? {
        return bigDecimal?.toPlainString()
    }
}
