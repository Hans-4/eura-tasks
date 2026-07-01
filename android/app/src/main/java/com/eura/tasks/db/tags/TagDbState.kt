package com.eura.tasks.db.tags

import com.eura.tasks.db.tasks.tags.TaskTagsEntity

data class TagDbState (
    val tags: List<TagsEntity> = emptyList(),
    val tagTitle: String = "",
    val selectedTagUuids: List<String> = emptyList(),
    val selectedTagIds: List<Int> = emptyList(),
    val tagsFromCurrentTask: List<TagsEntity> = emptyList(),
    val taskTags: List<TaskTagsEntity> = emptyList(),

    val searchQuery: String = "",
    val searchResults: List<TagsEntity> = emptyList()
)