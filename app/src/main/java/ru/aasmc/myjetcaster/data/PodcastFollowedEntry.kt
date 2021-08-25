package ru.aasmc.myjetcaster.data

import androidx.compose.runtime.Immutable
import androidx.room.*

@Entity(
    tableName = "podcast_followed_entries",
    foreignKeys = [
        ForeignKey(
            entity = Podcast::class,
            parentColumns = ["uri"],
            childColumns = ["podcast_uri"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("podcast_uri", unique = true)
    ]
)
@Immutable
data class PodcastFollowedEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "podcast_uri") val podcastUri: String
)