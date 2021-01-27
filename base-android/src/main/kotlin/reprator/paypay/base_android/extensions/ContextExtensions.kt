package reprator.paypay.base_android.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import android.provider.Settings
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import java.util.*

/**
 * Executes [block] on a [TypedArray] receiver. The [TypedArray] holds the attribute
 * values in [set] that are listed in [attrs]. In addition, if the given [AttributeSet]
 * specifies a style class (through the `style` attribute), that style will be applied
 * on top of the base attributes it defines.
 *
 * @param set The base set of attribute values.
 * @param attrs The desired attributes to be retrieved. These attribute IDs must be
 *              sorted in ascending order.
 * @param defStyleAttr An attribute in the current theme that contains a reference to
 *                     a style resource that supplies defaults values for the [TypedArray].
 *                     Can be 0 to not look for defaults.
 * @param defStyleRes A resource identifier of a style resource that supplies default values
 *                    for the [TypedArray], used only if [defStyleAttr] is 0 or can not be found
 *                     in the theme. Can be 0 to not look for defaults.
 *
 * @see Context.obtainStyledAttributes
 * @see android.content.res.Resources.Theme.obtainStyledAttributes
 */
inline fun Context.withStyledAttributes(
    set: AttributeSet? = null,
    attrs: IntArray =  intArrayOf(),
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    block: TypedArray.() -> Unit
) {
    val typedArray = obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes)
    try {
        typedArray.block()
    } finally {
        typedArray.recycle()
    }
}

inline fun <reified T : Context> Context.findBaseContext(): T? {
    var ctx: Context? = this
    do {
        if (ctx is T) {
            return ctx
        }
        if (ctx is ContextWrapper) {
            ctx = ctx.baseContext
        }
    } while (ctx != null)

    // If we reach here, there's not an Context of type T in our Context hierarchy
    return null
}

fun Activity.hideSoftInput() {
    val imm: InputMethodManager? = getSystemService()
    val currentFocus = currentFocus
    if (currentFocus != null && imm != null) {
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}

fun Fragment.hideSoftInput() = requireActivity().hideSoftInput()

fun Context.getStatusBarHeight(): Int {
    val resources = this.resources
    var statusBarHeight = 0

    val resourceId = resources.getIdentifier(
        "status_bar_height", "dimen", "android"
    )

    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }

    return statusBarHeight
}

fun Context.stringsArray(@ArrayRes msg: Int) = resources.getStringArray(msg)
fun Context.drawableArray(@ArrayRes msg: Int) = resources.obtainTypedArray(msg)

fun Context.getMergeStringResource(
    @StringRes stringResourceId: Int,
    @StringRes vararg stringArray: Int
): String {
    val totalStringArray = arrayOfNulls<String>(stringArray.size)
    for (i in stringArray.indices)
        totalStringArray[i] = getString(stringArray[i])

    return String.format(Locale.getDefault(), getString(stringResourceId), *totalStringArray)
}


fun Context.goToSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }.also { intent ->
        startActivity(intent)
    }
}

fun Context.color(@ColorRes color: Int) = ContextCompat.getColor(this, color)
fun Context.integer(@IntegerRes integer: Int) = resources.getInteger(integer)


fun Context.shortToast(msg: String) = Toast.makeText(
    this,
    msg,
    Toast.LENGTH_SHORT
).show()