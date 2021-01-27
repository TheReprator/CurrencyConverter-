package reprator.currencyconverter.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlinx.coroutines.test.runBlockingTest
import paypay.reprator.util.MainCoroutineRule
import reprator.currencyconverter.data.datasource.CurrencyExchangeRateRemoteDataSource
import reprator.currencyconverter.data.repository.db.DBManager
import reprator.currencyconverter.domain.repository.CurrencyExchangeRateRepository
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.Success
import reprator.paypay.base.util.ConnectionDetector

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CurrencyExchangeRateRepositoryImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    lateinit var dbManager: DBManager

    @MockK
    lateinit var currencyExchangeRateRemoteDataSource: CurrencyExchangeRateRemoteDataSource

    @MockK
    lateinit var connectionDetector: ConnectionDetector

    lateinit var coroutineScope: CoroutineScope

    private lateinit var currencyExchangeRateRepository: CurrencyExchangeRateRepository

    @Before
    fun createService() {
        MockKAnnotations.init(this, relaxed = true)

        coroutineScope = TestCoroutineScope(mainCoroutineRule.dispatcher)
        currencyExchangeRateRepository = CurrencyExchangeRateRepositoryImpl(
            currencyExchangeRateRemoteDataSource,
            dbManager, coroutineScope, connectionDetector
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `throw error from server,if list is empty`() =
        mainCoroutineRule.runBlockingTest {
            coEvery {
                dbManager.getCurrencyExchangeRates()
            } returns flow {
                emit(ErrorResult(Exception("No Record")))
            }

            coEvery {
                connectionDetector.isInternetAvailable
            } returns true

            coEvery {
                currencyExchangeRateRemoteDataSource.getCurrencyExchangeRates()
            } throws CurrencyExchangeRateException("Invalid data received")

            currencyExchangeRateRepository.getCurrencyExchangeRates().single()
        }

    @Test
    fun `throw error, if data doesn't exist in localDB & throw error as internet is not available`() =
        mainCoroutineRule.runBlockingTest {

            coEvery {
                dbManager.getCurrencyExchangeRates()
            } returns flow {
                emit(ErrorResult(Exception("No Record")))
            }

            coEvery {
                connectionDetector.isInternetAvailable
            } returns false

            val resultRemoteResult =
                currencyExchangeRateRepository.getCurrencyExchangeRates().single()

            Truth.assertThat(resultRemoteResult is Success).isFalse()
            Truth.assertThat(resultRemoteResult is ErrorResult).isTrue()
            Truth.assertThat((resultRemoteResult as ErrorResult).message)
                .isEqualTo("No internet connection.")

            coVerify(exactly = 1) {
                dbManager.getCurrencyExchangeRates()
            }

        }

    @Test
    fun `select rateList from localDB, if list is empty & display as well as insert after fetch`() =
        mainCoroutineRule.runBlockingTest {
            val rateListSuccess = listOf(
                ModalCurrencyExchangeRates("USDINR", "73.18"),
                ModalCurrencyExchangeRates("USDAED", "0.18")
            )

            coEvery {
                dbManager.getCurrencyExchangeRates()
            } returns flow {
                emit(ErrorResult(Exception("No Record")))
            }

            coEvery {
                connectionDetector.isInternetAvailable
            } returns true

            coEvery {
                currencyExchangeRateRemoteDataSource.getCurrencyExchangeRates()
            } returns flow {
                emit(Success(rateListSuccess))
            }

            coEvery {
                dbManager.saveCurrencyExchangeRates(any())
            } returns flow {
                emit(Success(listOf(1L, 2L)))
            }

            val resultRemoteResult =
                currencyExchangeRateRepository.getCurrencyExchangeRates().single()

            Truth.assertThat(resultRemoteResult is Success).isTrue()
            Truth.assertThat(resultRemoteResult.get()).isEqualTo(rateListSuccess)

            coVerifySequence {
                dbManager.getCurrencyExchangeRates()
                currencyExchangeRateRemoteDataSource.getCurrencyExchangeRates()
                dbManager.saveCurrencyExchangeRates(any())
            }
        }

    @Test
    fun `fetch RateList from Local db, if exist`() = mainCoroutineRule.runBlockingTest {
        val rateListSuccess = listOf(
            ModalCurrencyExchangeRates("USDINR", "73.18"),
            ModalCurrencyExchangeRates("USDAED", "0.18")
        )

        coEvery {
            dbManager.getCurrencyExchangeRates()
        } returns flow {
            emit(Success(rateListSuccess))
        }

        val resultRemoteResult = currencyExchangeRateRepository.getCurrencyExchangeRates().single()

        Truth.assertThat(resultRemoteResult is Success).isTrue()
        Truth.assertThat(resultRemoteResult.get()).isEqualTo(rateListSuccess)
    }
}