package com.example.smallgrpcapi

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.example.smallgrpcapi.PersonServiceOuterClassBuilders.FindPersonRequest
import com.example.smallgrpcapi.PersonServiceOuterClassBuilders.Person
import com.example.smallgrpcapi.util.GrpcClient
import com.example.smallgrpcapi.util.IntegrationTestWithDB
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTestWithDB
class ApplicationIT {

    @Autowired
    private lateinit var grpcClient: GrpcClient

    private lateinit var listAppender: ListAppender<ILoggingEvent>
    private val logger =
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger

    @BeforeEach
    fun setup() {
        listAppender = ListAppender<ILoggingEvent>()
        logger.addAppender(listAppender)
        listAppender.start()
    }

    @AfterEach
    fun tearDown() {
        logger.detachAppender(listAppender)
        listAppender.stop()
    }

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

    @Test
    fun `verify requestId in log`() {
        // Do something that will generate logs
        grpcClient.personGrpcClient.findPerson(FindPersonRequest { id = 1 })
        grpcClient.personGrpcClient.findPerson(FindPersonRequest { id = 12 })

        /*
        Asserting that loggedInUserId is in the MDC
        (copying list to avoid ConcurrentModificationException)
         */
        val loggedEvents =
            listAppender.list.toMutableList().filter { it.message.contains("Requesting person") }

        assertThat(loggedEvents).hasSize(2)
        val firstRequestId = loggedEvents[0].mdcPropertyMap.getOrDefault("requestId", "")
        val secondRequestId = loggedEvents[1].mdcPropertyMap.getOrDefault("requestId", "")
        assertThat(firstRequestId).isNotEmpty
        assertThat(secondRequestId).isNotEmpty
        assertThat(firstRequestId).isNotEqualTo(secondRequestId)
    }
}

