package thisgeh.com.github.confereai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Consulta::class, Contato::class], version = 1, exportSchema = false)
abstract class ConfereAiDatabase : RoomDatabase() {

    abstract fun consultaDao(): ConsultaDao

    abstract fun contatoDao(): ContatoDao

    companion object {
        @Volatile
        private var INSTANCE: ConfereAiDatabase? = null

        fun getDatabase(context: Context): ConfereAiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ConfereAiDatabase::class.java,
                    "confereai.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
