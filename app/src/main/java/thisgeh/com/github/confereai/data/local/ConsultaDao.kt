package thisgeh.com.github.confereai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultaDao {

    @Query("SELECT * FROM consultas ORDER BY dataCriacao DESC")
    fun listarTodas(): Flow<List<Consulta>>

    @Query("SELECT * FROM consultas WHERE id = :id")
    fun buscarPorId(id: Int): Flow<Consulta?>


    @Insert
    suspend fun inserir(consulta: Consulta): Long

    @Update
    suspend fun atualizar(consulta: Consulta)

    @Delete
    suspend fun deletar(consulta: Consulta)
}
