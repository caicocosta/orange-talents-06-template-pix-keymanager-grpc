package br.com.zupacademy.caico.registerkeys

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface KeyRepository : JpaRepository<PixKeys, UUID> {
    fun existsByKey(key: String): Boolean
    fun findByIdAndClientId(pixId: UUID, clientId: UUID): Optional<PixKeys>
    fun findByKey(key: String): Optional<PixKeys>
}
