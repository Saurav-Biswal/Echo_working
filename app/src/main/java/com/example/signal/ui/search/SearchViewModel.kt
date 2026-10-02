package com.example.signal.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signal.data.local.MemoryEntity
import com.example.signal.data.local.MemoryDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    private val memoryDao: MemoryDao
) : ViewModel() {

    private val _query = MutableStateFlow("")

    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<MemoryEntity>>(emptyList())

    val results: StateFlow<List<MemoryEntity>> = _results.asStateFlow()

    init {
        observeMemories()
    }

    fun updateQuery(value: String) {
        _query.value = value
        search(value)
    }

    private fun observeMemories() {

        viewModelScope.launch {

            memoryDao.getAllMemories().collectLatest { memories ->

                searchMemories(
                    memories = memories,
                    query = _query.value
                )
            }
        }
    }

    private fun search(query: String) {

        viewModelScope.launch {

            val memories = memoryDao.getAllMemoriesSnapshot()

            searchMemories(
                memories = memories,
                query = query
            )
        }
    }

    private fun searchMemories(
        memories: List<MemoryEntity>,
        query: String
    ) {

        if (query.isBlank()) {
            _results.value = memories
            return
        }

        val words = query
            .lowercase()
            .split(
                Regex("\\s+")
            )
            .filter {
                it.isNotBlank()
            }

        val ranked = memories
            .map { memory ->

                var score = 0

                val title = memory.title.lowercase()
                val summary = memory.summary.lowercase()
                val keywords = memory.keywords.lowercase()
                val content = memory.content.lowercase()
                val source = memory.sourceType.lowercase()
                val targetLocation = memory.targetLocation.lowercase()

                words.forEach { word ->

                    if (title.contains(word)) {
                        score += 100
                    }

                    if (keywords.contains(word)) {
                        score += 80
                    }

                    if (targetLocation.contains(word)) {
                        score += 70
                    }

                    if (summary.contains(word)) {
                        score += 50
                    }

                    if (source.contains(word)) {
                        score += 30
                    }

                    if (content.contains(word)) {
                        score += 20
                    }
                }

                memory to score
            }
            .filter {
                it.second > 0
            }
            .sortedByDescending {
                it.second
            }
            .map {
                it.first
            }

        _results.value = ranked
    }
}
class SearchViewModelFactory(
    private val memoryDao: com.example.signal.data.local.MemoryDao
) : androidx.lifecycle.ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(
        modelClass: Class<T>
    ): T {
        return SearchViewModel(memoryDao) as T
    }
}