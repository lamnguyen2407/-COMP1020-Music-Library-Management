package com.musicapp.service;

import com.musicapp.model.User;

public interface LoginCallback {
    void onSuccess(User user, String role);
    void onError(String errorMessage);
}