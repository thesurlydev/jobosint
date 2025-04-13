# LinkedIn Notes


## GraphQL Requests

Examples of queryId:

```
ca80b3b293240baf5a00226d8d6d78a1
c07b0d44515bceba51a9b73c01b0cecb
```

Examples of graphql requests:

https://www.linkedin.com/voyager/api/graphql
?variables=(cardSectionTypes:List(**JOB_DESCRIPTION_CARD**),jobPostingUrn:urn%3Ali%3Afsd_jobPosting%3A**4188561576**,includeSecondaryActionsV2:true)
&queryId=voyagerJobsDashJobPostingDetailSections.**c07b0d44515bceba51a9b73c01b0cecb**

https://www.linkedin.com/voyager/api/graphql
?variables=(cardSectionTypes:List(**SALARY_CARD**),jobPostingUrn:urn%3Ali%3Afsd_jobPosting%3A**4201303418**,includeSecondaryActionsV2:true)
&queryId=voyagerJobsDashJobPostingDetailSections.**c07b0d44515bceba51a9b73c01b0cecb**

https://www.linkedin.com/voyager/api/graphql
?variables=(cardSectionTypes:List(TOP_CARD,HOW_YOU_FIT_CARD),jobPostingUrn:urn%3Ali%3Afsd_jobPosting%3A4201303418,includeSecondaryActionsV2:true,includeHowYouFitCard:true,jobDetailsContext:(isJobSearch:true),includeFitLevelCard:true)
&queryId=voyagerJobsDashJobPostingDetailSections.c07b0d44515bceba51a9b73c01b0cecb

