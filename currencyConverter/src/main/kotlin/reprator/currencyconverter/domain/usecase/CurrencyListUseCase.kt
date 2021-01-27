package reprator.currencyconverter.domain.usecase

import kotlinx.coroutines.flow.Flow
import reprator.currencyconverter.domain.repository.CurrencyListRepository
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.useCases.PayPayResult
import timber.log.Timber
import javax.inject.Inject

class CurrencyListUseCase @Inject constructor(private val currencyListRepository: CurrencyListRepository) {

    suspend operator fun invoke(isFromWorkManager: Boolean = false): Flow<PayPayResult<List<ModalCurrency>>> {
        Timber.e("vikram2 $isFromWorkManager")
        return currencyListRepository.currencyList(isFromWorkManager)
    }
}
