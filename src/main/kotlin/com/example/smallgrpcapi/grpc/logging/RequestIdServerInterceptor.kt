package com.example.smallgrpcapi.grpc.logging

import com.example.smallgrpcapi.grpc.logging.Constants.REQUEST_ID_METADATA_KEY
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.kotlin.CoroutineContextServerInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.MDC
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Set the requestId for the current context if present in the gRPC metadata of the call
 * and add it to MDC, the later allows for logging this on all log entries.
 */
class RequestIdServerInterceptor(
    private val context: CoroutineContext = EmptyCoroutineContext
) : CoroutineContextServerInterceptor() {

    override fun coroutineContext(
        call: ServerCall<*, *>,
        headers: Metadata
    ): CoroutineContext {
        val requestId = headers.get(REQUEST_ID_METADATA_KEY)

        try {
            MDC.put(LOGGING_KEY, requestId)
            return Dispatchers.Default + MDCContext() + context
        } finally {
            // MDC must be cleared between requests
            MDC.remove(LOGGING_KEY)
        }
    }

    companion object {
        const val LOGGING_KEY = "requestId"
    }
}
