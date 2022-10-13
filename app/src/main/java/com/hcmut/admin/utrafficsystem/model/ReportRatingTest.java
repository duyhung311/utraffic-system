package com.hcmut.admin.utrafficsystem.model;

import com.hcmut.admin.utrafficsystem.repository.remote.model.response.ReportResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.ReportUser;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.StatusRenderData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportRatingTest {

    private static List<StatusRenderData> segmentList = new ArrayList<>();
    private static List<ReportResponse> responseList = new ArrayList<>();

    static {
        initTest();
    }

    public static void initTest() {
        StatusRenderData s = new StatusRenderData();
        s.setColor("#ff0000");
        s.setSegment(56812);
        s.setVelocity(5);
        StatusRenderData.Polyline p = new StatusRenderData.Polyline();
        p.setCoordinates(Arrays.asList(Arrays.asList(106.656547, 10.7782678), Arrays.asList(106.6561283, 10.7777304)));
        s.setPolyline(p);
        segmentList.add(s);

        s = new StatusRenderData();
        s.setColor("#0f912c");
        s.setSegment(56811);
        s.setVelocity(26);
        p = new StatusRenderData.Polyline();
        p.setCoordinates(Arrays.asList(Arrays.asList(106.6561757, 10.7776936), Arrays.asList(106.6565944, 10.778231)));
        s.setPolyline(p);
        segmentList.add(s);

        ArrayList<String> img1 = new ArrayList<String>();
        img1.addAll(Arrays.asList("https://ss-images.catscdn.vn/wpm450/2020/04/12/7326991/chi-google-lam-vy-da-3.jpg",
                "https://icdn.dantri.com.vn/thumb_w/640/2018/5/23/net-cuoi-be-gai-9-1527053440039156820618.jpg"));
        ArrayList<String> img2 = new ArrayList<>();
        img2.addAll(Arrays.asList("1", "2"));
        ReportResponse r = new ReportResponse(1, new ReportUser("5e3b931c31034d0308eb0ea4", "Lan", 3.f),6,
                img1,
                img2,
                "Teo em", 3f);
        responseList.add(r);

        img1 = new ArrayList<String>();
        img1.addAll(Arrays.asList("https://giadinh.mediacdn.vn/thumb_w/640/2020/6/8/photo-1-15916053017531747203552.jpg",
                "https://1.bp.blogspot.com/-jR4zDwjzt0c/XfXnVp6n0NI/AAAAAAAAUTc/o_gKWcHYTiIbL0JAPrPEGKAUeD_MkDwDQCLcBGAsYHQ/s1600/Hinh-Anh-Hot-Girl-Facebook-Wap102-Com%2B%25282%2529.jpg"));
        img2 = new ArrayList<>();
        img2.addAll(Arrays.asList("1", "2"));
        r = new ReportResponse(2, new ReportUser("5e3b931c31034d0308eb0ea4", "Phong", 3.f), 24,
                img1,
                img2,
                "Teo em", 4f);
        responseList.add(r);
    }

    public static List<StatusRenderData> getSegmentReport() {
        return segmentList;
    }

    public static List<ReportResponse> getReportFromSegment(long segmentId) {
        if (segmentId == 56812) {
            return Arrays.asList(responseList.get(0));
        }
        return Arrays.asList(responseList.get(1));
    }
}
