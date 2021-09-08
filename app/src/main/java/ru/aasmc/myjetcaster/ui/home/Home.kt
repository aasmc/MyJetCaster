package ru.aasmc.myjetcaster.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import ru.aasmc.myjetcaster.R
import ru.aasmc.myjetcaster.data.PodcastWithExtraInfo
import ru.aasmc.myjetcaster.ui.home.discover.Discover
import ru.aasmc.myjetcaster.ui.theme.Keyline1
import ru.aasmc.myjetcaster.ui.theme.MyJetCasterTheme
import ru.aasmc.myjetcaster.util.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime


@Composable
fun Home() {
    val viewModel: HomeViewModel = viewModel(HomeViewModel::class.java)
    val viewState by viewModel.state.collectAsState()

    Surface(
        Modifier.fillMaxSize()
    ) {
        HomeContent(
            featuredPodcasts = viewState.featuredPodcasts,
            isRefreshing = viewState.refreshing,
            selectedHomeCategory = viewState.selectedHomeCategory,
            homeCategories = viewState.homeCategories,
            onPodcastUnfollowed = viewModel::onPodcastUnfollowed,
            onCategorySelected = viewModel::onHomeCategorySelected,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = null
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_text_logo),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                )
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = { /* todo open search*/ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.cd_search)
                    )
                }
                IconButton(
                    onClick = { /* todo open account?*/ }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(id = R.string.cd_account)
                    )
                }
            }
        },
        modifier = modifier
    )
}

/**
 * This is the minimum amount of calculated contrast for a color to be used on top of the
 * surface color. These values are defined within the WCAG AA guidelines, and we use a value of
 * 3:1 which is the minimum for user-interface components.
 */
private const val MinContrastOfPrimaryVsSurface = 3f

@OptIn(ExperimentalPagerApi::class) // HorizontalPager is experimental
@Composable
fun HomeContent(
    featuredPodcasts: List<PodcastWithExtraInfo>,
    isRefreshing: Boolean,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (String) -> Unit,
    onCategorySelected: (HomeCategory) -> Unit
) {
    Column(modifier = modifier) {
        // we dynamically theme this sub-section of the layout to match the selected
        // 'top podcast'
        val surfaceColor = MaterialTheme.colors.surface
        val dominantColorState = rememberDominantColorState { color ->
            // we want a color which has sufficient contrast agains the surface color
            color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
        }

        DynamicThemePrimaryColorsFromImage(dominantColorState) {
            val pagerState = rememberPagerState(
                pageCount = featuredPodcasts.size,
                initialOffscreenLimit = 2
            )
            val selectedImageUrl = featuredPodcasts.getOrNull(pagerState.currentPage)
                ?.podcast?.imageUrl

            // when the selected image url changes, call updateColorsFromImageUrl() or reset()
            LaunchedEffect(selectedImageUrl) {
                if (selectedImageUrl != null) {
                    dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
                } else {
                    dominantColorState.reset()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalGradientScrim(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                        startYPercentage = 1f,
                        endYPercentage = 0f
                    )
            ) {
                val appBarColor = MaterialTheme.colors.surface.copy(alpha = 0.87f)

                // draw a scrim over the status bar which matches the app bar
                Spacer(
                    Modifier
                        .background(appBarColor)
                        .fillMaxWidth()
                        .statusBarsHeight()
                )

                HomeAppBar(
                    backgroundColor = appBarColor,
                    modifier = Modifier.fillMaxWidth()
                )

                if (featuredPodcasts.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))

                    FollowedPodcasts(
                        items = featuredPodcasts,
                        pagerState = pagerState,
                        onPodcastUnfollowed = onPodcastUnfollowed,
                        modifier = Modifier
                            .padding(start = Keyline1, top = 16.dp, end = Keyline1)
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
        if (isRefreshing) {
            // todo show a progress indicator or similar
        }
        if (homeCategories.isNotEmpty()) {
            HomeCategoryTabs(
                categories = homeCategories,
                selectedCategory = selectedHomeCategory,
                onCategorySelected = onCategorySelected
            )
        }

        when (selectedHomeCategory) {
            HomeCategory.Library -> {
                /* todo */
            }
            HomeCategory.Discover -> {
                Discover(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HomeCategoryTabs(
    categories: List<HomeCategory>,
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }
    TabRow(
        selectedTabIndex = selectedIndex,
        indicator = indicator,
        modifier = modifier
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = when (category) {
                            HomeCategory.Library -> stringResource(id = R.string.home_library)
                            HomeCategory.Discover -> stringResource(id = R.string.home_discover)
                        },
                        style = MaterialTheme.typography.body2
                    )
                }
            )
        }
    }
}


@Composable
fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

@ExperimentalPagerApi // HorizontalPager is experimental
@Composable
fun FollowedPodcasts(
    items: List<PodcastWithExtraInfo>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (String) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val (podcast, lastEpisodeDate) = items[page]
        FollowedPodcastCarouselItem(
            podcastImageUrl = podcast.imageUrl,
            lastEpisodeDate = lastEpisodeDate,
            onUnfollowedClick = { onPodcastUnfollowed(podcast.uri) },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight()
        )
    }
}


@Composable
private fun FollowedPodcastCarouselItem(
    modifier: Modifier = Modifier,
    podcastImageUrl: String? = null,
    lastEpisodeDate: OffsetDateTime? = null,
    onUnfollowedClick: () -> Unit
) {
    Column(modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Box(
            Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(1f)
        ) {
            if (podcastImageUrl != null) {
                Image(
                    painter = rememberImagePainter(data = podcastImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                )
            }
            ToggleFollowPodcastIconButton(
                isFollowed = true, // all podcasts are followed in this feed
                onClick = onUnfollowedClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        if (lastEpisodeDate != null) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = lastUpdated(updated = lastEpisodeDate),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun lastUpdated(
    updated: OffsetDateTime
): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(id = R.string.updated_longer)
        days >= 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }
        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)
        else -> stringResource(R.string.updated_today)
    }
}


@Composable
@Preview
fun PreviewPodcastCart() {
    MyJetCasterTheme {
        FollowedPodcastCarouselItem(
            modifier = Modifier.size(128.dp),
            onUnfollowedClick = {}
        )
    }
}


















