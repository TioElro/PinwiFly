package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Model.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    Button botonregistro,botonlogin,logingooglebtn,logincelularbtn;
    LoginButton loginfacebookbtn;


    private CallbackManager mCallbackManager;
    private static final String TAG = "FacebookAuthentication";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private List<AuthUI.IdpConfig> providers;

    private AccessTokenTracker mAccessTokenTracker;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listener != null)
            mFirebaseAuth.removeAuthStateListener(listener);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/fuente.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        
        printkey();
        setContentView(R.layout.activity_main);

        logincelularbtn = (Button) findViewById(R.id.botoncelular);



        logingooglebtn = (Button)findViewById(R.id.login_btn_google);
        logingooglebtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if(Common.isConnectedToInternet(getApplicationContext())){
                    logingoogle();
                }else{
                    Toast.makeText(MainActivity.this, "Porfavor revisa tu conexión a Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });
        crearPeticionGoogle();
        loginfacebookbtn = (LoginButton)findViewById(R.id.login_button);
        loginfacebookbtn.setReadPermissions("email","public_profile");
        mCallbackManager = CallbackManager.Factory.create();
        loginfacebookbtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                    Log.d(TAG,"onSuccess"+loginResult);
                    handleFacebookToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onEror"+error);
            }
        });


        mFirebaseAuth = FirebaseAuth.getInstance();
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        listener = mFirebaseAuth -> {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            if(user != null) {
                revisarUserFirebase(user);
            }else{
                revisarUserFirebase(null);
            }
        };

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mFirebaseAuth.signOut();
                }
            }
        };




        botonregistro = (Button)findViewById(R.id.botonregistro);
        botonlogin = (Button)findViewById(R.id.botoniniciar);

        botonregistro.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent register = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(register);
            }
        });

        botonlogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent login = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(login);
            }
        });

        logincelularbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    Intent celularlogin = new Intent(MainActivity.this, VerifyNumber.class);
                    startActivity(celularlogin);
                }else{
                    Toast.makeText(MainActivity.this, "Porfavor revisa tu conexión a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void logingoogle() {
        Intent signin = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signin,RC_SIGN_IN);
    }

    private void crearPeticionGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Inicio de sesión: correcto");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            revisarUserFirebase(user);
                        } else {
                            Log.d(TAG,"Inicio de sesión: incorrecto",task.getException());
                            Toast.makeText(MainActivity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
                            revisarUserFirebase(null);
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookToken"+accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Inicio de sesión: correcto");
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    revisarUserFirebase(user);
                }else{
                    Log.d(TAG,"Inicio de sesión: incorrecto",task.getException());
                    Toast.makeText(MainActivity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
                    revisarUserFirebase(null);
                }
            }
        });
    }


    private void printkey() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.pinwifly", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    private void revisarUserFirebase(FirebaseUser user) {
        if(user != null){
            if(!user.getDisplayName().equals("") && user.getDisplayName() != null){

                User current = new User(user.getDisplayName(),user.getEmail(),null,user.getPhoneNumber(),user.getPhotoUrl().toString());
                Intent Home = new Intent(MainActivity.this,Home.class);
                Common.currentUser = current;

                startActivity(Home);
                finish();
            }
        }else{

        }
    }


}
