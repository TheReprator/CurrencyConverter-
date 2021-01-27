package reprator.currencyconverter.data.datasource

import kotlinx.coroutines.flow.Flow
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.useCases.PayPayResult

interface CurrencyListRemoteDataSource {
    suspend fun currencyList(): Flow<PayPayResult<List<ModalCurrency>>>
}