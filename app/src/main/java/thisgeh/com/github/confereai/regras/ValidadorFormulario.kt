package thisgeh.com.github.confereai.regras


object ValidadorFormulario {

    const val TAMANHO_MINIMO_NOME = 2

    fun validarNome(nome: String): String? = when {
        nome.isBlank() -> "Informe quem está cobrando."
        nome.trim().length < TAMANHO_MINIMO_NOME -> "O nome está muito curto."
        else -> null
    }

    fun validarCnpjOpcional(cnpj: String): String? = when {
        cnpj.isBlank() -> null
        !ValidadorCnpj.valido(cnpj) -> "CNPJ inválido. Confira os números."
        else -> null
    }


    fun validarValorOpcional(valor: String): String? = when {
        valor.isBlank() -> null
        converterValor(valor) == null -> "Digite um valor válido, por exemplo 150,00."
        else -> null
    }

    fun validarNomeContato(nome: String): String? =
        if (nome.isBlank()) "Informe o nome da pessoa." else null

    fun validarTelefone(telefone: String): String? {
        val digitos = apenasDigitos(telefone)
        return when {
            digitos.isEmpty() -> "Informe o telefone."
            digitos.length !in 10..11 -> "Digite o telefone com DDD (10 ou 11 números)."
            else -> null
        }
    }


    fun converterValor(texto: String): Double? {
        val limpo = texto.trim().replace("R$", "").replace(" ", "")
        if (limpo.isEmpty()) return null
        val normalizado = if (limpo.contains(',')) {
            limpo.replace(".", "").replace(',', '.')
        } else {
            limpo
        }
        val numero = normalizado.toDoubleOrNull() ?: return null
        return if (numero > 0) numero else null
    }

    fun apenasDigitos(texto: String): String = texto.filter { it.isDigit() }
}
