package reprator.currencyconverter.datasource.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import io.mockk.spyk
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
import reprator.currencyconverter.data.datasource.CurrencyExchangeRateRemoteDataSource
import reprator.currencyconverter.datasource.remote.remoteMapper.CurrencyExchangeRateMapper
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.Success
import retrofit2.Response


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CurrencyExchangeRateRemoteDataSourceImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val mainCoroutineRule = MainCoroutineRule()

    var currencyApiService: CurrencyApiService = spyk<CurrencyApiService>()

    @SpyK
    var rateMapper: CurrencyExchangeRateMapper = CurrencyExchangeRateMapper()

    lateinit var cut: CurrencyExchangeRateRemoteDataSource

    @Before
    fun createService() {
        MockKAnnotations.init(this)
        cut = CurrencyExchangeRateRemoteDataSourceImpl(currencyApiService, rateMapper)
    }

    @Test
    fun `success with each concrete method behaviour`() = mainCoroutineRule.runBlockingTest {
        val mapResponse = mapOf("USDINR" to "73.18", "USDAED" to "0.18")
        val rawResponse = Response.success(mapResponse)

        val rateListSuccess = listOf(
            ModalCurrencyExchangeRates("USDINR", "73.18"),
            ModalCurrencyExchangeRates("USDAED", "0.18")
        )

        coEvery { rateMapper.map(mapResponse) } returns rateListSuccess

        coEvery {
            currencyApiService.exchangeRates()
        } returns rawResponse

        val resultRemoteResult = cut.getCurrencyExchangeRates().single()

        Truth.assertThat(resultRemoteResult is Success).isTrue()
        Truth.assertThat(resultRemoteResult.get()).isEqualTo(rateListSuccess)
    }

    @Test
    fun successWithMockRemoteDataSource() = mainCoroutineRule.runBlockingTest {

        val cut = mockk<CurrencyExchangeRateRemoteDataSource>(relaxed = true)
        val rateListSuccess = listOf(
            ModalCurrencyExchangeRates("USDINR", "73.18"),
            ModalCurrencyExchangeRates("USDAED", "0.18")
        )

        coEvery {
            cut.getCurrencyExchangeRates()
        } returns flow {
            emit(Success(rateListSuccess))
        }

        val resultRemoteResult = cut.getCurrencyExchangeRates().single()

        Truth.assertThat(resultRemoteResult is Success).isTrue()
        Truth.assertThat(resultRemoteResult.get()).isEqualTo(rateListSuccess)
    }

    @Test(expected = Exception::class)
    fun `throw error on invalid data for Rates`() = mainCoroutineRule.runBlockingTest {

        val cut = mockk<CurrencyExchangeRateRemoteDataSource>(relaxed = true)
        coEvery { cut.getCurrencyExchangeRates() } throws Exception("Invalid data received")
        cut.getCurrencyExchangeRates()
    }
}
