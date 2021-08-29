package ru.aasmc.myjetcaster.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.aasmc.myjetcaster.data.*

@Database(
    entities = [
        Podcast::class,
        Episode::class,
        PodcastCategoryEntry::class,
        Category::class,
        PodcastFollowedEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeTypeConverters::class)
abstract class JetcasterDatabase : RoomDatabase() {
    abstract fun podcastsDao(): PodcastsDao
    abstract fun episodesDao(): EpisodesDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun podcastCategoryEntryDao(): PodcastCategoryEntryDao
    abstract fun transactionRunnerDao(): TransactionRunnerDao
    abstract fun podcastFollowedEntryDao(): PodcastFollowedEntryDao
}