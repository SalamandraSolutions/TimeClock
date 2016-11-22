package layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.AndroidFont;
import com.onbarcode.barcode.android.Code128;
import com.onbarcode.barcode.android.IBarcode;

import za.co.salamandra.timeclock.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link barcodeGenerator.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link barcodeGenerator#newInstance} factory method to
 * create an instance of this fragment.
 */
public class barcodeGenerator extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //Create UI Variables
    private ImageView imgBarcodeRef;
    private EditText txtInputRef;
    private Button btnGenerateRef;
    //Create oeprating variables
    private String barcodeText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public barcodeGenerator() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment barcodeGenerator.
     */
    // TODO: Rename and change types and number of parameters
    public static barcodeGenerator newInstance(String param1, String param2) {
        barcodeGenerator fragment = new barcodeGenerator();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_barcode_generator, container, false);
        //UI Elements
        imgBarcodeRef = (ImageView) v.findViewById(R.id.imgBarcode);
        txtInputRef = (EditText) v.findViewById(R.id.txtInput);
        btnGenerateRef = (Button) v.findViewById(R.id.btnGenerate);
        //action
        btnGenerateRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });
        //return
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Check the users input
    public void Check() {
        try {
            String strText = txtInputRef.getText().toString();
            if (!strText.equals("")) {
                if (strText.length() < 13) {
                    if (!strText.contains(" ")) {
                        barcodeText = txtInputRef.getText().toString().toUpperCase();
                        GenerateBarcode();
                    } else {
                        Toast.makeText(getContext(), "Must not contain any spaces", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Input is too long", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Input is empty, please input some text", Toast.LENGTH_LONG).show();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void GenerateBarcode() {
        try {
            Code128 barcode = new Code128();
            barcode.setData(barcodeText);
            barcode.setProcessTilde(false);
            //Unit of measure, pixel, cm, or inch
            barcode.setUom(IBarcode.UOM_PIXEL);
            //Barcode bar module width (X) in pixel
            barcode.setX(1f);
            //Barcode bar module height (Y) in pixel
            barcode.setY(75f);
            //Barcode image margins
            barcode.setLeftMargin(10f);
            barcode.setRightMargin(10f);
            barcode.setTopMargin(10f);
            barcode.setBottomMargin(10f);
            //Barcode image resolution in dpi
            barcode.setResolution(600);
            //Display barcode encoding data below the barcode
            barcode.setShowText(true);
            //Barcode encoding data font style
            barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 12));
            //Space between barcode and barcode encoding data
            barcode.setTextMargin(6);
            barcode.setTextColor(AndroidColor.black);
            //Barcode bar color and background color in Android device
            barcode.setForeColor(AndroidColor.black);
            //barcode.setBackColor(AndroidColor.white);
            //Specify your barcode drawing area
            RectF bounds = new RectF(0, 0, 0, 0);
            //Specify your barcode drawing area
            Bitmap bitmap = Bitmap.createBitmap(190, 190, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            barcode.drawBarcode(canvas, bounds);
            imgBarcodeRef.setImageBitmap(bitmap);

        } catch (Exception err) {
            err.printStackTrace();
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
