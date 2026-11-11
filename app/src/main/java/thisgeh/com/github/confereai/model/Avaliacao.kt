package thisgeh.com.github.confereai.model


data class ContextoAvaliacao(
    val empresa: Empresa? = null,
    val nomeInformado: String = "",
    val respostasSim: Set<String> = emptySet(),
    val dataReferencia: Long = System.currentTimeMillis()
)

data class SinalRisco(val pontos: Int, val motivo: String)

data class ResultadoAvaliacao(
    val nivel: NivelRisco,
    val pontuacao: Int,
    val motivos: List<String>
)
