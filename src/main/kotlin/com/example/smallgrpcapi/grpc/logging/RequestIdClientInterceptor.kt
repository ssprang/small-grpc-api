package com.example.smallgrpcapi.grpc.logging

import com.example.smallgrpcapi.grpc.logging.Constants.REQUEST_ID_METADATA_KEY
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import java.util.UUID

/**
 * Set the requestId for the current context in the gRPC metadata of the call.
 */
class RequestIdClientInterceptor(
    private val requestIdSupplier: RequestIdSupplier
) : ClientInterceptor {

    override fun <ReqT, RespT> interceptCall(
        methodDescriptor: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        channel: Channel
    ): ClientCall<ReqT, RespT> {
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            channel.newCall(
                methodDescriptor,
                callOptions
            )
        ) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {

                requestIdSupplier.getRequestId()?.let { requestId ->
                    headers.put(REQUEST_ID_METADATA_KEY, requestId.toString())
                }
                super.start(responseListener, headers)
            }
        }
    }

    interface RequestIdSupplier {
        fun getRequestId(): UUID?
    }
}