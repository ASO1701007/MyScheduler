package jp.ac.asojuku.st.myscheduler

import android.app.Application
import io.realm.Realm

//Applicationを継承したアプリケーションクラス
class MySchedulerApplication:Application() {
    //アプリケーション全体で最初にやっておきたいことは、
    //アプリケーションクラスのライフサイクルの生成時に記述しておく
    override fun onCreate() {
        super.onCreate()
        //Realmの初期化
        Realm.init(this);

    }
}