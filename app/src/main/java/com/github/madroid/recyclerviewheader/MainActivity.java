package com.github.madroid.recyclerviewheader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.madroid.library.RecyclerViewHeader;

import java.util.ArrayList;

import static com.github.madroid.recyclerviewheader.R.id.recycler;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecycler ;
    private ArrayList<String> mList ;
    private ViewPager mViewPager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData() ;
        setupView();
    }

    private void initData() {

        mList = new ArrayList<>() ;
        for (int i = 0; i < 100; i++) {
            mList.add("item : " + i);
        }
    }

    private void initRecycler() {
        mRecycler = (RecyclerView) findViewById(recycler);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        mRecycler.setAdapter(new MyAdapter());

        RecyclerViewHeader header = RecyclerViewHeader.fromXml(this, R.layout.recycler_view_header) ;
        header.attachTo(mRecycler);


    }

    private void setupView() {
        initRecycler();

        mViewPager = (ViewPager) findViewById(R.id.viewpager) ;
        mViewPager.setAdapter(new RecyclerFragmentPagerAdapter(getSupportFragmentManager()));

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
           holder.item.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class RecyclerFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public RecyclerFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RecyclerHeaderFragment.newInstance();
                case 1:
                    return RecyclerHeaderFragment.newInstance();
                case 2:
                    return RecyclerHeaderFragment.newInstance();
                default:
                    return RecyclerHeaderFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private static class RecyclerHeaderFragment extends Fragment {

        static RecyclerHeaderFragment newInstance() {
            return new RecyclerHeaderFragment() ;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_recycler_header, null, false) ;
            view.findViewById(R.id.header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Header View", Toast.LENGTH_LONG).show() ;
                }
            });
            return view ;
        }
    }
}
