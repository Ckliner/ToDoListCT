package com.example.todolistct

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolistct.ui.theme.ToDoListCTTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.emptyFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TaskDatabase.getDatabase(applicationContext)
        val dao = database.taskDao()

        setContent {
            ToDoListCTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ToDoListScreen(
                        modifier = Modifier.padding(innerPadding),
                        dao = dao
                    )
                }
            }
        }
    }
}

@Composable
fun ToDoListScreen(modifier: Modifier = Modifier, dao: TaskDao) {
    val coroutineScope = rememberCoroutineScope()
    val tasks by dao.getAllTasks().collectAsState(initial = emptyList())
    var taskText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("To-Do List", style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = taskText,
                onValueChange = { taskText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter a task") }
            )
            Button(
                onClick = {
                    if (taskText.isNotBlank()) {
                        coroutineScope.launch {
                            dao.insertTask(Task(name = taskText))
                            taskText = ""
                        }
                    }
                }
            ) {
                Text("Add")
            }
        }

        HorizontalDivider()

        LazyColumn {
            items(tasks) { task ->
                TaskRow(task = task, dao = dao)
            }
        }
    }
}

@Composable
fun TaskRow(task: Task, dao: TaskDao) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(task.name, style = MaterialTheme.typography.bodyLarge)
        Button(
            onClick = {
                coroutineScope.launch {
                    dao.deleteTask(task)
                }
            }
        ) {
            Text("Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoListPreview() {
    ToDoListCTTheme {
        // Mock TaskDao for preview purposes
        ToDoListScreen(
            dao = object : TaskDao {
                override fun getAllTasks() = emptyFlow<List<Task>>()
                override suspend fun insertTask(task: Task) {}
                override suspend fun deleteTask(task: Task) {}
            }
        )
    }
}
