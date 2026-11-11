package thisgeh.com.github.confereai.regras

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import thisgeh.com.github.confereai.model.ContextoAvaliacao
import thisgeh.com.github.confereai.model.Empresa
import thisgeh.com.github.confereai.model.NivelRisco

class AvaliadorRiscoTest {

    private val avaliador = AvaliadorRisco.padrao()
    private val hoje = 1_800_000_000_000L
    private val dia = 86_400_000L

    private fun empresa(situacao: String = "ATIVA", diasAberta: Long = 5_000) = Empresa(
        cnpj = "00000000000191",
        razaoSocial = "BANCO DO BRASIL SA",
        nomeFantasia = "DIRECAO GERAL",
        situacao = situacao,
        dataInicioAtividade = hoje - diasAberta * dia
    )

    @Test
    fun semSinaisEhRiscoBaixo() {
        val resultado = avaliador.avaliar(
            ContextoAvaliacao(empresa = empresa(), nomeInformado = "Banco do Brasil", dataReferencia = hoje)
        )
        assertEquals(NivelRisco.BAIXO, resultado.nivel)
        assertEquals(0, resultado.pontuacao)
        assertTrue(resultado.motivos.isEmpty())
    }

    @Test
    fun pedirSenhaEhRiscoAlto() {
        val resultado = avaliador.avaliar(ContextoAvaliacao(respostasSim = setOf("senha")))
        assertEquals(NivelRisco.ALTO, resultado.nivel)
        assertEquals(60, resultado.pontuacao)
    }

    @Test
    fun nomeDivergenteEhAtencao() {
        val resultado = avaliador.avaliar(
            ContextoAvaliacao(empresa = empresa(), nomeInformado = "Nubank", dataReferencia = hoje)
        )
        assertEquals(NivelRisco.MEDIO, resultado.nivel)
        assertEquals(35, resultado.pontuacao)
    }

    @Test
    fun empresaInativaEhRiscoAlto() {
        val resultado = avaliador.avaliar(
            ContextoAvaliacao(empresa = empresa(situacao = "BAIXADA"), dataReferencia = hoje)
        )
        assertEquals(NivelRisco.ALTO, resultado.nivel)
    }

    @Test
    fun empresaMuitoRecenteSomaPontos() {
        val resultado = avaliador.avaliar(
            ContextoAvaliacao(empresa = empresa(diasAberta = 30), dataReferencia = hoje)
        )
        assertEquals(40, resultado.pontuacao)
        assertEquals(NivelRisco.MEDIO, resultado.nivel)
    }

    @Test
    fun motivosOrdenadosDoMaisGraveParaOMenos() {
        val resultado = avaliador.avaliar(ContextoAvaliacao(respostasSim = setOf("urgencia", "senha")))
        assertEquals(85, resultado.pontuacao)
        assertTrue(resultado.motivos.first().contains("senha"))
    }

    @Test
    fun comparacaoDeNomesIgnoraAcentoEPalavrasGenericas() {
        assertTrue(TextoUtil.nomeCorresponde("Itaú", listOf("ITAU UNIBANCO S.A.")))
        assertTrue(!TextoUtil.nomeCorresponde("Banco Bradesco", listOf("BANCO DO BRASIL SA")))
    }
}
