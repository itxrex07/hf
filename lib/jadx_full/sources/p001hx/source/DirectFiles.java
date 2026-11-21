package p001hx.source;

import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.okhttp.ApiClient;
import p001hx.util.Logs;
import p001hx.util.RemoteFile;

/* loaded from: classes3.dex */
final class DirectFiles {
    private static final int RETENTION = 512;
    private static final long SIZE_LOOKUP_TIMEOUT_MS = 5000;
    private final ApiClient api;
    private final Map<String, String> urls = new LruMap(RETENTION);
    private final Map<String, Long> sizes = new LruMap(RETENTION);
    private final ExecutorService sizeLookups = Executors.newCachedThreadPool();

    DirectFiles(ApiClient apiClient) {
        this.api = apiClient;
    }

    String knownUrl(String str) {
        String str2;
        if (str == null) {
            return null;
        }
        synchronized (this.urls) {
            str2 = this.urls.get(str);
        }
        return str2;
    }

    String resolveUrl(String str, String str2, String str3) {
        String knownUrl = knownUrl(str2);
        if (knownUrl != null) {
            return knownUrl;
        }
        if (str3 == null || str2 == null) {
            return null;
        }
        try {
            String fetchUrl = fetchUrl(str, str2, str3);
            long probeLength = fetchUrl.isEmpty() ? -1L : RemoteFile.probeLength(fetchUrl);
            Log.i(Logs.TAG, "direct file " + str2 + ": " + (fetchUrl.isEmpty() ? "none" : Uri.parse(fetchUrl).getHost() + ", " + probeLength + " bytes"));
            if (probeLength <= 0 || fetchUrl.equals(str3)) {
                return null;
            }
            remember(str2, fetchUrl);
            return fetchUrl;
        } catch (IOException | NumberFormatException | JSONException e) {
            Log.w(Logs.TAG, "direct file " + str2 + " not checked", e);
            return null;
        }
    }

