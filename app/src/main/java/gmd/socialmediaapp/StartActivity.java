package gmd.socialmediaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StartActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private Button signIn;
    private ArrayList<String> names;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        names = getIntent().getStringArrayListExtra("names");
        signIn = findViewById(R.id.signInButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        final Animation a = AnimationUtils.loadAnimation(this, R.anim.fui_slide_in_right);
        a.reset();
        final TextView rText = (TextView) findViewById(R.id.textView);
        rText.startAnimation(a);

        rText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rText.startAnimation(a);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                openApp();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        register(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    public void login() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        );
    }

    public void openApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addUser() {
        User user = new User(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Timestamp.now(), 0);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(user);
    }

    public void register(String userName) {
        boolean exists = false;

        for (String name : names) {
            if (userName.equals(name)) {
                exists = true;
            }
        }
        if (!exists) {
            addUser();
        }
    }
}
