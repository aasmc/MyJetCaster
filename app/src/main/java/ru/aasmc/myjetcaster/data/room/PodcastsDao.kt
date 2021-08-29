package ru.aasmc.myjetcaster.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.aasmc.myjetcaster.data.Podcast
import ru.aasmc.myjetcaster.data.PodcastWithExtraInfo

@Dao
abstract class PodcastsDao {

    @Query("SELECT * FROM podcasts WHERE uri = :uri")
    abstract fun podcastWithUri(uri: String): Flow<Podcast>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
        SELECT podcast_uri, MAX(published) AS last_episode_date
        FROM episodes
        GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
    """
    )
    abstract fun podcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Transaction
    @Query(
        """
            SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
            FROM podcasts 
            INNER JOIN (
                SELECT episodes.podcast_uri, MAX(published) AS last_episode_date
                FROM episodes
                INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri
                WHERE category_id = :categoryId
                GROUP BY episodes.podcast_uri
            ) inner_query ON podcasts.uri = inner_query.podcast_uri
            LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = inner_query.podcast_uri
            ORDER BY datetime(last_episode_date) DESC
            LIMIT :limit
        """
    )
    abstract fun podcastsInCategorySortedByLastEpisode(
        categoryId: Long,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts
        INNER JOIN(
            SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        INNER JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
    """
    )
    abstract fun followedPodcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Query("SELECT COUNT(*) FROM podcasts")
    abstract suspend fun count(): Int

    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Podcast): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(vararg entity: Podcast)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<Podcast>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Podcast)

    @Delete
    abstract suspend fun delete(entity: Podcast): Int

}















