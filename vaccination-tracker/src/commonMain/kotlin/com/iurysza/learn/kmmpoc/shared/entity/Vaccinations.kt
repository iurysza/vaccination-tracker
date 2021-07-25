package com.iurysza.learn.kmmpoc.shared.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vaccinations(

  @SerialName("doses_0")
  val singleDose: Int,

  @SerialName("doses_0_percentage")
  val singleDosePercentage: Double,

  @SerialName("doses_1")
  val firstDose: Int,

  @SerialName("doses_1_percentage")
  val firstDosePercentage: Double,

  @SerialName("doses_2")
  val secondDose: Int,

  @SerialName("doses_2_percentage")
  val secondDosePercentage: Double,

  @SerialName("fully_vaccinated")
  val fullyVaccinated: Int,

  @SerialName("percentage_fully_vaccinated")
  val fullyVaccinatedPercentage: Double,

  @SerialName("last_update")
  val lastUpdate: String,

  val total: Int
)