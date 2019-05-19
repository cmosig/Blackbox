package me2development.gmail.com.proofnews

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.URLUtil
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Url
import java.net.SocketTimeoutException
import java.net.URL
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import androidx.core.content.ContextCompat.getSystemService
import com.beust.klaxon.Klaxon
import okhttp3.OkHttpClient
import org.jetbrains.anko.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.random.Random


fun <T> T.doAsyncThrowOnUI(task: AnkoAsyncContext<T>.() -> Unit) = doAsync(
    exceptionHandler = { thr -> Handler(Looper.getMainLooper()).post { throw thr }},
    task = task)



data class DatResult(val id:String,val style:Double,
                     val cred:Double,val propagation:Double,val knowledge:Double,
                     val styleM:String,
                     val credM:String,
                     val propagationM:String,
                     val knowledgeM:String
                     )

fun countsAsUrl(text:String)=URLUtil.isValidUrl(text)||
        URLUtil.isValidUrl("http://$text")||
        URLUtil.isValidUrl("https://$text")

var json:String?=null

const val CHANNEL_ID="Merthan_channel_id"

class ActivityTwo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)
        //val result= Klaxon().parse<ComplicatedJson>(LEO)



        //Log.d("Result is",result.toString())

        doAsync {

            uiThread {


                with(intent){

                    if(action!=Intent.ACTION_MAIN){

                        val text= when {
                            hasExtra(Intent.EXTRA_PROCESS_TEXT) -> getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
                            hasExtra(Intent.EXTRA_TEXT) -> getStringExtra(Intent.EXTRA_TEXT)
                            else -> "Lol wtf"
                        }.toString()
                        val isUrl= URLUtil.isValidUrl(text.toString())
                        //toast("${if(isUrl) "Url:" else "Text:"} $text")


                        //toast("LOL data: $text $isUrl")



                        val client:OkHttpClient.Builder= OkHttpClient.Builder()
                        client.connectTimeout(
                            60, TimeUnit.SECONDS
                        )
                        client.readTimeout(60,TimeUnit.SECONDS)
                        client.writeTimeout(60,TimeUnit.SECONDS)



                        val retrofit=Retrofit.Builder()
                            .baseUrl("http://192.168.43.7:8081")
                            .client(client.build())
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build()

                        val api=retrofit.create(API::class.java)



                        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        mNotificationManager.createNotificationChannel(NotificationChannel(CHANNEL_ID,"wtf notification",NotificationManager.IMPORTANCE_DEFAULT))
                        //toast("Wtf")
                        val builder= NotificationCompat.Builder(this@ActivityTwo,CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setChannelId(CHANNEL_ID)
                            .setContentTitle("Checking")
                            .setContentText("This will only take a few seconds")
                            .setOngoing(true)
                            //.setOnlyAlertOnce(true)
                            .setProgress(100,0,true)
                        //.setPriority(Notification.PRIORITY_LOW)
                        val notification=builder.build()
                        mNotificationManager.notify(2,notification)






                        api.getRequest(

                            if(isUrl) text else "empty",
                            if(!isUrl) text else "empty",
                            "1"//System.nanoTime().toString()
                        )
                            .enqueue(object : Callback<String> {
                                override fun onFailure(call: Call<String>?, t: Throwable?) {
//                        builder.setContentText("Finished, failed")
//                        builder.setProgress(0,0,false)
//                        builder.setOngoing(false)
//                        mNotificationManager.notify(2,builder.build())

                                    finishNotification(builder,mNotificationManager,null)
                                    if(t!=null){
                                        if(t is SocketTimeoutException){
                                            toast("Timeout")
                                        }else{
                                            toast("unknown error \n$t")
                                            Log.e("onFailure",t.toString())
                                        }
                                    }
                                }

                                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                                    toast(if(response?.body().isNullOrBlank()) "EMPTY RESPONSE" else response?.body().toString())
                                    if(response?.body().isNullOrBlank()){
                                        mNotificationManager.cancel(2)
                                        toast("Error 4")
                                    }

                                    Log.d("Responsee",response?.body()?:"...")
                                    //toast(response?.body()?:"null")
                                    json=response?.body()
                                    val result= Klaxon().parse<ComplicatedJson>(response?.body()?:"")



                                    Log.d("Result is",result.toString())
                                    //Log.d()
                                    finishNotification(builder,mNotificationManager,result)
                                    //startActivity(Intent(this@ActivityTwo,DetailActivity::class.java))

//                        builder.setContentText("Finished, worked")
//                        builder.setProgress(0,0,false)
//                        builder.setOngoing(false)
//                        mNotificationManager.notify(2,builder.build())

                                }
                            })


                    }

                }







            }

        }
        finish()


