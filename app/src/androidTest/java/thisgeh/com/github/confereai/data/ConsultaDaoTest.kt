package thisgeh.com.github.confereai.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import thisgeh.com.github.confereai.data.local.ConfereAiDatabase
import thisgeh.com.github.confereai.data.local.Consulta
import thisgeh.com.github.confereai.data.local.ConsultaDao
import thisgeh.com.github.confereai.data.local.Contato
import thisgeh.com.github.confereai.data.local.ContatoDao
import thisgeh.com.github.confereai.model.FormaPagamento
import thisgeh.com.github.confereai.model.NivelRisco

@RunWith(AndroidJUnit4::class)
class ConsultaDaoTest {

    private lateinit var database: ConfereAiDatabase
    private lateinit var consultaDao: ConsultaDao
    private lateinit var contatoDao: ContatoDao

    @Before
    fun criarBanco() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ConfereAiDatabase::class.java
        ).allowMainThreadQueries().build()
        consultaDao = database.consultaDao()
        contatoDao = database.contatoDao()
    }

    @After
    fun fecharBanco() {
        database.close()
    }

    private fun novaConsulta(nome: String = "Banco do Brasil") = Consulta(
        nomeCobrador = nome,
        formaPagamento = FormaPagamento.BOLETO,
        nivelRisco = NivelRisco.BAIXO,
        pontuacao = 0
    )

    @Test
    fun inserirConsultaEListar() = runTest {
        val id = consultaDao.inserir(novaConsulta())

        val consultas = consultaDao.listarTodas().first()
        assertEquals(1, consultas.size)
        assertEquals("Banco do Brasil", consultas[0].nomeCobrador)
        assertEquals(NivelRisco.BAIXO, consultas[0].nivelRisco)
        assertEquals(id.toInt(), consultas[0].id)
    }

    @Test
    fun marcarComoConfiavel() = runTest {
        val id = consultaDao.inserir(novaConsulta()).toInt()
        val inserida = consultaDao.buscarPorId(id).first()!!
        assertFalse(inserida.confiavel)

        consultaDao.atualizar(inserida.copy(confiavel = true))

        assertTrue(consultaDao.buscarPorId(id).first()!!.confiavel)
    }

    @Test
    fun deletarConsulta() = runTest {
        val id = consultaDao.inserir(novaConsulta()).toInt()
        val inserida = consultaDao.buscarPorId(id).first()!!

        consultaDao.deletar(inserida)

        assertNull(consultaDao.buscarPorId(id).first())
        assertTrue(consultaDao.listarTodas().first().isEmpty())
    }

    @Test
    fun definirContatoPrincipal() = runTest {
        val ana = contatoDao.inserir(Contato(nome = "Ana", telefone = "11987654321", principal = true)).toInt()
        val carlos = contatoDao.inserir(Contato(nome = "Carlos", telefone = "1133334444")).toInt()

        contatoDao.definirPrincipal(carlos)

        val principal = contatoDao.buscarPrincipal().first()
        assertEquals(carlos, principal?.id)
        assertFalse(contatoDao.listarTodos().first().first { it.id == ana }.principal)
    }
}
