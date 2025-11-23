package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.myapplication.AppScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth

// Data class for the grades history
data class BimesterGrade(val name: String, val grade: String)

@OptIn(ExperimentalMaterial3Api::class) // Annotation to fix the Card error
@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
    appNavController: NavController
) {
    val userData = userViewModel.userData

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.Start)
        )

        if (userData != null) {
            // Student Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(userData.nome, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(userData.email, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Grades History Section - As requested
            GradesHistorySection()
            Spacer(modifier = Modifier.height(24.dp))

            // Menu Options
            ProfileMenuItem(
                text = "Meus Dados",
                icon = Icons.Default.Person,
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(
                text = "Notificações",
                icon = Icons.Default.Notifications,
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(
                text = "Segurança",
                icon = Icons.Default.Security,
                onClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            ProfileMenuItem(
                text = "Sair",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    userViewModel.clearUserData() // Limpa os dados do ViewModel
                    appNavController.navigate(AppScreen.Auth.route) {
                        popUpTo(appNavController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                isLogout = true
            )
        } else {
            // Loading state or placeholder
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Annotation to fix the Card error
@Composable
fun GradesHistorySection() {
    val history = listOf(
        BimesterGrade("1º Bimestre", "Média Final: 8.0"),
        BimesterGrade("2º Bimestre", "Média Final: 9.2"),
        BimesterGrade("3º Bimestre", "Média Final: 7.5")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Histórico de Notas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                history.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item.name, fontWeight = FontWeight.Bold)
                        Text(text = item.grade, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (index < history.lastIndex) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // Annotation to fix the Card error
@Composable
fun ProfileMenuItem(text: String, icon: ImageVector, onClick: () -> Unit, isLogout: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLogout) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            if (!isLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    MyApplicationTheme {
        // This preview will not have a real NavController, so the logout button will not work here.
        // PerfilScreen(appNavController = rememberNavController())
    }
}
