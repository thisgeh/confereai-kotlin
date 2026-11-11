package thisgeh.com.github.confereai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContatoDao {

    @Query("SELECT * FROM contatos ORDER BY principal DESC, nome ASC")
    fun listarTodos(): Flow<List<Contato>>

    @Query("SELECT * FROM contatos WHERE principal = 1 LIMIT 1")
    fun buscarPrincipal(): Flow<Contato?>

    @Query("SELECT * FROM contatos ORDER BY nome ASC")
    suspend fun listarTodosUmaVez(): List<Contato>

    @Insert
    suspend fun inserir(contato: Contato): Long

    @Delete
    suspend fun deletar(contato: Contato)


    @Query("UPDATE contatos SET principal = CASE WHEN id = :id THEN 1 ELSE 0 END")
    suspend fun definirPrincipal(id: Int)

    @Query("SELECT COUNT(*) FROM contatos")
    suspend fun contar(): Int
}
