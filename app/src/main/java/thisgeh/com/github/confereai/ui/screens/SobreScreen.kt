package thisgeh.com.github.confereai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import thisgeh.com.github.confereai.R
import thisgeh.com.github.confereai.ui.components.BarraSuperior
import thisgeh.com.github.confereai.ui.theme.ConfereaiTheme


@Composable
fun SobreScreen(onVoltar: () -> Unit) {
    Scaffold(topBar = { BarraSuperior(titulo = "Sobre o ConfereAí", onVoltar = onVoltar) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_confereai),
                contentDescription = "Logo do ConfereAí",
                modifier = Modifier.size(110.dp)
            )
            Text("ConfereAí", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Confira antes de pagar.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            CardTexto(
                titulo = "O problema",
                texto = "A população idosa está cada vez mais conectada e virou alvo de golpes " +
                    "financeiros. Golpistas se passam por bancos, lojas e até parentes, usam a " +
                    "pressa e o medo e pedem pagamentos por boleto ou Pix. Segundo a Fundação " +
                    "Seade (SP TIC 2025), 82% dos idosos do estado de São Paulo já sofreram " +
                    "tentativas de golpes virtuais."
            )
            CardTexto(
                titulo = "Quem é beneficiado",
                texto = "Pessoas idosas (60+) e suas famílias. O app usa letras grandes, botões " +
                    "altos, alto contraste e linguagem simples, sem termos técnicos."
            )
            CardTexto(
                titulo = "Como o app ajuda",
                texto = "Cria o hábito de conferir antes de agir: o cidadão informa quem está " +
                    "cobrando, consulta o CNPJ na Receita Federal (via BrasilAPI), responde ao " +
                    "teste do golpe e vê o resultado em um semáforo. Tudo fica salvo em \"Minhas " +
                    "consultas\" e, em caso de dúvida, ele liga para uma pessoa de confiança com " +
                    "um toque."
            )
            CardTexto(
                titulo = "Objetivos de Desenvolvimento Sustentável (ONU)",
                texto = "• ODS 10 – Redução das desigualdades: inclusão digital segura da pessoa " +
                    "idosa, com mais autonomia.\n" +
                    "• ODS 11 – Cidades e comunidades sustentáveis: fortalece a rede de " +
                    "proteção comunitária (família e pessoas de confiança) contra crimes digitais.\n" +
                    "• ODS 3 – Saúde e bem-estar: menos ansiedade e menos perdas financeiras " +
                    "que afetam a saúde emocional."
            )
            CardTexto(
                titulo = "Aviso",
                texto = "Este é um protótipo acadêmico (FIAP – Global Solution CitizenTech). " +
                    "Os dados de CNPJ vêm da BrasilAPI, uma API pública com dados abertos da " +
                    "Receita Federal. O app não tem parceria com bancos ou órgãos públicos e " +
                    "não substitui o contato com o seu banco ou com a polícia."
            )
            CardTexto(
                titulo = "Equipe",
                texto = "Geisa Rodrigues Santos – RM558820\nMatheus Soares Pereira – RM558937"
            )
        }
    }
}

@Composable
private fun CardTexto(titulo: String, texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(texto, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SobreScreenPreview() {
    ConfereaiTheme { SobreScreen(onVoltar = {}) }
}
