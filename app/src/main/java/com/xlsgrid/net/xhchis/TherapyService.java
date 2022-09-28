package com.xlsgrid.net.xhchis;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hc.auth.AuthorizeApi;
import com.hc.common.IResponseCallback;
import com.hc.instant.api.TherapyApi;
import com.hc.instant.model.Classification;
import com.hc.instant.model.Cure;
import com.hc.instant.model.Level;

import org.jdeferred2.AlwaysCallback;
import org.jdeferred2.Deferred;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.ProgressCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class TherapyService {

    private String channelID = "yjy20220965144699";

    private String TAG = "TherapyService";

    public List<Cure> fetchAllCures() {
        List<Cure> cures = new ArrayList<Cure>();

        Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();
        promise.done(new DoneCallback() {
            public void onDone(Object result) {

            }
        }).fail(new FailCallback<Exception>() {
            public void onFail(Exception rejection) {
                Log.e(TAG, rejection.toString());
            }
        }).progress(new ProgressCallback() {
            public void onProgress(Object progress) {

            }
        });

        new TherapyApi().fetchAllCures(channelID, new IResponseCallback<List<Cure>>() {
            @Override
            public void onRequestFailed(@NonNull Call call, @NonNull Exception exception) {
                deferred.reject(exception);
            }

            @Override
            public void onRequestSucceed(List<Cure> returnValue) {
                cures.addAll(returnValue);
                deferred.resolve(returnValue);
            }
        });

        try {
            promise.waitSafely();
        } catch (InterruptedException e) {

        }
        return cures;
    }

    boolean authorize() {
        final boolean[] ret = {false};

        Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();
        promise.done(new DoneCallback<Boolean>() {
            public void onDone(Boolean result) {
                ret[0] = result.booleanValue();
            }
        }).fail(new FailCallback<Exception>() {
            public void onFail(Exception rejection) {
                Log.e(TAG, rejection.toString());
            }
        }).progress(new ProgressCallback() {
            public void onProgress(Object progress) {

            }
        });

        new AuthorizeApi().authorize(channelID,
                new IResponseCallback<Boolean>() {
                    @Override
                    public void onRequestSucceed(Boolean returnValue) {
                        deferred.resolve(returnValue);

                    }

                    @Override
                    public void onRequestFailed(@NonNull Call call, @NonNull Exception exception) {
                        deferred.reject(exception);
                    }
                });

        try {
            promise.waitSafely();
        } catch (InterruptedException e) {

        }
        return ret[0];
    }

    boolean login(String userID, Context context) throws Exception {
        final boolean[] ret = {false};
        final Exception[] exception = new Exception[1];

        Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();
        promise.done(new DoneCallback<Boolean>() {
            public void onDone(Boolean result) {
                ret[0] = result.booleanValue();
            }
        }).fail(new FailCallback<Exception>() {
            public void onFail(Exception rejection) {
                Log.e(TAG,rejection.toString());
                exception[0] = rejection;
            }
        }).progress(new ProgressCallback() {
            public void onProgress(Object progress) {

            }
        });

        new AuthorizeApi().login(
                userID,
                channelID,
                context,
                new IResponseCallback<Boolean>() {
                    @Override
                    public void onRequestSucceed(Boolean isLoginSuccessful) {
                        deferred.resolve(isLoginSuccessful);

                    }

                    @Override
                    public void onRequestFailed(@NonNull Call call, @NonNull Exception exception) {
                        deferred.reject(exception);
                    }
                });

        try {
            promise.waitSafely();
        } catch (InterruptedException e) {

        }

        if (exception[0] != null) {
            throw exception[0];
        }
        return ret[0];
    }

    String fetchSolutionJSON(Cure cure,
                             String userID,
                             @Nullable Classification classification,
                             Duration duration,
                             int trackNum,
                             Level level,
                             Context context) throws Exception {
        final Exception[] exception = new Exception[1];
        StringBuffer ret = new StringBuffer();
        Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();
        promise.done(new DoneCallback<String>() {
            public void onDone(String result) {
                ret.append(result);
            }
        }).fail(new FailCallback<Exception>() {
            public void onFail(Exception rejection) {
                Log.e(TAG,rejection.toString());
                exception[0] = rejection;
            }
        }).progress(new ProgressCallback() {
            public void onProgress(Object progress) {

            }
        });

        new TherapyApi().fetchCureSolution(
                channelID,
                userID,
                cure,
                classification,
                duration,
                trackNum,
                level,
                context,
                new IResponseCallback<String>() {
                    @Override
                    public void onRequestFailed(@NonNull Call call, @NonNull Exception exception) {
                        deferred.reject(exception);
                    }

                    @Override
                    public void onRequestSucceed(String solutionJSON) {
                        deferred.resolve(solutionJSON);
                    }
                });
        try {
            promise.waitSafely();
        } catch (InterruptedException e) {

        }

        if (exception[0] != null) {
            throw exception[0];
        }
        return ret.toString();
    }


}
