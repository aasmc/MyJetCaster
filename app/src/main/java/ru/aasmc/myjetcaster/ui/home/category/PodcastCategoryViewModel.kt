package ru.aasmc.myjetcaster.ui.home.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.aasmc.myjetcaster.Graph
import ru.aasmc.myjetcaster.data.CategoryStore
import ru.aasmc.myjetcaster.data.EpisodeToPodcast
import ru.aasmc.myjetcaster.data.PodcastStore
import ru.aasmc.myjetcaster.data.PodcastWithExtraInfo

private const val LIMIT_EPISODES = 10
private const val LIMIT_PODCASTS = 10

class PodcastCategoryViewModel(
    private val categoryId: Long,
    private val categoryStore: CategoryStore = Graph.categoryStore,
    private val podcastStore: PodcastStore = Graph.podcastStore,
) : ViewModel() {
    private val _state = MutableStateFlow(PodcastCategoryViewState())

    val state: StateFlow<PodcastCategoryViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val recentPodcastsFlow = categoryStore.podcastsInCategorySortedByPodcastCount(
                categoryId = categoryId,
                limit = LIMIT_PODCASTS
            )
            val episodesFlow = categoryStore.episodesFromPodcastsInCategory(
                categoryId = categoryId,
                limit = LIMIT_EPISODES
            )

            // combine the flows and collect them into the view state with StateFlow
            combine(recentPodcastsFlow, episodesFlow) { topPodcasts, episodes ->
                PodcastCategoryViewState(
                    topPodcasts = topPodcasts,
                    episodes = episodes
                )
            }.collect { _state.value = it }
        }
    }

    fun onTogglePodcastFollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcastUri)
        }
    }
}

data class PodcastCategoryViewState(
    val topPodcasts: List<PodcastWithExtraInfo> = emptyList(),
    val episodes: List<EpisodeToPodcast> = emptyList()
)