package thisgeh.com.github.confereai.regras

import java.text.Normalizer


object TextoUtil {

    private val palavrasIgnoradas = setOf(
        "de", "da", "do", "das", "dos", "e", "a", "o",
        "ltda", "sa", "me", "epp", "eireli", "cia", "companhia",
        "banco", "bco", "pagamento", "pagamentos", "servico", "servicos",
        "financeira", "credito", "instituicao", "empresa", "comercio",
        "central", "atendimento", "loja"
    )


    fun normalizar(texto: String): String {
        val semAcento = Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}"), "")
        return semAcento
            .replace(Regex("[^a-z0-9 ]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun palavrasRelevantes(texto: String): List<String> =
        normalizar(texto)
            .split(" ")
            .filter { it.length >= 2 && it !in palavrasIgnoradas }

    fun nomeCorresponde(nome: String, nomesOficiais: List<String>): Boolean {
        val palavras = palavrasRelevantes(nome)
        if (palavras.isEmpty()) return true

        val oficiais = nomesOficiais.joinToString(" ") { normalizar(it) }
        val palavrasOficiais = oficiais.split(" ")
        val oficiaisSemEspaco = oficiais.replace(" ", "")

        return palavras.any { it in palavrasOficiais || oficiaisSemEspaco.contains(it) }
    }
}
