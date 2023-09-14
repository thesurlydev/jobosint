# LinkedIn Notes

## References
* https://github.com/nsandman/linkedin-api


## Auth

```java
String li_at = "AQEDAQBxrqwE-9bcAAABimc0SnMAAAGKi0DOc04AjHbSMaf3BKz1gDm71fI8QCW4klk_KSblgVHalRyywRyry3Z9Izn-RXvx0nTqCO1gomSf3Wtc51WzkqXYrRlewzld66qtBB7qyU16H8IWwxNZhpNy";
String csrfNumber = generateLinkedInCsrfToken();
String csrfToken = "ajax:" + csrfNumber;
String jSessionCookie = "JSESSIONID=" + csrfToken;
String cookieHeaderVal = "li_at=" + li_at + "; " + jSessionCookie;

HttpRequest request = HttpRequest.newBuilder(new URI("https://www.linkedin.com/voyager/api/graphql?includeWebMetadata=false&variables=(keywords:United%20States,query:(typeaheadFilterQuery:(geoSearchTypes:List(POSTCODE_1,POSTCODE_2,POPULATED_PLACE,ADMIN_DIVISION_1,ADMIN_DIVISION_2,COUNTRY_REGION,MARKET_AREA,COUNTRY_CLUSTER)),typeaheadUseCase:JOBS),type:GEO)&&queryId=voyagerSearchDashReusableTypeahead.1043b2d44b336397a7560ac3243a89a0"))
        .header("accept", "application/vnd.linkedin.normalized+json+2.1")
        .header("accept-encoding", "gzip, deflate, br")
        .header("cache-control", "no-cache")
        .header("content-type", "application/json; charset=utf-8")
        .header("cookie", cookieHeaderVal)
        .header("csrf-token", csrfToken)
        .header("pragma", "no-cache")
        .GET().build();
```


## Common Headers



## Company About

Browser URL: https://www.linkedin.com/company/apple/about/

