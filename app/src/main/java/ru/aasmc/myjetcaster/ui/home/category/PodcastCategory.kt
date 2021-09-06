package ru.aasmc.myjetcaster.ui.home.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import ru.aasmc.myjetcaster.data.PodcastWithExtraInfo
import ru.aasmc.myjetcaster.ui.theme.Keyline1
import ru.aasmc.myjetcaster.util.ToggleFollowPodcastIconButton
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
private fun CategoryPodcastRow(
    podcasts: List<PodcastWithExtraInfo>,
    onTogglePodcastFollowed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastIndex = podcasts.size - 1
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = Keyline1, top = 8.dp, end = Keyline1, bottom = 24.dp)
    ) {
        itemsIndexed(items = podcasts) { index: Int, (podcast, _, isFollowed): PodcastWithExtraInfo ->
            TopPodcastRowItem(
                podcastTitle = podcast.title,
                isFollowed = isFollowed,
                onToggleFollowClicked = { onTogglePodcastFollowed(podcast.uri) },
                podcastImageUrl = podcast.imageUrl,
                modifier = Modifier.width(128.dp)
            )
            if (index < lastIndex) Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Composable
private fun TopPodcastRowItem(
    podcastTitle: String,
    isFollowed: Boolean,
    modifier: Modifier = Modifier,
    onToggleFollowClicked: () -> Unit,
    podcastImageUrl: String? = null
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            if (podcastImageUrl != null) {
                Image(
                    painter = rememberImagePainter(
                        data = podcastImageUrl,
                        builder = {
                            crossfade(true)
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            ToggleFollowPodcastIconButton(
                isFollowed = isFollowed,
                onClick = onToggleFollowClicked,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        Text(
            text = podcastTitle,
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

/**
 * Read more here: https://www.baeldung.com/java-datetimeformatter
 */
private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}


























