package com.example.yylive_plugin

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.shim.ShimPluginRegistry

object FlutterViewPlugin {
    fun registerWith(flutterEngine: FlutterEngine?) {
        val key = FlutterViewPlugin::class.java.canonicalName
        val shimPluginRegistry = ShimPluginRegistry(flutterEngine!!)
        if (shimPluginRegistry.hasPlugin(key)) {
            return
        }
        val registrar = shimPluginRegistry.registrarFor(key)
        registrar.platformViewRegistry()
            .registerViewFactory(LiveViewConfig.nativeKeyLiveView, KeyLiveFactory(registrar.messenger()))
    }
}