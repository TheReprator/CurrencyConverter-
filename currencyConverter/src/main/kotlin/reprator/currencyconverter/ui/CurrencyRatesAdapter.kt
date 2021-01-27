package reprator.currencyconverter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import reprator.currencyconverter.databinding.RowCurrencyratesBinding
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base_android.util.GeneralDiffUtil
import javax.inject.Inject

class CurrencyRatesAdapter @Inject constructor():
    ListAdapter<ModalCurrencyExchangeRates, VHCurrencyRates>(GeneralDiffUtil<ModalCurrencyExchangeRates>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHCurrencyRates {
        val binding = RowCurrencyratesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VHCurrencyRates(binding)
    }

    override fun onBindViewHolder(holder: VHCurrencyRates, position: Int) {
        holder.binding.exchangeRates = getItem(position)
        holder.binding.executePendingBindings()
    }
}

class VHCurrencyRates(val binding: RowCurrencyratesBinding) :
    RecyclerView.ViewHolder(binding.root)
