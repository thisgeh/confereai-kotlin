package thisgeh.com.github.confereai.regras

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorFormularioTest {

    @Test
    fun nomeObrigatorio() {
        assertNotNull(ValidadorFormulario.validarNome(""))
        assertNotNull(ValidadorFormulario.validarNome("   "))
        assertNotNull(ValidadorFormulario.validarNome("A"))
        assertNull(ValidadorFormulario.validarNome("Banco do Brasil"))
    }

    @Test
    fun cnpjOpcionalMasValidadoQuandoPreenchido() {
        assertNull(ValidadorFormulario.validarCnpjOpcional(""))
        assertNull(ValidadorFormulario.validarCnpjOpcional("19.131.243/0001-97"))
        assertNotNull(ValidadorFormulario.validarCnpjOpcional("123"))
    }

    @Test
    fun valorAceitaVirgulaEPonto() {
        assertEquals(1234.56, ValidadorFormulario.converterValor("1.234,56")!!, 0.001)
        assertEquals(150.0, ValidadorFormulario.converterValor("150")!!, 0.001)
        assertEquals(99.9, ValidadorFormulario.converterValor("R$ 99,90")!!, 0.001)
        assertNull(ValidadorFormulario.converterValor("abc"))
        assertNull(ValidadorFormulario.converterValor("0"))
        assertNotNull(ValidadorFormulario.validarValorOpcional("-5"))
        assertNull(ValidadorFormulario.validarValorOpcional(""))
    }

    @Test
    fun telefoneComDdd() {
        assertNull(ValidadorFormulario.validarTelefone("(11) 98765-4321"))
        assertNull(ValidadorFormulario.validarTelefone("1133334444"))
        assertNotNull(ValidadorFormulario.validarTelefone("98765-4321"))
        assertNotNull(ValidadorFormulario.validarTelefone(""))
    }
}
