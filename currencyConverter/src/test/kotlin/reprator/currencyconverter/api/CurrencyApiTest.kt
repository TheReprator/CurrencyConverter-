package reprator.currencyconverter.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import reprator.currencyconverter.api.setup.EnvelopeConverterFactory
import reprator.currencyconverter.api.setup.createJacksonConverterFactory
import reprator.currencyconverter.api.setup.createJacksonMapper
import reprator.currencyconverter.api.setup.enqueueResponse
import reprator.currencyconverter.datasource.remote.CurrencyApiService
import retrofit2.Retrofit

@RunWith(JUnit4::class)
class CurrencyApiTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: CurrencyApiService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(EnvelopeConverterFactory(createJacksonMapper().typeFactory))
            .addConverterFactory(createJacksonConverterFactory())
            .build()
            .create(CurrencyApiService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getCurrencyList() = runBlocking {
        javaClass.enqueueResponse(mockWebServer, "currencylist.json")
        val currencyList = service.currencyList().body()
        val request = mockWebServer.takeRequest()

        Truth.assertThat(currencyList).isNotNull()
        Truth.assertThat(currencyList!!.size).isEqualTo(3)
        Truth.assertThat(currencyList["AED"]).isEqualTo("United Arab Emirates Dirham")

        Truth.assertThat(request.path).isEqualTo("/list?access_key=b03a2af1b28993cc2127b2d1458145a7")
        Truth.assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun getRateList() = runBlocking {
        javaClass.enqueueResponse(mockWebServer, "rates.json")
        val currencyList = service.exchangeRates().body()

        Truth.assertThat(currencyList).isNotNull()
        Truth.assertThat(currencyList!!.size).isEqualTo(3)
        Truth.assertThat(currencyList["USDAED"]).isEqualTo("3.672985")
    }


}