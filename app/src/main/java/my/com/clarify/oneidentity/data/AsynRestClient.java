package my.com.clarify.oneidentity.data;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

public class AsynRestClient {
    public static String serverUrl = "https://api.1id.ai/v2/";
    public static String viewIdentityWalletUrl = serverUrl + "view-identity-wallet";
    public static String createIdentityWalletUrl = serverUrl + "create-identity-wallet";
    public static String identityVerificationUrl = serverUrl + "identity-verification";
    public static String listPaymentWalletUrl = serverUrl + "list-payment-wallet";
    public static String addPaymentWalletUrl = serverUrl + "add-payment-wallet";
    public static String deletePaymentWalletUrl = serverUrl + "delete-payment-wallet";
    public static String listDIDUrl = serverUrl + "list-did";
    public static String addDIDUrl = serverUrl + "create-did";
    public static String listConnectionUrl = serverUrl + "list-connection";
    public static String listMessageUrl = serverUrl + "list-message";
    public static String deleteMessageUrl = serverUrl + "delete-message";
    public static String listCredentialUrl = serverUrl + "list-credential";
    public static String acknowledgeConnectionAcceptanceUrl = serverUrl + "acknowledge-connection-acceptance";
    public static String createConnectionRequestUrl = serverUrl + "create-connection-request";
    public static String sendMessageUrl = serverUrl + "send-message";
    public static String acceptCredentialOfferUrl = serverUrl + "accept-credential-offer";
    public static String receiveCredentialUrl = serverUrl + "receive-credential";
    public static String createProofOfferUrl = serverUrl + "create-proof-offer";
    public static String webSignInUrl = serverUrl + "web-sign-in";
    public static String uploadUrl = serverUrl + "upload" ;
    public static String createProofRequestUrl = serverUrl + "create-proof-request";
    public static String verifyAndAcceptProofOfferUrl = serverUrl + "verify-and-accept-proof-offer";
    public static String listProofUrl = serverUrl + "list-proof";
    public static String viewUploadUrl = serverUrl + "view-upload";

    public static int timeOutMili = 20000;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void genericPost(Context context, String url, String data, AsyncHttpResponseHandler responseHandler) {
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
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

    public static void upload(String token, String type, String imagePath, AsyncHttpResponseHandler responseHandler) {
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
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