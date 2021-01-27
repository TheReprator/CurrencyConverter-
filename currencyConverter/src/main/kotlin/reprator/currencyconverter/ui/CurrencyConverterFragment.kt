package reprator.currencyconverter.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import reprator.currencyconverter.CurrencyConverterViewModel
import reprator.currencyconverter.R
import reprator.currencyconverter.databinding.FragmentCurrencyConverterBinding
import reprator.currencyconverter.workManager.RefreshWorkSetup
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyConverterFragment : Fragment(R.layout.fragment_currency_converter) {

    @Inject
    lateinit var currencyRatesAdapter: CurrencyRatesAdapter

    @Inject
    lateinit var workPrefManager: WorkPrefManager

    @Inject
    lateinit var refreshWorkSetup: RefreshWorkSetup

    private val viewModel: CurrencyConverterViewModel by viewModels()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private var _binding: FragmentCurrencyConverterBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPeriodicTask()

        _binding = FragmentCurrencyConverterBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.currencyViewModel = viewModel
            it.currencyRatesAdapter = currencyRatesAdapter
        }

        viewModel.getCurrencyListUseCase()
        viewModel.getCurrencyExchangeRateUseCase()

        initializeObserver()
        initializeRecycler()
    }

    private fun startPeriodicTask() {
        if (workPrefManager.isFirstTimeLaunch)
            return

        refreshWorkSetup.startRefreshPeriodicWork(requireActivity().applicationContext)
        workPrefManager.isFirstTimeLaunch = true
    }

    private fun initializeRecycler() {
        with(binding.currencyRecRates) {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(
                ItemOffsetDecoration(requireContext(), R.dimen.currencyrate_item_padding)
            )
        }
    }

    private fun initializeObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.currencyExchangeRatesManipulated.collect {
                currencyRatesAdapter.submitList(it)
            }
        }
    }
}