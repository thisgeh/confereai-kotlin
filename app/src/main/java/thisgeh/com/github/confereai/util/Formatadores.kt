package thisgeh.com.github.confereai.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localeBrasil: Locale = Locale.forLanguageTag("pt-BR")

fun formatarDataHora(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", localeBrasil).format(Date(millis))

fun formatarData(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", localeBrasil).format(Date(millis))

fun formatarMoeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(localeBrasil).format(valor)


fun formatarTelefone(telefone: String): String {
    val d = telefone.filter { it.isDigit() }
    return when (d.length) {
        11 -> "(${d.substring(0, 2)}) ${d.substring(2, 7)}-${d.substring(7)}"
        10 -> "(${d.substring(0, 2)}) ${d.substring(2, 6)}-${d.substring(6)}"
        else -> telefone
    }
}
