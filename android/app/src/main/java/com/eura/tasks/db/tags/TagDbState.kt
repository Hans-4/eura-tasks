package com.eura.tasks.db.tags

data class TagDbState (
    val tags: List<TagsEntity> = emptyList(),
    val tagTitle: String = ""
)