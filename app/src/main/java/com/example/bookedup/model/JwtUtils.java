package com.example.bookedup.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {

    private static SecretKey secureKey;

    private static long userID = 0;

    private static final String SECRET_KEY = "korisnickoime"; // Replace with your actual secret key

    private static SecretKey getSecureKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public static long getUserIdFromToken(String token) throws JSONException {
        Log.d("JwtUtils", "Token " + token);
        if (token != null && !token.isEmpty()) {
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
            JSONObject jsonPayload = new JSONObject(payload);
            userID = jsonPayload.getInt("id");
        } else {
            Log.d("JwtUtils", "Token is null or empty");
        }
        return userID;
    }
}
