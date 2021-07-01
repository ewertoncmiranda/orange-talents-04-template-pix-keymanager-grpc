package miranda.kmanage.grpc.zup.sistemasexternos.bcbdto

data class PixKeyDetailsResponse(val keyType:KeyType,
                                 val key:String,
                                 val owner:Owner,
                                 val bankAccount: BankAccount,
                                 val createdAt:String       )

