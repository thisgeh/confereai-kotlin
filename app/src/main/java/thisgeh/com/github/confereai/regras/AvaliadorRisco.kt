package thisgeh.com.github.confereai.regras

import thisgeh.com.github.confereai.model.ContextoAvaliacao
import thisgeh.com.github.confereai.model.NivelRisco
import thisgeh.com.github.confereai.model.ResultadoAvaliacao
import thisgeh.com.github.confereai.regras.regrasrisco.RegraEmpresaRecente
import thisgeh.com.github.confereai.regras.regrasrisco.RegraNomeDivergente
import thisgeh.com.github.confereai.regras.regrasrisco.RegraPerguntasGolpe
import thisgeh.com.github.confereai.regras.regrasrisco.RegraRisco
import thisgeh.com.github.confereai.regras.regrasrisco.RegraSituacaoCadastral


class AvaliadorRisco(private val regras: List<RegraRisco>) {

    fun avaliar(contexto: ContextoAvaliacao): ResultadoAvaliacao {
        val sinais = regras
            .flatMap { it.avaliar(contexto) }
            .sortedByDescending { it.pontos }

        val pontuacao = sinais.sumOf { it.pontos }

        return ResultadoAvaliacao(
            nivel = classificar(pontuacao),
            pontuacao = pontuacao,
            motivos = sinais.map { it.motivo }
        )
    }

    companion object {
        const val LIMITE_MEDIO = 30
        const val LIMITE_ALTO = 60

        fun padrao() = AvaliadorRisco(
            listOf(
                RegraSituacaoCadastral(),
                RegraEmpresaRecente(),
                RegraNomeDivergente(),
                RegraPerguntasGolpe()
            )
        )

        fun classificar(pontuacao: Int): NivelRisco = when {
            pontuacao >= LIMITE_ALTO -> NivelRisco.ALTO
            pontuacao >= LIMITE_MEDIO -> NivelRisco.MEDIO
            else -> NivelRisco.BAIXO
        }
    }
}
