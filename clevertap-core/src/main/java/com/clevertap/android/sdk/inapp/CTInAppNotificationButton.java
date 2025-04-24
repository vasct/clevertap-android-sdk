package com.clevertap.android.sdk.inapp;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;

import com.clevertap.android.sdk.Constants;

import org.json.JSONObject;

@RestrictTo(Scope.LIBRARY)
public class CTInAppNotificationButton implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CTInAppNotificationButton> CREATOR
            = new Parcelable.Creator<CTInAppNotificationButton>() {
        @Override
        public CTInAppNotificationButton createFromParcel(Parcel in) {
            return new CTInAppNotificationButton(in);
        }

        @Override
        public CTInAppNotificationButton[] newArray(int size) {
            return new CTInAppNotificationButton[size];
        }
    };

    private final String backgroundColor;

    private final String borderColor;

    private final String borderRadius;

    private final String text;

    private final String textColor;

    private final CTInAppAction action;

    CTInAppNotificationButton(@NonNull JSONObject jsonObject) {
        text = jsonObject.optString(Constants.KEY_TEXT);
        textColor = jsonObject.optString(Constants.KEY_COLOR, Constants.BLUE);
        backgroundColor = jsonObject.optString(Constants.KEY_BG, Constants.WHITE);
        borderColor = jsonObject.optString(Constants.KEY_BORDER, Constants.WHITE);
        borderRadius = jsonObject.optString(Constants.KEY_RADIUS);
        action = CTInAppAction.createFromJson(jsonObject.optJSONObject(Constants.KEY_ACTIONS));
    }

    protected CTInAppNotificationButton(Parcel in) {
        text = in.readString();
        textColor = in.readString();
        backgroundColor = in.readString();
        borderColor = in.readString();
        borderRadius = in.readString();
        action = in.readParcelable(CTInAppAction.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(textColor);
        dest.writeString(backgroundColor);
        dest.writeString(borderColor);
        dest.writeString(borderRadius);
        dest.writeParcelable(action, flags);
    }


    String getBackgroundColor() {
        return backgroundColor;
    }

    String getBorderColor() {
        return borderColor;
    }

    String getBorderRadius() {
        return borderRadius;
    }

    public String getText() {
        return text;
    }

    String getTextColor() {
        return textColor;
    }

    public CTInAppAction getAction() {
        return action;
    }
}
