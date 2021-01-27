package reprator.currencyconverter.datasource.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import reprator.currencyconverter.data.datasource.CurrencyExchangeRateRemoteDataSource
import reprator.currencyconverter.datasource.remote.remoteMapper.CurrencyExchangeRateMapper
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.PayPayResult
import reprator.paypay.base.useCases.Success
import reprator.paypay.base.util.safeApiCall
import reprator.paypay.base.util.toResult
import javax.inject.Inject

class CurrencyExchangeRateRemoteDataSourceImpl @Inject constructor(
    private val currencyApiService: CurrencyApiService,
    private val currencyListMapper: CurrencyExchangeRateMapper
) : CurrencyExchangeRateRemoteDataSource {

    private suspend fun getCurrencyExchangeRatesApi(): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>> =
            flow {
                currencyApiService.exchangeRates().toResult {
                    emit(Success(currencyListMapper.map(it)))
                }
            }

    override suspend fun getCurrencyExchangeRates(): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>> =
        safeApiCall(call = { getCurrencyExchangeRatesApi() })
}

