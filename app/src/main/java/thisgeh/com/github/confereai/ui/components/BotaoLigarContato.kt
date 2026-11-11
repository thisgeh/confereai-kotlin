package thisgeh.com.github.confereai.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import thisgeh.com.github.confereai.data.local.Contato
import thisgeh.com.github.confereai.ui.theme.AzulLigar
import thisgeh.com.github.confereai.util.abrirDiscador


@Composable
fun BotaoLigarContato(
    contato: Contato?,
    onCadastrarContato: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (contato == null) {
        OutlinedButton(
            onClick = onCadastrarContato,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
        ) {
            Icon(Icons.Filled.Person, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cadastrar uma pessoa de confiança")
        }
    } else {
        Button(
            onClick = { abrirDiscador(context, contato.telefone) },
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulLigar)
        ) {
            Icon(Icons.Filled.Call, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Ligar para ${contato.nome}")
        }
    }
}
