package thisgeh.com.github.confereai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import thisgeh.com.github.confereai.data.local.ConfereAiDatabase
import thisgeh.com.github.confereai.data.local.Consulta
import thisgeh.com.github.confereai.data.remote.BrasilApiService
import thisgeh.com.github.confereai.data.remote.BrasilApiServiceFactory
import thisgeh.com.github.confereai.data.remote.paraEmpresa
import thisgeh.com.github.confereai.model.ContextoAvaliacao
import thisgeh.com.github.confereai.model.Empresa
import thisgeh.com.github.confereai.model.FormaPagamento
import thisgeh.com.github.confereai.model.NivelRisco
import thisgeh.com.github.confereai.regras.AvaliadorRisco
import thisgeh.com.github.confereai.regras.ValidadorCnpj
import thisgeh.com.github.confereai.regras.ValidadorFormulario
import thisgeh.com.github.confereai.repository.ConsultaRepository
import java.io.IOException

sealed class EmpresaUiState {
    object Inicial : EmpresaUiState()
    object Carregando : EmpresaUiState()
    data class Sucesso(val empresa: Empresa) : EmpresaUiState()
    data class Erro(val mensagem: String) : EmpresaUiState()
}

sealed class DetalheUiState {
    object Carregando : DetalheUiState()
    object NaoEncontrada : DetalheUiState()
    data class Carregada(val consulta: Consulta) : DetalheUiState()
}


enum class FiltroConsulta(val rotulo: String) {
    TODAS("Todas"),
    PERIGO("Perigo"),
    ATENCAO("Atenção"),
    SEGURAS("Seguras"),
    CONFIAVEIS("Confiáveis");

    fun aceita(consulta: Consulta): Boolean = when (this) {
        TODAS -> true
        PERIGO -> consulta.nivelRisco == NivelRisco.ALTO
        ATENCAO -> consulta.nivelRisco == NivelRisco.MEDIO
        SEGURAS -> consulta.nivelRisco == NivelRisco.BAIXO
        CONFIAVEIS -> consulta.confiavel
    }
}

