package com.example.andro.letscook;

        import android.content.Context;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Build;
        import android.os.VibrationEffect;
        import android.os.Vibrator;
        import android.support.annotation.NonNull;
        import android.support.design.widget.BaseTransientBottomBar;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import com.example.andro.letscook.support.FirebaseAuthUtility;
        import com.google.android.gms.auth.api.Auth;
        import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
        import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
        import com.google.android.gms.auth.api.signin.GoogleSignInResult;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthCredential;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.GoogleAuthProvider;
        import com.google.firebase.auth.TwitterAuthProvider;
        import com.twitter.sdk.android.core.Callback;
        import com.twitter.sdk.android.core.Result;
        import com.twitter.sdk.android.core.Twitter;
        import com.twitter.sdk.android.core.TwitterException;
        import com.twitter.sdk.android.core.TwitterSession;
        import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends AppCompatActivity {

        //Setting Up Google SignIn
        public static final int RC_SIGN_IN=1;
        GoogleApiClient mGoogleApiClient;

        //Button References
        private Button googleSignIn,twitterSignIn,facebookSignIn;
        //References of the Buttons that are Hidden
        private TwitterLoginButton twitterLoginButton;
        //LoginButton facebookLoginButton;

        //FirebaseAuth Reference
        private FirebaseAuth mAuth;

        private FirebaseUser user;

        //Vibrator Reference
        private Vibrator vibrator;

        //FirebaseAuthStateListener
        FirebaseAuth.AuthStateListener loginStateListener;

        @Override
        protected void onStart() {
            super.onStart();
            //Attaching AuthStateListener
            mAuth.addAuthStateListener(loginStateListener);

        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(loginStateListener);

    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //Initialize Twitter
            Twitter.initialize(this);
            //Buttons
            googleSignIn= findViewById(R.id.google_sign_in);
            twitterSignIn= findViewById(R.id.twitter_sign_in);
            //HiddenButtons
            twitterLoginButton= findViewById(R.id.twitter);

            //Initialize FireBase
            mAuth= FirebaseAuthUtility.getAuth();

            user=mAuth.getCurrentUser();



            twitterLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    handleTwitterSession(result.data);
                }

                @Override
                public void failure(TwitterException exception) {
                    Snackbar.make(twitterLoginButton,"Cannot Establish Connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            });


            vibrator=(Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Snackbar.make(twitterLoginButton,"Connection Failed", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    } )
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            loginStateListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    updateUI(firebaseAuth.getCurrentUser());
                }
            };

            twitterSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else
                        vibrator.vibrate(50);

                    ConnectivityManager connectivityManager
                            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                    if(activeNetworkInfo!=null&&activeNetworkInfo.isConnected()){
                        //To Intentionally Click TwitterLoginButton
                        twitterLoginButton.performClick();
                    }else{
                        Snackbar.make(twitterSignIn,"Internet Required",BaseTransientBottomBar.LENGTH_SHORT).show();
                    }

                }
            });

            googleSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else
                        vibrator.vibrate(50);

                    ConnectivityManager connectivityManager
                            = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if(activeNetworkInfo!=null&&activeNetworkInfo.isConnected()){
                        signIn();
                    }else{
                        Snackbar.make(googleSignIn,"Internet Required",BaseTransientBottomBar.LENGTH_SHORT).show();
                    }

                }
            });

        }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Snackbar.make(twitterLoginButton,"Cannot Establish Connection", BaseTransientBottomBar.LENGTH_SHORT).show();

            }
        }
        twitterLoginButton.onActivityResult(requestCode, resultCode,data);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            user= mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            Snackbar.make(googleSignIn,"Authentication Failed",BaseTransientBottomBar.LENGTH_SHORT).show();

                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            }

                        // ...
                    }
                });
    }

    public void handleTwitterSession(TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Snackbar.make(googleSignIn, "Authentication Failed", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(MainActivity.this,AllRecipes.class));
        }
    }


}

