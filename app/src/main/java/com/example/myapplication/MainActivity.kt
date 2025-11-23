package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodels.UserViewModel

// --- Data Classes (Modelos de Dados) ---
data class ClassInfo(val subject: String, val teacher: String, val time: String, val room: String, val icon: ImageVector)
data class NotificationInfo(val title: String, val description: String, val time: String, val icon: ImageVector, val color: Color)
data class QuickAccessItem(val title: String, val subtitle: String, val icon: ImageVector, val route: String, val backgroundColor: Color, val iconColor: Color)

// --- Estrutura da Navegação ---
sealed class AppScreen(val route: String) {
    object Auth : AppScreen("auth")
    object Main : AppScreen("main")
}

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Início", Icons.Default.Home)
    object Grades : BottomNavItem("grades", "Notas", Icons.Default.Assessment)
    object Agenda : BottomNavItem("agenda", "Agenda", Icons.Default.CalendarToday)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
}

val bottomBarItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Grades,
    BottomNavItem.Agenda,
    BottomNavItem.Profile
)

// --- Início do Aplicativo ---
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

// --- Controladores de Navegação ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    NavHost(navController = navController, startDestination = AppScreen.Auth.route) {
        composable(AppScreen.Auth.route) {
            AuthNavigation(userViewModel = userViewModel, onLoginOrRegisterSuccess = {
                navController.navigate(AppScreen.Main.route) {
                    popUpTo(AppScreen.Auth.route) { inclusive = true }
                }
            })
        }
        composable(AppScreen.Main.route) {
            MainScreenScaffold(userViewModel = userViewModel, appNavController = navController)
        }
    }
}

@Composable
fun AuthNavigation(userViewModel: UserViewModel, onLoginOrRegisterSuccess: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = onLoginOrRegisterSuccess,
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                userViewModel = userViewModel,
                onRegisterSuccess = onLoginOrRegisterSuccess,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainScreenScaffold(userViewModel: UserViewModel, appNavController: NavController) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomBarItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = BottomNavItem.Home.route, Modifier.padding(innerPadding)) {
            composable(BottomNavItem.Home.route) { HomeScreen(navController) }
            composable(BottomNavItem.Grades.route) { GradesAndAbsencesScreen(navController = navController, userViewModel = userViewModel) }
            composable(BottomNavItem.Agenda.route) { AgendaScreen() }
            composable(BottomNavItem.Profile.route) { PerfilScreen(userViewModel = userViewModel, appNavController = appNavController) }

            // Rotas do Acesso Rápido (Adicionadas)
            composable("absences") { FaltasScreen(navController = navController, userViewModel = userViewModel) }
            composable("tests") { TestDatesScreen(navController = navController, userViewModel = userViewModel) }
            composable("classes") { TodaysClassesScreen(navController = navController, userViewModel = userViewModel) }
            composable("documents") { RequestDocumentsScreen(navController = navController, userViewModel = userViewModel) }
            composable("messages") { MensagensScreen(navController = navController, userViewModel = userViewModel) }
        }
    }
}

// --- Telas Principais ---
@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp)
    ) {
        item { SummarySection() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { QuickAccessGrid(navController) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { TodaysClassesSection() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { RecentNotificationsSection() }
    }
}

// --- Componentes da HomeScreen (Restaurados) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummarySection() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard("Média Geral", "8.5", Icons.Default.TrendingUp, Modifier.weight(1f))
        SummaryCard("Frequência", "95%", Icons.Default.CalendarToday, Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                Text(title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(icon, title, tint = Color(0xFF1E88E5))
        }
    }
}

@Composable
fun QuickAccessGrid(navController: NavController) {
    val items = listOf(
        QuickAccessItem("Notas", "Consultar boletim", Icons.Default.Star, BottomNavItem.Grades.route, Color(0xFFE3F2FD), Color(0xFF4A90E2)),
        QuickAccessItem("Faltas", "Ver frequência", Icons.Default.PersonOff, "absences", Color(0xFFFEEEEE), Color(0xFFD32F2F)),
        QuickAccessItem("Provas", "Calendário de avaliações", Icons.Default.Event, "tests", Color(0xFFE8EAF6), Color(0xFF3F51B5)),
        QuickAccessItem("Aulas", "Horário do dia", Icons.Default.Book, "classes", Color(0xFFE0F2F1), Color(0xFF009688)),
        QuickAccessItem("Documentos", "Solicitar documentos", Icons.Default.Description, "documents", Color(0xFFFFF3E0), Color(0xFFFFA000)),
        QuickAccessItem("Mensagens", "Comunicados", Icons.Default.Mail, "messages", Color(0xFFF3E5F5), Color(0xFF8E24AA))
    )
    Column {
        Text("Acesso Rápido", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        LazyVerticalGrid(GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(260.dp)) {
            items(items) { item -> QuickAccessCard(item) { navController.navigate(item.route) } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAccessCard(item: QuickAccessItem, onClick: () -> Unit) {
    Card(Modifier.clickable(onClick = onClick), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(item.backgroundColor).padding(8.dp)) {
                Icon(item.icon, item.title, tint = item.iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(item.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodaysClassesSection() {
    val classes = listOf(
        ClassInfo("Matemática", "Prof. Ana Silva", "07:30", "Sala 201", Icons.Default.Calculate),
        ClassInfo("Ciências", "Prof. Carlos Lima", "08:20", "Lab 103", Icons.Default.Science),
        ClassInfo("Português", "Prof. Maria Santos", "09:10", "Sala 205", Icons.Default.Book)
    )
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Aulas de Hoje", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("15/11/2024", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            classes.forEach { ClassRow(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassRow(c: ClassInfo) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(c.icon, c.subject, modifier = Modifier.size(24.dp), tint = Color(0xFF4A90E2))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(c.subject, fontWeight = FontWeight.Bold)
                    Text(c.teacher, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(c.time, fontWeight = FontWeight.Bold)
                Text(c.room, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentNotificationsSection() {
    val notifications = listOf(
        NotificationInfo("Prova de História", "Agendada para 20/11/2024 - Conteúdo: Revolução Industrial", "Há 2 horas", Icons.Default.Warning, Color(0xFFFFA000)),
        NotificationInfo("Reunião de Pais", "Convocação para reunião no dia 25/11/2024 às 19h", "Ontem", Icons.Default.Info, Color(0xFF1976D2))
    )
    Column {
        Text("Notificações Recentes", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            notifications.forEach { NotificationCard(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCard(n: NotificationInfo) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = n.color.copy(alpha = 0.1f))) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(n.icon, n.title, tint = n.color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(n.title, fontWeight = FontWeight.Bold)
                Text(n.description, fontSize = 14.sp)
                Text(n.time, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen(rememberNavController())
    }
}
