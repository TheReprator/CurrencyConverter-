package reprator.currencyconverter.ui

import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import java.math.BigDecimal
import javax.inject.Inject

class FilterExchangedRates @Inject constructor() {
    operator fun invoke(
        quantity: String, selectedCurrency: String,
        modalCurrencyExchangeRatesList: List<ModalCurrencyExchangeRates>
    ): List<ModalCurrencyExchangeRates> {

        if (modalCurrencyExchangeRatesList.isNullOrEmpty())
            return emptyList()

        val rootCurrency = getRootCurrency(modalCurrencyExchangeRatesList) //i.e USD
        val selectedIndexRates = getSelectedCurrencyIndex(rootCurrency, selectedCurrency,
            modalCurrencyExchangeRatesList)                                 // i.e. index of USDINR

        val fromRate = modalCurrencyExchangeRatesList[selectedIndexRates].currencyValue.toFloat()

        return modalCurrencyExchangeRatesList.filterIndexed { index, _ ->
            index != selectedIndexRates
        }.map {

            val toRate = it.currencyValue.toFloat()

            val conversion = (quantity.toFloat() / fromRate.toDouble()) * toRate.toDouble()
            val roundedString = round(
                conversion
            ).toString()

            it.copy(
                currencyName = it.currencyName.removePrefix(rootCurrency),
                currencyValue = roundedString
            )
        }
    }

    private fun getRootCurrency(data: List<ModalCurrencyExchangeRates>): String {
        val first = data.first().currencyName
        val second = data.last().currencyName

        return second.commonPrefixWith(first)
    }

    private fun getSelectedCurrencyIndex(
        rootCurrency: String, selectedCurrency: String,
        data: List<ModalCurrencyExchangeRates>
    ): Int {

        val newSelectedCurrency = "$rootCurrency$selectedCurrency"

        return data.indexOfFirst {
            it.currencyName == newSelectedCurrency
        }
    }

    private fun round(numberToRound: Double, decimalPlaces: Int = 2): Float {
        try {
            val bd = BigDecimal(numberToRound.toString()).setScale(
                decimalPlaces,
                BigDecimal.ROUND_HALF_UP
            )
            return bd.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0f
    }
}