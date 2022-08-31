package com.example.smallgrpcapi.grpc.service

import com.example.smallgrpcapi.FindPersonRequest
import com.example.smallgrpcapi.PersonReply
import com.example.smallgrpcapi.PersonServiceGrpcKt
import com.example.smallgrpcapi.PersonServiceOuterClassBuilders.Person
import com.example.smallgrpcapi.PersonServiceOuterClassBuilders.PersonReply
import com.example.smallgrpcapi.database.PersonEntity
import com.example.smallgrpcapi.database.PersonEntityRepository
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PersonService(
    private val personEntityRepository: PersonEntityRepository
) : PersonServiceGrpcKt.PersonServiceCoroutineImplBase() {

    private val logger = LoggerFactory.getLogger(javaClass)
    override suspend fun findPerson(request: FindPersonRequest): PersonReply =
        PersonReply {
            // Logging something server side to test request logging
            logger.info("Requesting person {}", kv("personId", request.id))
            personEntityRepository.findByIdOrNull(request.id)?.toGrpc()?.let { this.person = it }
        }

    private fun PersonEntity.toGrpc() =
        this.let {
            Person {
                this.firstName = it.firstName
                this.lastName = it.lastName
            }
        }
}