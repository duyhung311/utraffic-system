package com.hcmut.admin.utrafficsystem.ui.voucher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.adapter.WordAdapter;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, MapActivity.OnBackPressCallback,View.OnClickListener {

    private static String word;
    private WordAdapter adapter;
    private List<String> list;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private ListView wordList;
    private TextView editQuery;
    private TextView clearText;
    Toolbar toolbar;
    Bundle bundle = new Bundle();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search, container, false);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Tìm kiếm");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });


        wordList = view.findViewById(R.id.wordList);
        editQuery = view.findViewById(R.id.editQuery);
        clearText = view.findViewById(R.id.clearAll);
        clearText.setOnClickListener(this);



        wordList.setOnItemClickListener(this);
        wordList.setOnItemLongClickListener(this);

        // Return a collection view of the values contained in this map
        list = new ArrayList<>(getWords(getContext()).keySet());

        adapter = new WordAdapter(getContext(),  list);
        wordList.setDivider(null);
        wordList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (editQuery.getText().toString().trim().length() == 1) {

                    editQuery.clearFocus();
                    editQuery.requestFocus();
                    editQuery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_back, 0, R.drawable.ic_close, 0);
                }
            }
        });

        InputMethodManager imm = (InputMethodManager) ((AppCompatActivity)getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        editQuery.requestFocus();

        editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
//                    Intent searchIntent = new Intent(SearchFragment.this, ResultActivity.class);
                    word = editQuery.getText().toString().trim();
//                    // Set Key with its specific key
                    setWord(getContext(), word, word);
//                    searchIntent.putExtra(KEYWORD, word);
//                    startActivity(searchIntent);
                    bundle.putString("word", word);
                    hideSoftKeyboard(getActivity());
                    NavHostFragment.findNavController(SearchFragment.this)
                            .navigate(R.id.action_searchFragment_to_resultFragment,bundle);
                    return true;
                }
                return false;
            }
        });

        editQuery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (event.getRawX() >= (editQuery.getRight() - editQuery.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        editQuery.setText("");
                        return true;
                    }else if ((event.getRawX() + editQuery.getPaddingLeft()) <= (editQuery.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() + editQuery.getLeft())) {
//                        Intent mainIntent = new Intent(SearchActivity.this, ProductActivity.class);
//                        startActivity(mainIntent);
                        return true;
                    }
                }
                return false;
            }
        });

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(word)) {
                    // Clear the adapter, then add list
                    adapter.clear();
                    list = new ArrayList<>(getWords(getContext()).keySet());
                    adapter.addAll(list);
                    wordList.setAdapter(adapter);
                }
            }
        };

        sharedpreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void setWord(Context context , String key , String word){
        sharedpreferences = context.getSharedPreferences("history_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(String.valueOf(key), word);
        editor.apply();
    }

    public Map<String, ?> getWords(Context context){
        sharedpreferences = context.getSharedPreferences("history_data", Context.MODE_PRIVATE);
        // Returns a map containing a list of pairs key/value representing the preferences.
        return sharedpreferences.getAll();
    }

    public static void clearSharedPreferences(Context context){
        context.getSharedPreferences("history_data", Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static void clearOneItemInSharedPreferences(String key, Context context){
        context.getSharedPreferences("history_data", Context.MODE_PRIVATE).edit().remove(key).apply();
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(listener);
        super.onDestroy();
    }

    public void clearAll() {
        clearSharedPreferences(getContext());
        adapter.clear();
        Toast.makeText(getContext(), "Cleared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((MapActivity) getContext()).hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(getContext(), ResultActivity.class);
//        // Send KEYWORD to ResultActivity
//        intent.putExtra(KEYWORD, list.get(position));
//        startActivity(intent);
        bundle.putString("word", list.get(position));
        hideSoftKeyboard(getActivity());
        NavHostFragment.findNavController(SearchFragment.this)
                .navigate(R.id.action_searchFragment_to_resultFragment,bundle);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v("long clicked","position: " + position);
        // Get value of a specific position
        word = list.get(position);
        // Set word as a key
        clearOneItemInSharedPreferences(word, getContext());
        // Remove element from adapter
        adapter.remove(word);
        Toast.makeText(getContext(), "Removed", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onBackPress() {
        hideSoftKeyboard(getActivity());
        NavHostFragment.findNavController(SearchFragment.this).popBackStack();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearAll:
                clearAll();
                break;
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
