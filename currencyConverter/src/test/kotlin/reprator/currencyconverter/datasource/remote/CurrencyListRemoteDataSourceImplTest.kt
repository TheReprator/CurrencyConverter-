package reprator.currencyconverter.datasource.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import paypay.reprator.util.MainCoroutineRule
import reprator.currencyconverter.data.datasource.CurrencyListRemoteDataSource
import reprator.currencyconverter.datasource.remote.remoteMapper.CurrencyListMapper
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.useCases.Success
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CurrencyListRemoteDataSourceImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val mainCoroutineRule = MainCoroutineRule()

    var currencyApiService: CurrencyApiService = spyk<CurrencyApiService>()

    @SpyK
    var listMapper = CurrencyListMapper()

    lateinit var cut: CurrencyListRemoteDataSource

    @Before
    fun createService() {
        MockKAnnotations.init(this)
        cut = CurrencyListRemoteDataSourceImpl(currencyApiService, listMapper)
    }

    @Test
    fun `success with each concrete method behaviour for currencyList`() =
        mainCoroutineRule.runBlockingTest {
            val mapResponse = mapOf("USD" to "United States of America", "IND" to "India")
            val rawResponse = Response.success(mapResponse)

            val currencyListSuccess = listOf(
                ModalCurrency("USD", "United States of America"),
                ModalCurrency("IND", "India")
            )

            coEvery { listMapper.map(mapResponse) } returns currencyListSuccess

            coEvery {
                currencyApiService.currencyList()
            } returns rawResponse

            val resultRemoteResult = cut.currencyList().single()

            Truth.assertThat(resultRemoteResult is Success).isTrue()
            Truth.assertThat(resultRemoteResult.get()).isEqualTo(currencyListSuccess)
        }

    @Test
    fun successWithMockRemoteDataSourceForCurrencyList() = mainCoroutineRule.runBlockingTest {

        val cut = mockk<CurrencyListRemoteDataSource>(relaxed = true)
        val currencyListSuccess = listOf(
            ModalCurrency("USD", "United States of America"),
            ModalCurrency("IND", "India")
        )

        coEvery {
            cut.currencyList()
        } returns flow {
            emit(Success(currencyListSuccess))
        }

        val resultRemoteResult = cut.currencyList().single()

        Truth.assertThat(resultRemoteResult is Success).isTrue()
        Truth.assertThat(resultRemoteResult.get()).isEqualTo(currencyListSuccess)
    }

}
