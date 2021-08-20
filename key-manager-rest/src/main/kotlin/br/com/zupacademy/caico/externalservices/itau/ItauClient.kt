package br.com.zupacademy.caico.externalservices.itau

import br.com.zupacademy.caico.TypeAccount
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.contas.url}")
interface ItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun findAccounts(@PathVariable clienteId: String, @QueryValue("tipo") type: String): HttpResponse<AccountsResponse>
}