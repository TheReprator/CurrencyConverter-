package reprator.currencyconverter.ui.bindingAdapters

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import reprator.currencyconverter.R
import reprator.currencyconverter.modals.ModalCurrency
import reprator.currencyconverter.ui.CurrencyAdapter


@BindingAdapter(
    value = ["currencyValueView", "currencyList", "selectedCurrency",
        "selectedCurrencyAttrChanged"],
    requireAll = false
)
fun setCurrencyListAdapter(
    spinner: Spinner,
    currencyValue: EditText,
    currencyList: List<ModalCurrency>,
    selectedCurrencyPosition: Int = -1,
    listener: InverseBindingListener?
) {
    if (currencyList.isNullOrEmpty()) return

    if (null != spinner.adapter)
        return

    val adapter = CurrencyAdapter(spinner.context, R.layout.currency_spinner_item, currencyList)
    spinner.adapter = adapter

    val selectedPosition = if (-1 >= selectedCurrencyPosition) 0 else selectedCurrencyPosition

    setCurrentSelection(spinner, selectedPosition)
    setSpinnerListener(currencyValue, spinner, listener)
}

@InverseBindingAdapter(attribute = "selectedCurrency")
fun getCurrencySelectedPosition(spinner: Spinner): Int {
    return spinner.selectedItemPosition
}

private fun setSpinnerListener(
    currencyValue: EditText,
    spinner: Spinner,
    listener: InverseBindingListener?
) {
    val selectedItem = spinner.selectedItemPosition
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            val currency = currencyValue.text.toString()
            if (currency.isNullOrEmpty() || 0.0 == currency.toDouble()) {
                if (selectedItem != parent?.selectedItemPosition) {
                    setCurrentSelection(spinner, selectedItem)
                    showAlertError(spinner.context)
                }
            } else
                listener?.onChange()
        }

        override fun onNothingSelected(adapterView: AdapterView<*>) {
            listener?.onChange()
        }
    }
}

private fun setCurrentSelection(spinner: Spinner, selectedCurrencyPosition: Int) {
    spinner.setSelection(selectedCurrencyPosition)
}

private fun showAlertError(context: Context) {
    val dialogBuilder = AlertDialog.Builder(context)
    dialogBuilder
        .setMessage("Please enter currency value")
        .setPositiveButton("OK", null)
        .create()
        .show()
}