package thisgeh.com.github.confereai.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import thisgeh.com.github.confereai.model.Empresa
import thisgeh.com.github.confereai.model.FormaPagamento
import thisgeh.com.github.confereai.model.PerguntaGolpe
import thisgeh.com.github.confereai.model.PerguntasGolpe
import thisgeh.com.github.confereai.regras.ValidadorCnpj
import thisgeh.com.github.confereai.regras.ValidadorFormulario
import thisgeh.com.github.confereai.ui.components.BarraSuperior
import thisgeh.com.github.confereai.ui.components.LinhaInfo
import thisgeh.com.github.confereai.ui.theme.ConfereaiTheme
import thisgeh.com.github.confereai.ui.theme.FundoRiscoAlto
import thisgeh.com.github.confereai.ui.theme.FundoRiscoBaixo
import thisgeh.com.github.confereai.ui.theme.RiscoAlto
import thisgeh.com.github.confereai.ui.theme.RiscoBaixo
import thisgeh.com.github.confereai.util.formatarData
import thisgeh.com.github.confereai.viewmodel.ConsultaViewModel
import thisgeh.com.github.confereai.viewmodel.EmpresaUiState

@Composable
fun NovaConsultaScreen(
    viewModel: ConsultaViewModel,
    snackbarHostState: SnackbarHostState,
    onVoltar: () -> Unit,
    onConsultaSalva: (Int) -> Unit
) {
    val empresaState by viewModel.empresaState.collectAsStateWithLifecycle()

    NovaConsultaContent(
        empresaState = empresaState,
        snackbarHostState = snackbarHostState,
        onBuscarEmpresa = viewModel::buscarEmpresa,
        onLimparEmpresa = viewModel::limparEmpresa,
        onSalvar = { nome, cnpj, valor, forma, respostasSim ->
            viewModel.salvarConsulta(nome, cnpj, valor, forma, respostasSim, onConsultaSalva)
        },
        onVoltar = onVoltar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaConsultaContent(
    empresaState: EmpresaUiState,
    snackbarHostState: SnackbarHostState,
    onBuscarEmpresa: (String) -> Unit,
    onLimparEmpresa: () -> Unit,
    onSalvar: (
        nome: String,
        cnpj: String,
        valor: String,
        forma: FormaPagamento,
        respostasSim: Set<String>
    ) -> Unit,
    onVoltar: () -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var cnpj by rememberSaveable { mutableStateOf("") }
    var valor by rememberSaveable { mutableStateOf("") }
    var forma by rememberSaveable { mutableStateOf<FormaPagamento?>(null) }

    val respostas = remember { mutableStateMapOf<String, Boolean>() }
    var tentouSalvar by rememberSaveable { mutableStateOf(false) }
    var salvando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()


    val erroNome = ValidadorFormulario.validarNome(nome).takeIf { tentouSalvar }
    val erroCnpj = ValidadorFormulario.validarCnpjOpcional(cnpj)
        .takeIf { tentouSalvar || ValidadorCnpj.limpar(cnpj).length >= 14 }
    val erroValor = ValidadorFormulario.validarValorOpcional(valor).takeIf { tentouSalvar }
    val erroForma = "Escolha como pediram o pagamento.".takeIf { tentouSalvar && forma == null }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Conferir uma cobrança", onVoltar = onVoltar) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            TituloSecao("1. Quem está cobrando?")
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome de quem está cobrando *") },
                placeholder = { Text("Ex.: Banco, loja, parente...") },
                isError = erroNome != null,
                supportingText = { Text(erroNome ?: "Obrigatório. Use o nome que disseram para você.") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )


            TituloSecao("2. CNPJ de quem está cobrando (opcional)")
            OutlinedTextField(
                value = cnpj,
                onValueChange = { novo ->
                    cnpj = novo.take(18)
                    if (empresaState !is EmpresaUiState.Inicial) onLimparEmpresa()
                },
                label = { Text("CNPJ") },
                placeholder = { Text("00.000.000/0000-00") },
                isError = erroCnpj != null,
                supportingText = {
                    Text(erroCnpj ?: "Aparece no boleto, no comprovante do Pix ou no site da empresa.")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    keyboardType = KeyboardType.Ascii
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedButton(
                onClick = { onBuscarEmpresa(cnpj) },
                enabled = ValidadorCnpj.valido(cnpj) && empresaState !is EmpresaUiState.Carregando,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Consultar na Receita Federal")
            }
            EstadoEmpresa(
                estado = empresaState,
                onTentarNovamente = { onBuscarEmpresa(cnpj) }
            )


            TituloSecao("3. Como pediram para pagar?")

            FormaPagamento.entries.chunked(2).forEach { linha ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    linha.forEach { opcao ->
                        FilterChip(
                            selected = forma == opcao,
                            onClick = { forma = opcao },
                            label = { Text(opcao.rotulo) },
                            leadingIcon = if (forma == opcao) {
                                { Icon(Icons.Filled.Check, contentDescription = null) }
                            } else {
                                null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        )
                    }
                }
            }
            if (erroForma != null) {
                Text(erroForma, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor cobrado em R$ (opcional)") },
                placeholder = { Text("150,00") },
                isError = erroValor != null,
                supportingText = if (erroValor != null) {
                    { Text(erroValor) }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )


            TituloSecao("4. Teste do golpe")
            Text(
                "Responda pensando na mensagem ou ligação que você recebeu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            PerguntasGolpe.todas.forEach { pergunta ->
                CardPergunta(
                    pergunta = pergunta,
                    resposta = respostas[pergunta.id],
                    onResponder = { sim -> respostas[pergunta.id] = sim }
                )
            }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    tentouSalvar = true
                    val formaEscolhida = forma
                    val valido = ValidadorFormulario.validarNome(nome) == null &&
                        ValidadorFormulario.validarCnpjOpcional(cnpj) == null &&
                        ValidadorFormulario.validarValorOpcional(valor) == null &&
                        formaEscolhida != null
                    if (valido && formaEscolhida != null) {
                        salvando = true
                        onSalvar(
                            nome,
                            cnpj,
                            valor,
                            formaEscolhida,
                            respostas.filterValues { it }.keys.toSet()
                        )
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Confira os campos destacados em vermelho.")
                        }
                    }
                },
                enabled = !salvando,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp)
            ) {
                Text("Ver resultado e salvar")
            }
        }
    }
}

