package com.example.myapplication.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodels.UserData
import com.example.myapplication.ui.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var turma by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var periodo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isTurmaExpanded by remember { mutableStateOf(false) }
    val turmas = listOf("9º Ano A", "9º Ano B", "1º Ano A", "1º Ano B", "2º Ano A", "2º Ano B", "3º Ano A", "3º Ano B")

    var isPeriodoExpanded by remember { mutableStateOf(false) }
    val periodos = listOf("2025.1", "2025.2", "2026.1", "2026.2")

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Cadastro de Aluno",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome Completo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // Turma Dropdown
        ExposedDropdownMenuBox(expanded = isTurmaExpanded, onExpandedChange = { isTurmaExpanded = !isTurmaExpanded }) {
            OutlinedTextField(
                value = turma,
                onValueChange = {},
                readOnly = true,
                label = { Text("Turma") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTurmaExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = isTurmaExpanded, onDismissRequest = { isTurmaExpanded = false }) {
                turmas.forEach { selectedTurma ->
                    DropdownMenuItem(
                        text = { Text(selectedTurma) },
                        onClick = { 
                            turma = selectedTurma
                            isTurmaExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = matricula, onValueChange = { matricula = it }, label = { Text("Matrícula") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // Período Dropdown
        ExposedDropdownMenuBox(expanded = isPeriodoExpanded, onExpandedChange = { isPeriodoExpanded = !isPeriodoExpanded }) {
            OutlinedTextField(
                value = periodo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Período") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPeriodoExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = isPeriodoExpanded, onDismissRequest = { isPeriodoExpanded = false }) {
                periodos.forEach { selectedPeriodo ->
                    DropdownMenuItem(
                        text = { Text(selectedPeriodo) },
                        onClick = { 
                            periodo = selectedPeriodo
                            isPeriodoExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), visualTransformation = PasswordVisualTransformation())
        
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                            val firebaseUser = authResult.user

                            if (firebaseUser != null) {
                                val userData = UserData(
                                    nome = nome,
                                    email = email,
                                    turma = turma,
                                    matricula = matricula,
                                    periodo = periodo
                                )
                                firestore.collection("users").document(firebaseUser.uid).set(userData).await()
                                userViewModel.loadUserData(userData) // Carrega os dados no ViewModel
                                onRegisterSuccess()
                            } else {
                                errorMessage = "Ocorreu um erro ao criar o usuário."
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Erro desconhecido."
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    errorMessage = "Por favor, preencha e-mail e senha."
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Cadastrar", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Já tem uma conta? Faça login")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MyApplicationTheme {
        RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
    }
}
