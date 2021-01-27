package reprator.currencyconverter.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import reprator.currencyconverter.data.datasource.CurrencyExchangeRateRemoteDataSource
import reprator.currencyconverter.data.datasource.CurrencyListRemoteDataSource
import reprator.currencyconverter.data.repository.CurrencyExchangeRateRepositoryImpl
import reprator.currencyconverter.data.repository.CurrencyListRepositoryImpl
import reprator.currencyconverter.data.repository.db.DBManager
import reprator.currencyconverter.datasource.remote.CurrencyApiService
import reprator.currencyconverter.datasource.remote.CurrencyExchangeRateRemoteDataSourceImpl
import reprator.currencyconverter.datasource.remote.CurrencyListRemoteDataSourceImpl
import reprator.currencyconverter.datasource.remote.remoteMapper.CurrencyExchangeRateMapper
import reprator.currencyconverter.datasource.remote.remoteMapper.CurrencyListMapper
import reprator.currencyconverter.domain.repository.CurrencyExchangeRateRepository
import reprator.currencyconverter.domain.repository.CurrencyListRepository
import reprator.currencyconverter.domain.usecase.CurrencyExchangeRateUseCase
import reprator.currencyconverter.domain.usecase.CurrencyListUseCase
import reprator.currencyconverter.ui.WorkPrefManager
import reprator.currencyconverter.ui.WorkPrefManagerImpl
import reprator.currencyconverter.workManager.RefreshWorkSetup
import reprator.currencyconverter.workManager.RefreshWorkSetupImpl
import reprator.paypay.base.util.ConnectionDetector
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
class CurrencyConverterModule {

    @Provides
    fun provideCurrencyListRemoteDataSource(
        currencyApiService: CurrencyApiService,
        currencyListMapper: CurrencyListMapper
    ): CurrencyListRemoteDataSource {
        return CurrencyListRemoteDataSourceImpl(
            currencyApiService,
            currencyListMapper
        )
    }

    @Provides
    fun provideCurrencyExchangeRateRemoteDataSource(
        currencyApiService: CurrencyApiService, currencyListMapper: CurrencyExchangeRateMapper
    ): CurrencyExchangeRateRemoteDataSource {
        return CurrencyExchangeRateRemoteDataSourceImpl(
            currencyApiService,
            currencyListMapper
        )
    }

    @Provides
    fun provideCurrencyListRepository(
        currencyListRemoteDataSource: CurrencyListRemoteDataSource,
        connectionDetector: ConnectionDetector,
        coroutineScope: CoroutineScope,
        dbManager: DBManager
    ): CurrencyListRepository {
        return CurrencyListRepositoryImpl(
            currencyListRemoteDataSource,
            dbManager, coroutineScope,
            connectionDetector
        )
    }

    @Provides
    fun provideCurrencyExchangeRateRepository(
        currencyExchangeRateRemoteDataSource: CurrencyExchangeRateRemoteDataSource,
        connectionDetector: ConnectionDetector,
        coroutineScope: CoroutineScope,
        dbManager: DBManager
    ): CurrencyExchangeRateRepository {
        return CurrencyExchangeRateRepositoryImpl(
            currencyExchangeRateRemoteDataSource,
            dbManager,
            coroutineScope,
            connectionDetector
        )
    }

    @Provides
    fun provideCurrencyListUseCase(
        currencyListRepository: CurrencyListRepository
    ): CurrencyListUseCase {
        return CurrencyListUseCase(currencyListRepository)
    }

    @Provides
    fun provideCurrencyExchangeRateUseCase(
        currencyExchangeRateRepository: CurrencyExchangeRateRepository
    ): CurrencyExchangeRateUseCase {
        return CurrencyExchangeRateUseCase(currencyExchangeRateRepository)
    }

    @Provides
    fun provideCurrencyApiService(
        retrofit: Retrofit
    ): CurrencyApiService {
        return retrofit
            .create(CurrencyApiService::class.java)
    }

    @Provides
    fun provideWorkPrefManager(
        sharedPreferences: SharedPreferences
    ): WorkPrefManager {
        return WorkPrefManagerImpl(sharedPreferences)
    }

    @Provides
    fun provideRefreshWorkSetup(): RefreshWorkSetup {
        return RefreshWorkSetupImpl()
    }
}