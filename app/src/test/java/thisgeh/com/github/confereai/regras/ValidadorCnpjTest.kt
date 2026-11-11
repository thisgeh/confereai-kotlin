package thisgeh.com.github.confereai.regras

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorCnpjTest {

    @Test
    fun cnpjNumericoValidoComOuSemMascara() {
        assertTrue(ValidadorCnpj.valido("19.131.243/0001-97"))
        assertTrue(ValidadorCnpj.valido("19131243000197"))
        assertTrue(ValidadorCnpj.valido("00.000.000/0001-91"))
    }

    @Test
    fun cnpjComDigitoErradoEInvalido() {
        assertFalse(ValidadorCnpj.valido("19.131.243/0001-98"))
    }

    @Test
    fun cnpjComTamanhoErradoOuRepetidoEInvalido() {
        assertFalse(ValidadorCnpj.valido("1913124300019"))
        assertFalse(ValidadorCnpj.valido("11.111.111/1111-11"))
        assertFalse(ValidadorCnpj.valido(""))
    }

    @Test
    fun cnpjAlfanumericoValido() {
        // Exemplo oficial da Receita Federal: 12.ABC.345/01DE-35
        assertTrue(ValidadorCnpj.valido("12.ABC.345/01DE-35"))
        assertTrue(ValidadorCnpj.valido("12abc34501de35"))
    }

    @Test
    fun formatarColocaMascara() {
        assertEquals("19.131.243/0001-97", ValidadorCnpj.formatar("19131243000197"))
    }
}
