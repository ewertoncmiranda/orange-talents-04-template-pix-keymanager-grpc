package miranda.kmanage.grpc.zup.sistemasexternos.bcbdto

import miranda.kmanage.grpc.zup.TipoConta

enum class AccountType {
    SVGS {
        override fun toProtoType(): TipoConta {
           return TipoConta.CONTA_POUPANCA
        }
    }, CACC {
        override fun toProtoType(): TipoConta {
            return TipoConta.CONTA_CORRENTE
        }
    };

    abstract fun toProtoType():TipoConta
}