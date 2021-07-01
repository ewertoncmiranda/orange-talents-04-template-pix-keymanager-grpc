package miranda.kmanage.grpc.zup.deletarchave

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import miranda.kmanage.grpc.zup.DeletaChaveRequester
import miranda.kmanage.grpc.zup.DeletarChaveServiceGrpc
import miranda.kmanage.grpc.zup.NovaChaveRequester
import miranda.kmanage.grpc.zup.PixKeymanagerServiceGrpc
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import miranda.kmanage.grpc.zup.sistemasexternos.SistemaBancoCentralBrasil
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.*
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
@MicronautTest(transactional = false)
class DeletarChaveTests (
            val grpc:DeletarChaveServiceGrpc.DeletarChaveServiceBlockingStub,
            val repositorio: ChavePixRepositorio
            ){

    /*Variaveis e Funções de Mockagem*/
    @field:Inject
    lateinit var bcbCliente: SistemaBancoCentralBrasil

    @MockBean(SistemaBancoCentralBrasil::class)
    fun bcbClient(): SistemaBancoCentralBrasil?{
        return Mockito.mock(SistemaBancoCentralBrasil::class.java)
    }

    @field:Inject
    lateinit var itauBaseDeDados: ItauBaseDeDados

    @MockBean(ItauBaseDeDados::class)
    fun itauBaseDeDados(): ItauBaseDeDados?{
        return Mockito.mock(ItauBaseDeDados::class.java)
    }

    /*Configurações do JUNIT*/

    @AfterEach
    fun cleanUp(){ repositorio.deleteAll()   }

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repositorio.save(ChavePix(
            tipoDaChave = TipoDeChave.EMAIL,
            chave = "rponte@gmail.com",
            clientId = UUID.randomUUID().toString(),
            tipoConta = TipoDaConta.CONTA_CORRENTE,
            conta = ContaDoBanco("","","","","02467781054")
        ))
    }

    /*Factory para acesso dos services do GRPC*/
    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME)channel: ManagedChannel):
                                        DeletarChaveServiceGrpc.DeletarChaveServiceBlockingStub{
         return DeletarChaveServiceGrpc.newBlockingStub(channel) }
        }

    /*Testes*/
    @Test
    fun deve_excluir_uma_chave_existente(){

        Mockito.`when`(bcbCliente.delete(deletePixKeyRequest(),CHAVE_EXISTENTE.chave))
            .thenReturn(HttpResponse.ok(""))
        val response = grpc.exclui(DeletaChaveRequester.newBuilder()
                        .setClienteId(CHAVE_EXISTENTE.clientId.toString())
                        .setIdPix(CHAVE_EXISTENTE.id!!).build())

        Assertions.assertTrue(response.deletado)
        Assertions.assertEquals(true,response.deletado)
        Assertions.assertNotNull(response.deletado)

    }

    @Test
    fun nao_deve_excluir_quando_bcb_lancar_erro(){

        Mockito.`when`(bcbCliente.cadastrar(createPixKeyRequester()))
            .thenReturn(HttpResponse.badRequest())

        val response = assertThrows<StatusRuntimeException> {grpc.exclui(DeletaChaveRequester.newBuilder()
                                .setClienteId(CHAVE_EXISTENTE.clientId.toString())
                                .setIdPix(CHAVE_EXISTENTE.id!!).build())
        }

        with(response){
            Assertions.assertEquals(Status.INTERNAL.code,response.status.code)
        }
    }

    @Test
    fun nao_deve_excluir_quando_chave_nao_existe(){
        val response = assertThrows<StatusRuntimeException> {grpc.exclui(DeletaChaveRequester.newBuilder()
            .setClienteId(CHAVE_EXISTENTE.clientId.toString())
            .setIdPix(2L).build())
        }

        with(response){
            Assertions.assertEquals(Status.NOT_FOUND.code,response.status.code)
        }
    }

    @Test
    fun nao_deve_excluir_se_chaveexiste_mas_nao_pertence_cliente(){
        repositorio.save(ChavePix(
            tipoDaChave = TipoDeChave.EMAIL,
            chave = "tomas_bola@gmail.com",
            clientId = UUID.randomUUID().toString(),
            tipoConta = TipoDaConta.CONTA_CORRENTE,
            conta = ContaDoBanco("","","","","07326021066")
        ))

        val response = assertThrows<StatusRuntimeException> {
             grpc.exclui(DeletaChaveRequester.newBuilder()
                            .setClienteId("tomas_bola@gmail.com")
                            .setIdPix(1L).build())
        }

        with(response){
            Assertions.assertEquals(Status.FAILED_PRECONDITION.code,response.status.code)
        }
    }



    /*Funções que retornam objetos para serem usados nos testes*/
    fun createPixKeyRequester(): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            KeyType.CPF,"02467781054",
            Owner(Type.NATURAL_PERSON,"Rafael M C Ponte","02467781054"),
            BankAccount("60701190","0001","291900", AccountType.CACC)
        )
    }

    fun deletePixKeyRequest():DeletePixKeyRequest{
        return DeletePixKeyRequest(CHAVE_EXISTENTE.chave,"60701190");
    }

}