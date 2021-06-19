package miranda.kmanage.grpc.zup.enum

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


@MicronautTest
internal class TestValidacaoEnumTipoChave {


    @Nested
    inner class ALEATORIA{
        /* 1-Happy Path ,2-COM valor */

        @Test
        fun deve_validar_com_a_chave_nula_ou_vazia(){
            with(TipoDeChave.ALEATORIO){
                assertTrue(valida(null))
                assertTrue(valida(""))
                assertFalse(valida("numero-aleatorio-de-chave"))
            }
        }

        @Test
        fun nao_deve_validar_chave_aleatoria_com_valor(){
            with(TipoDeChave.ALEATORIO){
                assertFalse(valida("valordechave"))
            }
        }

    }

    @Nested
    inner class CPF{
        /** 1-Happy Path , 2-Formato Invalido(nulo ,com ponto e vazio) */

        @Test
        fun deve_validar_valor_cpf(){
            with(TipoDeChave.CPF){
                assertTrue(valida("50897787013"))
                assertTrue(valida("74804221050"))
                assertTrue(valida("96002646000"))
            }
        }

        @Test
        fun nao_deve_validar_formato_invalido(){
            with(TipoDeChave.CPF){
                assertFalse(valida("508.977.870-13"))
                assertFalse(valida(" "))
                assertFalse(valida(null ))
                assertFalse(valida("7480422105078"))
                assertFalse(valida("xxxxxxxxx"))
            }
        }


    }

    @Nested
    inner class EMAIL{

        @Test
        fun deve_validar_valor_email(){
            with(TipoDeChave.EMAIL){
                assertTrue(valida("tomas@gmail.com"))
                assertTrue(valida("tomas@gmail.com.br"))
                assertTrue(valida("s@gmail.com"))
            }
        }


        @Test
        fun nao_deve_validar_valor_email(){
            with(TipoDeChave.EMAIL){
                assertFalse(valida("s@g"))
                assertFalse(valida("tomasgmail.com.br"))
                assertFalse(valida("gatmail.com"))
                assertFalse(valida(" "))
                assertFalse(valida(null))
            }

        }
    }

    @Nested
    inner class CELULAR {

        @Test
        fun deve_validar_o_valor_celular(){
            with(TipoDeChave.CELULAR){
                assertTrue(valida("+5519983588959"))
                assertTrue(valida("+3322977988555"))
            }
        }


        @Test
        fun nao_deve_validar_o_valor_celular(){
            with(TipoDeChave.CELULAR){
                assertFalse(valida("5519983588959"))
                assertFalse(valida("+552277988555"))
                assertFalse(valida(" "))
                assertFalse(valida(null))
            }
        }
    }


}