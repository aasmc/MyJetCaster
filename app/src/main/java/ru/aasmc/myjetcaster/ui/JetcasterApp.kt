package ru.aasmc.myjetcaster.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ru.aasmc.myjetcaster.R

@Composable
fun JetcasterApp() {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(checkIfOnline(context)) }
    if (isOnline) {
        // todo implement home screen
    } else {

    }
}

@Suppress("DEPRECATION")
private fun checkIfOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false

        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } else {
        cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}


@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.connection_error_title))},
        text ={ Text(text = stringResource(id = R.string.connection_error_message))},
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry_label))
            }
        }
    )
}


























