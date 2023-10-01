package com.jobosint.client;

import com.github.mizosoft.methanol.MoreBodyHandlers;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@Slf4j
public class LinkedInHttpClient {


    private HttpClient getClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    // see: https://github.com/tfSheol/lnkds/blob/master/src/voyager.rs

    private HttpClient getVoyagerClient() {
        // https://www.linkedin.com/voyager/api/graphql
        // ?includeWebMetadata=true
        // &variables=(keywords:United%20States,query:(typeaheadFilterQuery:(geoSearchTypes:List(POSTCODE_1,POSTCODE_2,POPULATED_PLACE,ADMIN_DIVISION_1,ADMIN_DIVISION_2,COUNTRY_REGION,MARKET_AREA,COUNTRY_CLUSTER)),typeaheadUseCase:JOBS),type:GEO)
        // &&queryId=voyagerSearchDashReusableTypeahead.1043b2d44b336397a7560ac3243a89a0
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    // Just a random 19 digit number
    private String generateLinkedInCsrfToken() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(19);

        // Generate the first digit between 1 and 9 (inclusive)
        sb.append(rand.nextInt(9) + 1);

        // Generate the remaining digits between 0 and 9 (inclusive)
        for (int i = 1; i < 19; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    public void testVoyager() throws URISyntaxException, IOException, InterruptedException {

        // TODO how to get &&queryId=voyagerSearchDashReusableTypeahead.1043b2d44b336397a7560ac3243a89a0

        // TODO how to get this value?
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

        HttpResponse<String> response = getClient()
                .send(request, MoreBodyHandlers.decoding(HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));

        log.info(String.valueOf(response.statusCode()));

        var filePath = Path.of("linkedin-response-" + csrfNumber + ".json");
        try {
            Files.writeString(filePath, response.body());
            log.info("Wrote {}", filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void test() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI("https://digitalsanctum.com"))
                .GET().build();

        HttpResponse<String> response = getClient().send(request, HttpResponse.BodyHandlers.ofString());

        log.info(response.body());
    }
}
// https://www.linkedin.com/voyager/api/graphql?includeWebMetadata=true&variables=(organizationUrn:urn%3Ali%3Afsd_company%3A1406226)&&queryId=voyagerJobsTalentBrandDashOrganizationCommitments.1fde19686e00bd632ab0b65190ad5a80

// https://www.linkedin.com/voyager/api/graphql?includeWebMetadata=false&variables=(keywords:United%20States,query:(typeaheadFilterQuery:(geoSearchTypes:List(POSTCODE_1,POSTCODE_2,POPULATED_PLACE,ADMIN_DIVISION_1,ADMIN_DIVISION_2,COUNTRY_REGION,MARKET_AREA,COUNTRY_CLUSTER)),typeaheadUseCase:JOBS),type:GEO)&&queryId=voyagerSearchDashReusableTypeahead.1043b2d44b336397a7560ac3243a89a0

// https://www.linkedin.com/voyager/api/graphql?variables=(count:24,jobCollectionSlug:recommended,query:(origin:GENERIC_JOB_COLLECTIONS_LANDING),start:24)&&queryId=voyagerJobsDashJobCards.7f620806e019034bcecb1ee5710bbc93


//bcookie="v=2&b90a768b-549f-4b00-8076-cc0de72c7582"; bscookie="v=1&2023090300235789e27726-3642-416f-8c90-8e8397cd3549AQETExzLNq_sixgKJ96959DOl-ASQFYt"; _gcl_au=1.1.147968107.1693700638; aam_uuid=17825509645987542710588835426652234410; g_state={"i_l":0}; timezone=America/Los_Angeles; li_theme=light; li_theme_set=app; li_sugr=5dc5d558-4d49-4cb0-a019-3226c28e18c1; _guid=f77f9ea5-9694-420b-998a-897ed3ed5eb8; AnalyticsSyncHistory=AQKNiTXmBCJMhwAAAYpYbOEFmy1HGf0Ep3R2eUoc1TzyZumBt9e8_MfRHznKD8qlIh3LJ342tlTJIGHVc9sl9A; lms_ads=AQGPJ2kFzh8hcAAAAYpYbOGA43vxSEd-MJe3oIxowJYb5sXP7gfCAPgmuwOE1Y0r7viBvOk-utpbq4LgPPseTkPMHYeIgrei; lms_analytics=AQGPJ2kFzh8hcAAAAYpYbOGA43vxSEd-MJe3oIxowJYb5sXP7gfCAPgmuwOE1Y0r7viBvOk-utpbq4LgPPseTkPMHYeIgrei; li_rm=AQGCfjeIBav4dgAAAYpnKbMDei3ZJ6qjNu8fvyy4QKieJOAGpDnnQK54nV7V1ijFyb9XJgtHAXh91T2NfZb0Wh_9sE4-U7WHXHc7xWOtvFWxdcbn_rDL_EJC; li_g_recent_logout=v=1&true; visit=v=1&M; AMCVS_14215E3D5995C57C0A495C55%40AdobeOrg=1; AMCV_14215E3D5995C57C0A495C55%40AdobeOrg=-637568504%7CMCIDTS%7C19606%7CMCMID%7C17619757192709877960640876258824507745%7CMCAAMLH-1694552708%7C9%7CMCAAMB-1694552708%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1693955108s%7CNONE%7CvVersion%7C5.1.1; lang=v=2&lang=en-us; liap=true; li_at=AQEDAQBxrqwE-9bcAAABimc0SnMAAAGKi0DOc04AjHbSMaf3BKz1gDm71fI8QCW4klk_KSblgVHalRyywRyry3Z9Izn-RXvx0nTqCO1gomSf3Wtc51WzkqXYrRlewzld66qtBB7qyU16H8IWwxNZhpNy; JSESSIONID="ajax:2647583767201524909"; UserMatchHistory=AQLZvWljyB9L8AAAAYpnNIrm5LfcWYaheWGqYxFsm1sSokKvbnmnRNmRV4BRU2HO0mXfK--hu63MbhYl6UTZbn3A0S8GNcYi_1gRtU_vMjsCm6Qoy97rv0DouYANtCx3kVVmHbT9kcxHED6ABBskC_u70pdesvaN74jqB310ixx_TdjzEg7ptTm__aHpLArs72GdnBFXlstQJYFGZDCOxgvvBb27i-IPZvKBomIp_Pk4Wyj0A-QywGegl0zEjNrkMzj_scDQMbk6q3KMAiDbBXKS8WALWmp4NUvMJ3NLjrAeo_VIQSLitU-nXBK7G-fkQPbOQh1fnnIvkpcts2V-oWMsmuolStM; lidc="b=OB84:s=O:r=O:a=O:p=O:g=2995:u=892:x=1:i=1693948609:t=1693988127:v=2:sig=AQECW2JkL5Prlr7bPNtChiHZu0Pm2bAb"; sdsc=22%3A1%2C1693948612735%7EJAPP%2C0Axj5b8YeF1q9hmEE5k0xmwKyXN0%3D


// company

// curl 'https://www.linkedin.com/voyager/api/graphql?includeWebMetadata=true&variables=(universalName:apple)&&queryId=voyagerOrganizationDashCompanies.14020cba5d6e7118597eb6a8f5727a35' \
//  -H 'authority: www.linkedin.com' \
//  -H 'accept: application/vnd.linkedin.normalized+json+2.1' \
//  -H 'accept-language: en-US,en;q=0.9' \
//  -H 'cache-control: no-cache' \
//  -H 'cookie: bcookie="v=2&b90a768b-549f-4b00-8076-cc0de72c7582"; bscookie="v=1&2023090300235789e27726-3642-416f-8c90-8e8397cd3549AQETExzLNq_sixgKJ96959DOl-ASQFYt"; _gcl_au=1.1.147968107.1693700638; aam_uuid=17825509645987542710588835426652234410; g_state={"i_l":0}; timezone=America/Los_Angeles; li_theme=light; li_theme_set=app; li_sugr=5dc5d558-4d49-4cb0-a019-3226c28e18c1; _guid=f77f9ea5-9694-420b-998a-897ed3ed5eb8; li_rm=AQGCfjeIBav4dgAAAYpnKbMDei3ZJ6qjNu8fvyy4QKieJOAGpDnnQK54nV7V1ijFyb9XJgtHAXh91T2NfZb0Wh_9sE4-U7WHXHc7xWOtvFWxdcbn_rDL_EJC; visit=v=1&M; AMCV_14215E3D5995C57C0A495C55%40AdobeOrg=-637568504%7CMCIDTS%7C19606%7CMCMID%7C17619757192709877960640876258824507745%7CMCAAMLH-1694552708%7C9%7CMCAAMB-1694552708%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1693955108s%7CNONE%7CvVersion%7C5.1.1; liap=true; li_at=AQEDAQBxrqwE-9bcAAABimc0SnMAAAGKi0DOc04AjHbSMaf3BKz1gDm71fI8QCW4klk_KSblgVHalRyywRyry3Z9Izn-RXvx0nTqCO1gomSf3Wtc51WzkqXYrRlewzld66qtBB7qyU16H8IWwxNZhpNy; JSESSIONID="ajax:2647583767201524909"; lang=v=2&lang=en-us; sdsc=1%3A1SZM1shxDNbLt36wZwCgPgvN58iw%3D; AnalyticsSyncHistory=AQJOJgij5wmOJAAAAYpq-6zRl5Jf4xADDGsu3tDiV87e5VIPI918GJq9_ai17sPV3NreCdFwNd1TveHHgaybzA; lms_ads=AQFxQV6OKeVucgAAAYpq-61ze4lClMa_qKihu66X43kraTRZKnPAbXyAKq4Y_qBmWtPFzB46CQNjjLeBvZIEiepJ7GExhcgF; lms_analytics=AQFxQV6OKeVucgAAAYpq-61ze4lClMa_qKihu66X43kraTRZKnPAbXyAKq4Y_qBmWtPFzB46CQNjjLeBvZIEiepJ7GExhcgF; UserMatchHistory=AQIJbWEoqe7BUwAAAYpq_nymXW25GQuU9ywSCNhSQSIayV9WqLx3UBCX_A8kk2fFZ90Op0pOOrP4iLGH6f49MsC2ghbFiMckcv3uo6pb97eKMgLdo8c8t9vgfifxA0cZmvVs0HrXeNEXAxAuKyBkcbFXsAezgUff3rgiY-Oytd14F7z6SUhAFD7iB62NNB23kWaqw7dnEhhtV37YpAMt4_rzFJbPFbcu27kZ2f_uwM1B1fMAnpVinDGoBHRx7WEXUmbfDq337pOQkUEUsQ3LEdoyO1z9HMBmIjVR5YzukYxCFjTXUSzyc1U31x5xiYiw2NDrkAzvGKAnMJrKVCOq0tTtVOp5Sj0; lidc="b=OB84:s=O:r=O:a=O:p=O:g=2995:u=892:x=1:i=1694012177:t=1694074535:v=2:sig=AQHW2gGRcMakvI7Ffd9Vf1Wb7X38i03P"' \
//  -H 'csrf-token: ajax:2647583767201524909' \
//  -H 'pragma: no-cache' \
//  -H 'referer: https://www.linkedin.com/search/results/companies/?heroEntityKey=urn%3Ali%3Aorganization%3A162479&keywords=apple&origin=SWITCH_SEARCH_VERTICAL&position=0&searchId=f9a853a5-f810-4cca-8051-02465c26532c&sid=Hkp' \
//  -H 'sec-ch-ua: "Chromium";v="116", "Not)A;Brand";v="24", "Google Chrome";v="116"' \
//  -H 'sec-ch-ua-mobile: ?0' \
//  -H 'sec-ch-ua-platform: "Linux"' \
//  -H 'sec-fetch-dest: empty' \
//  -H 'sec-fetch-mode: cors' \
//  -H 'sec-fetch-site: same-origin' \
//  -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36' \
//  -H 'x-li-lang: en_US' \
//  -H 'x-li-page-instance: urn:li:page:d_flagship3_company;knCWufqxQFqFI72cMufJWQ==' \
//  -H 'x-li-pem-metadata: Voyager - Organization - Member=organization-top-card,Voyager - Organization - Member=organization-affiliated-pages' \
//  -H 'x-li-track: {"clientVersion":"1.13.2615","mpVersion":"1.13.2615","osName":"web","timezoneOffset":-7,"timezone":"America/Los_Angeles","deviceFormFactor":"DESKTOP","mpName":"voyager-web","displayDensity":1,"displayWidth":3840,"displayHeight":2160}' \
//  -H 'x-restli-protocol-version: 2.0.0' \
//  --compressed