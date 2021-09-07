package ru.aasmc.myjetcaster.ui.home.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import ru.aasmc.myjetcaster.R
import ru.aasmc.myjetcaster.data.Episode
import ru.aasmc.myjetcaster.data.EpisodeToPodcast
import ru.aasmc.myjetcaster.data.Podcast
import ru.aasmc.myjetcaster.data.PodcastWithExtraInfo
import ru.aasmc.myjetcaster.ui.home.PreviewEpisodes
import ru.aasmc.myjetcaster.ui.home.PreviewPodcasts
import ru.aasmc.myjetcaster.ui.theme.Keyline1
import ru.aasmc.myjetcaster.ui.theme.MyJetCasterTheme
import ru.aasmc.myjetcaster.util.ToggleFollowPodcastIconButton
import ru.aasmc.myjetcaster.util.viewModelProviderFactoryOf
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun PodcastCategory(
    categoryId: Long,
    modifier: Modifier = Modifier
) {
    /**
     * CategoryEpisodeViewModel requires the category as part if its constructor,
     * therefore we need to assist with its instantiation with a custom factory and custom key
     */
    val viewModel: PodcastCategoryViewModel = viewModel(
        // we use a custom key, using the category parameter
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf { PodcastCategoryViewModel(categoryId) }
    )
    val viewState by viewModel.state.collectAsState()
    // todo reset scroll position when category changes
    Column(modifier = modifier) {
        CategoryPodcasts(
            topPodcasts = viewState.topPodcasts,
            viewModel = viewModel
        )
        EpisodeList(episodes = viewState.episodes)
    }
}

@Composable
private fun CategoryPodcasts(
    topPodcasts: List<PodcastWithExtraInfo>,
    viewModel: PodcastCategoryViewModel
) {
    LazyRow(
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.Start // Place children horizontally such that they are as close as possible to the beginning of the horizontal axis
    ) {
        item {
            CategoryPodcastRow(
                podcasts = topPodcasts,
                onTogglePodcastFollowed = viewModel::onTogglePodcastFollowed,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}

@Composable
private fun EpisodeList(episodes: List<EpisodeToPodcast>) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center // Place children such that they are as close as possible to the middle of the main axis
    ) {
        items(episodes, key = { it.episode.uri }) { item ->
            EpisodeListItem(
                episode = item.episode,
                podcast = item.podcast,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}

@Composable
fun EpisodeListItem(
    episode: Episode,
    podcast: Podcast,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier.clickable { /* todo */ } then modifier
    ) {
        val (
            divider, episodeTitle, podcastTitle, image, playIcon,
            date, addPlayList, overflow
        ) = createRefs()

        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )
        // if there's an imageUrl we can show it using Coil
        Image(
            painter = rememberImagePainter(
                data = podcast.imageUrl,
                builder = {
                    crossfade(true)
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .constrainAs(image) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                },
        )
        Text(
            text = episode.title,
            maxLines = 2,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(episodeTitle) {
                linkTo(
                    start = parent.start,
                    end = image.start,
                    startMargin = Keyline1,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, 16.dp)
                // this will use wrap content if constraints allow it,
                // if need to use wrap content regardless of constraints, then this
                // must be wrapContent
                width = Dimension.preferredWrapContent
            }
        )
        // Creates and returns a bottom barrier, containing the specified elements.
        val titleImageBarrier = createBottomBarrier(podcastTitle, image)
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = podcast.title,
                maxLines = 2,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.constrainAs(podcastTitle) {
                    linkTo(
                        start = parent.start,
                        end = image.start,
                        startMargin = Keyline1,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(episodeTitle.bottom, 6.dp)
                    width = Dimension.preferredWrapContent
                }
            )
        }

        Image(
            imageVector = Icons.Rounded.PlayCircleFilled,
            contentDescription = stringResource(id = R.string.cd_play),
            // Scale the source uniformly (maintaining the source's aspect ratio) so
            // that both dimensions (width and height) of the source will be equal to or less
            // than the corresponding dimension of the destination
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 24.dp)
                ) { /* todo */ }
                .size(48.dp)
                .padding(6.dp)
                .constrainAs(playIcon) {
                    start.linkTo(parent.start, Keyline1)
                    top.linkTo(titleImageBarrier, margin = 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                }
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = when {
                    episode.duration != null -> {
                        // if we have the duration, we combine the date/duration via a
                        // formatted string
                        stringResource(
                            R.string.episode_date_duration,
                            MediumDateFormatter.format(episode.published),
                            episode.duration.toMinutes().toInt()
                        )
                    }
                    // otherwise we just use the date
                    else -> {
                        MediumDateFormatter.format(episode.published)
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.constrainAs(date) {
                    centerVerticallyTo(playIcon)
                    linkTo(
                        start = playIcon.end,
                        startMargin = 12.dp,
                        end = addPlayList.start,
                        endMargin = 16.dp,
                        bias = 0f // float this towards the start
                    )
                }
            )
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.constrainAs(addPlayList) {
                    end.linkTo(overflow.start)
                    centerVerticallyTo(playIcon)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PlaylistAdd,
                    contentDescription = stringResource(id = R.string.cd_add)
                )
            }

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.constrainAs(overflow) {
                    end.linkTo(parent.end, 8.dp)
                    // Adds top and bottom links towards the corresponding anchors of playIcon.
                    centerVerticallyTo(playIcon)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.cd_more)
                )
            }
        }
    }
}

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

@Preview
@Composable
fun PreviewEpisodeListItem() {
    MyJetCasterTheme {
        EpisodeListItem(
            episode = PreviewEpisodes[0],
            podcast = PreviewPodcasts[0],
            modifier = Modifier.fillMaxWidth()
        )
    }
}
























