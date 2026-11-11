package thisgeh.com.github.confereai.regras.regrasrisco

import thisgeh.com.github.confereai.model.ContextoAvaliacao
import thisgeh.com.github.confereai.model.SinalRisco


class RegraSituacaoCadastral : RegraRisco {

    override fun avaliar(contexto: ContextoAvaliacao): List<SinalRisco> {
        val empresa = contexto.empresa ?: return emptyList()
        if (empresa.ativa) return emptyList()

        return listOf(
            SinalRisco(
                pontos = PONTOS,
                motivo = "A empresa está com situação \"${empresa.situacao}\" na Receita " +
                    "Federal. Ela não deveria estar cobrando nada."
            )
        )
    }

    companion object {
        const val PONTOS = 100
    }
}
