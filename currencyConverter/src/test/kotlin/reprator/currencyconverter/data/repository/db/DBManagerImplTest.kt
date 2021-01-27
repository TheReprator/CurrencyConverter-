package reprator.currencyconverter.data.repository.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import reprator.currencyconverter.modals.ModalCurrency
import kotlinx.coroutines.test.runBlockingTest
import paypay.reprator.util.MainCoroutineRule
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.Success

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DBManagerImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    lateinit var dbCurrencyDao: DBCurrencyDao

    private lateinit var dbManager: DBManager

    private val rateListOutput = listOf(
        ModalCurrency("USD", "United States of America"),
        ModalCurrency("INR", "India")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        dbManager = DBManagerImpl(dbCurrencyDao)
    }

    @Test
    fun emptyCurrencyList() =
        mainCoroutineRule.runBlockingTest {
            val input = emptyList<ModalCurrency>()
            coEvery {
                dbCurrencyDao.getCurrencyList()
            } returns input

            val outputResult = dbManager.currencyList().single()

            Truth.assertThat(outputResult is ErrorResult).isTrue()
            Truth.assertThat((outputResult as ErrorResult).message).isEqualTo("No Record")
        }

    @Test
    fun getCurrencyList() =
        mainCoroutineRule.runBlockingTest {
            coEvery {
                dbCurrencyDao.getCurrencyList()
            } returns rateListOutput

            val outputResult = dbManager.currencyList().single()

            Truth.assertThat(outputResult is Success).isTrue()
            Truth.assertThat(outputResult.get()).isEqualTo(rateListOutput)
        }

    @Test
    fun saveCurrencyList() =
        mainCoroutineRule.runBlockingTest {

            val successResult = listOf(1L, 2L)

            coEvery {
                dbCurrencyDao.insertCurrencyList(any())
            } returns successResult

            val outputResult = dbManager.saveCurrencyList(rateListOutput).single()

            Truth.assertThat(outputResult is Success).isTrue()
            Truth.assertThat(outputResult.get()).isEqualTo(successResult)
        }

    @Test
    fun deleteCurrencyList() =
        mainCoroutineRule.runBlockingTest {

            coEvery {
                dbCurrencyDao.deleteCurrencyList()
            } returns 1

            val outputResult = dbManager.deleteCurrencyList().single()

            Truth.assertThat(outputResult is Success).isTrue()
            Truth.assertThat(outputResult.get()).isEqualTo(1)
        }
}