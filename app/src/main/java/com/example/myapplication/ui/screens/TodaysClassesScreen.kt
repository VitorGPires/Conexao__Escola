package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
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

// Re-defining data class for this screen with more details.
data class ClassDetails(val subject: String, val teacher: String, val time: String, val room: String, val icon: ImageVector)

@Composable
fun TodaysClassesScreen(
    navController: NavController, 
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    val classes = listOf(
        ClassDetails("Matemática", "Prof. Ana Silva", "07:30", "Sala 201", Icons.Default.Calculate),
        ClassDetails("Ciências", "Prof. Carlos Lima", "08:20", "Lab 103", Icons.Default.Science),
        ClassDetails("Português", "Prof. Maria Santos", "09:10", "Sala 205", Icons.Default.Book),
        ClassDetails("História", "Prof. João Costa", "10:20", "Sala 101", Icons.Default.History),
        ClassDetails("Geografia", "Prof. Bia Pereira", "11:10", "Sala 302", Icons.Default.Public)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MainHeader(navController = navController, userViewModel = userViewModel)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Aulas de Hoje", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("15/11/2024", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(classes) { classItem ->
                ClassDetailsCard(classDetails = classItem)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsCard(classDetails: ClassDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = classDetails.icon,
                contentDescription = classDetails.subject,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = classDetails.subject, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = classDetails.teacher, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = classDetails.time, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = classDetails.room, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TodaysClassesScreenPreview() {
    MyApplicationTheme {
        TodaysClassesScreen(rememberNavController())
    }
}