//        NotificationManagerCompat.from(this@ActivityTwo).apply {
//
//            //builder.setProgress(100,0,true)
//            notify(2,builder.build())
//        }
        //startActivity(Intent(this@ActivityTwo,DetailActivity::class.java))




    }

    fun finishNotification(notification:NotificationCompat.Builder,manager:NotificationManager,json:ComplicatedJson?){

        if(json==null){
            toast("error 2")
            return
        }

        val res = applicationContext.getResources();

        val m=when(json.allgemeinerScore().roundToInt()){
            in 0..33->M(R.drawable.flag,-1)
            in 34..66->M(R.drawable.hand,0)
            in 66..120->M(R.drawable.correct,1)
            else->M(R.drawable.ic_launcher_foreground,0)
        }
        val rounded=json.allgemeinerScore().roundToInt()
        val (a,b,c) =when(m.type){
            -1-> listOf("We don't trust this website","Analysis result: ${rounded}% trustworthy","ALERT")
            0-> listOf("No conclusive result, proceed with care","Analysis result: ${rounded}% trustworthy","info")
            1-> listOf("We trust this website","Analysis result: ${rounded}% trustworthy","Info")
            else-> listOf("","","")
        }

        notification.setContentTitle(a)
        notification.setContentText(b)
        notification.setSubText(c)
        val i=Intent(this,DetailActivity::class.java)
        i.putExtra("S",json)
        val pending=PendingIntent.getActivity(this,1,i,PendingIntent.FLAG_UPDATE_CURRENT)
        notification.setAutoCancel(true)
            .setContentIntent(pending)


        var contactPic = Helper.drawableToBitmap(res.getDrawable(m.res))
      //  Log.d("Absturz","\" $contactPic $res \\n ${res.getDimension(android.R.dimen.notification_large_icon_height)}")
   // toast("$res \n ${res.getDimension(android.R.dimen.notification_large_icon_height)}")

    val height = res.getDimension(android.R.dimen.notification_large_icon_height);
    val width = res.getDimension(android.R.dimen.notification_large_icon_width);
    contactPic = Bitmap.createScaledBitmap(contactPic, width.toInt(), height.toInt(), false);




        notification.setLargeIcon(contactPic)

        notification.setProgress(0,0,false)
        notification.setOngoing(false)

        manager.notify(2,notification.build())






    }




}



data class M(val res:Int,val type:Int)//Type -1.0.1=bad,ok,good

const val LEOL="""{
    "credibility" : {
        "score": 35,
        "apis": [
            {
                "name": "testAPI1",
                "score": 6,
                "info": "testinfo"
            },
            {
                "name": "testAPI2",
                "score": 9,
                "info": "neue info"
            }
        ]
    },
    "propagation" : {
        "score": 29,
        "apis": [
            {
                "name": "testAPI1",
                "score": 6,
                "info": "testinfo"
            },
            {
                "name": "testAPI2",
                "score": 9,
                "info": "neue info"
            },
            {
                "name": "testAPI3",
                "score": 9,
                "info": "neue tolle info"
            }
        ]
    },
    "style" : {
        "score": 29,
        "apis": [
            {
                "name": "testAPI1",
                "score": 6,
                "info": "testinfo"
            },
            {
                "name": "testAPI2",
                "score": 9,
                "info": "neue info"
            }
        ]
    },
    "knowledge" : {
        "score": 29,
        "apis": [
            {
                "name": "testAPI1",
                "score": 6,
                "info": "testinfo"
            },
            {
                "name": "testAPI2",
                "score": 9,
                "info": "neue info"
            },
            {
                "name": "testAPI3",
                "score": 9,
                "info": "neue tolle info"
            },
            {
                "name": "testAPI3",
                "score": 9,
                "info": "neue tolle info"
            },
            {
                "name": "testAPI3",
                "score": 9,
                "info": "neue tolle info"
            }
        ]
    }
}"""


