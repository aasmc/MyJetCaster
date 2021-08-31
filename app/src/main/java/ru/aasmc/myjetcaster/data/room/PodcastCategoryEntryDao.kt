package ru.aasmc.myjetcaster.data.room

import androidx.room.*
import ru.aasmc.myjetcaster.data.PodcastCategoryEntry

@Dao
abstract class PodcastCategoryEntryDao {
    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: PodcastCategoryEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(vararg entity: PodcastCategoryEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<PodcastCategoryEntry>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: PodcastCategoryEntry)

    @Delete
    abstract suspend fun delete(entity: PodcastCategoryEntry): Int
}