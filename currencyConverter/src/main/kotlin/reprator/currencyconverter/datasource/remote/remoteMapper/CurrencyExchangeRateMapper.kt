package reprator.currencyconverter.datasource.remote.remoteMapper

import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.util.Mapper
import javax.inject.Inject

class CurrencyExchangeRateMapper @Inject constructor() :
    Mapper<Map<String, String>, List<ModalCurrencyExchangeRates>> {

    override suspend fun map(from: Map<String, String>) =
        from.toList().map { ModalCurrencyExchangeRates(it.first, it.second) }
}