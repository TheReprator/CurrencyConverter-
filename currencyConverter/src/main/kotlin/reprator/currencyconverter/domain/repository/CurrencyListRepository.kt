package reprator.currencyconverter.domain.repository

import kotlinx.coroutines.flow.Flow
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.useCases.PayPayResult

interface CurrencyListRepository {
    suspend fun currencyList(isFromWorkManager: Boolean = false): Flow<PayPayResult<List<ModalCurrency>>>
}