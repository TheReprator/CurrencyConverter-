package reprator.currencyconverter.datasource.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {

    companion object {
        private const val CURRENCY_API_KEY = "b03a2af1b28993cc2127b2d1458145a7"
    }

    @GET("list")
    suspend fun currencyList(
        @Query("access_key") searchItem: String = CURRENCY_API_KEY
    ): Response<Map<String, String>>

    @GET("live")
    suspend fun exchangeRates(
        @Query("access_key") searchItem: String = CURRENCY_API_KEY
    ): Response<Map<String, String>>
}