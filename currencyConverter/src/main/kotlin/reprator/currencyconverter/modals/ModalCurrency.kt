package reprator.currencyconverter.modals

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "currencyList")
data class ModalCurrency(@PrimaryKey val countryCode: String,
                         val countryName: String): Parcelable