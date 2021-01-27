package reprator.currencyconverter.datasource.remote.remoteMapper

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import io.mockk.*
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.util.Mapper

@RunWith(JUnit4::class)
class CurrencyExchangeRateMapperTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @SpyK
    var mapper: CurrencyExchangeRateMapper = CurrencyExchangeRateMapper()

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `create the map into Rate list pojo class`() = runBlockingTest {
        val exchangeRates_given = mapOf<String, String>("USDINR" to "73.18", "USDAED" to "0.18")

        val exchangeRates_expected = listOf(
            ModalCurrencyExchangeRates("USDINR", "73.18"),
            ModalCurrencyExchangeRates("USDAED", "0.18")
        )

        // given
       // val mapper = spyk<CurrencyExchangeRateMapper>()
        coEvery {  mapper.map( exchangeRates_given ) } returns exchangeRates_expected

        // when checking mocked method
        val firstResult = mapper.map( exchangeRates_given )

        // then
        Truth.assertThat(exchangeRates_expected).isEqualTo(firstResult)

        coVerify(atMost = 1,) {  mapper.map(exchangeRates_given) }

        confirmVerified(mapper)
    }
}