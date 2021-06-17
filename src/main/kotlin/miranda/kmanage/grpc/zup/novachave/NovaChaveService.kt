package miranda.kmanage.grpc.zup.novachave

import io.micronaut.validation.Validated
import miranda.kmanage.grpc.zup.NovaChaveResponse
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.conta.ContaDoBancoRepositorio
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.exception.ChaveExistenteException
import miranda.kmanage.grpc.zup.exception.ClienteNaoCadastradoNoBancoException
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class NovaChaveService(
    private val chavePixRepositorio: ChavePixRepositorio,
    private val baseDeDados: ItauBaseDeDados,
    private val contaDoBancoRepositorio: ContaDoBancoRepositorio
) {

    @Transactional
    fun cadastrar(@Valid novaChavePix: NovaChavePix): NovaChaveResponse{

        val tipoConta = when(novaChavePix.conta){
            TipoDaConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
            TipoDaConta.CONTA_CORRENTE->"CONTA_CORRENTE"
            else -> ""
        }
        if(chavePixRepositorio.existsByChave(novaChavePix.chave!!)) throw ChaveExistenteException("Chave ${novaChavePix.chave}Já cadastrada")


        val response = baseDeDados.buscarCLientePorId(novaChavePix.clienteId!!,tipoConta)

        val conta = response.body()?.toModel() ?: throw ClienteNaoCadastradoNoBancoException("Cliente não encontrado no banco.")


        var chavepix = novaChavePix.toModel()
        //Converte contaResponse para conta, salva conta e atribui o objeto de conta retornado
        //pelo repositorio ao objeto de conta de chave pix
        chavepix.conta = contaDoBancoRepositorio.save(conta)

        val chaveSalva = chavePixRepositorio.save(chavepix)

        return NovaChaveResponse.newBuilder()
                                .setClienteId(chaveSalva.clientId)
                                .setIdPix(chaveSalva.chave)
                                .build()
    }
}