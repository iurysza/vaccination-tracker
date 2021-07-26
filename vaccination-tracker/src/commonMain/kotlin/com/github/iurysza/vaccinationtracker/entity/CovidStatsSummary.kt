package com.github.iurysza.vaccinationtracker.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class CovidStatsSummary(
  val cases: Cases,
  val deaths: Deaths,
  val iso_code: String,
  val recovered: Recovered,
  val state: String,
  val suspects: Suspects,
  val tests: Tests,
  val vaccinations: Vaccinations
)

@Serializable
internal data class Cases(
  val date: String,
  val last_update: String,
  val new: Int? = null,
  val total: Int
)

@Serializable
internal data class Deaths(
  val date: String,
  val last_update: String,
  val new: Int? = null,
  val total: Int
)

@Serializable
internal data class Recovered(
  val date: String,
  val last_update: String,
  val total: Int
)

@Serializable
internal data class Suspects(
  val date: String,
  val last_update: String,
  val total: Int
)

@Serializable
internal data class Tests(
  val date: String,
  val last_update: String,
  val total: Int
)
