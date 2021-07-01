package miranda.kmanage.grpc.zup.sistemasexternos.bcbdto

import miranda.kmanage.grpc.zup.TipoChave

enum class KeyType {
    PHONE {
        override fun toProtoType(): TipoChave {
            return TipoChave.CELULAR
        }
    }, CPF{
        override fun toProtoType(): TipoChave {
            return TipoChave.CPF
        }
    } ,
    RANDOM{
        override fun toProtoType(): TipoChave {
            return TipoChave.ALEATORIO
        }
    },
    EMAIL{
        override fun toProtoType(): TipoChave {
            return TipoChave.EMAIL
        }
    };

    abstract fun toProtoType():TipoChave

}