```shell
curl 'https://www.linkedin.com/voyager/api/graphql?includeWebMetadata=true&variables=(universalName:apple)&&queryId=voyagerOrganizationDashCompanies.14020cba5d6e7118597eb6a8f5727a35' \
  -H 'authority: www.linkedin.com' \
  -H 'accept: application/vnd.linkedin.normalized+json+2.1' \
  -H 'accept-language: en-US,en;q=0.9' \
  -H 'cache-control: no-cache' \
  -H 'cookie: bcookie="v=2&b90a768b-549f-4b00-8076-cc0de72c7582"; bscookie="v=1&2023090300235789e27726-3642-416f-8c90-8e8397cd3549AQETExzLNq_sixgKJ96959DOl-ASQFYt"; _gcl_au=1.1.147968107.1693700638; aam_uuid=17825509645987542710588835426652234410; g_state={"i_l":0}; timezone=America/Los_Angeles; li_theme=light; li_theme_set=app; li_sugr=5dc5d558-4d49-4cb0-a019-3226c28e18c1; _guid=f77f9ea5-9694-420b-998a-897ed3ed5eb8; li_rm=AQGCfjeIBav4dgAAAYpnKbMDei3ZJ6qjNu8fvyy4QKieJOAGpDnnQK54nV7V1ijFyb9XJgtHAXh91T2NfZb0Wh_9sE4-U7WHXHc7xWOtvFWxdcbn_rDL_EJC; visit=v=1&M; AMCV_14215E3D5995C57C0A495C55%40AdobeOrg=-637568504%7CMCIDTS%7C19606%7CMCMID%7C17619757192709877960640876258824507745%7CMCAAMLH-1694552708%7C9%7CMCAAMB-1694552708%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1693955108s%7CNONE%7CvVersion%7C5.1.1; liap=true; li_at=AQEDAQBxrqwE-9bcAAABimc0SnMAAAGKi0DOc04AjHbSMaf3BKz1gDm71fI8QCW4klk_KSblgVHalRyywRyry3Z9Izn-RXvx0nTqCO1gomSf3Wtc51WzkqXYrRlewzld66qtBB7qyU16H8IWwxNZhpNy; JSESSIONID="ajax:2647583767201524909"; lang=v=2&lang=en-us; AnalyticsSyncHistory=AQJOJgij5wmOJAAAAYpq-6zRl5Jf4xADDGsu3tDiV87e5VIPI918GJq9_ai17sPV3NreCdFwNd1TveHHgaybzA; lms_ads=AQFxQV6OKeVucgAAAYpq-61ze4lClMa_qKihu66X43kraTRZKnPAbXyAKq4Y_qBmWtPFzB46CQNjjLeBvZIEiepJ7GExhcgF; lms_analytics=AQFxQV6OKeVucgAAAYpq-61ze4lClMa_qKihu66X43kraTRZKnPAbXyAKq4Y_qBmWtPFzB46CQNjjLeBvZIEiepJ7GExhcgF; sdsc=22%3A1%2C1694012960299%7EJAPP%2C0HsaEBvc4hmqwY%2FpRdckSB9e0RbE%3D; UserMatchHistory=AQIUKzofBS6FvQAAAYprDPoyoA9C8wP_UYMPMuBuYRD6OYyuP-gg-91-bnji6nNvJGckwtdUKpUR2a578X1qlHeSzZrFoyuRnsipkKi8SFecRBU3ahAQAOugI15s5-38fxUh654kMvIUEZzqkROcfpw0Ki85N3YjK3TtwyD2Y0tsxor0LjlppLQdUcjd4qr2FTP9tM-xbQuMq69f_tEwNbSDachjbUeCJFHJMBY1PluzcIyNkVRbFEO0bZDfHlt7q45fjxZKlqZSf3FF3_caW8YRCMPBnq7FVo9aJ_eJuW7hDLV1dDlFax9gFxJiAZyzTZ3D6W51QRuIXLf4kFjY-8cwdnMYmv4; lidc="b=OB84:s=O:r=O:a=O:p=O:g=2995:u=892:x=1:i=1694013127:t=1694074535:v=2:sig=AQEycSXHf7ECld49DLQe5rEd1pD5Xm0n"' \
  -H 'csrf-token: ajax:2647583767201524909' \
  -H 'pragma: no-cache' \
  -H 'referer: https://www.linkedin.com/company/apple/about/' \
  -H 'sec-ch-ua: "Chromium";v="116", "Not)A;Brand";v="24", "Google Chrome";v="116"' \
  -H 'sec-ch-ua-mobile: ?0' \
  -H 'sec-ch-ua-platform: "Linux"' \
  -H 'sec-fetch-dest: empty' \
  -H 'sec-fetch-mode: cors' \
  -H 'sec-fetch-site: same-origin' \
  -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36' \
  -H 'x-li-lang: en_US' \
  -H 'x-li-page-instance: urn:li:page:d_flagship3_company;XqQJL/FYQRqZc8WdBUy7nw==' \
  -H 'x-li-pem-metadata: Voyager - Organization - Member=organization-top-card,Voyager - Organization - Member=organization-affiliated-pages' \
  -H 'x-li-track: {"clientVersion":"1.13.2615","mpVersion":"1.13.2615","osName":"web","timezoneOffset":-7,"timezone":"America/Los_Angeles","deviceFormFactor":"DESKTOP","mpName":"voyager-web","displayDensity":1,"displayWidth":3840,"displayHeight":2160}' \
  -H 'x-restli-protocol-version: 2.0.0' \
  --compressed
```

Response: [linkedin-responses/1.json](linkedin-responses/1.json)

