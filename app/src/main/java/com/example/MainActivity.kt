package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Task
import com.example.ui.TaskViewModel
import com.example.ui.components.UniverseBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) { // Always dark theme for cosmic universe feeling!
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // 1. Kinetic Starry Universe Background
                        UniverseBackground()

                        // 2. Main Content Dashboard
                        TaskDashboardScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDashboardScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasksState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val priorityFilter by viewModel.priorityFilter.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    // Statistics calculations for the cosmic progress bar
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // HEADER: App Name and subtitle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "do",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White
                    )
                    Text(
                        text = ".",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = Color(0xFF00E5FF) // Cyber Cyan dot
                    )
                }
                Text(
                    text = "Chart your cosmic workflow",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontFamily = FontFamily.SansSerif
                )
            }

            // Quick add button in Header
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFA07CFE), Color(0xFF00E5FF))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("header_add_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // COSMIC PROGRESS BAR (Glassmorphic Card)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0x1AFFFFFF)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Orbit Completion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "$completedTasks of $totalTasks resolved",
                        fontSize = 12.sp,
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Clean glowing progress line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0x33FFFFFF), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFA07CFE), Color(0xFF00E5FF))
                                ),
                                CircleShape
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SEARCH BAR (Glassmorphic)
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search the cosmos...", color = Color.White.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x15FFFFFF),
                unfocusedContainerColor = Color(0x10FFFFFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x15FFFFFF), RoundedCornerShape(14.dp))
                .testTag("search_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PRIORITY HORIZONTAL FILTERS
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                PriorityFilterChip(
                    label = "All Orbits",
                    isSelected = priorityFilter == null,
                    activeColor = Color(0xFFA07CFE),
                    onClick = { viewModel.priorityFilter.value = null }
                )
            }
            item {
                PriorityFilterChip(
                    label = "High",
                    isSelected = priorityFilter == "HIGH",
                    activeColor = Color(0xFFFF5B8C), // Supernova pink
                    onClick = { viewModel.priorityFilter.value = "HIGH" }
                )
            }
            item {
                PriorityFilterChip(
                    label = "Medium",
                    isSelected = priorityFilter == "MEDIUM",
                    activeColor = Color(0xFFFFB800), // Solar yellow
                    onClick = { viewModel.priorityFilter.value = "MEDIUM" }
                )
            }
            item {
                PriorityFilterChip(
                    label = "Low",
                    isSelected = priorityFilter == "LOW",
                    activeColor = Color(0xFF00E5FF), // Cosmic cyan
                    onClick = { viewModel.priorityFilter.value = "LOW" }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TASK LIST OR EMPTY STATE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (tasks.isEmpty()) {
                EmptyUniverseState(
                    onAddTaskClick = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCardItem(
                            task = task,
                            onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                            onEditClick = { taskToEdit = task },
                            onDeleteClick = { viewModel.deleteTask(task) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom of screen
                    }
                }
            }
        }
    }

    // Task Add Dialog
    if (showAddDialog) {
        TaskFormDialog(
            title = "Launch Task",
            onDismiss = { showAddDialog = false },
            onSave = { name, desc, priority, color ->
                viewModel.addTask(name, desc, priority, color)
                showAddDialog = false
            }
        )
    }

    // Task Edit Dialog
    taskToEdit?.let { task ->
        TaskFormDialog(
            title = "Recalibrate Task",
            initialTask = task,
            onDismiss = { taskToEdit = null },
            onSave = { name, desc, priority, color ->
                viewModel.updateTask(
                    task.copy(
                        title = name,
                        description = desc,
                        priority = priority,
                        colorHex = color
                    )
                )
                taskToEdit = null
            }
        )
    }
}

