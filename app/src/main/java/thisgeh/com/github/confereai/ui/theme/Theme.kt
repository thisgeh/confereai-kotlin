package thisgeh.com.github.confereai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val ConfereAiColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E9F2),
    onPrimaryContainer = AzulPrimario,
    secondary = VerdeSecundario,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3F2EC),
    onSecondaryContainer = Color(0xFF00382E),
    tertiary = AzulLigar,
    onTertiary = Color.White,
    error = RiscoAlto,
    onError = Color.White,
    background = Fundo,
    onBackground = Texto,
    surface = Color.White,
    onSurface = Texto,
    surfaceVariant = Color(0xFFE6ECF0),
    onSurfaceVariant = TextoSuave
)

@Composable
fun ConfereaiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ConfereAiColorScheme,
        typography = Typography,
        content = content
    )
}
