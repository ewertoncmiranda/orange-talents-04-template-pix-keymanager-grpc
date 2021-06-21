package miranda.kmanage.grpc.zup.exception

import java.lang.RuntimeException

class ChaveNaoPertenceAoUsuarioException(override val message:String):RuntimeException(message)

