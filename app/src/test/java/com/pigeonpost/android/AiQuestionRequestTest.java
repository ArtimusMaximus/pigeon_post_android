package com.pigeonpost.android;

import com.google.gson.Gson;
import com.pigeonpost.android.data.remote.dto.AiQuestionRequest;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class AiQuestionRequestTest {
    @Test public void ordinaryQuestionsUseStandardWithoutSourceSelection() {
        var json = new Gson().toJsonTree(new AiQuestionRequest("What did I eat?")).getAsJsonObject();
        assertEquals("STANDARD", json.get("answerMode").getAsString());
        assertEquals(0, json.getAsJsonArray("selectedSourceIds").size());
    }
    @Test public void selectedIdsAreCopiedAndSerializedAsNumbers() {
        var ids = new ArrayList<Long>(Arrays.asList(94L, 98L));
        var request = new AiQuestionRequest("When?", "EVENT_DATE", ids);
        ids.clear();
        var json = new Gson().toJsonTree(request).getAsJsonObject();
        assertEquals("EVENT_DATE", json.get("answerMode").getAsString());
        assertEquals(2, json.getAsJsonArray("selectedSourceIds").size());
        assertEquals(94L, json.getAsJsonArray("selectedSourceIds").get(0).getAsLong());
    }
}