```shell
curl 'https://www.linkedin.com/voyager/api/graphql?includeWebMetadata=true&variables=(memberTabType:ABOUT,organizationVanityName:apple)&&queryId=voyagerOrganizationDashDiscoverCardGroups.d2d2a841390299fe41efe333600cee79' \
  -H 'authority: www.linkedin.com' \
  -H 'accept: application/vnd.linkedin.normalized+json+2.1' \
  -H 'accept-language: en-US,en;q=0.9' \
  -H 'cache-control: no-cache' \
  -H 'cookie: bcookie="v=2&b90a768b-549f-4b00-8076-cc0de72c7582"; bscookie="v=1&2023090300235789e27726-3642-416f-8c90-8e8397cd3549AQETExzLNq_sixgKJ96959DOl-ASQFYt"; _gcl_au=1.1.147968107.1693700638; aam_uuid=17825509645987542710588835426652234410; g_state={"i_l":0}; timezone=America/Los_Angeles; li_theme=light; li_theme_set=app; li_sugr=5dc5d558-4d49-4cb0-a019-3226c28e18c1; _guid=f77f9ea5-9694-420b-998a-897ed3ed5eb8; li_rm=AQGCfjeIBav4dgAAAYpnKbMDei3ZJ6qjNu8fvyy4QKieJOAGpDnnQK54nV7V1ijFyb9XJgtHAXh91T2NfZb0Wh_9sE4-U7WHXHc7xWOtvFWxdcbn_rDL_EJC; visit=v=1&M; AMCV_14215E3D5995C57C0A495C55%40AdobeOrg=-637568504%7CMCIDTS%7C19606%7CMCMID%7C17619757192709877960640876258824507745%7CMCAAMLH-1694552708%7C9%7CMCAAMB-1694552708%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1693955108s%7CNONE%7CvVersion%7C5.1.1; liap=true; li_at=AQEDAQBxrqwE-9bcAAABimc0SnMAAAGKi0DOc04AjHbSMaf3BKz1gDm71fI8QCW4klk_KSblgVHalRyywRyry3Z9Izn-RXvx0nTqCO1gomSf3Wtc51WzkqXYrRlewzld66qtBB7qyU16H8IWwxNZhpNy; JSESSIONID="ajax:2647583767201524909"; lang=v=2&lang=en-us; AnalyticsSyncHistory=AQJOJgij5wmOJAAAAYpq-6zRl5Jf4xADDGsu3tDiV87e5VIPI918GJq9_ai17sPV3NreCdFwNd1TveHHgaybzA; lms_ads=AQFxQV6OKeVucgAAAYpq-61ze4lClMa_qKihu66X43kraTRZKnPAbXyAKq4Y_qBmWtPFzB46CQNjjLeBvZIEiepJ7GExhcgF; lms_analytics=AQFxQV6OKeVucgAAAYpq-61ze4lClMa_qKihu66X43kraTRZKnPAbXyAKq4Y_qBmWtPFzB46CQNjjLeBvZIEiepJ7GExhcgF; sdsc=22%3A1%2C1694012960299%7EJAPP%2C0HsaEBvc4hmqwY%2FpRdckSB9e0RbE%3D; UserMatchHistory=AQISFskdwzmeqgAAAYprDTq9MYx8zmqi7okBf52Gsu5crfFJlMRBMjvaEaa0f8ghgASup7vKAotfXqF8IK2s4ftV7F05Nkm30jq-fsAWLffdQU6LS16TLiIXHsJPLVhyzQGgX0H3CfpTd8gzs8AYRBtt0fcA5PS5hbRD9RiB4Apkqr6tjq8tn2Dc-7XQWDm0V00-5z2NFCipzK93_MMixXFnI3C2mEW-jTq3-_wGsJayPYPAiR5i4vl9sLKPYfLec4l1q9F9cfDG1gX4RinsCvnc2g5_5-acKc2xWOMAEMYK3YT__owvDYXBcsb9rM6b0whjUBTyDXtLQcvzoc8ommmFlWcCKew; lidc="b=OGST00:s=O:r=O:a=O:p=O:g=3071:u=1:x=1:i=1694013143:t=1694099543:v=2:sig=AQFFw_y2C_TuBj8LulCwzX1cKAvXOnAZ"' \
  -H 'csrf-token: ajax:2647583767201524909' \
  -H 'pragma: no-cache' \
  -H 'referer: https://www.linkedin.com/company/apple/about/' \
  -H 'sec-ch-ua: "Chromium";v="116", "Not)A;Brand";v="24", "Google Chrome";v="116"' \
  -H 'sec-ch-ua-mobile: ?0' \
  -H 'sec-ch-ua-platform: "Linux"' \
  -H 'sec-fetch-dest: empty' \
  -H 'sec-fetch-mode: cors' \
  -H 'sec-fetch-site: same-origin' \
  -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36' \
  -H 'x-li-lang: en_US' \
  -H 'x-li-page-instance: urn:li:page:d_flagship3_company;XqQJL/FYQRqZc8WdBUy7nw==' \
  -H 'x-li-pem-metadata: Voyager - Organization - Member=organization-discover-module' \
  -H 'x-li-track: {"clientVersion":"1.13.2615","mpVersion":"1.13.2615","osName":"web","timezoneOffset":-7,"timezone":"America/Los_Angeles","deviceFormFactor":"DESKTOP","mpName":"voyager-web","displayDensity":1,"displayWidth":3840,"displayHeight":2160}' \
  -H 'x-restli-protocol-version: 2.0.0' \
  --compressed
```

Response: [linkedin-responses/1.json](linkedin-responses/2.json)