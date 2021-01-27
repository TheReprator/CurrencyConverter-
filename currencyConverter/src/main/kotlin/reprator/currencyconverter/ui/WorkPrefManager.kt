package reprator.currencyconverter.ui

import android.content.SharedPreferences
import javax.inject.Inject

interface WorkPrefManager {
    var isFirstTimeLaunch: Boolean
}

class WorkPrefManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : WorkPrefManager {

    private val mEditor: SharedPreferences.Editor = sharedPreferences.edit()

    override var isFirstTimeLaunch: Boolean
        get() {
            return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, false)
        }
        set(isFirstTime) {
            mEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            mEditor.commit()
        }

    companion object {
        const val IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH"
    }

}