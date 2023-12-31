
#include "eglcore.h"

#define LOG_TAG "EGLCore"
#define ELOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define ELOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

/**
 * EGL是介于诸如OpenGL 或OpenVG的Khronos渲染API与底层本地平台窗口系统的接口。它被用于处理图形管理、表面/缓冲捆绑、渲染同步及支援使用其他Khronos API进行的高效、加速、混合模式2D和3D渲染。
 * cangwang 2018.12.1
 */
EGLCore::EGLCore():mDisplay(EGL_NO_DISPLAY),mSurface(EGL_NO_SURFACE),mContext(EGL_NO_CONTEXT) {

}

EGLCore::~EGLCore() {
    mDisplay = EGL_NO_DISPLAY;
    mSurface = EGL_NO_SURFACE;
    mContext = EGL_NO_CONTEXT;
}

void EGLCore::start(ANativeWindow *window) {
    mDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    GLint majorVersion;
    GLint minorVersion;
    //获取支持最低和最高版本
    if (!eglInitialize(mDisplay,&majorVersion,&minorVersion)){
        ELOGE("eglInitialize failed: %d",eglGetError());
        return;
    }
    ELOGD("eglInitialize success");
    EGLConfig config = chooseConfig();
    ELOGD("chooseConfig success");

    mContext = createContext(mDisplay, config);
    ELOGD("createContext success");
    EGLint format = 0;
    if (!eglGetConfigAttrib(mDisplay,config,EGL_NATIVE_VISUAL_ID,&format)){
        ELOGE("eglGetConfigAttrib failed: %d",eglGetError());
    }
    ELOGD("eglGetConfigAttrib success");
    ANativeWindow_setBuffersGeometry(window,0,0,format);
    ELOGD("setBuffersGeometry success");
    //创建On-Screen 渲染区域
    mSurface = eglCreateWindowSurface(mDisplay,config,window,0);
    if (mSurface == nullptr || mSurface == EGL_NO_SURFACE){
        ELOGE("eglCreateWindowSurface failed: %d",eglGetError());
        return;
    }
    ELOGD("eglCreateWindowSurface success");
    if (eglMakeCurrent(mDisplay, mSurface, mSurface, mContext) == false) {
        ELOGE("make current error:${Integer.toHexString(egl?.eglGetError() ?: 0)}");
    }
    ELOGD("eglMakeCurrent success");
}

EGLConfig EGLCore::chooseConfig() {
    int configsCount = 0;
    EGLConfig configs;
//    EGLint* attributes = getAttributes();
    EGLint attributes[] =
            {EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, //指定渲染api类别
             EGL_RED_SIZE, 8,
             EGL_GREEN_SIZE, 8,
             EGL_BLUE_SIZE, 8,
             EGL_ALPHA_SIZE, 8,
             EGL_DEPTH_SIZE, 0,
             EGL_STENCIL_SIZE, 0,
             EGL_NONE
            };
    int configSize = 1;
    if (eglChooseConfig(mDisplay, attributes, &configs, configSize, &configsCount) == true) {
        return configs;
    } else {
        ELOGE("eglChooseConfig failed: %d",eglGetError());
    }
    return nullptr;
}

EGLint* EGLCore::getAttributes() {
    EGLint attribList[] =
            {EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, //指定渲染api类别
             EGL_RED_SIZE, 8,
             EGL_GREEN_SIZE, 8,
             EGL_BLUE_SIZE, 8,
             EGL_ALPHA_SIZE, 8,
             EGL_DEPTH_SIZE, 0,
             EGL_STENCIL_SIZE, 0,
             EGL_NONE
    };
    return attribList;
}

EGLContext EGLCore::createContext(EGLDisplay eglDisplay, EGLConfig eglConfig) {
    //创建渲染上下文
    //只使用opengles2
    GLint contextAttrib[] = {EGL_CONTEXT_CLIENT_VERSION, 3 ,
                             EGL_NONE};
    // EGL_NO_CONTEXT表示不向其它的context共享资源
    mContext = eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, contextAttrib);
    if (mContext == EGL_NO_CONTEXT){
        ELOGE("eglCreateContext failed: %d",eglGetError());
        return GL_FALSE;
    }
    return mContext;
}

