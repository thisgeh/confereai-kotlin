package thisgeh.com.github.confereai.regras


object ValidadorCnpj {

    private val pesosPrimeiroDigito = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
    private val pesosSegundoDigito = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)


    fun limpar(valor: String): String =
        valor.uppercase().replace(Regex("[^0-9A-Z]"), "")

    fun valido(valor: String): Boolean {
        val cnpj = limpar(valor)

        if (!Regex("^[0-9A-Z]{12}[0-9]{2}$").matches(cnpj)) return false


        if (cnpj.all { it == cnpj[0] }) return false

        val primeiro = calcularDigito(cnpj.substring(0, 12), pesosPrimeiroDigito)
        val segundo = calcularDigito(cnpj.substring(0, 13), pesosSegundoDigito)

        return cnpj[12].digitToInt() == primeiro && cnpj[13].digitToInt() == segundo
    }


    fun formatar(valor: String): String {
        val cnpj = limpar(valor)
        if (cnpj.length != 14) return valor
        return "${cnpj.substring(0, 2)}.${cnpj.substring(2, 5)}.${cnpj.substring(5, 8)}/" +
            "${cnpj.substring(8, 12)}-${cnpj.substring(12)}"
    }

    private fun calcularDigito(base: String, pesos: IntArray): Int {
        var soma = 0
        base.forEachIndexed { i, caractere ->

            soma += (caractere.code - 48) * pesos[i]
        }
        val resto = soma % 11
        return if (resto < 2) 0 else 11 - resto
    }
}
