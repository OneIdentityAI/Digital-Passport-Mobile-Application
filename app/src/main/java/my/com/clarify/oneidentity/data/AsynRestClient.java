package my.com.clarify.oneidentity.data;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

public class AsynRestClient {
    public static String detectPassportUrl = "http://3.0.93.202/demo/detect-passport";
    public static String detectICUrl = "https://pixel-staging.ddns.net/demov2/detect-ic";
    public static String identityServiceUrl =  Build.VERSION.SDK_INT < Build.VERSION_CODES.M?"http://pixel.ddns.net/":"https://api.1id.ai/v2/";
    public static String viewUploadUrl = identityServiceUrl + "view-upload";

    public static String createIdentityWalletUrl = identityServiceUrl + "create-identity-wallet";
    public static String identityVerificationDemoUrl = identityServiceUrl + "identity-verification-demo";
    public static String uploadUrl = identityServiceUrl + "upload" ;
    public static String listConnectionUrl = identityServiceUrl + "list-connection";

    public static String viewIdentityWalletUrl = identityServiceUrl + "view-identity-wallet";
    public static String createOneIdentityUrl = identityServiceUrl + "create-one-identity";
    public static String listPaymentWalletUrl = identityServiceUrl + "list-payment-wallet";
    public static String addPaymentWalletUrl = identityServiceUrl + "add-payment-wallet";
    public static String deletePaymentWalletUrl = identityServiceUrl + "delete-payment-wallet";
    public static String listDIDUrl = identityServiceUrl + "list-did";
    public static String addDIDUrl = identityServiceUrl + "create-did";
    public static String listMessageUrl = identityServiceUrl + "list-message";
    public static String deleteMessageUrl = identityServiceUrl + "delete-message";
    public static String listCredentialUrl = identityServiceUrl + "list-credential";
    public static String acknowledgeConnectionAcceptanceUrl = identityServiceUrl + "acknowledge-connection-acceptance";
    public static String createConnectionRequestUrl = identityServiceUrl + "create-connection-request";
    public static String acceptConnectionRequestUrl = identityServiceUrl + "accept-connection-request";
    public static String sendMessageUrl = identityServiceUrl + "send-message";
    public static String acceptCredentialOfferUrl = identityServiceUrl + "accept-credential-offer";
    public static String receiveCredentialUrl = identityServiceUrl + "receive-credential";
    public static String createProofOfferUrl = identityServiceUrl + "create-proof-offer";
    public static String webSignInUrl = identityServiceUrl + "web-sign-in";
    public static String maintainIdentityUrl = identityServiceUrl + "maint-identity";
    public static String maintainWalletNameUrl = identityServiceUrl + "maint-wallet-name";
    public static String createProofRequestUrl = identityServiceUrl + "create-proof-request";
    public static String verifyAndAcceptProofOfferUrl = identityServiceUrl + "verify-and-accept-proof-offer";
    public static String listProofUrl = identityServiceUrl + "list-proof";

    public static int timeOutMili = 30000;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void genericPost(Context context, String url, String data, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(timeOutMili);
        client.addHeader("Authorization", "Bearer " + AppDelegate.apikey);
        StringEntity entity = null;
        try {
            entity = new StringEntity(data);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        client.post(context, url, entity, "application/json", responseHandler);
    }

    public static void detectPassport(String selfieImagePath, String identityDocumentImagePath, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(timeOutMili);
        File selfieFile = new File(selfieImagePath);
        File identityDocumentFile = new File(identityDocumentImagePath);
        RequestParams params = new RequestParams();
        try {
            params.put("selfie", selfieFile);
            params.put("passport", identityDocumentFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(detectPassportUrl, params, responseHandler);
    }

    public static void detectIC(String selfieImagePath, String identityDocumentImagePath, String country, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(timeOutMili);
        File selfieFile = new File(selfieImagePath);
        File identityDocumentFile = new File(identityDocumentImagePath);
        RequestParams params = new RequestParams();
        try {
            params.put("selfie", selfieFile);
            params.put("ic", identityDocumentFile);
            params.put("country", country);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(detectICUrl, params, responseHandler);
    }

    public static void upload(String token, String type, String imagePath, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(timeOutMili);
        client.addHeader("Authorization", "Bearer " + AppDelegate.apikey);
        File myFile = new File(imagePath);
        RequestParams params = new RequestParams();
        try {
            params.put("token", token);
            params.put("type", type);
            params.put("file", myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(uploadUrl, params, responseHandler);
    }
}