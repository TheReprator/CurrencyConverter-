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
import reprator.currencyconverter.domain.repository.CurrencyListRepository
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.useCases.Success

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CurrencyListUseCaseTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val mainCoroutineRule = MainCoroutineRule()

    @RelaxedMockK
    lateinit var currencyListRepository: CurrencyListRepository

    private lateinit var currencyListUseCase: CurrencyListUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        currencyListUseCase = CurrencyListUseCase(currencyListRepository)
    }

    @Test
    fun `get currency list with mocked Object`() =
        mainCoroutineRule.runBlockingTest {
            val rateListSuccess = listOf(
                ModalCurrency("USD", "United States of America"),
                ModalCurrency("INR", "India")
            )

            coEvery {
                currencyListRepository.currencyList()
            } returns flow {
                emit(Success(rateListSuccess))
            }

            val useCaseResult = currencyListUseCase().single()

            Truth.assertThat(useCaseResult is Success).isTrue()
            Truth.assertThat(useCaseResult.get()).isEqualTo(rateListSuccess)

            coVerifySequence {
                currencyListRepository.currencyList()
            }
            coVerify(atMost = 1) {
                currencyListRepository.currencyList()
            }
        }

    @Test
    fun `get currency list with solid object`() =
        mainCoroutineRule.runBlockingTest {
            val rateListSuccess = listOf(
                ModalCurrency("USD", "United States of America"),
                ModalCurrency("INR", "India")
            )

            coEvery {
                currencyListUseCase()
            } returns flow {
                emit(Success(rateListSuccess))
            }

            val useCaseResult = currencyListUseCase().single()

            Truth.assertThat(useCaseResult is Success).isTrue()
            Truth.assertThat(useCaseResult.get()).isEqualTo(rateListSuccess)

            coVerify(atMost = 1) {
                currencyListUseCase()
                currencyListRepository.currencyList()
            }
        }
}