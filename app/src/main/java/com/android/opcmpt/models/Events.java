package com.android.opcmpt.models;

import com.android.opcmpt.realm.table.EventsRealm;

import java.io.Serializable;

public class Events implements Serializable {

    public long eid = -1;
    public String event_title = "";
    public String event_image = "";
    public String event_datainicial = "";
    public String event_datafinal = "";
    public String event_description = "";
    public String event_moreinfo = "";
    public String event_localname = "";
    public String event_video = "";
    public String event_videoid = "";
    public String event_pessoas = "";
    public long event_count = -1;

    public EventsRealm getObjectRealm() {
        EventsRealm p = new EventsRealm();
        p.eid = eid;
        p.event_title = event_title;
        p.event_image = event_image;
        p.event_datainicial = event_datainicial;
        p.event_datafinal = event_datafinal;
        p.event_description = event_description;
        p.event_moreinfo = event_moreinfo;
        p.event_localname = event_localname;
        p.event_video = event_video;
        p.event_videoid = event_videoid;
        p.event_pessoas = event_pessoas;
        p.event_count = event_count;

        return p;
    }

    public Events() {
    }

    public boolean isDraft(){
        return !(event_description != null && !event_description.trim().equals(""));
    }

}
