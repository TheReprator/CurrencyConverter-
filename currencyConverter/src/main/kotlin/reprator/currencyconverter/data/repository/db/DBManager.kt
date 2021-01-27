package reprator.currencyconverter.data.repository.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import reprator.currencyconverter.modals.ModalCurrency
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.PayPayResult
import reprator.paypay.base.useCases.Success
import reprator.paypay.base.util.safeApiCall
import javax.inject.Inject

interface DBManager {
    suspend fun saveCurrencyList(currencyList: List<ModalCurrency>): Flow<PayPayResult<List<Long>>>
    suspend fun currencyList(): Flow<PayPayResult<List<ModalCurrency>>>
    suspend fun deleteCurrencyList(): Flow<PayPayResult<Int>>

    suspend fun saveCurrencyExchangeRates(modalCurrencyRates: List<ModalCurrencyExchangeRates>): Flow<PayPayResult<List<Long>>>
    suspend fun getCurrencyExchangeRates(): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>>
    suspend fun deleteCurrencyExchangeRates(): Flow<PayPayResult<Int>>
}

class DBManagerImpl @Inject constructor(private val dbCurrencyDao: DBCurrencyDao) : DBManager {

    private suspend fun currencyListDB(): Flow<PayPayResult<List<ModalCurrency>>> {
        return flow {
            val data = dbCurrencyDao.getCurrencyList()
            if (data.isNullOrEmpty())
                emit(ErrorResult(message = "No Record"))
            else
                emit(Success(data))
        }
    }

    override suspend fun currencyList(): Flow<PayPayResult<List<ModalCurrency>>> =
        safeApiCall(call = { currencyListDB() })

    private suspend fun saveCurrencyListDB(currencyList: List<ModalCurrency>): Flow<PayPayResult<List<Long>>> {
        val longList = dbCurrencyDao.insertCurrencyList(currencyList)
        return flowOf(Success(longList))
    }

    override suspend fun saveCurrencyList(currencyList: List<ModalCurrency>) =
        safeApiCall(call = { saveCurrencyListDB(currencyList) })

    private suspend fun deleteCurrencyListDB(): Flow<PayPayResult<Int>> {
        val longList = dbCurrencyDao.deleteCurrencyList()
        return flowOf(Success(longList))
    }

    override suspend fun deleteCurrencyList(): Flow<PayPayResult<Int>> =
        safeApiCall(call = { deleteCurrencyListDB() })


    private suspend fun getCurrencyExchangeRateDB(): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>> {
        return flow {
            val data = dbCurrencyDao.getCurrencyExchangeItem()
            if (data.isNullOrEmpty())
                emit(ErrorResult(message = "No Record"))
            else
                emit(Success(data))
        }
    }

    override suspend fun getCurrencyExchangeRates(): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>> =
        safeApiCall(call = { getCurrencyExchangeRateDB() })

    private suspend fun saveCurrencyExchangeRatesDB(modalCurrencyRates: List<ModalCurrencyExchangeRates>): Flow<PayPayResult<List<Long>>> {
        val longList = dbCurrencyDao.insertCurrencyExchange(modalCurrencyRates)
        return flowOf(Success(longList))
    }

    override suspend fun saveCurrencyExchangeRates(modalCurrencyRates: List<ModalCurrencyExchangeRates>): Flow<PayPayResult<List<Long>>> =
        safeApiCall(call = { saveCurrencyExchangeRatesDB(modalCurrencyRates) })

    private suspend fun deleteCurrencyExchangeRatesDB(): Flow<PayPayResult<Int>> {
        val longList = dbCurrencyDao.deleteCurrencyExchangeItem()
        return flowOf(Success(longList))
    }

    override suspend fun deleteCurrencyExchangeRates(): Flow<PayPayResult<Int>> =
        safeApiCall(call = { deleteCurrencyExchangeRatesDB() })
}