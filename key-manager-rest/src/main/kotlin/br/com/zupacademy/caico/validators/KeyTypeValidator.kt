package br.com.zupacademy.caico.validators

import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.exceptionsmodels.InvalidFormat
import javax.inject.Singleton

@Singleton
class KeyTypeValidator {

    fun isValidFormat(type: TypeKey, key: String) : Boolean{
        when (type) {
            TypeKey.DOCUMENT -> if(!key.matches("^([0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}|[0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}\\/?[0-9]{4}\\-?[0-9]{2})\$".toRegex())){
                return false
            }
            TypeKey.EMAIL -> if(!key.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})\$".toRegex())){
                return false
            }
            TypeKey.CELPHONE -> if(!key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())){
                return false
            }
            else -> throw InvalidFormat("Tipo de chave desconhecido")
        }
        return true
    }
}