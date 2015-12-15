package com.github.madroid.recyclerviewheader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.madroid.library.RecyclerViewHeader;

import java.util.ArrayList;

import static com.github.madroid.recyclerviewheader.R.id.recycler;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecycler ;
    private ArrayList<String> mList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initRecycler();
    }

    private void initRecycler() {
        mRecycler = (RecyclerView) findViewById(recycler);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(new MyAdapter());

        RecyclerViewHeader header = RecyclerViewHeader.fromXml(this, R.layout.recycler_view_header) ;
        header.attachTo(mRecycler);

    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView item ;
        public MyViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.item) ;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.view_recycler_item, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
           // holder.item.setText();
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }
}
