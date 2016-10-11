package edu.uwyo.toddt.checkbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


public class CatFragment extends Fragment {

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

    // Variables
    String ID="";
    ContentValues values;
    private SimpleCursorAdapter dataAdapter;
    private SimpleCursorAdapter spinAdapter;
    private ViewSwitcher catSwitcher;
    boolean isArrow = false;
    ListView list;
    EditText cat_ed;


    public CatFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View catInflater = inflater.inflate(R.layout.fragment_cat, container, false);

        catSwitcher = (ViewSwitcher) catInflater.findViewById(R.id.catSwitcher);
        cat_ed = (EditText) catInflater.findViewById(R.id.cat_edit);
        Button btnSubmit = (Button) catInflater.findViewById(R.id.btnCatSubmit);
        final Button btnDelete = (Button) catInflater.findViewById(R.id.btnCatDelete);
        list = (ListView) catInflater.findViewById(R.id.cat_listV);
        list.setClickable(true);

        Cursor c = getActivity().getContentResolver().query(CONTENT_URI_cat, projection_cat, null, null, null);
        if(c!=null) {
            // columns to use
            String[] columns = new String[] {
                    KEY_ROWID,
                    KEY_CATNAME
            };

            // views to bind data to
            int[] to = new int[] {
                    R.id.cat_ID,
                    R.id.category_name
            };

            // create adapter
            dataAdapter = new SimpleCursorAdapter(
                    getActivity(), R.layout.cat_info,
                    c,
                    columns,
                    to,
                    0);

            list.setAdapter(dataAdapter);
        }

        final FloatingActionButton fab = (FloatingActionButton) catInflater.findViewById(R.id.catFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catSwitcher.showNext();
                if(!isArrow) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow));
                    isArrow = true;
                }
                else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plus));
                    cat_ed.setText("");
                    ID="";
                    btnDelete.setVisibility(View.INVISIBLE);
                    isArrow = false;
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                String catid = cat_ed.getText().toString();
                String[] args = new String[]{catid};

                values = createData(cv_cat, args);
                if(ID.equals("")){
                    Uri rt_uri = getActivity().getContentResolver().insert(CONTENT_URI_cat, values);
                }
                else {
                    getActivity().getContentResolver().update(CONTENT_URI_cat, values, "_id = " + ID, null);
                    ID = "";
                }

                if(!isArrow) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow));
                    isArrow = true;
                }
                else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plus));
                    cat_ed.setText("");
                    ID="";
                    btnDelete.setVisibility(View.INVISIBLE);
                    isArrow = false;
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_check_book, new CatFragment())
                        .commit();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                String arg = ID;
                String query = "SELECT * from CONTENT_URI_trans1 where _id=" + arg;
                Cursor cursor = getActivity().getContentResolver().query(CONTENT_URI_trans1, null, "category._id = " +ID, null, null);

                if(!cursor.isAfterLast()) {
                    Toast.makeText(getActivity(), "Category in use: could not delete.", Toast.LENGTH_SHORT).show();
                }
                else {
                    String[] aString = new String[] {};
                    if(!ID.equals("")){
                        getActivity().getContentResolver().delete(CONTENT_URI_cat, "_id = " + ID, aString);
                        ID = "";
                    }
                }



                if(!isArrow) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow));
                    isArrow = true;
                }
                else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plus));
                    cat_ed.setText("");
                    ID="";
                    btnDelete.setVisibility(View.INVISIBLE);
                    isArrow = false;
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_check_book, new CatFragment())
                        .commit();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) list.getItemAtPosition(position);

                ID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROWID));
                String catID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATNAME));
                cat_ed.setText(catID);
                btnDelete.setVisibility(View.VISIBLE);
                catSwitcher.showNext();
                if(!isArrow) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow));
                    isArrow = true;
                }
                else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plus));
                    cat_ed.setText("");
                    ID="";
                    btnDelete.setVisibility(View.INVISIBLE);
                    isArrow = false;
                }
            }
        });

        return catInflater;
    }

    public ContentValues createData(String[] key, String[] data) {
        ContentValues cv = new ContentValues();
        for (int i=0; i<key.length; i++) {
            cv.put(key[i], data[i]);
        }
        return cv;
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
}
