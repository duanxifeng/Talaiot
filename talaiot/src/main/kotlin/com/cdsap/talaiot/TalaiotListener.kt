package com.cdsap.talaiot


import com.cdsap.talaiot.entities.TaskLength
import com.cdsap.talaiot.entities.TaskMessageState
import com.cdsap.talaiot.publisher.TalaiotPublisher
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState


class TalaiotListener(
    private val talaiotPublisher: TalaiotPublisher
) : BuildListener, TaskExecutionListener {

    private val taskLenghtList = mutableListOf<TaskLength>()
    private var listOfTasks: HashMap<String, Long> = hashMapOf()

    override fun settingsEvaluated(settings: Settings) {
    }

    override fun buildFinished(result: BuildResult) {
        talaiotPublisher.publish(taskLenghtList)
    }

    override fun projectsLoaded(gradle: Gradle) {
    }

    override fun buildStarted(gradle: Gradle) {
    }

    override fun projectsEvaluated(gradle: Gradle) {
    }

    override fun beforeExecute(task: Task) {
        listOfTasks[task.path] = System.currentTimeMillis()
    }

    override fun afterExecute(task: Task, state: TaskState) {
        val time = System.currentTimeMillis() - (listOfTasks[task.path] as Long)
        taskLenghtList.add(
            TaskLength(
                ms = time,
                taskName = task.path,
                state = when (state.skipMessage) {
                    "UP-TO-DATE" -> TaskMessageState.UP_TO_DATE
                    "FROM-CACHE" -> TaskMessageState.FROM_CACHE
                    "NO-SOURCE" -> TaskMessageState.NO_SOURCE
                    else -> TaskMessageState.NO_MESSAGE_STATE
                }
            )
        )
    }
}
