package com.example.andro.letscook;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Build;
        import android.os.Parcel;
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

        import com.google.android.gms.auth.api.Auth;
        import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
        import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
        import com.google.android.gms.auth.api.signin.GoogleSignInResult;
        import com.google.android.gms.auth.api.signin.SignInAccount;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.SignInButton;
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

        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{


//Setting Up Google SignIn
        public static final int RC_SIGN_IN=1;
        GoogleApiClient mGoogleApiClient;

        //Button References
        Button googleSignIn,twitterSignIn;
        //References of the Buttons that are Hidden
        TwitterLoginButton twitterLoginButton;
        //FirebaseAuth Reference
        private FirebaseAuth mAuth;

        private FirebaseUser user;

        //Vibrator Reference
        Vibrator vibrator;

        //Network State Broadcast Receiver Reference
        private NetworkStateReceiver networkStateReceiver;

        //FirebaseAuthStateListener
        FirebaseAuth.AuthStateListener loginStateListener;



        @Override
        protected void onStart() {
            super.onStart();

            mAuth.addAuthStateListener(loginStateListener);



        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            googleSignIn = (Button)findViewById(R.id.google_sign_in);
            twitterSignIn=(Button)findViewById(R.id.twitter_sign_in);
            //HiddenButtons
            twitterLoginButton=(TwitterLoginButton)findViewById(R.id.twitter);

            //Initialize Firebase
            mAuth=FirebaseAuth.getInstance();

            user=mAuth.getCurrentUser();


            networkStateReceiver = new NetworkStateReceiver(this);
            networkStateReceiver.addListener(this);
            this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));


            twitterLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    Log.d("TAG", "twitterLogin:success" + result);
                    handleTwitterSession(result.data);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.w("TAG", "twitterLogin:failure", exception);

                    Snackbar.make(twitterLoginButton,"Cannot Establish Connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            });


            vibrator=(Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Snackbar.make(twitterLoginButton,"Connection Failed", BaseTransientBottomBar.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this,"Connection Failed", Toast.LENGTH_SHORT).show();
                        }
                    } /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Snackbar.make(twitterLoginButton,"Cannot Establish Connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                // ...
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            user= mAuth.getCurrentUser();
                            Intent i = new Intent(MainActivity.this,AllRecipes.class);
                            i.putExtra("Name",user.getDisplayName()+"");
                            i.putExtra("Email",user.getEmail());
                            i.putExtra("Profile",user.getPhotoUrl()+"");
                            startActivity(i);
                            Log.i("Email",user.getPhotoUrl()+"");
                            Log.i("Email",user.getDisplayName()+"");
                            Log.i("Email",user.getEmail()+"");


                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void handleTwitterSession(TwitterSession session) {
        //Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            Intent i = new Intent(MainActivity.this,AllRecipes.class);
                            i.putExtra("Name",user.getDisplayName()+"");
                            i.putExtra("Email",user.getEmail());
                            i.putExtra("Profile",user.getPhotoUrl()+"");
                            Log.i("Email",user.getPhotoUrl()+"");
                            Log.i("Email",user.getDisplayName()+"");
                            Log.i("Email",user.getEmail()+"");
                            startActivity(i);
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    public void onNetworkAvailable() {
        Log.i("NetworkAvailable",1+"");
        //mAuth.addAuthStateListener(loginStateListener);

        loginStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null) {
                    Intent i = new Intent(MainActivity.this,AllRecipes.class);
                    i.putExtra("Name",firebaseAuth.getCurrentUser().getDisplayName());
                    i.putExtra("Email",firebaseAuth.getCurrentUser().getEmail());
                    i.putExtra("Profile",firebaseAuth.getCurrentUser().getPhotoUrl()+"");
                    startActivity(i);
                }
            }
        };


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
        Log.i("NetworkAvailable",2+"");
        //mAuth.addAuthStateListener(loginStateListener);

        Toast.makeText(this,"Connect to the Internet for Better Experience",Toast.LENGTH_LONG).show();



        loginStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null) {
                    Intent i = new Intent(MainActivity.this,AllRecipes.class);
                    i.putExtra("Name",firebaseAuth.getCurrentUser().getDisplayName());
                    i.putExtra("Email",firebaseAuth.getCurrentUser().getEmail());
                    i.putExtra("Profile",firebaseAuth.getCurrentUser().getPhotoUrl()+"");
                    startActivity(i);
                }
            }
        };


        //Toast.makeText(this,"Connect to the Internet, Improves Experience",Toast.LENGTH_LONG);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(googleSignIn,"No Internet Detected",Snackbar.LENGTH_LONG).show();
            }
        });
        twitterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(googleSignIn,"No Internet Detected",Snackbar.LENGTH_LONG).show();
            }
        });
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

