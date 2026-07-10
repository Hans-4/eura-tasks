package com.eura.tasks.db.tasks.tags

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskEntity

data class TaskWithTags(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "taskUuid",
        entityColumn = "tagUuid",
        associateBy = Junction(
            value = TaskTagsEntity::class,
            parentColumn = "taskUuid",
            entityColumn = "tagUuid"
        )
    )
    val tags: List<TagsEntity>
)