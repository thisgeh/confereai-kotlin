package thisgeh.com.github.confereai.model


data class Empresa(
    val cnpj: String,
    val razaoSocial: String,
    val nomeFantasia: String = "",
    val situacao: String,
    val dataInicioAtividade: Long? = null,
    val municipio: String = "",
    val uf: String = "",
    val atividadePrincipal: String = ""
) {

    val ativa: Boolean get() = situacao.trim().uppercase() == "ATIVA"


    val nomeExibicao: String
        get() = nomeFantasia.trim().ifEmpty { razaoSocial.trim() }

    val cidade: String
        get() = when {
            municipio.isBlank() -> uf
            uf.isBlank() -> municipio
            else -> "$municipio - $uf"
        }
}
