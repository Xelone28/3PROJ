//package com.console.ratcord.entity
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.TypeConverters
//import com.console.food.database.Converters
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
//
//@Serializable
//@Entity(tableName = "recipe")
//@TypeConverters(Converters::class)
//data class User(
//    @SerialName("pk")
//    @PrimaryKey val id: Int,
//    val title: String,
//    @SerialName("featured_image")
//    val featuredImageUrl: String,
//    val rating: Int,
//    val description: String,
//    @SerialName("cooking_instructions")
//    val cookingInstructions: String?,
//    val ingredients: List<String>,
//    @SerialName("long_date_added")
//    val longDateAdded: Int,
//    @SerialName("long_date_updated")
//    var longDateUpdated: Int,
//)
