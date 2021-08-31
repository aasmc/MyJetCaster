package ru.aasmc.myjetcaster.data

import kotlinx.coroutines.flow.Flow
import ru.aasmc.myjetcaster.data.room.PodcastFollowedEntryDao
import ru.aasmc.myjetcaster.data.room.PodcastsDao
import ru.aasmc.myjetcaster.data.room.TransactionRunner

/**
 * A data repository for [Podcast] instances.
 */
class PodcastStore(
    private val podcastsDao: PodcastsDao,
    private val podcastFollowedEntryDao: PodcastFollowedEntryDao,
    private val transactionRunner: TransactionRunner
) {

    /**
     * Returns a flow containing the [Podcast] with the given [uri].
     */
    fun podcastWithUri(uri: String): Flow<Podcast> {
        return podcastsDao.podcastWithUri(uri)
    }

    /**
     * Returns a flow containing the entire collection of podcasts, sorted by the last episode
     * publish date for each podcast.
     */
    fun podcastsSortedByLastEpisode(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastsDao.podcastsSortedByLastEpisode(limit)
    }

    fun followedPodcastsSortedByLastEpisode(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastsDao.followedPodcastsSortedByLastEpisode(limit)
    }

    private suspend fun followPodcast(podcastUri: String) {
        podcastFollowedEntryDao.insert(PodcastFollowedEntry(podcastUri = podcastUri))
    }

    suspend fun unfollowPodcast(podcastUri: String) {
        podcastFollowedEntryDao.deleteWithPodcastUri(podcastUri)
    }

    suspend fun togglePodcastFollowed(podcastUri: String) = transactionRunner {
        if (podcastFollowedEntryDao.isPodcastFollowed(podcastUri)) {
            unfollowPodcast(podcastUri)
        } else {
            followPodcast(podcastUri)
        }
    }

    /**
     * Add a new [Podcast] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addPodcast(podcast: Podcast) {
        podcastsDao.insert(podcast)
    }

    suspend fun isEmpty(): Boolean = podcastsDao.count() == 0
}














