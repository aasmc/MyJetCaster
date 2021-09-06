package ru.aasmc.myjetcaster.ui.home.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.aasmc.myjetcaster.Graph
import ru.aasmc.myjetcaster.data.Category
import ru.aasmc.myjetcaster.data.CategoryStore

class DiscoverViewModel(
    private val categoryStore: CategoryStore = Graph.categoryStore
) : ViewModel() {
    //  holds currently selected category
    private val _selectedCategory = MutableStateFlow<Category?>(null)

    // holds view state which the UI collects via [state]
    private val _state = MutableStateFlow(DiscoverViewState())

    val state: StateFlow<DiscoverViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // combines the latest values from each of the flows allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                categoryStore.categoriesSortedByPodcastCount()
                    .onEach { categories ->
                        // if we haven't got a selected category yet, select the first
                        if (categories.isNotEmpty() && _selectedCategory.value == null) {
                            _selectedCategory.value = categories[0]
                        }
                    },
                _selectedCategory
            ) { categories, selectedCategory ->
                DiscoverViewState(
                    categories = categories,
                    selectedCategory = selectedCategory
                )
            }.collect { _state.value = it }
        }
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }
}

data class DiscoverViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)



