    String playInfoFor(String str, String str2, int i, int i2) {
        if (str2 == null || i < 0 || i2 < 0) {
            return null;
        }
        String key = SignedResource.key(str2, i, i2);
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONObject jSONObject2 = jSONObject.getJSONObject("data");
            List<JSONObject> directStreams = directStreams(str2, fetchEpisodeRows(str2, i, i2), key);
            if (directStreams.isEmpty()) {
                Log.w(Logs.TAG, "play-info " + key + ": no direct file");
                return null;
            }
            Collections.sort(directStreams, new Comparator() { // from class: hx.source.DirectFiles.0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return DirectFiles.lambda$playInfoFor$0((JSONObject) obj, (JSONObject) obj2);
                }
            });
            String joinResolutions = joinResolutions(directStreams);
            jSONObject2.put("streams", new JSONArray((Collection) directStreams));
            jSONObject2.put("displayResolutions", joinResolutions);
            Log.i(Logs.TAG, "play-info " + key + ": direct files at " + joinResolutions + "p on " + Uri.parse(directStreams.get(0).getString("url")).getHost());
            return jSONObject.toString();
        } catch (IOException | JSONException e) {
            Log.w(Logs.TAG, "play-info " + key + ": direct files not resolved", e);
            return null;
        }
    }

    static /* synthetic */ int lambda$playInfoFor$0(JSONObject jSONObject, JSONObject jSONObject2) {
        return Integer.compare(jSONObject.optInt("resolutions"), jSONObject2.optInt("resolutions"));
    }

    String withSizes(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONObject optJSONObject = jSONObject.optJSONObject("data");
            String optString = optJSONObject != null ? optJSONObject.optString("subjectId", "") : "";
            JSONArray optJSONArray = optJSONObject != null ? optJSONObject.optJSONArray("list") : null;
            if (!optString.isEmpty() && optJSONArray != null) {
                List<Callable<Boolean>> sizeLookupsFor = sizeLookupsFor(optString, optJSONArray);
                int countSized = countSized(optString, this.sizeLookups.invokeAll(sizeLookupsFor, SIZE_LOOKUP_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                Log.i(Logs.TAG, "resource list " + optString + ": sized " + countSized + " of " + sizeLookupsFor.size() + " items");
                return countSized > 0 ? jSONObject.toString() : str;
            }
            return str;
        } catch (InterruptedException | JSONException e) {
            Log.w(Logs.TAG, "resource list sizes not read: " + e.getMessage());
            return str;
        }
    }

    private List<JSONObject> directStreams(String str, List<JSONObject> list, String str2) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            try {
                JSONObject jSONObject = list.get(i);
                String string = jSONObject.getString("resourceId");
                String fetchUrl = fetchUrl(str, string, jSONObject.optString("resourceLink"));
                if (!fetchUrl.isEmpty()) {
                    arrayList.add(streamOf(jSONObject, string, fetchUrl));
                }
            } catch (IOException | JSONException e) {
                Log.w(Logs.TAG, "play-info " + str2 + ": direct file row " + i + " not resolved", e);
            }
        }
        return arrayList;
    }

    private static JSONObject streamOf(JSONObject jSONObject, String str, String str2) throws JSONException {
        return new JSONObject().put("format", "MP4").put("id", str).put("url", str2).put("resolutions", String.valueOf(jSONObject.getInt("resolution"))).put("size", jSONObject.optString("size")).put("duration", jSONObject.optInt("duration")).put("codecName", jSONObject.optString("codecName")).put("signCookie", "").put("idType", "");
    }

    private static String joinResolutions(List<JSONObject> list) throws JSONException {
        StringBuilder sb = new StringBuilder();
        for (JSONObject jSONObject : list) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(jSONObject.getString("resolutions"));
        }
        return sb.toString();
    }

    private List<JSONObject> fetchEpisodeRows(String str, int i, int i2) throws IOException, JSONException {
        String str2;
        int i3;
        int i4;
        int i5 = 0;
        JSONObject fetchResourceListing = fetchResourceListing(str, i, i2, 0);
        ArrayList arrayList = new ArrayList();
        addEpisodeRows(arrayList, fetchResourceListing.getJSONArray("list"), i, i2, 0);
        JSONArray optJSONArray = fetchResourceListing.optJSONArray("collectionResolutions");
        if (optJSONArray == null) {
            return arrayList;
        }
        while (i5 < optJSONArray.length()) {
            int optInt = optJSONArray.getJSONObject(i5).optInt("resolution");
            if (optInt <= 0 || hasResolution(arrayList, optInt)) {
                str2 = str;
                i3 = i;
                i4 = i2;
            } else {
                str2 = str;
                i3 = i;
                i4 = i2;
                addResolutionRows(arrayList, str2, i3, i4, optInt);
            }
            i5++;
            str = str2;
            i = i3;
            i2 = i4;
        }
        return arrayList;
    }

    private void addResolutionRows(List<JSONObject> list, String str, int i, int i2, int i3) {
        try {
            addEpisodeRows(list, fetchResourceListing(str, i, i2, i3).getJSONArray("list"), i, i2, i3);
        } catch (IOException | JSONException e) {
            Log.w(Logs.TAG, "resource list " + SignedResource.key(str, i, i2) + " at " + i3 + "p not read", e);
        }
    }

    private JSONObject fetchResourceListing(String str, int i, int i2, int i3) throws IOException, JSONException {
        return new JSONObject(this.api.get(this.api.url("wefeed-mobile-bff/subject-api/resource" + ("?subjectId=" + str + "&page=1&perPage=8&all=0&startPosition=1&endPosition=1&pagerMode=2&resolution=" + i3 + "&se=" + i + "&epFrom=" + i2 + "&epTo=" + i2)))).getJSONObject("data");
    }

    private static void addEpisodeRows(List<JSONObject> list, JSONArray jSONArray, int i, int i2, int i3) throws JSONException {
        for (int i4 = 0; i4 < jSONArray.length(); i4++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i4);
            if ((jSONObject.optInt("se", -1) == i && jSONObject.optInt("ep", -1) == i2) && (i3 <= 0 || jSONObject.optInt("resolution") == i3)) {
                list.add(jSONObject);
            }
        }
    }

    private static boolean hasResolution(List<JSONObject> list, int i) {
        Iterator<JSONObject> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().optInt("resolution") == i) {
                return true;
            }
        }
        return false;
    }

    private String fetchUrl(String str, String str2, String str3) throws IOException, JSONException {
        JSONObject fetchFile = fetchFile(str, str2, str3);
        return fetchFile != null ? fetchFile.optString("resourceLink", "") : "";
    }

    private JSONObject fetchFile(String str, String str2, String str3) throws IOException, JSONException {
        String str4 = "?subjectId=" + str + "&resourceId=" + str2 + "&failUrl=" + Uri.encode(str3) + "&failCode=404&resourceNum=0";
        JSONObject fetchFirstPosition = fetchFirstPosition("wefeed-mobile-bff/subject-api/resource-position/v2" + str4);
        return fetchFirstPosition != null ? fetchFirstPosition : fetchFirstPosition("wefeed-mobile-bff/subject-api/resource-position" + str4);
    }

    private JSONObject fetchFirstPosition(String str) throws IOException, JSONException {
        JSONArray optJSONArray = new JSONObject(this.api.get(this.api.url(str))).getJSONObject("data").optJSONArray("list");
        if (optJSONArray == null || optJSONArray.length() == 0) {
            return null;
        }
        return optJSONArray.getJSONObject(0);
    }

    private List<Callable<Boolean>> sizeLookupsFor(final String str, JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            final JSONObject optJSONObject = jSONArray.optJSONObject(i);
            if (optJSONObject != null && !optJSONObject.optString("resourceId", "").isEmpty()) {
                arrayList.add(new Callable() { // from class: hx.source.DirectFiles.1
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return DirectFiles.this.m25lambda$sizeLookupsFor$1$hxsourceDirectFiles(str, optJSONObject);
                    }
                });
            }
        }
        return arrayList;
    }

    /* renamed from: lambda$sizeLookupsFor$1$hx-source-DirectFiles, reason: not valid java name */
    /* synthetic */ Boolean m25lambda$sizeLookupsFor$1$hxsourceDirectFiles(String str, JSONObject jSONObject) throws Exception {
        return Boolean.valueOf(resolveSize(str, jSONObject));
    }

    private static int countSized(String str, List<Future<Boolean>> list) throws InterruptedException {
        int i = 0;
        for (Future<Boolean> future : list) {
            try {
                if (!future.isCancelled() && future.get().booleanValue()) {
                    i++;
                }
            } catch (ExecutionException e) {
                Log.w(Logs.TAG, "resource list " + str + ": direct file size not read", e.getCause());
            }
        }
        return i;
    }

    private boolean resolveSize(String str, JSONObject jSONObject) throws IOException, JSONException {
        String string = jSONObject.getString("resourceId");
        Long cachedSize = knownUrl(string) != null ? cachedSize(string) : null;
        if (cachedSize == null && (cachedSize = lookUpSize(str, string, jSONObject.optString("resourceLink", ""))) == null) {
            return false;
        }
        jSONObject.put("size", String.valueOf(cachedSize));
        return true;
    }

    private Long cachedSize(String str) {
        Long l;
        synchronized (this.sizes) {
            l = this.sizes.get(str);
        }
        return l;
    }

    private Long lookUpSize(String str, String str2, String str3) throws IOException, JSONException {
        JSONObject fetchFile = fetchFile(str, str2, str3);
        String optString = fetchFile != null ? fetchFile.optString("resourceLink", "") : "";
        long optLong = fetchFile != null ? fetchFile.optLong("size", 0L) : 0L;
        if (optString.isEmpty() || optString.equals(str3) || optLong <= 0) {
            return null;
        }
        synchronized (this.sizes) {
            this.sizes.put(str2, Long.valueOf(optLong));
        }
        remember(str2, optString);
        return Long.valueOf(optLong);
    }

    private void remember(String str, String str2) {
        synchronized (this.urls) {
            this.urls.put(str, str2);
        }
    }
}
