package reprator.currencyconverter.domain.repository

import kotlinx.coroutines.flow.Flow
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.PayPayResult

interface CurrencyExchangeRateRepository {
    suspend fun getCurrencyExchangeRates(isFromWorkManager: Boolean = false): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>>
}