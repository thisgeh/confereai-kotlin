package thisgeh.com.github.confereai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import thisgeh.com.github.confereai.data.local.Contato
import thisgeh.com.github.confereai.data.local.inicial
import thisgeh.com.github.confereai.regras.ValidadorFormulario
import thisgeh.com.github.confereai.ui.components.BarraSuperior
import thisgeh.com.github.confereai.ui.components.EstadoVazio
import thisgeh.com.github.confereai.ui.theme.AzulLigar
import thisgeh.com.github.confereai.ui.theme.ConfereaiTheme
import thisgeh.com.github.confereai.ui.theme.Estrela
import thisgeh.com.github.confereai.util.abrirDiscador
import thisgeh.com.github.confereai.util.formatarTelefone
import thisgeh.com.github.confereai.viewmodel.ContatoViewModel

@Composable
fun ContatosScreen(
    viewModel: ContatoViewModel,
    snackbarHostState: SnackbarHostState,
    onVoltar: () -> Unit
) {
    val contatos by viewModel.contatos.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ContatosContent(
        contatos = contatos,
        snackbarHostState = snackbarHostState,
        onSalvar = viewModel::inserir,
        onDefinirPrincipal = viewModel::definirPrincipal,
        onDeletar = viewModel::deletar,
        onLigar = { contato -> abrirDiscador(context, contato.telefone) },
        onVoltar = onVoltar
    )
}

@Composable
fun ContatosContent(
    contatos: List<Contato>,
    snackbarHostState: SnackbarHostState,
    onSalvar: (nome: String, telefone: String) -> Boolean,
    onDefinirPrincipal: (Contato) -> Unit,
    onDeletar: (Contato) -> Unit,
    onLigar: (Contato) -> Unit,
    onVoltar: () -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var telefone by rememberSaveable { mutableStateOf("") }
    var tentouSalvar by rememberSaveable { mutableStateOf(false) }
    var contatoParaRemover by remember { mutableStateOf<Contato?>(null) }
    val focusManager = LocalFocusManager.current

    val erroNome = ValidadorFormulario.validarNomeContato(nome).takeIf { tentouSalvar }
    val erroTelefone = ValidadorFormulario.validarTelefone(telefone).takeIf { tentouSalvar }

    contatoParaRemover?.let { contato ->
        AlertDialog(
            onDismissRequest = { contatoParaRemover = null },
            title = { Text("Remover contato?") },
            text = { Text("${contato.nome} deixará de ser uma pessoa de confiança.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeletar(contato)
                    contatoParaRemover = null
                }) { Text("Remover", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { contatoParaRemover = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Pessoas de confiança", onVoltar = onVoltar) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Em caso de dúvida, ligue para alguém de confiança antes de pagar.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }


            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Adicionar pessoa", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = nome,
                            onValueChange = { nome = it },
                            label = { Text("Nome *") },
                            placeholder = { Text("Ex.: Ana (filha)") },
                            isError = erroNome != null,
                            supportingText = if (erroNome != null) {
                                { Text(erroNome) }
                            } else {
                                null
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = telefone,
                            onValueChange = { telefone = it.take(20) },
                            label = { Text("Telefone com DDD *") },
                            placeholder = { Text("(11) 98765-4321") },
                            isError = erroTelefone != null,
                            supportingText = if (erroTelefone != null) {
                                { Text(erroTelefone) }
                            } else {
                                null
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                tentouSalvar = true
                                if (onSalvar(nome, telefone)) {
                                    nome = ""
                                    telefone = ""
                                    tentouSalvar = false
                                    focusManager.clearFocus()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                        ) {
                            Text("Salvar contato")
                        }
                    }
                }
            }

            item {
                Text("Seus contatos", style = MaterialTheme.typography.titleMedium)
            }

            if (contatos.isEmpty()) {
                item {
                    EstadoVazio(
                        icone = Icons.Filled.Person,
                        titulo = "Nenhuma pessoa cadastrada",
                        descricao = "Adicione um filho, neto, amigo ou vizinho em quem você confia."
                    )
                }
            } else {
                items(contatos, key = { it.id }) { contato ->
                    CardContato(
                        contato = contato,
                        onLigar = { onLigar(contato) },
                        onDefinirPrincipal = { onDefinirPrincipal(contato) },
                        onRemover = { contatoParaRemover = contato }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardContato(
    contato: Contato,
    onLigar: () -> Unit,
    onDefinirPrincipal: () -> Unit,
    onRemover: () -> Unit
) {
    Card(
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
                    .size(48.dp)
                    .background(AzulLigar, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(contato.inicial(), color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(contato.nome, style = MaterialTheme.typography.titleMedium)
                Text(formatarTelefone(contato.telefone), style = MaterialTheme.typography.bodyLarge)
                if (contato.principal) {
                    Text(
                        "Contato principal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            IconButton(onClick = onDefinirPrincipal, enabled = !contato.principal) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Definir como contato principal",
                    tint = if (contato.principal) Estrela else Color(0xFFB0BEC5)
                )
            }
            IconButton(onClick = onLigar) {
                Icon(Icons.Filled.Call, contentDescription = "Ligar para ${contato.nome}", tint = AzulLigar)
            }
            IconButton(onClick = onRemover) {
                Icon(Icons.Filled.Delete, contentDescription = "Remover ${contato.nome}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContatosContentPreview() {
    ConfereaiTheme {
        ContatosContent(
            contatos = listOf(
                Contato(id = 1, nome = "Ana (filha)", telefone = "11987654321", principal = true),
                Contato(id = 2, nome = "Carlos (vizinho)", telefone = "1133334444")
            ),
            snackbarHostState = SnackbarHostState(),
            onSalvar = { _, _ -> true },
            onDefinirPrincipal = {},
            onDeletar = {},
            onLigar = {},
            onVoltar = {}
        )
    }
}
