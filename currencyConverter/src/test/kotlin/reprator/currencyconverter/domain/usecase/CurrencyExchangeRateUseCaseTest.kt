package reprator.currencyconverter.domain.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import paypay.reprator.util.MainCoroutineRule
import reprator.currencyconverter.domain.repository.CurrencyExchangeRateRepository
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.Success

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CurrencyExchangeRateUseCaseTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val mainCoroutineRule = MainCoroutineRule()

    @RelaxedMockK
    lateinit var currencyExchangeRateRepository: CurrencyExchangeRateRepository

    private lateinit var currencyExchangeRateUseCase: CurrencyExchangeRateUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        currencyExchangeRateUseCase = CurrencyExchangeRateUseCase(currencyExchangeRateRepository)
    }

    @Test
    fun `get rate list with mocked Object`() =
        mainCoroutineRule.runBlockingTest {
            val rateListSuccess = listOf(
                ModalCurrencyExchangeRates("USDINR", "73.18"),
                ModalCurrencyExchangeRates("USDAED", "0.18")
            )

            coEvery {
                currencyExchangeRateRepository.getCurrencyExchangeRates()
            } returns flow {
                emit(Success(rateListSuccess))
            }

            val useCaseResult = currencyExchangeRateUseCase().single()

            Truth.assertThat(useCaseResult is Success).isTrue()
            Truth.assertThat(useCaseResult.get()).isEqualTo(rateListSuccess)

            coVerifySequence {
                currencyExchangeRateRepository.getCurrencyExchangeRates()
            }
            coVerify(atMost = 1) {
                currencyExchangeRateRepository.getCurrencyExchangeRates()
            }
        }

    @Test
    fun `get rate list with solid object`() =
        mainCoroutineRule.runBlockingTest {
            val rateListSuccess = listOf(
                ModalCurrencyExchangeRates("USDINR", "73.18"),
                ModalCurrencyExchangeRates("USDAED", "0.18")
            )

            coEvery {
                currencyExchangeRateUseCase()
            } returns flow {
                emit(Success(rateListSuccess))
            }

            val useCaseResult = currencyExchangeRateUseCase().single()

            Truth.assertThat(useCaseResult is Success).isTrue()
            Truth.assertThat(useCaseResult.get()).isEqualTo(rateListSuccess)

            coVerify(atMost = 1) {
                currencyExchangeRateUseCase()
                currencyExchangeRateRepository.getCurrencyExchangeRates()
            }
        }
}