package jp.ac.asojuku.st.myscheduler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmBaseAdapter
import org.w3c.dom.Text
import android.text.format.DateFormat

/*Realmとデータを橋渡しするアダプタークラス*/
class SchesuleAdapter(data: OrderedRealmCollection<Schedule>?) : RealmBaseAdapter<Schedule>(data) {

    //リストビューのセル（View部品）の定義を保持するためのクラス
    inner class ViewHolder(cell:View) {
        //日付表示用のビューの定義保存用
        val date = cell.findViewById<TextView>(android.R.id.text1);
        //タイトル表示用のビューの定義保存用
        val title = cell.findViewById<TextView>(android.R.id.text2);

    }
    //リストビューのセルへデータを補充するメソッド
    //第１引数：getView()しようとしているセルのリスト上の位置
    //第２引数：getView()しようとしているビューの部品（セル）：新規はnull
    //第３引数：ビュー部品（セル）が所属する親ビュー（リストビュー）
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //getView()しようとするビュー部品
        val view:View;
        //ビュー部品を再利用するためのViewHolder
        val viewHolder:ViewHolder;

        //新規の時と再利用の時で分岐
        when(convertView){
            //新規ビュー部品表示の時
            null->{

                //LayoutInflatorのインスタンスを取得
                val inflater = LayoutInflater.from(parent?.context);

                //LayoutInflaterのinflate()メソッドでXML定義からView部品を生成
                //第一引数：生成するビュー部品のレイアウト定義XML
                //第二引数：生成するビュー部品を所属させる親ビュー部品（今回はリストビュー）
                //第三引数：生成するビュー部品を親部品に所属させるときはfalse,独立はture
                view = inflater.inflate(android.R.layout.simple_list_item_2,
                        parent,false);
                //生成したviewの情報を保存するviewHolderを生成する
                viewHolder = ViewHolder(view);
                //viewHolderインスタンスをview部品のtagに記憶しておく
                view.tag = viewHolder;
            }
            //再利用ビュー部品の表示の時
            else->{
                //リターンするビュー部品は引き渡されたビュー部品
                view = convertView;
                //再び再利用するためにビュー部品のViewHolderの情報を取得
                viewHolder = view.tag as ViewHolder;
            }
        }

        //ViewHolderのビュー部品のデータ情報を更新
        this.adapterData?.run {
            //リストデータから位置を指定してスケジュールを取得
            val schedule = this.get(position);
            viewHolder.date.text= DateFormat.format("yyyy/MM/dd",schedule.date);
            viewHolder.title.text = schedule.title;
        }

        //組み立てたビューをリターンしてリストビューに表示
        return view;
    }
}
