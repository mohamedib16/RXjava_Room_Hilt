package com.devm7mdibrahim.rxjava_room_hilt.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.devm7mdibrahim.rxjava_room_hilt.R;
import com.devm7mdibrahim.rxjava_room_hilt.data.local.entity.User;
import com.devm7mdibrahim.rxjava_room_hilt.databinding.ActivityLoginBinding;
import com.devm7mdibrahim.rxjava_room_hilt.ui.login.viewModel.LoginViewModel;
import com.devm7mdibrahim.rxjava_room_hilt.ui.main.activity.MainActivity;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityLoginBinding loginBinding;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        initViewModel();
        loginBinding.loginButton.setOnClickListener(this);
    }

    private void initViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    public void onClick(View v) {
        User userData = getUserData();
        if (userData != null) {
            loginViewModel.insertUser(userData);
            loginViewModel.getRequestState().observe(this, requestState -> {
                switch (requestState.status) {
                    case LOADING: {
                        loginBinding.loginProgressBar.setVisibility(View.VISIBLE);
                        break;
                    }
                    case SUCCESS: {
                        loginBinding.loginProgressBar.setVisibility(View.GONE);
                        Toast.makeText(this, requestState.data, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        break;
                    }
                    case ERROR: {
                        loginBinding.loginProgressBar.setVisibility(View.GONE);
                        Toast.makeText(this, requestState.message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            });
        }
    }

    private User getUserData() {
        String userName = Objects.requireNonNull(loginBinding.loginUserNameTextInputLayout.getEditText()).getText().toString();
        String userAge = Objects.requireNonNull(loginBinding.loginUserAgeTextInputLayout.getEditText()).getText().toString();
        String userJobTitle = Objects.requireNonNull(loginBinding.loginJobTitleTextInputLayout.getEditText()).getText().toString();

        if (userName.trim().isEmpty() ||
                userAge.trim().isEmpty() ||
                userJobTitle.trim().isEmpty()) {
            Toast.makeText(this, "Empty Field! All data are required!", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            int radioId = loginBinding.loginGenderRadioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(radioId);
            String userGender = radioButton.getText().toString();

            return new User(userName, userAge, userJobTitle, userGender);
        }
    }
}