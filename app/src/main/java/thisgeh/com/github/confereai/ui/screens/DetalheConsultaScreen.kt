package thisgeh.com.github.confereai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import thisgeh.com.github.confereai.data.local.Consulta
import thisgeh.com.github.confereai.data.local.Contato
import thisgeh.com.github.confereai.data.local.listaMotivos
import thisgeh.com.github.confereai.model.FormaPagamento
import thisgeh.com.github.confereai.model.NivelRisco
import thisgeh.com.github.confereai.regras.ValidadorCnpj
import thisgeh.com.github.confereai.ui.components.BarraSuperior
import thisgeh.com.github.confereai.ui.components.BotaoLigarContato
import thisgeh.com.github.confereai.ui.components.EstadoVazio
import thisgeh.com.github.confereai.ui.components.LinhaInfo
import thisgeh.com.github.confereai.ui.components.SemaforoRisco
import thisgeh.com.github.confereai.ui.theme.ConfereaiTheme
import thisgeh.com.github.confereai.ui.theme.Estrela
import thisgeh.com.github.confereai.ui.theme.RiscoAlto
import thisgeh.com.github.confereai.ui.theme.RiscoBaixo
import thisgeh.com.github.confereai.util.formatarDataHora
import thisgeh.com.github.confereai.util.formatarMoeda
import thisgeh.com.github.confereai.viewmodel.ConsultaViewModel
import thisgeh.com.github.confereai.viewmodel.ContatoViewModel
import thisgeh.com.github.confereai.viewmodel.DetalheUiState

@Composable
fun DetalheConsultaScreen(
    consultaId: Int,
    consultaViewModel: ConsultaViewModel,
    contatoViewModel: ContatoViewModel,
    snackbarHostState: SnackbarHostState,
    onVoltar: () -> Unit,
    onCadastrarContato: () -> Unit
) {
    val estado by remember(consultaId) { consultaViewModel.detalhe(consultaId) }
        .collectAsStateWithLifecycle(initialValue = DetalheUiState.Carregando)
    val contatoPrincipal by contatoViewModel.contatoPrincipal.collectAsStateWithLifecycle()

    DetalheConsultaContent(
        estado = estado,
        contatoPrincipal = contatoPrincipal,
        snackbarHostState = snackbarHostState,
        onAlternarConfiavel = consultaViewModel::alternarConfiavel,
        onDeletar = { consulta ->
            consultaViewModel.deletar(consulta)
            onVoltar()
        },
        onCadastrarContato = onCadastrarContato,
        onVoltar = onVoltar
    )
}

@Composable
fun DetalheConsultaContent(
    estado: DetalheUiState,
    contatoPrincipal: Contato?,
    snackbarHostState: SnackbarHostState,
    onAlternarConfiavel: (Consulta) -> Unit,
    onDeletar: (Consulta) -> Unit,
    onCadastrarContato: () -> Unit,
    onVoltar: () -> Unit
) {
    Scaffold(
        topBar = { BarraSuperior(titulo = "Resultado da consulta", onVoltar = onVoltar) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (estado) {
                DetalheUiState.Carregando -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                DetalheUiState.NaoEncontrada -> EstadoVazio(
                    icone = Icons.Filled.Search,
                    titulo = "Consulta não encontrada",
                    descricao = "Ela pode ter sido apagada.",
                    textoBotao = "Voltar",
                    onClickBotao = onVoltar
                )
                is DetalheUiState.Carregada -> DetalheConsulta(
                    consulta = estado.consulta,
                    contatoPrincipal = contatoPrincipal,
                    onAlternarConfiavel = onAlternarConfiavel,
                    onDeletar = onDeletar,
                    onCadastrarContato = onCadastrarContato
                )
            }
        }
    }
}

@Composable
private fun DetalheConsulta(
    consulta: Consulta,
    contatoPrincipal: Contato?,
    onAlternarConfiavel: (Consulta) -> Unit,
    onDeletar: (Consulta) -> Unit,
    onCadastrarContato: () -> Unit
) {
    var confirmarExclusao by remember { mutableStateOf(false) }
    if (confirmarExclusao) {
        DialogoApagar(
            nome = consulta.nomeCobrador,
            onConfirmar = {
                confirmarExclusao = false
                onDeletar(consulta)
            },
            onCancelar = { confirmarExclusao = false }
        )
    }

    val motivos = consulta.listaMotivos()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SemaforoRisco(nivel = consulta.nivelRisco)


        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Por que esse resultado?", style = MaterialTheme.typography.titleMedium)
                if (motivos.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = RiscoBaixo)
                        Spacer(Modifier.width(8.dp))
                        Text("Nenhum sinal de alerta encontrado.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    motivos.forEach { motivo ->
                        Row {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = RiscoAlto)
                            Spacer(Modifier.width(8.dp))
                            Text(motivo, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                Text(
                    "Pontuação de risco: ${consulta.pontuacao}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("Dados da cobrança", style = MaterialTheme.typography.titleMedium)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                LinhaInfo("Quem está cobrando", consulta.nomeCobrador)
                if (consulta.cnpj.isNotBlank()) LinhaInfo("CNPJ", ValidadorCnpj.formatar(consulta.cnpj))
                if (consulta.nomeOficial.isNotBlank()) LinhaInfo("Nome na Receita Federal", consulta.nomeOficial)
                LinhaInfo("Forma de pagamento", consulta.formaPagamento.rotulo)
                consulta.valor?.let { LinhaInfo("Valor", formatarMoeda(it)) }
                LinhaInfo("Consultado em", formatarDataHora(consulta.dataCriacao))
                if (consulta.confiavel) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Estrela)
                        Spacer(Modifier.width(6.dp))
                        Text("Marcada como confiável", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        BotaoLigarContato(contato = contatoPrincipal, onCadastrarContato = onCadastrarContato)


        if (consulta.nivelRisco == NivelRisco.ALTO) {
            Text(
                "Por segurança, cobranças com risco alto não podem ser marcadas como confiáveis.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Button(
                onClick = { onAlternarConfiavel(consulta) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (consulta.confiavel) MaterialTheme.colorScheme.surfaceVariant else Estrela,
                    contentColor = if (consulta.confiavel) MaterialTheme.colorScheme.onSurface else Color.Black
                )
            ) {
                Icon(Icons.Filled.Star, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (consulta.confiavel) "Desmarcar como confiável" else "Marcar como confiável")
            }
        }

        OutlinedButton(
            onClick = { confirmarExclusao = true },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Apagar consulta")
        }
    }
}

@Preview(showBackground = true, name = "Resultado com perigo")
@Composable
private fun DetalheConsultaPreview() {
    ConfereaiTheme {
        DetalheConsultaContent(
            estado = DetalheUiState.Carregada(
                Consulta(
                    id = 1,
                    nomeCobrador = "Filho com número novo",
                    formaPagamento = FormaPagamento.PIX,
                    valor = 2000.0,
                    nivelRisco = NivelRisco.ALTO,
                    pontuacao = 75,
                    motivos = "Golpistas pedem segredo para que ninguém avise que é golpe.\n" +
                        "O golpe do \"parente com número novo\" é muito comum."
                )
            ),
            contatoPrincipal = null,
            snackbarHostState = SnackbarHostState(),
            onAlternarConfiavel = {},
            onDeletar = {},
            onCadastrarContato = {},
            onVoltar = {}
        )
    }
}
