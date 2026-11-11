package thisgeh.com.github.confereai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import thisgeh.com.github.confereai.model.FormaPagamento
import thisgeh.com.github.confereai.model.NivelRisco


@Entity(tableName = "consultas")
data class Consulta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val nomeCobrador: String,

    val cnpj: String = "",

    val nomeOficial: String = "",
    val valor: Double? = null,
    val formaPagamento: FormaPagamento,
    val nivelRisco: NivelRisco,
    val pontuacao: Int,

    val motivos: String = "",

    val confiavel: Boolean = false,
    val dataCriacao: Long = System.currentTimeMillis()
)


fun Consulta.listaMotivos(): List<String> =
    motivos.lines().filter { it.isNotBlank() }
