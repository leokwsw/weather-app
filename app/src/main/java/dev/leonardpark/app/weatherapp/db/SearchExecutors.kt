package dev.leonardpark.app.weatherapp.db

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SearchExecutors (
  diskIO: Executor = Executors.newSingleThreadExecutor(),
  network: Executor = Executors.newFixedThreadPool(3),
  mainThread: Executor = MainThreadExecutor()
) {
  private var mDiskIO: Executor = diskIO
  private var mNetworkIO: Executor = network
  private var mMainThread: Executor = mainThread

  fun diskIO(): Executor = mDiskIO

  fun networkIO(): Executor = mNetworkIO

  fun mainThread(): Executor = mMainThread

  private class MainThreadExecutor : Executor {
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
      mainThreadHandler.post(command)
    }
  }
}