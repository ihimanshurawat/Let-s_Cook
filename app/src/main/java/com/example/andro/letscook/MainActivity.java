package com.example.andro.letscook;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
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
        import android.view.HapticFeedbackConstants;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;
        import com.example.andro.letscook.Support.FirebaseAuthUtility;
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
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.twitter.sdk.android.core.Callback;
        import com.twitter.sdk.android.core.Result;
        import com.twitter.sdk.android.core.Twitter;
        import com.twitter.sdk.android.core.TwitterException;
        import com.twitter.sdk.android.core.TwitterSession;
        import com.twitter.sdk.android.core.identity.TwitterLoginButton;

        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{


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

        //Network State Broadcast Receiver Reference
        private NetworkStateReceiver networkStateReceiver;

        //FirebaseAuthStateListener
        FirebaseAuth.AuthStateListener loginStateListener;

        @Override
        protected void onStart() {
            super.onStart();
            //Attaching AuthStateListener
            mAuth.addAuthStateListener(loginStateListener);
//            networkStateReceiver = new NetworkStateReceiver(this);
//            networkStateReceiver.addListener(this);
//            this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(loginStateListener);
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);

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


            networkStateReceiver = new NetworkStateReceiver(this);
            networkStateReceiver.addListener(this);
            this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));



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


    @Override
    public void onNetworkAvailable() {

        twitterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else
                    vibrator.vibrate(50);

                //To Intentionally Click TwitterLoginButton
                twitterLoginButton.performClick();

            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                else
                    vibrator.vibrate(50);
                signIn();
            }
        });
    }

    @Override
    public void onNetworkUnavailable() {
        Toast.makeText(this,"Connect to the Internet for Better Experience",Toast.LENGTH_LONG).show();

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                else
                    vibrator.vibrate(50);
                Snackbar.make(googleSignIn,"No Internet Detected",Snackbar.LENGTH_LONG).show();
            }
        });
        twitterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                else
                    vibrator.vibrate(50);
                Snackbar.make(googleSignIn,"No Internet Detected",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void updateUI(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(MainActivity.this,AllRecipes.class));
        }
    }


}


class NetworkStateReceiver extends BroadcastReceiver {

    private ConnectivityManager mManager;
    private List<NetworkStateReceiverListener> mListeners;
    private boolean mConnected;

    public NetworkStateReceiver(Context context) {
        mListeners = new ArrayList<NetworkStateReceiverListener>();
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkStateChanged();
    }

    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;

        if (checkStateChanged()) notifyStateToAll();
    }

    private boolean checkStateChanged() {
        boolean prev = mConnected;
        NetworkInfo activeNetwork = mManager.getActiveNetworkInfo();
        mConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return prev != mConnected;
    }

    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : mListeners) {
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if (listener != null) {
            if (mConnected) listener.onNetworkAvailable();
            else listener.onNetworkUnavailable();
        }
    }

    public void addListener(NetworkStateReceiverListener l) {
        mListeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkStateReceiverListener l) {
        mListeners.remove(l);
    }

    public interface NetworkStateReceiverListener {
         void onNetworkAvailable();
         void onNetworkUnavailable();
    }
}

