# Complete APK Technical/API Inspection Report — `efmd30`

**Artifact basis:** `/home/ubuntu/downloads/efmd30_full` and `/home/ubuntu/apk_full_complete/efmd30_full.apk` only.

**Important correction:** the earlier 35,094,528-byte download was incomplete and ended inside `lib/armeabi-v7a/libalivcffmpeg.so`. It was not used as the basis for this report. The fresh artifact is 70,146,494 bytes, has a valid ZIP central directory, passes `unzip -t`, and was extracted/decompiled from scratch.

## 1. Executive summary

The APK is `com.community.oneroom`, version `4.0.03.1020.03`, version code `50020133`, with `com.transsion.subroom.activity.SplashActivity` as the principal launcher activity. The complete archive contains 6,306 ZIP entries, 5,946 raw `res/` files, 76 assets, 150 `META-INF` entries, both ARM native-library ABIs, and three DEX files. JADX 1.5.1 completed successfully and produced 73 Java source files and 6,713 reconstructed resource files.

The directly recoverable network implementation is concentrated in a patch/overlay package named `p001hx.*` and is loaded through `assets/hx.*`. It dynamically locates the host app's obfuscated network manager (`fh.c`) and shared-header holder (`eh.b`), reflectively obtains the host OkHttp client and Retrofit base URL, installs interceptors, and proxies selected API responses and DASH playback resources. The host app's original API service interfaces, DTO classes, and normal Retrofit annotations are not present as ordinary recoverable Java/Kotlin/smali code in the three DEX files. Therefore, this report documents the API surface that is genuinely observable from route literals, request construction, response mutation, and JSON access patterns; it does not claim a complete host-backend contract that the APK does not contain.

The patch performs five major functions:

1. It adds build/client tagging to host requests.
2. It independently calls multiple `aoneroom.com` API hosts for visitor login and play-info, using a redacted manifest gateway secret and HMAC-MD5 signing.
3. It rewrites play-info/resource-list responses to point at signed DASH manifests or a local loopback server.
4. It rewrites selected home, member, operating-page, and promotion responses.
5. It serves locally generated/interleaved MP4 byte ranges over `127.0.0.1:47291` while fetching DASH segments from CDN origins with signed cookies and retries.

## 2. Extraction and verification scope

| Item | Confirmed result |
|---|---|
| APK size | 70,146,494 bytes |
| SHA-256 | `494ed84bf0a88620d4268966abb4b232827c21f153012e1581387d4b93cb2adc` |
| ZIP validation | `unzip -t` passed with no errors |
| ZIP entries | 6,306 |
| Raw resources | 5,946 files under `res/` |
| Assets | 76 files |
| DEX files | `classes.dex`, `classes2.dex`, `classes3.dex` |
| DEX sizes | 64,397,164; 16,292; 117,508 bytes |
| DEX class definitions | 4; 6; 98 respectively |
| JADX output | 73 Java files; 6,713 reconstructed resources; successful completion |
| Native ABIs | `arm64-v8a`, `armeabi-v7a` |
| Manifest permissions | 57 |
| Package | `com.community.oneroom` |
| Version | `4.0.03.1020.03` / code `50020133` |
| Main activity | `com.transsion.subroom.activity.SplashActivity` |

Native libraries include the app/player stack (`libsaasCorePlayer.so`, `libsaasDownloader.so`, FFmpeg/codec libraries, Media3 extensions), patch support (`libhxpatch.so`), MMKV, crash/telemetry libraries, and advertising/device libraries. Native string scanning was performed, but native symbols and strings are not treated as API implementation unless corroborated by Java/resource evidence.

## 3. Recovered code and dynamic-loading boundary

### 3.1 Patch assets

| Asset | Observed value/role |
|---|---|
| `assets/hx.factory` | `uvj.ucxx.mb.QiQ9O3z` |
| `assets/hx.hook` | `hx.AppPatch` |
| `assets/hx.classes` | Class list targeted by the overlay; includes member, download, ad, device, identity, and network-related classes |
| `assets/hx.code` | Binary code-overlay payload; not a normal Java source archive |
| `assets/hx.cer` | Certificate/patch support payload |

