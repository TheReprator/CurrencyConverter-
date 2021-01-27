package reprator.currencyconverter.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import reprator.currencyconverter.modals.ModalCurrency

public class CurrencyAdapter(
    context: Context,
    private  val textViewResourceId: Int,
    private val values: List<ModalCurrency>
) : ArrayAdapter<ModalCurrency>(context, textViewResourceId, values) {

    override fun getCount() = values.size
    override fun getItem(position: Int) = values[position]
    override fun getItemId(position: Int) = position.toLong()

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label: TextView = LayoutInflater.from(parent.context).inflate(textViewResourceId, parent, false) as TextView
        label.text = values[position].countryCode
        return label
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }
}