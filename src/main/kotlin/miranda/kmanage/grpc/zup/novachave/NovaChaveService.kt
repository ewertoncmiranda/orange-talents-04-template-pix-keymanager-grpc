package miranda.kmanage.grpc.zup.novachave

import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import miranda.kmanage.grpc.zup.NovaChaveResponse
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.conta.ContaDoBancoRepositorio
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.exception.ChaveJaCadastradaException
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

        val tipoConta = verificaTipoDaConta(novaChavePix.conta!!)

        if(chavePixRepositorio.existsByChave(novaChavePix.chave!!)) throw ChaveJaCadastradaException("Chave ${novaChavePix.chave}Já cadastrada")

        //Busca no cliente erp do Itau um idCliente e um tipo de conta
        val response = baseDeDados.buscarCLientePorIdEConta(novaChavePix.clienteId!!,tipoConta)

        //verifica se o cliente foi encontrado
        if(response.status.code == HttpStatus.NOT_FOUND.code) { throw ClienteNaoCadastradoNoBancoException("Cliente não encontrado no banco.")}

        val conta = response.body()?.toModel()

        var chavepix = novaChavePix.toModel()

        if(!contaDoBancoRepositorio.existsByNumeroConta(conta!!.numeroConta)){
            chavepix.conta = contaDoBancoRepositorio.save(conta)
        }else{
             chavepix.conta = contaDoBancoRepositorio.findByNumeroConta(conta.numeroConta).get()
        }

        val chaveSalva = chavePixRepositorio.save(chavepix)

        return NovaChaveResponse.newBuilder()
                                .setClienteId(chaveSalva.clientId)
                                .setIdPix(chaveSalva.chave)
                                .build()
    }

    fun verificaTipoDaConta(conta: TipoDaConta):String{
        return when(conta){
            TipoDaConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
            TipoDaConta.CONTA_CORRENTE->"CONTA_CORRENTE"
            else -> "-----ERRO---------"
        }
    }
}