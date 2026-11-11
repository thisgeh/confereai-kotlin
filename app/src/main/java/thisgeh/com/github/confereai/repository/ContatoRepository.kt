package thisgeh.com.github.confereai.repository

import thisgeh.com.github.confereai.data.local.Contato
import thisgeh.com.github.confereai.data.local.ContatoDao
import kotlinx.coroutines.flow.Flow

class ContatoRepository(private val dao: ContatoDao) {

    val contatos: Flow<List<Contato>> = dao.listarTodos()

    val contatoPrincipal: Flow<Contato?> = dao.buscarPrincipal()


    suspend fun inserir(contato: Contato) {
        val primeiro = dao.contar() == 0
        dao.inserir(contato.copy(principal = contato.principal || primeiro))
    }

    suspend fun deletar(contato: Contato) {
        dao.deletar(contato)

        if (contato.principal) {
            dao.listarTodosUmaVez().firstOrNull()?.let { dao.definirPrincipal(it.id) }
        }
    }

    suspend fun definirPrincipal(contato: Contato) = dao.definirPrincipal(contato.id)
}
