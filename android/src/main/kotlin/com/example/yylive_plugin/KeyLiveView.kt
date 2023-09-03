package com.example.yylive_plugin

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.example.yylive_plugin.util.EvaDownloader
import com.yy.yyeva.inter.IEvaFetchResource
import com.yy.yyeva.inter.OnEvaResourceClickListener
import com.yy.yyeva.mix.EvaResource
import com.yy.yyeva.mix.EvaSrc
import com.yy.yyeva.util.EvaVideoEntity
import com.yy.yyeva.util.ScaleType
import com.yy.yyeva.view.EvaAnimViewV3
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import java.net.URL

class KeyLiveView internal constructor(
    var context: Context,
    var messenger: BinaryMessenger,
    id: Int,
    params: Map<String?, Any?>
) :
    PlatformView, MethodChannel.MethodCallHandler {

    private var evaDownloader: EvaDownloader? = null
    private val yyNativeView: EvaAnimViewV3
    // 视频信息
    private var videoPath = "effect.mp4"
    private var videoLoop = 1

    init {
        yyNativeView = EvaAnimViewV3(context)
        if (params.containsKey("videoPath")) {
            videoPath = params["videoPath"] as String
        }

        if (params.containsKey("videoLoop")) {
            videoLoop = params["videoLoop"] as Int
        }
        val methodChannel = MethodChannel(messenger, LiveViewConfig.nativeKeyLiveView + "_" + id)
        methodChannel.setMethodCallHandler(this)
        initView(params)
    }

    private fun initView(params: Map<String?, Any?>) {
        if (evaDownloader == null) {
            evaDownloader = EvaDownloader(context)
        }

        yyNativeView.setScaleType(ScaleType.CENTER_CROP)
        /**
         * 注册资源获取类
         */
        yyNativeView.setFetchResource(object : IEvaFetchResource {
            /**
             * 获取图片资源
             * 无论图片是否获取成功都必须回调 result 否则会无限等待资源
             */
            override fun setImage(resource: EvaResource, result: (Bitmap?, EvaSrc.FitType?) -> Unit) {
                /**
                 * tag是素材中的一个标记，在制作素材时定义
                 * 解析时由业务读取tag决定需要播放的内容是什么
                 * 比如：一个素材里需要显示多个头像，则需要定义多个不同的tag，表示不同位置，需要显示不同的头像，文字类似
                 */
                val tag = resource.tag
                if (params.containsKey(tag)){
                    val url = params[tag] as String
                    evaDownloader?.decodeImageFromURL(
                        URL(url),
                        object : EvaDownloader.ParseImageCompletion {
                            override fun onComplete(bitmap: Bitmap) {
                                result(bitmap, null)
                            }
                            override fun onError(){
                                result(null, null)
                            }
                        })
                }else {
                    result(null, null)
                }
            }

            /**
             * 获取文字资源
             */
            override fun setText(resource: EvaResource, result: (EvaResource) -> Unit) {
                val tag = resource.tag
                var text = params[tag] as String?
                if (text != null && text.isNotEmpty()) {
                    resource.text = text
                    resource.textAlign = "center"
                }
                result(resource)
            }

            /**
             * 播放完毕后的资源回收
             */
            override fun releaseSrc(resources: List<EvaResource>) {
            }
        })

        // 注册点击事件监听
        yyNativeView.setOnResourceClickListener(object : OnEvaResourceClickListener {
            override fun onClick(resource: EvaResource) {
            }
        })

//        playLocal()
//        playRemote("https://lxcode.bs2cdn.yy.com/084e52e9-fd58-4967-ba8b-cd3c4d6c1849.mp4")
    }

    private fun playLocal() {
        Thread {
            //循环三次
            yyNativeView.setLoop(videoLoop)
            yyNativeView.startPlay(context.getAssets(), videoPath)
        }.start()
    }

    private fun playRemote(url: String) {
        if (evaDownloader == null) {
            evaDownloader = EvaDownloader(context)
        }
        evaDownloader?.decodeFromURL(
            URL(url),
            object : EvaDownloader.ParseCompletion {
                override fun onComplete(videoItem: EvaVideoEntity) {
                    play(videoItem)
                }
                override fun onError() {
                }
            })
    }

    private fun play(videoInfo: EvaVideoEntity) {
        // 播放前强烈建议检查文件的md5是否有改变
        // 因为下载或文件存储过程中会出现文件损坏，导致无法播放
        Thread {
            val file = videoInfo.mCacheDir
            if (!file.exists()) {
                return@Thread
            }
            yyNativeView.startPlay(file)
        }.start()
    }

    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
//        println("插件 MethodChannel call.method:" + methodCall.method + "  call arguments:" + methodCall.arguments)
        if ("play" == methodCall.method) {
            result.success("成功")
            playLocal()
        }else if ("setText" == methodCall.method) {
            val text = methodCall.arguments as String
            result.success("修改成功")
        }
    }

    override fun getView(): View {
        return yyNativeView
    }

    override fun dispose() {
    }
}
