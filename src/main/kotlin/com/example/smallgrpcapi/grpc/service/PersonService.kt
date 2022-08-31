package com.example.smallgrpcapi.grpc.service

import com.example.smallgrpcapi.FindPersonRequest
import com.example.smallgrpcapi.PersonReply
import com.example.smallgrpcapi.PersonServiceGrpcKt
import com.example.smallgrpcapi.PersonServiceOuterClassBuilders.Person
import com.example.smallgrpcapi.PersonServiceOuterClassBuilders.PersonReply
import com.example.smallgrpcapi.database.PersonEntity
import com.example.smallgrpcapi.database.PersonEntityRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PersonService(
    private val personEntityRepository: PersonEntityRepository
) : PersonServiceGrpcKt.PersonServiceCoroutineImplBase() {
    override suspend fun findPerson(request: FindPersonRequest): PersonReply =
        PersonReply {
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