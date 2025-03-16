package com.example.calculator2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.calculator2.ui.theme.Calculator2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем тему приложения
        setContent {
            Calculator2Theme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    // Состояние для хранения введенного текста
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    // Состояние для отображения ошибок
    var errorMessage by remember { mutableStateOf("") }

    // Scaffold — основа интерфейса, помогает организовать размещение компонентов
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Колонка для вертикального размещения элементов
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    // Поле ввода для выражений
                    TextField(
                        value = inputText,
                        onValueChange = {
                            val text = it.text
                            inputText = it // Обновляем состояние с введенным текстом
                            errorMessage = "" // Сбрасываем сообщение об ошибке

                            // Проверяем, завершено ли выражение символом "="
                            if (text.endsWith("=")) {
                                val expression = text.substringBefore("=") // Получаем выражение до "="
                                val result = try {
                                    // Вычисляем результат
                                    evaluateExpression(expression)
                                } catch (e: Exception) {
                                    e.message ?: "Error" // Устанавливаем сообщение об ошибке
                                }
                                // Отображаем результат
                                inputText = TextFieldValue("$expression = $result")
                            }
                        },
                        label = { Text("Enter numbers") }, // Подсказка
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number // Используем цифровую клавиатуру
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Если есть сообщение об ошибке, выводим его
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage, // Сообщение об ошибке
                            color = Color.Red, // Красный цвет текста ошибки
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Кнопка для очистки текстового поля
                    Button(
                        onClick = {
                            inputText = TextFieldValue("") // Очищаем текст
                            errorMessage = "" // Сбрасываем ошибки
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(0.5f), // Кнопка занимает 50% ширины экрана
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue, // Синий цвет кнопки
                            contentColor = Color.White // Белый цвет текста
                        )
                    ) {
                        Text("Clear") // Текст на кнопке
                    }
                }
            }
        }
    )
}

// Функция для вычисления результата выражения
fun evaluateExpression(expression: String): String {
    // Удаляем пробелы из выражения
    val sanitizedExpression = expression.replace(" ", "")
    // Разбиваем выражение на операнды по символам операций
    val parts = sanitizedExpression.split('+', '-', '*', '/')

    // Проверяем, есть ли два операнда
    if (parts.size == 2) {
        val operand1 = parts[0].toDoubleOrNull() // Первый операнд
        val operand2 = parts[1].toDoubleOrNull() // Второй операнд

        // Если оба операнда корректны, вычисляем результат
        if (operand1 != null && operand2 != null) {
            return when {
                sanitizedExpression.contains('+') -> (operand1 + operand2).toString()
                sanitizedExpression.contains('-') -> (operand1 - operand2).toString()
                sanitizedExpression.contains('*') -> (operand1 * operand2).toString()
                sanitizedExpression.contains('/') -> {
                    // Проверяем деление на ноль
                    if (operand2 == 0.0) throw IllegalArgumentException("Cannot divide by zero")
                    (operand1 / operand2).toString()
                }
                else -> throw IllegalArgumentException("Invalid operation") // Ошибка операции
            }
        }
    }

    // Если выражение некорректно, генерируем исключение
    throw IllegalArgumentException("Invalid expression")
}

@Preview(showBackground = true)
@Composable
fun CalculatorAppPreview() {
    // Предварительный просмотр интерфейса
    Calculator2Theme {
        CalculatorApp()
    }
}
