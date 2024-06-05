package com.example.sunrise.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.activities.LoginActivity;
import android.Manifest;
import com.example.sunrise.R;
import com.example.sunrise.adapters.NavigationAdapter;
import com.example.sunrise.models.User;
import com.example.sunrise.navigation.ProfileNavigationRoutes;
import com.example.sunrise.services.UserService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ShapeableImageView profilePicture;
    private TextView usernameTextView;
    private TextView userEmailTextView;
    private FirebaseAuth mAuth;
    private final UserService userService = new UserService();

    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup appbar menu options
        setupMenu();

        // Initialize Firebase Auth object
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        profilePicture = view.findViewById(R.id.profile_picture);
        usernameTextView = view.findViewById(R.id.username);
        userEmailTextView = view.findViewById(R.id.user_email);

        // Setup profile data
        setupProfileData();

        // Setup navigation routes
        setupNavigationRoutes(view);

        // Initialize some contracts
        takePictureLauncher = registerForTakePictureContract();
        pickImageLauncher = registerForPickImageContract();
        requestCameraPermissionLauncher = registerForCameraPermissionContract();

        // Set profile picture click listener
        profilePicture.setOnClickListener(v -> onAvatarClicked());

        // Set username click listener to show the update nickname dialog
        usernameTextView.setOnClickListener(v -> showUpdateNicknameBottomSheet());
    }

    /**
     * Sets up the profile data by retrieving information from Firebase Auth and setting it to the views.
     */
    private void setupProfileData() {
        Uri photoUrl = mAuth.getCurrentUser().getPhotoUrl();
        String username = mAuth.getCurrentUser().getDisplayName();
        String email = mAuth.getCurrentUser().getEmail();

        // Set username and email
        usernameTextView.setText(username);
        userEmailTextView.setText(email);

        // Load profile picture using Picasso
        Picasso.get()
                .load(photoUrl)
                .into(profilePicture);
    }

    /**
     * Sets up the navigation routes in the RecyclerView.
     */
    private void setupNavigationRoutes(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.navigation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true); // Enable some optimization of recyclerView

        List<ProfileNavigationRoutes> navigationItems = Arrays.asList(ProfileNavigationRoutes.values());
        NavigationAdapter<ProfileNavigationRoutes> adapter = new NavigationAdapter<>(navigationItems, item -> {
            switch (item) {
                case Feedback -> handleFeedbackClick();
                case Settings -> navigateToSettings();
                case About -> navigateToAboutFragment();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Handles the click event for the feedback route.
     */
    private void handleFeedbackClick() {
        // Create an intent to open email application
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:contatornbsov@gmail.com"));
        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to navigate to Settings
     */
    private void navigateToSettings() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_page_profile_to_settingsFragment);
    }

    /**
     * Method to navigate to AboutFragment
     */
    private void navigateToAboutFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_page_profile_to_aboutFragment);
    }

    /**
     * Sets up the menu options for the profile fragment, such as sign out
     */
    private void setupMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.profile_fragment_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.logout) {
                    showSignOutConfirmationDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    /**
     * Shows an AlertDialog asking the user if they really want to sign out
     */
    private void showSignOutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_title_sign_out))
                .setMessage(getString(R.string.dialog_message_sign_out))
                .setPositiveButton(getString(R.string.button_sign_out), (dialog, which) -> performSignOut())
                .setNegativeButton(getString(R.string.button_cancel), null)
                .show();
    }

    /**
     * Performs the sign-out process
     */
    private void performSignOut() {
        // handle logout
        FirebaseAuth.getInstance().signOut();

        // Send user to Login activity
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);

        // Finish the parent of fragments which is MainActivity
        if (getActivity() != null) {
            getActivity().finish();
        }

        // Notify user about successful sign out
        Toast.makeText(getContext(), getString(R.string.sign_out_success_message), Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the click event for the avatar. Displays a dialog with options to take a photo or choose from the gallery.
     * Launches the appropriate activity based on the user's choice.
     */
    private void onAvatarClicked() {
        String[] options = new String[]{"Take photo", "Choose from gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose your avatar");

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                    } else {
                        launchCamera();
                    }
                    break;
                case 1:
                    pickImageLauncher.launch("image/*");
                    break;
                default:
                    // Cancel
                    break;
            }
        });

        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * Launches the camera to take a picture.
     */
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(takePictureIntent);
    }

    /**
     * Registers the contract for taking a picture. Defines the behavior upon receiving the result.
     * @return The launcher for taking a picture.
     */
    private ActivityResultLauncher<Intent> registerForTakePictureContract() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        if (bitmap != null) {
                            Toast.makeText(requireContext(), "Please wait, till image uploaded", Toast.LENGTH_SHORT).show();
                            profilePicture.setImageBitmap(bitmap);
                            saveAvatarToFB(bitmap);
                        }
                    }
                }
        );
    }

    /**
     * Registers the contract for picking an image from the gallery. Defines the behavior upon receiving the result.
     * @return The launcher for picking an image.
     */
    private ActivityResultLauncher<String> registerForPickImageContract() {
        return registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        try {
                            InputStream is = getContext().getContentResolver().openInputStream(result);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            if (bitmap != null) {
                                Toast.makeText(requireContext(), "Please wait, till image uploaded", Toast.LENGTH_SHORT).show();
                                profilePicture.setImageBitmap(bitmap);
                                saveAvatarToFB(bitmap);
                            }
                        } catch (Exception ex) {
                            Log.e("ProfileFragment", Objects.requireNonNull(ex.getLocalizedMessage()));
                        }
                    }
                }
        );
    }

    /**
     * Registers the contract for requesting camera permission.
     * If the permission is granted, it will proceed to launch the camera.
     * If the permission is denied, it will show a toast message.
     *
     * @return The launcher for requesting camera permission.
     */
    private ActivityResultLauncher<String> registerForCameraPermissionContract() {
        return registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Saves the avatar image to Firebase Storage after converting it to a byte array.
     * @param bitmap The bitmap representation of the avatar image.
     */
    private void saveAvatarToFB(Bitmap bitmap) {
        String userUID = mAuth.getCurrentUser().getUid();
        String storagePath = "avatars/" + userUID + "_avatar";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(storagePath);

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Create metadata with cache control headers
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCacheControl("public, max-age=7776000") // Cache avatar for three months
                .build();

        // Upload the byte array to Firebase Storage with metadata
        UploadTask uploadTask = storageReference.putBytes(byteArray, metadata);

        // Upload the byte array to Firebase Storage
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> updateProfilePicture(uri)))
                .addOnFailureListener(e -> {
                    System.out.println(e.getLocalizedMessage());
                    Toast.makeText(requireContext(), "Failed to upload image to Firebase Storage.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the profile picture of the current user with the provided URI.
     * @param uri The URI of the updated profile picture.
     */
    private void updateProfilePicture(Uri uri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After it was successfully saved in firebase cloud and auth,
                        // save this profile picture to User real-time db object
                        userService.updateProfilePictureUri(currentUser.getUid(), uri.toString());

                        Toast.makeText(requireContext(), "Profile picture updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Shows a bottom sheet to update the nickname.
     */
    private void showUpdateNicknameBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                R.layout.edit_name_bottom_sheet,
                (ViewGroup) getView(),
                false
        );

        EditText nicknameEditText = bottomSheetView.findViewById(R.id.Nickname);
        Button updateButton = bottomSheetView.findViewById(R.id.update_btn);

        // Set current nickname to edit nickname editfield
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentDisplayName = currentUser.getDisplayName();
        nicknameEditText.setText(currentDisplayName);

        updateButton.setOnClickListener(v -> {
            String newNickname = nicknameEditText.getText().toString().trim();
            if (!newNickname.isEmpty()) {
                updateNickname(newNickname);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Nickname cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     * Updates the user's nickname in Firebase Auth.
     *
     * @param newNickname the new nickname to be updated
     */
    private void updateNickname(String newNickname) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newNickname)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Nickname updated successfully
                        usernameTextView.setText(newNickname);
                        Toast.makeText(requireContext(), "Nickname updated successfully.", Toast.LENGTH_SHORT).show();

                        // Update user in the database
                        User updatedUser = new User(user.getUid(), newNickname, user.getEmail(), user.getPhotoUrl().toString());
                        userService.updateUser(updatedUser);
                    } else {
                        // Handle the error
                        Toast.makeText(requireContext(), "Failed to update nickname.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
