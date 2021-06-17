package miranda.kmanage.grpc.zup.conta

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import javax.persistence.Entity

@Repository
interface ContaDoBancoRepositorio : CrudRepository<ContaDoBanco,Long>