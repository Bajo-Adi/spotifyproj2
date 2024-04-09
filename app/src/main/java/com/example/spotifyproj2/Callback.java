package com.example.spotifyproj2;

public interface Callback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}