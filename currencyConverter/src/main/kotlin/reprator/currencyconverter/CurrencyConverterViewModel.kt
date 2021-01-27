package reprator.currencyconverter

import androidx.annotation.VisibleForTesting
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import reprator.currencyconverter.domain.usecase.CurrencyExchangeRateUseCase
import reprator.currencyconverter.domain.usecase.CurrencyListUseCase
import reprator.currencyconverter.modals.ModalCurrency
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.currencyconverter.ui.FilterExchangedRates
import reprator.paypay.base.extensions.computationalBlock
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.Success
import reprator.paypay.base.util.AppCoroutineDispatchers

class CurrencyConverterViewModel @ViewModelInject constructor(
    private val coroutineDispatcherProvider: AppCoroutineDispatchers,
    private val currencyListUseCase: CurrencyListUseCase,
    private val currencyExchangeRateUseCase: CurrencyExchangeRateUseCase,
    private val filterExchangedRates: FilterExchangedRates
) : ViewModel() {

    private val _isLoadingCurrencyList = MutableStateFlow(true)
    val isLoadingCurrencyList: StateFlow<Boolean> = _isLoadingCurrencyList

    private val _errorMsgCurrencyList = MutableStateFlow("")
    val errorMsgCurrencyList: StateFlow<String> = _errorMsgCurrencyList

    private val _isLoadingCurrencyRates = MutableStateFlow(true)
    val isLoadingCurrencyRates: StateFlow<Boolean> = _isLoadingCurrencyRates

    private val _errorRateMsg = MutableStateFlow("")
    val errorRateMsg: StateFlow<String> = _errorRateMsg

    val selectedCurrencyPosition = MutableLiveData(-1)

    @VisibleForTesting
    val _currencyList =
        MutableStateFlow(emptyList<ModalCurrency>())
    val currencyList: StateFlow<List<ModalCurrency>> = _currencyList

    @VisibleForTesting
    val _currencyExchangeRates =
        MutableStateFlow(emptyList<ModalCurrencyExchangeRates>())

    private val _currencyExchangeRatesManipulated =
        MutableStateFlow(emptyList<ModalCurrencyExchangeRates>())
    val currencyExchangeRatesManipulated: StateFlow<List<ModalCurrencyExchangeRates>> =
        _currencyExchangeRatesManipulated

    val currencyValue = MutableLiveData<String>("1")

    init {
        executeFilterUseCase()
    }

    fun getCurrencyListUseCase() {
        computationalBlock {
            currencyListUseCase().flowOn(coroutineDispatcherProvider.io).catch { e ->
                _errorMsgCurrencyList.value = e.localizedMessage
            }.onStart {
                _isLoadingCurrencyList.value = true
            }.onCompletion {
                _isLoadingCurrencyList.value = false
            }.flowOn(coroutineDispatcherProvider.main)
                .collect {
                    withContext(coroutineDispatcherProvider.main) {
                        when (it) {
                            is Success -> {
                                _currencyList.value = it.data
                            }
                            is ErrorResult -> {
                                _errorMsgCurrencyList.value = it.message!!
                            }
                        }
                    }
                }
        }
    }

    fun getCurrencyExchangeRateUseCase() {
        computationalBlock {
            currencyExchangeRateUseCase().flowOn(coroutineDispatcherProvider.io)
                .catch { e ->
                    _errorRateMsg.value = e.localizedMessage
                }.onStart {
                    _isLoadingCurrencyRates.value = true
                }.onCompletion {
                    _isLoadingCurrencyRates.value = false
                }.flowOn(coroutineDispatcherProvider.main)
                .collect {
                    withContext(coroutineDispatcherProvider.main) {
                        when (it) {
                            is Success -> {
                                _currencyExchangeRates.value = it.data
                                filterData()
                            }
                            is ErrorResult -> {
                                _errorRateMsg.value = it.message!!
                            }
                        }
                    }
                }
        }
    }

    fun retryCurrencyList() {
        _errorMsgCurrencyList.value = ""
        getCurrencyListUseCase()
    }

    fun retryExchangeRates() {
        _errorRateMsg.value = ""
        getCurrencyExchangeRateUseCase()
    }

    private fun executeFilterUseCase() {

        MediatorLiveData<Boolean>().apply {
            addSource(selectedCurrencyPosition.distinctUntilChanged()) {
                filterData()
            }

            addSource(currencyValue) {
                filterData()
            }
        }.observeForever {
        }
    }

    @VisibleForTesting
    fun filterData() {
        computationalBlock {

            if (currencyValue.value.isNullOrEmpty() || 0.0 == currencyValue.value!!.toDouble()) {
                _currencyExchangeRatesManipulated.value = emptyList()
                return@computationalBlock
            }

            if (-1 >= selectedCurrencyPosition.value!!)
                return@computationalBlock

            val data = filterExchangedRates(
                currencyValue.value!!,
                currencyList.value[selectedCurrencyPosition.value!!].countryCode,
                _currencyExchangeRates.value
            )

            withContext(coroutineDispatcherProvider.main) {
                _currencyExchangeRatesManipulated.value = data
            }
        }
    }

    private fun computationalBlock(
        coroutineExceptionHandler: CoroutineExceptionHandler? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.computationalBlock(
            coroutineDispatcherProvider,
            coroutineExceptionHandler,
            block
        )
    }
}