`p001hx.AppPatch.install(ClassLoader)` schedules an installer on the main looper. The installer retries discovery, resolves `fh.c`, unlocks the patch laboratory, reads the installed-app build tag, locates `eh.b`, and installs the build-tag and DASH interceptors. It removes applied hot patches when possible. If the host network manager or shared-header class cannot be found, it logs a failure and leaves the host unpatched.

### 3.2 Reflective host integration

`p001hx.AppNetwork.locate(Class<?>)` searches the resolved manager class for:

- a Kotlin `Lazy` singleton;
- a field assignable to `okhttp3.OkHttpClient`;
- a field whose type starts with `retrofit2.`;
- the Retrofit call-factory field;
- the Retrofit `okhttp3.HttpUrl` base URL.

`AppNetwork.useClient()` replaces both the manager's OkHttp client field and Retrofit's call factory, restoring the original client if the second replacement fails. The exact class members, Retrofit service interfaces, DTO names, and host-app base URL value are deliberately not hard-coded in the patch and are absent from the normal decompilation.

**Confirmed absence:** no complete ordinary host API interface catalog with `@GET`, `@POST`, DTO classes, response annotations, or server error models was recovered from the three DEX files. The patch accesses the host implementation indirectly and independently constructs only the calls described below.

## 4. API hosts and route catalog

### 4.1 Directly constructed API calls

The following are confirmed by executable patch code. The base host is selected from the fallback list in `CloudFrontPlayInfoClient` unless the host app's Retrofit base URL is used by `ApiClient` for intercepted host calls.

| Method | Host | Path | Query/body observed | Feature | Evidence |
|---|---|---|---|---|---|
| `POST` | each selected `api*.aoneroom.com` host | `/wefeed-mobile-bff/user-api/visitor-login` | body `{}` | visitor authentication | `CloudFrontPlayInfoClient.visitorToken()` |
| `GET` | selected `api*.aoneroom.com` host | `/wefeed-mobile-bff/subject-api/play-info/v2` | `subjectId`, `se`, `ep`, `isVip=true` | play metadata | `MovieBoxSource.resource()` and play-info lookup |
| `GET` | host app Retrofit base | `/wefeed-mobile-bff/subject-api/resource` | `subjectId`, `page=1`, `perPage=8`, `all=0`, `startPosition=1`, `endPosition=1`, `pagerMode=2`, `resolution`, `se`, `epFrom`, `epTo` | direct-file/resource list | `DirectFiles.fetchList()` |
| `GET` | host app Retrofit base | `/wefeed-mobile-bff/subject-api/resource-position/v2` | `subjectId`, `resourceId`, `failUrl`, `failCode=404`, `resourceNum=0` | direct-file position lookup | `DirectFiles.fetchFirstPosition()` |
| `GET` | host app Retrofit base | `/wefeed-mobile-bff/subject-api/resource-position` | same as above | fallback direct-file lookup | `DirectFiles.fetchFirstPosition()` |

The patch also recognizes and rewrites these host-app response paths, but does not independently implement their server calls:

| Path | Observed operation |
|---|---|
| `/wefeed-mobile-bff/subject-api/bottom-tab` | removes configured sub-tabs |
| `/wefeed-mobile-bff/user-api/profile` | recursively changes objects containing `daysLeft`, `memberType`, and `expiryDate` |
| `/wefeed-mobile-bff/vip/member/detail` | same membership-object rewrite |
| `/wefeed-mobile-bff/tab-operating-page` | removes selected feed promotions |
| `/wefeed-mobile-bff/activity/marketing/sku-promotion` | replaces non-null `data` with JSON null |
| `/wefeed-mobile-bff/subject-api/resource/v2` | first internally downgraded to the non-`/v2` list URL for the request, then resource-list response may be rewritten |
| `/wefeed-mobile-bff/subject-api/play-info/v2` | response is converted to manifest/local-loopback URLs |
| `/wefeed-mobile-bff/subject-api/resource-position` | request is tagged as a direct-file lookup and may receive a carrier field in client info |

### 4.2 API-host fallback and retry behavior

The independent play-info client tries these hosts:

1. `api6.aoneroom.com`
2. `api4sg.aoneroom.com`
3. `api5.aoneroom.com`
4. `api4.aoneroom.com`
5. `api3.aoneroom.com`

