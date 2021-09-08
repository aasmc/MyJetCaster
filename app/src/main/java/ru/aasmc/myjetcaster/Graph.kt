package ru.aasmc.myjetcaster

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.rometools.rome.io.SyndFeedInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import org.slf4j.event.LoggingEvent
import ru.aasmc.myjetcaster.data.*
import ru.aasmc.myjetcaster.data.room.JetcasterDatabase
import ru.aasmc.myjetcaster.data.room.TransactionRunner
import java.io.File

object Graph {
    lateinit var okHttpClient: OkHttpClient

    lateinit var database: JetcasterDatabase
        private set

    private val transactionRunner: TransactionRunner
        get() = database.transactionRunnerDao()

    private val syndFeedInput by lazy { SyndFeedInput() }

    val podcastsRepository by lazy {
        PodcastsRepository(
            podcastsFetcher = podcastFetcher,
            podcastStore = podcastStore,
            episodeStore = episodeStore,
            categoryStore = categoryStore,
            transactionRunner = transactionRunner,
            mainDispatcher = mainDispatcher
        )
    }

    private val podcastFetcher by lazy {
        PodcastsFetcher(
            okHttpClient = okHttpClient,
            syndFeedInput = syndFeedInput,
            ioDispatcher = ioDispatcher
        )
    }

    val podcastStore by lazy {
        PodcastStore(
            podcastDao = database.podcastsDao(),
            podcastFollowedEntryDao = database.podcastFollowedEntryDao(),
            transactionRunner = transactionRunner
        )
    }

    private val episodeStore by lazy {
        EpisodeStore(
            episodesDao = database.episodesDao()
        )
    }

    val categoryStore by lazy {
        CategoryStore(
            categoriesDao = database.categoriesDao(),
            categoryEntryDao = database.podcastCategoryEntryDao(),
            episodesDao = database.episodesDao(),
            podcastsDao = database.podcastsDao()
        )
    }

    private val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO


    fun provide(context: Context) {
        okHttpClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
            .apply {
                if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
            }
            .build()

        database = Room.databaseBuilder(context, JetcasterDatabase::class.java, "data.db")
            // set this property for learning purposes only
            .fallbackToDestructiveMigration()
            .build()
    }

}



















