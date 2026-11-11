package thisgeh.com.github.confereai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import thisgeh.com.github.confereai.R
import thisgeh.com.github.confereai.data.local.Contato
import thisgeh.com.github.confereai.model.NivelRisco
import thisgeh.com.github.confereai.ui.components.BarraSuperior
import thisgeh.com.github.confereai.ui.components.BotaoLigarContato
import thisgeh.com.github.confereai.ui.components.CardOpcao
import thisgeh.com.github.confereai.ui.theme.AzulLigar
import thisgeh.com.github.confereai.ui.theme.AzulPrimario
import thisgeh.com.github.confereai.ui.theme.ConfereaiTheme
import thisgeh.com.github.confereai.ui.theme.RiscoMedio
import thisgeh.com.github.confereai.ui.theme.VerdeSecundario
import thisgeh.com.github.confereai.viewmodel.ConsultaViewModel
import thisgeh.com.github.confereai.viewmodel.ContatoViewModel

@Composable
fun HomeScreen(
    consultaViewModel: ConsultaViewModel,
    contatoViewModel: ContatoViewModel,
    snackbarHostState: SnackbarHostState,
    onNovaConsulta: () -> Unit,
    onHistorico: () -> Unit,
    onContatos: () -> Unit,
    onSobre: () -> Unit
) {
    val consultas by consultaViewModel.consultas.collectAsStateWithLifecycle()
    val contatoPrincipal by contatoViewModel.contatoPrincipal.collectAsStateWithLifecycle()

    HomeContent(
        totalConsultas = consultas.size,
        totalPerigo = consultas.count { it.nivelRisco == NivelRisco.ALTO },
        contatoPrincipal = contatoPrincipal,
        snackbarHostState = snackbarHostState,
        onNovaConsulta = onNovaConsulta,
        onHistorico = onHistorico,
        onContatos = onContatos,
        onSobre = onSobre
    )
}

@Composable
fun HomeContent(
    totalConsultas: Int,
    totalPerigo: Int,
    contatoPrincipal: Contato?,
    snackbarHostState: SnackbarHostState,
    onNovaConsulta: () -> Unit,
    onHistorico: () -> Unit,
    onContatos: () -> Unit,
    onSobre: () -> Unit
) {
    Scaffold(
        topBar = {
            BarraSuperior(titulo = "ConfereAí") {
                IconButton(onClick = onSobre) {
                    Icon(Icons.Filled.Info, contentDescription = "Sobre o ConfereAí")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.logo_confereai),
                    contentDescription = "Logo do ConfereAí",
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White, CircleShape)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Olá!", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "O que você quer conferir hoje?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }


            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Confira antes de pagar", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Golpistas se passam por bancos, lojas e parentes para pedir Pix ou " +
                            "boleto, principalmente para pessoas idosas. Antes de pagar, faça " +
                            "uma consulta no ConfereAí.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            CardOpcao(
                icone = Icons.Filled.Search,
                cor = AzulPrimario,
                titulo = "Conferir uma cobrança",
                descricao = "Diga quem está cobrando, confira o CNPJ e faça o teste do golpe.",
                onClick = onNovaConsulta
            )
            CardOpcao(
                icone = Icons.AutoMirrored.Filled.List,
                cor = RiscoMedio,
                titulo = "Minhas consultas",
                descricao = when (totalConsultas) {
                    0 -> "Você ainda não fez nenhuma consulta."
                    1 -> "1 consulta salva" + if (totalPerigo > 0) " (com perigo)." else "."
                    else -> "$totalConsultas consultas salvas" +
                        if (totalPerigo > 0) ", $totalPerigo com perigo." else "."
                },
                onClick = onHistorico
            )
            CardOpcao(
                icone = Icons.Filled.Person,
                cor = AzulLigar,
                titulo = "Pessoas de confiança",
                descricao = "Cadastre quem você quer chamar quando estiver em dúvida.",
                onClick = onContatos
            )
            CardOpcao(
                icone = Icons.Filled.Info,
                cor = VerdeSecundario,
                titulo = "Sobre o ConfereAí",
                descricao = "Problema, público, objetivo e ODS do projeto.",
                onClick = onSobre
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "Está em dúvida agora?",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            BotaoLigarContato(contato = contatoPrincipal, onCadastrarContato = onContatos)
        }
    }
}

@Preview(showBackground = true, name = "Tela inicial")
@Composable
private fun HomeContentPreview() {
    ConfereaiTheme {
        HomeContent(
            totalConsultas = 3,
            totalPerigo = 1,
            contatoPrincipal = Contato(id = 1, nome = "Ana (filha)", telefone = "11987654321", principal = true),
            snackbarHostState = SnackbarHostState(),
            onNovaConsulta = {},
            onHistorico = {},
            onContatos = {},
            onSobre = {}
        )
    }
}
