package miranda.kmanage.grpc.zup.listachaves

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import miranda.kmanage.grpc.zup.ListaChavesPixRequest
import miranda.kmanage.grpc.zup.ListaTodasChavesClientServiceGrpc
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import org.junit.jupiter.api.*
import java.lang.AssertionError

@MicronautTest(transactional = false)
class ListaChavesTests (
             val grpc: ListaTodasChavesClientServiceGrpc.ListaTodasChavesClientServiceBlockingStub,
             val repositorio: ChavePixRepositorio
            ){

    /**/

    /*Criação de classe Factory*/
    @Factory
    class Client{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME)channel: ManagedChannel)
                     :ListaTodasChavesClientServiceGrpc.ListaTodasChavesClientServiceBlockingStub{
            return ListaTodasChavesClientServiceGrpc.newBlockingStub(channel)
        }
    }

    /*Configuração dos Testes-JUnit*/

    private lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repositorio.save(
            ChavePix(
                tipoDaChave = TipoDeChave.EMAIL,
                chave = "rponte@gmail.com",
                clientId = "2ac09233-21b2-4276-84fb-d83dbd9f8bab",
                tipoConta = TipoDaConta.CONTA_CORRENTE,
                conta = ContaDoBanco("ITAÚ UNIBANCO S.A.",
                    "Cassio Almeida",
                    "0001",
                    "084329",
                    "02467781054")
            )
        )
    }

    @AfterEach
    fun cleanUp(){
        repositorio.deleteAll()
    }

    @Test
    fun deve_listar_as_chaves_cadastradas(){
        val response = grpc.listar(ListaChavesPixRequest.newBuilder()
                                        .setClientId(CHAVE_EXISTENTE.clientId).build())

        with(response){
            Assertions.assertNotNull(response)
            Assertions.assertEquals(CHAVE_EXISTENTE.chave ,response.getListaDeChaves(0).chave)
        }
    }

    @Test
    fun nao_deve_salvar_se_chave_vazia(){
        val response = assertThrows<StatusRuntimeException> {
            grpc.listar(ListaChavesPixRequest.newBuilder()
                .setClientId("").build())
        }

        with(response){
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code ,response.status.code)

        }
    }



}