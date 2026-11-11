package thisgeh.com.github.confereai.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.merge
import thisgeh.com.github.confereai.ui.screens.ContatosScreen
import thisgeh.com.github.confereai.ui.screens.DetalheConsultaScreen
import thisgeh.com.github.confereai.ui.screens.HistoricoScreen
import thisgeh.com.github.confereai.ui.screens.HomeScreen
import thisgeh.com.github.confereai.ui.screens.NovaConsultaScreen
import thisgeh.com.github.confereai.ui.screens.SobreScreen
import thisgeh.com.github.confereai.viewmodel.ConsultaViewModel
import thisgeh.com.github.confereai.viewmodel.ContatoViewModel

@Composable
fun AppNavigation(
    consultaViewModel: ConsultaViewModel,
    contatoViewModel: ContatoViewModel
) {
    val navController = rememberNavController()


    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        merge(consultaViewModel.mensagens, contatoViewModel.mensagens).collect { mensagem ->
            val resultado = snackbarHostState.showSnackbar(
                message = mensagem.texto,
                actionLabel = mensagem.acao,
                withDismissAction = mensagem.acao == null,
                duration = SnackbarDuration.Short
            )
            if (resultado == SnackbarResult.ActionPerformed) {
                mensagem.aoClicarAcao?.invoke()
            }
        }
    }

    val abrirNovaConsulta = {
        consultaViewModel.limparEmpresa()
        navController.navigate(Rotas.NOVA_CONSULTA)
    }

    NavHost(navController = navController, startDestination = Rotas.HOME) {
        composable(Rotas.HOME) {
            HomeScreen(
                consultaViewModel = consultaViewModel,
                contatoViewModel = contatoViewModel,
                snackbarHostState = snackbarHostState,
                onNovaConsulta = abrirNovaConsulta,
                onHistorico = { navController.navigate(Rotas.HISTORICO) },
                onContatos = { navController.navigate(Rotas.CONTATOS) },
                onSobre = { navController.navigate(Rotas.SOBRE) }
            )
        }
        composable(Rotas.NOVA_CONSULTA) {
            NovaConsultaScreen(
                viewModel = consultaViewModel,
                snackbarHostState = snackbarHostState,
                onVoltar = { navController.popBackStack() },
                onConsultaSalva = { id ->

                    navController.navigate(Rotas.detalhe(id)) {
                        popUpTo(Rotas.NOVA_CONSULTA) { inclusive = true }
                    }
                }
            )
        }
        composable(Rotas.HISTORICO) {
            HistoricoScreen(
                viewModel = consultaViewModel,
                snackbarHostState = snackbarHostState,
                onVoltar = { navController.popBackStack() },
                onAbrirConsulta = { id -> navController.navigate(Rotas.detalhe(id)) },
                onNovaConsulta = abrirNovaConsulta
            )
        }
        composable(
            route = Rotas.DETALHE,
            arguments = listOf(navArgument("consultaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val consultaId = backStackEntry.arguments?.getInt("consultaId") ?: 0
            DetalheConsultaScreen(
                consultaId = consultaId,
                consultaViewModel = consultaViewModel,
                contatoViewModel = contatoViewModel,
                snackbarHostState = snackbarHostState,
                onVoltar = { navController.popBackStack() },
                onCadastrarContato = { navController.navigate(Rotas.CONTATOS) }
            )
        }
        composable(Rotas.CONTATOS) {
            ContatosScreen(
                viewModel = contatoViewModel,
                snackbarHostState = snackbarHostState,
                onVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.SOBRE) {
            SobreScreen(onVoltar = { navController.popBackStack() })
        }
    }
}
