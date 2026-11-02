package com.hexsoft.dailypoints.data.repository

import com.hexsoft.dailypoints.data.local.entities.GoalEntity
import com.hexsoft.dailypoints.data.local.entities.PlannedTaskEntity
import com.hexsoft.dailypoints.data.local.entities.ProfileEntity
import com.hexsoft.dailypoints.data.local.entities.TaskCompletionEntity
import com.hexsoft.dailypoints.data.local.entities.TaskEntity
import com.hexsoft.dailypoints.domain.model.Goal
import com.hexsoft.dailypoints.domain.model.PlannedTask
import com.hexsoft.dailypoints.domain.model.Profile
import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.domain.model.TaskCompletion
import com.hexsoft.dailypoints.utils.DateUtils

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    profileId = profileId,
    name = name,
    description = description,
    category = category,
    points = points,
    active = active,
    createdAt = createdAt,
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    profileId = profileId,
    name = name,
    description = description,
    category = category,
    points = points,
    active = active,
    createdAt = createdAt,
)

fun TaskCompletionEntity.toDomain(): TaskCompletion = TaskCompletion(
    id = id,
    taskId = taskId,
    date = DateUtils.fromDbString(date),
    completed = completed,
    completedAt = completedAt,
)

fun TaskCompletion.toEntity(): TaskCompletionEntity = TaskCompletionEntity(
    id = id,
    taskId = taskId,
    date = DateUtils.toDbString(date),
    completed = completed,
    completedAt = completedAt,
)

fun PlannedTaskEntity.toDomain(): PlannedTask = PlannedTask(
    id = id,
    taskId = taskId,
    plannedDate = DateUtils.fromDbString(plannedDate),
)

fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    dailyGoal = dailyGoal,
    monthlyGoal = monthlyGoal,
)

fun Goal.toEntity(): GoalEntity = GoalEntity(
    id = id,
    dailyGoal = dailyGoal,
    monthlyGoal = monthlyGoal,
)

fun ProfileEntity.toDomain(): Profile = Profile(
    id = id,
    name = name,
    extraPoints = extraPoints,
    createdAt = createdAt,
)
