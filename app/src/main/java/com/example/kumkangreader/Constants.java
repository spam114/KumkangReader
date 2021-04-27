package com.example.kumkangreader;
public class Constants {
    public static final String PREFS_NAME = "BarcodeScannerPrefs";

    //For Debugging
    public static final boolean DEBUG = true;
    public static final int COLOR_BG_GRAY = 0XF0F0F0;


    public enum DEBUG_TYPE {
        TYPE_DEBUG, TYPE_ERROR
    }

    //스캐너관련
    public static final String ACTION_BARCODE_OPEN 							= "kr.co.bluebird.android.bbapi.action.BARCODE_OPEN";
    public static final String ACTION_BARCODE_CLOSE 						= "kr.co.bluebird.android.bbapi.action.BARCODE_CLOSE";
    public static final String ACTION_BARCODE_SET_TRIGGER 					= "kr.co.bluebird.android.bbapi.action.BARCODE_SET_TRIGGER";
    public static final String ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS 		= "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_REQUEST_SUCCESS";
    public static final String ACTION_BARCODE_CALLBACK_REQUEST_FAILED 		= "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_REQUEST_FAILED";
    public static final String ACTION_BARCODE_CALLBACK_DECODING_DATA 		= "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_DECODING_DATA";
    public static final String ACTION_BARCODE_SET_PARAMETER 				= "kr.co.bluebird.android.bbapi.action.BARCODE_SET_PARAMETER";
    public static final String ACTION_BARCODE_GET_PARAMETER 				= "kr.co.bluebird.android.bbapi.action.BARCODE_GET_PARAMETER";
    public static final String ACTION_BARCODE_CALLBACK_PARAMETER 			= "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_PARAMETER";
    public static final String ACTION_BARCODE_GET_STATUS 					= "kr.co.bluebird.android.action.BARCODE_GET_STATUS";
    public static final String ACTION_BARCODE_CALLBACK_GET_STATUS 			= "kr.co.bluebird.android.action.BARCODE_CALLBACK_GET_STATUS";


    public static final String EXTRA_BARCODE_DECODING_DATA 					= "EXTRA_BARCODE_DECODING_DATA";
    public static final String EXTRA_HANDLE 								= "EXTRA_HANDLE";
    public static final String EXTRA_INT_DATA2 								= "EXTRA_INT_DATA2";
    public static final String EXTRA_STR_DATA1 								= "EXTRA_STR_DATA1";
    public static final String EXTRA_INT_DATA3								= "EXTRA_INT_DATA3";


    public static final int ERROR_FAILED									= -1;
    public static final int ERROR_NOT_SUPPORTED								= -2;
    public static final int ERROR_NO_RESPONSE								= -4;
    public static final int ERROR_BATTERY_LOW								= -5;
    public static final int ERROR_BARCODE_DECODING_TIMEOUT					= -6;
    public static final int ERROR_BARCODE_ERROR_USE_TIMEOUT					= -7;
    public static final int ERROR_BARCODE_ERROR_ALREADY_OPENED				= -8;
    public static final int ERROR_BARCODE_EXCEED_ASCII_CODE					= -10;
    //끝


    public static final int BARCODE_RECEIVED = 30;

    ///---
    public static final String BTH_SCAN_TO_CONNECT="[BTH_CONNECT]";

    /**
     * Method to be used throughout the app for logging debug messages
     *
     * @param type    - One of TYPE_ERROR or TYPE_DEBUG
     * @param TAG     - Simple String indicating the origin of the message
     * @param message - Message to be logged
     */
}