package ru.aasmc.myjetcaster.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.aasmc.myjetcaster.data.room.TransactionRunner

/**
 * Data repo for Podcasts.
 */
class PodcastsRepository(
    private val podcastsFetcher: PodcastFetcher,
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val categoryStore: CategoryStore,
    private val transactionRunner: TransactionRunner,
    mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun updatePodcasts(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || podcastStore.isEmpty()) {
            refreshingJob = scope.launch {
                podcastsFetcher(SampleFeeds)
                    .filter { it is PodcastRssResponse.Success }
                    .map { it as PodcastRssResponse.Success }
                    .collect { (podcast, episodes, categories) ->
                        transactionRunner {
                            podcastStore.addPodcast(podcast)
                            episodeStore.addEpisodes(episodes)

                            categories.forEach { category ->
                                // step 1: insert category
                                val categoryId = categoryStore.addCategory(category)
                                // step 2: add the podcast to the category
                                categoryStore.addPodcastToCategory(
                                    podcastUri = podcast.uri,
                                    categoryId = categoryId
                                )
                            }
                        }
                    }
            }
        }
    }
}