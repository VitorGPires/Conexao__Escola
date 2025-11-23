package com.example.myapplication.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.viewmodels.UserViewModel

@Composable
fun MainHeader(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
) {
    val userData = userViewModel.userData
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (userData != null) {
            Column {
                Text(userData.nome, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("${userData.turma} - Matrícula: ${userData.matricula}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Período: ${userData.periodo}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column {
                Text("Carregando...", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Carregando...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}