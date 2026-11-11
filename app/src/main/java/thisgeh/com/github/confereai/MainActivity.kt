package thisgeh.com.github.confereai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import thisgeh.com.github.confereai.navigation.AppNavigation
import thisgeh.com.github.confereai.ui.theme.ConfereaiTheme
import thisgeh.com.github.confereai.viewmodel.ConsultaViewModel
import thisgeh.com.github.confereai.viewmodel.ContatoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConfereaiTheme {
                val consultaViewModel: ConsultaViewModel = viewModel(
                    factory = ConsultaViewModel.factory(applicationContext)
                )
                val contatoViewModel: ContatoViewModel = viewModel(
                    factory = ContatoViewModel.factory(applicationContext)
                )
                AppNavigation(
                    consultaViewModel = consultaViewModel,
                    contatoViewModel = contatoViewModel
                )
            }
        }
    }
}
