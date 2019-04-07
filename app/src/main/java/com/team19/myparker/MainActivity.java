package com.team19.myparker;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity
{
	public static final String TAG = "MainActivity";
	private static final int RC_SIGN_IN = 0x1001;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(this.getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		GoogleSignInClient client = GoogleSignIn.getClient(this, gso);

		Intent signInIntent = client.getSignInIntent();
		this.startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RC_SIGN_IN)
		{
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try
			{
				Log.d(TAG, "Google sign succeeded.");
				final GoogleSignInAccount account = task.getResult(ApiException.class);

				Log.d(TAG, "Initialzing Firebase...");
				this.firebaseAuthWithGoogle(account);
				this.updateUI(account);
			}
			catch (ApiException e)
			{
				Log.w(TAG, "Google sign in failed.", e);
			}
		}
	}

	private void updateUI(GoogleSignInAccount gsa)
	{

	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
	{
		Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

		FirebaseApp.initializeApp(this);
		final FirebaseAuth mAuth = FirebaseAuth.getInstance();
		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();

							//FirebaseFirestore firestore = FirebaseFirestore.getInstance();
						}
						else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());
						}
					}
				});
	}
}
