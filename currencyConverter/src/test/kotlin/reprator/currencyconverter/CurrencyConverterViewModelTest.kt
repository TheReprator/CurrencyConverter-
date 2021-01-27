package reprator.currencyconverter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlinx.coroutines.test.runBlockingTest
import paypay.reprator.util.MainCoroutineRule
import reprator.currencyconverter.domain.usecase.CurrencyExchangeRateUseCase
import reprator.currencyconverter.domain.usecase.CurrencyListUseCase
import reprator.currencyconverter.modals.ModalCurrency
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.currencyconverter.ui.FilterExchangedRates
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.Success

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CurrencyConverterViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val coroutinesTestRule = MainCoroutineRule()

    @MockK
    lateinit var currencyListUseCase: CurrencyListUseCase

    @MockK
    lateinit var currencyExchangeRateUseCase: CurrencyExchangeRateUseCase

    @MockK
    lateinit var filterExchangedRates: FilterExchangedRates

    private lateinit var currencyConverterViewModel: CurrencyConverterViewModel

    private val currencyRateInput = listOf(
        ModalCurrencyExchangeRates("USDAED", "3.672985"),
        ModalCurrencyExchangeRates("USDAFN", "77.150404"),
        ModalCurrencyExchangeRates("USDZWL", "322.000186")
    )

    private val currencyListInput = listOf(
        ModalCurrency("AED", "United Arab Emirate"),
        ModalCurrency("AFN", "Afganistan"),
        ModalCurrency("ZWL", "Zimbabwe")
    )

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        currencyConverterViewModel = CurrencyConverterViewModel(
            coroutinesTestRule.testDispatcherProvider, currencyListUseCase,
            currencyExchangeRateUseCase, filterExchangedRates
        )
    }

    @Test
    fun `hit currency api at launch`() = coroutinesTestRule.dispatcher.runBlockingTest {

        Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyList.value).isTrue()
        Truth.assertThat(currencyConverterViewModel.errorMsgCurrencyList.value).isEmpty()

        coEvery {
            currencyListUseCase()
        } returns flow {
            emit(Success(currencyListInput))
        }

        currencyConverterViewModel.getCurrencyListUseCase()

        val currencyListResult: List<ModalCurrency> = currencyConverterViewModel.currencyList.value

        Truth.assertThat(currencyListResult).isEqualTo(currencyListInput)
        Truth.assertThat(currencyListResult.size).isEqualTo(3)

        Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyList.value).isFalse()
        Truth.assertThat(currencyConverterViewModel.errorMsgCurrencyList.value).isEmpty()
    }

    @Test
    fun `throw error with currency api at launch`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            val errorResult = "No data exist On server"

            Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyList.value).isTrue()
            Truth.assertThat(currencyConverterViewModel.errorMsgCurrencyList.value).isEmpty()

            coEvery {
                currencyListUseCase()
            } returns flow {
                emit(ErrorResult(message = errorResult))
            }

            currencyConverterViewModel.getCurrencyListUseCase()

            val currencyListResult: List<ModalCurrency> =
                currencyConverterViewModel.currencyList.value

            Truth.assertThat(currencyListUseCase().single()).isInstanceOf(ErrorResult::class.java)
            Truth.assertThat(currencyConverterViewModel.errorMsgCurrencyList.value)
                .isEqualTo(errorResult)

            Truth.assertThat(currencyListResult).isEqualTo(emptyList<ModalCurrency>())
            Truth.assertThat(currencyListResult.size).isEqualTo(0)
        }

    @Test(expected = Exception::class)
    fun `flow catch error with currency api at launch`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            val errorResult = Exception("No data exist On server")

            Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyList.value).isTrue()
            Truth.assertThat(currencyConverterViewModel.errorMsgCurrencyList.value).isEmpty()

            coEvery {
                currencyListUseCase()
            } throws errorResult

            currencyConverterViewModel.getCurrencyListUseCase()

            Truth.assertThat(currencyListUseCase().single()).isInstanceOf(ErrorResult::class.java)
        }

    @Test
    fun `hit rate api at launch`() = coroutinesTestRule.dispatcher.runBlockingTest {

        Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyRates.value).isTrue()
        Truth.assertThat(currencyConverterViewModel.errorRateMsg.value).isEmpty()

        coEvery {
            currencyExchangeRateUseCase()
        } returns flow {
            emit(Success(currencyRateInput))
        }

        currencyConverterViewModel.getCurrencyExchangeRateUseCase()

        val rateListResult: List<ModalCurrencyExchangeRates> =
            currencyConverterViewModel._currencyExchangeRates.value

        Truth.assertThat(rateListResult).isEqualTo(currencyRateInput)
        Truth.assertThat(rateListResult.size).isEqualTo(3)

        Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyRates.value).isFalse()
        Truth.assertThat(currencyConverterViewModel.errorRateMsg.value).isEmpty()
    }

    @Test
    fun `throw error with rate api at launch`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            val errorResult = "No data exist On server"

            Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyRates.value).isTrue()
            Truth.assertThat(currencyConverterViewModel.errorRateMsg.value).isEmpty()

            coEvery {
                currencyExchangeRateUseCase()
            } returns flow {
                emit(ErrorResult(message = errorResult))
            }

            currencyConverterViewModel.getCurrencyExchangeRateUseCase()

            val currencyListResult: List<ModalCurrencyExchangeRates> =
                currencyConverterViewModel._currencyExchangeRates.value

            Truth.assertThat(currencyExchangeRateUseCase().single())
                .isInstanceOf(ErrorResult::class.java)
            Truth.assertThat(currencyConverterViewModel.errorRateMsg.value)
                .isEqualTo(errorResult)

            Truth.assertThat(currencyListResult).isEqualTo(emptyList<ModalCurrency>())
            Truth.assertThat(currencyListResult.size).isEqualTo(0)
        }

    @Test(expected = Exception::class)
    fun `flow catch error with rate api at launch`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            val errorResult = Exception("No data exist On server")

            Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyRates.value).isTrue()
            Truth.assertThat(currencyConverterViewModel.errorRateMsg.value).isEmpty()

            coEvery {
                currencyExchangeRateUseCase()
            } throws errorResult

            currencyConverterViewModel.getCurrencyExchangeRateUseCase()

            Truth.assertThat(currencyExchangeRateUseCase().single())
                .isInstanceOf(ErrorResult::class.java)
        }

    @Test
    fun `displayRates for 1AED to other currencies in USD, for AED selection as currency, after rate & Currency fetch`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            currencyConverterViewModel.selectedCurrencyPosition.value = 0

            currencyConverterViewModel._currencyList.value = currencyListInput
            currencyConverterViewModel._currencyExchangeRates.value = currencyRateInput

            val expectedOutput = listOf(
                ModalCurrencyExchangeRates("AFN", "21.0"),
                ModalCurrencyExchangeRates("ZWL", "87.67")
            )

            coEvery {
                filterExchangedRates("1", "AED", currencyRateInput)
            } returns expectedOutput

            currencyConverterViewModel.filterData()

            Truth.assertThat(currencyConverterViewModel.currencyExchangeRatesManipulated.value)
                .isEqualTo(expectedOutput)
            Truth.assertThat(currencyConverterViewModel.currencyExchangeRatesManipulated.value.size)
                .isEqualTo(2)
            Truth.assertThat(currencyConverterViewModel.currencyExchangeRatesManipulated.value[1].currencyName)
                .isEqualTo("ZWL")
        }

    @Test
    fun `retry for currency api fetch`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyList.value).isTrue()
            Truth.assertThat(currencyConverterViewModel.errorMsgCurrencyList.value).isEmpty()

            currencyConverterViewModel.retryCurrencyList()

            coVerify(atMost = 1) {
                currencyConverterViewModel.getCurrencyListUseCase()
            }
        }

    @Test
    fun `retry for rate api fetch`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            Truth.assertThat(currencyConverterViewModel.isLoadingCurrencyRates.value).isTrue()
            Truth.assertThat(currencyConverterViewModel.errorRateMsg.value).isEmpty()

            currencyConverterViewModel.retryExchangeRates()

            coVerify(atMost = 1) {
                currencyConverterViewModel.getCurrencyExchangeRateUseCase()
            }
        }

    @Test
    fun `do nothing for invalid currency position -1`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            currencyConverterViewModel.selectedCurrencyPosition.value = -1

            currencyConverterViewModel.filterData()

            Truth.assertThat(currencyConverterViewModel.currencyExchangeRatesManipulated.value)
                .isEmpty()
            Truth.assertThat(currencyConverterViewModel.currencyExchangeRatesManipulated.value.size)
                .isEqualTo(0)
        }

    @Test
    fun `do nothing for currency value 0 or empty`() =
        coroutinesTestRule.dispatcher.runBlockingTest {

            currencyConverterViewModel.currencyValue.value = ""

            currencyConverterViewModel.filterData()

            Truth.assertThat(currencyConverterViewModel.currencyExchangeRatesManipulated.value.size)
                .isEqualTo(0)

            currencyConverterViewModel.currencyValue.value = "000"

            currencyConverterViewModel.filterData()

            Truth.assertThat(currencyConverterViewModel.currencyExchangeRatesManipulated.value.size)
                .isEqualTo(0)
        }

}