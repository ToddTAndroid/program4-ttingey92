package edu.uwyo.toddt.checkbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.security.Provider;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // content Uri's
    public static final String PROVIDER="edu.cs4730.prog4db";
    public static Uri CONTENT_URI_cat = Uri.parse("content://"+PROVIDER+"/Category");
    public static Uri CONTENT_URI_acc = Uri.parse("content://"+PROVIDER+"/Accounts");
    public static Uri CONTENT_URI_trans1 = Uri.parse("content://"+PROVIDER+"/Accounts/transactions/1");

    // content keys
    public static final String KEY_NAME = "Name";
    public static final String KEY_CATNAME = "CatName";
    public static final String KEY_DATE = "Date";
    public static final String KEY_TYPE = "CheckNum";
    public static final String KEY_AMOUNT = "Amount";
    public static final String KEY_CAT = "Category";
    public static final String KEY_ROWID = "_id";
    public static final String TABLE_CHECKING = "checking";

    // content string arrays
    String[] cv_trans = new String[]{KEY_DATE, KEY_TYPE, KEY_NAME, KEY_AMOUNT, KEY_CAT};
    String[] cv_cat = new String[]{KEY_CATNAME};
    String[] cv_acc = new String[]{KEY_NAME};
    String[] projection_trans = new String[]{TABLE_CHECKING+"."+KEY_ROWID, KEY_DATE, KEY_TYPE, KEY_NAME, KEY_AMOUNT, KEY_CAT};
    String[] projection_cat = new String[]{KEY_ROWID, KEY_CATNAME};

    // Variables for the fragment
    ContentValues values;
    String ID="";
    private SimpleCursorAdapter dataAdapter;
    private SimpleCursorAdapter spinAdapter;
    private ViewSwitcher transSwitcher;
    boolean isArrow = false;
    ListView list;
    EditText date_ed, type_ed, name_ed, amount_ed;
    Spinner cat_ed;

    public TransFragment() {
        // Required empty public constructor
    }

    public static TransFragment newInstance(String param1, String param2) {
        TransFragment fragment = new TransFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View transInflater = inflater.inflate(R.layout.fragment_trans, container, false);

        transSwitcher = (ViewSwitcher) transInflater.findViewById(R.id.transSwitcher);
        date_ed = (EditText) transInflater.findViewById(R.id.edit_date);
        type_ed = (EditText) transInflater.findViewById(R.id.edit_type);
        name_ed = (EditText) transInflater.findViewById(R.id.edit_name);
        amount_ed = (EditText) transInflater.findViewById(R.id.edit_amount);
        cat_ed = (Spinner) transInflater.findViewById(R.id.cat_spinner);
        final FloatingActionButton fab = (FloatingActionButton) transInflater.findViewById(R.id.transFab);
        final Button btnDelete = (Button) transInflater.findViewById(R.id.btnTransDelete);
        Button btnSubmit = (Button) transInflater.findViewById(R.id.btnTransSubmit);

        // Move database info to ListView.
        list = (ListView) transInflater.findViewById(R.id.trans_listV);
        list.setClickable(true);
        Cursor c = getActivity().getContentResolver().query(CONTENT_URI_trans1, projection_trans, null, null, null);

        if(c!=null) {
            // columns to use
            String[] columns = new String[] {
                    KEY_ROWID,
                    KEY_DATE,
                    KEY_TYPE,
                    KEY_NAME,
                    KEY_AMOUNT,
                    KEY_CATNAME
            };

            // views to bind data to
            int[] to = new int[] {
                    R.id.trans_ID,
                    R.id.trans_date,
                    R.id.trans_type,
                    R.id.trans_name,
                    R.id.trans_amount,
                    R.id.trans_category
            };

            // create adapter
            dataAdapter = new SimpleCursorAdapter(
                    getActivity(), R.layout.trans_info,
                    c,
                    columns,
                    to,
                    0);

            list.setAdapter(dataAdapter);
        }

        Cursor catC = getActivity().getContentResolver().query(CONTENT_URI_cat, projection_cat, null, null, null);

        if(catC!=null) {

            String[] spinCols = new String[]{
                    KEY_CATNAME
            };

            int[] rows = new int[]{android.R.id.text1};

            spinAdapter = new SimpleCursorAdapter(
                    getActivity(), android.R.layout.simple_spinner_item,
                    catC,
                    spinCols,
                    rows,
                    0
                    );
            spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cat_ed.setAdapter(spinAdapter);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transSwitcher.showNext();
                if(!isArrow) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow));
                    isArrow = true;
                }
                else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plus));
                    date_ed.setText("");
                    type_ed.setText("");
                    name_ed.setText("");
                    amount_ed.setText("");
                    cat_ed.setSelection(1);
                    ID="";
                    btnDelete.setVisibility(View.INVISIBLE);
                    isArrow = false;
                }
            }
        });



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                String[] aString = new String[] {};
                if(!ID.equals("")){
                    getActivity().getContentResolver().delete(CONTENT_URI_trans1, "_id = " + ID, aString);
                }
                if(!isArrow) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow));
                    isArrow = true;
                }
                else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plus));
                    date_ed.setText("");
                    type_ed.setText("");
                    name_ed.setText("");
                    amount_ed.setText("");
                    cat_ed.setSelection(1);
                    ID="";
                    btnDelete.setVisibility(View.INVISIBLE);
                    isArrow = false;
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_check_book, new TransFragment())
                        .commit();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {

                String date = date_ed.getText().toString();
                String type = type_ed.getText().toString();
                String name = name_ed.getText().toString();
                String amount = amount_ed.getText().toString();
                String catid = Long.toString(cat_ed.getSelectedItemId());

                String[] args = new String[]{date, type, name, amount, catid};

                values = createData(cv_trans, args);

                if(ID.equals("")) {
                    Uri rt_uri = getActivity().getContentResolver().insert(CONTENT_URI_trans1, values);
                }
                else {
                    getActivity().getContentResolver().update(CONTENT_URI_trans1, values, "_id = " + ID, null);
                    ID = "";
                }


                if(!isArrow) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow));
                    isArrow = true;
                }
                else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plus));
                    date_ed.setText("");
                    type_ed.setText("");
                    name_ed.setText("");
                    amount_ed.setText("");
                    cat_ed.setSelection(1);
                    ID="";
                    btnDelete.setVisibility(View.INVISIBLE);
                    isArrow = false;
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_check_book, new TransFragment())
                        .commit();
            }
        });



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) list.getItemAtPosition(position);

                ID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROWID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME));
                String amount = cursor.getString(cursor.getColumnIndexOrThrow(KEY_AMOUNT));
                String catID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CAT));
                date_ed.setText(date);
                type_ed.setText(type);
                name_ed.setText(name);
                amount_ed.setText(amount);
                long idNum = Long.parseLong(catID);
                cat_ed.setSelection(getIndex(cat_ed, idNum));
                btnDelete.setVisibility(View.VISIBLE);
                transSwitcher.showNext();
                if(!isArrow) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow));
                    isArrow = true;
                }
                else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plus));
                    date_ed.setText("");
                    type_ed.setText("");
                    name_ed.setText("");
                    amount_ed.setText("");
                    cat_ed.setSelection(1);
                    ID="";
                    btnDelete.setVisibility(View.INVISIBLE);
                    isArrow = false;
                }
            }
        });


        return transInflater;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String name);
    }

    public ContentValues createData(String[] key, String[] data) {
        ContentValues cv = new ContentValues();
        for (int i=0; i<key.length; i++) {
            cv.put(key[i], data[i]);
        }
        return cv;
    }

    public int getIndex(Spinner spinner, long catID){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            long ID = spinner.getItemIdAtPosition(i);
            if (ID == catID){
                index = i;
            }
        }
        return index;
    }


}
