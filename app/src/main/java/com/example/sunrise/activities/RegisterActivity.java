package com.example.sunrise.activities;

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

import com.example.sunrise.R;
import com.example.sunrise.constants.Icon;
import com.example.sunrise.models.Category;
import com.example.sunrise.models.Tag;
import com.example.sunrise.models.User;
import com.example.sunrise.models.UserSettings;
import com.example.sunrise.services.CategoryService;
import com.example.sunrise.services.MyDayService;
import com.example.sunrise.services.TagService;
import com.example.sunrise.services.UserService;
import com.example.sunrise.services.UserSettingsService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextNickname;
    Button registerBtn;
    TextView loginScreenLink;
    private FirebaseAuth mAuth;
    private String defaultTagId;
    private static final String TAG = "RegisterActivity";
    private UserService userService;

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
        userService = new UserService(); // Initialize UserService

        // Getting views
        editTextNickname = findViewById(R.id.user_nickname);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        registerBtn = findViewById(R.id.register_btn);
        loginScreenLink = findViewById(R.id.login_now);

        loginScreenLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        registerBtn.setOnClickListener(v -> {
            String email, password, nickname;

            nickname = String.valueOf(editTextNickname.getText()).trim();
            email = String.valueOf(editTextEmail.getText()).trim();
            password = String.valueOf(editTextPassword.getText()).trim();

            // Validation
            if (TextUtils.isEmpty(nickname)) {
                Toast.makeText(this, "Enter nickname", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // User creation logic
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(RegisterActivity.this, "Account created!",
                                        Toast.LENGTH_SHORT).show();

                                // Get newly created user and fill it with default data
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Create default tag, category and user
                                createDefaultTag(user, nickname);
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

    /**
     * Creates a default tag for the user and saves it to Firebase.
     * Once the tag is saved successfully, it proceeds to create the default category and user profile.
     *
     * @param user The FirebaseUser object representing the newly registered user.
     */
    private void createDefaultTag(FirebaseUser user, String nickname) {
        TagService tagService = new TagService();

        // Create a default tag
        Tag defaultTag = new Tag("personal", -13108, user.getUid());

        tagService.saveTag(defaultTag, task -> {
            if (task.isSuccessful()) {
                // after tag saved to firebase save it as class property
                defaultTagId = defaultTag.getTagId();

                // Create default category and user profile
                createMyDay(user.getUid());
                createDefaultCategory(user);
                createUserProfile(user, nickname);
            } else {
                Log.e(TAG, "Failed to save default tag: " + task.getException());
            }
        });
    }

    /**
     * method to create MyDay Object for user
     */
    private void createMyDay(String userId) {
        MyDayService myDayService = new MyDayService();

        myDayService.createMyDay(userId);
    }

    /**
     * Method to create a default category and save it to UserSettings
     */
    private void createDefaultCategory(FirebaseUser user) {
        CategoryService categoryService = new CategoryService();

        // Create a default category
        Category defaultCategory = new Category("Personal", -13057, Icon.FLOWER.toString(), defaultTagId, user.getUid());

        categoryService.saveCategory(defaultCategory, task -> {
            if (task.isSuccessful()) {
                saveDefaultCategoryToUserSettings(defaultCategory, user.getUid());
                Log.d(TAG, "Default category created successfully.");
            } else {
                Log.e(TAG, "Failed to save default category: " + task.getException());
                showToast("Failed to create account. Please try again later.");
            }
        });
    }

    /**
     * Method to create a default user profile using UserService
     */
    private void createUserProfile(FirebaseUser user, String nickname) {
        String defaultProfilePhoto = "https://firebasestorage.googleapis.com/v0/b/sunrise-1a7c7.appspot.com/o/default_funny_avater.png?alt=media&token=20c96f68-3551-4db7-80d4-86a79370729b";

        // Update properties in Firebase auth
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .setPhotoUri(Uri.parse(defaultProfilePhoto))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save user profile data in my own database
                        User newUser = new User(user.getUid(), nickname, user.getEmail(), defaultProfilePhoto);
                        userService.createUser(newUser);

                        // Proceed to MainActivity after profile update
                        proceedToMainActivity();
                    } else {
                        Log.e(TAG, "Failed to update user profile: " + task.getException());
                        showToast("Failed to create account. Please try again later.");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveDefaultCategoryToUserSettings(Category defaultCategory, String userId) {
        // Initialize UserSettings service
        UserSettingsService userSettingsService = new UserSettingsService();

        // Retrieve the category Id after saving
        String defaultCategoryId = defaultCategory.getCategoryId();

        // Create a UserSettings object with the default category Id
        UserSettings userSettings = new UserSettings(userId, defaultCategoryId);

        // Save the UserSettings object
        userSettingsService.createUserSettings(userSettings, task -> {
            if (task.isSuccessful()) {
                // All tasks are complete, proceed to MainActivity
                proceedToMainActivity();
            } else {
                Log.e(TAG, "Failed to save user settings: " + task.getException());
                showToast("Failed to create account. Please try again later.");
            }
        });
    }

    /**
     * Method to proceed to MainActivity after all necessary setup tasks are completed.
     */
    private void proceedToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
