package miranda.kmanage.grpc.zup.chavepix

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface ChavePixRepositorio:CrudRepository<ChavePix,Long> {

   fun findByChave(chave: String):Optional<ChavePix>

   fun existsByChave(chave:String):Boolean

   @Query("SELECT c FROM ChavePix c where c.id=:id and c.clientId=:clientId")
   fun buscarPorIdEChave(id:Long ,clientId:String):Optional<ChavePix>
}
