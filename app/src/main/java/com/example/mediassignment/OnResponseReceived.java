package com.example.mediassignment;

import com.example.mediassignment.room.UserEntity;

interface OnResponseReceived {
    void responseSuccess(UserEntity userEntity);
}