It launches one daemon lookup thread per host, waits up to 5,000 ms for an accepted response, caches visitor tokens per host, and applies a 600,000 ms cooldown to failed/slow hosts. A successful response is accepted only if the `PlayInfoAcceptance` predicate accepts it; otherwise the code falls back to the host app's response/edge-cache path.

## 5. Headers, tokens, and signing

### 5.1 Gateway headers

`GatewayHeaders.create()` emits the following headers for independently constructed API requests:

| Header | Confirmed value/algorithm |
|---|---|
| `Accept` | `application/json` |
| `Content-Type` | `application/json` |
| `x-client-token` | `<unixMillis>,<MD5(reverse(unixMillis))>` |
| `x-tr-signature` | HMAC-MD5 signature described below |
| `x-client-info` | installed-app client JSON, optionally augmented with build tag |
| `X-Client-Build` | rotating build tag when available |
| `x-forwarded-for` | literal `unknown` |
| `Authorization` | `Bearer <visitor-token>` when a token is available |

The manifest contains metadata entries named `gateway_secret_test` and `gateway_secret_online`. Their values are intentionally redacted here. The report identifies the key names and algorithm only; it does not reproduce embedded secret material or captured tokens.

### 5.2 Signature construction

`GatewaySignature.create(key, method, pathAndQuery, body, timestamp)` constructs the UTF-8 signing string:

```text
<HTTP method>\n
application/json\n
application/json\n
<body byte length or empty>\n
<timestamp millis>\n
<MD5(body bytes) or empty>\n
<canonical path and query>
```

The MAC is `HmacMD5` using the redacted gateway secret. The output format is:

```text
<timestamp>|2|<Base64-without-padding HMAC-MD5>
```

Canonicalization preserves the path, URL-decodes each query key/value pair, sorts parameters by decoded key, and joins them with `&`. The method, content type, content length, timestamp, body digest, and canonical path are therefore all signed.

### 5.3 Host-app build tagging

`BuildTagInterceptor` adds/refreshes `X-Client-Build` and updates `X-Client-Info`. For `/subject-api/resource-position`, it adds `sp_code: "21403"` to the JSON client-info object when parseable. It checks stream-list responses for an upgrade notice containing `aoneroom.com/other/`; when found, it replaces the device identity and repeats the request with a stock certificate/build tag.

## 6. Request and response contracts recoverable from code

The following are JSON shapes inferred from explicit field access and mutation. They are not server-validated OpenAPI contracts.

### 6.1 Visitor login

Request:

```json
{}
```

The code requires this response shape:

```json
{
  "data": {
    "token": "<visitor token>"
  }
}
```

The token is cached by API host and sent as `Authorization: Bearer ...` for play-info requests.

### 6.2 Play-info

The code reads:

```json
{
  "data": {
    "streams": [
      {
        "signCookie": "<CloudFront- or Edge-Cache cookie>",
        "url": "<progressive or manifest URL>",
        "resourceLink": "<optional URL>",
        "downloadUrl": "<optional URL>",
        "playUrl": "<optional URL>",
        "resolutions": "360,720",
        "size": 123456,
        "format": "MP4"
      }
    ]
  }
}
```

Observed fields include `signCookie`, `url`, `resourceLink`, `downloadUrl`, `playUrl`, `resolutions`, `size`, `duration`, `codecName`, `id`, and `idType`. A signed cookie is converted to a DASH `index.mpd` URL. If a progressive stream has no sign cookie and a loopback server is available, its `url` is replaced with a local URL.

### 6.3 Resource list

The code expects:

```json
{
  "data": {
    "subjectId": "12345",
    "list": [
      {
        "resourceId": "resource-1",
        "se": 1,
        "ep": 1,
        "resolution": 720,
        "resourceLink": "https://origin.example/file.mp4",
        "size": 123456
      }
    ]
  }
}
```

`ResourceListRewriter` replaces each valid `resourceLink` with a loopback URL containing subject, season, episode, resolution, origin, size, and optional resource ID. `DirectFiles` uses `resourceLink`, `resourceId`, `resolution`, `duration`, and related fields to construct MP4 play-info.

### 6.4 Direct-file synthesized play-info

