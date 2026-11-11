package thisgeh.com.github.confereai.viewmodel


data class Mensagem(
    val texto: String,
    val acao: String? = null,
    val aoClicarAcao: (() -> Unit)? = null
)
