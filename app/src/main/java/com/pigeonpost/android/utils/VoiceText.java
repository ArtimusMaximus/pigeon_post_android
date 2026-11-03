package com.pigeonpost.android.utils;

public final class VoiceText {
    private VoiceText() {}
    public static String append(String existing,String spoken) {
        String current=existing==null?"":existing;
        String segment=spoken==null?"":spoken.trim();
        if(segment.isEmpty()) return current;
        if(current.trim().isEmpty()) return segment;
        return current+(Character.isWhitespace(current.charAt(current.length()-1))?"":" ")+segment;
    }
}
