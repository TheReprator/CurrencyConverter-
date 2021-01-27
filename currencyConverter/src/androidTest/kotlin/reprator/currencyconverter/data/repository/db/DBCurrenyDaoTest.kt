package reprator.currencyconverter.data.repository.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import reprator.currencyconverter.modals.ModalCurrency
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PostsDaoTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var mDatabase: DBCurrencyDatabase
    private lateinit var dao: DBCurrencyDao

    @Before
    fun init() {
        mDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DBCurrencyDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = mDatabase.currencyDAO()
    }

    @Test
    fun noCurrencyExist() = runBlocking {
        val currencyList = emptyList<ModalCurrency>()

        val dbCurrencyList = dao.getCurrencyList()

        Truth.assertThat(dbCurrencyList).isEqualTo(currencyList)
        Truth.assertThat(dbCurrencyList.isEmpty()).isTrue()
    }

    @Test
    @Throws(InterruptedException::class)
    fun insert_and_select_rateList() = runBlocking {
        val rateList = listOf(
            ModalCurrencyExchangeRates("USDINR", "73.15"),
            ModalCurrencyExchangeRates("USDAED", "0.35")
        )

        dao.insertCurrencyExchange(rateList)

        val dbPost = dao.getCurrencyExchangeItem().first()
        Truth.assertThat(dbPost).isEqualTo(rateList[0])
    }

    @Test
    fun deleteRecordsAfterInsertingRates() = runBlocking {
        val rateList = listOf(
            ModalCurrencyExchangeRates("USDINR", "73.15"),
            ModalCurrencyExchangeRates("USDAED", "0.35")
        )

        dao.insertCurrencyExchange(rateList)

        val dbPost = dao.getCurrencyExchangeItem().first()
        Truth.assertThat(dbPost).isEqualTo(rateList[0])

        dao.deleteCurrencyExchangeItem()

        val dbCurrencyList = dao.getCurrencyList()

        Truth.assertThat(dbCurrencyList.isEmpty()).isTrue()
    }

    @Test
    fun insert_and_select_currencyList() = runBlocking {
        val currencyList = listOf(
            ModalCurrency("USD", "United States of America"),
            ModalCurrency("INR", "Indian Rupees")
        )

        dao.insertCurrencyList(currencyList)

        val dbCurrencyList = dao.getCurrencyList().first()

        Truth.assertThat(dbCurrencyList).isEqualTo(currencyList[0])
    }

    @After
    fun cleanup() {
        mDatabase.close()
    }
}
