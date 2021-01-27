package reprator.currencyconverter.datasource.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import reprator.currencyconverter.data.datasource.CurrencyListRemoteDataSource
import reprator.currencyconverter.datasource.remote.remoteMapper.CurrencyListMapper
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.useCases.PayPayResult
import reprator.paypay.base.useCases.Success
import reprator.paypay.base.util.safeApiCall
import reprator.paypay.base.util.toResult
import javax.inject.Inject

class CurrencyListRemoteDataSourceImpl @Inject constructor(
    private val currencyApiService: CurrencyApiService,
    private val currencyListMapper: CurrencyListMapper
) : CurrencyListRemoteDataSource {

    private suspend fun currencyListApi(): Flow<PayPayResult<List<ModalCurrency>>> =
        flow {
            currencyApiService.currencyList().toResult {
                emit(Success(currencyListMapper.map(it)))
            }
        }

    override suspend fun currencyList(): Flow<PayPayResult<List<ModalCurrency>>> =
        safeApiCall(call = { currencyListApi() })
}

