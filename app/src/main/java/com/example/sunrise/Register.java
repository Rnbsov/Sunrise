package com.example.sunrise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sunrise.constants.Icon;
import com.example.sunrise.models.Category;
import com.example.sunrise.models.Tag;
import com.example.sunrise.models.UserSettings;
import com.example.sunrise.services.CategoryService;
import com.example.sunrise.services.TagService;
import com.example.sunrise.services.UserSettingsService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button registerBtn;
    TextView loginScreenLink;
    private FirebaseAuth mAuth;
    private String defaultTagId;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth object
        mAuth = FirebaseAuth.getInstance();

        // Getting views
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        registerBtn = findViewById(R.id.register_btn);
        loginScreenLink = findViewById(R.id.login_now);

        loginScreenLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        registerBtn.setOnClickListener(v -> {
            String email, password;

            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(Register.this, "Account created!",
                                        Toast.LENGTH_SHORT).show();

                                // Get newly created user and fill it with default data
                                FirebaseUser user = mAuth.getCurrentUser();
                                createDefaultUserProfile(user);

                                // Create default category and save to UserSettings
                                createDefaultCategory(user);

                                // Send user to MainActivity
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                // If sign in fails, display a message to the user.
                                String errorMessage = task.getException().getLocalizedMessage();
                                showToast(errorMessage != null ? errorMessage : getString(R.string.authentication_failed));
                            }
                        }
                    });
        });
    }

    private void createDefaultTag(FirebaseUser user) {
        TagService tagService = new TagService();

        // Create a default tag
        Tag defaultTag = new Tag("personal", -13108, user.getUid());

        tagService.saveTag(defaultTag, task -> {
            // after tag saved to firebase save it as class property
            defaultTagId = defaultTag.getTagId();
        });
    }

    /**
     * this method creates default category and saves it in UserSettings
     */
    private void createDefaultCategory(FirebaseUser user) {
        CategoryService categoryService = new CategoryService();
        UserSettingsService userSettingsService = new UserSettingsService();

        // Create a default category
        Category defaultCategory = new Category("Personal", -13057, Icon.FLOWER.toString(), defaultTagId, user.getUid());

        // Save the default category to Firebase
        categoryService.saveCategory(defaultCategory, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Retrieve the category Id after saving
                    String defaultCategoryId = defaultCategory.getCategoryId();

                    // Create a UserSettings object with the default category Id
                    UserSettings userSettings = new UserSettings(user.getUid(), defaultCategoryId);

                    // Save the UserSettings object
                    userSettingsService.createUserSettings(userSettings);
                } else {
                    Log.e(TAG, "Failed to save default category: " + task.getException());
                    showToast("Failed to create account. Please try again later.");
                }
            }
        });
    }

    /**
     * Sets default profile information for a newly registered user in the app.
     * This method sets a default display name and avatar for the user.
     *
     * @param user The FirebaseUser object representing the newly registered user.
     */
    private void createDefaultUserProfile(FirebaseUser user) {
        // Create a UserProfileChangeRequest to set default display name and avatar
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Stranger")
                .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/sunrise-1a7c7.appspot.com/o/default_funny_avater.png?alt=media&token=20c96f68-3551-4db7-80d4-86a79370729b"))
                .build();

        // Update the user's profile with the default information
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}