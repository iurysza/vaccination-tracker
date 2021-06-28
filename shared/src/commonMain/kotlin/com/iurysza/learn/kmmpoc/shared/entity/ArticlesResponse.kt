package com.iurysza.learn.kmmpoc.shared.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticlesResponse(
  val exhaustiveNbHits: Boolean,
  val hits: List<Hit>,
  val hitsPerPage: Int,
  val nbHits: Int,
  val nbPages: Int,
  val page: Int,
  val params: String,
  val processingTimeMS: Int,
  val query: String
)

@Serializable
data class Author(
  val matchLevel: String,
  val matchedWords: List<String>,
  val value: String
)

@Serializable
data class HighlightResult(
  val author: Author? = null,
  val story_text: StoryText? = null,
  val title: Title? = null,
  val url: Url? = null,
)

@Serializable
data class StoryText(
  val matchLevel: String,
  val matchedWords: List<String>,
  val value: String
)

@Serializable
data class Title(
  val matchLevel: String,
  val matchedWords: List<String>,
  val value: String
)

@Serializable
data class Url(
  val matchLevel: String,
  val matchedWords: List<String>,
  val value: String
)

@Serializable
data class Hit(
  @SerialName("_highlightResult")
  val highlightResult: HighlightResult? = null,
  @SerialName("_tags")
  val tags: List<String>,
  val author: String,
  @SerialName("comment_text")
  val commentText: String? = null,
  @SerialName("created_at")
  val createdAt: String,
  @SerialName("created_at_i")
  val createdAtI: Int,
  @SerialName("num_comments")
  val numComments: Int? = null,
  val objectID: String? = null,
  @SerialName("parent_id")
  val parentId: String? = null,
  val points: Int? = null,
  @SerialName("relevancy_score")
  val relevancyScore: Int? = null,
  @SerialName("story_id")
  val storyId: String? = null,
  @SerialName("story_text")
  val storyText: String? = null,
  @SerialName("story_title")
  val storyTitle: String? = null,
  @SerialName("story_url")
  val storyUrl: String? = null,
  val title: String? = null,
  val url: String? = null,
)