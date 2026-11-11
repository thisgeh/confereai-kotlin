package thisgeh.com.github.confereai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import thisgeh.com.github.confereai.model.NivelRisco


@Composable
fun SemaforoRisco(nivel: NivelRisco, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = nivel.corFundo),
        border = BorderStroke(2.dp, nivel.cor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(Color(0xFF263238), RoundedCornerShape(40.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                listOf(NivelRisco.ALTO, NivelRisco.MEDIO, NivelRisco.BAIXO).forEach { luz ->
                    val acesa = luz == nivel
                    Box(
                        modifier = Modifier
                            .size(if (acesa) 48.dp else 36.dp)
                            .background(
                                if (acesa) luz.cor else luz.cor.copy(alpha = 0.25f),
                                CircleShape
                            )
                            .border(2.dp, if (acesa) Color.White else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (acesa) {
                            Icon(luz.icone, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
            Text(
                text = nivel.titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = nivel.cor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = nivel.orientacao,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
