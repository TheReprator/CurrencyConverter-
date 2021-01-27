package reprator.currencyconverter.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import paypay.reprator.util.MainCoroutineRule
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class FilterExchangedRatesTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val mainCoroutineRule = MainCoroutineRule()

    lateinit var filterExchangedRates: FilterExchangedRates

    private val currencyListInput = listOf(
        ModalCurrencyExchangeRates("USDAED", "3.672985"),
        ModalCurrencyExchangeRates("USDAFN", "77.150404"),
        ModalCurrencyExchangeRates("USDZWL", "322.000186")
    )

    @Before
    fun setup() {
        filterExchangedRates = FilterExchangedRates()
    }

    @Test
    fun `filter rates, for 1 AED`() {
        val expectedOutput = listOf(
            ModalCurrencyExchangeRates("AFN", "21.0"),
            ModalCurrencyExchangeRates("ZWL", "87.67")
        )

        val result = filterExchangedRates("1","AED",
            modalCurrencyExchangeRatesList= currencyListInput)

        Truth.assertThat(result).isEqualTo(expectedOutput)
        Truth.assertThat(result.size).isEqualTo(2)
        Truth.assertThat(result[1].currencyName).isEqualTo("ZWL")
    }
}