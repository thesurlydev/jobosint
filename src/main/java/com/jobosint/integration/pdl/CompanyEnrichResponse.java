package com.jobosint.integration.pdl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyEnrichResponse(String display_name, String size, String founded, String summary, Location location, String linkedin_url) {

    @Data
    public static class Location {
        private String metro;
    }
}

/*
{
  "status": 200,
  "name": "stripe",
  "display_name": "Stripe",
  "size": "1001-5000",
  "employee_count": 7458,
  "id": "stripe",
  "founded": 2010,
  "industry": "internet",
  "naics": [
    {
      "naics_code": "5112",
      "sector": "information",
      "sub_sector": "publishing industries (except internet)",
      "industry_group": "software publishers",
      "naics_industry": null,
      "national_industry": null
    }
  ],
  "sic": [
    {
      "sic_code": "7372",
      "major_group": "business services",
      "industry_group": "computer programming, data processing, and other computer related services",
      "industry_sector": "prepackaged software"
    }
  ],
  "location": {
    "name": "south san francisco, california, united states",
    "locality": "south san francisco",
    "region": "california",
    "metro": "san francisco, california",
    "country": "united states",
    "continent": "north america",
    "street_address": "354 oyster point boulevard",
    "address_line_2": null,
    "postal_code": "94080",
    "geo": "37.65,-122.40"
  },
  "linkedin_id": "2135371",
  "linkedin_url": "linkedin.com/company/stripe",
  "linkedin_slug": "stripe",
  "facebook_url": "facebook.com/stripehq",
  "twitter_url": "twitter.com/stripe",
  "profiles": [
    "linkedin.com/company/stripe",
    "linkedin.com/company/2135371",
    "facebook.com/stripehq",
    "twitter.com/stripe",
    "crunchbase.com/organization/stripe"
  ],
  "website": "stripe.com",
  "ticker": null,
  "gics_sector": null,
  "mic_exchange": null,
  "type": "private",
  "summary": "stripe is a financial infrastructure platform for businesses. millions of companies—from the world’s largest enterprises to the most ambitious startups—use stripe to accept payments, grow their revenue, and accelerate new business opportunities. headquartered in san francisco and dublin, the company aims to increase the gdp of the internet.",
  "tags": [
    "mobile",
    "technology",
    "retail",
    "mobile payments",
    "data analysis",
    "ecommerce",
    "online payments",
    "financial technology",
    "software",
    "marketing automation"
  ],
  "headline": "Help increase the GDP of the internet.",
  "alternative_names": [
    "stripe (via iqtalent partners)",
    "smb account executive",
    "stripe vintage modern",
    "shashi medicorium",
    "stripe inc",
    "stripe, inc.",
    "st",
    "special protection services limited",
    "stripe via security industry specialists inc.",
    "stripe open source retreat"
  ],
  "alternative_domains": [],
  "affiliated_profiles": [],
  "total_funding_raised": 8746087947,
  "latest_funding_stage": "grant",
  "last_funding_date": "2023-04-25",
  "number_funding_rounds": 20,
  "funding_stages": [
    "series_d",
    "seed",
    "series_a",
    "pre_seed",
    "series_unknown",
    "series_c",
    "secondary_market",
    "grant",
    "series_h",
    "series_g",
    "series_b",
    "series_e",
    "series_i"
  ],
  "employee_count_by_country": {
    "united states": 5086,
    "ireland": 512,
    "india": 349,
    "united kingdom": 301,
    "canada": 249,
    "singapore": 244,
    "australia": 104,
    "germany": 99,
    "spain": 81,
    "france": 76,
    "mexico": 59,
    "japan": 55,
    "netherlands": 46,
    "brazil": 27,
    "other_uncategorized": 22,
    "philippines": 21,
    "sweden": 18,
    "united arab emirates": 10,
    "belgium": 8,
    "romania": 8,
    "colombia": 7,
    "switzerland": 6,
    "pakistan": 5,
    "italy": 4,
    "indonesia": 4,
    "malaysia": 4,
    "costa rica": 4,
    "poland": 4,
    "israel": 4,
    "bangladesh": 4,
    "thailand": 4,
    "china": 3,
    "new zealand": 3,
    "south korea": 3,
    "nigeria": 3,
    "turkey": 2,
    "ukraine": 2,
    "austria": 2,
    "taiwan": 2,
    "vietnam": 1,
    "jordan": 1,
    "estonia": 1,
    "argentina": 1,
    "ghana": 1,
    "greece": 1,
    "morocco": 1,
    "sri lanka": 1,
    "egypt": 1,
    "sudan": 1,
    "azerbaijan": 1,
    "portugal": 1,
    "kenya": 1
  },
  "likelihood": 4,
  "dataset_version": "25.2"
}
 */