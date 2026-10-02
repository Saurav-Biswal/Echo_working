package com.example.signal.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.signal.data.local.MemoryEntity
import com.example.signal.data.local.SignalDatabase
import com.example.signal.ui.theme.SignalBlue
import com.example.signal.ui.theme.SignalSurfaceSoft
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val database = SignalDatabase.getDatabase(context)

    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(database.memoryDao())
    )

    val query by viewModel.query.collectAsState()

    val results by viewModel.results.collectAsState()

    Scaffold(

        topBar = {

            androidx.compose.material3.TopAppBar(

                title = {
                    Text(
                        text = "SEARCH SIGNAL",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            OutlinedTextField(

                value = query,

                onValueChange = {
                    viewModel.updateQuery(it)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),

                placeholder = {
                    Text(
                        "Search your memories..."
                    )
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },

                singleLine = true
            )

            if (query.isBlank()) {

                Text(
                    text = "${results.size} memories",
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 4.dp
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

            } else {

                Text(
                    text = "${results.size} results",
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 4.dp
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (results.isEmpty()) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SignalBlue
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Nothing found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Try another search",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                LazyColumn(

                    modifier = Modifier.fillMaxSize(),

                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),

                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(
                        items = results,
                        key = {
                            it.id
                        }
                    ) { memory ->

                        SearchMemoryCard(
                            memory = memory
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchMemoryCard(
    memory: MemoryEntity
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = SignalSurfaceSoft
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = SignalBlue
                )

                Spacer(
                    modifier = Modifier.padding(4.dp)
                )

                Text(
                    text = memory.sourceType.ifBlank {
                        "Web"
                    },
                    color = SignalBlue,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Text(
                text = memory.title.ifBlank {
                    "Untitled Memory"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (memory.summary.isNotBlank()) {

                Text(
                    text = memory.summary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (memory.keywords.isNotBlank()) {

                Text(
                    text = memory.keywords,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            if (memory.targetLocation.isNotBlank()) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SignalBlue
                    )

                    Spacer(
                        modifier = Modifier.padding(3.dp)
                    )

                    Text(
                        text = memory.targetLocation,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}