@Composable
fun PriorityFilterChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f, label = "chipScale")

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) activeColor.copy(alpha = 0.25f) else Color(0x10FFFFFF)
            )
            .border(
                width = 1.2.dp,
                color = if (isSelected) activeColor else Color(0x1AFFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (label != "All Orbits") {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(activeColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCardItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val accentColor = remember(task.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(task.colorHex))
        } catch (e: Exception) {
            Color(0xFFA07CFE) // Fallback purple
        }
    }

    val alphaMultiplier = if (task.isCompleted) 0.5f else 1.0f

    // Cosmic glow and background colors based on priority
    val priorityIndicator = when (task.priority) {
        "HIGH" -> "✦ High"
        "MEDIUM" -> "✧ Med"
        else -> "◦ Low"
    }

    val priorityColor = when (task.priority) {
        "HIGH" -> Color(0xFFFF5B8C)
        "MEDIUM" -> Color(0xFFFFB800)
        else -> Color(0xFF00E5FF)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0x1F110B24) // Deep universe glassmorphic card
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (task.isCompleted) 0.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = accentColor.copy(alpha = 0.2f),
                spotColor = accentColor.copy(alpha = 0.3f)
            )
            .border(
                width = 1.dp,
                color = if (task.isCompleted) Color(0x10FFFFFF) else accentColor.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("task_item_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onToggleCompletion,
                    onLongClick = onEditClick
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cosmic custom checkbox with completion animation
            CosmicCheckbox(
                checked = task.isCompleted,
                onCheckedChange = onToggleCompletion,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Task texts
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Task title
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = alphaMultiplier),
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Priority Badge
                    Box(
                        modifier = Modifier
                            .background(priorityColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .border(0.8.dp, priorityColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = priorityIndicator,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = priorityColor
                        )
                    }
                }

                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f * alphaMultiplier),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Edit Action
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Task",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Delete Action
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = Color(0xFFFF5B8C).copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CosmicCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "CheckboxScale"
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .scale(scale)
            .border(
                width = 2.dp,
                color = if (checked) accentColor else Color.White.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .background(
                color = if (checked) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Disable default ripple to keep cosmic layout clean
                onClick = onCheckedChange
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun EmptyUniverseState(
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Beautiful minimal galaxy spiral drawing
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFF00E5FF),
            modifier = Modifier
                .size(64.dp)
                .shadow(16.dp, CircleShape, spotColor = Color(0xFF00E5FF))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your universe is clear.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap the plus button or below to chart a new task into orbit.",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddTaskClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA07CFE)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Initialize Task", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TaskFormDialog(
    title: String,
    initialTask: Task? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var priority by remember { mutableStateOf(initialTask?.priority ?: "MEDIUM") }
    var selectedColorHex by remember { mutableStateOf(initialTask?.colorHex ?: "#A07CFE") }

    val cosmicColors = listOf(
        "#A07CFE" to "Nebula Purple",
        "#FF5B8C" to "Supernova Pink",
        "#FFB800" to "Solar Yellow",
        "#00E5FF" to "Cosmic Cyan",
        "#00E676" to "Aurora Green",
        "#FF7A5C" to "Stellar Coral"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF130E26) // Deep stellar navy background
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x33A07CFE), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.6f))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Name Input
                Text(
                    text = "Task Vector",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Task title...", color = Color.White.copy(alpha = 0.4f)) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x10FFFFFF),
                        unfocusedContainerColor = Color(0x08FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color(0xFFA07CFE),
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_name_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description Input
                Text(
                    text = "Mission Brief",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Task descriptions/notes...", color = Color.White.copy(alpha = 0.4f)) },
                    minLines = 2,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x10FFFFFF),
                        unfocusedContainerColor = Color(0x08FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color(0xFFA07CFE),
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Priority Segment
                Text(
                    text = "Priority Level",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                        val isSel = priority == p
                        val activeBgColor = when (p) {
                            "HIGH" -> Color(0xFFFF5B8C)
                            "MEDIUM" -> Color(0xFFFFB800)
                            else -> Color(0xFF00E5FF)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) activeBgColor.copy(alpha = 0.25f) else Color(0x08FFFFFF))
                                .border(
                                    width = 1.2.dp,
                                    color = if (isSel) activeBgColor else Color(0x1AFFFFFF),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { priority = p }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) activeBgColor else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color picker
                Text(
                    text = "Stellar Color Signature",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    cosmicColors.forEach { (hex, _) ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = selectedColorHex == hex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .padding(if (isSelected) 3.dp else 0.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColorHex = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Action Button (Glowing)
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(name, description, priority, selectedColorHex)
                        }
                    },
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA07CFE),
                        disabledContainerColor = Color(0x22FFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (name.isNotBlank()) 8.dp else 0.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color(0xFFA07CFE)
                        )
                        .testTag("dialog_save_button")
                ) {
                    Text(
                        text = if (initialTask == null) "Launch Into Orbit" else "Recalibrate Orbit",
                        fontWeight = FontWeight.Bold,
                        color = if (name.isNotBlank()) Color.White else Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
