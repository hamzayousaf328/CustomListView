package com.example.tetris;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FirstFragment extends Fragment {

    ArrayList<HashMap<String, String>> hashMapForJSON;

    ListView listViewForJSON;

    //Initializing listViewForJSON and hashMapForJSON when view created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View viewList = inflater.inflate(R.layout.fragment_first, container, false);
        listViewForJSON = (ListView) viewList.findViewById(R.id.listview_first);
        hashMapForJSON = new ArrayList<>();

        return viewList;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetJsonData getJsonData = new GetJsonData();
        getJsonData.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public class GetJsonData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String s = "";
            try {
                URL url = new URL("https://api.github.com/search/repositories?q=tetris");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                //Getting the response code
                int responsecode = conn.getResponseCode();

                if (responsecode != 200) {
                    throw new RuntimeException("HttpResponseCode: " + responsecode);
                } else {

                    String inline = "";
                    Scanner scanner = new Scanner(url.openStream());

                    //Write all the JSON data into a string using a scanner
                    while (scanner.hasNext()) {
                        inline += scanner.nextLine();
                    }

                    //Close the scanner
                    scanner.close();

                    //Made JSON object for parsing
                    JSONObject data_obj = new JSONObject(inline);

                    //Get the required object from the above created object
                    JSONArray arr =  data_obj.getJSONArray("items");

                    //Get the required data using its key
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String nameOfItem = obj.getString("name");
                        String loginName = obj.getJSONObject("owner").getString("login");
                        String sizeOfItem = obj.getString("size");
                        String has_wiki = obj.getString("has_wiki");

                        HashMap<String, String> tempHashMap = new HashMap<>();
                        tempHashMap.put("name", nameOfItem);
                        tempHashMap.put("loginName", loginName);
                        tempHashMap.put("size", sizeOfItem);
                        tempHashMap.put("has_wiki", has_wiki);

                        //Putting JSON data in hashMapForJSON
                        hashMapForJSON.add(tempHashMap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {

            //Setting up MySimpleArrayAdapter
            MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
                    getActivity(),
                    hashMapForJSON,
                    R.layout.row_layout,
                    new String[] {"name", "loginName", "size", "has_wiki"},
                    new int[]{R.id.textView, R.id.textView2, R.id.textView3, R.id.textView4}
            );
            listViewForJSON.setAdapter(adapter);
        }
        public class MySimpleArrayAdapter extends SimpleAdapter {
            private final Context context;
            private final List<? extends Map<String, ?>> values;

            public MySimpleArrayAdapter(Context context, List<? extends Map<String, ?>> values, int resource, String[] from, int[] to) {
                super(context, values, resource, from, to);
                this.context = context;
                this.values = values;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_layout, parent, false);

                //Getting the position and values of items in row in row_layout
                TextView textViewName = (TextView) rowView.findViewById(R.id.textView);
                TextView textViewLoginName = (TextView) rowView.findViewById(R.id.textView2);
                TextView textViewSize = (TextView) rowView.findViewById(R.id.textView3);
                TextView textViewHasWiki = (TextView) rowView.findViewById(R.id.textView4);
                textViewName.setText(values.get(position).get("name").toString());
                textViewLoginName.setText(values.get(position).get("loginName").toString());
                textViewSize.setText(values.get(position).get("size").toString());
                textViewHasWiki.setText(values.get(position).get("has_wiki").toString());

                //Changing the values of Row in row_layout
                if(textViewHasWiki.getText().equals("true")) {
                    rowView.setBackgroundColor(Color.parseColor("#8CE9F0"));
                }

                return rowView;
            }
        }
    }

}