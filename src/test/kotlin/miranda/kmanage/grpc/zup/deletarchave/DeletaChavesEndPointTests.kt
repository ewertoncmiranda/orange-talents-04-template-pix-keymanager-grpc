package miranda.kmanage.grpc.zup.deletarchave

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import miranda.kmanage.grpc.zup.DeletaChaveRequester
import miranda.kmanage.grpc.zup.DeletarChaveServiceGrpc
import miranda.kmanage.grpc.zup.PixKeymanagerServiceGrpc
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import org.junit.jupiter.api.*
import java.util.*

@MicronautTest(transactional = false)
class DeletaChavesEndPointTests (val  repositorio: ChavePixRepositorio,
                                val  grpc: DeletarChaveServiceGrpc.DeletarChaveServiceBlockingStub){

    lateinit var  CHAVE_PRE_SALVA : ChavePix

    @AfterEach
    fun limpaBanco(){
        repositorio.deleteAll()
    }

    @BeforeEach
    fun setup(){
        CHAVE_PRE_SALVA = repositorio.save(
            ChavePix("Tomas",
                TipoDeChave.ALEATORIO,
                UUID.randomUUID().toString(),
                TipoDaConta.CONTA_CORRENTE,null)
        )
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): DeletarChaveServiceGrpc.DeletarChaveServiceBlockingStub? {
            return  DeletarChaveServiceGrpc.newBlockingStub(channel)
        }
    }

        @Test
        fun deve_remover_chave_existente() {
            //Ação
            val response = grpc.exclui(
                DeletaChaveRequester.newBuilder()
                    .setIdPix(CHAVE_PRE_SALVA.id!!)
                    .setClienteId(CHAVE_PRE_SALVA.clientId)
                    .build()
            )
            //Validação
            Assertions.assertEquals(response.deletado, true)
            Assertions.assertTrue(response.deletado)
        }

        @Test
        fun nao_deve_remover_se_chave_inexistente(){
            val response = assertThrows<StatusRuntimeException> { grpc.exclui(
                DeletaChaveRequester.newBuilder()
                    .setIdPix(20L)
                    .setClienteId(CHAVE_PRE_SALVA.clientId)
                    .build()
            ) }
            //Validação
            with(response){
                Assertions.assertEquals(Status.NOT_FOUND.code , status.code)
            }
        }

       @Test
       fun nao_deve_remover_se_chave_existe_mas_pertence_outro_cliente() {
        val response = assertThrows<StatusRuntimeException> { grpc.exclui(
            DeletaChaveRequester.newBuilder()
                .setIdPix(CHAVE_PRE_SALVA.id!!)
                .setClienteId(UUID.randomUUID().toString())
                .build()
        ) }
        //Validação
        with(response){
            Assertions.assertEquals(Status.NOT_FOUND.code , status.code)
            Assertions.assertEquals("Chave informada nao pertence ao usuario informado!",status.description)
        }
    }

}