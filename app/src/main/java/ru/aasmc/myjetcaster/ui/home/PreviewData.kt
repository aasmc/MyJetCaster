package ru.aasmc.myjetcaster.ui.home

import ru.aasmc.myjetcaster.data.Category
import ru.aasmc.myjetcaster.data.Episode
import ru.aasmc.myjetcaster.data.Podcast
import ru.aasmc.myjetcaster.data.PodcastWithExtraInfo
import java.time.OffsetDateTime
import java.time.ZoneOffset


val PreviewCategories = listOf(
    Category(name = "Crime"),
    Category(name = "News"),
    Category(name = "Comedy")
)

val PreviewPodcasts = listOf(
    Podcast(
        uri = "fakeUri://podcast/1",
        title = "Android Developers Backstage",
        author = "Android Developers"
    ),
    Podcast(
        uri = "fakeUri://podcast/2",
        title = "Google Developers podcast",
        author = "Google Developers"
    )
)

val PreviewPodcastsWithExtraInfo = PreviewPodcasts.mapIndexed { index, podcast ->
    PodcastWithExtraInfo().apply {
        this.podcast = podcast
        this.lastEpisodeDate = OffsetDateTime.now()
        this.isFollowed = index % 2 == 0
    }
}

val PreviewEpisodes = listOf(
    Episode(
        uri = "fakeUri://episode/1",
        podcastUri = PreviewPodcasts[0].uri,
        title = "Episode 140: Bubbles!",
        summary = "In this episode, Romain, Chet and Tor talked with Mady Melor and Artur Tsurkan from the System UI team about... Bubbles!",
        published = OffsetDateTime.of(2020, 6, 2, 9, 27, 0, 0, ZoneOffset.of("+02:00"))
    )
)
