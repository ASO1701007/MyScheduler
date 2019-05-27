package jp.ac.asojuku.st.myscheduler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.lang.IllegalArgumentException
//import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


//スケジュールの新規機能・更新・削除画面
class ScheduleEditActivity : AppCompatActivity() {

    //Realmインスタンス操作用の変数
    private lateinit var realm: Realm;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
        //Realmの接続をオープンしてインスタンス変数に代入
        realm = Realm.getDefaultInstance();

    }

    //画面インスタンス表示・再表示時のライフサイクルコールバックメソッド
    override fun onResume() {
        super.onResume()

        //この画面が開かれる際にインテントにスケジュールIDが設定されている場合は取得
        //ディフォルトの−１が取得された場合は「新規登録」呼び出し、それ以外は「詳細」呼び出し
        val scheduleId = intent?.getLongExtra("schedule_id",-1L);

        //スケジュールIDがディフォルト値（ー1）以外の場合は、詳細情報をDBから取得
        if (scheduleId != -1L){
            //詳細情報を Realmから検索取得
            val schedule = realm.where<Schedule>()
                    .equalTo("id",scheduleId)//条件指定を追加（idが同じ）
                    .findFirst();//最初にヒットしたもの一件を取得

            //取得したスケジュール情報を各入力欄に設定して表示
            //日付欄
            dateEdit.setText(DateFormat.format("yyyy/MM/dd",schedule?.date));
            //タイトル
            titleEdit.setText(schedule?.title);
            //スケジュール詳細
            detailEdit.setText(schedule?.detail);

            //削除ボタンは表示状態に設定
            delete.visibility = View.VISIBLE;
        }
        else{
            //スケジュールIDがデフォルト値の場合、新規登録モード
            //削除ボタンは非表示(かつ、存在する)に設定
            delete.visibility = View.INVISIBLE;
        }



        //保存ボタン（save）のclickリスナー処理を設定
        save.setOnClickListener {

            //scheduleIdの値によって保存ボタン処理を分岐
            when (scheduleId) {
                //デフォルト値（ー１L）の時、新規登録処理
                -1L -> {


                    //Realmのトランザクション処理
                    realm.executeTransaction {
                        //Realmからidの最大値を取得
                        val maxId = realm.where<Schedule>().max("id");
                        //idの最大値をlong型に変換（変換できない場合はディフォルト値0L）にしてから＋１
                        val nextId = (maxId?.toLong() ?: 0L) + 1;
                        //Realmからidを指定してモデルクラス（Schedule）のインスタンスを生成
                        val schedule = realm.createObject<Schedule>(nextId);
                        //日付入力欄の値を日付型に変換して新規モデルクラスのインスタンスに設定
                        dateEdit.text.toString().toDate("yyyy/MM/dd")?.let {
                            //変換した日付型オブジェクト(it)をモデルの日付項目に設定
                            schedule.date = it;
                        }
                        //タイトル入力欄の値（文字列）を新規モデルクラスのインスタンスに設定
                        schedule.title = titleEdit.text.toString();
                        //詳細入力欄の値（文字列）を新規モデルクラスのインスタンスに設定
                        schedule.detail = detailEdit.text.toString();
                    }
                    //モデルクラスのインスタンスへの設定が完了したら、okアラートを表示
                    this.alert("追加しました") {
                        //okボタンを押されたらおkダイアログを閉じる
                        yesButton { finish() }
                    }.show();
                }
                //デフォルト値(-1L)以外の時、更新処理
                else->{
                    //Realmのトランザクション処理
                    realm.executeTransaction {
                        val schedule = realm.where<Schedule>()
                                .equalTo("id",scheduleId)//条件指定を追加(idが同じ)
                                .findFirst()//最初にヒットしたもの一覧を取得
                        //取得したスケジュール(詳細情報)を画面表示項目の値で再設定
                        //日付欄を設定
                        val strDate = dateEdit.text.toString();//日付欄の文字列
                        strDate.toDate("yyyy/MM/dd")?.let {
                            schedule?.date = it//「文字列->日付」の変換結果の日付型を設定
                        }
                        //タイトル欄の値を設定
                        schedule?.title = titleEdit.text.toString()
                        //詳細欄の値を設定
                        schedule?.detail =dateEdit.text.toString()

                        //トランザクションが完了すると、スケジュールの設定値でDBレコード再保存が完了される
                        //okアラートダイアログ表示
                        this.alert ("修正しました"){
                            //okボタンを押されたら
                            yesButton {
                                finish() //ダイアログを閉じる
                            }
                        }.show();//アラートを表示
                    }
                }
            }
        }
        //削除ボタンのクリックリスナー処理
        delete.setOnClickListener {
            //Realmのトランザクション処理
            realm.executeTransaction {
                //idを元にDBからスケジュールのインスタンスを取得
                val schedule = realm.where<Schedule>()
                        .equalTo("id", scheduleId)//条件指定を追加(idが同じ)
                        .findFirst()//最初にヒットしたもの一覧を取得
                //取得したインスタンスのレコードをRealmから削除
                schedule?.deleteFromRealm();

                //トランザクション完了時にRealmからレコードが削除される
                //アラートを表示
                this.alert ("削除しました" ){
                    yesButton {
                        finish();
                    }
                }.show();
            }
        }
    }

    //画面インスタンス破棄時のライフサイクル・コールバックメソッド
    override fun onDestroy() {
        super.onDestroy()
        //Realmの接続をクローズ
        realm.close();
    }
    //Stringクラスの拡張関数を作る
    //文字列型を日付型に変更する関数
    fun String.toDate(pattern:String = "yyyy/MM/dd HH:mm"):Date?{
        val sdFormat = try {
            //引数で渡された文字列を元にフォーマットクラスのインスタンスを作る
            SimpleDateFormat(pattern);
        }catch (e: IllegalArgumentException){
            null;
        }
        //sdFormat変数にlet式内の処理を実施して戻り値を日付型に代入
        val date = sdFormat?.let{
            try {
                //sdFormatを使って自分インスタンス（String） = (true)を日付オブジェクトに変換
                //itはsdFormat thisはこのString
                //sdFormat.parse（Stringオブジェクト）
                it.parse(this);
            }catch (e:ParseException){
                //日付型に生成失敗したら、nullを返す
                null;
            }
        }
        //dateには日付インスタンスか、nullが入っているので、リターン
        return date;
    }
}
