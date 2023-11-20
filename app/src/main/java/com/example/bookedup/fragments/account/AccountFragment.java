package com.example.bookedup.fragments.account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.bookedup.R;


public class AccountFragment extends Fragment {

    ImageView profilePic;
    EditText firstNameInput;
    EditText phoneInput;
    EditText addressInput;
    EditText lastNameInput;
    EditText passwordInput;
    Button updateProfileBtn;
    Button deleteProfileBtn;
    ProgressBar progressBar;

    public AccountFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        profilePic = view.findViewById(R.id.profile_image_view);

        firstNameInput = view.findViewById(R.id.profile_first_name);
        lastNameInput = view.findViewById(R.id.profile_last_name);
        phoneInput = view.findViewById(R.id.profile_phone);
        addressInput = view.findViewById(R.id.profile_address);
        passwordInput = view.findViewById(R.id.profile_password);

        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        deleteProfileBtn = view.findViewById(R.id.profle_delete_btn);

        progressBar = view.findViewById(R.id.profile_progress_bar);

        //getUserData();
//
//        updateProfileBtn.setOnClickListener((v -> {
//            updateBtnClick();
//        }));
//
//        logoutBtn.setOnClickListener((v)->{
//            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        FirebaseUtil.logout();
//                        Intent intent = new Intent(getContext(),SplashActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                }
//            });
//
//
//
//        });
//
//        profilePic.setOnClickListener((v)->{
//            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
//                    .createIntent(new Function1<Intent, Unit>() {
//                        @Override
//                        public Unit invoke(Intent intent) {
//                            imagePickLauncher.launch(intent);
//                            return null;
//                        }
//                    });
//        });

        return view;
    }


}