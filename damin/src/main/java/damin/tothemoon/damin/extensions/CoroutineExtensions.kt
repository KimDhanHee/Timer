package damin.tothemoon.damin.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

val defaultScope: CoroutineScope
  get() = defaultScope()

private fun defaultScope(vararg jobs: Job): CoroutineScope =
  CoroutineScope(
    jobs.fold(Dispatchers.Default as CoroutineContext,
      { acc, job -> acc + job })
  )

val mainScope: CoroutineScope
  get() = mainScope()

private fun mainScope(vararg jobs: Job): CoroutineScope =
  CoroutineScope(
    jobs.fold(Dispatchers.Main as CoroutineContext,
      { acc, job -> acc + job })
  )

val ioScope: CoroutineScope
  get() = ioScope()

private fun ioScope(vararg jobs: Job): CoroutineScope =
  CoroutineScope(
    jobs.fold(Dispatchers.IO as CoroutineContext,
      { acc, job -> acc + job })
  )