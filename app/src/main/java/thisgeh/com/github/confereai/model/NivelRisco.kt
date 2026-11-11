package thisgeh.com.github.confereai.model


enum class NivelRisco(val titulo: String, val orientacao: String) {
    BAIXO(
        titulo = "Parece seguro",
        orientacao = "Não encontramos sinais de golpe. Mesmo assim, confira o valor " +
            "e o nome de quem vai receber antes de pagar."
    ),
    MEDIO(
        titulo = "Atenção!",
        orientacao = "Encontramos sinais suspeitos. Não pague agora. Ligue para uma " +
            "pessoa de confiança ou para o número oficial da empresa."
    ),
    ALTO(
        titulo = "Pare! Pode ser golpe",
        orientacao = "Não pague, não clique em links e não passe nenhum código. " +
            "Desligue e converse com alguém de confiança."
    )
}
