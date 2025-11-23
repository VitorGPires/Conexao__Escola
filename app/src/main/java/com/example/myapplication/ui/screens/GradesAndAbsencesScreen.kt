package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodels.UserViewModel

enum class EvaluationType { PROVA, TRABALHO, EXERCICIO }
enum class FilterType { TODAS, BIMESTRE, PROVAS }

data class Evaluation(val name: String, val grade: Double, val date: String, val bimester: Int, val type: EvaluationType)
data class SubjectDetails(val name: String, val teacher: String, val average: Double, val icon: ImageVector, val evaluations: List<Evaluation>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesAndAbsencesScreen(
    navController: NavController, 
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    val allSubjects = listOf(
        SubjectDetails("Matemática", "Prof. João Silva", 9.2, Icons.Default.Calculate, listOf(
            Evaluation("Prova - Álgebra", 9.5, "15/10", 4, EvaluationType.PROVA),
            Evaluation("Trabalho - Geometria", 8.8, "10/10", 4, EvaluationType.TRABALHO),
            Evaluation("Exercícios", 9.3, "08/10", 3, EvaluationType.EXERCICIO)
        )),
        SubjectDetails("Português", "Prof. Maria Costa", 8.5, Icons.Default.Book, listOf(
            Evaluation("Redação - Narrativa", 8.0, "12/10", 4, EvaluationType.TRABALHO),
            Evaluation("Prova - Literatura", 9.0, "05/10", 3, EvaluationType.PROVA)
        )),
        SubjectDetails("Ciências", "Prof. Carlos Lima", 7.8, Icons.Default.Science, listOf(
            Evaluation("Lab - Química", 7.5, "18/10", 4, EvaluationType.TRABALHO),
            Evaluation("Prova - Física", 8.2, "20/10", 4, EvaluationType.PROVA)
        )),
        SubjectDetails("História", "Prof. Ana Pereira", 9.0, Icons.Default.History, listOf(
            Evaluation("Seminário - Brasil Colonial", 9.5, "22/10", 3, EvaluationType.PROVA)
        ))
    )

    var selectedFilter by remember { mutableStateOf(FilterType.TODAS) }
    var selectedBimester by remember { mutableStateOf<Int?>(1) }

    val filteredSubjects = remember(selectedFilter, selectedBimester) {
        when (selectedFilter) {
            FilterType.TODAS -> allSubjects
            FilterType.PROVAS -> allSubjects.map { subject ->
                subject.copy(evaluations = subject.evaluations.filter { it.type == EvaluationType.PROVA })
            }.filter { it.evaluations.isNotEmpty() }
            FilterType.BIMESTRE -> {
                if (selectedBimester == null) emptyList() else allSubjects.map { subject ->
                    subject.copy(evaluations = subject.evaluations.filter { it.bimester == selectedBimester })
                }.filter { it.evaluations.isNotEmpty() }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        StudentHeader(navController = navController, userViewModel = userViewModel)
        LazyColumn {
            item {
                FilterTabs(selectedFilter) { newFilter ->
                    selectedFilter = newFilter
                }
            }
            if (selectedFilter == FilterType.BIMESTRE) {
                item {
                    BimesterFilter(selectedBimester) { bimester ->
                        selectedBimester = bimester
                    }
                }
            }

            if (filteredSubjects.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhuma nota encontrada para este filtro.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(filteredSubjects) { subject ->
                    SubjectGradesCard(subject = subject)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun StudentHeader(navController: NavController, userViewModel: UserViewModel) {
    val userData = userViewModel.userData
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.primary)
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
        Spacer(modifier = Modifier.weight(1f))
        GradeChip(grade = 8.7, isAverage = true)
    }
}

@Composable
fun FilterTabs(selectedFilter: FilterType, onFilterSelected: (FilterType) -> Unit) {
    val tabs = FilterType.values()
    val selectedTabIndex = tabs.indexOf(selectedFilter)

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).clip(RoundedCornerShape(8.dp))
    ) {
        tabs.forEachIndexed { index, filter ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                text = { Text(filter.name.lowercase().replaceFirstChar { it.titlecase() }) },
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            )
        }
    }
}

@Composable
fun BimesterFilter(selectedBimester: Int?, onBimesterSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (1..4).forEach { bimester ->
            Button(
                onClick = { onBimesterSelected(bimester) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (bimester == selectedBimester) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (bimester == selectedBimester) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(text = "${bimester}º Bim")
            }
        }
    }
}

@Composable
fun SubjectGradesCard(subject: SubjectDetails) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = subject.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(subject.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(subject.teacher, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.weight(1f))
                GradeChip(grade = subject.average)
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (subject.evaluations.isEmpty()) {
                    Text("Nenhuma avaliação encontrada para este filtro.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    subject.evaluations.forEach { evaluation ->
                        EvaluationRow(evaluation = evaluation)
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluationRow(evaluation: Evaluation) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(evaluation.name, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text(evaluation.grade.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE6A045)) // This color is semantic, kept for now
        Spacer(modifier = Modifier.width(8.dp))
        Text(evaluation.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun GradeChip(grade: Double, isAverage: Boolean = false) {
    val backgroundColor = when {
        grade >= 8.5 -> Color(0xFFD7F9E9)
        grade >= 7.0 -> Color(0xFFFFF5E6)
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when {
        grade >= 8.5 -> Color(0xFF2E7D32)
        grade >= 7.0 -> Color(0xFFE6A045)
        else -> MaterialTheme.colorScheme.onErrorContainer
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GradesScreenPreview() {
    MyApplicationTheme {
        GradesAndAbsencesScreen(navController = rememberNavController())
    }
}
