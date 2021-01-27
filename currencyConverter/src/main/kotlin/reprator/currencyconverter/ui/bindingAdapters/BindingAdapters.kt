package reprator.currencyconverter.ui.bindingAdapters

import android.content.Context
import android.opengl.Visibility
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("goneUnless")
fun bindGoneUnless(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) {
        VISIBLE
    } else {
        GONE
    }
}

@BindingAdapter(
    value = ["isLoading", "isError"],
    requireAll = true
) fun bindErrorLoaderParent(view: View, isLoading: Boolean, isError: Boolean) {
    view.visibility = if (!isLoading && !isError) {
        GONE
    } else {
        VISIBLE
    }
}

@BindingAdapter("hideKeyboardOnInputDone")
fun hideKeyboardOnInputDone(view: EditText, enabled: Boolean) {
    if (!enabled) return
    val listener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            view.clearFocus()
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        false
    }
    view.setOnEditorActionListener(listener)
}


@BindingAdapter(value = ["currencyExchangeListAdapter"])
fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
    this.run {
        this.setHasFixedSize(true)
        this.adapter = adapter
    }
}