package com.github.iurysza.vaccinationtracker.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VaccinationDataResponseItem(
    @SerialName("average_14days")
    val average14Days: Average14days,

    @SerialName("average_7days")
    val average7Days: Average7days,

    @SerialName("daily_vaccinations")
    val dailyVaccinations: DailyVaccinations,

    @SerialName("iso_code")
    val isoCode: String,

    @SerialName("last_update")
    val lastUpdate: String,

    @SerialName("last_update_date")
    val lastUpdateDate: String,

    @SerialName("people_fully_vaccinated_per_hundred")
    val peopleFullyVaccinatedPerHundred: Int,

    @SerialName("people_vaccinated_per_hundred")
    val peopleVaccinatedPerHundred: Int,

    @SerialName("source_name")
    val sourceName: String,

    @SerialName("source_website")
    val sourceWebsite: String,

    val state: String,

    @SerialName("total_vaccinations")
    val totalVaccinations: TotalVaccinations,

    @SerialName("total_vaccinations_per_hundred")
    val totalVaccinationsPerHundred: Int,

    val vaccines: List<String>
)

@Serializable
data class Average7days(
    @SerialName("0")
    val singleDose: Int,
    @SerialName("1")
    val firstDose:Int,
    @SerialName("2")
    val secondDose: Int,
    @SerialName("per_million")
    val perMillion: Int,
    val total: Int
)

@Serializable
data class Average14days(
    @SerialName("0")
    val singleDose: Int,
    @SerialName("1")
    val firstDose:Int,
    @SerialName("2")
    val secondDose: Int,
    @SerialName("per_million")
    val perMillion: Int,
    val total: Int
)

@Serializable
data class DailyVaccinations(
    @SerialName("0")
    val singleDose: Int?=null,
    @SerialName("1")
    val firstDose:Int?=null,
    @SerialName("2")
    val secondDose: Int?=null,
    @SerialName("fully_vaccinated")
    val fullyVaccinated: Int?=null,
    val total: Int?=null
)

@Serializable
data class TotalVaccinations(
    @SerialName("0")
    val singleDose: Int,

    @SerialName("1")
    val firstDose:Int,

    @SerialName("2")
    val secondDose: Int,

    @SerialName("fully_vaccinated")
    val fullyVaccinated: Int,

    @SerialName("percentage_doses_0")
    val singleDosePercentage: Double,

    @SerialName("percentage_doses_1")
    val firstDosePercentage: Double,

    @SerialName("percentage_doses_2")
    val secondDosePercentage: Double,

    @SerialName("percentage_fully_vaccinated")
    val fullyVaccinatedPercentage: Double,

    val total: Int
)
