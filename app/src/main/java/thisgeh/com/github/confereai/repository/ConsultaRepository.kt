package thisgeh.com.github.confereai.repository

import thisgeh.com.github.confereai.data.local.Consulta
import thisgeh.com.github.confereai.data.local.ConsultaDao
import kotlinx.coroutines.flow.Flow

class ConsultaRepository(private val dao: ConsultaDao) {

    val consultas: Flow<List<Consulta>> = dao.listarTodas()

    fun buscarPorId(id: Int): Flow<Consulta?> = dao.buscarPorId(id)

    suspend fun inserir(consulta: Consulta): Long = dao.inserir(consulta)

    suspend fun atualizar(consulta: Consulta) = dao.atualizar(consulta)

    suspend fun deletar(consulta: Consulta) = dao.deletar(consulta)
}
