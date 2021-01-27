package reprator.currencyconverter.data.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import reprator.currencyconverter.modals.ModalCurrency
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates

@Database(entities = [ModalCurrency::class, ModalCurrencyExchangeRates::class],
    version = 1, exportSchema = true)
abstract class DBCurrencyDatabase : RoomDatabase() {
    abstract fun currencyDAO(): DBCurrencyDao
}
