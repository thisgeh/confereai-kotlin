package thisgeh.com.github.confereai.regras.regrasrisco

import thisgeh.com.github.confereai.model.ContextoAvaliacao
import thisgeh.com.github.confereai.model.SinalRisco


interface RegraRisco {
    fun avaliar(contexto: ContextoAvaliacao): List<SinalRisco>
}
