package com.develop.devtask.service;

import android.app.AlertDialog;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONException;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class ApiRequest {
    private static ApiRequest firstInstance = null;
    private static boolean firstThread = false;
    final OkHttpClient okHttpClient;
    private AlertDialog waitingAlertDialog;

    public ApiRequest(Context context) {
        okHttpClient = getUnsafeOkHttpClient();
        AndroidNetworking.initialize(context, okHttpClient);
    }

    public static ApiRequest getInstance(Context context) {
        if (firstInstance == null) {
            if (firstThread) {
                Thread.currentThread();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            firstInstance = new ApiRequest(context);
        }
        return firstInstance;
    }

    /**
     * @param url      constant url of service
     * @param model    returned model
     * @param priority priority of request
     * @param callback callback of service if you removed the model replace it with string
     */
    public void createGetRequest(String url, Class model, Priority priority, final ServiceCallback callback) {
        AndroidNetworking.get(url)
                .setPriority(priority)
                .build()
                .getAsObject(model, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * @param url      constant url of service
     * @param priority priority of request
     * @param callback callback of service if you removed the model replace it with string
     */
    public void createGetRequest(String url, Priority priority, final ServiceCallback callback) {
        AndroidNetworking.get(url)
                .setPriority(priority)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                try {
                    callback.onFail(anError);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void createPutRequest(String url,String token,HashMap<String, String> params, final ServiceCallback callback) {
        AndroidNetworking.put(url)
                .addBodyParameter(params)
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                try {
                    callback.onFail(anError);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void createPutRequest(String url,String token, final ServiceCallback callback) {
        AndroidNetworking.put(url)
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * @param url      constant url of service
     * @param resModel response model
     * @param model    parameters of your request
     * @param priority priority of request
     * @param callback callback of service if you removed the model replace it with string
     */
    public void createPostRequest(String url, Class resModel, HashMap<String, String> model, Priority priority, final ServiceCallback callback) {
        AndroidNetworking.post(url)
                .addBodyParameter(model)
                .setPriority(priority)
                .build()
                .getAsObject(resModel, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * @param url      constant url of service
     * @param priority priority of request
     * @param callback callback of service if you removed the model replace it with string
     */
    public void createPostRequest(String url, HashMap<String, String> mParams, Priority priority, final ServiceCallback callback) {
        AndroidNetworking.post(url)
                .addBodyParameter(mParams)
                .setPriority(priority)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void createPostRequest(String url, HashMap<String, Object> params,  final ServiceCallback callback) {
        AndroidNetworking.post(url)
                .addBodyParameter(params)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public void createPostRequest(String url, String token,HashMap<String, String> params,  final ServiceCallback callback) {
        AndroidNetworking.post(url)
                .addBodyParameter(params)
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * @param url      constant url of service
     * @param object   object of Class Request
     * @param callback callback of service if you removed the model replace it with string
     */
    public void createPostRequest(String url, Object object, final ServiceCallback callback) {
        AndroidNetworking.post(url)
                .addBodyParameter(object)
                .setPriority(Priority.HIGH)
                .addHeaders("Content-Type", "application/json")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void createPostRequest(String url, String token, final ServiceCallback callback) {
        AndroidNetworking.post(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer " + token)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void createGetRequest(String url, String token, final ServiceCallback callback) {
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer " + token)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * @param url      constant url of service
     * @param files    files which you want to upload
     * @param params   parameters of your request
     * @param priority priority of request
     * @param callback callback of service if you removed the model replace it with string
     */
    public void createUploadRequest(String url, HashMap<String, File> files, HashMap<String, String> params, Priority priority, final ServiceCallback callback) {
        AndroidNetworking.upload(url)
                .addMultipartParameter(params)
                .addMultipartFile(files)
                .setPriority(priority)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                }).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                try {
                    callback.onFail(anError);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * @param url      constant url of service
     * @param files    files which you want to upload
     * @param params   parameters of your request
     * @param priority priority of request
     * @param callback callback of service if you removed the model replace it with string
     */
    public void createUploadRequest(String url, String token, HashMap<String, File> files, HashMap<String, String> params, Priority priority, final ServiceCallback callback) {
        AndroidNetworking.upload(url)
                .addHeaders("Authorization","Bearer "+token)
                .addHeaders("Accept","application/json")
                .addMultipartParameter(params)
                .addMultipartFile(files)
                .setPriority(priority)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                }).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                try {
                    callback.onFail(anError);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param url      constant url of service
     * @param files    files which you want to upload
     * @param params   parameters of your request
     * @param priority priority of request
     * @param progress progress on the upload
     * @param callback callback of service if you removed the model replace it with string
     */
    public void createUploadRequest(String url, HashMap<String, File> files, HashMap<String, String> params, Priority priority, final UploadProgress progress, final ServiceCallback callback) {
        AndroidNetworking.upload(url)
                .addMultipartFile(files)
                .addMultipartParameter(params)
                .setPriority(priority)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        progress.onProgress((int) ((bytesUploaded / totalBytes) * 100));
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            callback.onFail(anError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier((hostname, session) -> true);

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface ServiceCallback<T> {
        void onSuccess(T response) throws JSONException;

        void onFail(ANError error) throws JSONException;
    }

    public interface UploadProgress {
        void onProgress(int percent);
    }

    public AlertDialog getWaitingAlertDialog() {
        return waitingAlertDialog;
    }

    public void setWaitingAlertDialog(AlertDialog waitingAlertDialog) {
        this.waitingAlertDialog = waitingAlertDialog;
    }
}
