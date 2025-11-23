package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainHeader(navController: NavController? = null, showAverage: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE3F2FD))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navController?.previousBackStackEntry != null) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF4A90E2))
            }
        } else {
            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color(0xFF4A90E2))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Ana Silva Santos", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("9º Ano B - Matrícula: 2024001", fontSize = 14.sp, color = Color.Gray)
            Text("Período: 2024.2", fontSize = 14.sp, color = Color.Gray)
        }
        if (showAverage) {
            GradeChip(grade = 8.7, isAverage = true)
        }
    }
}

@Composable
fun GradeChip(grade: Double, isAverage: Boolean = false) {
    val backgroundColor = when {
        grade >= 8.5 -> Color(0xFFD7F9E9)
        grade >= 7.0 -> Color(0xFFFFF5E6)
        else -> Color(0xFFFEEEEE)
    }
    val contentColor = when {
        grade >= 8.5 -> Color(0xFF2E7D32)
        grade >= 7.0 -> Color(0xFFE6A045)
        else -> Color(0xFFD32F2F)
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = if (isAverage) 12.dp else 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = if (isAverage) "Média: $grade" else grade.toString(),
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}