When a direct resource is resolved, `DirectFiles` can synthesize an object of this form:

```json
{
  "format": "MP4",
  "id": "<subject id>",
  "url": "<direct resource URL>",
  "resolutions": "720",
  "size": "<size>",
  "duration": 0,
  "codecName": "<codec if present>",
  "signCookie": "",
  "idType": ""
}
```

This shape is generated from code and is not evidence that the backend always returns exactly this object.

### 6.5 Membership and promotion mutations

For every recursively visited JSON object containing all three keys `daysLeft`, `memberType`, and `expiryDate`, the patch writes:

```json
{
  "daysLeft": 9999,
  "memberType": "<unchanged>",
  "expiryDate": "2099-12-31"
}
```

For SKU promotion responses, a non-null top-level `data` is changed to JSON null. For operating-page/feed data, web links and promotion targets beginning at `/member`, `/web`, or `/profile` are removed from `BANNER`, `CUSTOM`, and `SINGLE_IMAGE` structures.

## 7. Signed-cookie and playback flow

### 7.1 Cookie parsing

`SignedResource` supports two cookie styles:

- `CloudFront-Policy=<base64-like policy>`: decodes the policy, reads `Statement[0].Resource`, requires an HTTPS resource ending in `/*`, and appends `index.mpd`.
- `Edge-Cache-Cookie=<...;urlprefix=<base64>:...>`: decodes `urlprefix`, requires HTTPS, ensures a trailing slash, and appends `index.mpd`.

The cookie itself is retained for CDN requests. `MovieBoxSource` caches signed resources by `subjectId/season/episode` and refreshes them if a CDN returns 403 or if the signed resource moves.

### 7.2 DASH parsing and MP4 generation

`DashFile.open()` downloads and parses the DASH manifest, selects a video representation at or below the requested height (falling back to the smallest available representation) and the highest suitable audio representation, resolves segment timelines, downloads initialization segments, reads MP4 track metadata, interleaves audio/video tracks, and exposes a synthetic MP4 byte layout. It supports byte-range reads and parallel segment fetching.

`CdnClient` uses `HttpURLConnection` with:

- connect timeout: 15 seconds;
- read timeout: 30 seconds;
- up to 5 attempts with 2-second delay;
- `Cookie` on every request;
- optional `Range` header;
- one cookie refresh after HTTP 403;
- acceptance of HTTP 200 or 206 only.

`DashFile` uses a special unpaced-CDN mode for host `sacdn.hakunaymatata.com`, with 8 parallel reads and 256 KiB chunks; the normal/paced mode uses 16 parallel reads and 48 KiB chunks. It verifies `Content-Range` for every ranged CDN response.

### 7.3 Local loopback server

On the main process, `DashServer` binds only to `127.0.0.1:47291`, with an eight-worker pool and queue depth 16. Generated URLs have this form:

```text
http://127.0.0.1:47291/dash/<subjectId>/<season>/<episode>/<height>.mp4?origin=<encoded-origin>&size=<origin-size>&resourceId=<optional-id>
```

Only `GET` and `HEAD` are accepted. The path must match numeric subject/season/episode/height components. Validated `origin` must be HTTP(S); invalid values are discarded. Range behavior returns:

| Condition | Status |
|---|---|
| full file | `200 OK` |
| satisfiable range | `206 Partial Content` with `Content-Range` |
| unsatisfiable range | `416 Range Not Satisfiable` |
| no local file but origin available | `302 Found` to origin |
| no file/no origin | `404 Not Found` |
| I/O failure | `503 Service Unavailable` |

Responses include `Accept-Ranges: bytes`, `Content-Type: video/mp4`, and `Connection: close`.

## 8. Interceptor and response-rewrite flow

`DashInterceptor` runs after the build-tag interceptor. It first detects `/subject-api/resource/v2` requests and internally changes them to the non-v2 resource-list URL while preserving query parameters except `isVip`. It then classifies the response path:

| Classification | Rewrite |
|---|---|
| play-info | replace edge-cache play-info when an accepted unpaced response is available; otherwise convert signed URLs to manifests or route progressive streams through loopback |
| resource list | if downloads are routed, replace valid resource links with loopback URLs after resolving direct-file sizes |
| app tabs | remove sub-tabs listed by `assets/hx_hidden_home_tabs` |
| member info | extend matching membership objects |
| operating page | remove web/member/profile promotions |
| SKU promotion | null out `data` |
| other | unchanged |

