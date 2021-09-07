package ru.aasmc.myjetcaster.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.aasmc.myjetcaster.R
import ru.aasmc.myjetcaster.util.quantityStringResource
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime


@Composable
private fun FollowedPodcastCarouselItem(
    modifier: Modifier = Modifier,
    podcastImageUrl: String? = null,
    lastEpisodeUpdate: OffsetDateTime? = null,
    onUnfollowedClick: ()->Unit
) {

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