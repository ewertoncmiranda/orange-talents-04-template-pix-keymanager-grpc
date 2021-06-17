package miranda.kmanage.grpc.zup.chavepix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface ChavePixRepositorio:CrudRepository<ChavePix,Long> {

    fun existsByChave(chave:String):Boolean
}