package ru.aasmc.myjetcaster.data

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import kotlin.coroutines.resumeWithException

/**
 * Suspending wrapper around an OkHttp [Call], using [Call.enqueue].
 *
 * Explanation of the suspendCancellableCoroutine:
 * https://medium.com/swlh/kotlin-coroutines-in-android-suspending-functions-8a2f980811f8
 */
suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
    enqueue(
        object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response) {
                    // If we have a response but we're cancelled while resuming, we need to
                    // close() the unused response
                    if (response.body != null) {
                        response.closeQuietly()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }
        }
    )

    continuation.invokeOnCancellation {
        try {
            cancel()
        } catch (t: Throwable) {
            // Ignore cancel exception
        }
    }
}