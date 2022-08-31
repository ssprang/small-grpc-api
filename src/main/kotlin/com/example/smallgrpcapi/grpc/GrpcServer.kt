package com.example.smallgrpcapi.grpc

import com.example.smallgrpcapi.grpc.service.PersonService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.health.v1.HealthCheckResponse
import io.grpc.protobuf.services.HealthStatusManager
import io.grpc.protobuf.services.ProtoReflectionService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class GrpcService(
    private val grpcServer: GrpcServer
) : SmartLifecycle {

    private var running = false

    override fun start() {
        grpcServer.start()
        grpcServer.setToServing()
        running = true
    }

    override fun stop() {
        grpcServer.stop()
        running = false
    }

    override fun isRunning() = running

    // Higher number starts later and stops earlier
    override fun getPhase() = Int.MAX_VALUE - 1
}

@Component
class GrpcServer(
    personService: PersonService,
    @Value("\${grpc.port}") private val grpcPort: Int,
    @Value("\${grpc.secondsBeforeHardShutdown}") private val secondsBeforeHardShutdown: Int
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val manager: HealthStatusManager = HealthStatusManager()
    private val server: Server = ServerBuilder
        .forPort(grpcPort)
        .executor(Executors.newCachedThreadPool())
        // For health checks
        .addService(manager.healthService)
        // For introspection using grpcurl
        .addService(ProtoReflectionService.newInstance())
        // The grpc api
        .addService(personService)
        .build()

    fun start() {
        manager.setStatus(
            HealthStatusManager.SERVICE_NAME_ALL_SERVICES,
            HealthCheckResponse.ServingStatus.NOT_SERVING
        )

        server.start()
        logger.info("Grpc server started on port $grpcPort.")
    }

    fun setToServing() {
        manager.setStatus(
            HealthStatusManager.SERVICE_NAME_ALL_SERVICES,
            HealthCheckResponse.ServingStatus.SERVING
        )
        logger.info("Grpc server status set to serving.")
    }

    fun stop() {
        logger.info("Grpc server shutting down.")
        manager.setStatus(
            HealthStatusManager.SERVICE_NAME_ALL_SERVICES,
            HealthCheckResponse.ServingStatus.NOT_SERVING
        )
        server.shutdown()
        // Give clients time to finish requests.
        server.awaitTermination(secondsBeforeHardShutdown.toLong(), TimeUnit.SECONDS)
        server.shutdownNow()
        logger.info("Grpc server stopped.")
    }
}