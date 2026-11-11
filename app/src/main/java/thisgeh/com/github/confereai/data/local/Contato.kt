package thisgeh.com.github.confereai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contatos")
data class Contato(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,

    val telefone: String,

    val principal: Boolean = false
)


fun Contato.inicial(): String = nome.trim().firstOrNull()?.uppercase() ?: "?"
