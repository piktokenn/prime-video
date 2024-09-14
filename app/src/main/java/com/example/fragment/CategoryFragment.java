package com.example.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.CategoryAdapter;
import com.example.item.ItemCategory;
import com.example.util.Constant;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;
import com.example.util.RecyclerTouchListener;
import com.rcloud.netflix.AdController;
import com.rcloud.netflix.MainActivity;
import com.rcloud.netflix.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CategoryFragment extends Fragment {

    ArrayList<ItemCategory> mListItem;
    public RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    TextView textView;
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;
    ItemCategory itemCategory;
    private Activity activity;
//    private int AD_COUNT = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

//        prf = new PrefManager(context);
//
//        //Newrelease native ads
//        adControllerlocalInstance = AdController.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);


        mListItem = new ArrayList<>();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_category));
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.rv_video);
        textView = rootView.findViewById(R.id.txt_no);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        fragmentManager = getFragmentManager();

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getSubCat().execute(Constant.CATEGORY_URL);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(activity, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                itemCategory = mListItem.get(position);
                Constant.CATEGORY_IDD = itemCategory.getCategoryId();
                Constant.CATEGORY_TITLEE = itemCategory.getCategoryName();

                if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {
                    AdController.getInstance().showInterAdCallBack(activity, new AdController.MyCallback() {
                        @Override
                        public void callbackCall() {
                            CategoryListFragment categoryListFragment = new CategoryListFragment();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.hide(CategoryFragment.this);
                            fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                            fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                            fragmentTransaction.commit();
                            ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                        }
                    });
                } else {
                    CategoryListFragment categoryListFragment = new CategoryListFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(CategoryFragment.this);
                    fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                    fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                    fragmentTransaction.commit();
                    ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class getSubCat extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //error solved
            if(activity == null)
                return;

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemCategory objItem = new ItemCategory();

                        objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                        objItem.setCategoryId(objJson.getString(Constant.CATEGORY_CID));
                        objItem.setCategoryImageUrl(objJson.getString(Constant.CATEGORY_IMAGE));

                        mListItem.add(objItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }

    private void displayData() {
        if (activity != null) {
            categoryAdapter = new CategoryAdapter(activity, mListItem);
            recyclerView.setAdapter(categoryAdapter);

            if (categoryAdapter.getItemCount() == 0) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_category));
    }
}
