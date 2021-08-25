package ru.aasmc.myjetcaster.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.aasmc.myjetcaster.data.Category

/**
 * [Room] DAO for [Category] related operations.
 */
@Dao
abstract class CategoriesDao {

    @Query(
        """
            SELECT categories.* FROM categories
            INNER JOIN (
                SELECT category_id, COUNT(podcast_uri) AS podcast_count FROM podcast_category_entries
                 GROUP BY category_id
            ) ON category_id = categories.id
            ORDER BY podcast_count DESC LIMIT :limit
        """
    )
    abstract fun categoriesSortedByPodcastCount(limit: Int): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE name = :name")
    abstract suspend fun getCategoryWithName(name: String): Category?

    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(vararg entity: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<Category>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Category)

    @Delete
    abstract suspend fun delete(entity: Category): Int

}






















