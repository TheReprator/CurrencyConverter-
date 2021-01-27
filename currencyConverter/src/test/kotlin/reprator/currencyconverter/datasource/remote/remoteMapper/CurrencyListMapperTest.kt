package reprator.currencyconverter.datasource.remote.remoteMapper

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.util.Mapper

@RunWith(JUnit4::class)
class CurrencyListMapperTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mapper: Mapper<Map<String, String>, List<ModalCurrency>>

    @Before
    fun setUp() {
        mapper = CurrencyListMapper()
    }

    @Test
    fun `create the map into Currency list pojo class`() = runBlockingTest {
        // Given
        val currencyList_given = mapOf<String, String>("USD" to "United States Of America",
            "INR" to "India")

        val currencyList_expected = listOf(
            ModalCurrency("USD", "United States Of America"),
            ModalCurrency("INR", "India")
        )

        //When
        val actual = mapper.map(currencyList_given)

        // Then
        Truth.assertThat(currencyList_expected).isEqualTo(actual)
    }

}