class ConsultaViewModel(
    private val repository: ConsultaRepository,
    private val api: BrasilApiService,
    private val avaliador: AvaliadorRisco = AvaliadorRisco.padrao()
) : ViewModel() {


    val consultas: StateFlow<List<Consulta>> = repository.consultas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _filtro = MutableStateFlow(FiltroConsulta.TODAS)
    val filtro: StateFlow<FiltroConsulta> = _filtro.asStateFlow()


    val consultasFiltradas: StateFlow<List<Consulta>> =
        combine(repository.consultas, _filtro) { lista, filtro ->
            lista.filter { filtro.aceita(it) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _empresaState = MutableStateFlow<EmpresaUiState>(EmpresaUiState.Inicial)
    val empresaState: StateFlow<EmpresaUiState> = _empresaState.asStateFlow()

    private val _mensagens = Channel<Mensagem>(Channel.BUFFERED)
    val mensagens: Flow<Mensagem> = _mensagens.receiveAsFlow()

    fun selecionarFiltro(filtro: FiltroConsulta) {
        _filtro.value = filtro
    }

    fun detalhe(id: Int): Flow<DetalheUiState> =
        repository.buscarPorId(id).map { consulta ->
            if (consulta == null) DetalheUiState.NaoEncontrada else DetalheUiState.Carregada(consulta)
        }


    fun buscarEmpresa(cnpjDigitado: String) {
        val cnpj = ValidadorCnpj.limpar(cnpjDigitado)
        if (!ValidadorCnpj.valido(cnpj)) {
            _empresaState.value = EmpresaUiState.Erro("CNPJ inválido. Confira os números.")
            return
        }

        viewModelScope.launch {
            _empresaState.value = EmpresaUiState.Carregando
            _empresaState.value = try {
                val resposta = api.buscarCnpj(cnpj)
                val corpo = resposta.body()
                if (resposta.isSuccessful && corpo != null) {
                    EmpresaUiState.Sucesso(corpo.paraEmpresa().copy(cnpj = cnpj))
                } else {
                    EmpresaUiState.Erro(mensagemErroHttp(resposta.code()))
                }
            } catch (e: IOException) {
                EmpresaUiState.Erro("Sem internet. Verifique sua conexão e tente novamente.")
            } catch (e: Exception) {
                EmpresaUiState.Erro("Não foi possível consultar agora. Tente novamente.")
            }
        }
    }

    fun limparEmpresa() {
        _empresaState.value = EmpresaUiState.Inicial
    }


    fun salvarConsulta(
        nomeCobrador: String,
        cnpjDigitado: String,
        valorDigitado: String,
        formaPagamento: FormaPagamento,
        respostasSim: Set<String>,
        aoSalvar: (Int) -> Unit
    ) {
        if (ValidadorFormulario.validarNome(nomeCobrador) != null ||
            ValidadorFormulario.validarCnpjOpcional(cnpjDigitado) != null ||
            ValidadorFormulario.validarValorOpcional(valorDigitado) != null
        ) {
            enviarMensagem(Mensagem("Confira os campos destacados em vermelho."))
            return
        }

        val cnpj = ValidadorCnpj.limpar(cnpjDigitado)
        val empresa = (empresaState.value as? EmpresaUiState.Sucesso)?.empresa
            ?.takeIf { cnpj.isNotEmpty() && it.cnpj == cnpj }

        val resultado = avaliador.avaliar(
            ContextoAvaliacao(
                empresa = empresa,
                nomeInformado = nomeCobrador.trim(),
                respostasSim = respostasSim
            )
        )

        val consulta = Consulta(
            nomeCobrador = nomeCobrador.trim(),
            cnpj = cnpj,
            nomeOficial = empresa?.nomeExibicao.orEmpty(),
            valor = ValidadorFormulario.converterValor(valorDigitado),
            formaPagamento = formaPagamento,
            nivelRisco = resultado.nivel,
            pontuacao = resultado.pontuacao,
            motivos = resultado.motivos.joinToString("\n")
        )

        viewModelScope.launch {
            val id = repository.inserir(consulta)
            _empresaState.value = EmpresaUiState.Inicial
            enviarMensagem(Mensagem("Consulta salva em \"Minhas consultas\"."))
            aoSalvar(id.toInt())
        }
    }


    fun alternarConfiavel(consulta: Consulta) {
        if (!consulta.confiavel && consulta.nivelRisco == NivelRisco.ALTO) {
            enviarMensagem(
                Mensagem("Por segurança, uma cobrança com risco alto não pode ser marcada como confiável.")
            )
            return
        }
        val atualizada = consulta.copy(confiavel = !consulta.confiavel)
        viewModelScope.launch {
            repository.atualizar(atualizada)
            enviarMensagem(
                Mensagem(
                    if (atualizada.confiavel) "Marcada como confiável." else "Removida das confiáveis."
                )
            )
        }
    }

    fun deletar(consulta: Consulta) {
        viewModelScope.launch {
            repository.deletar(consulta)
            enviarMensagem(
                Mensagem(
                    texto = "Consulta apagada.",
                    acao = "Desfazer",
                    aoClicarAcao = { viewModelScope.launch { repository.inserir(consulta) } }
                )
            )
        }
    }

    private fun enviarMensagem(mensagem: Mensagem) {
        _mensagens.trySend(mensagem)
    }

    private fun mensagemErroHttp(codigo: Int): String = when (codigo) {
        400 -> "CNPJ inválido. Confira os números."
        404 -> "Não encontramos esse CNPJ na Receita Federal. Desconfie dessa cobrança!"
        429 -> "Muitas consultas seguidas. Aguarde um minuto e tente de novo."
        in 500..599 -> "O serviço de consulta está fora do ar agora. Tente mais tarde."
        else -> "Erro ao consultar (código $codigo). Tente novamente."
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = ConfereAiDatabase.getDatabase(context).consultaDao()
                    return ConsultaViewModel(
                        repository = ConsultaRepository(dao),
                        api = BrasilApiServiceFactory().create()
                    ) as T
                }
            }
    }
}