The response body is replaced through the host OkHttp `ResponseBody` factory and `Content-Length` is removed so the rewritten byte count is recalculated by the client.

## 9. Complete assets and mini-app payloads

### 9.1 `appTab.json`

The embedded configuration contains bottom tabs for Home, NovelHub, ShortTV, Downloads, and Me. Home sub-tabs include Live, Trending, wajegame, Nollywood, Movie, TV, ShortTV, Football, Anime, Kids, Education, Music, Free Novels, and Game. Confirmed externally linked/deep-linked values include:

- `https://sportsnow.top/?currentTagId=0&currentTagName=&from=home_top_tab&utm_source=mb_app_inner`
- `https://waje.bet/activity/landing/cbc831?is_lazy_load=true`
- an `oneroom://com.community.oneroom` deep link to mini-app ID `1000681057309249536`, page `pages/bookstore/index`.

The numerous `pbcdn.aoneroom.com` URLs are image/CDN assets, not API endpoints.

### 9.2 `skulist.json`

The file contains `skuList: null` and three member-right entries: no disturbing ads, 720P quality videos, and multiple simultaneous downloads. The icons point to `test-acdn.aoneroom.com`; these are configuration images, not API routes.

### 9.3 Mini-app packages

Nested offline packages were extracted:

| Package | Manifest/signature | TAR size |
|---|---:|---:|
| `100000` | present | 803,328 bytes |
| `1000681057309249536` | present | 1,858,560 bytes |

Nested scans found mini-app/platform domains and resources including `api.byte-app.com`, `api-static.byte-app.com`, `app-oss.byte-app.com`, `api-dev.somodomain.com`, `api.eagllwin.com`, `api.hisavana.com`, `cm-oss.hisavana.com`, `datagateway.shalltry.com`, novel/CDN URLs, and JavaScript bridge assets. These are mini-app, ad/telemetry, or CDN surfaces unless a route is independently shown to be a core `aoneroom` backend route.

## 10. URL and domain classification

### Core API/gateway hosts

- `api6.aoneroom.com`
- `api4sg.aoneroom.com`
- `api5.aoneroom.com`
- `api4.aoneroom.com`
- `api3.aoneroom.com`
- `applet-api.aoneroom.com`
- `applet-api-test.aoneroom.com`

The first five are directly used by the recovered play-info gateway client. The applet hosts are present in mini-app/configuration material and are not proven to serve the core routes listed above.

### CDN, media, and asset hosts

- `pbcdn.aoneroom.com`
- `test-acdn.aoneroom.com`
- `sacdn.hakunaymatata.com`
- `nacdn.novelhubapp.com`
- `static.novelhubapp.com`
- `transsion-wefeed-novel-prod-alifk.oss-eu-central-1.aliyuncs.com`
- `app-oss.byte-app.com`
- `app-manage-oss.sunnbird.com`

### Mini-app, advertising, telemetry, or third-party hosts

- `api.byte-app.com`
- `api-static.byte-app.com`
- `api-dev.somodomain.com`
- `api.hisavana.com`
- `cm-oss.hisavana.com`
- `datagateway.shalltry.com`
- `cloud-config-service-pre.rtc.aliyuncs.com`
- `waje.bet`
- `sportsnow.top`

The complete raw URL scans also include documentation/library URLs, Android/LLVM links, and third-party SDK references. They are not treated as product API endpoints without executable call-site evidence.

## 11. Internal Android route map

`assets/therouter/routeMap.json` contains internal activity/provider/service routes, including login, download, web, stream detail, movie detail, profile, member, sports, search, room, rewards, short-TV, and audio routes. Examples:

- `/loginapi/select_country` → `LoginSelectCountryActivity`
- `/Login/Api` → `LoginProvider`
- `/download/provider` → `DownloadManagerApi`
- `/web/web` → `WebActivity`
- `/stream/details` → `StreamDetailServiceImpl`
- `/playvideo/detail` → `StreamDetailActivity`
- `/movie/details` → `MovieDetailServiceImpl`
- `/member/MemberProvider` → `MemberProvider`
- `/main/tab` → `MainActivity`

