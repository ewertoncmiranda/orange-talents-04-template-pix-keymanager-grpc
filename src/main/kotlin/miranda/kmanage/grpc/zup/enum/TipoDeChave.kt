package miranda.kmanage.grpc.zup.enum
import br.com.caelum.stella.validation.CPFValidator
import io.micronaut.validation.validator.constraints.EmailValidator
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.KeyType
import javax.validation.constraints.Email

enum class TipoDeChave {

    CPF {
    override fun valida(chave: String?): Boolean {
       if(chave.isNullOrBlank()){ return false  }
       if(!chave.matches("^\\d{11}\$".toRegex())) { return false }
        return CPFValidator().run {
            assertValid(chave)
            isEligible(chave)
    }   }
    override fun toBcbType(): KeyType {
        return  KeyType.CPF
      }
    },
    CELULAR {
    override fun valida(chave: String?): Boolean {
        if(chave.isNullOrBlank()){ return false}
        return chave.matches("^\\+[1-9][0-9]\\d{11}\$".toRegex())
    }
    override fun toBcbType(): KeyType {
         return  KeyType.PHONE
       }
    },

    EMAIL {
    override fun valida(chave: String?): Boolean {
        if(chave.isNullOrBlank()){ return false}
        if(!chave.matches("^([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+\$".toRegex())){return false}
        return true
    }
    override fun toBcbType(): KeyType {
        return  KeyType.EMAIL
      }
    },

    ALEATORIO {
    override fun valida(chave: String?): Boolean = chave.isNullOrBlank()
    override fun toBcbType(): KeyType {
        return  KeyType.RANDOM
      }
    } ;

abstract fun valida(chave:String?):Boolean

abstract fun toBcbType():KeyType
}