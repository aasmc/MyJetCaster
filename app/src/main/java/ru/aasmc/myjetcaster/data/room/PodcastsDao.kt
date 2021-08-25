package ru.aasmc.myjetcaster.data.room

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.aasmc.myjetcaster.data.Podcast

@Dao
abstract class PodcastsDao {

    @Query("SELECT * FROM podcasts WHERE uri = :uri")
    abstract fun podcastWithUri(uri: String): Flow<Podcast>

}