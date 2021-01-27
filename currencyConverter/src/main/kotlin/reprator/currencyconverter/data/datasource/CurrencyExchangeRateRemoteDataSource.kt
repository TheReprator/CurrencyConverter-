package reprator.currencyconverter.data.datasource

import kotlinx.coroutines.flow.Flow
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.PayPayResult

interface CurrencyExchangeRateRemoteDataSource {
    suspend fun getCurrencyExchangeRates(): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>>
}