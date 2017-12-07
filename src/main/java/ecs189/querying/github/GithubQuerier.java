package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            // Get commits info
            JSONObject payload = event.getJSONObject("payload");
            JSONArray commits = payload.getJSONArray("commits");

            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");
            // Add table with commits
            sb.append("<style>");
            sb.append("table, th, td, tr { border-collapse: collapse; border: 1px solid black }");
            sb.append("</style>");
            sb.append("<table style=\"width:55%\">");
            sb.append("<tr>");
            sb.append("<th style=\"width:15%\">Commit SHA</th>");
            sb.append("<th style=\"width:40%\">Commit message</th>");
            sb.append("</tr>");
            for(int j = 0; j < commits.length(); j++) {
                JSONObject commit = commits.getJSONObject(j);
                sb.append("<tr>");
                sb.append("<td>" + commit.getString("sha").substring(0, 8) + "</td>");
                sb.append("<td>" + commit.getString("message") + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        String url = BASE_URL + user + "/events";
        System.out.println(url);
        JSONObject json = Util.queryAPI(new URL(url));
        System.out.println(json);
        JSONArray events = json.getJSONArray("root");
        int j = 0;
        for (int i = 0; i < events.length() && j < 10; i++) {
            JSONObject object = events.getJSONObject(i);
            String type = object.getString("type");
            if(type.equals("PushEvent")){
                eventList.add(object);
                j++;
            }
        }
        return eventList;
    }
}