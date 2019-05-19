package me2development.gmail.com.proofnews

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
import java.io.Serializable


interface API {

    //@GET("?eval&url={url1}&text={text1}&requestID={requestID1}")
    @GET("?eval")
    fun getRequest(

        @Query("url",encoded = true)url1:String="",
        @Query("text",encoded = true)text1:String="",
        @Query("requestID",encoded = true)requestID1:String): Call<String>

}


data class ComplicatedJson(
    @SerializedName("credibility")
    val credibility: Credibility,
    @SerializedName("knowledge")
    val knowledge: Knowledge,
    @SerializedName("propagation")
    val propagation: Propagation,
    @SerializedName("style")
    val style: Style
):Serializable{
    fun allgemeinerScore()=(credibility.score+knowledge.score+propagation.score+style.score) / 4.0
}

data class Style(
    @SerializedName("apis")
    val apis: List<Api>,
    @SerializedName("score")
    val score: Float
):Serializable

data class Api(
    @SerializedName("info")
    val info: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("score")
    val score: Float
):Serializable

data class Propagation(
    @SerializedName("apis")
    val apis: List<Api>,
    @SerializedName("score")
    val score: Float
):Serializable

data class Knowledge(
    @SerializedName("apis")
    val apis: List<Api>,
    @SerializedName("score")
    val score: Float
):Serializable

data class Credibility(
    @SerializedName("apis")
    val apis: List<Api>,
    @SerializedName("score")
    val score: Float
):Serializable

