package reprator.currencyconverter.modals

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "currencyRateList")
data class ModalCurrencyExchangeRates(
    @PrimaryKey val currencyName: String,
    val currencyValue: String
): Parcelable