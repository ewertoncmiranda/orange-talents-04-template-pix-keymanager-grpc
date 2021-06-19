package miranda.kmanage.grpc.zup.novachave

import io.micronaut.http.HttpStatus
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
        //verifica o tipo da chave e atribui uma string para passar como parametro para o cliente
        val tipoConta = verificaConta(novaChavePix.conta!!)

        //verifica no repositório se a chave já é cadastrada. Se sim, lança uma exceção
        if(chavePixRepositorio.existsByChave(novaChavePix.chave!!)) throw ChaveExistenteException("Chave ${novaChavePix.chave}Já cadastrada")

        //Busca no cliente erp do Itau um idCliente e um tipo de conta
        val response = baseDeDados.buscarCLientePorIdEConta(novaChavePix.clienteId!!,tipoConta)

        //verifica se o cliente foi encontrado
        if(response.status.code == HttpStatus.NOT_FOUND.code) { throw ClienteNaoCadastradoNoBancoException("Cliente não encontrado no banco.")}

        val conta = response.body()?.toModel()

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

    fun verificaConta(conta: TipoDaConta):String{
        return when(conta){
            TipoDaConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
            TipoDaConta.CONTA_CORRENTE->"CONTA_CORRENTE"
            else -> "-----ERRO---------"
        }
    }
}