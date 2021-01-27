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
import reprator.currencyconverter.data.datasource.CurrencyListRemoteDataSource
import reprator.currencyconverter.data.repository.db.DBManager
import reprator.currencyconverter.domain.repository.CurrencyListRepository
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.Success
import reprator.paypay.base.util.ConnectionDetector

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CurrencyListRepositoryImplTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    lateinit var dbManager: DBManager

    @MockK
    lateinit var currencyListRemoteDataSource: CurrencyListRemoteDataSource

    @MockK
    lateinit var connectionDetector: ConnectionDetector

    private lateinit var coroutineScope: CoroutineScope

    private lateinit var currencyListRepository: CurrencyListRepository

    @Before
    fun createService() {
        MockKAnnotations.init(this, relaxed = true)

        coroutineScope = TestCoroutineScope(mainCoroutineRule.dispatcher)
        currencyListRepository = CurrencyListRepositoryImpl(
            currencyListRemoteDataSource, dbManager,
            coroutineScope, connectionDetector
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `throw error from server,if currency list is empty`() =
        mainCoroutineRule.runBlockingTest {
            coEvery {
                dbManager.currencyList()
            } returns flow {
                emit(ErrorResult(Exception("No Record")))
            }

            coEvery {
                connectionDetector.isInternetAvailable
            } returns true

            coEvery {
                currencyListRemoteDataSource.currencyList()
            } throws CurrencyListException("Invalid data received")

            currencyListRepository.currencyList().single()
        }

    @Test
    fun `throw error, if data doesn't exist in localDB & throw error as internet is not available`() =
        mainCoroutineRule.runBlockingTest {

            coEvery {
                dbManager.currencyList()
            } returns flow {
                emit(ErrorResult(Exception("No Record")))
            }

            coEvery {
                connectionDetector.isInternetAvailable
            } returns false

            val resultRemoteResult =
                currencyListRepository.currencyList().single()

            Truth.assertThat(resultRemoteResult is Success).isFalse()
            Truth.assertThat(resultRemoteResult is ErrorResult).isTrue()
            Truth.assertThat((resultRemoteResult as ErrorResult).message)
                .isEqualTo("No internet connection.")

            coVerify(exactly = 1) {
                dbManager.currencyList()
            }

        }

    @Test
    fun `select rateList from localDB, if list is empty & display as well as insert after fetch`() =
        mainCoroutineRule.runBlockingTest {
            val rateListSuccess = listOf(
                ModalCurrency("USD", "United States of America"),
                ModalCurrency("INR", "India")
            )

            coEvery {
                dbManager.currencyList()
            } returns flow {
                emit(ErrorResult(Exception("No Record")))
            }

            coEvery {
                connectionDetector.isInternetAvailable
            } returns true

            coEvery {
                currencyListRemoteDataSource.currencyList()
            } returns flow {
                emit(Success(rateListSuccess))
            }

            coEvery {
                dbManager.saveCurrencyList(any())
            } returns flow {
                emit(Success(listOf(1L, 2L)))
            }

            val resultRemoteResult =
                currencyListRepository.currencyList().single()

            Truth.assertThat(resultRemoteResult is Success).isTrue()
            Truth.assertThat(resultRemoteResult.get()).isEqualTo(rateListSuccess)

            coVerifySequence {
                dbManager.currencyList()
                currencyListRemoteDataSource.currencyList()
                dbManager.saveCurrencyList(any())
            }
        }

    @Test
    fun `fetch RateList from Local db, if exist`() = mainCoroutineRule.runBlockingTest {
        val rateListSuccess = listOf(
            ModalCurrency("USD", "United States of America"),
            ModalCurrency("INR", "India")
        )

        coEvery {
            dbManager.currencyList()
        } returns flow {
            emit(Success(rateListSuccess))
        }

        val resultRemoteResult = currencyListRepository.currencyList().single()

        Truth.assertThat(resultRemoteResult is Success).isTrue()
        Truth.assertThat(resultRemoteResult.get()).isEqualTo(rateListSuccess)
    }
}