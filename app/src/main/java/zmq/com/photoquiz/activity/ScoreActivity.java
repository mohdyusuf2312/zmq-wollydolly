package zmq.com.photoquiz.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import zmq.com.photoquiz.R;
import zmq.com.photoquiz.database.Score;

public class ScoreActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        listView = findViewById(R.id.score_list);
        MyAdapter adapter = new MyAdapter(this);
        listView.setAdapter(adapter);

    }

    private class MyAdapter extends BaseAdapter {
        private Context context;
        private List<Score> scoreList;

        public MyAdapter(Context context) {
            this.context = context;
            scoreList = MyApp.getDatabaseInstance().getScoreDao().getAll();
            Collections.sort(scoreList, new Comparator<Score>() {
                @Override
                public int compare(Score o1, Score o2) {
                    if (o1.getScore() < o2.getScore())
                        return 1;
                    else if (o1.getScore() > o2.getScore())
                        return -1;
                    else
                        return 0;
                }
            });
        }

        @Override
        public int getCount() {
            return scoreList.size();
        }

        @Override
        public Object getItem(int position) {
            return scoreList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.score_item, parent, false);
            TextView name = view.findViewById(R.id.player_name);
            TextView score = view.findViewById(R.id.player_score);
            name.setText(scoreList.get(position).getName());
            score.setText(String.valueOf(scoreList.get(position).getScore()));
            return view;
        }
    }
}
