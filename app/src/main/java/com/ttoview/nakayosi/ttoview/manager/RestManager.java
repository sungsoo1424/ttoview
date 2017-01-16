package com.ttoview.nakayosi.ttoview.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.ttoview.nakayosi.ttoview.model.TagInfoModel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by sungs on 2016-09-19.
 */
public class RestManager extends AsyncTask<String, Void, TagInfoModel>  {
    TagInfoModel tagInfoModel = new TagInfoModel();
    private Context mContext;
    ProgressDialog dialog;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;
    // 네트워크 사용유무
    boolean isNetworkEnabled = false;
    // GPS 상태값
    boolean isGetLocation = false;
    Location location;
    double lat; // 위도
    double lon; // 경도
    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;

    public RestManager(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(mContext, "","카드 인증중 입니다..", true);
    }

    @Override
    protected TagInfoModel doInBackground(String... params) {
        //테그정보 획득
        tagInfoModel = getTagInfo(params[0]);
        //gps정보 획득

        //위치정보 획득
        locationSearch(params[1],params[2]);

        return tagInfoModel;
    }

    @Override
    protected void onPostExecute(TagInfoModel tagInfoModel) {
        //종료시
        dialog.dismiss();
        super.onPostExecute(tagInfoModel);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }



    //gps정보로부터 지역정보를 얻는다.
    public void locationSearch(String lat, String lng) {
        URL url1 = null;
        HttpURLConnection con1 = null;
        String location = "";
        try {
            // 접속 URL 경로
            String surl = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=";
            surl +=lat;
            surl +=","+lng;
            surl +="&result_type=postal_code&language=ko&key=AIzaSyA4cue8D26H0LsM1EMzZGIYsoE9uk2nvVY";

            //String surl = "http://localhost:8080/certification.do";
            // URL생성
            url1 = new URL(surl);
            // 커넥션 생성
            con1 = (HttpURLConnection) url1.openConnection();
            // 파싱
            tagInfoModel.setCurrent_Location(locationXmlParse(con1.getInputStream()));
            // 닫기
            con1.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TagInfoModel getTagInfo(String uid)
    {
        URL url = null;
        HttpURLConnection con = null;
        TagInfoModel tagInfo = null;
        try {
            // 접속 URL 경로
            String surl = "http://ttoview.cafe24.com/certification.do?uid=";
            surl +=uid;

            //String surl = "http://localhost:8080/certification.do";
            // URL생성
            url = new URL(surl);
            // 커넥션 생성
            con = (HttpURLConnection) url.openConnection();
            // 파싱
            tagInfo = tagInfoXmlParse(con.getInputStream());
            // 닫기
            con.disconnect();

        } catch (Exception e) {
            Log.d("test",e.toString());
        }
        return tagInfo;
    }


    //위치정보xml parser
    private String locationXmlParse(InputStream in) throws Exception {
        Document doc = null;
        doc = parseXML(in);
        NodeList descNodes = doc.getElementsByTagName("result");
        String formatted_address = "지역정보 없음";
        for (int i = 0; i < descNodes.getLength(); i++) {
            for (Node node = descNodes.item(i).getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeName().equals("formatted_address")) {
                    formatted_address = node.getTextContent();
                }
            }
        }
        return formatted_address;
    }

    private TagInfoModel tagInfoXmlParse(InputStream in) throws Exception {
        Document doc = null;
        doc = parseXML(in);
        NodeList descNodes = doc.getElementsByTagName("result");
        TagInfoModel tagInfo = new TagInfoModel();
        for (int i = 0; i < descNodes.getLength(); i++) {
            for (Node node = descNodes.item(i).getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeName().equals("certification")) {
                    tagInfo.setCertification(node.getTextContent());
                }

                if (node.getNodeName().equals("uid")) {
                    tagInfo.setUid(node.getTextContent());
                }
                if (node.getNodeName().equals("place_code")) {
                    tagInfo.setPlace_code(Integer.parseInt(node.getTextContent()));
                }

                if (node.getNodeName().equals("record_count")) {
                    tagInfo.setRecord_count(Integer.parseInt(node.getTextContent()));
                }
                if (node.getNodeName().equals("lat")) {
                    tagInfo.setLat(node.getTextContent());
                }
                if (node.getNodeName().equals("lng")) {
                    tagInfo.setLng(node.getTextContent());
                }
                if (node.getNodeName().equals("location")) {
                    tagInfo.setLocation(node.getTextContent());
                }

                if (node.getNodeName().equals("last_record_time")) {
                    tagInfo.setLast_record_time(Timestamp.valueOf(node.getTextContent()));
                }

            }
        }
        return tagInfo;
    }


    private static Document parseXML(InputStream stream) throws Exception {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;
        try {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
            doc = objDocumentBuilder.parse(stream);
        } catch (Exception ex) {
            throw ex;
        }
        return doc;
    }
}