@Composable
private fun TituloSecao(texto: String) {
    Text(texto, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
}


@Composable
private fun EstadoEmpresa(estado: EmpresaUiState, onTentarNovamente: () -> Unit) {
    when (estado) {
        EmpresaUiState.Inicial -> Unit
        EmpresaUiState.Carregando -> Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Text("Consultando a Receita Federal...", style = MaterialTheme.typography.bodyLarge)
        }
        is EmpresaUiState.Sucesso -> CardEmpresa(estado.empresa)
        is EmpresaUiState.Erro -> Card(
            colors = CardDefaults.cardColors(containerColor = FundoRiscoAlto),
            border = BorderStroke(1.dp, RiscoAlto),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = RiscoAlto)
                    Spacer(Modifier.width(8.dp))
                    Text(estado.mensagem, style = MaterialTheme.typography.bodyLarge)
                }
                TextButton(onClick = onTentarNovamente) { Text("Tentar novamente") }
            }
        }
    }
}

@Composable
private fun CardEmpresa(empresa: Empresa) {
    val corBorda = if (empresa.ativa) RiscoBaixo else RiscoAlto
    Card(
        colors = CardDefaults.cardColors(containerColor = if (empresa.ativa) FundoRiscoBaixo else FundoRiscoAlto),
        border = BorderStroke(1.dp, corBorda),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                if (empresa.ativa) "Empresa encontrada e ATIVA" else "Atenção: empresa ${empresa.situacao}",
                style = MaterialTheme.typography.titleMedium,
                color = corBorda
            )
            LinhaInfo("Nome na Receita Federal", empresa.razaoSocial.ifBlank { "-" })
            if (empresa.nomeFantasia.isNotBlank()) LinhaInfo("Nome fantasia", empresa.nomeFantasia)
            empresa.dataInicioAtividade?.let { LinhaInfo("Aberta em", formatarData(it)) }
            if (empresa.cidade.isNotBlank()) LinhaInfo("Cidade", empresa.cidade)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardPergunta(
    pergunta: PerguntaGolpe,
    resposta: Boolean?,
    onResponder: (Boolean) -> Unit
) {
    val corFundo = when (resposta) {
        true -> FundoRiscoAlto
        false -> FundoRiscoBaixo
        null -> Color.White
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = corFundo),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(pergunta.texto, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = resposta == true,
                    onClick = { onResponder(true) },
                    label = { Text("Sim") },
                    leadingIcon = if (resposta == true) {
                        { Icon(Icons.Filled.Check, contentDescription = null) }
                    } else {
                        null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
                FilterChip(
                    selected = resposta == false,
                    onClick = { onResponder(false) },
                    label = { Text("Não") },
                    leadingIcon = if (resposta == false) {
                        { Icon(Icons.Filled.Check, contentDescription = null) }
                    } else {
                        null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Nova consulta")
@Composable
private fun NovaConsultaContentPreview() {
    ConfereaiTheme {
        NovaConsultaContent(
            empresaState = EmpresaUiState.Inicial,
            snackbarHostState = SnackbarHostState(),
            onBuscarEmpresa = {},
            onLimparEmpresa = {},
            onSalvar = { _, _, _, _, _ -> },
            onVoltar = {}
        )
    }
}
