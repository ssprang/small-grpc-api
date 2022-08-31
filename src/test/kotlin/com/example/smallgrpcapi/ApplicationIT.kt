package com.example.smallgrpcapi

import com.example.smallgrpcapi.PersonServiceOuterClassBuilders.FindPersonRequest
import com.example.smallgrpcapi.PersonServiceOuterClassBuilders.Person
import com.example.smallgrpcapi.util.GrpcClient
import com.example.smallgrpcapi.util.IntegrationTestWithDB
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTestWithDB
class ApplicationIT {

    @Autowired
    private lateinit var grpcClient: GrpcClient

    @Test
    fun `request existing person`() {
        val result = grpcClient.personGrpcClient.findPerson(FindPersonRequest {
            id = 1
        })
        assertThat(result.hasPerson()).isTrue
        assertThat(result.person).isEqualTo(Person {
            firstName = "Anna"
            lastName = "Andersson"
        })
    }

    @Test
    fun `request non-existing person`() {
        val result = grpcClient.personGrpcClient.findPerson(FindPersonRequest {
            id = 12
        })
        assertThat(result.hasPerson()).isFalse
    }
}

