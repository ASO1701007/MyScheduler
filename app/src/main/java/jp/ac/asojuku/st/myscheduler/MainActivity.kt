package jp.ac.asojuku.st.myscheduler

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.realm.Realm
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    private lateinit var realm: Realm;

    //画面生成時のライフサイクルイベント
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //Realmへ接続してインスタンスを取得
        realm = Realm.getDefaultInstance();
        //Realmからscheduleモデルのデータを取得
        val schedules = realm.where<Schedule>().findAll();
        //リストビューのadapterプロパティに検索結果を入れたアダプターを設定
        listView.adapter = SchesuleAdapter(schedules);

        //フローティングアクションボタンがクリックされた時のリスナー処理を設定
        fab.setOnClickListener{ view->
             //新規登録画面へ遷移
            startActivity<ScheduleEditActivity>();
//            //スナックバーを表示
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                  .setAction("Action", null).show()
        }
    }
    //画面の表示再表示のライフサイクルイベントのコールバックメソッド
    override fun onResume() {
        super.onResume()

        //リストビューのOnItemClickListener処理の設定
        //アイテム（項目）を選んだ時の反応
        listView.setOnItemClickListener {
            parent,//第一引数：リストビューのアダプタ
            view,//第二引数：タップされた項目のビュー部品（セル）
            position, //第三引数：タップされた項目のデータリスト上の位置（インデックス）
            id //第四引数：タップされた項目のid
            -> //アロー演算子で上記の引数を使った処理を記述
            val schedule=parent.getItemAtPosition(position) as Schedule
            //詳細画面へスケジュールのidをインテントに入れ込んで引き渡して画面遷移
            startActivity<ScheduleEditActivity>("schedule_id" to schedule.id);
        }
    }

    //画面破棄時のライフサイクルイベント
    override fun onDestroy() {
        super.onDestroy()
        //Realmとの接続を閉じる
        realm.close();
    }

}
