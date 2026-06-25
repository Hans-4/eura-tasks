package com.eura.tasks.db.tags

data class TagDbState (
    val tags: List<TagsEntity> = emptyList(),
    val tagTitle: String = "",
    val selectedTagUuids: List<String> = emptyList(),
    val tagsFromCurrentTask: List<TagsEntity> = emptyList(),

    val searchQuery: String = "",
    val searchResults: List<TagsEntity> = emptyList()
)