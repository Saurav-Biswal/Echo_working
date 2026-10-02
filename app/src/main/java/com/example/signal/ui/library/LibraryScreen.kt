package com.example.signal.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.signal.data.local.MemoryEntity
import com.example.signal.ui.theme.SignalBlue
import com.example.signal.ui.theme.SignalSurfaceSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBack: () -> Unit,
    onMemoryClick: (Long) -> Unit
) {

    val viewModel: LibraryViewModel =
        viewModel()

    // --------------------------------------------------
    // OBSERVE VIEWMODEL
    // --------------------------------------------------

    val memories by
    viewModel.memories.collectAsState()

    val searchQuery by
    viewModel.searchQuery.collectAsState()

    val searchResults by
    viewModel.searchResults.collectAsState()

    val isSearching by
    viewModel.isSearching.collectAsState()

    // --------------------------------------------------
    // DECIDE WHAT TO DISPLAY
    // --------------------------------------------------

    val displayedMemories =
        if (isSearching) {

            searchResults

        } else {

            memories
        }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "MY SIGNAL",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,

                            contentDescription =
                                "Back"
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

            // --------------------------------------------------
            // SEARCH BAR
            // --------------------------------------------------

            OutlinedTextField(

                value = searchQuery,

                onValueChange = {
                    viewModel.search(it)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 12.dp
                    ),

                placeholder = {

                    Text(
                        text = "Search your Signal memories"
                    )
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Search,

                        contentDescription =
                            "Search"
                    )
                },

                trailingIcon = {

                    if (searchQuery.isNotBlank()) {

                        IconButton(

                            onClick = {
                                viewModel.clearSearch()
                            }

                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Close,

                                contentDescription =
                                    "Clear search"
                            )
                        }
                    }
                },

                singleLine = true
            )

            // --------------------------------------------------
            // MEMORY COUNT
            // --------------------------------------------------

            Text(

                text =
                    if (isSearching) {

                        "${displayedMemories.size} results"

                    } else {

                        "${displayedMemories.size} memories"
                    },

                modifier = Modifier.padding(

                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 8.dp
                ),

                style =
                    MaterialTheme.typography.titleMedium,

                fontWeight =
                    FontWeight.SemiBold
            )

            // --------------------------------------------------
            // EMPTY STATE
            // --------------------------------------------------

            if (displayedMemories.isEmpty()) {

                Column(

                    modifier =
                        Modifier.fillMaxSize(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Bookmark,

                        contentDescription =
                            null,

                        tint =
                            SignalBlue
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(

                        text =
                            if (isSearching) {

                                "No memories found"

                            } else {

                                "No memories yet"
                            },

                        style =
                            MaterialTheme.typography.titleMedium,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(

                        text =
                            if (isSearching) {

                                "Try a different search"

                            } else {

                                "Save something to Signal"
                            },

                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                // --------------------------------------------------
                // MEMORY LIST
                // --------------------------------------------------

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(

                            start =
                                20.dp,

                            end =
                                20.dp,

                            top =
                                8.dp,

                            bottom =
                                24.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)

                ) {

                    items(

                        items =
                            displayedMemories,

                        key = {
                            it.id
                        }

                    ) { memory ->

                        MemoryCard(

                            memory =
                                memory,

                            onClick = {

                                onMemoryClick(
                                    memory.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryCard(

    memory: MemoryEntity,

    onClick: () -> Unit

) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    SignalSurfaceSoft
            ),

        elevation =
            CardDefaults.cardElevation(

                defaultElevation =
                    0.dp
            )

    ) {

        Column(

            modifier =
                Modifier.padding(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)

        ) {

            // --------------------------------------------------
            // SOURCE TYPE
            // --------------------------------------------------

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Icon(

                    imageVector =
                        Icons.Default.Bookmark,

                    contentDescription =
                        null,

                    tint =
                        SignalBlue
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(

                    text =
                        memory.sourceType.ifBlank {

                            "Web"
                        },

                    style =
                        MaterialTheme.typography.labelMedium,

                    color =
                        SignalBlue,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            // --------------------------------------------------
            // TITLE
            // --------------------------------------------------

            Text(

                text =
                    memory.title.ifBlank {

                        "Untitled Memory"
                    },

                style =
                    MaterialTheme.typography.titleLarge,

                fontWeight =
                    FontWeight.Bold
            )

            // --------------------------------------------------
            // SUMMARY
            // --------------------------------------------------

            if (
                memory.summary.isNotBlank()
            ) {

                Text(

                    text =
                        memory.summary,

                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }

            // --------------------------------------------------
            // KEYWORDS
            // --------------------------------------------------

            if (
                memory.keywords.isNotBlank()
            ) {

                Text(

                    text =
                        memory.keywords,

                    style =
                        MaterialTheme.typography.labelMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            // --------------------------------------------------
            // TARGET LOCATION
            // --------------------------------------------------

            if (
                memory.targetLocation.isNotBlank()
            ) {

                Row(

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.LocationOn,

                        contentDescription =
                            null,

                        tint =
                            SignalBlue
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(

                        text =
                            memory.targetLocation,

                        style =
                            MaterialTheme.typography.bodyMedium,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}