package thisgeh.com.github.confereai.navigation


object Rotas {
    const val HOME = "home"
    const val NOVA_CONSULTA = "nova_consulta"
    const val HISTORICO = "historico"
    const val DETALHE = "consulta/{consultaId}"
    const val CONTATOS = "contatos"
    const val SOBRE = "sobre"

    fun detalhe(consultaId: Int) = "consulta/$consultaId"
}
