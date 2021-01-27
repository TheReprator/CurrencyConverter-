package reprator.currencyconverter.datasource.remote.remoteMapper

import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.util.Mapper
import javax.inject.Inject

class CurrencyListMapper @Inject constructor() : Mapper<Map<String, String>, List<ModalCurrency>> {

    override suspend fun map(from: Map<String, String>): List<ModalCurrency> =
        from.toList().map { ModalCurrency(it.first, it.second) }
}