package br.com.zupacademy.caico.externalservices.bcb

import br.com.zupacademy.caico.externalservices.itau.AccountsResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.url}")
interface ClientBcb {

    @Post("/api/v1/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML])
    fun create(@Body request: CreateKeyBcbRequest): HttpResponse<CreateKeyBcbResponse>

    @Delete("/api/v1/pix/keys/{key}",
    produces = [MediaType.APPLICATION_XML],
    consumes = [MediaType.APPLICATION_XML])
    fun delete(@Body request: DeleteKeyBcbRequest, @PathVariable key: String): HttpResponse<DeleteKeyBcbResponse>
}