package com.example.dcc.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dcc.R;
import com.example.dcc.helpers.EDaily;
import com.example.dcc.helpers.ObjectStorage;
import com.example.dcc.helpers.hacks.DCCArrayList;
import com.example.dcc.helpers.hacks.EdailyArrayAdapter;

/**
 *
 * Created by harmonbc on 5/29/13.
 */
public class EDailyList extends ListFragment {
    EdailyArrayAdapter adapter;
    ListView listview;
    DCCArrayList edailies;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edaily_list,
                container, false);
        edailies = (DCCArrayList)getArguments().getSerializable("edailies");

        //Make an adapter
        adapter = new EdailyArrayAdapter(getActivity(), edailies);

        //Get the listview, set the listview and the adapter
        listview = (ListView)view.findViewById(R.id.edailylistv);
        Log.e("GILMORE", (listview != null) + " " + (adapter != null));
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Get the corrosponding item
                EDaily edaily = (EDaily) edailies.get(i);
                //Make the fragment that will be launched
                MemberDetailFragment detailFrag = new MemberDetailFragment();
                //Place the member to be displayed into a bundle
                Bundle bundle = new Bundle();
                bundle.putSerializable("edaily", edaily);
                detailFrag.setArguments(bundle);

                //Launch the fragment activity
                FragmentManager manager = getActivity().getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                ObjectStorage.setFragment(R.id.fragmentcontainerright, detailFrag);
                transaction.replace(R.id.fragmentcontainerright, detailFrag);

                transaction.commit();
            }
        });


        for(Object e : edailies){
            adapter.add(((EDaily)e));
        }
        return view;
    }
}