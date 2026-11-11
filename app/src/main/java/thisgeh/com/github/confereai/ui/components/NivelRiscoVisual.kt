package thisgeh.com.github.confereai.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import thisgeh.com.github.confereai.model.NivelRisco
import thisgeh.com.github.confereai.ui.theme.FundoRiscoAlto
import thisgeh.com.github.confereai.ui.theme.FundoRiscoBaixo
import thisgeh.com.github.confereai.ui.theme.FundoRiscoMedio
import thisgeh.com.github.confereai.ui.theme.RiscoAlto
import thisgeh.com.github.confereai.ui.theme.RiscoBaixo
import thisgeh.com.github.confereai.ui.theme.RiscoMedio



val NivelRisco.cor: Color
    get() = when (this) {
        NivelRisco.BAIXO -> RiscoBaixo
        NivelRisco.MEDIO -> RiscoMedio
        NivelRisco.ALTO -> RiscoAlto
    }

val NivelRisco.corFundo: Color
    get() = when (this) {
        NivelRisco.BAIXO -> FundoRiscoBaixo
        NivelRisco.MEDIO -> FundoRiscoMedio
        NivelRisco.ALTO -> FundoRiscoAlto
    }

val NivelRisco.icone: ImageVector
    get() = when (this) {
        NivelRisco.BAIXO -> Icons.Filled.CheckCircle
        NivelRisco.MEDIO -> Icons.Filled.Warning
        NivelRisco.ALTO -> Icons.Filled.Close
    }
