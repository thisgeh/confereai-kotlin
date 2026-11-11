package thisgeh.com.github.confereai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import thisgeh.com.github.confereai.data.local.Consulta
import thisgeh.com.github.confereai.model.FormaPagamento
import thisgeh.com.github.confereai.model.NivelRisco
import thisgeh.com.github.confereai.ui.components.BarraSuperior
import thisgeh.com.github.confereai.ui.components.EstadoVazio
import thisgeh.com.github.confereai.ui.components.cor
import thisgeh.com.github.confereai.ui.components.icone
import thisgeh.com.github.confereai.ui.theme.ConfereaiTheme
import thisgeh.com.github.confereai.ui.theme.Estrela
import thisgeh.com.github.confereai.util.formatarDataHora
import thisgeh.com.github.confereai.util.formatarMoeda
import thisgeh.com.github.confereai.viewmodel.ConsultaViewModel
import thisgeh.com.github.confereai.viewmodel.FiltroConsulta

@Composable
fun HistoricoScreen(
    viewModel: ConsultaViewModel,
    snackbarHostState: SnackbarHostState,
    onVoltar: () -> Unit,
    onAbrirConsulta: (Int) -> Unit,
    onNovaConsulta: () -> Unit
) {
    val todas by viewModel.consultas.collectAsStateWithLifecycle()
    val filtradas by viewModel.consultasFiltradas.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()

    HistoricoContent(
        totalConsultas = todas.size,
        consultas = filtradas,
        filtro = filtro,
        snackbarHostState = snackbarHostState,
        onSelecionarFiltro = viewModel::selecionarFiltro,
        onAbrirConsulta = onAbrirConsulta,
        onAlternarConfiavel = viewModel::alternarConfiavel,
        onDeletar = viewModel::deletar,
        onNovaConsulta = onNovaConsulta,
        onVoltar = onVoltar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoContent(
    totalConsultas: Int,
    consultas: List<Consulta>,
    filtro: FiltroConsulta,
    snackbarHostState: SnackbarHostState,
    onSelecionarFiltro: (FiltroConsulta) -> Unit,
    onAbrirConsulta: (Int) -> Unit,
    onAlternarConfiavel: (Consulta) -> Unit,
    onDeletar: (Consulta) -> Unit,
    onNovaConsulta: () -> Unit,
    onVoltar: () -> Unit
) {
    var consultaParaApagar by remember { mutableStateOf<Consulta?>(null) }

    consultaParaApagar?.let { consulta ->
        DialogoApagar(
            nome = consulta.nomeCobrador,
            onConfirmar = {
                onDeletar(consulta)
                consultaParaApagar = null
            },
            onCancelar = { consultaParaApagar = null }
        )
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Minhas consultas", onVoltar = onVoltar) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNovaConsulta,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nova consulta") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (totalConsultas == 0) {
                EstadoVazio(
                    icone = Icons.AutoMirrored.Filled.List,
                    titulo = "Você ainda não fez nenhuma consulta",
                    descricao = "Recebeu uma cobrança? Toque em \"Nova consulta\" para conferir antes de pagar.",
                    textoBotao = "Fazer minha primeira consulta",
                    onClickBotao = onNovaConsulta,
                    modifier = Modifier.padding(top = 48.dp)
                )
            } else {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroConsulta.entries.forEach { opcao ->
                        FilterChip(
                            selected = filtro == opcao,
                            onClick = { onSelecionarFiltro(opcao) },
                            label = { Text(opcao.rotulo) }
                        )
                    }
                }
                Text(
                    text = "Mostrando ${consultas.size} de $totalConsultas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                if (consultas.isEmpty()) {
                    EstadoVazio(
                        icone = Icons.Filled.Search,
                        titulo = "Nenhuma consulta neste filtro",
                        descricao = "Escolha outro filtro para ver as suas consultas.",
                        textoBotao = "Mostrar todas",
                        onClickBotao = { onSelecionarFiltro(FiltroConsulta.TODAS) }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),

                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(consultas, key = { it.id }) { consulta ->
                            CardConsulta(
                                consulta = consulta,
                                onClick = { onAbrirConsulta(consulta.id) },
                                onAlternarConfiavel = { onAlternarConfiavel(consulta) },
                                onDeletar = { consultaParaApagar = consulta }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardConsulta(
    consulta: Consulta,
    onClick: () -> Unit,
    onAlternarConfiavel: () -> Unit,
    onDeletar: () -> Unit
) {
    val nivel = consulta.nivelRisco
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(nivel.cor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(nivel.icone, contentDescription = nivel.titulo, tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    consulta.nomeCobrador,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(nivel.titulo, style = MaterialTheme.typography.bodyLarge, color = nivel.cor)
                val detalhes = buildString {
                    append(consulta.formaPagamento.rotulo)
                    consulta.valor?.let { append(" • ").append(formatarMoeda(it)) }
                }
                Text(detalhes, style = MaterialTheme.typography.bodyMedium)
                Text(
                    formatarDataHora(consulta.dataCriacao),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onAlternarConfiavel) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = if (consulta.confiavel) "Desmarcar como confiável" else "Marcar como confiável",
                    tint = if (consulta.confiavel) Estrela else Color(0xFFB0BEC5)
                )
            }
            IconButton(onClick = onDeletar) {
                Icon(Icons.Filled.Delete, contentDescription = "Apagar consulta")
            }
        }
    }
}

@Composable
fun DialogoApagar(nome: String, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
        title = { Text("Apagar consulta?") },
        text = { Text("A consulta de \"$nome\" será apagada do seu histórico.") },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Apagar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Preview(showBackground = true, name = "Histórico com consultas")
@Composable
private fun HistoricoContentPreview() {
    ConfereaiTheme {
        HistoricoContent(
            totalConsultas = 2,
            consultas = listOf(
                Consulta(
                    id = 1, nomeCobrador = "Banco do Brasil", formaPagamento = FormaPagamento.BOLETO,
                    nivelRisco = NivelRisco.BAIXO, pontuacao = 0, valor = 150.0, confiavel = true
                ),
                Consulta(
                    id = 2, nomeCobrador = "Filho com número novo", formaPagamento = FormaPagamento.PIX,
                    nivelRisco = NivelRisco.ALTO, pontuacao = 115, valor = 2000.0
                )
            ),
            filtro = FiltroConsulta.TODAS,
            snackbarHostState = SnackbarHostState(),
            onSelecionarFiltro = {},
            onAbrirConsulta = {},
            onAlternarConfiavel = {},
            onDeletar = {},
            onNovaConsulta = {},
            onVoltar = {}
        )
    }
}

@Preview(showBackground = true, name = "Histórico vazio")
@Composable
private fun HistoricoVazioPreview() {
    ConfereaiTheme {
        HistoricoContent(
            totalConsultas = 0,
            consultas = emptyList(),
            filtro = FiltroConsulta.TODAS,
            snackbarHostState = SnackbarHostState(),
            onSelecionarFiltro = {},
            onAbrirConsulta = {},
            onAlternarConfiavel = {},
            onDeletar = {},
            onNovaConsulta = {},
            onVoltar = {}
        )
    }
}
