package thisgeh.com.github.confereai.model


enum class FormaPagamento(val rotulo: String) {
    PIX("Pix"),
    BOLETO("Boleto"),
    CARTAO("Cartão"),
    OUTRO("Outro")
}
