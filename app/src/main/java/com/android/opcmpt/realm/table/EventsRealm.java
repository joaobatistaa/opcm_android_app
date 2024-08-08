package com.android.opcmpt.realm.table;

import com.android.opcmpt.models.Events;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EventsRealm extends RealmObject {

    @PrimaryKey
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

    public long added_date = 0;

    public Events getOriginal() {
        Events e = new Events();
        e.eid = eid;
        e.event_title = event_title;
        e.event_image = event_image;
        e.event_datainicial = event_datainicial;
        e.event_datafinal = event_datafinal;
        e.event_description = event_description;
        e.event_moreinfo = event_moreinfo;
        e.event_localname = event_localname;
        e.event_video = event_video;
        e.event_videoid = event_videoid;
        e.event_pessoas = event_pessoas;
        e.event_count = event_count;
        return e;
    }

}