These are Android routing paths, not HTTP API endpoints. Their presence does not establish a network URL, request schema, or backend database operation.

## 12. End-to-end flow reconstructed from executable evidence

1. Application startup loads the `hx` overlay/factory and schedules `AppPatch` installation.
2. The installer resolves `fh.c`, obtains its singleton, locates the host OkHttp client and Retrofit instance, and reads the runtime base URL.
3. It obtains shared headers from `eh.b`, creates a rotating build tag from installed-app information, and installs build-tag plus DASH interceptors.
4. For play-info, the patch may issue visitor login independently against each ready API host. The token is cached per host.
5. The patch requests play-info with signed gateway headers and bearer token, races the hosts for up to five seconds, and accepts an unpaced response if its predicate passes.
6. The host or patch returns play-info. Signed cookies are parsed to a DASH manifest URL; direct-file data is used when no signed DASH stream is available.
7. Response rewriting converts stream/resource links to manifest URLs or local loopback URLs.
8. The player requests the loopback URL. `DashServer` resolves the resource, downloads/parses the DASH manifest and initialization segments, fetches ranged media segments with the signed cookie, interleaves tracks, and serves MP4 bytes/ranges.
9. HTTP 403 triggers cookie refresh; failed CDN reads retry up to five times. Missing local resources may redirect to the original origin.
10. Selected home/member/promotion responses are rewritten before returning to the host app.

## 13. Confirmed facts versus architectural assumptions

### Confirmed directly

- APK identity, size, checksum, ZIP validity, DEX/resource/asset/native inventory.
- Patch classes, asset hook descriptors, reflective class names, API path literals, host fallback list.
- Header names, HMAC-MD5 algorithm, timestamp/token formats, canonical-query behavior, and secret metadata key names.
- Visitor-login body `{}`, token response access path, play-info query parameters, resource-list and resource-position query construction.
- Signed-cookie parsing, DASH manifest selection, CDN retry/range/cookie behavior, loopback port/path/status behavior.
- Response fields accessed and exact JSON mutations performed by the patch.
- Mini-app package IDs, manifests/signatures, configuration URLs, and internal Android route mappings.

### Not present or not provable from the APK

- The complete host-app Retrofit interface set and annotation metadata.
- Exact host-app API base URL value at runtime; the patch reads it reflectively.
- Complete DTO/model definitions for the host app, including nullability and server validation constraints.
- Authoritative HTTP status/error schemas for core backend routes beyond locally handled statuses and generic `IOException` behavior.
- Backend database tables, foreign keys, cache technology, queue workers, and server-side business rules.
- Token issuance internals, token expiry/refresh semantics, and server-side signature verification code.
- A complete list of all network traffic generated by every embedded SDK, mini-app, and native library.
- Any claim that embedded test CDN URLs or ad/mini-app URLs are core API endpoints.

## 14. Recommended next evidence for a complete backend specification

To turn this client-side reverse-engineering result into a server-validated API contract, capture traffic from a controlled test device or emulator for startup, visitor login, home tabs, profile/member detail, play-info, resource list, resource-position fallback, and a DASH playback session. Preserve method, full URL, request headers excluding secrets, request body, response status, response headers, and redacted JSON. Multiple subjects, VIP/non-VIP states, direct-file and DASH streams, expired cookies, and error responses are required to establish optionality, pagination, status codes, and error schemas.

## 15. Reproducibility paths

- Complete APK: `/home/ubuntu/apk_full_complete/efmd30_full.apk`
- Extracted APK: `/home/ubuntu/apk_full_complete/unpacked`
- JADX output: `/home/ubuntu/apk_full_complete/jadx_full`
- Nested mini-app extraction: `/home/ubuntu/apk_full_complete/nested_extracted`
- Scan outputs: `/home/ubuntu/apk_full_complete/scans`
- Manifest: `/home/ubuntu/apk_full_complete/jadx_full/resources/AndroidManifest.xml`
- Relevant assets: `assets/hx.*`, `assets/appTab.json`, `assets/skulist.json`, `assets/therouter/routeMap.json`
