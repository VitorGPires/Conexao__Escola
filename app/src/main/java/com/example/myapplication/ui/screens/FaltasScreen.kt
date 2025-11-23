package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

data class AbsenceDetails(val subject: String, val absences: Int, val limit: Int, val icon: ImageVector)

@Composable
fun FaltasScreen(
    navController: NavController, 
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    val absenceData = listOf(
        AbsenceDetails("Matemática", 5, 20, Icons.Default.Calculate),
        AbsenceDetails("Português", 2, 20, Icons.Default.Book),
        AbsenceDetails("Ciências", 7, 20, Icons.Default.Science),
        AbsenceDetails("História", 1, 20, Icons.Default.History)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
    ) {
        MainHeader(navController = navController, userViewModel = userViewModel)

        AbsenceSummary()

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Faltas por Matéria", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(absenceData) {
                AbsenceCard(details = it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenceSummary() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        SummaryItem("Total de Faltas", "15", Icons.Default.ArrowDownward, Color(0xFFD32F2F))
        SummaryItem("Frequência", "95%", Icons.Default.ArrowUpward, Color(0xFF2E7D32))
    }
}

@Composable
fun SummaryItem(title: String, value: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = color)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenceCard(details: AbsenceDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = details.icon, contentDescription = null, tint = Color(0xFF4A90E2), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = details.subject, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Limite de ${details.limit} faltas", fontSize = 12.sp, color = Color.Gray)
            }
            Text(text = "${details.absences}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = if (details.absences > details.limit * 0.75) Color(0xFFD32F2F) else Color.DarkGray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FaltasScreenPreview() {
    MyApplicationTheme {
        FaltasScreen(rememberNavController())
    }
}
