package reprator.currencyconverter.domain.usecase

import kotlinx.coroutines.flow.Flow
import reprator.currencyconverter.domain.repository.CurrencyExchangeRateRepository
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.PayPayResult
import timber.log.Timber
import javax.inject.Inject

class CurrencyExchangeRateUseCase @Inject constructor(
    private val currencyExchangeRateRepository: CurrencyExchangeRateRepository
) {
    suspend operator fun invoke(isFromWorkManager: Boolean = false): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>> {
        Timber.e("vikram3 $isFromWorkManager")
        return currencyExchangeRateRepository.getCurrencyExchangeRates(isFromWorkManager)
    }
}
