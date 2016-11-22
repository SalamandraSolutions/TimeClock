package layout;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import network.postRequest;
import za.co.salamandra.timeclock.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link addEmployee.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link addEmployee#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addEmployee extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView profileImage;
    private EditText firstName, lastName, empCellNum, empEmailAddress, empID;
    private Button btnCreate;

    private String image_file;

    private View v;

    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private Uri picUri;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public addEmployee() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment addEmployee.
     */
    // TODO: Rename and change types and number of parameters
    public static addEmployee newInstance(String param1, String param2) {
        addEmployee fragment = new addEmployee();
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
        v = inflater.inflate(R.layout.fragment_add_employee, container, false);

        //get UI elements
        profileImage = (ImageView) v.findViewById(R.id.imageProfile);
        firstName = (EditText) v.findViewById(R.id.first_name);
        lastName = (EditText) v.findViewById(R.id.last_name);
        empEmailAddress = (EditText) v.findViewById(R.id.employee_email_address);
        empCellNum = (EditText) v.findViewById(R.id.employee_cell_num);
        empID = (EditText) v.findViewById(R.id.employee_id);
        btnCreate = (Button) v.findViewById(R.id.email_create_employee);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.imageProfile) {
                    try {
                        //Intent will handle the capture of image
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //The return data will be handled by onActivityResults
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                    } catch (ActivityNotFoundException anfe) {
                        Toast.makeText(getContext(), "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
                        Log.v("MyActivity", anfe.getMessage());
                    }
                    //setImage_file();
                }
            }
        });

        //create employee
         btnCreate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 network.postRequest postRequest = new postRequest();
                 Toast.makeText(getContext(), "Adding employee, please wait...", Toast.LENGTH_SHORT).show();
                 boolean bResponse = postRequest.postEmployee(convertImage(), firstName.getText().toString(), lastName.getText().toString(),
                         empCellNum.getText().toString(), empEmailAddress.getText().toString(), empID.getText().toString());
                 if(bResponse) {
                     Toast.makeText(getContext(), "Employee has been added successfully!", Toast.LENGTH_LONG).show();
                     //reset form
                     profileImage.setImageResource(R.drawable.account_circle);
                     firstName.setText("");
                     lastName.setText("");
                     empEmailAddress.setText("");
                     empCellNum.setText("");
                     empID.setText("");
                 } else {
                     Toast.makeText(getContext(), "FAILED: Creating a new Employee", Toast.LENGTH_LONG).show();
                 }

             }
         });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                // get the Uri for the captured image
                picUri = data.getData();
                performCrop(data);
            }
            // user is returning from cropping the image
            else if (requestCode == CROP_PIC) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                ImageView picView = (ImageView) v.findViewById(R.id.imageProfile);
                picView.setImageBitmap(thePic);
            }
        }
    }

    private void performCrop(Intent data) {
        //take care for exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1.5);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(getContext(), "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
            Bitmap thePic = (Bitmap) data.getExtras().get("data");
            ImageView picView = (ImageView) v.findViewById(R.id.imageProfile);
            picView.setImageBitmap(thePic);
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

    public String convertImage() {
        Bitmap bitmap = ((BitmapDrawable)profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b,Base64.DEFAULT);
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

    public void setImage_file() {

        Bitmap bitmap = ((BitmapDrawable)profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();
        String file = Base64.encodeToString(data, 0);
        this.image_file = file;
    }



}