GLboolean EGLCore::buildContext(ANativeWindow *window) {
    //与本地窗口通信
    mDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (mDisplay == EGL_NO_DISPLAY){
        ELOGE("eglGetDisplay failed: %d",eglGetError());
        return GL_FALSE;
    }

    GLint majorVersion;
    GLint minorVersion;
    //获取支持最低和最高版本
    if (!eglInitialize(mDisplay,&majorVersion,&minorVersion)){
        ELOGE("eglInitialize failed: %d",eglGetError());
        return GL_FALSE;
    }

    EGLConfig config;
    EGLint numConfigs = 0;
    //颜色使用565，读写类型需要egl扩展
    EGLint attribList[] = {
            EGL_RED_SIZE,5, //指定RGB中的R大小（bits）
            EGL_GREEN_SIZE,6, //指定G大小
            EGL_BLUE_SIZE,5,  //指定B大小
//            EGL_RENDERABLE_TYPE,EGL_OPENGL_ES3_BIT_KHR, //渲染类型，为相机扩展类型
            EGL_SURFACE_TYPE, EGL_PBUFFER_BIT,  //绘图类型，
            EGL_NONE
    };

    //让EGL推荐匹配的EGLConfig
    if(!eglChooseConfig(mDisplay,attribList,&config,1,&numConfigs)){
        ELOGE("eglChooseConfig failed: %d",eglGetError());
        return GL_FALSE;
    }

    //找不到匹配的
    if (numConfigs <1){
        ELOGE("eglChooseConfig get config number less than one");
        return GL_FALSE;
    }

    //创建渲染上下文
    //只使用opengles3
    GLint contextAttrib[] = {EGL_CONTEXT_CLIENT_VERSION,3,EGL_NONE};
    // EGL_NO_CONTEXT表示不向其它的context共享资源
    mContext = eglCreateContext(mDisplay,config,EGL_NO_CONTEXT,contextAttrib);
    if (mContext == EGL_NO_CONTEXT){
        ELOGE("eglCreateContext failed: %d",eglGetError());
        return GL_FALSE;
    }

    EGLint format = 0;
    if (!eglGetConfigAttrib(mDisplay,config,EGL_NATIVE_VISUAL_ID,&format)){
        ELOGE("eglGetConfigAttrib failed: %d",eglGetError());
        return GL_FALSE;
    }
    ANativeWindow_setBuffersGeometry(window,0,0,format);

    //创建On-Screen 渲染区域
    mSurface = eglCreateWindowSurface(mDisplay,config,window,0);
    if (mSurface == EGL_NO_SURFACE){
        ELOGE("eglCreateWindowSurface failed: %d",eglGetError());
        return GL_FALSE;
    }

    //把EGLContext和EGLSurface关联起来，单缓冲只使用了一个surface
    if (!eglMakeCurrent(mDisplay,mSurface,mSurface,mContext)){
        ELOGE("eglMakeCurrent failed: %d",eglGetError());
        return GL_FALSE;
    }

    ELOGD("buildContext Succeed");
    return GL_TRUE;
}

/**
 * 现在只使用单缓冲绘制
 */
void EGLCore::swapBuffer() {
    //双缓冲绘图，原来是检测出前台display和后台缓冲的差别的dirty区域，然后再区域替换buffer
    //1）首先计算非dirty区域，然后将非dirty区域数据从上一个buffer拷贝到当前buffer；
    //2）完成buffer内容的填充，然后将previousBuffer指向buffer，同时queue buffer。
    //3）Dequeue一块新的buffer，并等待fence。如果等待超时，就将buffer cancel掉。
    //4）按需重新计算buffer
    //5）Lock buffer，这样就实现page flip，也就是swapbuffer
    if (mDisplay != nullptr && mSurface != nullptr) {
        eglSwapBuffers(mDisplay, mSurface);
    }
}

void EGLCore::release() {
    ELOGD("release");
    eglDestroySurface(mDisplay,mSurface);
    eglMakeCurrent(mDisplay,EGL_NO_SURFACE,EGL_NO_SURFACE,EGL_NO_CONTEXT);
    eglDestroyContext(mDisplay,mContext);
    eglReleaseThread();
    eglTerminate(mDisplay);
    mDisplay = EGL_NO_DISPLAY;
    mContext = EGL_NO_CONTEXT;
    mSurface = EGL_NO_SURFACE;
}