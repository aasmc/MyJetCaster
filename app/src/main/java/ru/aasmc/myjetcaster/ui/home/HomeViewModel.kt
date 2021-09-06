package ru.aasmc.myjetcaster.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.aasmc.myjetcaster.Graph
import ru.aasmc.myjetcaster.data.PodcastStore
import ru.aasmc.myjetcaster.data.PodcastWithExtraInfo
import ru.aasmc.myjetcaster.data.PodcastsRepository

class HomeViewModel(
    private val podcastsRepository: PodcastsRepository = Graph.podcastsRepository,
    private val podcastStore: PodcastStore = Graph.podcastStore
) : ViewModel() {
    // holds currently selected home category
    private val selectedHomeCategory = MutableStateFlow(HomeCategory.Discover)

    // holds currently available home categories
    private val categories = MutableStateFlow(HomeCategory.values().asList())

    // holds view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeViewState())
    private val refreshing = MutableStateFlow(false)

    // immutable stateFlow available to clients
    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values
            combine(
                categories,
                selectedHomeCategory,
                podcastStore.followedPodcastsSortedByLastEpisode(limit = 20),
                refreshing
            ) { categories, selectedCategory, podcasts, refreshing ->
                HomeViewState(
                    homeCategories = categories,
                    selectedHomeCategory = selectedCategory,
                    featuredPodcasts = podcasts,
                    refreshing = refreshing,
                    errorMessage = null // todo
                )
            }.catch { throwable ->
                // todo emit a UI error here. For now we'll just rethrow
                throw throwable
            }.collect {
                _state.value = it
            }
        }
        refresh(force = false)
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            // Calls the specified function block and returns its encapsulated result if
            // invocation was successful, catching any Throwable exception that was thrown
            // from the block function execution and encapsulating it as a failure.
            kotlin.runCatching {
                refreshing.value = true
                podcastsRepository.updatePodcasts(force)
            }
            // todo look at the result of runCatching and show any errors
            // result may be checked by isFailure or isSuccess
            refreshing.value = false
        }
    }

    fun onHomeCategorySelected(category: HomeCategory) {
        selectedHomeCategory.value = category
    }

    fun onPodcastUnfollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.unfollowPodcast(podcastUri)
        }
    }
}

enum class HomeCategory {
    Library, Discover
}

data class HomeViewState(
    val featuredPodcasts: List<PodcastWithExtraInfo> = emptyList(),
    val refreshing: Boolean = false,
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = emptyList(),
    val errorMessage: String? = null
)































