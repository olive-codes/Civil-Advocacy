package com.oliviabecht.civiladvocacy;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Downloader implements Runnable {

    private MainActivity mainActivity;
    private static final String API_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyAnkDYKTjdFgf0AhX-lw_ezPyybp8hxZ2s&address=";
    private String location;

    public Downloader(MainActivity mainActivity, String location) {
        this.mainActivity = mainActivity;
        this.location = location;
    }

    public interface NameDownloaderMethods {
        void onNamesLoaded();
    }

    @Override
    public void run() {
        //A builder to parse together the proper url using zipcode or City along with API
        Uri.Builder uriBuilder = Uri.parse(API_URL + location).buildUpon();
        //String formatting
        String urlToUse = uriBuilder.toString();
        StringBuilder stringBuilder = new StringBuilder();

        try {
            //try to create a connection
            URL url = new URL(urlToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //do nothing
               return;
            }
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        //pass in our string builder that hold our uri
        strategy (stringBuilder.toString());
    }

    private void strategy (String str) {
        try {
            JSONObject newOfficialObj = new JSONObject(str);
            JSONObject normalizedInput = newOfficialObj.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");
            Office newOffice;

            JSONArray offices = newOfficialObj.getJSONArray("offices");
            JSONArray officials = newOfficialObj.getJSONArray("officials");

            for (int i = 0; i < offices.length(); i++) {

                JSONObject office = (JSONObject) offices.get(i);
                final String officeName = office.getString("name");
                JSONArray officialIndices = office.getJSONArray("officialIndices");

                for (int j = 0; j < officialIndices.length(); j++) {
                    int index = Integer.parseInt(officialIndices.get(j).toString());

                    JSONObject officialDetails = (JSONObject) officials.get(index);
                    final String officialName = officialDetails.getString("name");
                    newOffice = new Office(officeName, officialName);

                    //Does this official have an address
                    if (officialDetails.has("address")) {
                        JSONArray address = officialDetails.getJSONArray("address");
                        JSONObject currAddress = (JSONObject) address.get(0);
                        String line1 = "", line2 = "", cityLine = "", stateLine = "", zipLine = "";

                        if (currAddress.has("line1")) {
                            line1 = currAddress.getString("line1");
                        }
                        if (currAddress.has("line2")) {
                            line2 = currAddress.getString("line2");
                        }
                        if (currAddress.has("city")) {
                            cityLine = currAddress.getString("city");
                        }
                        if (currAddress.has("state")) {
                            stateLine = currAddress.getString("state");
                        }
                        if (currAddress.has("zip")) {
                            zipLine = currAddress.getString("zip");
                        }

                        //Based off what parts of the address the official has ..
                        //format their address
                        String firstLine = line1 + line2;
                        String secondLine = cityLine + ", " + stateLine + " " + zipLine;
                        String fullAddress = firstLine + ", " + secondLine;
                        newOffice.setAddress(fullAddress);
                    }

                    if (officialDetails.has("party")) {
                        //need to add some syntax to match professors documentation
                        String party = "(" + officialDetails.getString("party") + ")";
                        newOffice.setParty(party);
                    }

                    if (officialDetails.has("phones")) {
                        JSONArray phoneArray = officialDetails.getJSONArray("phones");
                        String phoneNum = (String) phoneArray.get(0);
                        newOffice.setPhone(phoneNum);
                    }

                    if (officialDetails.has("urls")) {
                        JSONArray urlArray = officialDetails.getJSONArray("urls");
                        String url = (String) urlArray.get(0);
                        newOffice.setWebsite(url);
                    }


                    if (officialDetails.has("emails")) {
                        JSONArray emailArray = officialDetails.getJSONArray("emails");
                        String email = (String) emailArray.get(0);
                        newOffice.setEmail(email);
                    }

                    if (officialDetails.has("photoUrl")) {
                        String photoUrl = officialDetails.getString("photoUrl");
                        newOffice.setPhoto(photoUrl);
                    }

                    //does the official have any channels...
                    if (officialDetails.has("channels")) {
                        JSONArray channelArr = officialDetails.getJSONArray("channels");
                        //see what channels this official may have
                        for (int z = 0; z < channelArr.length(); z++) {
                            JSONObject channel = (JSONObject) channelArr.get(z);
                            //type: fb, yt, twitter
                            String channelType = channel.getString("type");
                            //this is not full url, generally is the officials name
                            String channelId = channel.getString("id");
                            //switch statement to easily set fb, yt, twitter
                            switch (channelType) {
                                case "Facebook":
                                    String facebookString = "https://www.facebook.com/" + channelId;
                                    newOffice.setFacebook(facebookString);
                                    break;
                                case "YouTube":
                                    String youTubeString = "https://www.youtube.com/" + channelId;
                                    newOffice.setYoutube(channelId);
                                    break;
                                case "Twitter":
                                    String twitterString = "https://www.twitter.com/" + channelId;
                                    newOffice.setTwitter(channelId);
                                    break;
                                default:
                                    System.out.println("error");
                            }
                        }
                    }

                    //make final our Office class object
                    final Office newOfficeF = newOffice;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.addOfficialData(newOfficeF);
                            String str = officeName + " " + officialName;

                        }
                    });
                }
            }

        } catch (Exception exception) {
            System.out.println("exception");
            exception.printStackTrace();
        }
    }
}
