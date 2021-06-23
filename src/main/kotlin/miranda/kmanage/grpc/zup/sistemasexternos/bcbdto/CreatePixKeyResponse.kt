package miranda.kmanage.grpc.zup.sistemasexternos.bcbdto

data class CreatePixKeyResponse(val keyType:KeyType,
                                val key:String,
                                val owner:Owner,
                                val bankAccount: BankAccount,
                                val createdAt:String       )

