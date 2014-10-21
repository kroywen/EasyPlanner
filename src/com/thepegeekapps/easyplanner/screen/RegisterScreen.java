package com.thepegeekapps.easyplanner.screen;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.TextUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.billing.IabHelper;
import com.thepegeekapps.easyplanner.billing.IabResult;
import com.thepegeekapps.easyplanner.billing.Inventory;
import com.thepegeekapps.easyplanner.billing.Purchase;
import com.thepegeekapps.easyplanner.storage.Settings;
import com.thepegeekapps.easyplanner.util.Utilities;

public class RegisterScreen extends BaseScreen implements OnClickListener {
	
	private EditText email;
	private EditText firstname;
	private EditText lastname;
	private EditText password;
	private EditText confirmPassword;
	private RadioGroup typeSelectorGroup;
	private View pricingLink;
	private Button createUserBtn;
	
	private IabHelper helper;
	private String SKU_PREMIUM = "easy_planner_subscription_premium";
	public static final int RC_REQUEST = 10001;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_screen);
		initializeViews();
		
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtinmFB6DWsvfb7vT4ZfyIyUnDNjweL4lzLhPmW5ED6B4vdl5zDguyjWnX4YkBfwLK3zG7Ic9r5sy+1R7V9TRD5agF1k3y1CJaGd7NaLp8JVNaoLwbLgvLwcdrpXi6QMBycccMEit6CcbRxLMh+W11yX5UZ+PcI22eiDu4Cz7aI0w4Rptz25LF+fynj/03ANDO6ofPxo2lmo9M9f4y1G2jNVc4CxCAFbMFpgxYZ4c6TihpJmnwPqaQqlFAoN79PIb4ppJofDGFcA5wvTRfJisajMtoL3DSspk5fKZz59ZYARtR801uoYa3z1dphOyvsrn9rHUtH5l6EY6XLFIN3GzowIDAQAB";
		helper = new IabHelper(this, base64EncodedPublicKey);
		helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (result == null || !result.isSuccess()) {
					log("Problem setting up In-app Billing: " + result);
				} else {
					log("In-app billing service has been successfully initialized");
				}
			}
		});
	}
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (helper != null) {
		   helper.dispose();
		   helper = null;
	   }
	}
	
	private void initializeViews() {
		email = (EditText) findViewById(R.id.email);
		firstname = (EditText) findViewById(R.id.firstname);
		lastname = (EditText) findViewById(R.id.lastname);
		password = (EditText) findViewById(R.id.password);
		confirmPassword = (EditText) findViewById(R.id.confirmPassword);
		typeSelectorGroup = (RadioGroup) findViewById(R.id.typeSelectorGroup);
		pricingLink = findViewById(R.id.pricingLink);
		pricingLink.setOnClickListener(this);
		createUserBtn = (Button) findViewById(R.id.createUserBtn);
		createUserBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createUserBtn:
			if (Utilities.isConnectionAvailable(this)) {
				hideSoftKeyborad();
				boolean result = checkInfoFilled();
				if (result) {
					String type = getTypeValue();
					if ("free".equalsIgnoreCase(type)) {
						createUser();
					} else {
						startCreatePremium();
					}
				}
			} else {
				showConnectionErrorDialog();
			}
			break;
		case R.id.pricingLink:
			Intent intent = new Intent(this, PricingScreen.class);
			startActivity(intent);
			break;
		}
	}
	
	private boolean checkInfoFilled() {
		if (TextUtils.isEmpty(email.getText().toString().trim())) {
			showInfoDialog(R.string.error, R.string.invalid_email_address);
			return false;
		} else if (TextUtils.isEmpty(firstname.getText().toString().trim())) {
			showInfoDialog(R.string.error, R.string.invalid_first_name);
			return false;
		} else if (TextUtils.isEmpty(lastname.getText().toString().trim())) {
			showInfoDialog(R.string.error, R.string.invalid_last_name);
			return false;
		} else if (TextUtils.isEmpty(password.getText().toString().trim())) {
			showInfoDialog(R.string.error, R.string.invalid_password);
			return false;
		} else if (TextUtils.isEmpty(confirmPassword.getText().toString().trim())) {
			showInfoDialog(R.string.error, R.string.invalid_confirm_password);
			return false;
		} else if (!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
			showInfoDialog(R.string.error, R.string.passwords_do_not_match);
			return false;
		}
		
		return true;
	}
	
	private void startCreatePremium() {
		if (helper != null) {
			log("Subscriptions supported: " + helper.subscriptionsSupported());
			if (helper.subscriptionsSupported()) {
				List<String> skuList = new ArrayList<String>();
				skuList.add(SKU_PREMIUM);
				
				log("Querying inventory...");
				helper.queryInventoryAsync(true, skuList, new IabHelper.QueryInventoryFinishedListener() {
					@Override
					public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
						if (result != null && result.isFailure()) {
							log(getString(R.string.query_inventory_error, result.getMessage()));
							showInfoDialog(getString(R.string.error), 
								getString(R.string.query_inventory_error, result.getMessage()));
							return;
						}
						
						if (inventory != null) {
							log("Done");
							Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
							if (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase)) {
								log("User has been already subscribed to premium");
								createUser();
							} else {
								log("User is not subscribed yet, show purchase flow dialog");
								launchSubscriptionsPurchaseFlow();
							}
						} else {
							log("Inventory list is empty");
						}
					}
				});
			} else {
				showInfoDialog(R.string.error, R.string.subscriptions_not_supported);
			}
		}
	}
	
	private void launchSubscriptionsPurchaseFlow() {
		helper.launchSubscriptionPurchaseFlow(RegisterScreen.this, SKU_PREMIUM, RC_REQUEST, 
			new IabHelper.OnIabPurchaseFinishedListener() {
				@Override
				public void onIabPurchaseFinished(IabResult result, Purchase info) {
					if (helper == null) return;
					if (result != null && result.isFailure()) {
						log(getString(R.string.purchasing_error, result.getMessage()));
						showInfoDialog(getString(R.string.error), 
							getString(R.string.purchasing_error, result.getMessage()));
						return;
					}
					if (!verifyDeveloperPayload(info)) {
						log("Developer payload verification error");
		                showInfoDialog(R.string.error, R.string.developer_payload_verification_error);
		                return;
					}
					if (info != null) {
						if (TextUtils.isEmpty(info.getSku())) {
							if (info.getSku().equals(SKU_PREMIUM)) {
								log("User has been successfully subscribed to premium");
								createUser();
							}
						} else {
							log("Purchase SKU is empty");
						}
					} else {
						log("Purchase info is null");
					}
				}
			}, Utilities.generateDeveloperPayload(email.getText().toString().trim()));
	}
	
	private boolean verifyDeveloperPayload(Purchase p) {
		if (p == null) return false;
		String payload = p.getDeveloperPayload(); 
		String original = Utilities.generateDeveloperPayload(email.getText().toString().trim());
		return (TextUtils.isEmpty(payload) || TextUtils.isEmpty(original)) ? false :
			payload.equals(original);		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (helper == null) return;
        if (!helper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
	
	private void createUser() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.USER));
		intent.setAction(ApiData.PUT);
		intent.putExtra(ApiData.PARAM_EMAIL, email.getText().toString().trim());
		intent.putExtra(ApiData.PARAM_FIRST, firstname.getText().toString().trim());
		intent.putExtra(ApiData.PARAM_LAST, lastname.getText().toString().trim());
		intent.putExtra(ApiData.PARAM_PASSWORD, password.getText().toString().trim());
		intent.putExtra(ApiData.PARAM_TYPE, getTypeValue());
		intent.putExtra(ApiData.PARAM_APIKEY, ApiData.APIKEY);
		startService(intent);
		showProgressDialog(R.string.creating_user);
	}
	
	private String getTypeValue() {
		int id = typeSelectorGroup.getCheckedRadioButtonId();
		return id == R.id.freeBtn ? "free" : "premium";
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			int status = apiResponse.getStatus();
			if (status == ApiResponse.STATUS_SUCCESS) {
				if (ApiData.USER.equalsIgnoreCase(apiResponse.getRequestName())) {
					settings.setString(Settings.EMAIL, email.getText().toString().trim());
					settings.setString(Settings.PASSWORD, password.getText().toString().trim());
					String token = (String) apiResponse.getData();
					settings.setString(Settings.TOKEN, token);
					settings.setLong(Settings.TOKEN_EXPIRE_DATE, System.currentTimeMillis() + 30*60*1000);
					
					setResult(RESULT_OK);
					finish();
				}
			} else {
				showInfoDialog(getString(R.string.error), apiResponse.getError());
			}
		} else {
			showInfoDialog(getString(R.string.error), apiResponse.getError());
		}
	}

}
