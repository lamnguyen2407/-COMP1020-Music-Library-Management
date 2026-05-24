package com.musicapp.service;

import com.musicapp.model.User;

public interface RegisterCallback {
    void onSuccess(User user);
    void onError(String errorMessage);
}