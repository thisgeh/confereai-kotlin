package thisgeh.com.github.confereai.regras.regrasrisco

import thisgeh.com.github.confereai.model.ContextoAvaliacao
import thisgeh.com.github.confereai.model.PerguntaGolpe
import thisgeh.com.github.confereai.model.PerguntasGolpe
import thisgeh.com.github.confereai.model.SinalRisco


class RegraPerguntasGolpe(
    private val perguntas: List<PerguntaGolpe> = PerguntasGolpe.todas
) : RegraRisco {

    override fun avaliar(contexto: ContextoAvaliacao): List<SinalRisco> =
        perguntas
            .filter { it.id in contexto.respostasSim }
            .map { SinalRisco(pontos = it.pontos, motivo = it.motivo) }
}
