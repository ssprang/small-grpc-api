package com.example.smallgrpcapi.util

import com.example.smallgrpcapi.PersonServiceGrpc
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component

@Component
class GrpcClient(
    @Value("\${grpc.host:localhost}")
    private val grpcHost: String,

    @Value("\${grpc.port}")
    private val grpcPort: Int
) : SmartLifecycle {

    private var running = false
    private val channel = ManagedChannelBuilder
        .forAddress(grpcHost, grpcPort)
        .usePlaintext()
        .build()

    val personGrpcClient: PersonServiceGrpc.PersonServiceBlockingStub =
        PersonServiceGrpc.newBlockingStub(channel)

    override fun start() {
        running = true
    }

    override fun stop() {
        channel.shutdownNow()
        running = false
    }

    override fun isRunning() = running

    // Higher number starts later and stops earlier (starts after the server here)
    override fun getPhase() = Int.MAX_VALUE
}