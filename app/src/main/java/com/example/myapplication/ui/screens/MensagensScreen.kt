package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
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
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.composables.MainHeader
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodels.UserViewModel

data class Message(val title: String, val sender: String, val time: String, val icon: ImageVector, val read: Boolean)

@Composable
fun MensagensScreen(
    navController: NavController, 
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    val messages = listOf(
        Message("Reunião de Pais e Mestres", "Secretaria", "Ontem", Icons.Default.Campaign, false),
        Message("Atualização de Calendário", "Coordenação", "2 dias atrás", Icons.Default.Info, false),
        Message("Festa Junina - Convite", "Diretoria", "2 dias atrás", Icons.Default.Info, true),
        Message("Lembrete: Pagamento", "Financeiro", "3 dias atrás", Icons.Default.CheckCircle, true)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
    ) {
        MainHeader(navController = navController, userViewModel = userViewModel)

        Text(
            "Caixa de Entrada",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) {
                MessageCard(message = it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageCard(message: Message) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (message.read) Color(0xFFF5F5F5) else Color.White),
        elevation = CardDefaults.cardElevation(if (message.read) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (message.read) Color.LightGray else Color(0xFF4A90E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(message.icon, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(message.title, fontWeight = if (message.read) FontWeight.Normal else FontWeight.Bold, fontSize = 16.sp)
                Text(message.sender, fontSize = 14.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(message.time, fontSize = 12.sp, color = Color.Gray)
                if (!message.read) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4A90E2)))
                }
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp).padding(start = 8.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MensagensScreenPreview() {
    MyApplicationTheme {
        MensagensScreen(rememberNavController())
    }
}
