package com.example.smallgrpcapi.database

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonEntityRepository : CrudRepository<PersonEntity, Int>