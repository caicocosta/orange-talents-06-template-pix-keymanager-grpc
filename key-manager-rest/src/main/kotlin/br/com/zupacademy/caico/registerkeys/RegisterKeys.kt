package br.com.zupacademy.caico.registerkeys

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/registerkeys")
class RegisterKeys {

    @Post
    fun register(@Body request: RegisterKeyRequest){

    }
}