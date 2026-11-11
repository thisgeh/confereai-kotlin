package thisgeh.com.github.confereai.regras.regrasrisco

import thisgeh.com.github.confereai.model.ContextoAvaliacao
import thisgeh.com.github.confereai.model.SinalRisco
import thisgeh.com.github.confereai.regras.TextoUtil


class RegraNomeDivergente : RegraRisco {

    override fun avaliar(contexto: ContextoAvaliacao): List<SinalRisco> {
        val empresa = contexto.empresa ?: return emptyList()
        val nome = contexto.nomeInformado.trim()
        if (nome.isEmpty()) return emptyList()

        val corresponde = TextoUtil.nomeCorresponde(
            nome,
            listOf(empresa.razaoSocial, empresa.nomeFantasia)
        )
        if (corresponde) return emptyList()

        return listOf(
            SinalRisco(
                pontos = PONTOS,
                motivo = "Disseram que era \"$nome\", mas esse CNPJ é de \"${empresa.nomeExibicao}\"."
            )
        )
    }

    companion object {
        const val PONTOS = 35
    }
}
