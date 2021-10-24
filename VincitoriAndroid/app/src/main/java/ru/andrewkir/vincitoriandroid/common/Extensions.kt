package ru.andrewkir.vincitoriandroid.common

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import ru.andrewkir.vincitoriandroid.web.model.ApiResponse
import ru.andrewkir.vincitoriandroid.R

fun <activity : Activity> Activity.startActivityClearBackStack(activityClass: Class<activity>) {
    Intent(this, activityClass).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun Fragment.openLink(link: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    this.startActivity(browserIntent)
}

fun Fragment.handleApiError(
    error: ApiResponse.OnErrorResponse,
    retry: (() -> Unit)? = null
) {
    if (error.isNetworkFailure) {
        requireView().createRetrySnackbar(
            "R.string.check_internet", //TODO
            retry
        )
        return
    }

    if (error.body == null && error.code == null) {
        requireView().createRetrySnackbar("getString(R.string.server_error)")//TODO
    }

    val parsedError = try {
        if (error.body == null) ""
        else {
            val jsonObj = JSONObject(error.body.string())
            jsonObj.getString("error")
        }
    } catch (ex: JSONException) {
        ""
    }

    if (error.code == 401) {
            Toast.makeText(requireContext(), "getString(R.string.error_retry_signin)", Toast.LENGTH_SHORT).show()
        return
    }

    if (parsedError.isNotEmpty()) requireView().createRetrySnackbar(parsedError)
    else requireView().createRetrySnackbar("getString(R.string.error_server_error)")
}

fun View.createRetrySnackbar(msg: String, retry: (() -> Unit)? = null) {
    val snack = Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
    retry?.let {
        snack.setAction("context.getString(R.string.error_retry_text)") {
            it()
        }
    }
    snack.show()
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()