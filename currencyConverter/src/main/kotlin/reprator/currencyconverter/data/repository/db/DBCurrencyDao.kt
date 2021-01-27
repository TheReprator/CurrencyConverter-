package reprator.currencyconverter.data.repository.db

import androidx.room.*
import reprator.currencyconverter.modals.ModalCurrency
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates

@Dao
interface DBCurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyList(currencyList: List<ModalCurrency>): List<Long>

    @Query("SELECT * FROM currencyList")
    fun getCurrencyList(): List<ModalCurrency>

    @Query("DELETE FROM currencyList")
    suspend fun deleteCurrencyList(): Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyExchange(modalCurrencyRates: List<ModalCurrencyExchangeRates>): List<Long>

    @Query("SELECT * FROM currencyRateList")
    fun getCurrencyExchangeItem(): List<ModalCurrencyExchangeRates>

    @Query("DELETE FROM currencyRateList")
    suspend fun deleteCurrencyExchangeItem(): Int


}