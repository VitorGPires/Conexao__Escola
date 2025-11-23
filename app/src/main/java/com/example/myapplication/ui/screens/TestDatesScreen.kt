package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
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

data class TestInfo(val subject: String, val date: String, val content: String, val icon: ImageVector)

@Composable
fun TestDatesScreen(
    navController: NavController, 
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    val tests = listOf(
        TestInfo("Prova de Matemática", "25/05/2025", "Álgebra e Geometria", Icons.Default.Calculate),
        TestInfo("Prova de Português", "27/05/2025", "Literatura e Gramática", Icons.Default.Book),
        TestInfo("Prova de História", "29/05/2025", "Brasil Colônia", Icons.Default.History),
        TestInfo("Prova de Ciências", "03/06/2025", "Física e Química", Icons.Default.Science)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
    ) {
        MainHeader(navController = navController, userViewModel = userViewModel)

        Text(
            "Calendário de Provas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tests) {
                TestCard(test = it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestCard(test: TestInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = test.icon,
                    contentDescription = null,
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(test.subject, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(test.content, fontSize = 14.sp, color = Color.Gray)
            }
            Text(test.date, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TestDatesScreenPreview() {
    MyApplicationTheme {
        TestDatesScreen(rememberNavController())
    }
}
