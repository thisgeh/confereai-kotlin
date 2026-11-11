package thisgeh.com.github.confereai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import thisgeh.com.github.confereai.data.local.ConfereAiDatabase
import thisgeh.com.github.confereai.data.local.Contato
import thisgeh.com.github.confereai.regras.ValidadorFormulario
import thisgeh.com.github.confereai.repository.ContatoRepository

class ContatoViewModel(private val repository: ContatoRepository) : ViewModel() {

    val contatos: StateFlow<List<Contato>> = repository.contatos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val contatoPrincipal: StateFlow<Contato?> = repository.contatoPrincipal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _mensagens = Channel<Mensagem>(Channel.BUFFERED)
    val mensagens: Flow<Mensagem> = _mensagens.receiveAsFlow()

    fun inserir(nome: String, telefone: String): Boolean {
        if (ValidadorFormulario.validarNomeContato(nome) != null ||
            ValidadorFormulario.validarTelefone(telefone) != null
        ) {
            return false
        }
        viewModelScope.launch {
            repository.inserir(
                Contato(nome = nome.trim(), telefone = ValidadorFormulario.apenasDigitos(telefone))
            )
            _mensagens.trySend(Mensagem("${nome.trim()} foi adicionado(a) às pessoas de confiança."))
        }
        return true
    }

    fun definirPrincipal(contato: Contato) {
        viewModelScope.launch {
            repository.definirPrincipal(contato)
            _mensagens.trySend(Mensagem("${contato.nome} agora é o contato principal."))
        }
    }

    fun deletar(contato: Contato) {
        viewModelScope.launch {
            repository.deletar(contato)
            _mensagens.trySend(Mensagem("${contato.nome} foi removido(a)."))
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = ConfereAiDatabase.getDatabase(context).contatoDao()
                    return ContatoViewModel(ContatoRepository(dao)) as T
                }
            }
    }
}
