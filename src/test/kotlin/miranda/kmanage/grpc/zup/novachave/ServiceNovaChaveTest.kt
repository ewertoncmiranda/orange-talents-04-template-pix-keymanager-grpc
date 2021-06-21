package miranda.kmanage.grpc.zup.novachave

import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import miranda.kmanage.grpc.zup.NovaChaveResponse
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.conta.ContaDoBancoRepositorio
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import miranda.kmanage.grpc.zup.sistemasexternos.datatransfermodel.ContaCompletaResponse
import miranda.kmanage.grpc.zup.sistemasexternos.datatransfermodel.InstituicaoResponse
import miranda.kmanage.grpc.zup.sistemasexternos.datatransfermodel.TitularResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject

@MicronautTest
internal class ServiceNovaChaveTest(val service: NovaChaveService,val contaDoBancoRepositorio: ContaDoBancoRepositorio){

    @Inject
    lateinit var itauBaseDeDados : ItauBaseDeDados

    @Test
    fun deve_retornar_string_tipo_conta(){
       service.verificaTipoDaConta(TipoDaConta.CONTA_CORRENTE).let{
           assertEquals("CONTA_CORRENTE",it);
           assertNotNull(it)
       }
    }


    @Test
    fun deve_retornar_nova_chave_response() {
        val chave = novaChavePix()
        Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(chave.clienteId.toString(),"CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(ContaCompletaResponse("CONTA_CORRENTE"
                                            ,"0001"
                                            ,"12345678"
                                            ,TitularResponse("1bb194-3c52-4e67-8c35-a93c0af9284f","Yuri Matheus" ,"06628726061" )
                                            ,InstituicaoResponse("ITAU UNIBANCO S.A" ,"60701190") ) ))

        val response = service.cadastrar(novaChavePix())

        assertNotNull(response)
        Assertions.assertEquals(NovaChaveResponse::class.java,response::class.java)
    }

    fun novaChavePix(): NovaChavePix {
        return NovaChavePix(
            "bc35591d-b547-4151-a325-4a9d2cd19614",
            TipoDeChave.CPF,
            "43068789885",
            TipoDaConta.CONTA_CORRENTE
        )
    }

    @MockBean(ItauBaseDeDados::class)
    fun mockItauBaseDeDados(): ItauBaseDeDados?{
        return Mockito.mock(ItauBaseDeDados::class.java)
    }

}