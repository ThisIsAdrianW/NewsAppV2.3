//
// Created by crazy udacity student on 20.06.2018.
// based on tutorial from: https://blog.squareboat.com/securing-api-keys-inside-android-apps-using-android-ndk-be2e7e29dfd3 by Vipul Asri on 02/04/17.
//com.example.android.newsapp

#include <jni.h>

extern "C" {

    JNIEXPORT jstring JNICALL
    Java_com_example_android_newsapp_MainActivity_getAPIKey(JNIEnv *env, jobject instance) {

        return env-> NewStringUTF("dGVzdA==");
    }

}

