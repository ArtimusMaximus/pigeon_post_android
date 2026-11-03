package com.pigeonpost.android.utils;

import com.google.gson.Gson;
import com.pigeonpost.android.data.remote.dto.AiAnswerResponse;
import org.junit.Test;
import static org.junit.Assert.*;

public class RelatedSourcesTest {
    @Test public void olderServerResponsesRemainUsable() {
        var response=new Gson().fromJson("{\"answer\":\"Answer\",\"sources\":[]}",AiAnswerResponse.class);
        assertEquals("Answer",response.getAnswer());assertNull(response.getRelatedSources());
    }
    @Test public void answerAndBrowsingSourcesStaySeparate() {
        var response=new Gson().fromJson("{\"answer\":\"Answer\",\"sources\":[{\"noteId\":1}],\"relatedSources\":[{\"noteId\":2}]}",AiAnswerResponse.class);
        assertEquals(Long.valueOf(1),response.getSources().get(0).getNoteId());
        assertEquals(Long.valueOf(2),response.getRelatedSources().get(0).getNoteId());
    }
}
