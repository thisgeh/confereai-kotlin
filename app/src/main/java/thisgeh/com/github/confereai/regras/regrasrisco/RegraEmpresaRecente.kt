package thisgeh.com.github.confereai.regras.regrasrisco

import thisgeh.com.github.confereai.model.ContextoAvaliacao
import thisgeh.com.github.confereai.model.SinalRisco


class RegraEmpresaRecente : RegraRisco {

    override fun avaliar(contexto: ContextoAvaliacao): List<SinalRisco> {
        val abertura = contexto.empresa?.dataInicioAtividade ?: return emptyList()
        val dias = (contexto.dataReferencia - abertura) / MILIS_POR_DIA

        return when {
            dias < DIAS_MUITO_RECENTE -> listOf(
                SinalRisco(
                    pontos = PONTOS_MUITO_RECENTE,
                    motivo = "A empresa foi aberta há apenas $dias dias. Golpistas costumam " +
                        "abrir empresas novas para receber pagamentos."
                )
            )
            dias < DIAS_RECENTE -> listOf(
                SinalRisco(
                    pontos = PONTOS_RECENTE,
                    motivo = "A empresa tem menos de 1 ano. Confira com mais cuidado."
                )
            )
            else -> emptyList()
        }
    }

    companion object {
        const val DIAS_MUITO_RECENTE = 180
        const val DIAS_RECENTE = 365
        const val PONTOS_MUITO_RECENTE = 40
        const val PONTOS_RECENTE = 20
        const val MILIS_POR_DIA = 86_400_000L
    }
}
