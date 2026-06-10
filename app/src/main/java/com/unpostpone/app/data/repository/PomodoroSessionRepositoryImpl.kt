package com.unpostpone.app.data.repository

import com.unpostpone.app.data.local.dao.PomodoroSessionDao
import com.unpostpone.app.data.local.entity.PomodoroSessionEntity
import com.unpostpone.app.domain.model.PomodoroSession
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroSessionRepositoryImpl @Inject constructor(
    private val dao: PomodoroSessionDao,
) : PomodoroSessionRepository {

    override fun observeSessionsForDay(
        startOfDay: Long,
        endOfDay: Long,
    ): Flow<List<PomodoroSession>> =
        dao.observeForRange(startOfDay, endOfDay).map { list -> list.map(PomodoroSessionEntity::toDomain) }

    override suspend fun recordSession(session: PomodoroSession): Long =
        dao.insert(PomodoroSessionEntity.fromDomain(session))

    override suspend fun countCompletedFocusSessions(): Int =
        dao.countCompletedByType(PomodoroSessionType.FOCUS.name)
}
