package thisgeh.com.github.confereai.data.remote

import com.google.gson.annotations.SerializedName
import thisgeh.com.github.confereai.model.Empresa
import java.text.SimpleDateFormat
import java.util.Locale


data class EmpresaResponse(
    @SerializedName("cnpj") val cnpj: String?,
    @SerializedName("razao_social") val razaoSocial: String?,
    @SerializedName("nome_fantasia") val nomeFantasia: String?,
    @SerializedName("descricao_situacao_cadastral") val situacao: String?,
    @SerializedName("data_inicio_atividade") val dataInicioAtividade: String?,
    @SerializedName("municipio") val municipio: String?,
    @SerializedName("uf") val uf: String?,
    @SerializedName("cnae_fiscal_descricao") val atividadePrincipal: String?
)


fun EmpresaResponse.paraEmpresa(): Empresa = Empresa(
    cnpj = cnpj.orEmpty(),
    razaoSocial = razaoSocial.orEmpty().trim(),
    nomeFantasia = nomeFantasia.orEmpty().trim(),
    situacao = situacao?.trim()?.ifEmpty { null } ?: "DESCONHECIDA",
    dataInicioAtividade = converterData(dataInicioAtividade),
    municipio = municipio.orEmpty().trim(),
    uf = uf.orEmpty().trim(),
    atividadePrincipal = atividadePrincipal.orEmpty().trim()
)


private fun converterData(texto: String?): Long? {
    if (texto.isNullOrBlank()) return null
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(texto)?.time
    } catch (e: Exception) {
        null
    }
}
