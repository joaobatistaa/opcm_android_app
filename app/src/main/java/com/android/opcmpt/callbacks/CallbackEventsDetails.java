package com.android.opcmpt.callbacks;

import com.android.opcmpt.models.Events;

import java.util.ArrayList;
import java.util.List;

public class CallbackEventsDetails {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public Events events = null;
    public List<Events> eventos = new ArrayList<>();
}
